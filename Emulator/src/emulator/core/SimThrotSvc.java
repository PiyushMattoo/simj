package emulator.core;

import static emulator.core.Defs.SCPE_ARG;
import static emulator.core.Defs.SCPE_NOFNC;
import static emulator.core.Defs.SCPE_OK;
import static emulator.core.Defs.SWMASK;
import static emulator.core.Globals.sim_switches;

import java.io.File;
import java.util.logging.Logger;

public class SimThrotSvc extends Unit {
	private static Logger logger = Logger.getLogger(SimThrotSvc.class
			.getCanonicalName());

	private static final int SIM_THROT_WINIT = 1000; /* cycles to skip */
	private static final int SIM_THROT_WST = 10000; /* initial wait */
	private static final int SIM_THROT_WMUL = 4; /* multiplier */
	private static final int SIM_THROT_WMIN = 100; /* min wait */
	private static final int SIM_THROT_MSMIN = 10; /* min for measurement */
	protected static final int SIM_THROT_NONE = 0; /* throttle parameters */
	private static final int SIM_THROT_MCYC = 1;
	private static final int SIM_THROT_KCYC = 2;
	private static final int SIM_THROT_PCT = 3;
	
	long sim_throt_ms_start = 0;
	long sim_throt_ms_stop = 0;
	long sim_throt_wait = 0;
	protected long sim_throt_type = 0;
	long sim_throt_val = 0;
	long sim_throt_state = 0;

	/*
	 * Throttle service
	 * 
	 * Throttle service has three distinct states
	 * 
	 * 0 take initial measurement 1 take final measurement, calculate wait
	 * values 2 periodic waits to slow down the CPU
	 */
	@Override
	public int action(Unit uptr) {
		long delta_ms;
		double a_cps, d_cps;

		switch ((int) sim_throt_state) {

		case 0: /* take initial reading */
			sim_throt_ms_start = Timer.sim_os_msec();
			sim_throt_wait = SIM_THROT_WST;
			sim_throt_state++; /* next state */
			break; /* reschedule */

		case 1: /* take final reading */
			sim_throt_ms_stop = Timer.sim_os_msec();
			delta_ms = sim_throt_ms_stop - sim_throt_ms_start;
			if (delta_ms < SIM_THROT_MSMIN) { /* not enough time? */
				if (sim_throt_wait >= 100000000) { /* too many inst? */
					sim_throt_state = 0; /* fails in 32b! */
					return (int) Defs.SCPE_OK;
				}
				sim_throt_wait = sim_throt_wait * SIM_THROT_WMUL;
				sim_throt_ms_start = sim_throt_ms_stop;
			} else { /* long enough */
				a_cps = ((double) sim_throt_wait) * 1000.0 / (double) delta_ms;
				if (sim_throt_type == SIM_THROT_MCYC) /* calc desired cps */
					d_cps = (double) sim_throt_val * 1000000.0;
				else if (sim_throt_type == SIM_THROT_KCYC)
					d_cps = (double) sim_throt_val * 1000.0;
				else
					d_cps = (a_cps * ((double) sim_throt_val)) / 100.0;
				if (d_cps >= a_cps) {
					sim_throt_state = 0;
					return (int) SCPE_OK;
				}
				sim_throt_wait = (int) /* time between waits */
				((a_cps * d_cps * ((double) Timer.sim_idle_rate_ms)) / (1000.0 * (a_cps - d_cps)));
				if (sim_throt_wait < SIM_THROT_WMIN) { /* not long enough? */
					sim_throt_state = 0;
					return (int) SCPE_OK;
				}
				sim_throt_state++;
				// fprintf (stderr,
				// "Throttle values a_cps = %f, d_cps = %f, wait = %d\n",
				// a_cps, d_cps, sim_throt_wait);
			}
			break;

		case 2: /* throttling */
			Timer.sim_os_ms_sleep(1);
			break;
		}

		Control.sim_activate(uptr, sim_throt_wait); /* reschedule */
		return (int) SCPE_OK;
	}

	void sim_throt_sched() {
		sim_throt_state = 0;
		if (sim_throt_type != 0)
			Control.sim_activate(this, SIM_THROT_WINIT);
		return;
	}

	void sim_throt_cancel() {
		Control.sim_cancel(this);
	}

	int sim_show_throt(File st, Device dnotused, Unit unotused, int flag,
			String cptr) {
		if (Timer.sim_idle_rate_ms == 0)
			logger.info("Throttling not available");
		else {
			switch ((int) sim_throt_type) {

			case SIM_THROT_MCYC:
				logger.info(String.format("Throttle = %d megacycles",
						sim_throt_val));

				break;

			case SIM_THROT_KCYC:
				logger.info(String.format("Throttle = %d kilocycles",
						sim_throt_val));
				break;

			case SIM_THROT_PCT:
				logger.info(String.format("Throttle = %d%%", sim_throt_val));
				break;

			default:
				logger.info("Throttling disabled");
				break;
			}

			if ((sim_switches & SWMASK('D')) != 0) {
				logger.info(String
						.format("Wait rate = %d ms", Timer.sim_idle_rate_ms));
				if (sim_throt_type != 0)
					logger.info(String.format("Throttle interval = %d cycles",
							sim_throt_wait));
			}
		}
		return (int) SCPE_OK;
	}
	
	/* Throttling package */

	public int sim_set_throt(int arg, String cptr) {
		char c;
		long val;

		if (arg == 0) {
			if ((cptr != null) && (cptr.length() != 0))
				return (int) SCPE_ARG;
			sim_throt_type = SIM_THROT_NONE;
			sim_throt_cancel();
		} else if (Timer.sim_idle_rate_ms == 0)
			return (int) SCPE_NOFNC;
		else {
			cptr = cptr.toUpperCase();
			val = Long.parseLong(cptr);
			c = cptr.charAt(cptr.length() - 1);

			if (c == 'M')
				sim_throt_type = SIM_THROT_MCYC;
			else if (c == 'K')
				sim_throt_type = SIM_THROT_KCYC;
			else if ((c == '%') && (val > 0) && (val < 100))
				sim_throt_type = SIM_THROT_PCT;
			else
				return (int) SCPE_ARG;
			if (Timer.sim_idle_enab) {
				logger.finest("Idling disabled");
				Timer.sim_clr_idle(null, 0, null);
			}
			sim_throt_val = (long) val;
		}
		return (int) SCPE_OK;
	}
	
}
