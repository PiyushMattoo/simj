/*
 * Command.java
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

package org.naturalcli;


/**
 * Represents a command definition 
 * 
 * @author Ferran Busquets
 */
public class Command {

	/** Help message */
	private String help;

	/** Command executor */	
	private ICommandExecutor executor;
	
	/** Syntax definition  */
	private Syntax syntax;

	private final char CHAR_HIDDEN_COMMAND = '.';
	
	/**
	 * Constructs a new command.
	 * 
	 * @param syntax the syntax for the command.
	 * @param helpthe help help of the command.
	 * @param ce command executor.
	 * @throws InvalidSyntaxDefinionException.
	 */
	public Command(String syntax, String help, ICommandExecutor ce) throws InvalidSyntaxException{
		this.prepare(syntax, help, ce);
	}

	/**
	 * Default constructor only for inheritors.
	 */
	protected Command() {
	}
	
	/**
	 * Initialize the command.
	 * 
	 * @param syntax the syntax for the command.
	 * @param helpthe help help of the command.
	 * @param ce command executor.
	 * @throws InvalidSyntaxDefinionException.
	 */
	protected void prepare(String syntax, String help, ICommandExecutor ce) throws InvalidSyntaxException 
	{
		if (help == null || help.length() == 0)
			throw new IllegalArgumentException("Syntax cannot be empty.");
		if (ce == null)
			throw new IllegalArgumentException("Command executor cannot be null.");
		this.help = help;
		this.syntax = new Syntax(syntax);
		this.executor = ce;
	}
	
	/**
	 * Determine if this is a hidden command.
	 * 
	 * @return <code>true</code> if it's a hidden command, <code>false</code> if not.
	 */
	public boolean isHidden() {
		return getHelp().charAt(0) == CHAR_HIDDEN_COMMAND;
	}

	/**
	 * Returns a string with the syntax for the command.
	 * 
	 * @return A string with the syntax for the command.
	 */
	public Syntax getSyntax() {
		return syntax;
	}

	/**
	 * Returns the help for the commend.
	 * 
	 * @return The help for the command.
	 */
	public String getHelp() {
		return help;
	}

	/**
	 * Get the executor for the command.
	 * 
	 * @return the executor.
	 */
	public ICommandExecutor getExecutor() {
		return executor;
	}

}
