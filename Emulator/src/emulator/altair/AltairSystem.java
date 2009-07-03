package emulator.altair;

import emulator.core.SimSystem;

public class AltairSystem extends SimSystem {

	public AltairSystem() {
		this.name="Altair8080";
		this.CPU = new AltairCPU();
		logger.info("Registered system: Altair8080");
		
	}
}
