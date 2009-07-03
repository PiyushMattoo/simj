package emulator.core;

import java.io.File;
import java.util.logging.Logger;

import static emulator.core.Globals.*;
import static emulator.core.Unit.*;
import static emulator.core.Defs.*;

/**
 * This library includes the following routines:
 * 
 * sim_timer_init - initialize timing system <BR>
 * sim_rtc_init - initialize <BR>
 * calibration sim_rtc_calb - calibrate clock <BR>
 * sim_timer_init - initialize timing <BR>
 * system sim_idle - virtual machine idle <BR>
 * sim_os_msec - return elapsed time in msec <BR>
 * sim_os_sleep - sleep specified number of seconds <BR>
 * sim_os_ms_sleep - sleep specified number of milliseconds <BR>
 * 
 * @author ccebelenski
 * 
 */
public class Timer {

	private static Logger logger = Logger.getLogger(Timer.class
			.getCanonicalName());

	public static boolean sim_idle_enab = false; /* global flag */

	private static final int SIM_NTIMERS = 8; /* # timers */
	private static final int SIM_TMAX = 500; /* max timer makeup */

	private static final int SIM_IDLE_CAL = 10; /* ms to calibrate */
	private static final int SIM_IDLE_MAX = 10; /* max granularity idle */
	private static final int SIM_IDLE_STMIN = 10; /* min sec for stability */
	private static final int SIM_IDLE_STDFLT = 20; /* dft sec for stability */
	private static final int SIM_IDLE_STMAX = 600; /* max sec for stability */

	final static boolean rtc_avail = true;
	/* OS independent clock calibration package */

	static long[] rtc_ticks = new long[SIM_NTIMERS]; /* ticks */
	static long[] rtc_hz = new long[SIM_NTIMERS]; /* tick rate */
	static long[] rtc_rtime = new long[SIM_NTIMERS]; /* real time */
	static long[] rtc_vtime = new long[SIM_NTIMERS]; /* virtual time */
	static long[] rtc_nxintv = new long[SIM_NTIMERS]; /* next interval */
	static long[] rtc_based = new long[SIM_NTIMERS]; /* base delay */
	static long[] rtc_currd = new long[SIM_NTIMERS]; /* current delay */
	static long[] rtc_initd = new long[SIM_NTIMERS]; /* initial delay */
	static long[] rtc_elapsed = new long[SIM_NTIMERS]; /* sec since init */

	static long sim_idle_rate_ms = 0;
	static long sim_idle_stable = SIM_IDLE_STDFLT;

	private static SimThrotSvc sim_throt_unit = new SimThrotSvc();

	/**
	 * Returns current time in milliseconds.
	 * 
	 * @return
	 */
	public static long sim_os_msec() {
		return System.currentTimeMillis();
	};

	public static void sim_rtcn_init_all() {
		int i;

		for (i = 0; i < SIM_NTIMERS; i++) {
			if (rtc_initd[i] != 0)
				sim_rtcn_init(rtc_initd[i], i);
		}

	}

	public static long sim_os_ms_sleep(long millis) {
		long start_time = System.currentTimeMillis();
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return System.currentTimeMillis() - start_time;

	}

	public static long sim_rtcn_init(long time, int tmr) {
		if (time == 0)
			time = 1;
		if ((tmr < 0) || (tmr >= SIM_NTIMERS))
			return time;
		rtc_rtime[tmr] = sim_os_msec();
		rtc_vtime[tmr] = rtc_rtime[tmr];
		rtc_nxintv[tmr] = 1000;
		rtc_ticks[tmr] = 0;
		rtc_hz[tmr] = 0;
		rtc_based[tmr] = time;
		rtc_currd[tmr] = time;
		rtc_initd[tmr] = time;
		rtc_elapsed[tmr] = 0;
		return time;
	}

	public static long sim_rtcn_calb(long ticksper, int tmr) {
		long new_rtime, delta_rtime;
		long delta_vtime;

		if ((tmr < 0) || (tmr >= SIM_NTIMERS))
			return 10000;
		rtc_hz[tmr] = ticksper;
		rtc_ticks[tmr] = rtc_ticks[tmr] + 1; /* count ticks */
		if (rtc_ticks[tmr] < ticksper) /* 1 sec yet? */
			return rtc_currd[tmr];
		rtc_ticks[tmr] = 0; /* reset ticks */
		rtc_elapsed[tmr] = rtc_elapsed[tmr] + 1; /* count sec */
		if (!rtc_avail) /* no timer? */
			return rtc_currd[tmr];
		new_rtime = sim_os_msec(); /* wall time */
		if (new_rtime < rtc_rtime[tmr]) { /* time running backwards? */
			rtc_rtime[tmr] = new_rtime; /* reset wall time */
			return rtc_currd[tmr]; /* can't calibrate */
		}
		delta_rtime = new_rtime - rtc_rtime[tmr]; /* elapsed wtime */
		rtc_rtime[tmr] = new_rtime; /* adv wall time */
		rtc_vtime[tmr] = rtc_vtime[tmr] + 1000; /* adv sim time */
		if (delta_rtime > 30000) /* gap too big? */
			return rtc_initd[tmr]; /* can't calibr */
		if (delta_rtime == 0) /* gap too small? */
			rtc_based[tmr] = rtc_based[tmr] * ticksper; /* slew wide */
		else
			rtc_based[tmr] = (long) (((double) rtc_based[tmr] * (double) rtc_nxintv[tmr]) / ((double) delta_rtime)); /*
																													 * new
																													 * base
																													 * rate
																													 */
		delta_vtime = rtc_vtime[tmr] - rtc_rtime[tmr]; /* gap */
		if (delta_vtime > SIM_TMAX) /* limit gap */
			delta_vtime = SIM_TMAX;
		else if (delta_vtime < -SIM_TMAX)
			delta_vtime = -SIM_TMAX;
		rtc_nxintv[tmr] = 1000 + delta_vtime; /* next wtime */
		rtc_currd[tmr] = (long) (((double) rtc_based[tmr] * (double) rtc_nxintv[tmr]) / 1000.0); /*
																								 * next
																								 * delay
																								 */
		if (rtc_based[tmr] <= 0) /* never negative or zero! */
			rtc_based[tmr] = 1;
		if (rtc_currd[tmr] <= 0) /* never negative or zero! */
			rtc_currd[tmr] = 1;
		return rtc_currd[tmr];
	}

	/* Prior interfaces - default to timer 0 */

	public static long sim_rtc_init(long time) {
		return sim_rtcn_init(time, 0);
	}

	public static long sim_rtc_calb(long ticksper) {
		return sim_rtcn_calb(ticksper, 0);
	}

	// sim_timer_init - get minimum sleep time available on this host

	public static boolean sim_timer_init() {
		sim_idle_enab = false; /* init idle off */
		sim_idle_rate_ms = 1; /* get OS timer rate */
		return (sim_idle_rate_ms != 0);
	}

	/*
	 * sim_idle - idle simulator until next event or for specified interval
	 * 
	 * Inputs: tmr = calibrated timer to use
	 * 
	 * Must solve the linear equation
	 * 
	 * ms_to_wait = w * ms_per_wait
	 * 
	 * Or w = ms_to_wait / ms_per_wait
	 */

	public static boolean sim_idle(int tmr, boolean sin_cyc) {
		long cyc_ms, w_ms, w_idle, act_ms;
		long act_cyc;

		if ((sim_clock_queue == null) || /* clock queue empty? */
		((sim_clock_queue.flags & UNIT_IDLE) == 0) || /* event not idle-able? */
		(rtc_elapsed[tmr] < sim_idle_stable)) { /* timer not stable? */
			if (sin_cyc)
				Control.sim_interval = Control.sim_interval - 1;
			return false;
		}
		cyc_ms = (rtc_currd[tmr] * rtc_hz[tmr]) / 1000; /* cycles per msec */
		if ((sim_idle_rate_ms == 0) || (cyc_ms == 0)) { /* not possible? */
			if (sin_cyc)
				Control.sim_interval = Control.sim_interval - 1;
			return false;
		}
		w_ms = (long) Control.sim_interval / cyc_ms; /* ms to wait */
		w_idle = w_ms / sim_idle_rate_ms; /* intervals to wait */
		if (w_idle == 0) { /* none? */
			if (sin_cyc)
				Control.sim_interval = Control.sim_interval - 1;
			return false;
		}
		act_ms = sim_os_ms_sleep(w_idle); /* wait */
		act_cyc = act_ms * cyc_ms;
		if (Control.sim_interval > act_cyc)
			Control.sim_interval = Control.sim_interval - act_cyc;
		else
			Control.sim_interval = 1;
		return true;
	}

	/* Set idling - implicitly disables throttling */

	public static int sim_set_idle(Unit uptr, int val, String cptr) {
		long v;

		if (sim_idle_rate_ms == 0)
			return (int) SCPE_NOFNC;
		if ((val != 0) && (sim_idle_rate_ms > (long) val))
			return (int) SCPE_NOFNC;
		if (cptr != null) {
			v = (long) Long.parseLong(cptr);
			if ((v < SIM_IDLE_STMIN))
				return (int) SCPE_ARG;
			sim_idle_stable = v;
		}
		sim_idle_enab = true;
		if (sim_throt_unit.sim_throt_type != SimThrotSvc.SIM_THROT_NONE) {
			sim_throt_unit.sim_set_throt(0, null);
			logger.finest("Throttling disabled");
		}
		return (int) SCPE_OK;
	}

	/* Clear idling */

	public static int sim_clr_idle(Unit uptr, int val, String cptr) {
		sim_idle_enab = false;
		return (int) SCPE_OK;
	}

	/* Show idling */

	public static int sim_show_idle(Unit uptr, int val) {
		if (sim_idle_enab)
			logger.finest("idle enabled");
		else
			logger.finest("idle disabled");
		return (int) SCPE_OK;
	}

}
