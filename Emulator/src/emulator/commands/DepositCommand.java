package emulator.commands;

import org.naturalcli.Command;
import org.naturalcli.InvalidSyntaxException;

public class DepositCommand extends Command {
	public DepositCommand() {
		try {
			prepare("deposit [<modifiers:string>] <list:string> <val:string>",
					"Deposit in memory or registers.",
					new DepositCommandExecutor());
		} catch (InvalidSyntaxException e) {
			throw new RuntimeException(e);
		}
	}
}
