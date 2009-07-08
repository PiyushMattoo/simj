package emulator.commands;

import org.naturalcli.Command;
import org.naturalcli.InvalidSyntaxException;

public class ContCommand extends Command {
	public ContCommand() {
		try {
			prepare("cont",
					"Continue simulation.",
					new ContCommandExecutor());
		} catch (InvalidSyntaxException e) {
			throw new RuntimeException(e);
		}
	}
}
