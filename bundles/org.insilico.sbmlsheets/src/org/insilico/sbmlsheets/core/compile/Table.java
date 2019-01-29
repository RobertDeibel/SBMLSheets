package org.insilico.sbmlsheets.core.compile;

import java.io.File;
import java.util.ArrayList;
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
		String name = uri.split(File.separator)[uri.split(File.separator).length - 1].replace(".csv", "");
		this.tableName = name;
	}

	public Table(String path, TreeNode node) {
		this(path);
		getSectionsFrom(node);
	}
	
	protected void addRow(List<String> row, List<List<String>> data) {
		data.add(row);
	}
	
	protected abstract String initTableType();

	protected abstract void getSectionsFrom(TreeNode node);

	protected abstract Spreadsheet buildSpreadsheet();
	

	
}
