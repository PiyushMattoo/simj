/* 
 * IntegerParamType.java
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
package org.naturalcli.parameters;

import org.naturalcli.IParameterType;


/**
 * The class implements an integer parameter type.
 * 
 * @author Ferran Busquets
 *
 */
public class IntegerParamType implements IParameterType {

	/* (non-Javadoc)
	 * @see org.naturalcli.paramtypes.IParameterType#getParameterTypeName()
	 */
	@Override
	public String getParameterTypeName() {
		return "integer";
	}

	/* (non-Javadoc)
	 * @see org.naturalcli.paramtypes.IParameterType#validateParameter(java.lang.String)
	 */
	@Override
	public boolean validateParameter(String value) {
        try {
            Integer.valueOf(value);
            return true;
        } catch (NumberFormatException e)
        {
            return false;
        }
    }

	/* (non-Javadoc)
	 * @see org.naturalcli.paramtypes.IParameterType#validationMessage(java.lang.String)
	 */
	@Override
	public String validationMessage(String value) {
		return this.validateParameter(value) ? null : "Bad integer.";
	}

	/* (non-Javadoc)
	 * @see org.naturalcli.paramtypes.IParameterType#convertParameterValue(java.lang.String)
	 */
	@Override
	public Object convertParameterValue(String strRepresentation) {
		return new Integer(strRepresentation);
	}		
}
