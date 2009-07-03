/*
 * CommandException.java
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
 */

package org.naturalcli;

/**
 * Thrown when something unexpected happens for a ParameterType
 * 
 * @author Ferran Busquets
 */

@SuppressWarnings("serial")
public class UnknownParameterType extends Exception {
    
    
    /**
     * Creates a new instance of ParameterTypeException
     * @param m Exception message
     */
    public UnknownParameterType(String m) {
        super(m);
    }

    /**
     * Creates a new instance of ParameterTypeException
     * @param m Exception message
     * @param c Cause of this exception
     */
    public UnknownParameterType(String m, Throwable c) {
        super(m);
        this.initCause(c);
    }
 }
