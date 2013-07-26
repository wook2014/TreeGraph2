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
package info.bioinfweb.treegraph.document.io;


import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.webinsel.util.log.ApplicationLogger;



/**
 * Implements basic functionality to iterate over files (stream data) with multiple trees.
 * 
 * @author Ben St&ouml;ver
 */
public abstract class AbstractDocumentIterator implements DocumentIterator {
	private ApplicationLogger loadLogger;
	private NodeBranchDataAdapter internalAdapter;
	private NodeBranchDataAdapter branchLengthsAdapter;
	private boolean translateInternalNodes;
	private Document nextDocument = null;
	private boolean beforeFirst = true;
	
	
	public AbstractDocumentIterator(ApplicationLogger loadLogger,
			NodeBranchDataAdapter internalAdapter,
			NodeBranchDataAdapter branchLengthsAdapter, boolean translateInternalNodes) {
		
		super();
		this.loadLogger = loadLogger;
		this.internalAdapter = internalAdapter;
		this.branchLengthsAdapter = branchLengthsAdapter;
		this.translateInternalNodes = translateInternalNodes;
	}


	public ApplicationLogger getLoadLogger() {
		return loadLogger;
	}


	public NodeBranchDataAdapter getInternalAdapter() {
		return internalAdapter;
	}


	public NodeBranchDataAdapter getBranchLengthsAdapter() {
		return branchLengthsAdapter;
	}


	public boolean isTranslateInternalNodes() {
		return translateInternalNodes;
	}
	
	
	private Document getNextDocument() throws Exception {
		if (beforeFirst) {
			nextDocument = readNext();
			beforeFirst = false;
		}
		return nextDocument;
	}
	
	
	protected abstract Document readNext() throws Exception;

	
	@Override
	public Document next() throws Exception {
		Document result = getNextDocument();
		nextDocument = readNext();
		return result;
	}


	@Override
	public boolean hasNext() throws Exception {
		return getNextDocument() != null;
	}


	@Override
	public Document peek() throws Exception {
		return getNextDocument();
	}
}
