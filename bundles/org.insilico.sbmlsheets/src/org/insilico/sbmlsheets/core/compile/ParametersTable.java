package org.insilico.sbmlsheets.core.compile;

import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.TreeNode;

import org.insilico.sbmlsheets.core.Constants;
import org.insilico.sbmlsheets.core.Spreadsheet;
import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.Parameter;

public class ParametersTable extends Table {
	
	private ListOf<Parameter> sections;

	public ParametersTable(String path) {
		super(path);
	}

	public ParametersTable(String path, TreeNode treeNode) {
		super(path, treeNode);
	}

	@Override
	protected Spreadsheet buildSpreadsheet() {
		List<List<String>> data = new ArrayList<>();
		// TODO Auto-generated method stub
		List<String> row = new ArrayList<>();
		for (TreeNode node : this.sections) {
			Parameter para = ((Parameter) node);
			row.add(para.getId());
			row.add(para.getName());
			row.add(Double.toString(para.getValue()));
			row.add(para.getUnits());
			row.add("global parameter");
			row.add(Integer.toString(para.getSBOTerm()));
		}
		return new Spreadsheet(this.uri, this.tableType, this.tableName, data);
	}

	@Override
	protected String initTableType() {
		return Constants.PARAMETERS_TABLE;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void getSectionsFrom(TreeNode node) {
		this.sections = ((ListOf<Parameter>) node);
	}

}
