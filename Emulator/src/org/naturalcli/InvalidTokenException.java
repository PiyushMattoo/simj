/* 
 * InvalidTokenException.java
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
 * Thrown when token with and invalid format is found. 
 * 
 * @author Ferran Busquets
 *
 */
@SuppressWarnings("serial")
public class InvalidTokenException extends Exception {

	/**
	 * 
	 */
	public InvalidTokenException() {
	}

	/**
	 * @param message
	 */
	public InvalidTokenException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public InvalidTokenException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public InvalidTokenException(String message, Throwable cause) {
		super(message, cause);
	}

}
