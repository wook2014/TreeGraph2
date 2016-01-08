/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2015-2016  Ben Stöver, Sarah Wiechers
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
package info.bioinfweb.treegraph.document.io;



public class SupportedFormatsFilter extends AbstractFilter {
  public static final String[] EXTENSIONS 
      = {".tre", ".tree", ".trees", ".nwk", ".con", ".nex", ".nexus", ".xtg", ".xml", ".phyloxml", ".phylo.xml", ".pxml"};  // ".tgf"


	public boolean validExtension(String name) {
		name = name.toLowerCase();
		for (int i = 0; i < EXTENSIONS.length; i++) {
			if (name.endsWith(EXTENSIONS[i])) {
				return true;
			}
		}
		return false;
	}
	

	@Override
	public String getDescription() {
		return "All Supported Formats";
	}


	public String getDefaultExtension() {
		return "";
	}
}