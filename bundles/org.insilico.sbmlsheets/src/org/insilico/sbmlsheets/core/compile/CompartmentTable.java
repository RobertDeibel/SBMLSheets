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

	public CompartmentTable(String path, TreeNode treeNode) {
		super(path, treeNode);	
		
	}

	@Override
	protected Spreadsheet buildSpreadsheet() {
		List<List<String>> data = new ArrayList<>();
		
		
		for (TreeNode node : this.sections) {
			List<String> row = new ArrayList<>();
			Compartment comp = (Compartment) node;
			
			row.add(comp.getId());
			row.add(comp.getName());
			row.add((comp.getSize() == Double.NaN) ? "NaN" : Double.toString(comp.getSize()) );
			row.add(comp.getUnits());
			row.add(comp.getSBOTermID());
			addRow(row, data);
		}
		
		return new Spreadsheet(this.uri, this.tableType, this.tableName, data);
		
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
