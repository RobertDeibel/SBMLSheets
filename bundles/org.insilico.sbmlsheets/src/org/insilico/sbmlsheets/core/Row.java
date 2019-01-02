package org.insilico.sbmlsheets.core;


import java.util.Iterator;
import java.util.List;

import javafx.beans.property.*;
import javafx.collections.*;
/**
 * The class {@link Row} represents a Row inside a {@link Spreadsheet} object. 
 * 
 * @author robert
 *
 */
@SuppressWarnings("restriction")
public class Row implements Iterable<StringProperty>{
	/**
	 * The value of an empty String property in a {@link Row}.
	 */
	private static final String INIT_CELL_VALUE = "bla";
	/**
	 * The value of an empty String property in a header {@link Row}.
	 */
	private static final String INIT_HEADER_VALUE = "Hier koennte Ihre Werbung stehen";
	/**
	 * The initial number of initiated cells in a {@link Row}. 
	 * Cells are of {@link StringProperty} type and stored in a {@link ObservableList} called {@link Row.cells}.
	 */
	private static final int INITIAL_CELLS = 10;
	
	/**
	 * Represents the content of the {@link Row}.
	 * An {@link ObservableList} filled with data of type {@link StringProperty}.
	 */
	private ObservableList<StringProperty> cells;
	
	
	//Methods for creating the head of the Spreadsheet
	
	/**
	 * Private constructor {@link Row} object without parameters.
	 * Only to be used for creation of a dummy header {@link Row} through {@link #createHead()}.
	 */
	private Row() {
		cells = FXCollections.observableArrayList();
		for (int i=0;i<INITIAL_CELLS; i++) {
			cells.add(new SimpleStringProperty(this,Integer.toString(i),INIT_HEADER_VALUE));
		}
	}
	/**
	 * Private constructor for {@link Row} with the contents of a header as a parameter.
	 * For creation of a header {@link Row} through {@link #createHead(List)}.
	 * The contents of {@code head} are stored as {@code name} and {@code value} of the {@link cells} fields.
	 * @param head The value and name of the header cells as a {@link List}
	 */
	private Row(List<String> head) {
		cells = FXCollections.observableArrayList();
		//iterate through every value of head
		for (String name : head) {
			//bind (bean, name, value) to a new StringProperty 
			cells.add(new SimpleStringProperty(this, name, name));
		}	
	}
	/**
	 * Method for calling the parameterless private constructor {@link #Row()} of {@link Row}.
	 * @return A {@link Row} object representing an empty header of a {@link Spreadsheet}.
	 */
	static Row createHead() {
		return new Row();
	}
	
	/**
	 * Method for calling the one-parameter private constructor {@link #Row(List)} of {@link Row}.
	 * @param head The value and name of the header cells as a {@link List}
	 * @return A {@link Row} object representing the header of a {@link Spreadsheet}.
	 * {@link cells} is filled with the contents of {@code head}. 
	 */
	public static Row createHead(List<String> head) {
		return new Row(head);
		
	}

	
	
	//Methods for the other Rows not associated with the Head
	
	/**
	 * Constructor for {@link Row}. Binds the property name of the contents of
	 * {@code head} to the contents of {@link #cells}.
	 * @param head The value and name of the header cells as a {@link List}
	 */
	public Row(Row head) {
		cells = FXCollections.observableArrayList();
		for (StringProperty cell : head) {
			addEmptyCell(cell.getName());
		}
	}
	/**
	 * Constructor for {@link Row}. Binds the property name of the contents of
	 * {@code head} to the contents of {@link #cells} and the contents of {@code data}
	 * to the value of {@link #cells}.
	 * @param data The contents of the rows value
	 * @param head The name of the properties
	 */
	public Row(List<String> data, Row head) {
		cells = FXCollections.observableArrayList();
		for (int i=0; i<head.size();i++) {
			cells.add(new SimpleStringProperty(this, head.getPropertyName(i), data.get(i)));
		}
	}
	

	/**
	 * Adds an empty {@link StringProperty} to the end of {@link #cells} with {@code name}
	 * as its property name and {@link #INIT_CELL_VALUE} as content.
	 * @param name
	 */
	private void addEmptyCell(String name) {
		cells.add(new SimpleStringProperty(this, name, INIT_CELL_VALUE));
	}
	
	
	//Row API
	/**
	 * Returns the {@link StringProperty} at the {@code index} in {@link #cells}.
	 * @param index The index of the requested cell in {@link #cells}.
	 * @return
	 * The {@link StringProperty} at the specified {@code index} in {@link #cells}
	 */
	public StringProperty cellProperty(int index) {
		return cells.get(index);
	}
	
	/**
	 * Returns the value of the cell at {@code index}.
	 * @param index The index of the requested cell value
	 * @return
	 * The {@code String} value of the requested cell.
	 */
	public String getCell(int index) {
		return cellProperty(index).get();
	}
	/**
	 * Returns the property name of the cell at {@code index}.
	 * @param index The index of the requested cell property name
	 * @return
	 * The {@code String} property name of the requested cell.
	 */
	public String getPropertyName(int index) {
		return cellProperty(index).getName();
	}
	/**
	 * Return the {@link #cells} field of a {@link Row} as a {@link ObservableList} of 
	 * {@link StringProperty}.
	 * @return
	 * 
	 */
	public ObservableList<StringProperty> getAllCells(){
		return cells;
	}
	
	
	
	public void setCell(int index, String value) {
		cellProperty(index).set(value);
	}
	
	public void setPropertyName(int index, String propName) {
		cells.set(index, new SimpleStringProperty(this, propName, cellProperty(index).getValue()));
	}
	
	public int size() {
		return cells.size();
	}
	
	@Override
	public String toString() {
		String row = "";
		for (int i=0; i<cells.size();i++) {
			row += String.format("| %s |",cellToString(i));
		}
		
		return row;
		
	}
	
	public String cellToString(int index) {
		return String.format("Value: %s; PropertyName: %s",getCell(index),getPropertyName(index));
	}						 
	
	@Override
	public Iterator<StringProperty> iterator() {
		return new RowIterator();
	}
	
	
	
	
	
	private class RowIterator implements Iterator<StringProperty>{
		private int position = 0;

		@Override
		public boolean hasNext() {
			if (position < cells.size()) {
				return true;
			}else {
				return false;
			}
		}

		@Override
		public StringProperty next() {
			if(this.hasNext()) {
				return cells.get(position++);
			}else {
				return null;
			}
		}
		
	}
	
}






