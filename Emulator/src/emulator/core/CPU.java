package emulator.core;

import java.util.HashMap;
import java.util.Map;

public abstract class CPU extends Device {

	public Map<String, Register> registers = new HashMap<String, Register>();
	public Memory memory;
	public Map<String, Modifier> modifiers = new HashMap<String, Modifier>();

	public Device cpuDevice;

	/**
	 * Main instruction loop
	 * 
	 * @return
	 */
	public abstract long run();

	public abstract void reset();

	public abstract void deposit(long val, long addr, Unit uptr, long sw);

	public abstract long examine(long addr, Unit uptr, long sw);

	public abstract void setSize(Unit uptr, long val, String cptr/* , void *desc */);


}
