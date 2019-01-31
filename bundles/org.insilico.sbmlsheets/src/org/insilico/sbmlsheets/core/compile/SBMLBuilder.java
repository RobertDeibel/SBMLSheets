package org.insilico.sbmlsheets.core.compile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.insilico.sbmlsheets.core.Constants;
import org.insilico.sbmlsheets.core.SheetProject;
import org.insilico.sbmlsheets.core.SheetReader;
import org.insilico.sbmlsheets.core.Spreadsheet;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLException;
import org.sbml.jsbml.SBMLReader;
import org.sbml.jsbml.xml.stax.SBMLWriter;

import javafx.collections.ObservableList;
import java.io.File;
import java.io.IOException;

import javax.swing.tree.TreeNode;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Class for reading and writing/building SBML files
 * @author Robert Deibel
 *
 */
public class SBMLBuilder {
	
	/**
	 * The {@link SheetProject} where {@link SBMLBuilder} is called from.
	 * Provides the information to build the SBML file or is written to if an SBML file is read.
	 */
	private SheetProject project;
		
	/**
	 * An {@link ObservableList} of {@link Table}s. These {@link Table}s define the children of the "model" node in 
	 * SBML.
	 */
	List<Table> model;
	
	
	public SBMLBuilder(SheetProject project) {
		
		this.project = project;
		this.model = new ArrayList<>();
		
	}
	
	/**
	 * Reads the {@link File}, converts it into an {@link SBMLDocument} and constructs the tables representing this SBML model.
	 * @param file The SBML file
	 */
	public void read(File file) {
		
		try {
			String path = project.getUri();
			
			project.setSbmlFileName(file.getName());
			
			SBMLDocument doc = SBMLReader.read(file);
			
			project.setSpecification(doc.getNamespace());
			
			Model model = doc.getModel();
			
			String sheetPath = path.replace(path.split(File.separator)[path.split(File.separator).length - 1], "");

			List<Spreadsheet> sheets = buildSheets(model, sheetPath);
			
			for (Spreadsheet sheet : sheets) {
				if (sheet != null) {
					project.addPathNameType(sheet.getFileLocation(), sheet.getTableName(), sheet.getTableType());
					sheet.save();
				}
			}
			
			
			
//			DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance()
//										.newDocumentBuilder();
//			Document doc = docBuilder.parse(file);
			
			//TODO Validation
			
//			if(doc.checkConsistency() == 0) {
//				System.out.println("Consistent");
//				buildFiles(doc.getFirstChild().getChildNodes());
//			}
			
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			System.err.println("Da ist ein Sheet Null");
		}
		
		
	}
	
	/**
	 * Returns a {@link List} of {@link Spreadsheet}s bulilt from the provided {@link Model} the 
	 * {@link Spreadsheet}s are initialized with {@code path} as their uri.
	 * @param model The {@link Model} for building of the {@link Spreadsheet}s
	 * @param path The uri for the {@link Spreadsheet}s
	 * @return a {@link List} of {@link Spreadsheet}s
	 */
	private List<Spreadsheet> buildSheets(Model model, String path) {
		List<Spreadsheet> sheets = new ArrayList<>();
		for (int i=0; i<model.getChildCount(); i++) {
			sheets.add(build(model.getChildAt(i), path));
		}
		return sheets;
	}
	
	/**
	 * Returns a {@link Spreadsheet} built from the {@code treeNode} and initialized with {@code path} as its uri
	 * @param treeNode The {@link TreeNode} to build the {@link Spreadsheet}. Must be a {@link ListOf} of some SBML model
	 * @param path The uri for the {@link Spreadsheet}
	 * @return A {@link Spreadsheet} built from the {@code treeNode}
	 */
	private Spreadsheet build(TreeNode treeNode, String path) {
		Table tab = TableFactory.getTableFrom(treeNode, path);
		if (tab != null) {
			return tab.buildSpreadsheet();
		}
		return null;
		
	}

	/**
	 * Checks if the {@link Document}s first two {@link Nodes} have the desired {@code name}s.
	 * The first {@link Node} hast to be named "sbml" the second "model" to be a valid SBMLFile.
	 * @param doc The document to be checked
	 * @return {@link true} if the {@link Document} is a valid SBML document.
	 */
	private boolean checkForSBML(Document doc) {
		String firstNodeName = doc.getDocumentElement().getNodeName();
		String secondNodeName = doc.getFirstChild().getNodeName();
		return firstNodeName.equals("sbml") && secondNodeName.equals("model");
	}

	/**
	 * Builds the SBML representation and returns it as a String
	 * @return The compiled SBML representation of the {@link #model}
	 */
	public void compile() {
		//TODO
		System.out.println("TODO compile");
		String sbmlPath = project.getUri().replace(".sheets", "") + project.getSbmlFileName();
		
		String sbmlSpecification = project.getSpecification(); 

		SBMLDocument doc = new SBMLDocument();
		doc.setNamespace(sbmlSpecification);
		
		doc.setLevel(Integer.parseInt(getLevel(sbmlSpecification)));
		doc.setVersion(Integer.parseInt(getVersion(sbmlSpecification)));
		
		Model model = new Model();
		doc.setModel(model);
		model.setLevel(doc.getLevel());
		model.setVersion(doc.getVersion());
		
		List<Spreadsheet> sheets = new ArrayList<>();
		for (String path : project.getPaths()) {
			sheets.add(SheetReader.readSheetFromFile(path));
		}
		
		for (Spreadsheet sheet : sheets) {
			Table tab = chooseTableToCreate(sheet);
			if(tab != null) {
				tab.addToSBMLModel(model);
			}
		}
		
		SBMLWriter writer = new SBMLWriter();
		
		try {
			
			writer.write(doc, sbmlPath);
			
			
		} catch (SBMLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	/**
	 * Returns the Version number of the SBML specification as String
	 * @param sbmlSpecification The SBML specification
	 * @return The Version number of the SBML specification as String
	 */
	private String getVersion(String sbmlSpecification) {
		String versionPattern = "(?<=version)\\d";
		return getPattern(versionPattern, sbmlSpecification);
		
	}

	/**
	 * Returns the pattern in target as a String
	 * @param pattern The regex pattern
	 * @param target The target String
	 * @return The pattern in target as a String
	 */
	private String getPattern(String regex, String target) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(target);
		matcher.find();
		return matcher.group().toString();
	}

	/**
	 * Returns the Level number of the SBML specification as String
	 * @param sbmlSpecification The SBML specification
	 * @return The Level number of the SBML specification as String
	 */
	private String getLevel(String sbmlSpecification) {
		final String levelPattern = "(?<=level)\\d";
		return getPattern(levelPattern, sbmlSpecification);
	}
	
	/**
	 * Returns a {@link Table} based on the provided {@link Spreadsheet}.
	 * @param sheet The {@link Spreadsheet} the selection is based on
	 * @return A {@link Table} based on the provided {@link Spreadsheet}.
	 */
	private Table chooseTableToCreate(Spreadsheet sheet) {
		return TableFactory.getTableFrom(sheet);
	}
	
}
