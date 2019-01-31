package org.insilico.sbmlsheets.core.compile;

import java.io.File;
import java.util.List;

import javax.swing.tree.TreeNode;

import org.insilico.sbmlsheets.core.Spreadsheet;
import org.sbml.jsbml.Model;

public abstract class Table {
	
	protected final String uri;
	
	protected String tableName;
	
	protected final String tableType = initTableType();
	
	protected Spreadsheet sheet;
		
	private Table(String path) {
		this.uri = path;
		initName();
	}
	
	public Table(Spreadsheet sheet) {
		this(sheet.getFileLocation());
		
		this.sheet = sheet;
	}
	
	public Table(String path, TreeNode node) {
		this(path);
		getSectionsFrom(node);
	}
	
	private void initName() {
		String name = uri.split(File.separator)[uri.split(File.separator).length - 1].replace(".csv", "");
		this.tableName = name;
	}

	protected void addRow(List<String> row, List<List<String>> data) {
		data.add(row);
	}
	
	protected abstract String initTableType();

	protected abstract void getSectionsFrom(TreeNode node);

	protected abstract Spreadsheet buildSpreadsheet();
	
	protected abstract void addToSBMLModel(Model model);
	
}
