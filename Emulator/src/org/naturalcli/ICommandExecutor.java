/*
 * ICommandExecutor.java
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

import net.wimpi.telnetd.io.BasicTerminalIO;

/**
 * A command executor runs a command for the parameters.
 * 
 * @author Ferran Busquets
 */
public interface ICommandExecutor {
    
	/**
	 * Execute the command.
	 *
	 * @param parseResult the parse data for the command.
	 */
    public void execute(ParseResult parseResult, BasicTerminalIO io) throws ExecutionException;

}
