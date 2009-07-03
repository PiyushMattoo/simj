package emulator.core;

import java.util.logging.Logger;

/**
 * Class representing a system.
 * @author ccebelenski
 *
 */
public abstract class SimSystem {

	public static Logger logger = Logger.getLogger(SimSystem.class
			.getCanonicalName());
	public String name; // System name
	public Device CPU; // CPU Device
	
	// TODO Options
	
	
}
