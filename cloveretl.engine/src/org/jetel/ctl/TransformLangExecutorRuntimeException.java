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
package org.jetel.ctl;

import org.jetel.ctl.ASTnode.CLVFFunctionCall;
import org.jetel.ctl.ASTnode.SimpleNode;

import edu.umd.cs.findbugs.annotations.SuppressWarnings;

/**
 * @author dpavlis
 * @since  9.9.2004
 *
 * Exception thrown by Interpreter at runtime when error preventing
 * other run occures.
 */
public class TransformLangExecutorRuntimeException extends RuntimeException {
	/**
     * 
     */
    private static final long serialVersionUID = 5326484475745284143L;
    SimpleNode nodeInError;
	Object[] arguments;
	int errorCode;
	
	public TransformLangExecutorRuntimeException(SimpleNode node,Object[] arguments,String message){
		this(node,arguments,message,null);
	}
    
    @SuppressWarnings(value="EI2")
    public TransformLangExecutorRuntimeException(SimpleNode node,Object[] arguments,String message,Throwable cause){
        super(message,cause);
        this.nodeInError=node;
        this.arguments=arguments;
    }
    
	public TransformLangExecutorRuntimeException(Object[] arguments,String message){
	    this(null,arguments,message,null);
	}
	
	public TransformLangExecutorRuntimeException(SimpleNode node,String message){
	    this(node,null,message,null);
	}
	
	public TransformLangExecutorRuntimeException(String message){
	    this(null,null,message,null);
	}
	
    public TransformLangExecutorRuntimeException(String message, Throwable cause){
        this(null,null,message,cause);
    }
    
    public TransformLangExecutorRuntimeException(SimpleNode node,String message, Throwable cause){
        this(node,null,message,cause);
    }
    
    public TransformLangExecutorRuntimeException(Object[] arguments,String message,Throwable cause){
        this(null,arguments,message,cause);
    }
    
	public SimpleNode getNode(){
		return nodeInError;
	}
	
    public void setNode(SimpleNode inError){
        nodeInError=inError;
    }
    
    @SuppressWarnings(value="EI")
	public Object[] getArguments(){
		return arguments;
	}
    
    public int getColumn() {
        if (nodeInError!=null)
            return nodeInError.getColumn();
        return -1;
    }
    
    public int getLine() {
        if (nodeInError!=null)
            return nodeInError.getLine();
        return -1;
    }
	
	@Override
	public String getMessage() {
		StringBuffer strBuf = new StringBuffer("Interpreter runtime exception");
        if (nodeInError != null) {
        	if (nodeInError instanceof CLVFFunctionCall) {
        		strBuf.append(" in function ").append(((CLVFFunctionCall) nodeInError).getName());
        	}
            strBuf.append(" on line ").append(nodeInError.getLine());
            strBuf.append(" column ").append(nodeInError.getColumn());
        }
        if (super.getMessage() != null) {
            strBuf.append(" - ");
        	strBuf.append(super.getMessage());
        }
        if (super.getCause() != null) {
        	if (getCause() instanceof NullPointerException) {
                strBuf.append(" - ");
        		strBuf.append("Unexpected null value.");
        	}
        	if (super.getCause().getMessage() != null) {
        		strBuf.append(" - '").append(super.getCause().getMessage()).append("'");
        	}
        }
		if (arguments != null) {
			for(int i = 0; i < arguments.length; i++) {
				strBuf.append("\n");
				strBuf.append("arg[").append(i).append("] ");
				strBuf.append(arguments[i] != null ? arguments[i].getClass().getName() : "null").append(" \"");
				strBuf.append(arguments[i] != null ? arguments[i] : "!! NULL !!").append("\"");
			}
		}
		return strBuf.toString();
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
}