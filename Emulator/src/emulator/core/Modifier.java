package emulator.core;

public abstract class Modifier {

	long mask; /* mask */
	long match; /* match */
	String pstring; /* print string */
	String mstring; /* match string */

	// void *desc; /* value descriptor */
	/* REG * if MTAB_VAL */

	public abstract long validate(Unit uptr, long val, String cptr);

	public abstract long display(Unit up, long v);

	public final static long MTAB_XTD = (1 << Unit.UNIT_V_RSV); /*
																 * ext entry
																 * flag
																 */
	public final static long MTAB_VDV = 001; /* valid for dev */
	public final static long MTAB_VUN = 002; /* valid for unit */
	public final static long MTAB_VAL = 004; /* takes a value */
	public final static long MTAB_NMO = 010; /* only if named */
	public final static long MTAB_NC = 020; /* no UC conversion */
	public final static long MTAB_SHP = 040; /* show takes parameter */
}
