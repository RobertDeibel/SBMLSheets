package org.insilico.sbmlsheets.core.compile;

import javax.swing.tree.TreeNode;

import org.insilico.sbmlsheets.core.Spreadsheet;

public class SpeciesTable extends Table {

	public SpeciesTable(String path) {
		super(path);
	}

	public SpeciesTable(TreeNode treeNode, String path) {
		super(path);
	}

	@Override
	protected Spreadsheet buildSpreadsheet() {
		// TODO Auto-generated method stub
		return null;
	}

}
