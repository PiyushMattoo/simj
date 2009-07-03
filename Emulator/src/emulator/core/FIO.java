package emulator.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * This library includes:
 * 
 * sim_finit - initialize package <BR>
 * sim_fopen - open file <BR>
 * sim_fread - endian independent read (formerly fxread) <BR>
 * sim_write - endian independent write (formerly fxwrite) <BR>
 * sim_fseek - extended (>32b) seek (formerly fseek_ext) <BR>
 * sim_fsize - get file size <BR>
 * 
 * sim_fopen and sim_fseek are OS-dependent. The other routines are not.
 * sim_fsize is always a 32b routine (it is used only with small capacity random
 * access devices like fixed head disks and DECtapes).
 */
public class FIO {

	public static int sim_end = 1; /* 1 = little */

	/*
	 * OS-independent, endian independent binary I/O package
	 * 
	 * For consistency, all binary data read and written by the simulator is
	 * stored in little endian data order. That is, in a multi-byte data item,
	 * the bytes are written out right to left, low order byte to high order
	 * byte. On a big endian host, data is read and written from high byte to
	 * low byte. Consequently, data written on a little endian system must be
	 * byte reversed to be usable on a big endian system, and vice versa.
	 * 
	 * These routines are analogs of the standard C runtime routines fread and
	 * fwrite. If the host is little endian, or the data items are size char,
	 * then the calls are passed directly to fread or fwrite. Otherwise, these
	 * routines perform the necessary byte swaps. Sim_fread swaps in place,
	 * sim_fwrite uses an intermediate buffer.
	 */

	public static int sim_finit() {
		return 0; // Java is big-endian
	}

	public static int sim_fread(ByteBuffer bptr, RandomAccessFile fptr) throws IOException {
		int c;

		if (sim_end == 1)
			bptr.order(ByteOrder.LITTLE_ENDIAN);
		c = fptr.read(bptr.array());
		return c;
	}

	public static int sim_fwrite(ByteBuffer bptr, RandomAccessFile fptr) throws IOException {

		if (sim_end == 1)
			bptr.order(ByteOrder.LITTLE_ENDIAN);
		fptr.write(bptr.array());

		return (bptr.array().length);
	}

	/* Get file size */

	public static long sim_fsize_name(String fname) throws IOException {
		RandomAccessFile f = sim_fopen(fname, "r");
		long len = sim_fsize(f);
		f.close();
		return len;
	}

	public static long sim_fsize(RandomAccessFile fp) throws IOException {

		return fp.length();

	}

	public static RandomAccessFile sim_fopen(String file, String mode)
			throws FileNotFoundException {

		return new RandomAccessFile(file, mode);

	}

	public static void sim_fseek(RandomAccessFile st, long xpos, long origin)
			throws IOException {
		st.seek(xpos + origin);

	}

}
