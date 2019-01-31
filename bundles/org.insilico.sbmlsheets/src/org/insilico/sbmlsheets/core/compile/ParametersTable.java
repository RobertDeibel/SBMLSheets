package org.insilico.sbmlsheets.core.compile;

import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.TreeNode;

import org.insilico.sbmlsheets.core.Constants;
import org.insilico.sbmlsheets.core.Row;
import org.insilico.sbmlsheets.core.Spreadsheet;
import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.Parameter;

import javafx.beans.property.StringProperty;

public class ParametersTable extends Table {
	
	private ListOf<Parameter> sections;

	public ParametersTable(Spreadsheet sheet) {
		super(sheet);
		sections = new ListOf<>();
	}

	public ParametersTable(String path, TreeNode treeNode) {
		super(path, treeNode);
	}

	@Override
	protected Spreadsheet buildSpreadsheet() {
		List<List<String>> data = new ArrayList<>();

		for (Parameter para : this.sections) {
			List<String> row = new ArrayList<>();

			row.add(para.getId());
			row.add(getParameterName(para));
			row.add(Double.toString(para.getValue()));
			row.add(para.getUnits());
			row.add("global parameter");
			row.add(Integer.toString(para.getSBOTerm()));
			
			this.addRow(row, data);
		}
		return new Spreadsheet(this.uri, this.tableType, this.tableName, data);
	}

	private String getParameterName(Parameter para) {
		return para.isSetName() ? para.getName() : para.getId();
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

	@Override
	protected void addToSBMLModel(Model model) {
		sections.setLevel(model.getLevel());
		sections.setVersion(model.getVersion());
		
		for (Row row : sheet.getData()) {
			if (row.isEmpty()) {
				continue;
			}
			Parameter parameter = new Parameter();
			parameter.setLevel(model.getLevel());
			parameter.setVersion(model.getVersion());
			
			for (StringProperty cell : row.getAllCells()) {
				switch (cell.getName()) {
				case "ID":
					parameter.setId(cell.getValue());
					break;
				case "Name":
					parameter.setName(cell.getValue());
					break;
				case "Value":
					parameter.setValue(Double.parseDouble(cell.getValue()));
					break;
				case "Unit":
					parameter.setUnits(cell.getValue());
					break;
				case "SBOTerm":
					if(! (cell.getValue().equals("") || Integer.parseInt(cell.getValue()) < 0)) {
						parameter.setSBOTerm(Integer.parseInt(cell.getValue()));
					}
					break;
				default:
					System.err.println("No Column Header matched!");
					break;
				}
			}
			sections.add(parameter);
		}
		model.setListOfParameters(sections);
	}

}
