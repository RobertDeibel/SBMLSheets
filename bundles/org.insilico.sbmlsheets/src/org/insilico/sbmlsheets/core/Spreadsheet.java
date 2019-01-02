package org.insilico.sbmlsheets.core;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.*;
import javafx.util.Callback;

import javax.inject.Inject;
@SuppressWarnings("restriction")
public class Spreadsheet {
	
	/**
	 * The number of {@link Row} objects an empty {@link Spreadsheet} is initialized with
	 */
	private final int INITAIAL_ROWS = 30;
	
	
	/**
	 * The content of the {@link Spreadsheet} modeled as {@link Row} Objects inside a {@link ObservableList}.
	 */
	ObservableList<Row> data;
	
	/**
	 * The names of the columns in {@link data} as a {@link Row} object.
	 */
	Row head;
		
	/**
	 * Which type of SBML table is represented
	 */
	String tableType;
	
	/**
	 * The Name of the Table 
	 */
	String tableName;
	
	/**
	 * The location of the table
	 */
	@Inject
	URL fileLocation;
	
	public Spreadsheet() {
		head = Row.createHead();
		data = FXCollections.observableArrayList();
		for (int i=0; i<INITAIAL_ROWS;i++) {
			addEmptyRow();
		}
		tableType = "";
		tableName = "";
	}
	

	@Inject
	public Spreadsheet(List<List<String>> data, String tableType, String tableName) {
		this.head = Row.createHead(data.remove(0));
		this.data = FXCollections.observableArrayList(convert(data));
		fillData();
		this.tableType = tableType;
		this.tableName = tableName;
	}

	private List<Row> convert(List<List<String>> data) {
		List<Row> table = new ArrayList<Row>(data.size());
		for (List<String> row : data) {
			table.add(new Row(row, head));
		}
		return table;
	}

	private void fillData() {
		while(data.size() < INITAIAL_ROWS) {
			addEmptyRow();
		}
	}
	public Row getRow(int index){
		return data.get(index);
	}
	
	public void setLastRow(Row row) {
		data.add(row);
		addEmptyRow();
	}
	
	public void addEmptyRowAt(int index) {
		data.add(index, new Row(head));
	}
	
	public void addEmptyRow() {
		data.add(new Row(head));
	}
	
	
	public void modifyRow(int index, Row row) {
		data.set(index, row);
	}
	
	public ObservableList<Row> getData(){
		return data;
	}
	public ObservableList<Row> getDataAndHead() {
		ObservableList<Row> dataAndHead = FXCollections.observableArrayList();
		dataAndHead.add(head);
		dataAndHead.addAll(data);
		
		return dataAndHead;
	}
	
	public Row getHead() {
		return head;
	}
	
	public int getRowCount() {
		return data.size();
	}
	
	public int getColCount() {
		return head.size();
	}
	
	@Override
	public String toString() {
		String headStr = this.head.toString();
		String[] spreadsheetStrgs = new String[data.size()];
		for (int i=0;i<data.size();i++) {
			spreadsheetStrgs[i] = data.get(i).toString();
		}
		int strSize = spreadsheetStrgs.length;
		String seperatorLine = new String(new char[strSize]).replace("\0", "-");
		String spreadsheet = "";
		spreadsheet += String.format("%s\n%s\n%s\n%s\n",seperatorLine,headStr,seperatorLine,seperatorLine);
		for (String s : spreadsheetStrgs) {
			spreadsheet += String.format("%s\n%s\n", s,seperatorLine);
		}
		
		return spreadsheet;
	}
	
}
