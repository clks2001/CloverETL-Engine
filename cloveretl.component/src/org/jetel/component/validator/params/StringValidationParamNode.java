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
package org.jetel.component.validator.params;

import javax.xml.bind.annotation.XmlValue;

/**
 * Parameter of validation rule which can contain string and can be (de)serialized.
 * 
 * @author drabekj (info@cloveretl.com) (c) Javlin, a.s. (www.cloveretl.com)
 * @created 28.11.2012
 */
final public class StringValidationParamNode extends ValidationParamNode {
	@XmlValue
	String value = new String();
	
	public StringValidationParamNode() {}
	
	public StringValidationParamNode(String value) {
		this.setValue(value);
	}
	public String getValue() {
		return value;
	}
	/**
	 * Sets new value, null is not allowed.
	 * Triggers change handler.
	 * 
	 * @param other Not null new value
	 */
	public void setValue(String other) {
		if(other != null) {
			value = other;
			if(getChangeHandler() != null) {
				getChangeHandler().changed(other);
			}
		}
	}
	
	@Override
	public String toString() {
		return value;
	}

}