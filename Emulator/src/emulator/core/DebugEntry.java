package emulator.core;

public class DebugEntry {

	String name; /* control name */
	long mask; /* control bit */

	// DEBUG_PRS(d) (sim_deb && d.dctrl)
	// #define DEBUG_PRD(d) (sim_deb && d->dctrl)
	// #define DEBUG_PRI(d,m) (sim_deb && (d.dctrl & (m)))
	// #define DEBUG_PRJ(d,m) (sim_deb && (d->dctrl & (m)))

}
