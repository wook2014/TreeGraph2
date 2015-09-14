package info.bioinfweb.treegraph.gui.mainframe;


import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.topologicalcalculation.LeafSet;
import info.bioinfweb.treegraph.document.topologicalcalculation.NodeInfo;
import info.bioinfweb.treegraph.document.topologicalcalculation.TopologicalCalculator;
import info.bioinfweb.treegraph.gui.treeframe.TreeInternalFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeSelection;
import info.bioinfweb.treegraph.gui.treeframe.TreeViewPanel;
import info.bioinfweb.treegraph.gui.treeframe.TreeViewPanelListener;

import java.util.Iterator;

import javax.swing.event.ChangeEvent;



public class TreeSelectionSynchronizer implements TreeViewPanelListener {
	private MainFrame owner;
	private boolean isUpdating = false;
	private TopologicalCalculator topologicalCalculator = null;
	
	
	public TreeSelectionSynchronizer(MainFrame owner) {
		super();
		this.owner = owner;		
	}


	public MainFrame getOwner() {
		return owner;
	}
	
	
	private void selectAccordingNodes(TreeViewPanel source, TreeViewPanel target) {
		if (!source.equals(target)) {
			TreeSelection selection = target.getSelection();
			selection.clear();
			for (Node node : source.getSelection().getAllElementsOfType(Node.class, false)) {
				System.out.println("Source Leafset: " + topologicalCalculator.getLeafSet(node));
				System.out.println("Target Leafset: " + topologicalCalculator.getLeafSet(target.getDocument().getTree().getPaintStart()));
				LeafSet targetLeafs = topologicalCalculator.getLeafSet(node).addTo(topologicalCalculator.getLeafSet(target.getDocument().getTree().getPaintStart()));
//				LeafSet targetLeafs = topologicalCalculator.getLeafSet(node);
				System.out.println("Added LS: " + targetLeafs);
				NodeInfo sourceNode = topologicalCalculator.findSourceNodeWithAllLeafs(target.getDocument().getTree().getPaintStart(), targetLeafs);
				if (sourceNode.getAdditionalCount() == 0) {
					System.out.println("Match found, additional count: " + sourceNode.getAdditionalCount());
					System.out.println("Matching Leafset: " + topologicalCalculator.getLeafSet(sourceNode.getNode()));
					selection.add(sourceNode.getNode());
				}
				else { // if (sourceNode.getAdditionalCount() == -1)
					System.out.println("Conflict found, additional count:" + sourceNode.getAdditionalCount());
//					System.out.println("Target Leafset: " + topologicalCalculator.getLeafSet(sourceNode.getNode()));
//					selection.add(sourceNode.getNode());
//					Node highestConflictingNode = topologicalCalculator.findHighestConflictingNode(target.getDocument().getTree().getPaintStart(), node, bestSourceNode);
//		
//					if (highestConflictingNode != null) {
//						selection.add(highestConflictingNode);
//					}
//					else {
//						System.out.println("Null.");
//					}					
				}
			}
		}					
	}


	@Override
	public void selectionChanged(ChangeEvent e) {
		if (!isUpdating) {
			isUpdating = true;  // Avoid recursive calls
			try {
				TreeViewPanel source = (TreeViewPanel)e.getSource();				
				Iterator<TreeInternalFrame> iterator = getOwner().treeFrameIterator();
				while (iterator.hasNext()) {
					TreeViewPanel target = iterator.next().getTreeViewPanel();
					selectAccordingNodes(source, target);
				}
			}
			finally {
				isUpdating = false;
			}
		}
	}

	
	@Override
	public void zoomChanged(ChangeEvent e) {
//		if (!isUpdating) {
//			isUpdating = true;  // Avoid recursive calls
//			try {
//				float zoom = ((TreeViewPanel)e.getSource()).getZoom();
//				Iterator<ResultTreePanel> iterator = getOwner().getTreePanels().iterator();
//				while (iterator.hasNext()) {
//					TreeViewPanel treePanel = iterator.next().getTreeScrollPane().getTreeViewPanel(); 
//					if (!e.getSource().equals(treePanel)) {
//						treePanel.setZoom(zoom);
//					}
//				}
//			}
//			finally {
//				isUpdating = false;
//			}
//		}
	}

	
		@Override
	public void sizeChanged(ChangeEvent e) {}
	
	
	public TopologicalCalculator getTopologicalCalculator() {
		return topologicalCalculator;
	}
	
	
	public void setTopologicalCalculator(TopologicalCalculator topologicalCalculator) {
		this.topologicalCalculator = topologicalCalculator;
	}
}