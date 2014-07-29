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


import java.util.List;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import info.bioinfweb.treegraph.document.Branch;
import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Legend;
import info.bioinfweb.treegraph.document.Legends;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.undo.SaveLegendsEdit;
import info.bioinfweb.treegraph.document.undo.WarningMessageEdit;



/**
 * Adds all children of a node to its parent node and than removes that node from the document.
 * 
 * @author Ben St&ouml;ver
 */
public class CollapseNodeEdit extends SaveLegendsEdit implements WarningMessageEdit {
  private Node node = null;
  private int index = 0;

  
	public CollapseNodeEdit(Document document, Node node) {
		super(document);
		this.node = node;
		if (node.hasParent()) {
			index = node.getParent().getChildren().indexOf(node); 
		}
	}
	
	
	private void editLegends() {
		Legends legends = document.getTree().getLegends();
		Node newAnchor;
		if (node.getParent() != null) {
			newAnchor = node.getParent();
		}
		else {
			newAnchor = node.getChildren().get(0);  // There can only be one child node in this case.
		}

		for (int i = 0; i < legends.size(); i++) {
			Legend l = legends.get(i);
			Node secondAnhcor = l.getFormats().getAnchor(1);
			if (l.getFormats().getAnchor(0) == node) {
				l.getFormats().setAnchor(0, newAnchor);
				setLegendsReanchored(true);
			}
			else if (secondAnhcor == node) {
				l.getFormats().setAnchor(1, newAnchor);
				setLegendsReanchored(true);
			}
		}
	}


	private void editBranchLength(Branch childBranch, double factor) {
		Branch formerParentBranch = node.getAfferentBranch();
		if (formerParentBranch.hasLength() && childBranch.hasLength()) {
			childBranch.setLength(childBranch.getLength() + formerParentBranch.getLength() * factor);
		}
	}
	
	
	@Override
	public void redo() throws CannotRedoException {
		saveLegends();
		editLegends();
		
		Node parent = node.getParent();
		if (parent != null) {
			int insertIndex = parent.getChildren().indexOf(node); 
			for (int i = 0; i < node.getChildren().size(); i++) {
				Node child = node.getChildren().get(i);
				child.setParent(parent);
				editBranchLength(child.getAfferentBranch(), 1);  // +1 to add parent length
				parent.getChildren().add(insertIndex + i, child);  // Kinder bleiben auch mit Node verkn�pft.
			}
			parent.getChildren().remove(node);
		}
		else if (node.getChildren().size() == 1) {
			node.getChildren().get(0).setParent(null);
			Node child = node.getChildren().get(0);
			document.getTree().setPaintStart(child);
			editBranchLength(child.getAfferentBranch(), 1);  // +1 to add parent length
		}
		else {
			throw new IllegalArgumentException("The root node can only be collapsed if it contains " +
					"exactly one subnode.");
		}
		
  	super.redo();
	}


	@Override
	public void undo() throws CannotUndoException {
		if (node.hasParent()) {
			List<Node> parentChildren = null;
			parentChildren = node.getParent().getChildren();
			for (int i = 0; i < node.getChildren().size(); i++) {
				Node child = node.getChildren().get(i);
				child.setParent(node);
				if (node.hasParent()) {
					parentChildren.remove(node.getChildren().get(i));
				}
				editBranchLength(child.getAfferentBranch(), -1);  // -1 to subtract parent length
			}
			parentChildren.add(index, node);
		}
		else {
			Node child = node.getChildren().get(0); 
			child.setParent(node);
			document.getTree().setPaintStart(node);
			editBranchLength(child.getAfferentBranch(), -1);  // -1 to subtract parent length
		}
		
		restoreLegends();
		super.undo();
	}


	public String getPresentationName() {
		return "Collapse node";
	}
}