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
 * to the console.
 * 
 * @see HelpCommandExecutor
 * @author Ferran Busquets
 */
public class HelpCommand extends Command {
	
	public HelpCommand(Set<Command> commands) 
	{
		try {
			prepare("help", "Shows the emulator.commands help on plain text.",
					new HelpCommandExecutor(commands)
			);
		} catch (InvalidSyntaxException e) {
			throw new RuntimeException(e);
		}
	}	

}
