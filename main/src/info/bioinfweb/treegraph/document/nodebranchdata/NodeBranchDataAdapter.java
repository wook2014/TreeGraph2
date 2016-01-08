/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2015-2016  Ben Stöver, Sarah Wiechers
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
package info.bioinfweb.treegraph.document.nodebranchdata;


import info.bioinfweb.treegraph.document.ConcretePaintableElement;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.TextElementData;



/**
 * This interface allows to access all data attached to nodes and branches in a standardized way.
 * 
 * @author Ben St&ouml;ver
 */
public interface NodeBranchDataAdapter {
	public static final String NAME_PREFIX = "info.bioinfweb.treegraph.";
	
	
	public String getName();
	
	public boolean readOnly();
	
	public boolean decimalOnly();
	
	public boolean isNewColumn();
	
	public boolean isDecimal(Node node);
	
	public boolean isString(Node node);
	
	public boolean isEmpty(Node node);
	
	public String getText(Node node); 
	
	public void setText(Node node, String value);
	
	public double getDecimal(Node node); 
	
	public void setDecimal(Node node, double value);
	
	/**
	 * Implementing classes should return an instance of {@link TextElementData} here. This should not be
	 * the same instance as the underlying data in contrast to {@link TextElementDataAdapter#getData(Node)}.
	 * 
	 * @param node - the node that carries the data
	 */
	public TextElementData toTextElementData(Node node);
	
	public void setTextElementData(Node node, TextElementData data);
	
	public void delete(Node node);
	
  /**
	 * This method should return the tree element which contains the data which is edited
	 * with the implementation of the respective adapter.
	 * @param node
	 * @return
	 */
	public ConcretePaintableElement getDataElement(Node node);
	
	/**
	 * A description of the adapter that is readable by the user should be returned here.
	 * @return
	 */
	public String toString();
}