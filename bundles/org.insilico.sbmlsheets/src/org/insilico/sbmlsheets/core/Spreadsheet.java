package org.insilico.sbmlsheets.core;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.*;

import javax.inject.Inject;
@SuppressWarnings("restriction")
/**
 * {@link Spreadsheet} models a Spreadsheet with rows of type {@link Row} saved in {@link data} the column names are stored in {@link head} which is of type {@link HeadRow}
 * 
 * @author Robert Deibel
 *
 */
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
	HeadRow head;
		
	/**
	 * Which type of SBML table is represented
	 */
	String tableType;
	
	/**
	 * The Name of the Table 
	 */
	String tableName;
	
	/**
	 * The location of the table in the file system
	 */
	String fileLocation;
	
	/**
	 * Constructor for an empty {@link Spreadsheet}
	 * @param uri The location of the representing CSV
	 */
	public Spreadsheet(String uri) {
		head = HeadRow.createHead();
		data = FXCollections.observableArrayList();
		for (int i=0; i<INITAIAL_ROWS;i++) {
			addEmptyRow();
		}
		tableType = "";
		tableName = "";
		this.fileLocation = uri;
	}
	
	/**
	 * Constructor for a pre-filled {@link Spreadsheet}
	 * @param data The contents of the {@link Spreadsheet}
	 * @param tableType The type of table 
	 * @param tableName The Name of the Table
	 * @param uri The location of the representing CSV
	 */
	@Inject
	public Spreadsheet(List<List<String>> data, String tableType, String tableName, String uri) {
		this.head = HeadRow.createHead(data.remove(0));
		this.data = FXCollections.observableArrayList(convert(data));
		fillTable();
		this.tableType = tableType;
		this.tableName = tableName;
		this.fileLocation = uri;
	}
	
	/**
	 * Converts given data of a pre-filled table into a {@link Spreadsheet} representation. 
	 * Called by the constructor {@link #Spreadsheet(List, String, String, String)}.
	 * @param data The contents of the {@link Spreadsheet} as read from a csv.
	 * @return The data wrapped as {@link Row} objects contained in a {@link List}.
	 */
	private List<Row> convert(List<List<String>> data) {
		List<Row> table = new ArrayList<Row>(data.size());
		for (List<String> row : data) {
			table.add(new Row(row, head));
		}
		return table;
	}
	
	/**
	 * Fills {@link #data} with empty {@link Row} objects if the minimum row number is not reached.
	 */
	private void fillTable() {
		while(data.size() < INITAIAL_ROWS) {
			addEmptyRow();
		}
	}
	
	/**
	 * Return the {@link Row} at the given index.
	 * @param index The index of the {@link Row} to be returned.
	 * @return The {@link Row} at {@code index}.
	 */
	public Row getRow(int index){
		return data.get(index);
	}
	
	/**
	 * Sets {@code row} as the last entry in {@link #data} and adds an empty {@link Row}. 
	 * @param row A {@link Row} object to be added to the end of {@link #data}.
	 */
	public void setLastRow(Row row) {
		data.add(row);
		addEmptyRow();
	}
	
	/**
	 * Adds an empty {@link Row} at the specified index in the list.
	 * @param index The index to insert the empty {@link Row}.
	 */
	public void addEmptyRowAt(int index) {
		data.add(index, new Row(head));
	}
	
	/**
	 * Adds an empty {@link Row} at the end of {@link #data}.
	 */
	public void addEmptyRow() {
		data.add(new Row(head));
	}
	
	/**
	 * Overrides the {@link Row} at the specified {@code index} in {@link #data}.
	 * @param index The index to insert {@code row}.
	 * @param row The {@link Row} to be inserted.
	 */
	public void modifyRow(int index, Row row) {
		data.set(index, row);
	}
	
	/**
	 * Returns {@link #data} containing {@link Row} object, representing the data of the {@link Spreadsheet}.
	 * @return {@link #data} containing {@link Row} object, representing the data of the {@link Spreadsheet}.
	 */
	public ObservableList<Row> getData(){
		return data;
	}
	
	/**
	 * Returns {@link #data} and {@link #head} in a single {@link ObservableList} of {@link Row} objects, 
	 * representing the data and the column heads of the {@link Spreadsheet}.
	 * @return {@link #data} and {@link #head} in a single {@link ObservableList} of {@link Row} objects, 
	 * representing the data and the column heads of the {@link Spreadsheet}.
	 */
	public ObservableList<Row> getDataAndHead() {
		ObservableList<Row> dataAndHead = FXCollections.observableArrayList();
		dataAndHead.add(head);
		dataAndHead.addAll(data);
		
		return dataAndHead;
	}
	
	/**
	 * Return the {@link #head} of the {@link Spreadsheet} represented by a {@link Row} object. 
	 * {@link #head} contains the column names as {@code value} and property {@code name}.
	 * @return The {@link #head} of the {@link Spreadsheet} represented by a {@link Row} object. 
	 */
	public Row getHead() {
		return head;
	}
	
	/**
	 * Returns the number of rows in the {@link Spreadsheet}, without the {@link #head}.
	 * @return The number of rows in the {@link Spreadsheet}, without the {@link #head}.
	 */
	public int getRowCount() {
		return data.size();
	}
	
	/**
	 * Returns the number of columns in the {@link Spreadsheet}.
	 * @return The number of columns in the {@link Spreadsheet}.
	 */
	public int getColCount() {
		return head.size();
	}

	/**
	 * Saves the {@link Spreadsheet} to the location specified at {@link #fileLocation}.
	 */
	public void save() {
		SheetWriter.writeSheetToFile(fileLocation, toCSVFormat());
	}

	/**
	 * changes the property names of a column.
	 * @param colNo The number of the column
	 * @param propName The new property name.
	 */
	public void updatePropertyNames(int colNo, String propName) {
		for (Row row : data) {
			row.setPropertyName(colNo, propName);
		}
	}
	
	/**
	 * Formats the {@link Spreadsheet} to a csv representation.
	 * @return The {@link Spreadsheet} in a csv representation.
	 */
	public String toCSVFormat() {
		String out = "";
		out += String.format("%s\n", head.toCSVFormat());
		for (Row row : data) {
			out += String.format("%s\n", row.toCSVFormat());
		}
		return out;
		
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
