package org.insilico.sbmlsheets.core.compile;

import java.util.List;

import javax.swing.tree.TreeNode;

import org.insilico.sbmlsheets.core.Spreadsheet;

public abstract class Table {
	
	protected List<? extends TreeNode> sections;
	
	protected final String uri;
	
	protected String tableName;
	
	protected final String tableType = initTableType();
		
	
	public Table(String path) {
		this.uri = path;
		initName();
	}

	private void initName() {
		this.tableName = this.uri.replaceAll(".csv", "");
	}

	public Table(TreeNode node, String path) {
		this(path);
		getSectionsFrom(node);
	}
	
	protected abstract String initTableType();

	protected abstract void getSectionsFrom(TreeNode node);

	protected abstract Spreadsheet buildSpreadsheet();
	

	
}
