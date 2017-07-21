/*
 * jETeL/CloverETL - Java based ETL application framework.
 * Copyright (c) Javlin, a.s. (info@cloveretl.com)
 *  
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jetel.component.validator;

import org.jetel.component.validator.rules.CustomValidationRule;

/**
 * Wrapper for custom validation rule which holds name and its code
 * 
 * @author drabekj (info@cloveretl.com) (c) Javlin, a.s. (www.cloveretl.com)
 * @created 18.4.2013
 * @see CustomValidationRule
 */
public class CustomRule {
	
	private String name;
	private String code;
	
	/**
	 * Create custom rule with given name and code
	 * @param name Name of rule
	 * @param code Code of rule
	 */
	public CustomRule(String name, String code) {
		this.name = name;
		this.code = code;
	}
	
	/**
	 * @return Code of rule
	 */
	public String getCode() {
		return code;
	}
	/**
	 * @return Name of rule
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param code New code to be set
	 */
	public void setCode(String code) {
		this.code = code;
	}
	/**
	 * @param name New name to be set
	 */
	public void setName(String name) {
		this.name = name;
	}
}