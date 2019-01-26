package org.insilico.sbmlsheets.core.compile;

import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.TreeNode;

import org.insilico.sbmlsheets.core.Constants;
import org.insilico.sbmlsheets.core.Spreadsheet;
import org.sbml.jsbml.Compartment;
import org.sbml.jsbml.ListOf;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CompartmentTable extends Table {
		

	public CompartmentTable(String path) {
		super(path);
	
	}

	public CompartmentTable(TreeNode treeNode, String path) {
		super(treeNode, path);	
		
	}

	@Override
	protected Spreadsheet buildSpreadsheet() {
		List<List<String>> data = new ArrayList<>();
		
		List<String> row = new ArrayList<>();
		for (TreeNode node : this.sections) {
			Compartment comp = (Compartment) node;
			
			row.add(comp.getId());
			row.add(comp.getName());
			row.add((comp.getSize() == Double.NaN) ? "NaN" : Double.toString(comp.getSize()) );
			row.add(comp.getUnits());
			row.add(comp.getSBOTermID());
			addRow(row, data);
		}
		
		
		
		return new Spreadsheet(data, this.tableType, this.tableName, this.uri);
		
	}

	private void addRow(List<String> row, List<List<String>> data) {
		for (int i=row.size(); i<data.size(); i++) {
			data.add(new ArrayList<>());
		}
		for (int i=0; i<row.size(); i++) {
			data.get(i).add(row.get(i));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void getSectionsFrom(TreeNode node) {
		this.sections = ((ListOf<Compartment>) node);		
	}

	@Override
	protected String initTableType() {
		return Constants.COMPARTMENT_TABLE;
	}



}
