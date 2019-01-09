package org.insilico.sbmlsheets.core;

import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

public class SheetProject{
	
	private ObservableList<StringProperty> paths;


	public void compileSBML() {
		// TODO Auto-generated method stub
		
	}
	
	public void removePath(int index) {
		paths.remove(index);
	}
	
	public void removePath(StringProperty path) {
		paths.remove(path);
	}
	
	public void addPath(String path) {
		paths.add(new SimpleStringProperty(path));
	}
	
	public void addPath(String path, int index) {
		paths.add(index, new SimpleStringProperty(path));
	}

	public ObservableList<StringProperty> getPaths() {
		return this.paths;
	}

}
