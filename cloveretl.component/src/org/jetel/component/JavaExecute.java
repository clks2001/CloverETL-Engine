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
package org.jetel.component;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetel.exception.ComponentNotReadyException;
import org.jetel.exception.ConfigurationProblem;
import org.jetel.exception.ConfigurationStatus;
import org.jetel.exception.XMLConfigurationException;
import org.jetel.graph.Node;
import org.jetel.graph.Result;
import org.jetel.graph.TransformationGraph;
import org.jetel.util.compile.DynamicJavaClass;
import org.jetel.util.file.FileUtils;
import org.jetel.util.property.ComponentXMLAttributes;
import org.jetel.util.property.RefResFlag;
import org.w3c.dom.Element;


/**
 * <h3>Java Execute Component</h3>
 * <!-- This component executes Java code. It dooesn't handle any input and output. This component is a wrapper 
 * around class implementing org.jetel.component.JavaRunner interface, which contains Java code to be executed 
 * (method run()). -->
 * <table border="1">
 * <th>Component:</th>
 * <tr><td><h4><i>Name:</i></h4></td><td>Java Execute</td></tr>
 * <tr><td><h4><i>Category:</i></h4></td><td></td></tr>
 * <tr><td><h4><i>Description:</i></h4></td><td>This component executes Java code. It dooesn't handle any input 
 * and output. This component is a wrapper around class implementing <i>org.jetel.component.JavaRunner</i> interface, 
 * which contains Java code to be executed, component calls method <i>run()</i> from the given class.</td></tr>
 * <tr><td><h4><i>Inputs:</i></h4></td><td>None</td></tr>
 * <tr><td><h4><i>Outputs:</i></h4></td><td>None</td></tr>
 * <tr><td><h4><i>Comment:</i></h4></td><td></td></tr>
 * </table>
 * <br>
 * <table border="1">
 * <th>XML attributes:</th>
 * <tr><td><b>type</b></td><td>"JAVA_EXECUTE"</td></tr>
 * <tr><td><b>id</b></td><td>component identification</td></tr> 
 * <tr><td><b>runnableClass</b></td><td>name of the class to be used</td></tr>
 * <tr><td><b>runnable</b></td><td>contains definition of class with java source</td></tr>
 * <tr><td><b>runnableURL</b></td><td>path to the file with code to be executed</td></tr>
 * <tr><td><b>charset </b><i>optional</i></td><td>encoding of extern source</td></tr>
 * <tr><td><i>..optional attribute..</i></td><td>any additional attribute is passed to the
 * class to be used in Properties object - as a key->value pair. There is no limit to how many optional
 * attributes can be used.</td>
 * </table>
 * 
 * @author jlehotsky <jakub.lehotsky@javlinconsulting.cz>
 * @created Dec 4, 2007
 *
 */


public class JavaExecute extends Node {

	public final static String COMPONENT_TYPE = "JAVA_EXECUTE";
	
	private static final String XML_RUNNABLECLASS_ATTRIBUTE = "runnableClass";
	private static final String XML_RUNNABLE_ATTRIBUTE = "runnable";
	private static final String XML_RUNNABLEURL_ATTRIBUTE = "runnableURL";
	private static final String XML_CHARSET_ATTRIBUTE = "charset";
	private static final String XML_PROPERTIES_ATTRIBUTE = "properties";

    private String runnable = null;
	private String runnableClass = null;
	private String runnableURL = null;
	private String charset = null;	
	
	private JavaRunnable codeToRun = null;

	private Properties runnableParameters = null;
	
	static Log logger = LogFactory.getLog(JavaExecute.class);

	/**
	 * Helper method, which generates instance of JavaRunnable class from String, CLASS file or URL with
	 * Java source. No matter, which of these forms is used, code must be instance of JavaRunnable class.
	 *  
     * @param runnable		String containing Java code to be executed
     * @param runnableClass	Name of CLASS with code (e.g. org.jetel.test.DemoRunnable)
     * @param runnableURL	URL of the source code with Java code to be executed
	 * @param charset		Character set for reading Java source from file	
	 * @param node			Reference to actual node, from where factory method is called  
	 * @param runnableParameters	Properties, which should be passed to a class
	 * @param classLoader	Reference to classLoader to be used
	 * @param doInit 		Set to true to call init of Runnable instnace created
	 * @return		Instance of JavaRunnable class
	 * @throws ComponentNotReadyException
	 */
	private JavaRunnable createRunnable(String runnable,
			String runnableClass, String runnableURL, String charset,
			Node node, Properties runnableParameters, ClassLoader classLoader, boolean doInit) throws ComponentNotReadyException {
		
		JavaRunnable codeToRun = null;
		
		if(runnable == null && runnableClass == null && runnableURL == null) {
            throw new ComponentNotReadyException("Runnable class is not defined.");
        }
		
        if (runnableClass != null) {
            //get runnable from link to the compiled class
            codeToRun = loadClass(runnableClass);
        } else if (runnable == null && runnableURL != null) {
        	runnable = FileUtils.getStringFromURL(node.getGraph().getRuntimeContext().getContextURL(), runnableURL, charset);
        }
        
        if (runnableClass == null) {
    		codeToRun = loadClassDynamic(runnable, classLoader);
        }
        
        codeToRun.setNode(node); 
        
        if (doInit && !codeToRun.init(runnableParameters)) {
            throw new ComponentNotReadyException("Error when initializing tranformation function !");        
        }
        
		return codeToRun;
	}
	    
    /**
     * Creates new instance of class already loaded in memory
     * 
     * @param logger		Reference to a logger to use
     * @param runnableClassName	Classname to load
     * @param libraryPaths	Classpath
     * @return
     * @throws ComponentNotReadyException
     */
    private JavaRunnable loadClass(String runnableClassName) throws ComponentNotReadyException {
    	return RecordTransformFactory.loadClassInstance(runnableClassName, JavaRunnable.class, this);
    }
    
    /* (non-Javadoc)
     * @see org.jetel.graph.Node#preExecute()
     */
    @Override
    public void preExecute() throws ComponentNotReadyException {
    	super.preExecute();
    	codeToRun.preExecute();
    }
    
    /* (non-Javadoc)
     * @see org.jetel.graph.GraphElement#postExecute(org.jetel.graph.TransactionMethod)
     */
    @Override
    public void postExecute() throws ComponentNotReadyException {
    	super.postExecute();
    	codeToRun.postExecute();
    }
    
    /**
     * Dynamically creates instance of a given class - compilation and class loading occurs (used
     * for reading code from String or from source .java file
     * 
     * @param logger	Logger to use
     * @param dynamicRunnableCode	Class with info about source code
     * @return	Instance of a class
     * @throws ComponentNotReadyException
     */
    private JavaRunnable loadClassDynamic(String runnable, ClassLoader classLoader) throws ComponentNotReadyException {
        return DynamicJavaClass.instantiate(runnable, JavaRunnable.class, this);
    }
	
    /**
     * Costruction of Java Execute component
     * 
     * @param id	Uniq identification of the component
     * @param runnable		String containing Java code to be executed
     * @param runnableClass	Name of CLASS with code (e.g. org.jetel.test.DemoRunnable)
     * @param runnableURL	URL of the source code with Java code to be executed
     */
	public JavaExecute(String id, String runnable, String runnableClass, String runnableURL, Properties internalProperties) {
		
		super(id);
		
		this.runnable = runnable;
		this.runnableClass = runnableClass;
		this.runnableURL = runnableURL;
		this.runnableParameters = internalProperties;
		
	}
	
	/**
	 * Costruction of Java Execute component, direct construction from instance of JavaRunnable class
	 * 
	 * @param id	Uniq identification of the component
	 * @param codeToRun	Class with code to execute
	 */
	public JavaExecute(String id, JavaRunnable codeToRun) {
		
		super (id);
		
		this.codeToRun = codeToRun;
		
	}
	
	@Override
	public Result execute() throws Exception {
		
		codeToRun.run();
		
		return runIt ? Result.FINISHED_OK : Result.ABORTED;
	}
	
	@Override
	public void init() throws ComponentNotReadyException {
		
		if(isInitialized()) return;
		super.init();

		/* Init Parameters */
		if (codeToRun != null) {
			codeToRun.init(runnableParameters);
		} else { 
		
		/* Create JavaRunnable instance to be executed by code */
		codeToRun = createRunnable(runnable, runnableClass, 
				runnableURL, charset, this,	runnableParameters, this.getClass().getClassLoader(), true);
		}
		
	}
	
	@Override
	public ConfigurationStatus checkConfig(ConfigurationStatus status) {		
		super.checkConfig(status);
		
		if (charset != null && !Charset.isSupported(charset)) {
        	status.add(new ConfigurationProblem(
            		"Charset "+charset+" not supported!", 
            		ConfigurationStatus.Severity.ERROR, this, ConfigurationStatus.Priority.NORMAL));
        }
		// Nothing to check here - trying to create runnable class is too strict in this phase 
		
        return status;
	}
	
	@Override
	public void toXML(Element xmlElement) {
		super.toXML(xmlElement);
		
		if (runnable!=null){
			xmlElement.setAttribute(XML_RUNNABLE_ATTRIBUTE,runnable);
		}
		
		if (runnableClass != null) {
			xmlElement.setAttribute(XML_RUNNABLECLASS_ATTRIBUTE, runnableClass);
		} 
		
		if (runnableURL != null) {
			xmlElement.setAttribute(XML_RUNNABLEURL_ATTRIBUTE, runnableURL);
		}
		
		if (charset != null){
			xmlElement.setAttribute(XML_CHARSET_ATTRIBUTE, charset);
		}
				
		Enumeration propertyAtts = runnableParameters.propertyNames();		
		StringBuffer result = new StringBuffer();
		while (propertyAtts.hasMoreElements()) {
			String attName = (String)propertyAtts.nextElement();
			result.append(attName + "=" + runnableParameters.getProperty(attName) + "\n");
			
		}
		xmlElement.setAttribute(XML_PROPERTIES_ATTRIBUTE, result.toString());
				
	}

	
	/**
	 * Creates new instance of this Component from XML definition.
	 * @param graph
	 * @param xmlElement
	 * @return
	 * @throws XMLConfigurationException
	 */
	public static Node fromXML(TransformationGraph graph, Element xmlElement) throws XMLConfigurationException {
		
		ComponentXMLAttributes xattribs = new ComponentXMLAttributes(xmlElement, graph);
		
		JavaExecute javaExecute;
		Properties internalProperties;
		
		try {
			try {
            	internalProperties = new Properties();
            	String stringProperties = xattribs.getString(XML_PROPERTIES_ATTRIBUTE,null);
            	if (stringProperties != null) {
            		internalProperties.load(new StringReader(stringProperties));
            	}
    		} catch (IOException e){
    			throw new RuntimeException("Unexpected IO exception in byte array reading.");
    		}
            javaExecute = new JavaExecute(
                    xattribs.getString(XML_ID_ATTRIBUTE),
                    xattribs.getStringEx(XML_RUNNABLE_ATTRIBUTE, null, RefResFlag.SPEC_CHARACTERS_OFF), 
                    xattribs.getString(XML_RUNNABLECLASS_ATTRIBUTE, null),
                    xattribs.getStringEx(XML_RUNNABLEURL_ATTRIBUTE,null, RefResFlag.SPEC_CHARACTERS_OFF),
                    internalProperties);

            if (xattribs.exists(XML_CHARSET_ATTRIBUTE)) {            	
            	javaExecute.setCharset(xattribs.getString(XML_CHARSET_ATTRIBUTE));
            }
		} catch (Exception ex) {
			throw new XMLConfigurationException(COMPONENT_TYPE + ":" + xattribs.getString(XML_ID_ATTRIBUTE, " unknown ID ") + ":" + ex.getMessage(), ex);
		}
		
		return javaExecute;
	}

	@Override
	public String getType() {
		return COMPONENT_TYPE;
	}
	
	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}
	
	// note: following two methods do nothing explicit as JavaExecute has got no internal state, that needs to
	//		 be reset or freed.
	
	@Override	
	public synchronized void reset() throws ComponentNotReadyException {
		super.reset();  
	}
	
	@Override
	public synchronized void free() {	
		super.free();
		
		if (codeToRun != null) {
			codeToRun.free();
		}
	}
}