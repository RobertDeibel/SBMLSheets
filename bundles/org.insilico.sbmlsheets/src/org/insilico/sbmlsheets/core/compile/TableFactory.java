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
	
	public static Table getTableFrom(TreeNode treeNode, String path) {
		TreeNode firstElem = treeNode.getChildAt(0);
		
		if (firstElem instanceof Compartment) {
			path += CompartmentTable.class.getSimpleName()+".csv";
			return new CompartmentTable(path, treeNode);
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
			path += ParametersTable.class.getSimpleName()+".csv";
			return new ParametersTable(path, treeNode);
		}
		if (firstElem instanceof Reaction) {
			path += ReactionsTable.class.getSimpleName()+".csv";
			return new ReactionsTable(path, treeNode);
		}
		if (firstElem instanceof Rule) {
			return new RulesTable(treeNode, RulesTable.class.getSimpleName()+".csv");
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


}
