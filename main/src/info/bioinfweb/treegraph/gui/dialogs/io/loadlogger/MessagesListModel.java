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
package info.bioinfweb.treegraph.gui.dialogs.io.loadlogger;


import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.ListModel;

import info.bioinfweb.commons.log.ApplicationLoggerMessage;



/**
 * List model used by {@link LoadLoggerDialog}.
 * 
 * @since 2.0.42
 * @author Ben St&ouml;ver
 */
public class MessagesListModel extends AbstractListModel implements ListModel {
	private Vector<ApplicationLoggerMessage> list = new Vector<ApplicationLoggerMessage>();
	

	@Override
	public Object getElementAt(int index) {
		return list.get(index);
	}

	
	@Override
	public int getSize() {
		return list.size();
	}


	public void add(ApplicationLoggerMessage e) {
    if (list.add(e)) {
    	fireIntervalAdded(this, getSize() - 1, getSize() - 1);
    }
	}


	public void clear() {
		int size = list.size();
		if (size > 0) {  // fireIntervalRemoved(this, 0, size - 1) throws ArrayIndexOutOfBoundsException if this is not checked.
			list.clear();
			fireIntervalRemoved(this, 0, size - 1);
		}
	}
}