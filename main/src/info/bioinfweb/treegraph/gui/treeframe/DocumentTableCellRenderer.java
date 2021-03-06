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
package info.bioinfweb.treegraph.gui.treeframe;


import java.awt.Color;
import java.awt.Component;
import java.awt.SystemColor;
import java.util.Iterator;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import info.bioinfweb.commons.graphics.GraphicsUtils;
import info.bioinfweb.treegraph.document.PaintableElement;



/**
 * Decorates any {@link TableCellRenderer} to highlight certain cells that correspond to the tree selection.
 * <p>
 * Note that simply inheriting from {@link DefaultTableCellRenderer} instead (as proposed by many articles) is not sufficient since, 
 * e.g., the boolean renderer, differs from the double renderer and will not work correctly when inherited from {@link DefaultTableCellRenderer}. 
 * 
 * @author Ben St&ouml;ver
 * @since 2.16.0
 */
public class DocumentTableCellRenderer implements TableCellRenderer {
	private TableCellRenderer underlyingRenderer;
	private DocumentTableModel tableModel;
	private TreeSelection treeSelection;
	private ElementHighlighting treeHighlighting;
	
	
	public DocumentTableCellRenderer(TableCellRenderer underlyingRenderer, DocumentTableModel tableModel, TreeSelection treeSelection, 
			ElementHighlighting treeHighlighting) {
		
		super();
		this.underlyingRenderer = underlyingRenderer;
		this.tableModel = tableModel;
		this.treeSelection = treeSelection;
		this.treeHighlighting = treeHighlighting;
	}


	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		Component result = underlyingRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		PaintableElement element = tableModel.getTreeElement(row, column);
		if (treeSelection.contains(element)) {
			result.setBackground(SystemColor.textHighlight);
			result.setForeground(SystemColor.textHighlightText);
		}
		else {
			boolean highlighted = false;
			Iterator<String> iterator = treeHighlighting.keyIterator();
			while (!highlighted && iterator.hasNext()) {
				HighlightedGroup group = treeHighlighting.get(iterator.next());
				if (group.contains(element)) {
					Color bgColor = group.suitableColor(tableModel.getDocument().getTree().getFormats().getBackgroundColor());
					result.setBackground(bgColor);
					result.setForeground(GraphicsUtils.getContrastColor(SystemColor.textText, bgColor, TreeViewPanel.MAX_SELECTION_COLOR_DIF));
					highlighted = true;
				}
			}
			
			if (!highlighted) {
				result.setBackground(SystemColor.text);
				result.setForeground(SystemColor.textText);
			}
		}
		
		return result;
	}
}
