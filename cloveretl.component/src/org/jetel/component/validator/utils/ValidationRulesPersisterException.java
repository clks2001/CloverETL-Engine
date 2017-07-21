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
package org.jetel.component.validator.utils;

/**
 * Simple exception to carry error during serialization/deserialization
 * 
 * @author drabekj (info@cloveretl.com) (c) Javlin, a.s. (www.cloveretl.com)
 * @created 17.12.2012
 * @see ValidationRulesPersister
 */
public class ValidationRulesPersisterException extends IllegalArgumentException {

	private static final long serialVersionUID = -6225907665618516560L;
	
	public ValidationRulesPersisterException() {
		super();
	}
	
	public ValidationRulesPersisterException(String s) {
		super(s);
	}
	public ValidationRulesPersisterException(String message, Throwable cause) {
		super(message,cause);
	}
	public ValidationRulesPersisterException(Throwable cause) {
		super(cause);
	}

}