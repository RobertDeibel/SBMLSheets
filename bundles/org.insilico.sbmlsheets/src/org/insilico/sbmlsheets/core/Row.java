package org.insilico.sbmlsheets.core;


import java.util.Iterator;
import java.util.List;

import javafx.beans.property.*;
import javafx.collections.*;
/**
 * The class {@link Row} represents a Row inside a {@link Spreadsheet} object. 
 * 
 * @author Robert Deibel
 *
 */
@SuppressWarnings("restriction")
public class Row implements Iterable<StringProperty>{
	/**
	 * The value of an empty String property in a {@link Row}.
	 */
	protected static final String INIT_CELL_VALUE = "";

	/**
	 * The initial number of initiated cells in a {@link Row}. 
	 * Cells are of {@link StringProperty} type and stored in a {@link ObservableList} called {@link Row.cells}.
	 */
	protected static final int INITIAL_CELLS = 10;
	
	/**
	 * Represents the content of the {@link Row}.
	 * An {@link ObservableList} filled with data of type {@link StringProperty}.
	 */
	protected ObservableList<StringProperty> cells;
	
	
	//Methods for creating the head of the Spreadsheet
	
	protected Row() {
		
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
	
	
	/**
	 * Sets the property value of the field in {@link #cells} at {@code index} to {@code value}.
	 * @param index The index of the cell
	 * @param value The value to be set
	 */
	public void setCell(int index, String value) {
		cellProperty(index).set(value);
	}
	/**
	 * Creates a new Property and sets the name as {@code propName} and the value as before.
	 * The new property is written to {@link #cells} at {@code index}.
	 * @param index  The index of the cell
	 * @param propName The name to be set as property name
	 */
	public void setPropertyName(int index, String propName) {
		cells.set(index, new SimpleStringProperty(this, propName, cellProperty(index).getValue()));
	}
	/**
	 * Returns the number of {@link StringProperty} stored in {@link #cells}.
	 * @return The number of {@link StringProperty} stored in {@link #cells}
	 */
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
	
	/**
	 * Helper for {@link #toString()}. Returns a textual representation of the cell at {@code index}.
	 * @param index The index of the cell
	 * @return A textual representation of the cell at {@code index}.
	 */
	public String cellToString(int index) {
		return String.format("Value: %s; PropertyName: %s",getCell(index),getPropertyName(index));
	}						 
	
	@Override
	public Iterator<StringProperty> iterator() {
		return new RowIterator();
	}
	
	
	
	
	/**
	 * The {@link Iterator} implementation for {@link Row}.
	 * @author Robert Deibel
	 *
	 */
	protected class RowIterator implements Iterator<StringProperty>{
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




	public String toCSVFormat() {
		String[] cellsAsArray = new String[cells.size()];
		for (int i=0; i<cells.size();i++) {
			cellsAsArray[i] = this.getCell(i);
		}
		return String.join(",", cellsAsArray);
	}



	public boolean isEmpty() {
		for (StringProperty value : this.cells) {
			if (!value.equals(INIT_CELL_VALUE)) {
				return false;
			}
		}
		return true;
	}
	
}






