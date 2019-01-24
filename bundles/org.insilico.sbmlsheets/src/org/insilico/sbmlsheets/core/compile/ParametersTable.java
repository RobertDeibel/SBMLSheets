package org.insilico.sbmlsheets.core.compile;

import javax.swing.tree.TreeNode;

import org.insilico.sbmlsheets.core.Spreadsheet;

public class ParametersTable extends Table {

	public ParametersTable(String path) {
		super(path);
	}

	public ParametersTable(TreeNode treeNode, String path) {
		super(path);
	}

	@Override
	protected Spreadsheet buildSpreadsheet() {
		// TODO Auto-generated method stub
		return null;
	}

}
