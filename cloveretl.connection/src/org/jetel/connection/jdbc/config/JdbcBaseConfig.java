/*
 *    jETeL/Clover - Java based ETL application framework.
 *    Copyright (C) 2002-2008  David Pavlis <david.pavlis@centrum.cz> and others.
 *    
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *    
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU    
 *    Lesser General Public License for more details.
 *    
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * Created on 5.3.2008 by dadik
 *
 */

package org.jetel.connection.jdbc.config;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Properties;

import org.jetel.metadata.DataFieldMetadata;
import org.jetel.util.string.StringUtils;

public class JdbcBaseConfig {
	
	public enum OperationType {
		READ,
		WRITE,
		CALL,
		TRANSACTION,
		UNKNOWN
	}

	protected static JdbcBaseConfig instance=new JdbcBaseConfig();
	
	protected JdbcBaseConfig(){
	}

	public static JdbcBaseConfig getInstance(){
		return instance;
	}
	
	
	public String getTargetDBName(){
		return "universal";
	}
	
	public int getTargetDBMajorVersion(){
		return -1;
	}
	
	public Properties getTargetDBDefaultProperties(){
		return new Properties();
	}
	
	public void optimizeConnection(Connection conn, OperationType operType) {
		switch (operType) {
		case READ:
			try {
				conn.setAutoCommit(false);
				conn.setHoldability(ResultSet.CLOSE_CURSORS_AT_COMMIT);
				conn.setReadOnly(true);
				conn.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
			} catch (SQLException ex) {
				// TODO: for now, do nothing
			}
			break;
		case WRITE:
		case CALL:
			try {
				conn.setAutoCommit(false);
				conn.setHoldability(ResultSet.CLOSE_CURSORS_AT_COMMIT);
				conn.setReadOnly(false);
				conn.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
			} catch (SQLException ex) {
				// TODO: for now, do nothing
			}
			break;

		case TRANSACTION:
			try {
				conn.setAutoCommit(true);
				conn.setHoldability(ResultSet.CLOSE_CURSORS_AT_COMMIT);
				conn.setReadOnly(false);
				conn.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
			} catch (SQLException ex) {
				// TODO: for now, do nothing
			}
			break;
		}
	}
	
	
	public void optimizeStatement(Statement stm, OperationType operType){
		switch (operType) {
			case READ:
				try{
					stm.setFetchDirection(ResultSet.FETCH_FORWARD);
				}catch(SQLException ex){
					//TODO: for now, do nothing;
				}
				break;
		}
	}
	
	public void optimizeResultSet(ResultSet res,OperationType operType){
		switch(operType){
		case READ:
			try{
				res.setFetchDirection(ResultSet.FETCH_FORWARD);
				//res.setFetchSize(-1); // some optimization/patch for MySQL
			}catch(SQLException ex){
				//TODO: for now, do nothing
			}
		}
	}

	public Statement createStatement(Connection con, OperationType operType) throws SQLException{
		switch (operType) {
			case READ:
					return con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY, ResultSet.CLOSE_CURSORS_AT_COMMIT);
			default:
				return con.createStatement();
		}
	}
	
	public PreparedStatement createPreparedStatemetn(Connection con, String sql,OperationType operType) throws SQLException{
		switch (operType) {
			case READ:
					return con.prepareStatement(sql,ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY, ResultSet.CLOSE_CURSORS_AT_COMMIT);
			default:
				return con.prepareStatement(sql);
		}
	}
	
	public CallableStatement createCallableStatement(Connection con, String sql, OperationType operType) throws SQLException{
			return con.prepareCall(sql);
	}
	
	
	public int jetelType2sql(DataFieldMetadata field){
		switch (field.getType()) {
		case DataFieldMetadata.INTEGER_FIELD:
			return Types.INTEGER;
		case DataFieldMetadata.NUMERIC_FIELD:
			return Types.NUMERIC;
		case DataFieldMetadata.STRING_FIELD:
			return Types.VARCHAR;
		case DataFieldMetadata.DATE_FIELD:
			boolean isDate = field.isDateFormat();
			boolean isTime = field.isTimeFormat();
			if (isDate && isTime || StringUtils.isEmpty(field.getFormatStr())) 
				return Types.TIMESTAMP;
			if (isDate)
				return Types.DATE;
			if (isTime)
				return Types.TIME;
			return Types.TIMESTAMP;
        case DataFieldMetadata.LONG_FIELD:
            return Types.BIGINT;
        case DataFieldMetadata.DECIMAL_FIELD:
            return Types.DECIMAL;
        case DataFieldMetadata.BYTE_FIELD:
        case DataFieldMetadata.BYTE_FIELD_COMPRESSED:
        	if (!StringUtils.isEmpty(field.getFormatStr())
					&& field.getFormatStr().equalsIgnoreCase(DataFieldMetadata.BLOB_FORMAT_STRING)) {
        		return Types.BLOB;
        	}
            return Types.BINARY;
        case DataFieldMetadata.BOOLEAN_FIELD:
        	return Types.BOOLEAN;
		default:
			throw new IllegalArgumentException("Can't handle Clover's data type :"+field.getTypeAsString());
		}
	}
	
	
	public char sqlType2jetel(int sqlType) {
		switch (sqlType) {
			case Types.INTEGER:
			case Types.SMALLINT:
			case Types.TINYINT:
			    return DataFieldMetadata.INTEGER_FIELD;
			//-------------------
			case Types.BIGINT:
			    return DataFieldMetadata.LONG_FIELD;
			//-------------------
			case Types.DECIMAL:
				return DataFieldMetadata.DECIMAL_FIELD;
			case Types.DOUBLE:
			case Types.FLOAT:
			case Types.NUMERIC:
			case Types.REAL:
				return DataFieldMetadata.NUMERIC_FIELD;
			//------------------
			case Types.CHAR:
			case Types.LONGVARCHAR:
			case Types.VARCHAR:
			case Types.OTHER:
				return DataFieldMetadata.STRING_FIELD;
			//------------------
			case Types.DATE:
			case Types.TIME:
			case Types.TIMESTAMP:
				return DataFieldMetadata.DATE_FIELD;
            //-----------------
            case Types.BINARY:
            case Types.VARBINARY:
            case Types.LONGVARBINARY:
            case Types.BLOB:
                return DataFieldMetadata.BYTE_FIELD;
			//-----------------
			case Types.BOOLEAN:
				return DataFieldMetadata.BOOLEAN_FIELD;
			// proximity assignment
			case Types.BIT:
				return DataFieldMetadata.STRING_FIELD;
			default:
				throw new IllegalArgumentException("Can't handle JDBC.Type :"+sqlType);
		}
	}

}
