/*
 *  jETeL/Clover - Java based ETL application framework.
 *  Copyright (C) 2002  David Pavlis
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.jetel.graph;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import org.jetel.exception.ComponentNotReadyException;

import java.util.logging.Logger;
/**
 * A class that represents processing Phase of Transformation Graph
 *
 * @author      D.Pavlis
 * @since       July 23, 2003
 * @revision    $Revision$
 * @see         OtherClasses
 */

public class Phase implements Comparable {

	// Attributes

	// Associations
	/**
	 * @since    April 2, 2002
	 */
	private List nodesInPhase;
	private List edgesInPhase;

	// specifies the order of this phase within graph
	private int phaseNum;

	private int phaseExecTime;
	private int phaseMemUtilization;
	//private int result;

	protected TransformationGraph graph;

	static Logger logger = Logger.getLogger("org.jetel.phase");

	/**  Description of the Field */
	public final static int RESULT_RUNNING = 0;
	/**  Description of the Field */
	public final static int RESULT_OK = 1;
	/**  Description of the Field */
	public final static int RESULT_ERROR = 2;

	// Operations

	/**
	 *Constructor for the TransformationGraph object
	 *
	 * @param  phaseNum  Description of the Parameter
	 * @since            April 2, 2002
	 */
	public Phase(int phaseNum) {
		this.phaseNum = phaseNum;
		nodesInPhase = new LinkedList();
		edgesInPhase = new LinkedList();
		//result = RESULT_RUNNING;
	}


	/**
	 *  Sets the Graph attribute of the Phase object
	 *
	 * @param  graph  The new Graph value
	 * @since         April 5, 2002
	 */
	public void setGraph(TransformationGraph graph) {
		this.graph = graph;
	}


	/**
	 *  Gets the phaseNum attribute of the Phase object
	 *
	 * @return    The phaseNum value
	 */
	public int getPhaseNum() {
		return phaseNum;
	}


	/**
	 * An operation that starts execution of graph
	 *
	 * @param  out  OutputStream - if defined, info messages are printed there
	 * @return      True if all nodes successfully started, otherwise False
	 * @since       April 2, 2002
	 */

	/**
	 *  Description of the Method
	 *
	 * @param  out  OutputStream - if defined, info messages are printed thereDescription of Parameter
	 * @return      returns TRUE if succeeded or FALSE if some Node or Edge failed initialization
	 * @since       April 10, 2002
	 */
	public boolean init(OutputStream out) {
		PrintStream log = null;
		Node node;
		Edge edge;
		Iterator nodeIterator = nodesInPhase.iterator();
		Iterator edgeIterator = edgesInPhase.iterator();
		phaseExecTime = phaseMemUtilization = 0;

		// if the output stream is specified, create logging possibility information
		if (out != null) {
			log = new PrintStream(out);
		}
		if (log != null) {
			log.print("[Clover] Initializing phase: ");
			log.println(phaseNum);
		}

		// iterate through all edges and initialize them
		while (edgeIterator.hasNext()) {
			try {
				edge = (Edge) edgeIterator.next();
				edge.init();
			} catch (IOException ex) {
				logger.severe(ex.getMessage());
				return false;
			}
		}
		// if logger exists, print some out information
		if (log != null) {
			log.println(" all edges initialized successfully... ");
		}

		// iterate through all nodes and initialize them
		if (log != null) {
			log.println(" initializing nodes: ");
		}
		while (nodeIterator.hasNext()) {
			try {
				node = (Node) nodeIterator.next();
				node.init();
			} catch (ComponentNotReadyException ex) {
				logger.severe(ex.getMessage());
				return false;
			}
			// if logger exists, print some out information
			if (log != null) {
				log.print("\t");
				log.println(node.getID());
			}
		}
		if (log != null) {
			log.print("[Clover] phase: ");
			log.print(phaseNum);
			log.println(" initialized successfully.");
		}
		return true;
		// initialized OK
	}


	/**
	 *  Description of the Method
	 *
	 * @return    Description of the Return Value
	 */
	public boolean check() {
		return true;
	}


	/**
	 * An operation that registers Node within current Phase
	 *
	 * @param  node  The feature to be added to the Node attribute
	 * @since        April 2, 2002
	 */
	public void assignNode(Node node) {
		nodesInPhase.add(node);

	}


	/**
	 *  Adds a feature to the Edge attribute of the Phase object
	 *
	 * @param  edge  The feature to be added to the Edge attribute
	 */
	public void assignEdge(Edge edge) {
		edgesInPhase.add(edge);
	}


	/**
	 *  Adds node to the graph (through this Phase)
	 *  Auxiliary method.
	 *
	 * @param  node  The feature to be added to the Node attribute
	 */
	public void addNode(Node node) {
		graph.addNode(node,phaseNum);
	}


	/**
	 *  Adds edge to the graph (through this Phase)
	 *  Auxiliary method.
	 *
	 * @param  edge  The feature to be added to the Edge attribute
	 */
	public void addEdge(Edge edge) {
		graph.addEdge(edge);
	}


	/**
	 *  Removes all Nodes from Phase
	 *
	 * @since    April 2, 2002
	 */
	public void destroy() {
		nodesInPhase.clear();
		edgesInPhase.clear();
	}


	/**
	 * Returns reference to Nodes contained in this phase.
	 *
	 * @return    The nodes value
	 * @since     July 29, 2002
	 */
	public List getNodes() {
		return nodesInPhase;
	}


	/**
	 *  Gets the edges attribute of the Phase object
	 *
	 * @return    The edges value
	 */
	public List getEdges() {
		return edgesInPhase;
	}


	/**
	 *  Description of the Method
	 *
	 * @param  to  Description of the Parameter
	 * @return     Description of the Return Value
	 */
	public int compareTo(Object to) {
		int toPhaseNum = ((Phase) to).getPhaseNum();
		if (phaseNum > toPhaseNum) {
			return 1;
		} else if (phaseNum < toPhaseNum) {
			return -1;
		} else {
			return 0;
		}
	}


	/**
	 *  Sets the phaseExecTime attribute of the Phase object
	 *
	 * @param  time  The new phaseExecTime value
	 */
	public void setPhaseExecTime(int time) {
		phaseExecTime = time;
	}


	/**
	 *  Sets the phaseMemUtilization attribute of the Phase object
	 *
	 * @param  mem  The new phaseMemUtilization value
	 */
	public void setPhaseMemUtilization(int mem) {
		phaseMemUtilization = mem;
	}


	/**
	 *  Gets the phaseExecTime attribute of the Phase object
	 *
	 * @return    The phaseExecTime value
	 */
	public int getPhaseExecTime() {
		return phaseExecTime;
	}


	/**
	 *  Gets the phaseMemUtilization attribute of the Phase object
	 *
	 * @return    The phaseMemUtilization value
	 */
	public int getPhaseMemUtilization() {
		return phaseMemUtilization;
	}
}
/*
 *  end class Phase
 */

