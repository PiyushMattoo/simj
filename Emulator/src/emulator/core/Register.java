package emulator.core;

/**
 * Representing a register
 * 
 * @author ccebelenski
 * 
 */
public class Register {

	public String name; /* name */
	public long value;
	public long radix; /* radix */
	public long width; /* width */
	public long offset; /* starting bit */
	public long depth; /* save depth */
	public long flags; /* flags */
	public long qptr; /* circ q ptr */
}
