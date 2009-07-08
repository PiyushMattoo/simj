package emulator.commands;

import org.naturalcli.Command;
import org.naturalcli.InvalidSyntaxException;

public class BootCommand extends Command {
	public BootCommand() {
		try {
			prepare("boot <unit:string>",
					"Bootstrap unit.",
					new BootCommandExecutor());
		} catch (InvalidSyntaxException e) {
			throw new RuntimeException(e);
		}
	}
}