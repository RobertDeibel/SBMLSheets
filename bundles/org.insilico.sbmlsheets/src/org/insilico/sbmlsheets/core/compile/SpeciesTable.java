package org.insilico.sbmlsheets.core.compile;

import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.TreeNode;

import org.insilico.sbmlsheets.core.Spreadsheet;
import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.Species;

import javafx.beans.property.StringProperty;

import org.insilico.sbmlsheets.core.Constants;
import org.insilico.sbmlsheets.core.Row;
public class SpeciesTable extends Table {
	
	ListOf<Species> sections;

	public SpeciesTable(Spreadsheet sheet) {
		super(sheet);
		sections = new ListOf<>();
	}

	public SpeciesTable(String path, TreeNode treeNode) {
		super(path, treeNode);
	}

	@Override
	protected Spreadsheet buildSpreadsheet() {
		List<List<String>> data = new ArrayList<>();

		
		for (Species spec : this.sections) {
			List<String> row = new ArrayList<>();
			
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

	@Override
	protected void addToSBMLModel(Model model) {
		sections.setLevel(model.getLevel());
		sections.setVersion(model.getVersion());
		
		for (Row row : sheet.getData()) {
			if (row.isEmpty()) {
				continue;
			}
			Species species = new Species();
			species.setLevel(model.getLevel());
			species.setVersion(model.getVersion());
			
			for (StringProperty cell : row) {
				switch (cell.getName()) {
				case "ID":
					species.setId(cell.getValue());
					break;
				case "Name":
					species.setName(cell.getValue());
					break;
				case "Compartment":
					species.setCompartment(cell.getValue());
					break;
				case "IsConstant":
					species.setConstant(Boolean.parseBoolean(cell.getValue()));
					break;
				case "SBOTerm":
					if(! (cell.getValue().equals("") || Integer.parseInt(cell.getValue()) < 0)) {
						species.setSBOTerm(Integer.parseInt(cell.getValue()));
					}
					break;
				case "InitialConcentration":
					if (!cell.getValue().equals("NaN")) {
						species.setInitialConcentration(Double.parseDouble(cell.getValue()));
					}
					break;
				case "hasOnlySubstanceUnits":
					species.setHasOnlySubstanceUnits(Boolean.parseBoolean(cell.getValue()));
					break;
				default:
					System.err.println("No Column Header matched!");
					break;
				}
			}
			sections.add(species);
		}
		model.setListOfSpecies(sections);
	}

}
