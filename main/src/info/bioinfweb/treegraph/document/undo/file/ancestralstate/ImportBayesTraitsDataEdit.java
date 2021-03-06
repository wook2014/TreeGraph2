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
package info.bioinfweb.treegraph.document.undo.file.ancestralstate;


import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import info.bioinfweb.commons.progress.ProgressMonitor;
import info.bioinfweb.treegraph.document.Branch;
import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.PieChartLabel;
import info.bioinfweb.treegraph.document.PieChartLabel.SectionData;
import info.bioinfweb.treegraph.document.change.DocumentChangeType;
import info.bioinfweb.treegraph.document.io.ancestralstate.AncestralStateData;
import info.bioinfweb.treegraph.document.io.ancestralstate.BayesTraitsReader;
import info.bioinfweb.treegraph.document.nodebranchdata.IDElementAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.VoidNodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.topologicalcalculation.LeafSet;
import info.bioinfweb.treegraph.document.topologicalcalculation.NodeInfo;
import info.bioinfweb.treegraph.document.undo.AbstractTopologicalCalculationEdit;
import info.bioinfweb.treegraph.document.undo.WarningMessageEdit;
import info.bioinfweb.treegraph.gui.actions.DocumentAction;



public class ImportBayesTraitsDataEdit extends AbstractTopologicalCalculationEdit implements WarningMessageEdit {
	private AncestralStateImportParameters parameters;
	private Map<Node, LeafSet> internalNodes = new HashMap<Node, LeafSet>();
	private Set<String> terminalNodesNotFound = new TreeSet<String>();
	private Set<String> internalDataNotAdded = new TreeSet<String>();
	private Set<String> nodeDataNotFound = new TreeSet<String>();
	private ProgressMonitor progressMonitor;
	


	public ImportBayesTraitsDataEdit(Document document,	AncestralStateImportParameters parameters, ProgressMonitor progressMonitor) {
		super(document, DocumentChangeType.TOPOLOGICAL_BY_RENAMING, parameters.getKeyAdapter(), true);  //BayesTraits supposedly does not support unrooted trees
		this.parameters = parameters;
		this.progressMonitor = progressMonitor;
	}
	
	
	public ImportBayesTraitsDataEdit(Document document,	AncestralStateImportParameters parameters) {
		this(document, parameters, null);
	}
	
	
	public boolean isAllTerminalsFound() {
		return terminalNodesNotFound.isEmpty();
	}
	
	
	public boolean isAllInternalsFound() {
		return internalDataNotAdded.isEmpty();
	}
	
	
	public boolean isAllNodeDataFound() {
		return nodeDataNotFound.isEmpty();
	}
	
	
	public Set<String> getTerminalNodesNotFound() {
		return terminalNodesNotFound;
	}
	
	
	public Set<String> getInternalDataNotAdded() {
		return internalDataNotAdded;
	}

	
	public Set<String> getNodeDataNotFound() {
		return nodeDataNotFound;
	}
	
	
	@Override
	public String getWarningText() {
		StringBuffer message = new StringBuffer();
		
		if (hasTerminalsNotFoundWarnings()) {
			message.append("The following MRCA/node definitions could not be reconstructed, because one or more\n");
			message.append("of the referenced terminal nodes is not contained in the current tree document:\n\n");
			message.append(DocumentAction.createElementList(terminalNodesNotFound, true));
		}
		
		if (hasInternalDataNotAddedWarnings()) {
			message.append("More than one BayesTraits node/MRCA definitions were found for the following node(s). \n");
			message.append("(Only the data of the MRCA definition(s) containing the highest number of terminal taxa were imported.\n");
			Iterator<String> iterator = internalDataNotAdded.iterator();
			if (!(parameters.getInternalNodeNamesAdapter() instanceof VoidNodeBranchDataAdapter)) {
				message.append("The names of the imported Node/MRCA definitions are listed below.)\n\n");
				while (iterator.hasNext()) {
					message.append("\"" + parameters.getInternalNodeNamesAdapter().getText(getDocument().getTree().getNodeByUniqueName(iterator.next())) + "\"" + "\n"); //TODO use DocumentAction.createElementList() here
				}
			}
			else {
				message.append("Unique node names of the affected nodes are listed below.)\n\n");
				message.append(DocumentAction.createElementList(internalDataNotAdded, true));
			}
		}
		
		if (hasNodeDataNotFoundWarnings()) {
			message.append("No probability data for the following internal nodes could be found:\n\n");
			message.append(DocumentAction.createElementList(nodeDataNotFound, true));
			message.append("\nMake sure the BayesTraits analysis worked correctly.");
		}
		
	  return message.toString();
	}
  
  
  @Override
	public boolean hasWarnings() {
		return hasTerminalsNotFoundWarnings() || hasInternalDataNotAddedWarnings() || hasNodeDataNotFoundWarnings();
	}
  

  public boolean hasTerminalsNotFoundWarnings() {
	  return !isAllTerminalsFound();
  }
  
  
  public boolean hasInternalDataNotAddedWarnings() {
	  return !isAllInternalsFound();
  }
	
	
  public boolean hasNodeDataNotFoundWarnings() {
	  return !isAllNodeDataFound();
  }


	private Node findReconstructedNode(AncestralStateData data) {
		LeafSet leafSet = null;
		if (getTopologicalCalculator().isProcessRooted()) {
			leafSet = new LeafSet(getTopologicalCalculator().getLeafCount() + 1);
		}
		else {
			leafSet = new LeafSet(getTopologicalCalculator().getLeafCount());
		}
		if (data.getName().equals(BayesTraitsReader.ROOT_NAME)) {
			for (int i = 0; i < leafSet.size(); i++) {
				leafSet.setChild(i, true);
			}
		}
		else {
			Iterator<String> iterator = data.getLeafNames().iterator();
			while (iterator.hasNext()) {
				int index = getTopologicalCalculator().getLeafIndex(iterator.next());
				if (index != -1) {				
					leafSet.setChild(index, true);
				}
				else {
					terminalNodesNotFound.add(data.getName());
					return null;
				}
			}
		}
		
		List<NodeInfo> results = getTopologicalCalculator().findNodeWithAllLeaves(getDocument().getTree(), leafSet, null);  //TODO Use a restricting leaf set here?
		Node result = null;
		if (!results.isEmpty()) {
			result = results.get(0).getNode();
		}
		//TODO Possibly handle case when multiple equivalent nodes are found?

		if (internalNodes.keySet().contains(result)) {
			internalDataNotAdded.add(result.getUniqueName());
			if (internalNodes.get(result).size() < leafSet.size()) {				
				internalNodes.put(result, leafSet);
			}
			else {				
				return null;
			}
		}
		else {
			internalNodes.put(result, leafSet);
		}
		
		return result;  //TODO handle isDownwards() == false
	}


	@Override
	public String getPresentationName() {
		return "Import BayesTraits log data";
	}


	@Override
	protected void performRedo() {
		getTopologicalCalculator().addLeafSets(getDocument().getTree().getPaintStart(), parameters.getKeyAdapter());		

		double progressInterval = 1 / (double)parameters.getData().size();
		for (String internalNodeName : parameters.getData().keySet()) {
			Node internalNode = findReconstructedNode(parameters.getData().get(internalNodeName));
			
			if (internalNode != null) {
				Branch branch = internalNode.getAfferentBranch();
				int importAdapterIndex = 0;
				Iterator<String> characterIterator = parameters.getData().get(internalNodeName).getSiteIterator();
				int characterIndex = 0;
				parameters.getInternalNodeNamesAdapter().setText(internalNode, internalNodeName);
				while (characterIterator.hasNext()) {
					String labelID = parameters.getPieChartLabelIDs()[characterIndex];
					PieChartLabel label = null;
					if (labelID != null) {
						label = new PieChartLabel(branch.getLabels());
						label.setID(parameters.getPieChartLabelIDs()[characterIndex]);
					}
					
					String siteKey = characterIterator.next();
					Iterator<String> stateIterator = parameters.getData().get(internalNodeName).getKeyIterator(siteKey);
					while (stateIterator.hasNext()) {
						if (labelID != null) {
							label.getSectionDataList().add(new PieChartLabel.SectionData(
									((IDElementAdapter)parameters.getImportAdapters()[importAdapterIndex]).getID(), ""));
						}
						
						double probability = parameters.getData().get(internalNodeName).getProbability(siteKey, stateIterator.next());
						if (!Double.isNaN(probability)) {
							parameters.getImportAdapters()[importAdapterIndex].setDecimal(internalNode, probability);
						}
						else {
							nodeDataNotFound.add(parameters.getData().get(internalNodeName).getName());
							parameters.getImportAdapters()[importAdapterIndex].setText(internalNode, "--");
						}
						importAdapterIndex += 1;
					}
					
					if (labelID != null) {
						internalNode.getAfferentBranch().getLabels().add(label);
					}
					characterIndex += 1;
				}
			}
			
			if (progressMonitor != null) {
				progressMonitor.addToProgressValue(progressInterval, "Writing data to \"" + internalNodeName + "\".");
			}
		}
	}
}