package emulator.core;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Class representing a system.
 * @author ccebelenski
 *
 */
public abstract class SimSystem {

	// Public pointer to the currently active system, null if not set.
	public static SimSystem system = null;
	
	public static Logger logger = Logger.getLogger(SimSystem.class
			.getCanonicalName());
	public String name; // System name
	public Device CPU; // CPU Device
	public Map<String, Device> devices = new HashMap<String, Device>(); // Devices
	
	// TODO Options
	
	
}
