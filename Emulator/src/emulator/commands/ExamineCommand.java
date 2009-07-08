package emulator.commands;

import org.naturalcli.Command;
import org.naturalcli.InvalidSyntaxException;

public class ExamineCommand extends Command {
	public ExamineCommand() {
		try {
			prepare("examine <args:string> ...",
					"Examine memory or registers.",
					new ExamineCommandExecutor());
		} catch (InvalidSyntaxException e) {
			throw new RuntimeException(e);
		}
	}
}
