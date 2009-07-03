package emulator.altair;

import java.io.IOException;

import emulator.core.Defs;
import emulator.core.Device;
import emulator.core.FIO;
import emulator.core.Unit;

public class AltairDiskDevice extends Device {

	private static final long UNIT_V_ENABLE = (Unit.UNIT_V_UF + 0); /*
																	 * Write
																	 * Enable
																	 */
	private static final long UNIT_ENABLE = (1 << UNIT_V_ENABLE);

	private static final long DSK_SECTSIZE = 137;
	private static final long DSK_SECT = 32;
	private static final long DSK_TRACSIZE = 4384;
	private static final long DSK_SURF = 1;
	private static final long DSK_CYL = 77;
	private static final long DSK_SIZE = (DSK_SECT * DSK_SURF * DSK_CYL * DSK_SECTSIZE);

	/* Global data on status */

	private static int cur_disk = 8; /* Currently selected drive */
	private static int cur_track[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0377 };
	private static int cur_sect[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0377 };
	private static int cur_byte[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0377 };
	private static int cur_flags[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0 };

	boolean dirty = false; /* 1 when buffer has unwritten data in it */
	Unit dptr; /* fileref to write dirty buffer to */

	static long dsk_rwait = 100; /* rotate latency */

	@Override
	public int attach(Unit up, String cp) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int boot(int u, Device dp) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int deposit(long v, long a, Unit up, int sw) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int detach(Unit up) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int examine(long v, long a, Unit up, int sw) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int msize(Unit up, int v, String cp, Device dp) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int reset(Device dp) {
		cur_disk = 0;
		return (int) Defs.SCPE_OK;
	}

	/*
	 * I/O instruction handlers, called from the CPU module when an IN or OUT
	 * instruction is issued.
	 * 
	 * Each function is passed an 'io' flag, where 0 means a read from the port,
	 * and 1 means a write to the port. On input, the actual input is passed as
	 * the return value, on output, 'data' is written to the device.
	 */

	/* Disk Controller Status/Select */

	/*
	 * IMPORTANT: The status flags read by port 8 IN instruction are INVERTED,
	 * that is, 0 is true and 1 is false. To handle this, the simulator keeps
	 * it's own status flags as 0=false, 1=true; and returns the COMPLEMENT of
	 * the status flags when read. This makes setting/testing of the flag bits
	 * more logical, yet meets the simulation requirement that they are reversed
	 * in hardware.
	 */

	long dsk10(int io, int data) throws IOException {

		if (io == 0) { /* IN: return flags */
			return ((~cur_flags[cur_disk]) & 0xFF); /* Return the COMPLEMENT! */
		}

		/* OUT: Controller set/reset/enable/disable */

		if (dirty)
			writebuf();

		/* printf("\n[%o] OUT 10: %x", PCX, data); */
		cur_disk = data & 0x0F;
		if ((data & 0x80) != 0) {
			cur_flags[cur_disk] = 0;

			/* Disable drive */
			// int off = 0;
			// if (cur_disk == 0377) off = 1;
			cur_sect[cur_disk] = 0377;
			cur_byte[cur_disk] = 0377;
			return (0);
		}
		cur_flags[cur_disk] = 0x1A; /* Enable: head move true */
		cur_sect[cur_disk] = 0377; /* reset internal counters */
		cur_byte[cur_disk] = 0377;
		if (cur_track[cur_disk] == 0)
			cur_flags[cur_disk] |= 0x40; /* track 0 if there */
		return (0);
	}

	/* Disk Drive Status/Functions */

	long dsk11(int io, int data) throws IOException {
		long stat;

		if (io == 0) { /* Read sector position */
			/* printf("\n[%o] IN 11", PCX); */
			if (dirty)
				writebuf();
			if ((cur_flags[cur_disk] & 0x04) != 0) { /* head loaded? */
				cur_sect[cur_disk]++;
				if (cur_sect[cur_disk] > 31)
					cur_sect[cur_disk] = 0;
				cur_byte[cur_disk] = 0377;
				stat = cur_sect[cur_disk] << 1;
				stat &= 0x3E; /* return 'sector true' bit = 0 (true) */
				stat |= 0xC0; /* set on 'unused' bits */
				return (stat);
			} else {
				return (0); /* head not loaded - return 0 */
			}
		}

		/* Drive functions */

		if (cur_disk > 7)
			return (0); /* no drive selected - can do nothin */

		/* printf("\n[%o] OUT 11: %x", PCX, data); */
		if ((data & 0x01) != 0) { /* Step head in */
			cur_track[cur_disk]++;
			if (cur_track[cur_disk] > 76)
				cur_track[cur_disk] = 76;
			if (dirty)
				writebuf();
			cur_sect[cur_disk] = 0377;
			cur_byte[cur_disk] = 0377;
		}

		if ((data & 0x02) != 0) { /* Step head out */
			cur_track[cur_disk]--;
			if (cur_track[cur_disk] < 0) {
				cur_track[cur_disk] = 0;
				cur_flags[cur_disk] |= 0x40; /* track 0 if there */
			}
			if (dirty)
				writebuf();
			cur_sect[cur_disk] = 0377;
			cur_byte[cur_disk] = 0377;
		}

		if (dirty)
			writebuf();

		if ((data & 0x04) != 0) { /* Head load */
			cur_flags[cur_disk] |= 0x04; /* turn on head loaded bit */
			cur_flags[cur_disk] |= 0x80; /* turn on 'read data available */
		}

		if ((data & 0x08) != 0) { /* Head Unload */
			cur_flags[cur_disk] &= 0xFB; /* off on 'head loaded' */
			cur_flags[cur_disk] &= 0x7F; /* off on 'read data avail */
			cur_sect[cur_disk] = 0377;
			cur_byte[cur_disk] = 0377;
		}

		/* Interrupts & head current are ignored */

		if ((data & 0x80) != 0) { /* write sequence start */
			cur_byte[cur_disk] = 0;
			cur_flags[cur_disk] |= 0x01; /* enter new write data on */
		}
		return 0;
	}

	/* Disk Data In/Out */

	int dsk12(int io, int data) throws IOException {
		int i;
		long pos;
		Unit uptr;

		uptr = this.units.get(cur_disk);
		if (io == 0) {
			if ((i = cur_byte[cur_disk]) < 138) { /* just get from buffer */
				cur_byte[cur_disk]++;
				return (uptr.filebuf.get(i) & 0xFF);
			}
			/* physically read the sector */
			/*
			 * printf("\n[%o] IN 12 (READ) T%d S%d", PCX, cur_track[cur_disk],
			 * cur_sect[cur_disk]);
			 */
			pos = DSK_TRACSIZE * cur_track[cur_disk];
			pos += DSK_SECTSIZE * cur_sect[cur_disk];
			FIO.sim_fseek(uptr.fileref, pos, 0);
			FIO.sim_fread(uptr.filebuf, uptr.fileref);
			cur_byte[cur_disk] = 1;
			return (uptr.filebuf.get(0) & 0xFF);
		} else {
			if (cur_byte[cur_disk] > 136) {
				i = cur_byte[cur_disk];
				uptr.filebuf.put(i, (byte) (data & 0xFF));
				writebuf();
				return (0);
			}
			i = cur_byte[cur_disk];
			dirty = true;
			dptr = uptr;
			uptr.filebuf.put(i, (byte) (data & 0xFF));
			cur_byte[cur_disk]++;
			return (0);
		}
	}

	void writebuf() throws IOException {
		long pos;
		int i;
		Unit uptr = this.units.get(cur_disk);
		i = cur_byte[cur_disk]; /* null-fill rest of sector if any */
		while (i < 138) {
			uptr.filebuf.put(i, (byte) 0x00);
			i++;
		}
		/*
		 * printf("\n[%o] OUT 12 (WRITE) T%d S%d", PCX, cur_track[cur_disk],
		 * cur_sect[cur_disk]); i = getch();
		 */
		pos = DSK_TRACSIZE * cur_track[cur_disk]; /* calc file pos */
		pos += DSK_SECTSIZE * cur_sect[cur_disk];
		FIO.sim_fseek(dptr.fileref, pos, 0);
		FIO.sim_fwrite(uptr.filebuf, uptr.fileref);
		cur_flags[cur_disk] &= 0xFE; /* ENWD off */
		cur_byte[cur_disk] = 0377;
		dirty = false;
		return;
	}

	public AltairDiskDevice() {

		// 8 units, 0-7
		AltairDiskUnit u0 = new AltairDiskUnit();
		AltairDiskUnit u1 = new AltairDiskUnit();
		AltairDiskUnit u2 = new AltairDiskUnit();
		AltairDiskUnit u3 = new AltairDiskUnit();
		AltairDiskUnit u4 = new AltairDiskUnit();
		AltairDiskUnit u5 = new AltairDiskUnit();
		AltairDiskUnit u6 = new AltairDiskUnit();
		AltairDiskUnit u7 = new AltairDiskUnit();

		this.units.add(u0);
		this.units.add(u1);
		this.units.add(u2);
		this.units.add(u3);
		this.units.add(u4);
		this.units.add(u5);
		this.units.add(u6);
		this.units.add(u7);

	}

	public static class AltairDiskUnit extends Unit {

		@Override
		public int action(Unit up) {
			// TODO Auto-generated method stub
			return 0;
		}

	}

}
