package emulator.core;

import java.io.File;

public class Globals {

	/* Global data */

	Device sim_dflt_dev = null;
	public static Unit sim_clock_queue = null;

	public static long sim_switches = 0;
	File sim_ofile = null;
	//SCHTAB *sim_schptr = false;
	Device sim_dfdev = null;
	Unit sim_dfunit = null;
	long sim_opt_out = 0;
	long sim_is_running = 0;
	public static long sim_brk_summ = 0;
	public static long sim_brk_types = 0;
	public static long sim_brk_dflt = 0;
	//char *sim_brk_act = null;
	//BRKTAB *sim_brk_tab = null;
	long sim_brk_ent = 0;
	long sim_brk_lnt = 0;
	long sim_brk_ins = 0;
	//boolean sim_brk_pend[SIM_BKPT_N_SPC] = { false };
	//long sim_brk_ploc[SIM_BKPT_N_SPC] = { 0 };
	long sim_quiet = 0;
	long sim_step = 0;


	volatile long stop_cpu = 0;
	//t_value *sim_eval = null;
	long sim_deb_close = 0;                                /* 1 = close debug */
	//FILE *sim_log = null;                                   /* log file */
	//FILE *sim_deb = null;                                   /* debug file */
	//static SCHTAB sim_stab;

	//static Unit sim_step_unit = { UDATA (&step_svc, 0, 0)  };

}
