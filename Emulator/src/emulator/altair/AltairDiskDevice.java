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

	private int cur_disk = 8; /* Currently selected drive */

	boolean dirty = false; /* 1 when buffer has unwritten data in it */
	AltairDiskUnit dptr = null;

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

	long dsk10(int io, long value) throws IOException {

		if (io == 0) { /* IN: return flags */
			dptr = (AltairDiskUnit) this.units.get(cur_disk);
			return ((~dptr.cur_flags) & 0xFF); /*
														 * Return the
														 * COMPLEMENT!
														 */
		}

		/* OUT: Controller set/reset/enable/disable */

		if (dirty)
			writebuf();

		/* printf("\n[%o] OUT 10: %x", PCX, data); */
		cur_disk = (int) (value & 0x0F);
		dptr = (AltairDiskUnit) this.units.get(cur_disk);
		if ((value & 0x80) != 0) {
			dptr.cur_flags = 0;

			/* Disable drive */
			// int off = 0;
			// if (cur_disk == 0377) off = 1;
			dptr.cur_sect = 0377;
			dptr.cur_byte = 0377;
			return (0);
		}
		dptr.cur_flags = 0x1A; /* Enable: head move true */
		dptr.cur_sect = 0377; /* reset internal counters */
		dptr.cur_byte = 0377;
		if (dptr.cur_track == 0)
			dptr.cur_flags |= 0x40; /* track 0 if there */
		return (0);
	}

	/* Disk Drive Status/Functions */

	long dsk11(int io, long value) throws IOException {
		long stat;
		dptr = (AltairDiskUnit) this.units.get(cur_disk);
		if (io == 0) { /* Read sector position */
			/* printf("\n[%o] IN 11", PCX); */
			if (dirty)
				writebuf();
			if ((dptr.cur_flags & 0x04) != 0) { /* head loaded? */
				dptr.cur_sect++;
				if (dptr.cur_sect > 31)
					dptr.cur_sect = 0;
				dptr.cur_byte = 0377;
				stat = dptr.cur_sect << 1;
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
		if ((value & 0x01) != 0) { /* Step head in */
			dptr.cur_track++;
			if (dptr.cur_track > 76)
				dptr.cur_track = 76;
			if (dirty)
				writebuf();
			dptr.cur_sect = 0377;
			dptr.cur_byte = 0377;
		}

		if ((value & 0x02) != 0) { /* Step head out */
			dptr.cur_track--;
			if (dptr.cur_track < 0) {
				dptr.cur_track = 0;
				dptr.cur_flags |= 0x40; /* track 0 if there */
			}
			if (dirty)
				writebuf();
			dptr.cur_sect = 0377;
			dptr.cur_byte = 0377;
		}

		if (dirty)
			writebuf();

		if ((value & 0x04) != 0) { /* Head load */
			dptr.cur_flags |= 0x04; /* turn on head loaded bit */
			dptr.cur_flags |= 0x80; /* turn on 'read data available */
		}

		if ((value & 0x08) != 0) { /* Head Unload */
			dptr.cur_flags &= 0xFB; /* off on 'head loaded' */
			dptr.cur_flags &= 0x7F; /* off on 'read data avail */
			dptr.cur_sect = 0377;
			dptr.cur_byte = 0377;
		}

		/* Interrupts & head current are ignored */

		if ((value & 0x80) != 0) { /* write sequence start */
			dptr.cur_byte = 0;
			dptr.cur_flags |= 0x01; /* enter new write data on */
		}
		return 0;
	}

	/* Disk Data In/Out */

	int dsk12(int io, long value) throws IOException {
		int i;
		long pos;

		dptr = (AltairDiskUnit) this.units.get(cur_disk);
		if (io == 0) {
			if ((i = dptr.cur_byte) < 138) { /* just get from buffer */
				dptr.cur_byte++;
				return (dptr.filebuf.get(i) & 0xFF);
			}
			/* physically read the sector */
			/*
			 * printf("\n[%o] IN 12 (READ) T%d S%d", PCX, cur_track[cur_disk],
			 * cur_sect[cur_disk]);
			 */
			pos = DSK_TRACSIZE * dptr.cur_track;
			pos += DSK_SECTSIZE * dptr.cur_sect;
			FIO.sim_fseek(dptr.fileref, pos, 0);
			FIO.sim_fread(dptr.filebuf, dptr.fileref);
			dptr.cur_byte = 1;
			return (dptr.filebuf.get(0) & 0xFF);
		} else {
			if (dptr.cur_byte > 136) {
				i = dptr.cur_byte;
				dptr.filebuf.put(i, (byte) (value & 0xFF));
				writebuf();
				return (0);
			}
			i = dptr.cur_byte;
			dirty = true;

			dptr.filebuf.put(i, (byte) (value & 0xFF));
			dptr.cur_byte++;
			return (0);
		}
	}

	void writebuf() throws IOException {
		long pos;
		int i;
		dptr = (AltairDiskUnit) this.units.get(cur_disk);
		i = dptr.cur_byte; /* null-fill rest of sector if any */
		while (i < 138) {
			dptr.filebuf.put(i, (byte) 0x00);
			i++;
		}
		/*
		 * printf("\n[%o] OUT 12 (WRITE) T%d S%d", PCX, cur_track[cur_disk],
		 * cur_sect[cur_disk]); i = getch();
		 */
		pos = DSK_TRACSIZE * dptr.cur_track; /* calc file pos */
		pos += DSK_SECTSIZE * dptr.cur_sect;
		FIO.sim_fseek(dptr.fileref, pos, 0);
		FIO.sim_fwrite(dptr.filebuf, dptr.fileref);
		dptr.cur_flags &= 0xFE; /* ENWD off */
		dptr.cur_byte = 0377;
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
		public int cur_track = 0;
		public int cur_sect = 0;
		public int cur_byte = 0;
		public int cur_flags = 0;

		@Override
		public int action(Unit up) {
			// TODO Auto-generated method stub
			return 0;
		}

	}

}
