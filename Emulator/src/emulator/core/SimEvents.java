package emulator.core;

public class SimEvents {

	
	public static long sim_process_event ()
	{
//	UNIT *uptr;
	long reason = 0;
//
//	if (stop_cpu)                                           /* stop CPU? */
//	    return SCPE_STOP;
//	if (sim_clock_queue == NULL) {                          /* queue empty? */
//	    UPDATE_SIM_TIME (noqueue_time);                     /* update sim time */
//	    sim_interval = noqueue_time = NOQUEUE_WAIT;         /* flag queue empty */
//	    return SCPE_OK;
//	    }
//	UPDATE_SIM_TIME (sim_clock_queue->time);                /* update sim time */
//	do {
//	    uptr = sim_clock_queue;                             /* get first */
//	    sim_clock_queue = uptr->next;                       /* remove first */
//	    uptr->next = NULL;                                  /* hygiene */
//	    uptr->time = 0;
//	    if (sim_clock_queue != NULL)
//	        sim_interval = sim_clock_queue->time;
//	    else sim_interval = noqueue_time = NOQUEUE_WAIT;
//	    if (uptr->action != NULL)
//	        reason = uptr->action (uptr);
//	    else reason = SCPE_OK;
//	    } while ((reason == SCPE_OK) && (sim_interval == 0));
//
//	/* Empty queue forces sim_interval != 0 */

	return reason;
	}
}
