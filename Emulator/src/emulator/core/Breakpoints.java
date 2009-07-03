package emulator.core;

public class Breakpoints {

	/* Test for breakpoint */

	public static long sim_brk_test (long loc, long btyp)
	{
//		
//	BRKTAB *bp;
//	long spc = (btyp >> SIM_BKPT_V_SPC) & (SIM_BKPT_N_SPC - 1);
//
//	if ((bp = sim_brk_fnd (loc)) && (btyp & bp->typ)) {     /* in table, type match? */
//	    if ((sim_brk_pend[spc] && (loc == sim_brk_ploc[spc])) || /* previous location? */
//	        (--bp->cnt > 0))                                /* count > 0? */
//	        return 0;
//	    bp->cnt = 0;                                        /* reset count */
//	    sim_brk_ploc[spc] = loc;                            /* save location */
//	    sim_brk_pend[spc] = TRUE;                           /* don't do twice */
//	    sim_brk_act = bp->act;                              /* set up actions */
//	    return (btyp & bp->typ);
//	    }
//	sim_brk_pend[spc] = FALSE;
	return 0;
	}
}
