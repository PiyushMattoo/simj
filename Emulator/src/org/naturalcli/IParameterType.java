/* 
 * IParameterType.java
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
 * A parameter type for all the emulator.commands.
 *
 * @author Ferran Busquets
 */
public interface IParameterType {

	/**
	 * Gets the parameter type name. 
	 * 
	 * @return the name of the parameter type
	 */
	public String getParameterTypeName();
	
	/**
	 * Checks if a parameter value is of this type 
	 * of parameter.
	 * 
	 * @param  the string to be validated as this parameter type
	 * @return <code>true</code> if the validation it's right;
	 *         <code>false</code> otherwise
	 */
	public boolean validateParameter(String value);

	/**
	 * Checks if a parameter value is of this type 
	 * of parameter and returns a detailed message
	 * if the validation fails.
	 * 
	 * @param  the string to be validated as this parameter type
	 * @return <code>null</code> if the validation it's right;
	 *         a detailed message if something it's wrong
	 */
	public String validationMessage(String value);

	/**
	 * Converts the string representing the parameter value to
	 * the corresponding type value.
	 *  
	 * @param strRepresentation the string representation of the value
	 * @return real object value
	 */
	public Object convertParameterValue(String strRepresentation);		
}
