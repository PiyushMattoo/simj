package emulator.core;

public class Reg {
	/* Register data structure */

	String name; /* name */
	// void *loc; /* location */
	long radix; /* radix */
	long width; /* width */
	long offset; /* starting bit */
	long depth; /* save depth */
	long flags; /* flags */
	long qptr; /* circ q ptr */

	public static long REG_FMT = 00003; /* see PV_x */
	public static long REG_RO = 00004; /* read only */
	public static long REG_HIDDEN = 00010; /* hidden */
	public static long REG_NZ = 00020; /* must be non-zero */
	public static long REG_UNIT = 00040; /* in unit struct */
	public static long REG_CIRC = 00100; /* circular array */
	public static long REG_VMIO = 00200; /* use VM data print/parse */
	public static long REG_VMAD = 00400; /* use VM addr print/parse */
	public static long REG_FIT = 01000; /* fit access to size */
	public static long REG_HRO = (REG_RO | REG_HIDDEN); /* hidden, read only */

}
