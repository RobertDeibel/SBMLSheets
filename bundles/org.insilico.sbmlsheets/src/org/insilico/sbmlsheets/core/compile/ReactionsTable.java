package org.insilico.sbmlsheets.core.compile;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.tree.TreeNode;

import org.insilico.sbmlsheets.core.Constants;
import org.insilico.sbmlsheets.core.Row;
import org.insilico.sbmlsheets.core.Spreadsheet;
import org.sbml.jsbml.Compartment;
import org.sbml.jsbml.KineticLaw;
import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.SpeciesReference;
import org.sbml.jsbml.text.parser.ParseException;

import javafx.beans.property.StringProperty;

public class ReactionsTable extends Table {
	
	ListOf<Reaction> sections;
	
	private final String REACTANT_SEPERATOR = " + ";
	private final String REACTION_SEPERATOR = " <=> ";

	public ReactionsTable(Spreadsheet sheet) {
		super(sheet);
		sections = new ListOf<>();
	}

	public ReactionsTable(String path, TreeNode treeNode) {
		super(path, treeNode);
	}

	@Override
	protected Spreadsheet buildSpreadsheet() {
		List<List<String>> data = new ArrayList<>();
		
		for (Reaction reaction : this.sections) {
			List<String> row = new ArrayList<>();
			
			row.add(reaction.getId());
			row.add(reaction.getName());
			row.add(getReactionFormula(reaction));
			row.add(reaction.getCompartment());
			row.add(getRegulator(reaction));
			row.add(reaction.getKineticLaw().getMath().toFormula());
			row.add(Integer.toString(reaction.getSBOTerm()));
			row.add(Boolean.toString(reaction.getReversible()));
			
			this.addRow(row, data);
		}
		this.sheet = new Spreadsheet(this.uri, this.tableType, this.tableName, data);
		return this.sheet;
	}

	private String getRegulator(Reaction reaction) {
		String regulatorSequence = "";
		for (int i=0; i<reaction.getNumModifiers(); i++) {
			regulatorSequence += reaction.getModifier(i).getId();
			//TODO
		}
		return "";
	}

	private String getReactionFormula(Reaction reaction) {
		String reactionFormula = "";

		reactionFormula = appendSpeciesReferencesAndStochemety(reaction.getListOfReactants(), reactionFormula);
		reactionFormula += this.REACTION_SEPERATOR;
		reactionFormula = appendSpeciesReferencesAndStochemety(reaction.getListOfProducts(), reactionFormula);
		
		return reactionFormula;
	}
	
	/**
	 * Appends all {@link SpeciesReference}s in {@code reference} to {@code target} with their corresponding stocheometry
	 * @param references {@link SpeciesReferences} found in a {@link Reaction}
	 * @param target String to append the references to
	 * @return {@code target} with appended references and stocheometry
	 */
	private String appendSpeciesReferencesAndStochemety(ListOf<SpeciesReference> references, String target) {
		for (Iterator<SpeciesReference> iterator = references.iterator(); iterator.hasNext();) {
			SpeciesReference reference = (SpeciesReference) iterator.next();
			target += ((reference.getStoichiometry() != 1) ?  Double.toString(reference.getStoichiometry())+" " : "");
			target += reference.getSpecies();
			if (iterator.hasNext()) {
				target += this.REACTANT_SEPERATOR;
			}
		}
		return target;
	}

	@Override
	protected String initTableType() {
		return Constants.REACTIONS_TABLE;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void getSectionsFrom(TreeNode node) {
		this.sections = ((ListOf<Reaction>) node);
	}

	@Override
	protected void addToSBMLModel(Model model) {
		sections.setLevel(model.getLevel());
		sections.setVersion(model.getVersion());
		
		for (Row row : sheet.getData()) {
			if (row.isEmpty()) {
				continue;
			}
			Reaction reaction = new Reaction();
			reaction.setLevel(model.getLevel());
			reaction.setVersion(model.getVersion());
			
			for (StringProperty cell : row.getAllCells()) {
				switch (cell.getName()) {
				case "ID":
					reaction.setId(cell.getValue());
					break;
				case "Name":
					reaction.setName(cell.getValue());
					break;
				case "ReactionFormula":
					reaction.setListOfReactants(makeSpeciesRef(model.getListOfSpecies(), cell.getValue(), true));
					reaction.setListOfProducts(makeSpeciesRef(model.getListOfSpecies(), cell.getValue(), false));
					break;
				case "Location":
					if (reaction.getLevel() > 2) {
						reaction.setCompartment(getCompartmentWithId(cell.getValue(), model.getListOfCompartments()));
					}
					break;
				case "Regulator":
					reaction.setListOfModifiers(null);
					break;
				case "KineticLaw":
					reaction.setKineticLaw(makeKineticLaw(cell.getValue()));
					break;
				case "SBOTerm":
					if(! (cell.getValue().equals("") || Integer.parseInt(cell.getValue()) < 0)) {
						reaction.setSBOTerm(Integer.parseInt(cell.getValue()));
					}
					break;
				case "IsReversible":
					reaction.setReversible(Boolean.parseBoolean(cell.getValue()));
					break;
				default:
					System.err.println("No Column Header matched!");;
				}
			}
			
			sections.add(reaction);
		}
		model.setListOfReactions(sections);
		
	}

	private KineticLaw makeKineticLaw(String value) {
		KineticLaw law = new KineticLaw();
		try {
			law.setFormula(value);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return law;
	}

	private Compartment getCompartmentWithId(String id, ListOf<Compartment> listOfCompartments) {
		for (Compartment compartment : listOfCompartments) {
			if (compartment.getId().equals(id)) {
				return compartment;
			}
		}
		return null;
	}

	private ListOf<SpeciesReference> makeSpeciesRef(ListOf<Species> listOfSpecies, String reactionFormula, boolean reactants) {
		String eductProduct = reactionFormula.split(this.REACTION_SEPERATOR)[reactants ? 0 : 1]
											.trim();
		ListOf<SpeciesReference> result = new ListOf<>();
		result.setLevel(listOfSpecies.getLevel());
		result.setVersion(listOfSpecies.getVersion());
		if (eductProduct.equals("")) {
			return null;
		}
		if (eductProduct.contains(this.REACTANT_SEPERATOR)) {
			for (String stocheometryAndId : eductProduct.split(" \\+ ")) {
				stocheometryAndId = stocheometryAndId.trim();
				setStocheometryAndId(listOfSpecies, stocheometryAndId, result);
			}
		} else {
			eductProduct = eductProduct.trim();
			setStocheometryAndId(listOfSpecies, eductProduct, result);
		}
		
		return result;
	}
	
	private void setStocheometryAndId(ListOf<Species> listOfSpecies, String stocheometryAndId, ListOf<SpeciesReference> result) {
		if (stocheometryAndId.split(" ").length > 1) {	
			String stocheometry = stocheometryAndId.split(" ")[0];
			String id = stocheometryAndId.split(" ")[1];
			for (Species species : listOfSpecies) {
				if (species.getId().equals(id)) {
					SpeciesReference ref = new SpeciesReference(species);
					ref.setLevel(result.getLevel());
					ref.setVersion(result.getVersion());
					ref.setStoichiometry(Double.parseDouble(stocheometry));	
					result.add(ref);
				}
			}
			
		} else {
			String id = stocheometryAndId;
			for (Species species : listOfSpecies) {
				if (species.getId().equals(id)) {
					SpeciesReference ref = new SpeciesReference(species);
					ref.setLevel(result.getLevel());
					ref.setVersion(result.getVersion());
					ref.setStoichiometry(1);	
					result.add(ref);
				}
			}
		}
	}
	

}
