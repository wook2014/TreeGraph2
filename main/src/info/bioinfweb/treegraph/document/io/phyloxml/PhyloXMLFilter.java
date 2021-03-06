/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011, 2013-2019  Ben Stöver, Sarah Wiechers, Kai Müller
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
package info.bioinfweb.treegraph.document.io.phyloxml;


import info.bioinfweb.treegraph.document.io.AbstractXMLFilter;
import info.bioinfweb.treegraph.document.io.ReadWriteFormat;
import info.bioinfweb.treegraph.document.io.TreeFilter;



/**
 * @author Ben St&ouml;ver
 * @since 2.0.35
 */
public class PhyloXMLFilter extends AbstractXMLFilter implements TreeFilter, PhyloXMLConstants {
	public static final String PHYLOXML_EXTENSION = ".phyloxml";
	public static final String PHYLO_XML_EXTENSION = ".phylo.xml";
	public static final String PXML_EXTENSION = ".pxml";
	
	
	@Override
	public ReadWriteFormat getFormat() {
		return ReadWriteFormat.PHYLO_XML;
	}


	public PhyloXMLFilter() {
		super(TAG_ROOT.getLocalPart());
	}


	public boolean validExtension(String name) {
		name = name.toLowerCase();
		return name.endsWith(PHYLOXML_EXTENSION) || name.endsWith(PHYLO_XML_EXTENSION) || name.endsWith(PXML_EXTENSION) || name.endsWith(XML_EXTENSION);
	}
	
	
	@Override
	public String getDescription() {
		return "PhyloXML (*.phyloxml; *.phylo.xml; *.pxml; *.xml)";
	}


	public String getDefaultExtension() {
		return PHYLOXML_EXTENSION;
	}
}