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
package info.bioinfweb.treegraph.document.undo.edit;


import info.bioinfweb.commons.SystemUtils;
import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.TextElementData;
import info.bioinfweb.treegraph.document.TreeSerializer;
import info.bioinfweb.treegraph.document.io.xtg.XTGReader;
import info.bioinfweb.treegraph.document.io.xtg.XTGWriter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeNameAdapter;
import info.bioinfweb.treegraph.document.undo.ImportTextElementDataParameters;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.* ;

import static org.junit.Assert.* ;



/**
 * Tests {@link SortLeafsEdit}.
 * 
 * @author Ben St&ouml;ver
 * @since 2.2.0
 */
public class SortLeafsEditTest {
	private Document createDocument() {
		try {
			return new XTGReader().read(new File("data" + SystemUtils.FILE_SEPARATOR + "sortLeafs" + SystemUtils.FILE_SEPARATOR + 
					"SortLeafs.xtg"));
		}
		catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
			return null;  // unreachable code
		}
	}
	
	
	/**
	 * Optional method to be used during the debug phase.
	 * 
	 * @param document
	 */
	private void writeTree(Document document) {
  	try {
  		new XTGWriter().write(document, new File("data" + SystemUtils.FILE_SEPARATOR + "sortLeafs" + 
  				SystemUtils.FILE_SEPARATOR + "SortLeafsOut.xtg"));
  	}
  	catch (Exception e) {
  		e.printStackTrace();
  	}
	}
	
	
  @Test
  public void testDistinctOrdering() {
  	List<TextElementData> order = new ArrayList<TextElementData>();
  	order.add(new TextElementData("L"));
  	order.add(new TextElementData("J"));
  	order.add(new TextElementData("K"));
  	order.add(new TextElementData("M"));
  	order.add(new TextElementData("C"));
  	order.add(new TextElementData("D"));
  	order.add(new TextElementData("B"));
  	order.add(new TextElementData("A"));
  	order.add(new TextElementData("G"));
  	order.add(new TextElementData("H"));
  	order.add(new TextElementData("I"));
  	order.add(new TextElementData("E"));
  	order.add(new TextElementData("F"));
  	
  	Document document = createDocument();
  	SortLeafsEdit edit = new SortLeafsEdit(document, document.getTree().getPaintStart(), order, 
  			NodeNameAdapter.getSharedInstance(), new ImportTextElementDataParameters());
  	document.executeEdit(edit);
  	
  	List<Node> leafs = TreeSerializer.getElementsInSubtreeAsList(document.getTree().getPaintStart(), true, Node.class);
  	for (int i = 0; i < order.size(); i++) {
	    assertEquals(order.get(i).getText(), leafs.get(i).getData().getText());
    }
  }
	
	
  @Test
  public void testConflictingOrderingUnchanged() {
  	List<TextElementData> order = new ArrayList<TextElementData>();
  	order.add(new TextElementData("B"));
  	order.add(new TextElementData("A"));  // Switch between A and B is topologically impossible.
  	order.add(new TextElementData("C"));
  	order.add(new TextElementData("D"));
  	order.add(new TextElementData("E"));
  	order.add(new TextElementData("F"));
  	order.add(new TextElementData("G"));
  	order.add(new TextElementData("H"));
  	order.add(new TextElementData("I"));
  	order.add(new TextElementData("J"));
  	order.add(new TextElementData("K"));
  	order.add(new TextElementData("L"));
  	order.add(new TextElementData("M"));
  	
  	Document document = createDocument();
  	SortLeafsEdit edit = new SortLeafsEdit(document, document.getTree().getPaintStart(), order, 
  			NodeNameAdapter.getSharedInstance(), new ImportTextElementDataParameters());
  	document.executeEdit(edit);
  	
  	List<Node> leafs = TreeSerializer.getElementsInSubtreeAsList(document.getTree().getPaintStart(), true, Node.class);
    assertEquals("A", leafs.get(0).getData().getText());
    assertEquals("B", leafs.get(1).getData().getText());
  	for (int i = 2; i < order.size(); i++) {
	    assertEquals(order.get(i).getText(), leafs.get(i).getData().getText());
    }
  }
	
	
  @Test
  public void testConflictingOrderingChanged() {
  	List<TextElementData> order = new ArrayList<TextElementData>();
  	order.add(new TextElementData("B"));
  	order.add(new TextElementData("C"));
  	order.add(new TextElementData("A"));  // Switch between A and B + C is topologically impossible.
  	order.add(new TextElementData("D"));
  	order.add(new TextElementData("E"));
  	order.add(new TextElementData("F"));
  	order.add(new TextElementData("G"));
  	order.add(new TextElementData("H"));
  	order.add(new TextElementData("I"));
  	order.add(new TextElementData("J"));
  	order.add(new TextElementData("K"));
  	order.add(new TextElementData("L"));
  	order.add(new TextElementData("M"));
  	
  	Document document = createDocument();
  	SortLeafsEdit edit = new SortLeafsEdit(document, document.getTree().getPaintStart(), order, 
  			NodeNameAdapter.getSharedInstance(), new ImportTextElementDataParameters());
  	document.executeEdit(edit);
  	
  	List<Node> leafs = TreeSerializer.getElementsInSubtreeAsList(document.getTree().getPaintStart(), true, Node.class);
    assertEquals("B", leafs.get(0).getData().getText());
    assertEquals("C", leafs.get(1).getData().getText());
    assertEquals("D", leafs.get(2).getData().getText());
    assertEquals("A", leafs.get(3).getData().getText());
  	for (int i = 4; i < order.size(); i++) {
	    assertEquals(order.get(i).getText(), leafs.get(i).getData().getText());
    }
  }
}
