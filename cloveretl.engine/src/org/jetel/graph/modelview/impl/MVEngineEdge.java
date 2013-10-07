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
package org.jetel.graph.modelview.impl;

import org.jetel.graph.Edge;
import org.jetel.graph.modelview.MVComponent;
import org.jetel.graph.modelview.MVEdge;
import org.jetel.graph.modelview.MVMetadata;

/**
 * General model wrapper for engine edge ({@link Edge}).
 * 
 * @author Kokon (info@cloveretl.com)
 *         (c) Javlin, a.s. (www.cloveretl.com)
 *
 * @created 19. 9. 2013
 */
public class MVEngineEdge implements MVEdge {

	private Edge engineEdge;
	
	public MVEngineEdge(Edge engineEdge) {
		this.engineEdge = engineEdge;
	}
	
	@Override
	public MVComponent getReader() {
		return new MVEngineComponent(engineEdge.getReader());
	}

	@Override
	public MVComponent getWriter() {
		return new MVEngineComponent(engineEdge.getWriter());
	}

	@Override
	public boolean hasMetadata() {
		return engineEdge.getMetadata() != null;
	}

	@Override
	public MVMetadata getMetadata() {
		if (hasMetadata()) {
			return new MVEngineMetadata(engineEdge.getMetadata());
		} else {
			return null;
		}
	}

	@Override
	public void setMetadata(MVMetadata metadata) {
		if (metadata != null) {
			engineEdge.setMetadata(metadata.getMetadata());
		}
	}
	
	@Override
	public int getOutputPortIndex() {
		return engineEdge.getOutputPortNumber();
	}

	@Override
	public int getInputPortIndex() {
		return engineEdge.getInputPortNumber();
	}

	@Override
	public int hashCode() {
		return engineEdge.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof MVEngineEdge)) { 
			return false;
		}
		return engineEdge.equals(((MVEngineEdge) obj).engineEdge);
	}

}
