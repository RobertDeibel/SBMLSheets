package org.insilico.sbmlsheets.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.plaf.synth.SynthSpinnerUI;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

public class SheetProject{
	
	private ObservableList<String> paths;
	
	private ObservableMap<String, String> names;
	
	private String sbmlFileName;
	
	private String uri;

	private String sbmlSpecification = "http://www.sbml.org/sbml/level3/version2/core";
	
	public SheetProject(String uri) {
		this.setUri(uri);
		paths = FXCollections.observableArrayList();
		names = FXCollections.observableHashMap();
		
	}


	public SheetProject(String uri, String name, String specification, List<String> paths, List<String> names) {
		this.uri = uri;
		this.sbmlFileName = name;
		this.sbmlSpecification = specification;
		for (String path : paths) {
			path = getUri().replace(".sheets", "") + path;
		}
		this.paths = FXCollections.observableArrayList(paths);
		this.names = FXCollections.observableHashMap();
		for (int i=0;i<paths.size();i++) {
			this.addNameToPath(paths.get(i), names.get(i));
		}
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
			out += String.format("%s:%s\n", path,names.get(path));
		}
		out += "}\n";
		return out;
	}


	public void removePath(int index) {
		paths.remove(index);
	}
	
	public void removePath(String path) {
		paths.remove(path);
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
		if (!paths.contains(name) && !names.containsValue(name)) {
			addPath(path);
			addNameToPath(path, name);
		}
	}

}
