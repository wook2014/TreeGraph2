/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011  Ben St�ver, Kai M�ller
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
package info.bioinfweb.treegraph.document.format.operate;


import info.bioinfweb.treegraph.document.format.ElementFormats;
import info.bioinfweb.treegraph.document.format.ScaleBarFormats;



/**
 * There only one operator for all scale bar formats because there can only be one 
 * scale bar per document.
 * @author Ben St&ouml;ver
 */
public class ScaleBarFormatsOperator extends AbstractOperator {
	private ScaleBarFormats formats;
	
	
	public ScaleBarFormatsOperator(ScaleBarFormats formats) {
		super();
		this.formats = formats;
	}


	@Override
	protected void doApplyTo(ElementFormats format) {
		((ScaleBarFormats)format).assignScaleBarFormats(formats);
	}

	
	public boolean validTarget(ElementFormats format) {
		return format instanceof ScaleBarFormats;
	}
}