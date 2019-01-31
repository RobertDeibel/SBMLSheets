package org.insilico.sbmlsheets.core.compile;

import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.TreeNode;

import org.insilico.sbmlsheets.core.Constants;
import org.insilico.sbmlsheets.core.Row;
import org.insilico.sbmlsheets.core.Spreadsheet;
import org.sbml.jsbml.Compartment;
import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.Model;

import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CompartmentTable extends Table {
	
	ListOf<Compartment> sections;
		

	public CompartmentTable(Spreadsheet sheet) {
		super(sheet);
		sections = new ListOf<>();
	
	}

	public CompartmentTable(String path, TreeNode treeNode) {
		super(path, treeNode);	
		
	}

	@Override
	protected Spreadsheet buildSpreadsheet() {
		List<List<String>> data = new ArrayList<>();
		
		
		for (Compartment comp : this.sections) {
			List<String> row = new ArrayList<>();
			
			row.add(comp.getId());
			row.add(comp.getName());
			row.add((comp.getSize() == Double.NaN) ? "NaN" : Double.toString(comp.getSize()) );
			row.add(comp.getUnits());
			row.add(Integer.toString(comp.getSBOTerm()));
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

	@Override
	protected void addToSBMLModel(Model model) {
		sections.setLevel(model.getLevel());
		sections.setVersion(model.getVersion());
		for (Row row : sheet.getData()) {
			if (row.isEmpty()) {
				continue;
			}
			Compartment compartment = new Compartment();
			compartment.setLevel(model.getLevel());
			compartment.setVersion(model.getVersion());
			
			for(StringProperty cell : row.getAllCells()) {
				switch (cell.getName()) {
				case "ID":
					compartment.setId(cell.getValue());
					break;
				case "Name":
					compartment.setName(cell.getValue());
					break;
				case "Size":
					compartment.setSize(Double.parseDouble(cell.getValue()));
					break;
				case "Unit":
					compartment.setUnits(cell.getValue());
					break;
				case "SBOTerm":
					if(! (cell.getValue().equals("") || Integer.parseInt(cell.getValue()) < 0)) {
						compartment.setSBOTerm(Integer.parseInt(cell.getValue()));
					}
					break;
				default:
					System.err.println("No Column Head matched!");
				}
			}
			
			sections.add(compartment);
		}
		
		model.setListOfCompartments(sections);
	}





}
