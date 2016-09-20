/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2015-2016  Ben Stöver, Sarah Wiechers, Kai Müller
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


import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import info.bioinfweb.commons.swing.ProgressDialog;
import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.io.ancestralstate.BayesTraitsReader;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.undo.file.ancestralstate.AncestralStateImportParameters;
import info.bioinfweb.treegraph.document.undo.file.ancestralstate.ImportBayesTraitsDataEdit;
import info.bioinfweb.treegraph.gui.actions.DocumentAction;
import info.bioinfweb.treegraph.gui.dialogs.io.ancestralstate.AssignBayesTraitsImportColumnsDialog;
import info.bioinfweb.treegraph.gui.dialogs.io.ancestralstate.ImportBayesTraitsDataDialog;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeInternalFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeSelection;



/**
 * Imports a log file from BayesTraits. This can only work if it was created with a command file generated by
 * TreeGraph 2 from the same tree.
 * 
 * @author BenStoever
 * @since 2.0.46
 */
public class ImportBayesTraitsDataAction extends DocumentAction {
	private ImportBayesTraitsDataDialog importBayesTraitsDataDialog = null;	
	private AssignBayesTraitsImportColumnsDialog assignBayesTraitsImportColumnsDialog = null;

	public ImportBayesTraitsDataAction(MainFrame mainFrame) {
		super(mainFrame);
		putValue(Action.NAME, "Import BayesTraits data..."); 
	  putValue(Action.MNEMONIC_KEY, KeyEvent.VK_B);
	}
	
	
	private ImportBayesTraitsDataDialog getImportBayesTraitsDataDialog() {
		if (importBayesTraitsDataDialog == null) {
			importBayesTraitsDataDialog = new ImportBayesTraitsDataDialog(getMainFrame());
		}
		return importBayesTraitsDataDialog;
	}	
	
	
	private AssignBayesTraitsImportColumnsDialog getAssignBayesTraitsImportColumnsDialog() {
		if (assignBayesTraitsImportColumnsDialog == null) {
			assignBayesTraitsImportColumnsDialog = new AssignBayesTraitsImportColumnsDialog(getMainFrame());
		}
		return assignBayesTraitsImportColumnsDialog;
	}
	
	
	@Override
	protected void onActionPerformed(ActionEvent e, final TreeInternalFrame frame) {
		if (getImportBayesTraitsDataDialog().execute(frame.getDocument(), frame.getTreeViewPanel().getSelection(), 
				frame.getSelectedAdapter())) {
			
			AncestralStateImportParameters parameters = new AncestralStateImportParameters();
			getImportBayesTraitsDataDialog().assignParameters(parameters);
			
			try {
				BayesTraitsReader bayesTraitsReader = new BayesTraitsReader();
				parameters.setData(bayesTraitsReader.read(parameters.getTableFile().getAbsolutePath()));
				
				//TODO Check if any data was loaded from the file, before displaying the next dialog.
				
				if (getAssignBayesTraitsImportColumnsDialog().execute(parameters, parameters.getData().get("Root"), frame.getDocument().getTree())) {
					final ProgressDialog progressDialog = new ProgressDialog(getMainFrame(), "Importing data...", null, true, null);
					final ImportBayesTraitsDataEdit edit = new ImportBayesTraitsDataEdit(frame.getDocument(), parameters, progressDialog);
					new SwingWorker<Void, Void>() {
						@Override
						protected Void doInBackground() throws Exception {
							frame.getDocument().executeEdit(edit);
							return null;
						}
						
						@Override
						protected void done() {
						 progressDialog.dispose();
							if (edit.hasWarnings()) {
								JOptionPane.showMessageDialog(MainFrame.getInstance(), edit.getWarningText(), 
								    "Warning", JOptionPane.WARNING_MESSAGE);
							}
						}
					}.execute();  // Start processing in a separate thread.
					progressDialog.setVisible(true);  // Show modal dialog.
				}
			}
			catch (FileNotFoundException ex) {
				JOptionPane.showMessageDialog(MainFrame.getInstance(), "The file \"" + parameters.getTableFile().getAbsolutePath() + 
						"\" was not found.", "Error", JOptionPane.ERROR_MESSAGE);
			}
			catch (SecurityException ex) {
				JOptionPane.showMessageDialog(MainFrame.getInstance(), "The permission for writing to the file \"" + 
			      parameters.getTableFile().getAbsolutePath() + "\" was denied.", "Error", JOptionPane.ERROR_MESSAGE);
			}
			catch (IOException ex) {
				JOptionPane.showMessageDialog(MainFrame.getInstance(), "The error \"" + ex.getMessage() + 
						"\" occured when writing to the file \"" + parameters.getTableFile().getAbsolutePath() + "\".", 
						"Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	
	@Override
	public void setEnabled(Document document, TreeSelection selection, NodeBranchDataAdapter tableAdapter) {
		setEnabled((document != null) && !document.getTree().isEmpty());
	}
}
