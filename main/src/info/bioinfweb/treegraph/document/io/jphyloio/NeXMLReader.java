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
package info.bioinfweb.treegraph.document.io.jphyloio;


import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import info.bioinfweb.jphyloio.JPhyloIO;
import info.bioinfweb.jphyloio.JPhyloIOEventReader;
import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.events.ConcreteJPhyloIOEvent;
import info.bioinfweb.jphyloio.events.EdgeEvent;
import info.bioinfweb.jphyloio.events.JPhyloIOEvent;
import info.bioinfweb.jphyloio.events.LabeledIDEvent;
import info.bioinfweb.jphyloio.events.LinkedLabeledIDEvent;
import info.bioinfweb.jphyloio.events.NodeEvent;
import info.bioinfweb.jphyloio.events.meta.LiteralMetadataContentEvent;
import info.bioinfweb.jphyloio.events.meta.LiteralMetadataEvent;
import info.bioinfweb.jphyloio.events.meta.ResourceMetadataEvent;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.events.type.EventTopologyType;
import info.bioinfweb.jphyloio.formats.nexml.NeXMLEventReader;
import info.bioinfweb.jphyloio.formats.xtg.XTGConstants;
import info.bioinfweb.jphyloio.utils.JPhyloIOReadingUtils;
import info.bioinfweb.treegraph.document.Branch;
import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.HiddenDataElement;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.TextElementData;
import info.bioinfweb.treegraph.document.Tree;
import info.bioinfweb.treegraph.document.format.Margin;
import info.bioinfweb.treegraph.document.format.NodeFormats;
import info.bioinfweb.treegraph.document.io.AbstractDocumentReader;
import info.bioinfweb.treegraph.document.io.DocumentIterator;
import info.bioinfweb.treegraph.document.io.SingleDocumentIterator;
import info.bioinfweb.treegraph.document.metadata.LiteralMetadataNode;
import info.bioinfweb.treegraph.document.metadata.MetadataNode;
import info.bioinfweb.treegraph.document.metadata.MetadataPath;
import info.bioinfweb.treegraph.document.metadata.MetadataPathElement;
import info.bioinfweb.treegraph.document.metadata.MetadataTree;
import info.bioinfweb.treegraph.document.metadata.ResourceMetadataNode;
import info.bioinfweb.treegraph.document.nodebranchdata.BranchLengthAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeNameAdapter;



public class NeXMLReader extends AbstractDocumentReader {
	private JPhyloIOEventReader reader;
	private String currentTreeName;
	private List<Tree> trees = new ArrayList<Tree>();
	private List<String> names = new ArrayList<String>();
	private Map<String, Node> idToNodeMap = new HashMap<String, Node>();
	private List<String> possiblePaintStartIDs = new ArrayList<String>();
	private List<String> rootNodeIDs = new ArrayList<String>(); //TODO Mark all root nodes with icon label or something similar
	Map<QName, Double> marginMap = new HashMap<QName, Double>();
	Map<QName, Object> valueMap = new HashMap<QName, Object>();	
//	private String currentColumnID = null;
	private NodeBranchDataAdapter nodeNameAdapter = NodeNameAdapter.getSharedInstance();
	private BranchLengthAdapter branchLengthAdapter = BranchLengthAdapter.getSharedInstance();
	

	public NeXMLReader() {
		super(false);
	}

	
	@Override
	public Document readDocument(BufferedInputStream stream) throws Exception {
		document = null;
		ReadWriteParameterMap parameters = new ReadWriteParameterMap();
		parameters.put(ReadWriteParameterMap.KEY_USE_OTU_LABEL, true);
		reader = new NeXMLEventReader(stream, parameters);  //TODO Use JPhyloIOReader for other formats (currently not possible, due to exceptions)
		
		try {
			JPhyloIOEvent event;
			while (reader.hasNextEvent()) {
	      event = reader.next();
	      switch (event.getType().getContentType()) {
	      	case DOCUMENT:
	      		if (event.getType().getTopologyType().equals(EventTopologyType.START)) {
	      			document = createEmptyDocument();
	      		}
	      		else if (event.getType().getTopologyType().equals(EventTopologyType.END)) {
		          reader.close();
		          
		          if (!trees.isEmpty()) {
		          	Tree tree = trees.get(parameterMap.getTreeSelector().select(names.toArray(new String[names.size()]), trees));
		          	document.setTree(tree);
		          }
		          else {
		          	throw new IOException("The document did not contain any tree or no valid tree could be read from the document.");
		          }
		          
		          return document;
	      		}
	      		break;
	      	case TREE_NETWORK_GROUP:
	      		if (event.getType().getTopologyType().equals(EventTopologyType.START)) {
	      			readTreeNetworkGroup(event);
	      		}
//	      		else if (event.getType().getTopologyType().equals(EventTopologyType.END)) {
//		          reader.next();
//	      		}
	        	break;
	        default:
	        	if (event.getType().getTopologyType().equals(EventTopologyType.START)) {  // Possible additional element, which is not read. SOLE and END events do not have to be processed here, because they have no further content.
	        		JPhyloIOReadingUtils.reachElementEnd(reader);
	      		}
	        	break;
	      }
	    }
		}
		finally {
	    reader.close();
	    stream.close();
		}		
		trees.clear();
		return null;
	}
	
	
	private void readTreeNetworkGroup(JPhyloIOEvent treeGroupEvent) throws XMLStreamException, IOException {
		JPhyloIOEvent event = reader.next();  
    while (!event.getType().getTopologyType().equals(EventTopologyType.END)) {    	
    	if (event.getType().getContentType().equals(EventContentType.TREE)) { // Networks can not be displayed by TG and are therefore not read  		
    		readTree(event.asLabeledIDEvent());
    	}
    	else {  // Possible additional element, which is not read
      	if (event.getType().getTopologyType().equals(EventTopologyType.START)) {  // SOLE and END events do not have to be processed here, because they have no further content.
      		JPhyloIOReadingUtils.reachElementEnd(reader);
    		}
      }
      event = reader.next();
    }
  }
	
	
	private void readTree(LabeledIDEvent treeEvent) throws XMLStreamException, IOException {
		if ((treeEvent.getLabel() != null) && !treeEvent.getLabel().isEmpty()) {
			currentTreeName = treeEvent.getLabel();
    }
		else {
			currentTreeName = treeEvent.getID();
		}
		
		possiblePaintStartIDs.clear();
		idToNodeMap.clear();
		
    JPhyloIOEvent event = reader.next();
    while (!event.getType().getTopologyType().equals(EventTopologyType.END)) {    	
    	if (event.getType().getContentType().equals(EventContentType.NODE)) {
    		readNode(event.asNodeEvent());
    	}
    	else if (event.getType().getContentType().equals(EventContentType.EDGE) || event.getType().getContentType().equals(EventContentType.ROOT_EDGE)) {
    		readEdge(event.asEdgeEvent());
    	}
    	else {  // Possible additional element, which is not read
      	if (event.getType().getTopologyType().equals(EventTopologyType.START)) {  // SOLE and END events do not have to be processed here, because they have no further content.
      		JPhyloIOReadingUtils.reachElementEnd(reader);
    		}
      }
      event = reader.next();
    }
    
    if (possiblePaintStartIDs.size() > 1) {
    	throw new IOException("More than one root node was found for the tree \"" + currentTreeName + "\", but this can not be displayed in TreeGraph 2.");
    }
    
    Tree tree = new Tree();
    tree.setPaintStart(idToNodeMap.get(possiblePaintStartIDs.get(0)));
    tree.assignUniqueNames();
    if (!rootNodeIDs.isEmpty()) {
    	tree.getFormats().setShowRooted(true);
    }
    trees.add(tree);
    names.add(currentTreeName);    
  }
	
	
	private void readNode(NodeEvent nodeEvent) throws XMLStreamException, IOException {
		Node node = Node.newInstanceWithBranch();

		nodeNameAdapter.setText(node, nodeEvent.getLabel());
		idToNodeMap.put(nodeEvent.getID(), node);
		possiblePaintStartIDs.add(nodeEvent.getID());
		
		if (nodeEvent.isRootNode()) {
			rootNodeIDs.add(nodeEvent.getID());
		}
		
		JPhyloIOEvent event = reader.next();
		MetadataPath path = new MetadataPath(true, ((event.getType().getContentType().equals(EventContentType.LITERAL_META) ? true : false)));
    readMetadata(node, event, path);
	}
	
	
	private void readEdge(EdgeEvent edgeEvent) throws XMLStreamException, IOException {
		Node targetNode = idToNodeMap.get(edgeEvent.getTargetID());
		Node sourceNode = idToNodeMap.get(edgeEvent.getSourceID());		
		
		if (targetNode.getParent() == null) {
			targetNode.setParent(sourceNode);
			branchLengthAdapter.setDecimal(targetNode, edgeEvent.getLength());
			
			if (sourceNode != null) {
				sourceNode.getChildren().add(targetNode);
				possiblePaintStartIDs.remove(edgeEvent.getTargetID());  // Nodes that were not referenced as target are possible paint starts
			}
		}
		else {  // Edge is network edge
			throw new IOException("Multiple parent nodes were specified for the node \"" + edgeEvent.getTargetID() + "\" in the tree \"" + currentTreeName 
					+ "\", but networks can not be displayed by TreeGraph 2.");
		}
		
		JPhyloIOEvent event = reader.next();
		MetadataPath path = new MetadataPath(false, ((event.getType().getContentType().equals(EventContentType.LITERAL_META) ? true : false)));
		readMetadata(targetNode.getAfferentBranch(), event, path);
  }


	private void readMetadata(HiddenDataElement node, JPhyloIOEvent event, MetadataPath parentPath) throws IOException {
 		Map<QName, Integer> resourceMap = new HashMap<QName, Integer>();
		Map<QName, Integer> literalMap = new HashMap<QName, Integer>();
		
		while (!event.getType().getTopologyType().equals(EventTopologyType.END)) {  // It is assumed that events are correctly nested   			
			if (event.getType().getContentType().equals(EventContentType.RESOURCE_META)) {
				ResourceMetadataEvent resourceMeta = event.asResourceMetadataEvent();
				
				if ((resourceMeta.getRel().getURI() != null)) {
					storePredicateAndIndexInMap(resourceMap, parentPath, resourceMeta.getRel().getURI());
					
					MetadataNode metadataNode = node.getMetadataTree().searchAndCreateNodeByPath(parentPath, true);
					if ((resourceMeta.getHRef() != null)) {
						((ResourceMetadataNode)metadataNode).setURI(resourceMeta.getHRef());					
					}						
					
//    					if(!(resourceMeta.getRel().getURI().equals(TreeDataAdapter.PREDICATE_INTERNAL_DATA))) {
//    					}
				}
				if (reader.peek().getType().getContentType().equals(EventContentType.LITERAL_META) ||reader.peek().getType().getContentType().equals(EventContentType.RESOURCE_META)) {
					event = reader.next();
					MetadataPath childPath = new MetadataPath(parentPath.isNode(), ((event.getType().getContentType().equals(EventContentType.LITERAL_META) ? true : false)));
					childPath.getElementList().addAll(parentPath.getElementList());
					readMetadata(node, event, childPath);
				}
			}
			else if (event.getType().getContentType().equals(EventContentType.LITERAL_META)) {
				LiteralMetadataEvent literalMeta = event.asLiteralMetadataEvent();		
				
				readLiteralMetadataContent(node, parentPath, literalMap, literalMeta, event);
				event = reader.next();
			}
			else {
				JPhyloIOReadingUtils.reachElementEnd(reader);
			}
		}
	}


	public void readLiteralMetadataContent(HiddenDataElement node, MetadataPath path, Map<QName, Integer> literalMap, LiteralMetadataEvent literalMeta, JPhyloIOEvent event) throws IOException {
		if (literalMeta.getPredicate().getURI() == null) {
			throw new InternalError("Handling string keys currently not implemented.");
		}
		
		if (reader.peek().getType().getContentType().equals(EventContentType.LITERAL_META_CONTENT)) {
			event = reader.next();
			LiteralMetadataContentEvent literalEvent = event.asLiteralMetadataContentEvent();
			
			storePredicateAndIndexInMap(literalMap, path, literalMeta.getPredicate().getURI());
		
			StringBuffer content = new StringBuffer();
			TextElementData data = null;
			
			if (literalEvent.getObjectValue() != null) {
				if (literalEvent.getObjectValue() instanceof Number) {
					data = new TextElementData(((Number)literalEvent.getObjectValue()).doubleValue());
				}
				else {
					content.append(literalEvent.getObjectValue().toString());    	  
				}
		  }
		  else {
		  	content.append(literalEvent.getStringValue());
		  }	

		  while (reader.peek().getType().getContentType().equals(EventContentType.LITERAL_META_CONTENT)) {
		    event = reader.next();
		    content.append(event.asLiteralMetadataContentEvent().getStringValue());  // Content can only be continued if it has only a string value      
		  }
		  
		  if (data == null) {
		  	data = new TextElementData(content.toString());
		  }
		  
			MetadataNode metadataNode = node.getMetadataTree().searchAndCreateNodeByPath(path, true);
			((LiteralMetadataNode)metadataNode).setValue(data);
		}
	}
	
		
	private void storePredicateAndIndexInMap(Map<QName, Integer> map, MetadataPath path, QName predicate) {
		if (!map.containsKey(predicate)) {
			map.put(predicate, 0);
		}
		else {
			map.put(predicate, map.get(predicate) + 1);
		}
		path.getElementList().add(new MetadataPathElement(predicate, map.get(predicate)));
	}
	
	
//	private void createInternalMetadata(HiddenDataElement node, JPhyloIOEvent event) throws IOException {
//		while (!event.getType().getTopologyType().equals(EventTopologyType.END)) {
//			if (event.getType().getContentType().equals(EventContentType.LITERAL_META) || event.getType().getContentType().equals(EventContentType.RESOURCE_META)) {
//				if (event.getType().getContentType().equals(EventContentType.RESOURCE_META)) {
//					marginMap.clear();
//					valueMap.clear();		
//				}	
//				else if (event.getType().getContentType().equals(EventContentType.LITERAL_META)) {
//					LiteralMetadataEvent literalMeta = event.asLiteralMetadataEvent();
//					if (literalMeta.getPredicate().getURI() != null) {
//						literalPredicate = literalMeta.getPredicate().getURI();
//					}
//				}
////				event = reader.next();
////				createInternalMetadata(node, event);
//			}
//			else if (event.getType().getContentType().equals(EventContentType.LITERAL_META_CONTENT)) {
//				readInternalMetadataContent(event.asLiteralMetadataContentEvent(), node, marginMap, valueMap);
//			}
//			else {
//				if (event.getType().getTopologyType().equals(EventTopologyType.START)) {
//					JPhyloIOReadingUtils.reachElementEnd(reader);
//				}
//			}
//			event = reader.next();
////			createInternalMetadata(node, event);
//		}
//	}
//	
//	
//	private void readInternalMetadataContent(LiteralMetadataContentEvent literalEvent, HiddenDataElement node, Map<QName, Double> marginMap, Map<QName, Object> valueMap) throws IOException {
////		TextElementData data = getValueOfLiteralMetadataContent(literalEvent);
//		
//		if (literalPredicate.equals(XTGConstants.PREDICATE_MARGIN_LEFT)) {
//			marginMap.put(literalPredicate, literalEvent.getObjectValue());
//			((Node)node).getFormats().getLeafMargin().
//		} 
//		else if (literalPredicate.equals(XTGConstants.PREDICATE_MARGIN_TOP)) {
//			marginMap.put(literalPredicate, data.getDecimal());
//		}
//		else if (literalPredicate.equals(XTGConstants.PREDICATE_MARGIN_RIGHT)) {
//			marginMap.put(literalPredicate, data.getDecimal());
//		}
//		else if (literalPredicate.equals(XTGConstants.PREDICATE_MARGIN_BOTTOM)) {
//			marginMap.put(literalPredicate, data.getDecimal());
//		}
//		else if (literalPredicate.equals(XTGConstants.PREDICATE_LINE_WIDTH)) {
//			valueMap.put(literalPredicate, data.getDecimal());
//		}
//		else if (literalPredicate.equals(XTGConstants.PREDICATE_LINE_COLOR)) {
//			valueMap.put(literalPredicate, data.getText());
//		}				
//		else if (literalPredicate.equals(XTGConstants.PREDICATE_NODE_ATTR_UNIQUE_NAME)) {
//			((Node)node).setUniqueName(data.getText());
//		}
//		else if (literalPredicate.equals(XTGConstants.PREDICATE_NODE_ATTR_EDGE_RADIUS)) {
//			((Node)node).getFormats().getCornerRadius().setInMillimeters((float) data.getDecimal());
//		}
////				else if (literalPredicate.equals(XTGConstants.PREDICATE_IS_DECIMAL)) {
////					if (node instanceof Node) {
////						}
////					}
////				else if (literalPredicate.equals(XTGConstants.PREDICATE_TEXT_COLOR)) {
////					if (node instanceof Node) {
////						((Node)node).getFormats().setTextColor((Color) data.getText());
////					}
////				}
//		else if (literalPredicate.equals(XTGConstants.PREDICATE_TEXT_HEIGHT)) {
//			if (node instanceof Node) {
//				((Node)node).getFormats().getTextHeight().setInMillimeters((float) data.getDecimal());
//			}
//		}
//		else if (literalPredicate.equals(XTGConstants.PREDICATE_TEXT_STYLE)) {
//			if (node instanceof Node) {
//				((Node)node).getFormats().setTextStyle((int) data.getDecimal());
//			}
//		}
//		else if (literalPredicate.equals(XTGConstants.PREDICATE_FONT_FAMILY)) {
//			if (node instanceof Node) {
//				((Node)node).getFormats().setFontName(data.getText());
//			}
//		}
////				else if (literalPredicate.equals(XTGConstants.PREDICATE_DECIMAL_FORMAT)) {
////					if (node instanceof Node) {
////						
////					}
////				}
////				else if (literalPredicate.equals(XTGConstants.PREDICATE_LOCALE_LANG)) {
////					if (node instanceof Node) {
////						((Node)node).getFormats().getLocale().getLanguage()
////					}
////				}
////				else if (literalPredicate.equals(XTGConstants.PREDICATE_LOCALE_COUNTRY)) {
////					if (node instanceof Node) {
////						((Node)node).getFormats().getLocale().getCountry().
////					}
////				}
////				else if (literalPredicate.equals(XTGConstants.PREDICATE_LOCALE_VARIANT)) {
////					if (node instanceof Node) {
////						((Node)node).getFormats().getLocale().getVariant().
////					}
////				}
//		else if (literalPredicate.equals(XTGConstants.PREDICATE_BRANCH_ATTR_CONSTANT_WIDTH)) {
//			((Branch)node).getFormats().setConstantWidth(Boolean.parseBoolean(data.getText()));
//		}
//		else if (literalPredicate.equals(XTGConstants.PREDICATE_BRANCH_ATTR_MIN_LENGTH)) {
//			((Branch)node).getFormats().getMinLength().setInMillimeters((float) data.getDecimal());
//		}
//		else if (literalPredicate.equals(XTGConstants.PREDICATE_BRANCH_ATTR_MIN_SPACE_ABOVE)) {
//			((Branch)node).getFormats().getMinSpaceAbove().setInMillimeters((float) data.getDecimal());
//		}
//		else if (literalPredicate.equals(XTGConstants.PREDICATE_BRANCH_ATTR_MIN_SPACE_BELOW)) {
//			((Branch)node).getFormats().getMinSpaceBelow().setInMillimeters((float) data.getDecimal());
//		}
//		
//		
//		if (marginMap.containsKey(XTGConstants.PREDICATE_MARGIN_LEFT) && marginMap.containsKey(XTGConstants.PREDICATE_MARGIN_TOP) 
//				&& marginMap.containsKey(XTGConstants.PREDICATE_MARGIN_RIGHT) && marginMap.containsKey(XTGConstants.PREDICATE_MARGIN_BOTTOM)) {
//			
//			Margin margin = new Margin(marginMap.get(XTGConstants.PREDICATE_MARGIN_LEFT).floatValue(), 
//					marginMap.get(XTGConstants.PREDICATE_MARGIN_TOP).floatValue(), 
//					marginMap.get(XTGConstants.PREDICATE_MARGIN_RIGHT).floatValue(), 
//					marginMap.get(XTGConstants.PREDICATE_MARGIN_BOTTOM).floatValue());
//			
//			if (node instanceof Node) {
//				NodeFormats nodeFormats = new NodeFormats();
//				nodeFormats.getLeafMargin().assign(margin);
//				((Node)node).getFormats().assignNodeFormats(nodeFormats);
//			}
//			marginMap.clear();
//		}
//		
////		if (valueMap.containsKey(XTGConstants.PREDICATE_LINE_WIDTH) && valueMap.containsKey(XTGConstants.PREDICATE_LINE_COLOR)) {
////			Color color = (Color) valueMap.get(XTGConstants.PREDICATE_LINE_COLOR);
////			Float lineWith = valueMap.get(XTGConstants.PREDICATE_LINE_WIDTH);
////			if (node instanceof Node) {
////				((Node)node).getFormats().setLineColor(color);
////				((Node)node).getFormats().getLineWidth().setInMillimeters(lineWith);
////			}
////			else if (node instanceof Branch) {
////				((Branch)node).getLineFormats().setLineColor(color);
////				((Branch)node).getLineFormats().getLineWidth().setInMillimeters(lineWith);
////			}
////			valueMap.clear();
////		}
//	}
	

	@Override
	public DocumentIterator createIterator(BufferedInputStream stream) throws Exception {
		return new SingleDocumentIterator(read(stream));
	}
	
	
	public void setMargin(Margin margin, JPhyloIOEvent event) throws IOException {
		LiteralMetadataEvent literalMarginEvent = event.asLiteralMetadataEvent();
		if (info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_MARGIN_LEFT.equals(literalMarginEvent.getPredicate().getURI())) {
			margin.getLeft().setInMillimeters(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Number.class).floatValue());
		}
		else if (info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_MARGIN_TOP.equals(literalMarginEvent.getPredicate().getURI())) {
			margin.getTop().setInMillimeters(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Number.class).floatValue());
		}
		else if (info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_MARGIN_RIGHT.equals(literalMarginEvent.getPredicate().getURI())) {
			margin.getRight().setInMillimeters(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Number.class).floatValue());
		}
		else if (info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_MARGIN_BOTTOM.equals(literalMarginEvent.getPredicate().getURI())) {
			margin.getBottom().setInMillimeters(JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Number.class).floatValue());
		}
	}
}
