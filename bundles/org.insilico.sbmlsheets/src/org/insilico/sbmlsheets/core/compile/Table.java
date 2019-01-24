package org.insilico.sbmlsheets.core.compile;

import org.insilico.sbmlsheets.core.Spreadsheet;

public abstract class Table {
	
	protected final String uri;
		
	
	public Table(String path) {
		this.uri = path;
	}

	protected abstract Spreadsheet buildSpreadsheet();
	

	
}
