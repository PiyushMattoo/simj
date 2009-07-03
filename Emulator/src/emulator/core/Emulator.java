package emulator.core;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Graphics;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import net.wimpi.telnetd.BootException;
import net.wimpi.telnetd.TelnetD;
import thor.app.VT100Telnet;
import emulator.altair.AltairSystem;

public class Emulator {

	public List<SimSystem> systems = new ArrayList<SimSystem>();
	
	public Emulator() {
	
		// Add in known systems.  TODO Makes this Sprung in or something
		systems.add(new AltairSystem());
		
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// Read in startup properties file
		Properties props = new Properties();
		try {
			props.load(new FileInputStream("emulator.properties"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Emulator emu = new Emulator();
		emu.bootTelnetd(props);


		emu.bootTerminal(props);
		
	}

	private void bootTelnetd(Properties props) {

		Telnetd d = new Telnetd(props);
		new Thread(d).start();

	}

	private void bootTerminal(Properties props) {
		Terminal t = new Terminal(props);
		Thread th = new Thread(t);
		th.setPriority(Thread.MIN_PRIORITY + 1);
		th.start();
		
		
	}
	private static class Telnetd implements Runnable {

		private Properties props = null;
		private TelnetD myTD = null;

		public Telnetd(Properties props) {
			this.props = props;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				myTD = TelnetD.createTelnetD(props);
			} catch (BootException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			myTD.start();
		}

	}

	private static class Terminal implements Runnable {

		private Properties props;
		
		public Terminal(Properties props) {
			this.props = props;
		}
		@Override
		public void run() {
			try {
				// TODO Start the console telnet vt100 emulator
				Component t = new VT100Telnet("localhost", 6666);
				Frame f = new Frame("localhost") {
					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					public void update(Graphics g) {
					}

					public void paint(Graphics g) {
					}
				};
				f.setLayout(new BorderLayout());
				f.add(t, BorderLayout.CENTER);
				f.setResizable(false);
				f.pack();
				f.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();

			}
			
		}
		
	}
}
