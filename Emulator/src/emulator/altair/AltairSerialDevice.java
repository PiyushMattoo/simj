package emulator.altair;

import emulator.core.Defs;
import emulator.core.Device;
import emulator.core.Register;
import emulator.core.Unit;

public class AltairSerialDevice extends Device {
	public static final int UNIT_V_ANSI =(Unit.UNIT_V_UF + 0);                     /* ANSI mode */
	public static final int UNIT_ANSI =  (1 << UNIT_V_ANSI);
	
	Register DATA;
	Register STAT;
	Register POS;
	Unit ptp_unit;
	Unit ptr_unit;
	
	
	

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
	    ptp_unit.buf = 0;
	    ptp_unit.u3 = 0x02;
	    sim_cancel (&ptp_unit);                             /* deactivate unit */
	    return (int) Defs.SCPE_OK;
	}

	/*  I/O instruction handlers, called from the CPU module when an
    IN or OUT instruction is issued.

    Each function is passed an 'io' flag, where 0 means a read from
    the port, and 1 means a write to the port.  On input, the actual
    input is passed as the return value, on output, 'data' is written
    to the device.
*/

long sio0s(int io, long value)
{
    if (io == 0) {
        return (STAT.value);
    } else {
        if (value == 0x03) {                             /* reset port! */
            STAT.value = 0x02;
            DATA.value = 0;
            POS.value = 0;
        }
        return (0);
    }
}

long sio0d(int io, long value)
{
    if (io == 0) {
        STAT.value = STAT.value & 0xFE;
        return (DATA.value);
    } else {
        sim_putchar(value);
    }
    return 0;
}

/* Port 2 controls the PTR/PTP devices */

long sio1s(int io, long value)
{
    if (io == 0) {
        if ((ptr_unit.flags & Unit.UNIT_ATT) == 0)           /* attached? */
            return 0x02;
        if (ptr_unit.u3 != 0)                           /* No more data? */
            return 0x02;
        return (0x03);                                  /* ready to read/write */
    } else {
        if (value == 0x03) {
            ptr_unit.u3 = 0;
            ptr_unit.buf = 0;
            ptr_unit.pos = 0;
            ptp_unit.u3 = 0;
            ptp_unit.buf = 0;
            ptp_unit.pos = 0;
        }
        return (0);
    }
}

long sio1d(int io, long value)
{
    int temp;
    Unit uptr;

    if (io == 0) {
        if ((ptr_unit.flags & Unit.UNIT_ATT) == 0)           /* attached? */
            return 0;
        if (ptr_unit.u3 != 0)
            return 0;
        uptr = ptr_unit;
        if ((temp = getc(ptr_unit.fileref)) == EOF) {    /* end of file? */
            ptr_unit.u3 = 0x01;
            return 0;
        }
        ptr_unit.pos++;
        return (temp & 0xFF);
    } else {

        putc(value, ptp_unit.fileref);
        ptp_unit.pos++;
    }
    return 0;
}

public AltairSerialDevice() {
	
	// Two units, PTP, PTR
	ptp_unit = new PTPUnit();
	ptr_unit = new PTRUnit();
	
	units.add(ptp_unit);
	units.add(ptr_unit);
	
	
	
	// Registers for the SIO device
	DATA = new Register();
	STAT = new Register();
	POS = new Register();
	POS.width=8;
	DATA.width =8;
	STAT.width=8;
	DATA.name = "DATA";
	STAT.name = "STAT";
	POS.name="POS";
	this.registers.add(DATA);
	this.registers.add(STAT);
	this.registers.add(POS);
	
}

private static class PTRUnit extends Unit {

	@Override
	public int action(Unit up) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}

private static class PTPUnit extends Unit {

	@Override
	public int action(Unit up) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
}
