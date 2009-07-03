package emulator.core;

public class Control {

	static Unit sim_clock_queue = null;
	public static long sim_interval = 0;
	static double sim_time;
	static long sim_rtime;
	static long noqueue_time;

	/*
	 * sim_cancel - cancel (dequeue) event
	 * 
	 * Inputs: uptr = pointer to unit Outputs: reason = result (SCPE_OK if ok)
	 */

	public static long sim_cancel(Unit uptr) {
		Unit cptr, nptr;

		if (sim_clock_queue == null)
			return Defs.SCPE_OK;
		UPDATE_SIM_TIME_UNIT(sim_clock_queue); /* update sim time */
		nptr = null;
		if (sim_clock_queue == uptr)
			nptr = sim_clock_queue = uptr.next;
		else {
			for (cptr = sim_clock_queue; cptr != null; cptr = cptr.next) {
				if (cptr.next == uptr) {
					nptr = cptr.next = uptr.next;
					break; /* end queue scan */
				}
			}
		}
		if (nptr != null)
			nptr.time = nptr.time + uptr.time;
		uptr.next = null; /* hygiene */
		uptr.time = 0;
		if (sim_clock_queue != null)
			sim_interval = sim_clock_queue.time;
		else
			sim_interval = noqueue_time = Defs.NOQUEUE_WAIT;
		return Defs.SCPE_OK;
	}

	private static void UPDATE_SIM_TIME_UNIT(Unit x) {
		sim_time = sim_time + (x.time - sim_interval);
		sim_rtime = sim_rtime + ((long) (x.time - sim_interval));
		x.time = sim_interval;
	}

	private static long UPDATE_SIM_TIME_LONG(long x) {
		sim_time = sim_time + (x - sim_interval);
		sim_rtime = sim_rtime + ((long) (x - sim_interval));
		x = sim_interval;
		return x;
	}

	/*
	 * sim_activate - activate (queue) event
	 * 
	 * Inputs: uptr = pointer to unit event_time = relative timeout Outputs:
	 * reason = result (SCPE_OK if ok)
	 */

	public static long sim_activate(Unit uptr, long event_time) {
		Unit cptr, prvptr;
		long accum;

		if (event_time < 0)
			return Defs.SCPE_IERR;
		if (sim_is_active(uptr) != 0) /* already active? */
			return Defs.SCPE_OK;
		if (sim_clock_queue == null) {
			noqueue_time = UPDATE_SIM_TIME_LONG(noqueue_time);
		} else { /* update sim time */
			UPDATE_SIM_TIME_UNIT(sim_clock_queue);
		}

		prvptr = null;
		accum = 0;
		for (cptr = sim_clock_queue; cptr != null; cptr = cptr.next) {
			if (event_time < (accum + cptr.time))
				break;
			accum = accum + cptr.time;
			prvptr = cptr;
		}
		if (prvptr == null) { /* insert at head */
			cptr = uptr.next = sim_clock_queue;
			sim_clock_queue = uptr;
		} else {
			cptr = uptr.next = prvptr.next; /* insert at prvptr */
			prvptr.next = uptr;
		}
		uptr.time = event_time - accum;
		if (cptr != null)
			cptr.time = cptr.time - uptr.time;
		sim_interval = sim_clock_queue.time;
		return Defs.SCPE_OK;
	}

	/*
	 * sim_is_active - test for entry in queue, return activation time
	 * 
	 * Inputs: uptr = pointer to unit Outputs: result = absolute activation time
	 * + 1, 0 if inactive
	 */

	public static long sim_is_active(Unit uptr) {
		Unit cptr;
		long accum;

		accum = 0;
		for (cptr = sim_clock_queue; cptr != null; cptr = cptr.next) {
			if (cptr == sim_clock_queue) {
				if (sim_interval > 0)
					accum = accum + sim_interval;
			} else
				accum = accum + cptr.time;
			if (cptr == uptr)
				return accum + 1;
		}
		return 0;
	}
}
