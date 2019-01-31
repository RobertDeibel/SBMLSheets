package org.insilico.sbmlsheets.core.compile;

import javax.swing.tree.TreeNode;

import org.insilico.sbmlsheets.core.Constants;
import org.insilico.sbmlsheets.core.Spreadsheet;
import org.sbml.jsbml.Event;
import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.Model;

public class EventTable extends Table {
	
	ListOf<Event> sections;

	public EventTable(Spreadsheet sheet) {
		super(sheet);
		sections = new ListOf<>();
	}

	public EventTable(String path, TreeNode treeNode) {
		// TODO Auto-generated constructor stub
		super(path,treeNode);
	}

	@Override
	protected Spreadsheet buildSpreadsheet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String initTableType() {
		return Constants.EVENTS_TABLE;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void getSectionsFrom(TreeNode node) {
		this.sections = ((ListOf<Event>) node);
	}

	@Override
	protected void addToSBMLModel(Model model) {
		// TODO Auto-generated method stub
		
	}

}
