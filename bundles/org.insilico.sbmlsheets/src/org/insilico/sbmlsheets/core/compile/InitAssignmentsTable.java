package org.insilico.sbmlsheets.core.compile;

import javax.swing.tree.TreeNode;

import org.insilico.sbmlsheets.core.Constants;
import org.insilico.sbmlsheets.core.Spreadsheet;
import org.sbml.jsbml.InitialAssignment;
import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.Model;

public class InitAssignmentsTable extends Table {
	
	ListOf<InitialAssignment> sections;

	public InitAssignmentsTable(Spreadsheet sheet) {
		super(sheet);
		sections = new ListOf<>();
	}

	public InitAssignmentsTable(String path, TreeNode treeNode) {
		super(path, treeNode);
	}

	@Override
	protected Spreadsheet buildSpreadsheet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String initTableType() {
		return Constants.INIT_ASSIGNMENTS_TABLE;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void getSectionsFrom(TreeNode node) {
		this.sections = ((ListOf<InitialAssignment>) node);
	}

	@Override
	protected void addToSBMLModel(Model model) {
		// TODO Auto-generated method stub
		
	}

}
