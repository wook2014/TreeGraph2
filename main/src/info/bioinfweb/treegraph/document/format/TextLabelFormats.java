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
package info.bioinfweb.treegraph.document.format;


import info.bioinfweb.treegraph.document.Label;

import java.awt.Color;
import java.awt.Font;
import java.text.DecimalFormat;
import java.util.Locale;



/**
 * @author Ben St&ouml;ver
 * @since 2.0.26
 */
public class TextLabelFormats extends LabelFormats implements TextFormats {
	public static final float DEFAULT_TEXT_HEIGHT_IN_MM = 4f;
	
	
	private TextFormats textFormats = new ConcreteTextFormats();
	
	
	private void init() {
		getTextHeight().setInMillimeters(DEFAULT_TEXT_HEIGHT_IN_MM);
	}


	public TextLabelFormats(Label owner, boolean above, int line, int linePosition) {
		super(owner, above, line, linePosition);
		init();
	}


	public TextLabelFormats(Label owner) {
		super(owner);
		init();
	}


	@Override
	public void addTextStyle(int style) {
		textFormats.addTextStyle(style);
	}


	@Override
	public DecimalFormat getDecimalFormat() {
		return textFormats.getDecimalFormat();
	}


	@Override
	public float getDescent() {
		return textFormats.getDescent();
	}


	@Override
	public Font getFont(float pixelsPerMillimeter) {
		return textFormats.getFont(pixelsPerMillimeter);
	}


	@Override
	public String getFontName() {
		return textFormats.getFontName();
	}


	@Override
	public Locale getLocale() {
		return textFormats.getLocale();
	}


	@Override
	public Color getTextColor() {
		return textFormats.getTextColor();
	}


	@Override
	public DistanceValue getTextHeight() {
		return textFormats.getTextHeight();
	}


	@Override
	public int getTextStyle() {
		return textFormats.getTextStyle();
	}


	@Override
	public boolean hasTextStyle(int style) {
		return textFormats.hasTextStyle(style);
	}


	@Override
	public void removeTextStyle(int style) {
		textFormats.removeTextStyle(style);
	}


	@Override
	public void setDecimalFormat(DecimalFormat decimalFormat, Locale locale) {
		textFormats.setDecimalFormat(decimalFormat, locale);
	}


	@Override
	public void setFontName(String fontName) {
		textFormats.setFontName(fontName);
	}


	@Override
	public void setLocale(Locale locale) {
		textFormats.setLocale(locale);
	}


	@Override
	public void setTextColor(Color textColor) {
		textFormats.setTextColor(textColor);
	}


	@Override
	public void setTextStyle(int style) {
		textFormats.setTextStyle(style);
	}

	
	@Override
	public void assignTextFormats(TextFormats other) {
		textFormats.assignTextFormats(other);
	}


	public void assign(TextLabelFormats other) {
		assignLabelFormats(other);
		assignTextFormats(other);
	}
	
	
	@Override
	public TextLabelFormats clone() {
		TextLabelFormats result = new TextLabelFormats(null);
		result.assign(this);
		return result;
	}
}