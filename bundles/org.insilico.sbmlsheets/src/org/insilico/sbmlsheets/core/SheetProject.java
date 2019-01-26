package org.insilico.sbmlsheets.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.plaf.synth.SynthSpinnerUI;

import org.insilico.sbmlsheets.core.compile.SBMLBuilder;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

public class SheetProject{
	
	private ObservableList<String> paths;
	
	private ObservableMap<String, String> names;
	
	private String sbmlFileName;
	
	private String modelName;
	
	private String uri;

	private String sbmlSpecification = "http://www.sbml.org/sbml/level3/version2/core";
	
	private int level;
	
	private int version;

	private ObservableMap<String, String> types;
	
	public SheetProject(String uri) {
		this.setUri(uri);
		paths = FXCollections.observableArrayList();
		names = FXCollections.observableHashMap();
		types = FXCollections.observableHashMap();
		
	}


	public SheetProject(String uri, String name, String specification, List<String> paths, List<String> names, List<String> types) {
		this.uri = uri;
		this.sbmlFileName = name;
		this.sbmlSpecification = specification;
		for (String path : paths) {
			path = getUri().replace(".sheets", "") + path;
		}
		this.paths = FXCollections.observableArrayList(paths);
		this.names = FXCollections.observableHashMap();
		this.types = FXCollections.observableHashMap();
		for (int i=0;i<paths.size();i++) {
			this.addNameToPath(paths.get(i), names.get(i));
			this.addTypeToPath(paths.get(i), types.get(i));
		}
	}


	private void addTypeToPath(String path, String type) {
		this.types.put(path, type);
	}


	private void addNameToPath(String path, String name) {
		this.names.put(path, name);
	}


	public List<String> readFilesInDir(File dir) {
		File[] files = dir.listFiles();
		List<String> paths = new ArrayList<String>();
		for (File file : files) {
			if (file.getAbsolutePath().endsWith("csv")) {
				paths.add(file.getPath());
			}
		}
		return paths;
	}


	public void compileSBML() {
		save();
		SBMLBuilder compiler = new SBMLBuilder(this);
		compiler.compile();
	}
	
	public void save() {
		SheetWriter.writeSheetToFile(this.uri, toFileFormat());
	}


	private String toFileFormat() {
		String out = "";
		out += String.format("NAME=%s\n",sbmlFileName);
		out += String.format("SPECIFICATION=%s\n", sbmlSpecification);
		out += "SHEETS={\n";
		for (String path : paths) {
			out += String.format("%s:%s:%s\n", path,names.get(path),types.get(path));
		}
		out += "}\n";
		return out;
	}


	public void removePath(int index) {
		paths.remove(index);
	}
	
	public void removePath(String path) {
		if(paths.contains(path)) {
			paths.remove(path);
		}
	}
	
	public void addPath(String path) {
		paths.add(path);
	}
	
	public void addPath(String path, int index) {
		paths.add(index, path);
	}

	public ObservableList<String> getPaths() {
		return this.paths;
	}


	public String getUri() {
		return uri;
	}


	public void setUri(String uri) {
		this.uri = uri;
	}


	public ObservableMap<String, String> getNames() {
		return names;
	}


	public void setNames(ObservableMap<String, String> names) {
		this.names = names;
	}
	
	public String getName(String path) {
		return names.get(path);
	}
	
	public String getSpecification() {
		return this.sbmlSpecification;
	}


	public void setSpecification(String specification) {
		this.sbmlSpecification = specification;
	}


	public String getSbmlFileName() {
		return sbmlFileName;
	}


	public void setSbmlFileName(String sbmlFileName) {
		this.sbmlFileName = sbmlFileName;
	}

	
	public void addPathNameType(String path, String name, String type) {
		if (!paths.contains(path) && !names.containsValue(name) && !types.containsValue(type)) {
			addPath(path);
			addNameToPath(path, name);
			addTypeToPath(path,type);
		}
	}

	/**
	 * Removes the entry for {@code path} from {@link #paths} and the map from {@link #names} and {@link #types} if they exist
	 * @param path The key to look for.
	 */
	public void removeAssociated(String path) {
		removePath(path);
		removeName(path);
		removeType(path);
	}


	/**
	 * Removes the map for {@code path} in {@link #types} if it exists.
	 * @param path The key to look for.
	 */
	private void removeType(String path) {
		if (types.containsKey(path)) {
			types.remove(path);
		}
	}

	/**
	 * Removes the map for {@code path} in {@link #names} if it exists.
	 * @param path The key to look for.
	 */
	private void removeName(String path) {
		if (names.containsKey(path)) {
			names.remove(path);
		}
	}

	/**
	 * Loads an existing SBML file and creates csv files and updates the .sheets file.
	 * @param file The selected SBML file.
	 */
	public void loadSBML(File file) {
		SBMLBuilder builder = new SBMLBuilder(this);
		builder.read(file);
	}

	/**
	 * Returns the {@link ObservableMap} of {@link #types}.
	 * @return The {@link ObservableMap} of {@link #types}.
	 */
	public ObservableMap<String, String> getTypes() {
		return this.types;
	}

	/**
	 * Returns the mapping of {@code path} in {@link #types}.
	 * @param path The path to a csv file of a {@link Spreadsheet}
	 * @return The mapping of {@code path} in {@link #types}.
	 */
	public String getType(String path) {
		if (types.containsKey(path)) {
			return types.get(path);
		}
		return "";
	}

}
