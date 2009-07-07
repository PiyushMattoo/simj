/* 
 * HelpCommandExecutor.java
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

import net.wimpi.telnetd.io.BasicTerminalIO;

import org.naturalcli.Command;
import org.naturalcli.ParseResult;
import org.naturalcli.ICommandExecutor;

/**
 * Executor for <code>HelpCommand</code>
 * 
 * @see HelpCommandExecutor
 * @author Ferran Busquets
 */
public class HelpCommandExecutor implements ICommandExecutor {

	/** Commands set */
	private Set<Command> commands;

	/**
	 * Constructor.
	 * 
	 * @param emulator.commands
	 *            the set of emulator.commands for the help
	 */
	public HelpCommandExecutor(Set<Command> commands) {
		this.commands = commands;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.naturalcli.ICommandExecutor#execute(java.lang.Object[])
	 */
	@Override
	public void execute(ParseResult parseResult, BasicTerminalIO io) {
		for (Command c : commands) {
			if (c.isHidden())
				continue;
			try {
				io.write((c.getSyntax().toString()));
				io.write("\t" + c.getHelp());
				io.write(BasicTerminalIO.CRLF);
			} catch (Exception e) {
			}
		}
	}

}
