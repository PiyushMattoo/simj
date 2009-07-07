package commands;

import org.naturalcli.Command;
import org.naturalcli.InvalidSyntaxException;

public class AttachCommand extends Command {

	public AttachCommand() {
		try {
			prepare("attach <unit:string> <file:string>",
					"Attach file to simulated unit.",
					new AttachCommandExecutor());
		} catch (InvalidSyntaxException e) {
			throw new RuntimeException(e);
		}
	}

}
