/* 
 * HelpCommand.java
 *
 * Copyright (C) 2007 Ferran Busquets
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.naturalcli.commands;

import java.util.Set;

import org.naturalcli.Command;
import org.naturalcli.InvalidSyntaxException;


/**
 * Implements a command that outputs help information 
 * to the console in HTML format.
 * 
 * @see HTMLHelpCommandExecutor
 * @author Ferran Busquets
 */
public class HTMLHelpCommand extends Command {

	/**
	 * Constructor.
	 * @param commands the set of commands for the help
	 */	
	public HTMLHelpCommand(Set<Command> commands) 
	{
		try {
			prepare("htmlhelp", "Shows the commands help on HTML format.",
					new HTMLHelpCommandExecutor(commands)
			);
		} catch (InvalidSyntaxException e) {
			throw new RuntimeException(e);
		}
	}
	
	

}
