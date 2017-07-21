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
package org.jetel.connection.jdbc.driver;

import java.lang.reflect.Method;
import java.net.URL;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetel.database.sql.JdbcDriver;
import org.jetel.database.sql.JdbcSpecific;
import org.jetel.exception.ComponentNotReadyException;
import org.jetel.graph.ContextProvider;
import org.jetel.util.classloader.GreedyURLClassLoader;
import org.jetel.util.classloader.MultiParentClassLoader;
import org.jetel.util.string.StringUtils;

/**
 * JDBC driver represents a JDBC driver. Can be create based on JDBC driver description or by all
 * necessary attributes. Method getDriver() is root knowledge of this class.
 * 
 * @author Martin Zatopek (martin.zatopek@javlinconsulting.cz)
 *         (c) Javlin Consulting (www.javlinconsulting.cz)
 *
 * @created 14.9.2007
 */
public class JdbcDriverImpl implements JdbcDriver {
    private static Log logger = LogFactory.getLog(JdbcDriver.class);
    
//    static {
//		DriverManager.setLogWriter(new PrintWriter(System.err));
//    }
    
    /**
     * Identifier of the JDBC driver.
     */
    private String database;
    
    /**
     * Name of this JDBC driver is used for communication with user - for example in the clover.GUI.
     */
    private String name;
    
    /**
     * Name of the class, which implements java.sql.Driver interface.
     */
    private String dbDriver;
    
    /**
     * Semicolon delimited list of jar/zip libraries of this JDBC driver.
     */
    private URL[] driverLibraries;

    /**
     * Jdbc specific associated by default with this jdbc driver.
     */
    private JdbcSpecific jdbcSpecific;

    /**
     * Custom connection properties.  
     */
    private Properties properties;

    /**
     * Class loader used to get instance of java.sql.Driver class.
     */
    private ClassLoader classLoader;
    private Driver driver;
        
    /**
     * Constructor.
     * @param jdbcDriverDescription
     * @throws ComponentNotReadyException
     */
    JdbcDriverImpl(JdbcDriverDescription jdbcDriverDescription) throws ComponentNotReadyException {
    	this(jdbcDriverDescription.getDatabase(),
    			jdbcDriverDescription.getName(),
    			jdbcDriverDescription.getDbDriver(),
    			jdbcDriverDescription.getDriverLibraryURLs(),
    			jdbcDriverDescription.getJdbcSpecific(),
    			jdbcDriverDescription.getProperties());
    }

    /**
     * Constructor.
     * @param database
     * @param name
     * @param dbDriver
     * @param driverLibraries
     * @param jdbcSpecific
     * @throws ComponentNotReadyException
     */
    public JdbcDriverImpl(String database, String name, String dbDriver, URL[] driverLibraries, JdbcSpecific jdbcSpecific, Properties properties) throws ComponentNotReadyException {
    	this.database = database;
    	this.name = name;
    	this.dbDriver = dbDriver;
    	this.driverLibraries = driverLibraries;
    	this.jdbcSpecific = jdbcSpecific;
    	this.properties = properties;
    	
    	prepareClassLoader();
    	prepareDriver();
    }

    /**
     * @return identifier of this jdbc driver. It is usually name of targeted database.
     */
    @Override
	public String getDatabase() {
        return database;
    }

    /**
     * @return human readable name of jdbc driver.
     */
    @Override
	public String getName() {
        return !StringUtils.isEmpty(name) ? name : database;
    }

    /**
     * @return jdbc specific associated by default with this jdbc driver
     */
    @Override
	public JdbcSpecific getJdbcSpecific() {
    	return jdbcSpecific;
    }
    
    /**
     * @return custom connection properties
     */
    @Override
	public Properties getProperties() {
    	Properties result = new Properties();
    	if (properties != null) {
    		result.putAll(properties);
    	}
    	return result;
    }
    
    /**
     * @return class loader, which is used to create java.sql.Driver instance, created in getDriver() method
     */
    @Override
	public ClassLoader getClassLoader() {
        return classLoader;
    }
    
    /**
     * That is major method of this class. Returns a java.sql.Driver represented by this entity.
     */
    @Override
	public Driver getDriver() {
    	return driver;
    }

    private void prepareClassLoader() throws ComponentNotReadyException {
    	
    	if (driverLibraries != null && driverLibraries.length > 0) {
    		/*
    		 *  paths to driver JARs specified, so create URL class loader and use specific class loader as its parent,
    		 *  that prevents package sealing violation and LinkageErrors if there were the same driver
    		 *  present more than once (this happens e.g. with Oracle JDBC driver in server environment)
    		 */
    		classLoader = ContextProvider.getAuthorityProxy().getClassLoader(driverLibraries, getJdbcSpecific().getDriverClassLoaderParent(), true);
    	} else {
    		/*
    		 * no class path so we suppose that the driver is either provided by runtime class loader or
    		 * is present on application classpath
    		 */
    		if (ContextProvider.getGraph() != null) {
    			ClassLoader runtimeClassLoader = ContextProvider.getGraph().getRuntimeContext().getClassLoader();
    			if (runtimeClassLoader != null) {
    				classLoader = new MultiParentClassLoader(runtimeClassLoader, Thread.currentThread().getContextClassLoader());
    			}
    		}
    		if (classLoader == null) {
    			classLoader = Thread.currentThread().getContextClassLoader();
    		}
    	}
    }
    
    private void prepareDriver() throws ComponentNotReadyException {
        try {
            driver = (Driver) Class.forName(dbDriver, true, getClassLoader()).newInstance();
        } catch (ClassNotFoundException ex1) {
            throw new ComponentNotReadyException("Cannot create JDBC driver '" + getName() + "'. Cannot find class.", ex1);
        } catch (Exception ex1) {
            throw new ComponentNotReadyException("Cannot create JDBC driver '" + getName() + "'.", ex1);
        }
        if (driver == null)
            throw new ComponentNotReadyException("Cannot create JDBC driver '" + getName() + "'. No driver found. " + dbDriver );
    }

    /**
     * Explicitly removes drivers loaded by classLoader created by this instance.
     * Prevents from "java.lang.OutOfMemoryError: PermGen space"
     * 
     * If the driver loads any native libraries,
     * the libraries are unloaded when the driver's classloader
     * is garbage collected. The classloader can only be GCed
     * when classes loaded by it are no longer referenced.
     * 
     * @see DriverUnregisterer
     */
	@Override
	public void free() {
		// process only classLoaders created by this instance
		if (this.classLoader == JdbcDriver.class.getClassLoader())
			return;
		
		// this may do nothing, because the "driver" instance
		// may not be registered with DriverManager
		if (driver.getClass().getClassLoader() == classLoader){
			try {
				DriverManager.deregisterDriver(driver);
			} catch (SQLException e1) {
				logger.error(e1);
			} catch (SecurityException e2) {
				// thrown by Sybase driver
				logger.warn("SecurityException while DriverManager.deregisterDriver()", e2);
			}
		}
		
		// DriverManager.getDrivers() only returns drivers loaded by the caller's classloader,
		// therefore we load the class DriverUnregisterer by this instance's classloader
		// and then use it to deregister the drivers from DriverManager.
		if (classLoader instanceof GreedyURLClassLoader) {
			GreedyURLClassLoader gcl = (GreedyURLClassLoader) classLoader;
			try {
	    		// try and obtain the URL of the JAR archive containing
	    		// DriverUnregisterer class
	    		// so that we can use it to deregister the driver
				URL url = DriverUnregisterer.class.getProtectionDomain().getCodeSource().getLocation();
				if (url != null) {
		        	gcl.addURL(url);
	        		ClassLoader originalCl = Thread.currentThread().getContextClassLoader();
		        	try {
		        		Thread.currentThread().setContextClassLoader(JdbcDriver.class.getClassLoader());
						Class<?> c = Class.forName("org.jetel.connection.jdbc.driver.DriverUnregisterer", true, gcl);
						Object du = c.newInstance();
						Method m = c.getMethod("unregisterDrivers", ClassLoader.class);
						m.invoke(du, classLoader);
		        	} finally {
		        		Thread.currentThread().setContextClassLoader(originalCl);
		        	}
				}
			} catch (Exception e) {
				if (logger.isWarnEnabled()) {
					logger.warn("Unable to create helper class for driver unregistering", e);
				}
			}

		}
		
		// additional cleanup operations may be necessary
		// before the garbage collection of the driver's classloader
		if (jdbcSpecific != null) {
			jdbcSpecific.unloadDriver(this);
		}
		
		// perform garbage collection as soon as possible
		System.gc();
	}
	
}