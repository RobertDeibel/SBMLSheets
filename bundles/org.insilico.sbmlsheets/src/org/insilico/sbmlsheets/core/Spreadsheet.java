package org.insilico.sbmlsheets.core;

import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;

import javafx.beans.property.StringProperty;
import javafx.collections.*;

import javax.inject.Inject;

public class Spreadsheet {
	
	/**
	 * The number of rows an empty table is initialized with
	 */
	private final int INITAIAL_ROWS = 30;
	
	
	/**
	 * The content of the table modelled as rows
	 */
	ObservableList<Row> data;
	
	/**
	 * The names of the columns
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
		buildHead();
		data = FXCollections.observableArrayList();
		for (int i=0; i<INITAIAL_ROWS;i++) {
			addEmptyRow();
		}
		tableType = "";
		tableName = "";
	}
	
	private void buildHead() {
		head = new Row();
	}
	@Inject
	public Spreadsheet(ObservableList<Row> data, String tableType, String tableName) {
		this.data = data;
		this.tableType = tableType;
		this.tableName = tableName;
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
	
}
