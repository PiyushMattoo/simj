package commands;

import org.naturalcli.Command;
import org.naturalcli.InvalidSyntaxException;


public class ShowCommand extends Command{


		
		public ShowCommand() 
		{
			try {
				prepare("show <name:string> ...", "Display attributes about devices and settings.", new ShowCommandExecutor());
			} catch (InvalidSyntaxException e) {
				throw new RuntimeException(e);
			}
		}

		
	
}
