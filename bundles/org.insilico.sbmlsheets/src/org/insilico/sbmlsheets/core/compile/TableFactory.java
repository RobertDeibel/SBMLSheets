package org.insilico.sbmlsheets.core.compile;

import javax.swing.tree.TreeNode;

import org.insilico.sbmlsheets.core.Constants;
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
	
	public static Table getTableFrom(TreeNode treeNode) {
		TreeNode firstElem = treeNode.getChildAt(0);
		
		if (firstElem instanceof Compartment) {
			return new CompartmentTable(treeNode, CompartmentTable.class.getSimpleName()+".csv");
		}
		if (firstElem instanceof Constraint) {
			return new ConstraintsTable(treeNode, ConstraintsTable.class.getSimpleName()+".csv");
		}
		if (firstElem instanceof Event) {
			return new EventTable(treeNode, EventTable.class.getSimpleName()+".csv");
		}
		if (firstElem instanceof FunctionDefinition) {
			return new FunctionDefTable(treeNode, FunctionDefTable.class.getSimpleName()+".csv");
		}
		if (firstElem instanceof InitialAssignment) {
			return new InitAssignmentsTable(treeNode, InitAssignmentsTable.class.getSimpleName()+".csv");
		}
		if (firstElem instanceof Parameter) {
			return new ParametersTable(treeNode, ParametersTable.class.getSimpleName()+".csv");
		}
		if (firstElem instanceof Reaction) {
			return new ReactionsTable(treeNode, ReactionsTable.class.getSimpleName()+".csv");
		}
		if (firstElem instanceof Rule) {
			return new RulesTable(treeNode, RulesTable.class.getSimpleName()+".csv");
		}
		if (firstElem instanceof Species) {
			return new SpeciesTable(treeNode, SpeciesTable.class.getSimpleName()+".csv");
		}
		if (firstElem instanceof UnitDefinition) {
			return new UnitDefTable(treeNode, UnitDefTable.class.getSimpleName()+".csv");
		}
		return null;
		
	}

	private static Table tableFromIdentifier(String identifier) {
		Table object = null;
		switch (identifier) {
		case Constants.COMPARTMENT_ID:
			object = new CompartmentTable(CompartmentTable.class.getSimpleName()+".csv");
			break;
		case Constants.CONSTRAINTS_ID:
			object = new ConstraintsTable(ConstraintsTable.class.getSimpleName()+".csv");
			break;
		case Constants.EVENTS_ID:
			object = new EventTable(EventTable.class.getSimpleName()+".csv");
			break;
		case Constants.FUNCTION_DEF_ID:
			object = new FunctionDefTable(FunctionDefTable.class.getSimpleName()+".csv");
			break;
		case Constants.INIT_ASSIGNMENTS_ID:
			object = new InitAssignmentsTable(InitAssignmentsTable.class.getSimpleName()+".csv");
			break;
		case Constants.PARAMETERS_ID:
			object = new ParametersTable(ParametersTable.class.getSimpleName()+".csv");
			break;
		case Constants.REACTIONS_ID:
			object = new ReactionsTable(ReactionsTable.class.getSimpleName()+".csv");
			break;
		case Constants.RULES_ID:
			object = new RulesTable(RulesTable.class.getSimpleName()+".csv");
			break;
		case Constants.SPECIES_ID:
			object = new SpeciesTable(SpeciesTable.class.getSimpleName()+".csv");
			break;
		case Constants.UNIT_DEF_ID:
			object = new UnitDefTable(UnitDefTable.class.getSimpleName()+".csv");
			break;
		default:
			System.err.println("Oh No! The specified table is not available!");
			break;
		}
		return object;
	}

}
