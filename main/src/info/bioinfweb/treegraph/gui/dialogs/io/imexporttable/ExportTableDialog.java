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
package info.bioinfweb.treegraph.gui.dialogs.io.imexporttable;


import info.bioinfweb.treegraph.document.NodeType;
import info.bioinfweb.treegraph.gui.CurrentDirectoryModel;
import info.bioinfweb.treegraph.gui.dialogs.EditDialog;
import info.bioinfweb.treegraph.gui.dialogs.nodebranchdatainput.NodeBranchDataInput;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;

import javax.swing.JPanel;
import javax.swing.BoxLayout;

import java.awt.GridBagLayout;

import javax.swing.JScrollPane;

import java.awt.GridBagConstraints;

import javax.swing.JButton;

import java.awt.Insets;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.Font;
import java.awt.Color;
import java.awt.Frame;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JTextField;
import javax.swing.JRadioButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;



/**
 * Dialog to export node/branch data columns to a table stored in a text file.
 * 
 * @author Ben St&ouml;ver
 */
public class ExportTableDialog extends EditDialog {
	public static final String EXPORT_FILE_EXT = "txt";
	
	private static final long serialVersionUID = 1L;

	
	private JPanel jContentPane = null;
	
	private JPanel nodeDataPanel = null;
	private NodeBranchDataInput nodeDataInput = null;
	private JScrollPane tableScrollPane = null;
	private JTable table = null;
	private JPanel nodeDataButtonsPanel = null;
	private JButton addButton = null;
	private JButton replaceButton = null;
	private JButton removeButton = null;
	private JButton clearButton = null;
	private JButton upButton = null;
	private JButton downButton = null;
	
	private JPanel filePanel = null;
	private JTextField fileTextField = null;
	private JButton fileButton = null;
	private JFileChooser fileChooser = null;
	
	private JPanel headingsPanel = null;
	private JCheckBox exportHeadingsCheckBox = null;
	
	private JPanel nodesPanel = null;
	private ButtonGroup nodesGroup = null;  //  @jve:decl-index=0:
	private JRadioButton internalsRadioButton = null;
	private JRadioButton leavesRadioButton = null;
  private JRadioButton bothRadioButton = null;
  private JCheckBox chckbxReplaceSpacesBy;
  private JButton addAllButton;

	
	/**
	 * @param owner
	 */
	public ExportTableDialog(Frame owner) {
		super(owner);
		setHelpCode(67);
		initialize();
		setLocationRelativeTo(owner);
	}

	
	@Override
	protected boolean onExecute() {
		getNodeDataInput().setAdapters(getDocument().getTree(), true, true, true, false, false, "");
		setButtonStatus();
		return true;
	}


	@Override
	protected boolean apply() {
		File file = new File(getFileTextField().getText());
		
		boolean write = true;
		if (file.exists()) {
			write = (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this, "The file \"" + 
		      getFileTextField().getText() + "\" already exists.\n Do you want to overwrite it?", "Warning", 
		      JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE));
		}
		
		if (write) {
			try {
				NodeType nodeType = NodeType.BOTH;
				if (getInternalsRadioButton().isSelected()) {
					nodeType = NodeType.INTERNAL_NODES;
				}
				else if (getLeavesRadioButton().isSelected()) {
					nodeType = NodeType.LEAVES;
				}
				boolean exportHeadings = getExportHeadingsCheckBox().isSelected();
				boolean replaceSpaces = getChckbxReplaceSpacesBy().isSelected();
				getTableModel().writeData(file, getDocument().getTree().getPaintStart(), nodeType, exportHeadings, replaceSpaces);
			}
			catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(this, "The path \"" + getFileTextField().getText() + 
						"\" is invalid.", "Error", JOptionPane.ERROR_MESSAGE);
				return false;
			}
			catch (SecurityException e) {
				JOptionPane.showMessageDialog(this, "The permission for writing to the file \"" + 
						getFileTextField().getText() + "\" was denied.", "Error", 
						JOptionPane.ERROR_MESSAGE);
				return false;
			}
			catch (IOException e) {
				JOptionPane.showMessageDialog(this, "The error \"" + e.getMessage() + 
						"\" occured when writing to the file \"" + getFileTextField().getText() + 
						"\".", "Error", JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}
		return write;
	}
	
	
	private void setButtonStatus() {
  	boolean enabled = getTableModel().size() > 0;
  	getReplaceButton().setEnabled(enabled);
  	getRemoveButton().setEnabled(enabled);
  	getClearButton().setEnabled(enabled);

  	enabled = getTableModel().size() >= 2;
  	getUpButton().setEnabled(enabled && (getTable().getSelectedRow() > 0));
  	getDownButton().setEnabled(enabled &&	
  			(getTable().getSelectedRow() < getTableModel().size() - 1));
	}
	
	
	private ExportNodeDataTableModel getTableModel() {
		return (ExportNodeDataTableModel)getTable().getModel();
	}


	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		setTitle("Export node/branch data");
		setContentPane(getJContentPane());
		pack();
	}


	private NodeBranchDataInput getNodeDataInput() {
		getNodeDataPanel();  // make sure instance exists
		return nodeDataInput;
	}
	
	
	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BoxLayout(getJContentPane(), BoxLayout.Y_AXIS));
			jContentPane.add(getNodeDataPanel(), null);
			jContentPane.add(getHeadingsPanel(), null);
			jContentPane.add(getNodesPanel(), null);
			jContentPane.add(getFilePanel(), null);
			jContentPane.add(getButtonsPanel(), null);
			getApplyButton().setVisible(false);
		}
		return jContentPane;
	}


	/**
	 * This method initializes nodeDataPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getNodeDataPanel() {
		if (nodeDataPanel == null) {
			GridBagConstraints tableGBC = new GridBagConstraints();
			tableGBC.fill = GridBagConstraints.BOTH;
			tableGBC.gridy = 1;
			tableGBC.weightx = 1.0;
			tableGBC.weighty = 1.0;
			tableGBC.gridheight = 2;
			tableGBC.gridx = 0;
			GridBagConstraints buttonsGBC = new GridBagConstraints();
			buttonsGBC.gridx = 1;
			buttonsGBC.gridheight = 2;
			buttonsGBC.anchor = GridBagConstraints.NORTH;
			buttonsGBC.fill = GridBagConstraints.HORIZONTAL;
			buttonsGBC.gridy = 0;
			nodeDataPanel = new JPanel();
			nodeDataPanel.setLayout(new GridBagLayout());
			nodeDataPanel.setBorder(BorderFactory.createTitledBorder(null, "Columns to export", 
					TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), 
					new Color(51, 51, 51)));
			nodeDataInput = new NodeBranchDataInput(nodeDataPanel, 0, 0);
			nodeDataPanel.add(getTableScrollPane(), tableGBC);
			nodeDataPanel.add(getNodeDataButtonsPanel(), buttonsGBC);
		}
		return nodeDataPanel;
	}


	/**
	 * This method initializes nodeDataButtonsPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getNodeDataButtonsPanel() {
		if (nodeDataButtonsPanel == null) {
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.gridx = 0;
			gridBagConstraints6.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints6.insets = new Insets(0, 4, 4, 4);
			gridBagConstraints6.gridy = 6;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.gridx = 0;
			gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints5.insets = new Insets(0, 4, 6, 4);
			gridBagConstraints5.gridy = 5;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 0;
			gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints4.insets = new Insets(0, 4, 16, 4);
			gridBagConstraints4.gridy = 4;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints3.insets = new Insets(0, 4, 6, 4);
			gridBagConstraints3.gridy = 3;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints2.insets = new Insets(0, 4, 16, 4);
			gridBagConstraints2.gridy = 1;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints1.insets = new Insets(0, 4, 6, 4);
			gridBagConstraints1.gridy = 0;
			nodeDataButtonsPanel = new JPanel();
			nodeDataButtonsPanel.setLayout(new GridBagLayout());
			nodeDataButtonsPanel.add(getAddButton(), gridBagConstraints1);
			nodeDataButtonsPanel.add(getReplaceButton(), gridBagConstraints2);
			GridBagConstraints gbc_btnAddAll = new GridBagConstraints();
			gbc_btnAddAll.fill = GridBagConstraints.HORIZONTAL;
			gbc_btnAddAll.insets = new Insets(0, 4, 16, 4);
			gbc_btnAddAll.gridx = 0;
			gbc_btnAddAll.gridy = 2;
			nodeDataButtonsPanel.add(getAddAllButton(), gbc_btnAddAll);
			nodeDataButtonsPanel.add(getRemoveButton(), gridBagConstraints3);
			nodeDataButtonsPanel.add(getUpButton(), gridBagConstraints5);
			nodeDataButtonsPanel.add(getClearButton(), gridBagConstraints4);
			nodeDataButtonsPanel.add(getDownButton(), gridBagConstraints6);
		}
		return nodeDataButtonsPanel;
	}


	/**
	 * This method initializes tableScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getTableScrollPane() {
		if (tableScrollPane == null) {
			tableScrollPane = new JScrollPane();
			tableScrollPane.setViewportView(getTable());
		}
		return tableScrollPane;
	}


	/**
	 * This method initializes table	
	 * 	
	 * @return javax.swing.JTable	
	 */
	private JTable getTable() {
		if (table == null) {
			table = new JTable(new ExportNodeDataTableModel());
			table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
					  public void valueChanged(ListSelectionEvent e) {
					  	setButtonStatus();
					  }
				  });
		}
		return table;
	}


	/**
	 * This method initializes addButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getAddButton() {
		if (addButton == null) {
			addButton = new JButton();
			addButton.setText("Add");
			addButton.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							getTableModel().add(getNodeDataInput().getSelectedAdapter());
							
							int sel = getTableModel().size() - 1;
							getTable().getSelectionModel().setSelectionInterval(sel, sel);
						}
					});
		}
		return addButton;
	}


	/**
	 * This method initializes replaceButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getReplaceButton() {
		if (replaceButton == null) {
			replaceButton = new JButton();
			replaceButton.setText("Replace");
			replaceButton.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							int replacedIndex = getTable().getSelectedRow();
							getTableModel().set(replacedIndex, getNodeDataInput().getSelectedAdapter());
							
							int[] rows = getTable().getSelectedRows();
							for (int i = 0; i < rows.length; i++) {
								if (rows[i] != replacedIndex) {
									getTableModel().remove(rows[i]);
								}
							}
						}
					});
		}
		return replaceButton;
	}


	private JButton getAddAllButton() {
		if (addAllButton == null) {
			addAllButton = new JButton("Add all");
			addAllButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					//getTableModel().clear();  // Avoid duplicate entries.
					int start = getTableModel().size() - 1;
					getTableModel().addAll(getNodeDataInput().getModel().getAdapters());
					getTable().getSelectionModel().setSelectionInterval(start, getTableModel().size() - 1);
				}
			});
		}
		return addAllButton;
	}

	
	/**
	 * This method initializes removeButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getRemoveButton() {
		if (removeButton == null) {
			removeButton = new JButton();
			removeButton.setText("Remove");
			removeButton.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							int[] rows = getTable().getSelectedRows();
							for (int i = 0; i < rows.length; i++) {
								getTableModel().remove(rows[i]);
							}
						}
					});
		}
		return removeButton;
	}


	/**
	 * This method initializes clearButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getClearButton() {
		if (clearButton == null) {
			clearButton = new JButton();
			clearButton.setText("Clear");
			clearButton.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							getTableModel().clear();
						}
					});
		}
		return clearButton;
	}


	/**
	 * This method initializes upButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getUpButton() {
		if (upButton == null) {
			upButton = new JButton();
			upButton.setText("Move up");
			upButton.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							int[] rows = getTable().getSelectedRows();
							for (int i = 0; i < rows.length; i++) {
								getTableModel().moveUp(rows[i]);
							}
							
							getTable().getSelectionModel().clearSelection();
							for (int i = 0; i < rows.length; i++) {
								getTable().getSelectionModel().addSelectionInterval(rows[i] - 1, rows[i] - 1);
							}
						}
					});
		}
		return upButton;
	}


	/**
	 * This method initializes downButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getDownButton() {
		if (downButton == null) {
			downButton = new JButton();
			downButton.setText("Move down");
			downButton.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							int[] rows = getTable().getSelectedRows();
							for (int i = rows.length - 1; i >= 0; i--) {
								getTableModel().moveDown(rows[i]);
							}
							
							getTable().getSelectionModel().clearSelection();
							for (int i = 0; i < rows.length; i++) {
								getTable().getSelectionModel().addSelectionInterval(rows[i] + 1, rows[i] + 1);
							}

							
//							int selRow = getTable().getSelectedRow();
//							getTableModel().moveDown(getTable().getSelectedRow());
//							getTable().getSelectionModel().setSelectionInterval(
//									selRow + 1, selRow + 1);
						}
					});
		}
		return downButton;
	}


	/**
	 * This method initializes filePanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getFilePanel() {
		if (filePanel == null) {
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.gridx = 1;
			gridBagConstraints8.insets = new Insets(0, 4, 0, 4);
			gridBagConstraints8.gridy = 0;
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints7.gridy = 0;
			gridBagConstraints7.weightx = 1.0;
			gridBagConstraints7.insets = new Insets(0, 4, 0, 4);
			gridBagConstraints7.gridx = 0;
			filePanel = new JPanel();
			filePanel.setLayout(new GridBagLayout());
			filePanel.setBorder(BorderFactory.createTitledBorder(null, "Destination file", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			filePanel.add(getFileTextField(), gridBagConstraints7);
			filePanel.add(getFileButton(), gridBagConstraints8);
		}
		return filePanel;
	}


	/**
	 * This method initializes fileTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getFileTextField() {
		if (fileTextField == null) {
			fileTextField = new JTextField();
		}
		return fileTextField;
	}


	/**
	 * This method initializes fileButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getFileButton() {
		if (fileButton == null) {
			fileButton = new JButton();
			fileButton.setText("...");
			fileButton.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							getFileChooser().setSelectedFile(new File(getFileTextField().getText()));
							if (JFileChooser.APPROVE_OPTION == getFileChooser().showSaveDialog(MainFrame.getInstance())) {
								String path = getFileChooser().getSelectedFile().getAbsolutePath();
								if (!path.endsWith("." + EXPORT_FILE_EXT)) {
									path += "." + EXPORT_FILE_EXT;
								}
								getFileTextField().setText(path);
							}
						}
					});
		}
		return fileButton;
	}


	private JFileChooser getFileChooser() {
		if (fileChooser == null) {
			fileChooser = new JFileChooser();
			fileChooser.setFileFilter(new FileNameExtensionFilter(
	        "Text files", EXPORT_FILE_EXT));
			CurrentDirectoryModel.getInstance().addFileChooser(fileChooser);  //TODO Necessary because this dialog does not inherit from FileDialog
		}
		return fileChooser;
	}


	private JPanel getHeadingsPanel() {
		if (headingsPanel == null) {
			headingsPanel = new JPanel();
			headingsPanel.setLayout(new GridBagLayout());
			headingsPanel.setBorder(BorderFactory.createTitledBorder(null, "Table settings", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, 
					new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			
			GridBagConstraints gbc_exportHeadingsCheckBox = new GridBagConstraints();
			gbc_exportHeadingsCheckBox.insets = new Insets(0, 0, 0, 5);
			gbc_exportHeadingsCheckBox.weightx = 1.0;
			gbc_exportHeadingsCheckBox.anchor = GridBagConstraints.WEST;
			gbc_exportHeadingsCheckBox.gridx = 0;
			gbc_exportHeadingsCheckBox.gridy = 0;
			headingsPanel.add(getExportHeadingsCheckBox(), gbc_exportHeadingsCheckBox);
			
			GridBagConstraints gbc_chckbxReplaceSpacesBy = new GridBagConstraints();
			gbc_chckbxReplaceSpacesBy.insets = new Insets(0, 0, 0, 5);
			gbc_chckbxReplaceSpacesBy.weightx = 1.0;
			gbc_chckbxReplaceSpacesBy.anchor = GridBagConstraints.WEST;
			gbc_chckbxReplaceSpacesBy.gridx = 1;
			gbc_chckbxReplaceSpacesBy.gridy = 0;
			headingsPanel.add(getChckbxReplaceSpacesBy(), gbc_chckbxReplaceSpacesBy);
		}
		return headingsPanel;
	}


	private JCheckBox getExportHeadingsCheckBox() {
		if (exportHeadingsCheckBox == null) {
			exportHeadingsCheckBox = new JCheckBox();
			exportHeadingsCheckBox.setText("Export column headings");
			exportHeadingsCheckBox.setSelected(true);
		}
		return exportHeadingsCheckBox;
	}
	
	
	private JCheckBox getChckbxReplaceSpacesBy() {
		if (chckbxReplaceSpacesBy == null) {
			chckbxReplaceSpacesBy = new JCheckBox("Replace spaces in node names with underscores");
		}
		return chckbxReplaceSpacesBy;
	}


	/**
	 * This method initializes NodesPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getNodesPanel() {
		if (nodesPanel == null) {
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 2;
			gridBagConstraints11.weightx = 1.0;
			gridBagConstraints11.anchor = GridBagConstraints.WEST;
			gridBagConstraints11.gridy = 0;
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.gridx = 1;
			gridBagConstraints10.weightx = 1.0;
			gridBagConstraints10.anchor = GridBagConstraints.WEST;
			gridBagConstraints10.gridy = 0;
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.gridx = 0;
			gridBagConstraints9.anchor = GridBagConstraints.WEST;
			gridBagConstraints9.fill = GridBagConstraints.NONE;
			gridBagConstraints9.weightx = 1.0;
			gridBagConstraints9.gridy = 0;
			nodesPanel = new JPanel();
			nodesPanel.setLayout(new GridBagLayout());
			nodesPanel.setBorder(BorderFactory.createTitledBorder(null, "Nodes to export", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			getNodesGroup();
			nodesPanel.add(getInternalsRadioButton(), gridBagConstraints9);
			nodesPanel.add(getLeavesRadioButton(), gridBagConstraints10);
			nodesPanel.add(getBothRadioButton(), gridBagConstraints11);
		}
		return nodesPanel;
	}


	private ButtonGroup getNodesGroup() {
		if (nodesGroup == null) {
			nodesGroup = new ButtonGroup();
			nodesGroup.add(getInternalsRadioButton());
			nodesGroup.add(getLeavesRadioButton());
			nodesGroup.add(getBothRadioButton());
		}
		return nodesGroup;
	}


	/**
	 * This method initializes InternalsRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getInternalsRadioButton() {
		if (internalsRadioButton == null) {
			internalsRadioButton = new JRadioButton();
			internalsRadioButton.setText("Internal nodes");
		}
		return internalsRadioButton;
	}


	private JRadioButton getLeavesRadioButton() {
		if (leavesRadioButton == null) {
			leavesRadioButton = new JRadioButton();
			leavesRadioButton.setText("Terminal nodes");
		}
		return leavesRadioButton;
	}


	/**
	 * This method initializes bothRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getBothRadioButton() {
		if (bothRadioButton == null) {
			bothRadioButton = new JRadioButton();
			bothRadioButton.setText("All");
			bothRadioButton.setSelected(true);
		}
		return bothRadioButton;
	}
}