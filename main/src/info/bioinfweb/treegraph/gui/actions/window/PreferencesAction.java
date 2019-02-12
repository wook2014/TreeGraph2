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
package info.bioinfweb.treegraph.gui.actions.window;


import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;

import info.bioinfweb.commons.swing.ExtendedAbstractAction;
import info.bioinfweb.treegraph.gui.dialogs.PreferencesDialog;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;



public class PreferencesAction extends ExtendedAbstractAction {
	private PreferencesDialog dialog = null;
	
	
	public PreferencesAction() {
		super();
		putValue(Action.NAME, "Preferences..."); 
	  putValue(Action.MNEMONIC_KEY, KeyEvent.VK_P);
		putValue(Action.SHORT_DESCRIPTION, "Edit preferences of TreeGraph 2"); 
	}
	

	public PreferencesDialog getDialog() {
		if (dialog == null) {
			dialog = new PreferencesDialog();
		}
		return dialog;
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		getDialog().execute();
	}
}
