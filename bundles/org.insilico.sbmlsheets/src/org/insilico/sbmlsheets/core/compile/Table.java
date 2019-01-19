package org.insilico.sbmlsheets.core.compile;

public abstract class Table {
	
	String uri;
	
	public Table(String path) {
		this.uri = path;
		buildModel(path);
	}

	protected abstract void buildModel(String path);
	
}
