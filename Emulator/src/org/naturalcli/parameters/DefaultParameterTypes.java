/* 
 * DefaultParameters.java
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

import java.util.Set;
import java.util.HashSet;

import org.naturalcli.IParameterType;

/**
 * Helper class to obtain a set of default parameter types.
 * 
 * @author Ferran Busquets
 */
public class DefaultParameterTypes {
	
	/**
	 * Creates a set with a default parameter types.
	 * 
	 * @return a set with the default parameter types.
	 * @see EmailParamType
	 * @see IdentifierParamType
	 * @see IntegerParamType
	 * @see StringParamType
	 * @see FileParamType
	 */
	public static Set<IParameterType> createSet()
	{
		Set<IParameterType> s = new HashSet<IParameterType>();
        s.add(new BinaryParamType());
        s.add(new ByteParamType());
        s.add(new DoubleParamType());
		s.add(new EmailParamType());
        s.add(new FileParamType());
        s.add(new FloatParamType());
        s.add(new HexadecimalParamType());
		s.add(new IdentifierParamType());
		s.add(new IntegerParamType());
        s.add(new LongParamType());
        s.add(new OctalParamType());
        s.add(new ShortParamType());
		s.add(new StringParamType());
        s.add(new URLParamType());
        s.add(new WorkingURLParamType());
		return s;
	}
}
