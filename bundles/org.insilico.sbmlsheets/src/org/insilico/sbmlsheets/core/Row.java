package org.insilico.sbmlsheets.core;


import java.util.Iterator;


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
	
	private Row() {
		cells = FXCollections.observableArrayList();
		for (int i=0;i<INITIAL_CELLS; i++) {
			cells.add(new SimpleStringProperty(this,Integer.toString(i),"Hier Koennte Ihre Werbung stehen"));
		}
	}
	
	static Row createHead() {
		return new Row();
	}
	
	
	//Methods for the other Rows not associated with the Head
	
	public Row(Row head) {
		cells = FXCollections.observableArrayList();
		for (StringProperty cell : head) {
			addEmptyCell(cell.getName());
		}
	}
	
	private void addEmptyCell(String name) {
		cells.add(new SimpleStringProperty(this, name, "bla"));
	}
	
	
	//Row API

	public StringProperty cellProperty(int index) {
		return cells.get(index);
	}
	
	
	public String getCell(int index) {
		return cellProperty(index).get();
	}
	
	public String getPropertyName(int index) {
		return cellProperty(index).getName();
	}
	
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
