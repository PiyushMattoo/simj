package emulator.core;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import net.wimpi.telnetd.io.BasicTerminalIO;
import net.wimpi.telnetd.io.toolkit.Editfield;
import net.wimpi.telnetd.io.toolkit.InputFilter;
import net.wimpi.telnetd.io.toolkit.Label;
import net.wimpi.telnetd.net.Connection;
import net.wimpi.telnetd.net.ConnectionData;
import net.wimpi.telnetd.net.ConnectionEvent;
import net.wimpi.telnetd.shell.Shell;

import org.naturalcli.Command;
import org.naturalcli.ExecutionException;
import org.naturalcli.NaturalCLI;
import org.naturalcli.commands.ExecuteFileCommand;
import org.naturalcli.commands.HelpCommand;

import commands.AttachCommand;
import commands.ShowCommand;

public class EmuShell implements Shell {
	public static Connection conn;
	public static BasicTerminalIO io;
	private Editfield ef;
	
	   // Create an empty command set
	   private Set<Command> cs = new HashSet<Command>();
	   // Create the interpreter
	   private NaturalCLI nc = new NaturalCLI(cs);

	   public boolean done = false;

	  public EmuShell() {
		  super();
		  
		  
		   // Add the commands that can be understood
		   cs.add(new HelpCommand(cs)); // help
		   cs.add(new ExecuteFileCommand(nc)); // execute file <filename:string>
		   cs.add(new ShowCommand()); // Show command
		   cs.add(new AttachCommand()); // attach command

	  }
	@Override
	public void run(Connection con) {
		try {
			conn = con;
			// mycon.setNextShell("nothing");
			io = conn.getTerminalIO();
			io.setAutoflushing(true);
			// dont forget to register listener
			conn.addConnectionListener(this);
			ConnectionData cd = conn.getConnectionData();
			// Force the console to VT100
			cd.setNegotiatedTerminalType("vt100");
			String terminalType = cd.getNegotiatedTerminalType();
			String hostAddress = cd.getHostAddress();
			int port = cd.getPort();

			// clear the screen and start from zero
			io.eraseScreen();
			io.homeCursor();

			// We just read any key
			io.write("Emulator console. [" + hostAddress + ": " + port + "]");
			io.write(BasicTerminalIO.CRLF);
			io.write("V0.1 - July 2009");
			io.write(BasicTerminalIO.CRLF);
			io.write("Terminal: " + terminalType);

			io.write(BasicTerminalIO.CRLF);
			io.write(BasicTerminalIO.CRLF);
			// TODO print some stats here.
			io.flush();

			// Set the terminal IO for the command processor.
			nc.setIo(io);
			

			while (!done) {
				Label l = new Label(io, "ProptString", "> ");
				l.draw();
				ef = new Editfield(io, "Prompt", 60);

				ef.registerInputFilter(new InputFilter() {

					public int filterInput(int key) throws IOException {

						return key;

					}
				});
				ef.run();
				io.write(BasicTerminalIO.CRLF);
				io.flush();
				processCommand(ef.getValue());

			}

		} catch (Exception ex) {
			// TODO Fixme
			ex.printStackTrace();
		}

	}

	@Override
	public void connectionIdle(ConnectionEvent ce) {
		try {
			io.write("CONNECTION_IDLE");
			io.flush();
		} catch (IOException e) {
			e.printStackTrace(); // TODO fixme
		}

	}

	@Override
	public void connectionLogoutRequest(ConnectionEvent ce) {
		try {
			io.write("CONNECTION_LOGOUTREQUEST");
			io.flush();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	@Override
	public void connectionSentBreak(ConnectionEvent ce) {
		try {
			io.write("CONNECTION_BREAK");
			io.flush();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void connectionTimedOut(ConnectionEvent ce) {
		try {
			io.write("CONNECTION_TIMEDOUT");
			io.flush();
			// close connection
			conn.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public static Shell createShell() {
		return new EmuShell();
	}// createShell
	
	
	public void processCommand(String command) throws IOException {
		
		try {
			if(command.length() == 0) return;
			nc.execute(command);
		
		} catch (ExecutionException e) {
			io.write("?CMD Error.");
			io.write(BasicTerminalIO.CRLF);
			io.flush();
		} catch (RuntimeException e) {
			io.write("?Sys Error. [" + e.getCause().getMessage() + "]");
			io.write(BasicTerminalIO.CRLF);
			io.flush();
		}
		
	}
}
