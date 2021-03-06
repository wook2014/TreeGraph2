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
package info.bioinfweb.treegraph.graphics.positionpaint.label;


import java.awt.Graphics2D;

import info.bioinfweb.treegraph.document.IconLabel;
import info.bioinfweb.treegraph.document.format.IconLabelFormats;
import info.bioinfweb.treegraph.graphics.positionpaint.label.icons.LabelIconMap;
import info.bioinfweb.treegraph.graphics.positionpaint.positiondata.PositionData;



public class IconLabelPainter extends AbstractGraphicalLabelPainter<IconLabel, PositionData> {
	@Override
	protected void doPaint(Graphics2D g, float pixelsPerMillimeter, PositionData positionData, IconLabel label) {
		IconLabelFormats f = ((IconLabel)label).getFormats();
		LabelIconMap.getInstance().get(f.getIcon()).paint(g, 
				positionData.getLeft().getInPixels(pixelsPerMillimeter), 
				positionData.getTop().getInPixels(pixelsPerMillimeter), f, pixelsPerMillimeter);
	}

	
	@Override
	public Class<IconLabel> getLabelClass() {
		return IconLabel.class;
	}


	@Override
	public Class<PositionData> getPositionDataClass() {
		return PositionData.class;
	}
}
