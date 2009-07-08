package emulator.altair;

import emulator.core.SimSystem;

public class AltairSystem extends SimSystem {

	public AltairSystem() {
		this.name="Altair8080";
		this.CPU = new AltairCPU();
		devices.put("CPU",this.CPU);
		
		// Devices
		AltairDiskDevice dsk = new AltairDiskDevice();
		AltairSerialDevice sio = new AltairSerialDevice();

		devices.put("DSK", dsk);
		devices.put("SIO", sio);
		
		logger.info("Registered system: Altair8080");
		
	}
}
