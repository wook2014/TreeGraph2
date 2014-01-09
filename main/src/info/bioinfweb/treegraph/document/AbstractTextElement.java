/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2014  Ben St�ver, Kai M�ller
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


import info.bioinfweb.treegraph.document.format.ConcreteTextFormats;



/**
 * This class can be used as the acestor of all classes that represent document elements 
 * which contain text or decimal values.
 * @see TextElementData 
 * @author Ben St&ouml;ver
 */
public abstract class AbstractTextElement extends ConcretePaintableElement implements TextElement {
	private TextElementData data = new TextElementData();
	
	
	/* (non-Javadoc)
	 * @see info.bioinfweb.treegraph.document.TextElement#getData()
	 */
	public TextElementData getData() {
	  return data;
  }
  

  /* (non-Javadoc)
	 * @see info.bioinfweb.treegraph.document.TextElement#getFormats()
	 */
  public abstract ConcreteTextFormats getFormats();

  
	@Override
	public String toString() {
		return this.getClass().getName() + "[data = " + getData() + "]";
	}
	
	
	/* (non-Javadoc)
	 * @see info.bioinfweb.treegraph.document.TextElement#assignTextElementData(info.bioinfweb.treegraph.document.TextElement)
	 */
	public void assignTextElementData(TextElement other) {
		getData().assign(other.getData());
	}


	@Override
	public abstract AbstractTextElement clone();
}