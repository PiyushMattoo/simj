/* 
 * CommandParseData.java
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
 * Encapsulate the data that restuls of a command parse. 
 * It means the parameters values and the list of tokens found.
 * 
 * @author Ferran Busquets
 */
public class ParseResult {
	
	/** Parameters values **/
	private Object[] parameterValues;
	
	/** Tokens given **/
	private boolean[] tokensGiven;

	/**
	 * Constructor
	 * 
	 * @param paramValues the ordered parameter values array. For optional
	 *                    parameters not provided will be <code>null</code>
	 * @param tokensGiven the ordered array with all the tokens saying if
	 *                    each token is given or not. For non-optional tokens
     *                    the value will be always <code>true</code>.
	 */
	public ParseResult(Object[] parameterValues, boolean[] tokensGiven)
	{
		if (parameterValues == null)
			throw new IllegalArgumentException("Parameter values array cannot be null.");
		if (tokensGiven == null)
			throw new IllegalArgumentException("Tokens found array cannot be null.");
		this.parameterValues = parameterValues;
		this.tokensGiven = tokensGiven;
	}

	/**
	 * Get the parameter value in the given index.
	 * 
	 * @param parameterIndex the parameter index. This index is relative
	 *                       only for the parameters.
	 * @return the parameter value.
	 */
	public Object getParameterValue(int parameterIndex)
	{
		return this.parameterValues[parameterIndex];
	}

	/**
	 * Get a copy of the parameter values
	 * 
	 * @return object array with the prameter values 
	 */
	public Object[] getParameterValues()
	{
		return this.parameterValues.clone();
	}	
	
	/**
	 * Get the number of all possible parameters
	 *  
	 * @return number of parameters
	 */
	public int getParameterCount()
	{
		return this.parameterValues.length;
	}
		
	/**
	 * Get if the token is given or not.
	 * 
	 * @param tokenIndex the token index. 
	 * 
	 * @return <code>true</code> if the token is given or <code>false</code>
	 *         if the token is not given (only for optional parameters).
	 */
	public boolean isTokenGiven(int tokenIndex)
	{
		return this.tokensGiven[tokenIndex];
	}
	
	/**
	 * Get a copy of the tokens given.
	 * 
	 * @return boolean array with the tokens given.
	 */
	public boolean[] getTokensGiven()
	{
		return this.tokensGiven.clone();
	}
	
}
