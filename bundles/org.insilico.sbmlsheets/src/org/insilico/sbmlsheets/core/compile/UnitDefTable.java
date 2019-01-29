package org.insilico.sbmlsheets.core.compile;

import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.TreeNode;

import org.insilico.sbmlsheets.core.Constants;
import org.insilico.sbmlsheets.core.Spreadsheet;
import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.Unit;
import org.sbml.jsbml.UnitDefinition;

public class UnitDefTable extends Table {

	public UnitDefTable(String path) {
		super(path);
	}

	public UnitDefTable(String path, TreeNode treeNode) {
		super(path, treeNode);
	}

	@Override
	protected Spreadsheet buildSpreadsheet() {
		List<List<String>> data = new ArrayList<>();
		
		//TODO
		for (TreeNode node : this.sections) {
			
			UnitDefinition unitDef = ((UnitDefinition) node);
			
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
		System.out.println(data);

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

}
