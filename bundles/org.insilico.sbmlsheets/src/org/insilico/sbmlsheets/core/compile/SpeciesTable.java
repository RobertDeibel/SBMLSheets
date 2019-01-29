package org.insilico.sbmlsheets.core.compile;

import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.TreeNode;

import org.insilico.sbmlsheets.core.Spreadsheet;
import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.Species;

import javafx.beans.property.StringProperty;

import org.insilico.sbmlsheets.core.Constants;
public class SpeciesTable extends Table {

	public SpeciesTable(String path) {
		super(path);
	}

	public SpeciesTable(String path, TreeNode treeNode) {
		super(path, treeNode);
	}

	@Override
	protected Spreadsheet buildSpreadsheet() {
		List<List<String>> data = new ArrayList<>();

		
		for (TreeNode node : this.sections) {
			List<String> row = new ArrayList<>();
			Species spec = ((Species) node);
			
			row.add(spec.getId());
			row.add(spec.getName());
			row.add(spec.getCompartment());
			row.add(Boolean.toString(spec.getConstant()));
			row.add(Integer.toString(spec.getSBOTerm()));
			row.add(Double.toString(spec.getInitialConcentration()));
			row.add(Boolean.toString(spec.getHasOnlySubstanceUnits()));
			
			this.addRow(row, data);			
		}
		
		return new Spreadsheet(this.uri, this.tableType, this.tableName, data);
	}

	@Override
	protected String initTableType() {
		return Constants.SPECIES_TABLE;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void getSectionsFrom(TreeNode node) {
		this.sections = ((ListOf<Species>) node);		
	}

}
