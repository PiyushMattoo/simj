package emulator.core;

import java.util.ArrayList;
import java.util.List;

public abstract class Device {
	/* Device data structure */

	String name; /* name */
	public List<Unit> units = new ArrayList<Unit>(); /* units */
	public List<Register> registers = new ArrayList<Register>(); /* registers */
	List<Modifier> modifiers = new ArrayList<Modifier>(); /* modifiers */
	long numunits; /* #units */
	long aradix; /* address radix */
	long awidth; /* address width */
	long aincr; /* addr increment */
	long dradix; /* data radix */
	long dwidth; /* data width */
	// void *ctxt; /* context */
	protected long flags; /* flags */
	long dctrl; /* debug control */
	List<DebugEntry> debflags = new ArrayList<DebugEntry>(); /* debug flags */

	String lname; /* logical name */

	public abstract int examine(long v, long a, Unit up, int sw);

	public abstract int deposit(long v, long a, Unit up, int sw);

	public abstract int reset(Device dp);

	public abstract int boot(int u, Device dp);

	public abstract int attach(Unit up, String cp);

	public abstract int detach(Unit up);

	public abstract int msize(Unit up, int v, String cp, Device dp);

	/* Device flags */

	public static long DEV_V_DIS = 0; /* dev disabled */
	public static long DEV_V_DISABLE = 1; /* dev disable-able */
	public static long DEV_V_DYNM = 2; /* mem size dynamic */
	public static long DEV_V_NET = 3; /* network attach */
	public static long DEV_V_DEBUG = 4; /* debug capability */
	public static long DEV_V_RAW = 5; /* raw supported */
	public static long DEV_V_RAWONLY = 6; /* only raw supported */
	public static long DEV_V_UF_31 = 12; /* user flags, V3.1 */
	public static long DEV_V_UF = 16; /* user flags */
	public static long DEV_V_RSV = 31; /* reserved */

	public static long DEV_DIS = (1 << DEV_V_DIS);
	public static long DEV_DISABLE = (1 << DEV_V_DISABLE);
	public static long DEV_DYNM = (1 << DEV_V_DYNM);
	public static long DEV_NET = (1 << DEV_V_NET);
	public static long DEV_DEBUG = (1 << DEV_V_DEBUG);
	public static long DEV_RAW = (1 << DEV_V_RAW);
	public static long DEV_RAWONLY = (1 << DEV_V_RAWONLY);

	public static long DEV_UFMASK_31 = (((1 << DEV_V_RSV) - 1) & ~((1 << DEV_V_UF_31) - 1));
	public static long DEV_UFMASK = (((1 << DEV_V_RSV) - 1) & ~((1 << DEV_V_UF) - 1));
	public static long DEV_RFLAGS = (DEV_UFMASK | DEV_DIS); /* restored flags */

}
