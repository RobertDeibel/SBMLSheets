package org.insilico.sbmlsheets.core.compile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.insilico.sbmlsheets.core.Constants;
import org.insilico.sbmlsheets.core.SheetProject;
import org.insilico.sbmlsheets.core.Spreadsheet;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLReader;

import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import java.io.File;
import java.io.IOException;

import javax.swing.tree.TreeNode;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

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
		List<String> paths = project.getPaths();
		Map<String, String> types = project.getTypes();
		Map<String, String> names = project.getNames();
		for (int i=0; i<project.getPaths().size(); i++) {
			String path = paths.get(i);
			model.add(chooseTableToCreate(types.get(path), path, names.get(path)));
		}
		
		
	}
	
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
					System.out.println("SheetType: "+sheet.getTableType()+" SheetName: "+sheet.getTableName());
					project.addPathNameType(sheet.getFileLocation(), sheet.getTableName(), sheet.getTableType());
					System.out.println(project.getPaths());
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
	
	private List<Spreadsheet> buildSheets(Model model, String path) {
		List<Spreadsheet> sheets = new ArrayList<>();
		for (int i=0; i<model.getChildCount(); i++) {
			sheets.add(build(model.getChildAt(i), path));
		}
		return sheets;
	}

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
	public String compile() {
		//TODO
		System.out.println("TODO compile");
		try {	
			
			String sbmlSpecification = project.getSpecification(); 
			DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance()
										.newDocumentBuilder();
			
			//root
			Document doc = docBuilder.newDocument();
			Element sbmlRoot = doc.createElementNS(sbmlSpecification, "sbml");
			doc.appendChild(sbmlRoot);
			sbmlRoot.setAttribute("level", getLevel(sbmlSpecification));
			sbmlRoot.setAttribute("version", getVersion(sbmlSpecification));
			//model node; the other nodes are inserted as its children
			Element model = doc.createElement("model");
			sbmlRoot.appendChild(model);
			
			
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Returns the Version number of the SBML specification as String
	 * @param sbmlSpecification The SBML specification
	 * @return The Version number of the SBML specification as String
	 */
	private String getVersion(String sbmlSpecification) {
		final String versionPattern = "(?<=version).";
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
		return matcher.group().toString();
	}

	/**
	 * Returns the Level number of the SBML specification as String
	 * @param sbmlSpecification The SBML specification
	 * @return The Level number of the SBML specification as String
	 */
	private String getLevel(String sbmlSpecification) {
		final String levelPattern = "(?<=level).";
		return getPattern(levelPattern, sbmlSpecification);
	}

	private Table chooseTableToCreate(String type, String path, String name) {
		Table object = null;
		switch (type) {
		case Constants.COMPARTMENT_TABLE:
			object = new CompartmentTable(path);
			break;
		case Constants.CONSTRAINTS_TABLE:
			object = new ConstraintsTable(path);
			break;
		case Constants.EVENTS_TABLE:
			object = new EventTable(path);
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
