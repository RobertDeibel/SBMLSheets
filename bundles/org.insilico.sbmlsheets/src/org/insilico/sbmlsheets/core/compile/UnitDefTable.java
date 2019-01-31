package org.insilico.sbmlsheets.core.compile;

import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.TreeNode;

import org.insilico.sbmlsheets.core.Constants;
import org.insilico.sbmlsheets.core.Row;
import org.insilico.sbmlsheets.core.Spreadsheet;
import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.Unit;
import org.sbml.jsbml.Unit.Kind;
import org.sbml.jsbml.UnitDefinition;

import javafx.beans.property.StringProperty;

public class UnitDefTable extends Table {
	
	ListOf<UnitDefinition> sections;

	public UnitDefTable(Spreadsheet sheet) {
		super(sheet);
		sections = new ListOf<>();
	}

	public UnitDefTable(String path, TreeNode treeNode) {
		super(path, treeNode);
	}

	@Override
	protected Spreadsheet buildSpreadsheet() {
		List<List<String>> data = new ArrayList<>();
		
		for (UnitDefinition unitDef : this.sections) {
			
			for (Unit unit : unitDef.getListOfUnits()) {
				List<String> row = new ArrayList<>();
				row.add(unitDef.getId());
				String name = (unitDef.getName().equals("") ? unitDef.getId() : unitDef.getName());
				row.add(name);
				row.add(unit.getKind().getName());
				row.add(Double.toString(unit.getExponent()));
				row.add(Double.toString(unit.getMultiplier()));
				row.add(Integer.toString(unit.getScale()));
				
				this.addRow(row, data);
			}
			
		}

		return new Spreadsheet(this.uri, this.tableType, this.tableName, data);
	}

	@Override
	protected String initTableType() {
		return Constants.UNIT_DEF_TABLE;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void getSectionsFrom(TreeNode node) {
		this.sections = ((ListOf<UnitDefinition>) node);
	}

	@Override
	protected void addToSBMLModel(Model model) {
		sections.setLevel(model.getLevel());
		sections.setVersion(model.getVersion());
		
		String currentId = sheet.getRow(0).getCell(0);
		UnitDefinition unitDef = new UnitDefinition();
		unitDef.setLevel(model.getLevel());
		unitDef.setVersion(model.getVersion());
		for (Row row : sheet.getData()) {
			if (row.isEmpty()) {
				continue;
			}
			Unit unit = new Unit();
			unit.setLevel(model.getLevel());
			unit.setVersion(model.getVersion());
			
			if (!row.getCell(0).equals(currentId)) {
				currentId = row.getCell(0);
				model.addUnitDefinition(unitDef);
				sections.add(unitDef);
				unitDef = new UnitDefinition();
				unitDef.setLevel(model.getLevel());
				unitDef.setVersion(model.getVersion());
			}
		
			for (StringProperty cell : row.getAllCells()) {
				switch (cell.getName()) {
				case "ID":
					if (!unitDef.isSetId()) {
						unitDef.setId(cell.getValue());
					}
					break;
				case "Name":
					if (!unitDef.isSetName()) {
						unitDef.setName(cell.getValue());
					}
					break;
				case "Kind":
					unit.setKind(Kind.valueOf(cell.getValue().toUpperCase()));
					break;
				case "Exponent":
					unit.setExponent(Double.parseDouble(cell.getValue()));
					break;
				case "Multiplier":
					unit.setMultiplier(Double.parseDouble(cell.getValue()));
					break;
				case "Scale":
					unit.setScale(Integer.parseInt(cell.getValue()));
					break;
				default:
					System.err.println("No Column Header matched!");;
				}
			}
			
			unitDef.addUnit(unit);
		}
		model.addUnitDefinition(unitDef);
		sections.add(unitDef);
	}

}
