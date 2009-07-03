/*
 * ParameterTypes.java
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

import java.util.Collection;

import org.naturalcli.parameters.DefaultParameterTypes;



/**
 * This class checks parameters values against their types.
 *
 * @author Ferran Busquets
 *
 * @see IParameterType
 */
public class ParameterValidator {

	/** Parameter types for the validation */
	private Collection<IParameterType> parameterTypes;
	
	/** 
     * Creates a new instance of <code>ParameterValidator</code> with default parameter types
     */
    public ParameterValidator() 
    {   
    	this.parameterTypes = DefaultParameterTypes.createSet();
    }	
	
	/** 
     * Creates a new instance of <code>ParameterValidator</code>
     * 
     * @param parameterTypes the parameter types collection
     */
    public ParameterValidator(Collection<IParameterType> parameterTypes) 
    {   
    	this.parameterTypes = parameterTypes;
    }	


    /**
     * Validate a parameter value for a type
     * 
     * @param value the parameter value
     * @param type the parameter type name
     * @return <code>null</code> if validated, otherwise a error message
     * @throws UnknownParameterType raised if the parameter is not found
     */
    public String validate(String value, String type) throws UnknownParameterType
    {
    	IParameterType pt = getParameterType(type);
    	// If not found throw exception
    	if (pt == null)
            throw new UnknownParameterType(type);
    	// Validate the parameter
    	return pt.validationMessage(value);
    }
        
    /**
     * Gets the parameter type for the given type name
     *  
     * @param type the type name
     * @return the paramter type object
     */
    public IParameterType getParameterType(String type)
    {
    	IParameterType pt = null;
    	// Look for the parameter type
    	for (IParameterType s : this.parameterTypes)
    	{
    		if (s.getParameterTypeName().equals(type))
    		{
    			pt = s;
    			break;
    		}
    	}
    	return pt;
    }
}
