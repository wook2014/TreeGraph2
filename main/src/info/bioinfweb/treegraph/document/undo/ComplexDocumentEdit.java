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
package info.bioinfweb.treegraph.document.undo;


import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import info.bioinfweb.treegraph.document.Branch;
import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.TreePath;



/**
 * This class is the ancestor to all edits that are too complex to have an own <code>undo()</code>
 * method. Descendant classes do not specify a <code>redo()</code>- and an <code>undo()</code>-method 
 * but override the abstract method {@link #performRedo()}.<br>
 * This class makes a copy of the old tree in the document before calling <code>performRedo()</code>
 * so all classes implementing {@link #performRedo()} have to translate references to elements of
 * the old tree by using the {@link #findEquivilant(Node)} or {@link #findEquivalent(Branch)}-methods.
 * 
 * @author Ben St&ouml;ver
 */
public abstract class ComplexDocumentEdit extends DocumentEdit {
	private Node oldRoot = null;
	private Node newRoot = null;
	private boolean firstRedone = false;
	
	
	public ComplexDocumentEdit(Document document) {
		super(document);
		oldRoot = document.getTree().getPaintStart();
		newRoot = oldRoot.cloneWithSubtree(true);  // Unique names are copied as well.
	}
	
	
	/**
	 * Returns the node in the new tree (copy of the old) with the equivalent position
	 * to <code>old</code>.
	 * 
	 * @param old - the node in the old tree
	 * @return the node in the new tree
	 */
	protected Node findEquivilant(Node old) {
		return new TreePath(old).findNode(newRoot);
	}

	
	/**
	 * Returns the branch in the new tree (copy of the old) with the equivalent position
	 * to <code>old</code>.
	 * 
	 * @param old - the branch in the old tree
	 * @return the branch in the new tree
	 */
	protected Branch findEquivalent(Branch old) {
		return new TreePath(old.getTargetNode()).findNode(newRoot).getAfferentBranch();
	}

	
	protected abstract void performRedo();
	
	
	@Override
	public void redo() throws CannotRedoException {
		document.getTree().setPaintStart(newRoot);
  	if (!firstRedone) {
			performRedo();  //Kann nicht schon in Konstruktor aufgerufen werden.
			newRoot = document.getTree().getPaintStart();  // Falls Wurzel in perfom redo ver�ndert wurde.
			firstRedone = true;
		}
		super.redo();
	}


	@Override
	public void undo() throws CannotUndoException {
		document.getTree().setPaintStart(oldRoot);
		super.undo();
	}
}