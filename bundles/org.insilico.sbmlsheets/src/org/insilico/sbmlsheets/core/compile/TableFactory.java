package org.insilico.sbmlsheets.core.compile;

import javax.swing.tree.TreeNode;

import org.insilico.sbmlsheets.core.Constants;
import org.insilico.sbmlsheets.core.Spreadsheet;
import org.sbml.jsbml.Compartment;
import org.sbml.jsbml.Constraint;
import org.sbml.jsbml.Event;
import org.sbml.jsbml.FunctionDefinition;
import org.sbml.jsbml.InitialAssignment;
import org.sbml.jsbml.Parameter;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.Rule;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.UnitDefinition;
import org.w3c.dom.Node;

public final class TableFactory {
	/**
	 * Supressed constructor of the class
	 */
	private TableFactory() {}
	
	public static Table getTableFrom(TreeNode treeNode, String path) {
		TreeNode firstElem = null;
		if (treeNode.getChildCount() > 0) {
			 firstElem = treeNode.getChildAt(0);
		} else {
			return null;
		}
		
		if (firstElem instanceof Compartment) {
			path += CompartmentTable.class.getSimpleName()+".csv";
			return new CompartmentTable(path, treeNode);
		}
		if (firstElem instanceof Constraint) {
			//TODO
			path += ConstraintsTable.class.getSimpleName()+".csv";
			return new ConstraintsTable(path, treeNode);
		}
		if (firstElem instanceof Event) {
			//TODO
			path += EventTable.class.getSimpleName()+".csv";
			return new EventTable(path, treeNode);
		}
		if (firstElem instanceof FunctionDefinition) {
			//TODO
			path += FunctionDefTable.class.getSimpleName()+".csv";
			return new FunctionDefTable(path, treeNode);
		}
		if (firstElem instanceof InitialAssignment) {
			//TODO
			path += InitAssignmentsTable.class.getSimpleName()+".csv";
			return new InitAssignmentsTable(path, treeNode);
		}
		if (firstElem instanceof Parameter) {
			path += ParametersTable.class.getSimpleName()+".csv";
			return new ParametersTable(path, treeNode);
		}
		if (firstElem instanceof Reaction) {
			path += ReactionsTable.class.getSimpleName()+".csv";
			return new ReactionsTable(path, treeNode);
		}
		if (firstElem instanceof Rule) {
			//TODO
			path += RulesTable.class.getSimpleName()+".csv";
			return new RulesTable(path, treeNode);
		}
		if (firstElem instanceof Species) {
			path += SpeciesTable.class.getSimpleName()+".csv";
			return new SpeciesTable(path, treeNode);
		}
		if (firstElem instanceof UnitDefinition) {
			path += UnitDefTable.class.getSimpleName()+".csv";
			return new UnitDefTable(path, treeNode);
		}
		return null;
		
	}

	public static Table getTableFrom(Spreadsheet sheet) {
		switch (sheet.getTableType()) {
		case Constants.COMPARTMENT_TABLE:
			return new CompartmentTable(sheet);
		case Constants.CONSTRAINTS_TABLE:
			return new ConstraintsTable(sheet);
		case Constants.EVENTS_TABLE:
			return new EventTable(sheet);
		case Constants.FUNCTION_DEF_TABLE:
			return new FunctionDefTable(sheet);
		case Constants.INIT_ASSIGNMENTS_TABLE:
			return new InitAssignmentsTable(sheet);
		case Constants.PARAMETERS_TABLE:
			return new ParametersTable(sheet);
		case Constants.REACTIONS_TABLE:
			return new ReactionsTable(sheet);
		case Constants.RULES_TABLE:
			return new RulesTable(sheet);
		case Constants.SPECIES_TABLE:
			return new SpeciesTable(sheet);
		case Constants.UNIT_DEF_TABLE:
			return new UnitDefTable(sheet);
		default:
			System.err.println("Oh No! The specified table is not available!");
			break;
		}
		return null;
	}


}
