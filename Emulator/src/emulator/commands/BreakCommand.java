package emulator.commands;

import org.naturalcli.Command;
import org.naturalcli.InvalidSyntaxException;

public class BreakCommand extends Command {
	public BreakCommand() {
		try {
			prepare("break <bpt:string> ...",
					"Set breakpoints.",
					new BreakCommandExecutor());
		} catch (InvalidSyntaxException e) {
			throw new RuntimeException(e);
		}
	}
}
