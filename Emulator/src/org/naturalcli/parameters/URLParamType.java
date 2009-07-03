/* 
 * URLParamType.java
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

import java.net.MalformedURLException;
import java.net.URL;

import org.naturalcli.IParameterType;

/**
 * The class implements an URL parameter type. 
 * @author Ferran Busquets 
 */
public class URLParamType implements IParameterType {

	/* (non-Javadoc)
	 * @see org.naturalcli.IParameterType#getParameterTypeName()
	 */
	@Override
	public String getParameterTypeName() {
		return "url";
	}

	/* (non-Javadoc)
	 * @see org.naturalcli.IParameterType#validateParameter(java.lang.String)
	 */
	@Override
	public boolean validateParameter(String value) {
		try {
			new URL(value);
			return true;
		} catch (MalformedURLException e) {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see org.naturalcli.IParameterType#validationMessage(java.lang.String)
	 */
	@Override
	public String validationMessage(String value) {
		return this.validateParameter(value) ? null : "Malformed URL.";
	}
	
	/* (non-Javadoc)
	 * @see org.naturalcli.IParameterType#convertParameterValue(java.lang.String)
	 */
	@Override
	public Object convertParameterValue(String strRepresentation) {
		try {
			return new URL(strRepresentation);
		} catch (MalformedURLException e) {
			return null;
		}
	}


}
