package org.insilico.sbmlsheets.core.compile;

import java.util.ArrayList;
import java.util.List;

import org.insilico.sbmlsheets.core.Constants;

import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

public class SBMLCompiler {
	
	List<Table> model;

	public SBMLCompiler(ObservableList<String> paths, ObservableMap<String, String> types) {
		this.model = new ArrayList<>();
		for (int i=0; i<paths.size(); i++) {
			model.add(chooseTableToCreate(types.get(paths.get(i)), paths.get(i)));
		}
	}

	private Table chooseTableToCreate(String type, String path) {
		Table object = null;
		switch (type) {
		case Constants.COMPARTMENT_TABLE:
			object = new CompartmentTable(path);
			break;
		case Constants.CONSTRAINTS_TABLE:
			object = new ConstraintsTable(path);
			break;
		case Constants.EVENTS_TABLE:
			object = new ObjectTable(path);
			break;
		case Constants.FUNCTION_DEF_TABLE:
			object = new FunctionDefTable(path);
			break;
		case Constants.INIT_ASSIGNMENTS_TABLE:
			object = new InitAssignmentsTable(path);
			break;
		case Constants.PARAMETERS_TABLE:
			object = new ParametersTable(path);
			break;
		case Constants.REACTIONS_TABLE:
			object = new ReactionsTable(path);
			break;
		case Constants.RULES_TABLE:
			object = new RulesTable(path);
			break;
		case Constants.SPECIES_TABLE:
			object = new SpeciesTable(path);
			break;
		case Constants.UNIT_DEF_TABLE:
			object = new UnitDefTable(path);
			break;
		default:
			System.err.println("Oh No! The specified table is not available!");
			break;
		}
		return object;
	}
	
}
