package emulator.core;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

public abstract class Unit {

	/*
	 * Unit data structure
	 * 
	 * Parts of the unit structure are device specific, that is, they are not
	 * referenced by the simulator control package and can be freely used by
	 * device simulators. Fields starting with 'buf', and flags starting with
	 * 'UF', are device specific. The definitions given here are for a typical
	 * sequential device.
	 */

	public Unit next; /* next active */
	// t_stat (*action)(struct sim_unit *up); /* action routine */
	public String filename; /* open file name */
	public RandomAccessFile fileref; /* file reference */
	public ByteBuffer filebuf; /* memory buffer */
	public long hwmark; /* high water mark */
	public long time; /* time out */
	public long flags; /* flags */
	public long capac; /* capacity */
	public long pos; /* file position */
	public long buf; /* buffer */
	public long wait; /* wait */
	public long u3; /* device specific */
	public long u4; /* device specific */
	public long u5; /* device specific */
	public long u6; /* device specific */

	public abstract int action(Unit up);

	/* Unit flags */

	public static long UNIT_V_UF_31 = 12; /* dev spec, V3.1 */
	public static int UNIT_V_UF = 16; /* device specific */
	public static long UNIT_V_RSV = 31; /* reserved!! */

	public static long UNIT_ATTABLE = 000001; /* attachable */
	public static long UNIT_RO = 000002; /* read only */
	public static long UNIT_FIX = 000004; /* fixed capacity */
	public static long UNIT_SEQ = 000010; /* sequential */
	public static long UNIT_ATT = 000020; /* attached */
	public static long UNIT_BINK = 000040; /* K = power of 2 */
	public static long UNIT_BUFABLE = 000100; /* bufferable */
	public static long UNIT_MUSTBUF = 000200; /* must buffer */
	public static long UNIT_BUF = 000400; /* buffered */
	public static long UNIT_ROABLE = 001000; /* read only ok */
	public static long UNIT_DISABLE = 002000; /* disable-able */
	public static long UNIT_DIS = 004000; /* disabled */
	public static long UNIT_RAW = 010000; /* raw mode */
	public static long UNIT_TEXT = 020000; /* text mode */
	public static long UNIT_IDLE = 040000; /* idle eligible */

	public static long UNIT_UFMASK_31 = (((1 << UNIT_V_RSV) - 1) & ~((1 << UNIT_V_UF_31) - 1));
	public static long UNIT_UFMASK = (((1 << UNIT_V_RSV) - 1) & ~((1 << UNIT_V_UF) - 1));
	public static long UNIT_RFLAGS = (UNIT_UFMASK | UNIT_DIS); /*
																 * restored
																 * flags
																 */

}
