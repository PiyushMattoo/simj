package emulator.core;

/**
 The interface between the simulator control package (SCP) and the
 simulator consists of the following routines and data structures

 sim_name                simulator name string <BR>
 sim_devices[]           array of pointers to simulated devices <BR>
 sim_PC                  pointer to saved PC register descriptor <BR>
 sim_interval            simulator interval to next event <BR>
 sim_stop_messages[]     array of pointers to stop messages <BR>
 sim_instr()             instruction execution routine <BR>
 sim_load()              binary loader routine <BR>
 sim_emax                maximum number of words in an instruction <BR>

 In addition, the simulator must supply routines to print and parse
 architecture specific formats

 print_sym               print symbolic output <BR>
 parse_sym               parse symbolic input
 */
public class Defs {

	/* System independent definitions */

	public static long FLIP_SIZE = (1 << 16); /* flip buf size */

	public static long PATH_MAX = 512;

	public static long CBUFSIZE = (128 + PATH_MAX); /* string buf size */

	/* Breakpoint spaces definitions */

	public static long SIM_BKPT_N_SPC = 64; /* max number spaces */
	public static long SIM_BKPT_V_SPC = 26; /* location in arg */

	/* Extended switch definitions (bits >= 26) */

	public static long SIM_SW_HIDE = (1 << 26); /* enable hiding */
	public static long SIM_SW_REST = (1 << 27); /* attach/restore */
	public static long SIM_SW_REG = (1 << 28); /* register value */
	public static long SIM_SW_STOP = (1 << 29); /* stop message */

	/*
	 * Simulator status codes
	 * 
	 * 0 ok 1 - (SCPE_BASE - 1) simulator specific SCPE_BASE - n general
	 */

	public static long SCPE_OK = 0; /* normal return */
	public static long SCPE_BASE = 64; /* base for messages */
	public static long SCPE_NXM = (SCPE_BASE + 0); /* nxm */
	public static long SCPE_UNATT = (SCPE_BASE + 1); /* no file */
	public static long SCPE_IOERR = (SCPE_BASE + 2); /* I/O error */
	public static long SCPE_CSUM = (SCPE_BASE + 3); /* loader cksum */
	public static long SCPE_FMT = (SCPE_BASE + 4); /* loader format */
	public static long SCPE_NOATT = (SCPE_BASE + 5); /* not attachable */
	public static long SCPE_OPENERR = (SCPE_BASE + 6); /* open error */
	public static long SCPE_MEM = (SCPE_BASE + 7); /* alloc error */
	public static long SCPE_ARG = (SCPE_BASE + 8); /* argument error */
	public static long SCPE_STEP = (SCPE_BASE + 9); /* step expired */
	public static long SCPE_UNK = (SCPE_BASE + 10); /* unknown command */
	public static long SCPE_RO = (SCPE_BASE + 11); /* read only */
	public static long SCPE_INCOMP = (SCPE_BASE + 12); /* incomplete */
	public static long SCPE_STOP = (SCPE_BASE + 13); /* sim stopped */
	public static long SCPE_EXIT = (SCPE_BASE + 14); /* sim exit */
	public static long SCPE_TTIERR = (SCPE_BASE + 15); /* console tti err */
	public static long SCPE_TTOERR = (SCPE_BASE + 16); /* console tto err */
	public static long SCPE_EOF = (SCPE_BASE + 17); /* end of file */
	public static long SCPE_REL = (SCPE_BASE + 18); /* relocation error */
	public static long SCPE_NOPARAM = (SCPE_BASE + 19); /* no parameters */
	public static long SCPE_ALATT = (SCPE_BASE + 20); /* already attached */
	public static long SCPE_TIMER = (SCPE_BASE + 21); /* hwre timer err */
	public static long SCPE_SIGERR = (SCPE_BASE + 22); /* signal err */
	public static long SCPE_TTYERR = (SCPE_BASE + 23); /* tty setup err */
	public static long SCPE_SUB = (SCPE_BASE + 24); /* subscript err */
	public static long SCPE_NOFNC = (SCPE_BASE + 25); /* func not imp */
	public static long SCPE_UDIS = (SCPE_BASE + 26); /* unit disabled */
	public static long SCPE_NORO = (SCPE_BASE + 27); /* rd only not ok */
	public static long SCPE_INVSW = (SCPE_BASE + 28); /* invalid switch */
	public static long SCPE_MISVAL = (SCPE_BASE + 29); /* missing value */
	public static long SCPE_2FARG = (SCPE_BASE + 30); /* too few arguments */
	public static long SCPE_2MARG = (SCPE_BASE + 31); /* too many arguments */
	public static long SCPE_NXDEV = (SCPE_BASE + 32); /* nx device */
	public static long SCPE_NXUN = (SCPE_BASE + 33); /* nx unit */
	public static long SCPE_NXREG = (SCPE_BASE + 34); /* nx register */
	public static long SCPE_NXPAR = (SCPE_BASE + 35); /* nx parameter */
	public static long SCPE_NEST = (SCPE_BASE + 36); /* nested DO */
	public static long SCPE_IERR = (SCPE_BASE + 37); /* internal error */
	public static long SCPE_MTRLNT = (SCPE_BASE + 38); /* tape rec lnt error */
	public static long SCPE_LOST = (SCPE_BASE + 39); /* Telnet conn lost */
	public static long SCPE_TTMO = (SCPE_BASE + 40); /* Telnet conn timeout */
	public static long SCPE_STALL = (SCPE_BASE + 41); /* Telnet conn stall */
	public static long SCPE_AFAIL = (SCPE_BASE + 42); /* assert failed */
	public static long SCPE_KFLAG = 0010000; /* tti data flag */
	public static long SCPE_BREAK = 0020000; /* tti break flag */

	/* Print value format codes */

	public static long PV_RZRO = 0; /* right, zero fill */
	public static long PV_RSPC = 1; /* right, space fill */
	public static long PV_LEFT = 2; /* left justify */

	/* Default timing parameters */

	public static long KBD_POLL_WAIT = 5000; /* keyboard poll */
	public static long KBD_MAX_WAIT = 500000;
	public static long SERIAL_IN_WAIT = 100; /* serial in time */
	public static long SERIAL_OUT_WAIT = 100; /* serial output */
	public static long NOQUEUE_WAIT = 10000; /* min check time */

	public static long KBD_LIM_WAIT(long x) {
		return (((x) > KBD_MAX_WAIT) ? KBD_MAX_WAIT : (x));
	}

	public static long KBD_WAIT(long w, long s) {
		return ((w != 0) ? w : KBD_LIM_WAIT(s));
	}

	/* Convert switch letter to bit mask */

	public static long SWMASK(long x) {
		return (1 << (((int) (x)) - ((int) 'A')));
	}

	/* String match */

	public static long MATCH_CMD(String ptr, String cmd) {
		return ptr.compareTo(cmd);
	}

	/* The following macros define structure contents */

	// public static long UDATA(act,fl,cap)
	// NULL,act,NULL,NULL,NULL,0,0,(fl),(cap),0,0

	// public static long ORDATA(nm,loc,wd) #nm, &(loc), 8, (wd), 0, 1
	// public static long DRDATA(nm,loc,wd) #nm, &(loc), 10, (wd), 0, 1
	// public static long HRDATA(nm,loc,wd) #nm, &(loc), 16, (wd), 0, 1
	// public static long FLDATA(nm,loc,pos) #nm, &(loc), 2, 1, (pos), 1
	// public static long GRDATA(nm,loc,rdx,wd,pos) #nm, &(loc), (rdx), (wd),
	// (pos), 1
	// public static long BRDATA(nm,loc,rdx,wd,dep) #nm, (loc), (rdx), (wd), 0,
	// (dep)
	// public static long URDATA(nm,loc,rdx,wd,off,dep,fl) \
	// #nm, &(loc), (rdx), (wd), (off), (dep), ((fl) | REG_UNIT)

}
