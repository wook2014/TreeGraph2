/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011, 2013-2016  Ben Stöver, Sarah Wiechers, Kai Müller
 * <http://treegraph.bioinfweb.info/>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.treegraph.document;


import info.bioinfweb.treegraph.document.position.PositionData;
import info.bioinfweb.treegraph.graphics.positionpaint.PositionPaintType;

import java.util.*;



public abstract class AbstractPaintableElement implements Cloneable, PaintableElement {
	protected EnumMap<PositionPaintType, PositionData> positions = 
		  new EnumMap<PositionPaintType, PositionData>(PositionPaintType.class);

  
	/* (non-Javadoc)
	 * @see info.webinsel.treegraph.document.PaintableElement#getPosition(int)
	 */
	public PositionData getPosition(PositionPaintType type) {
		PositionData result = positions.get(type);
		if (result == null) {
			result = new PositionData();
			positions.put(type, result);
		}
		
		return result;
	}
	
	
	/**
	 * Implementing classes should not copy the <code>EnumMap positions</code> because 
	 * the copy of this element will be located at another position. Therefor the copy will 
	 * should an empty <code>EnumMap</code>.
	 */
	@Override
	public abstract AbstractPaintableElement clone();
}