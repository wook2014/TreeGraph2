/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011, 2013-2017  Ben Stöver, Sarah Wiechers, Kai Müller
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
package info.bioinfweb.treegraph.gui.actions.select;


import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.NodeType;
import info.bioinfweb.treegraph.document.PaintableElement;
import info.bioinfweb.treegraph.document.Tree;
import info.bioinfweb.treegraph.document.TreeElement;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.tools.TreeSerializer;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeInternalFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeSelection;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.Action;



public class SelectLeavesInSubtreeAction extends AbstractSelectionAction {
	public SelectLeavesInSubtreeAction(MainFrame mainFrame) {
		super(mainFrame);
	  putValue(Action.NAME, "Select leaves in subtree(s)"); 
	  putValue(Action.MNEMONIC_KEY, KeyEvent.VK_B);
	  putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, 18);
	}


	@Override
	public void setEnabled(Document document, TreeSelection selection, NodeBranchDataAdapter tableAdapter) {
		setEnabled((selection != null) && selection.containsType(TreeElement.class));
	}

	
	@Override
	protected void performSelection(ActionEvent e, TreeInternalFrame frame,
			TreeSelection selection) {

		ArrayList<PaintableElement> selectionCopy = new ArrayList<PaintableElement>(selection.size());  // Copy selection to avoid a ConcurrentModificationException when adding new elements in the loop.
		selectionCopy.addAll(selection);
		Iterator<PaintableElement> iterator = selectionCopy.iterator();
		while (iterator.hasNext()) {
			PaintableElement element = iterator.next();
			Node root = Tree.getLinkedNode(element);
			if (root != null) {
				Node[] subelements = TreeSerializer.getElementsInSubtree(root, NodeType.LEAVES, Node.class);
				for (int j = 0; j < subelements.length; j++) {
					selection.add(subelements[j]);
					selection.add(subelements[j].getAfferentBranch());
				}
			}
		}
	}
}