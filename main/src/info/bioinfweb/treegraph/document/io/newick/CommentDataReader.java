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
package info.bioinfweb.treegraph.document.io.newick;


import info.bioinfweb.treegraph.document.HiddenDataMap;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.TextElementData;



/**
 * Reads hidden node data from a comment in a Newick string like it is generated by BEAST.
 * 
 * @author Ben St&ouml;ver
 * @since 2.0.24
 */
public class CommentDataReader {
	public static final char START_SYMBOL = '&';
	public static final char ALLOCATION_SEPARATOR_SYMBOL = ',';
	public static final char ALLOCATION_SYMBOL = '=';
	public static final char FIELD_START_SYMBOL = '{';
  public static final char FIELD_END_SYMBOL = '}';
	public static final char VALUE_SEPARATOR_SYMBOL = ',';
	public static final char STRING_DELIMITER = '"';
	
	public static final char INDEX_START_SYMBOL = '[';
	public static final char INDEX_END_SYMBOL = ']';
	
	public static final String DEFAULT_BRANCH_COLUMN_NAME = "unnamedBranchHotComment";
	public static final String DEFAULT_NODE_COLUMN_NAME = "unnamedNodeHotComment";
	
	
	private TextElementData readTextElementData(String text) {
		if (text.startsWith(Character.toString(STRING_DELIMITER)) && text.endsWith(Character.toString(STRING_DELIMITER))) {
			return new TextElementData(text.substring(1, text.length() - 1));  // Values like "100" should also be read as a string.
		}
		else {
			try {
				return new TextElementData(Double.parseDouble(text));
			}
			catch (NumberFormatException e) {
				return new TextElementData(text);
			}
		}
	}
	
	
	private int findFieldEnd(String text, int pos) {
		while (pos < text.length()) {
			if (text.charAt(pos) == ALLOCATION_SEPARATOR_SYMBOL) {
				return pos;
			}
			else {
				pos++;
			}
		}
		return -1;
	}
	
	
	private int findAllocationEnd(String text, int start) {
		if (start >= text.length()) {
			return -1;
		}
		else {
			int pos = start;
			while (pos < text.length()) {
				switch (text.charAt(pos)) {
					case FIELD_START_SYMBOL:
						pos = findFieldEnd(text, pos);
						if (pos == -1) {
							return -1;
						}
						break;
					case ALLOCATION_SEPARATOR_SYMBOL:
						return pos;
				}
				pos++;
			}
			return pos;
		}
	}
	
	
	public void read(String comment, Node node, boolean isOnNode) {
		if (comment.startsWith("" + START_SYMBOL)) {
			int start = 1;
			int end = findAllocationEnd(comment, start);
			while (end != -1) {
				String[] parts = comment.substring(start, end).split("" + ALLOCATION_SYMBOL);
				if (parts.length == 2) {
					for (int j = 0; j < parts.length; j++) {
						parts[j] = parts[j].trim();
					}
					
					if (parts[1].startsWith("" + FIELD_START_SYMBOL)) {
						if (parts[1].endsWith("" + FIELD_END_SYMBOL)) {
							String[] values = parts[1].substring(1, parts[1].length() - 1).split(
									"" + VALUE_SEPARATOR_SYMBOL);
							for (int j = 0; j < values.length; j++) {
								node.getAfferentBranch().getHiddenDataMap().put(parts[0] + INDEX_START_SYMBOL + j + INDEX_END_SYMBOL,
										readTextElementData(values[j].trim()));
							}
						}
					}
					else {
						node.getAfferentBranch().getHiddenDataMap().put(parts[0],	readTextElementData(parts[1]));
					}
				}
				
				start = end + 1;
				end = findAllocationEnd(comment, start);
			}
		}
		else if (comment.length() > 0) {  // Read unformatted comment
			HiddenDataMap map =	node.getAfferentBranch().getHiddenDataMap();
			String name = DEFAULT_BRANCH_COLUMN_NAME;
			if (isOnNode) {
				name = DEFAULT_NODE_COLUMN_NAME;
			}
			map.put(name, readTextElementData(comment));
		}
	}
}