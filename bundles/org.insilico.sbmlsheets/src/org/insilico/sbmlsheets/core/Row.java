package org.insilico.sbmlsheets.core;


import java.util.Iterator;

import javafx.beans.property.*;
import javafx.collections.*;

public class Row implements Iterable<StringProperty>{
	
	private final int INITIAL_CELLS = 10;

	ObservableList<StringProperty> cells;
	
	public Row() {
		cells = FXCollections.observableArrayList();
		createHeader();
	}
	
	public Row(Row head) {
		cells = FXCollections.observableArrayList();
		for (StringProperty cell : head) {
			addEmptyCell(cell.getName());
		}
	}

	public StringProperty cellProperty(int index) {
		if (cells.get(index) == null) cells.add(index, new SimpleStringProperty(this, Integer.toString(index),""));
		return cells.get(index);
	}
	
	public String getCell(int index) {
		return cellProperty(index).get();
	}
	
	public ObservableList<StringProperty> getAllCells(){
		return cells;
	}
	
	
	
	public void setCell(int index, String rowName, String value) {
		cellProperty(index).set(value);
	}
	
	void createHeader() {
		for (int i=0;i<INITIAL_CELLS; i++) {
			cells.add(new SimpleStringProperty(this,Integer.toString(i),"Hier Koennte Ihre Werbung stehen"+i));
		}
	}
	
	private void addEmptyCell(String name) {
		System.out.println(name);
		cells.add(new SimpleStringProperty(this, name, "bla"+name));
	}
	
	
	
	@Override
	public Iterator<StringProperty> iterator() {
		return new RowIterator();
	}
	
	
	
	
	
	private class RowIterator implements Iterator{
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
		public Object next() {
			if(this.hasNext()) {
				return cells.get(position++);
			}else {
				return null;
			}
		}
		
	}
}
