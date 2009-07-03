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
 * Executor for <code>HTMLHelpCommand</code>
 * 
 * @see HTMLHelpCommandExecutor
 * @author Ferran Busquets
 */
public class HTMLHelpCommandExecutor implements ICommandExecutor {

	/** Sets of commands */
	private Set<Command> commands;

	/**
	 * Constructor.
	 * 
	 * @param commands
	 *            the set of commands for the help
	 */
	public HTMLHelpCommandExecutor(Set<Command> commands) {
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
			String syn = c.getSyntax().getDefinition().replaceAll("<", "&lt;")
					.replaceAll(">", "&gt;");
			try {
				io.write("<b>" + syn + "</b><br>");
				io.write("<p>&nbsp;&nbsp;&nbsp;" + c.getHelp() + "<br>");
				io.write("<p>");
			} catch (Exception e) {
			}
		}
	}

}
