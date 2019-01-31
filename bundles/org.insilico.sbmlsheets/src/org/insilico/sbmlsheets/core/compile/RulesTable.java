package org.insilico.sbmlsheets.core.compile;

import javax.swing.tree.TreeNode;

import org.insilico.sbmlsheets.core.Constants;
import org.insilico.sbmlsheets.core.Spreadsheet;
import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.Rule;

public class RulesTable extends Table {
	
	ListOf<Rule> sections;

	public RulesTable(Spreadsheet sheet) {
		super(sheet);
		sections = new ListOf<>();
	}

	public RulesTable(String path, TreeNode treeNode) {
		super(path, treeNode);
	}

	@Override
	protected Spreadsheet buildSpreadsheet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String initTableType() {
		return Constants.RULES_TABLE;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void getSectionsFrom(TreeNode node) {
		this.sections = ((ListOf<Rule>) node);
	}

	@Override
	protected void addToSBMLModel(Model model) {
		// TODO Auto-generated method stub
		
	}

}
