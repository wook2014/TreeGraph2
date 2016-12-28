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
package info.bioinfweb.treegraph.gui.actions.file;


import info.bioinfweb.treegraph.document.Branch;
import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.gui.actions.DocumentAction;
import info.bioinfweb.treegraph.gui.dialogs.io.ExportBranchLabelCaptionDialog;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeInternalFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeSelection;

import java.awt.event.ActionEvent;

import javax.swing.Action;



public class ExportBranchLabelCaptionAction extends DocumentAction {
	private ExportBranchLabelCaptionDialog documentDialog;
	
	
	public ExportBranchLabelCaptionAction(MainFrame mainFrame) {
		super(mainFrame);
		putValue(Action.NAME, "Export label caption..."); 
		putValue(Action.SHORT_DESCRIPTION, "Export label caption");
	}


	private ExportBranchLabelCaptionDialog getDocumentDialog() {
		if (documentDialog == null) {
			documentDialog = new ExportBranchLabelCaptionDialog(getMainFrame());
		}
		return documentDialog;
	}
	
	
	@Override
	protected void onActionPerformed(ActionEvent e, TreeInternalFrame frame) {
		if (getDocumentDialog().execute(frame.getTreeViewPanel().getSelection().getFirstElementOfType(Branch.class))) {
			//TODO Implement
		}
	}


	@Override
	public void setEnabled(Document document, TreeSelection selection, NodeBranchDataAdapter tableAdapter) {
		setEnabled(oneElementSelected(selection) && selection.containsType(Branch.class) && 
				!selection.getFirstElementOfType(Branch.class).getLabels().isEmpty());
	}
}