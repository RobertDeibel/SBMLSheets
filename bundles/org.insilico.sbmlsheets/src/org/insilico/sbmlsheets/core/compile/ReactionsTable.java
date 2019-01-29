package org.insilico.sbmlsheets.core.compile;

import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.TreeNode;

import org.insilico.sbmlsheets.core.Constants;
import org.insilico.sbmlsheets.core.Spreadsheet;
import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.SpeciesReference;

public class ReactionsTable extends Table {
	
	private final String REACTION_SEPERATOR = "<=>";

	public ReactionsTable(String path) {
		super(path);
	}

	public ReactionsTable(String path, TreeNode treeNode) {
		super(path, treeNode);
	}

	@Override
	protected Spreadsheet buildSpreadsheet() {
		List<List<String>> data = new ArrayList<>();
		
		// TODO Auto-generated method stub		
		for (TreeNode node : this.sections) {
			List<String> row = new ArrayList<>();
			
			Reaction reaction = ((Reaction) node);
			
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
		return new Spreadsheet(this.uri, this.tableType, this.tableName, data);
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
		for (SpeciesReference reference : references) {
			target += ((reference.getStoichiometry() != 1) ?  " "+Double.toString(reference.getStoichiometry()) : "");
			target += " "+ reference.getSpecies();
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

}
