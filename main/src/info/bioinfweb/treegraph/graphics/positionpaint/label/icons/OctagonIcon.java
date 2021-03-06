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
package info.bioinfweb.treegraph.graphics.positionpaint.label.icons;


import info.bioinfweb.treegraph.document.format.IconLabelFormats;

import java.awt.Shape;
import java.awt.geom.Path2D;



/**
 * @author Ben St&ouml;ver
 * @since 2.0.25
 */
public class OctagonIcon extends ShapeLabelIcon implements LabelIcon {
	public static final float SQRT_OF_2 = (float)Math.sqrt(2f);
	
	
	@Override
	public Shape getShape(float x, float y, IconLabelFormats formats, float pixelsPerMillimeter) {
		float lineWidth = formats.getLineWidth().getInPixels(pixelsPerMillimeter);
		x += 0.5f * lineWidth;
		y += 0.5f * lineWidth;
		float width = formats.getWidth().getInPixels(pixelsPerMillimeter) - lineWidth;
		float cornerLengthX = width / (2 + SQRT_OF_2);
		float edgeLengthX = SQRT_OF_2 * cornerLengthX;
		float height = formats.getHeight().getInPixels(pixelsPerMillimeter) - lineWidth;
		float cornerLengthY = height / (2 + SQRT_OF_2);
		float edgeLengthY = SQRT_OF_2 * cornerLengthY;
		
		Path2D result = new Path2D.Float();
		result.moveTo(x + cornerLengthX, y);
		result.lineTo(x + cornerLengthX + edgeLengthX, y);
		result.lineTo(x + width, y + cornerLengthY);
		result.lineTo(x + width, y + cornerLengthY + edgeLengthY);
		result.lineTo(x + cornerLengthX + edgeLengthX, y + height);
		result.lineTo(x + cornerLengthX, y + height);
		result.lineTo(x, y + edgeLengthY + cornerLengthY);
		result.lineTo(x, y + cornerLengthY);
		result.closePath();
		return result;
	}

	
	public String id() {
		return "Octagon";
	}
}