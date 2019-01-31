package org.insilico.sbmlsheets.core.compile;

import javax.swing.tree.TreeNode;

import org.insilico.sbmlsheets.core.Constants;
import org.insilico.sbmlsheets.core.Spreadsheet;
import org.sbml.jsbml.FunctionDefinition;
import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.Model;

public class FunctionDefTable extends Table {
	
	ListOf<FunctionDefinition> sections;

	public FunctionDefTable(Spreadsheet sheet) {
		super(sheet);
		sections = new ListOf<>();
	}

	public FunctionDefTable(String path, TreeNode treeNode) {
		super(path, treeNode);
	}

	@Override
	protected Spreadsheet buildSpreadsheet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String initTableType() {
		return Constants.FUNCTION_DEF_TABLE;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void getSectionsFrom(TreeNode node) {
		this.sections = ((ListOf<FunctionDefinition>) node);
	}

	@Override
	protected void addToSBMLModel(Model model) {
		// TODO Auto-generated method stub
		
	}

}
