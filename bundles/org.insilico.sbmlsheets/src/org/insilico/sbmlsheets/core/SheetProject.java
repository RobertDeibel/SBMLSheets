package org.insilico.sbmlsheets.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class SheetProject{
	
	private ObservableList<String> paths;
	
	private String uri;
	
	public SheetProject(String uri) {
		this.setUri(uri);
		String dir = uri.replace(".sheets", "");
		paths = FXCollections.observableArrayList(readFilesInDir(new File(dir)));
	}


	private List<String> readFilesInDir(File dir) {
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
		// TODO Auto-generated method stub
		
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

}
