package org.insilico.sbmlsheets.core;

import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;

/**
 * {@link HeadRow} models a header {@link Row} inside a {@link Spreadsheet}. 
 * It should be filled with the name of the columns as property {@code value} and property 
 * {@code name} of a field in {@link cells}.
 * The {@code private} constructors of {@link HeadRow} enable that it is only instantiated 
 * through other methods.
 * The value and name of the properties in {@link #cells} is the same in {@link HeadRow}.
 * @author Robert Deibel
 *
 */
public class HeadRow extends Row {
	
	private static final String INIT_CELL_VALUE = "Hier koennte Ihre Werbung stehen";
	/**
	 * Private constructor {@link HeadRow} object without parameters.
	 * Only to be used for creation of a empty {@link HeadRow} through {@link #createHead()}.
	 */
	private HeadRow() {
		cells = FXCollections.observableArrayList();
		for (int i=0;i<INITIAL_CELLS; i++) {
			cells.add(new SimpleStringProperty(this,Integer.toString(i),INIT_CELL_VALUE));
		}
	}
	/**
	 * Private constructor for {@link HeadRow} with the contents of a header as a parameter.
	 * For creation of a {@link HeadRow} through {@link #createHead(List)}.
	 * The contents of {@code head} are stored as {@code name} and {@code value} of the {@link cells} fields.
	 * @param head The value and name of the header cells as a {@link List}
	 */
	private HeadRow(List<String> head) {
		cells = FXCollections.observableArrayList();
		//iterate through every value of head
		for (String name : head) {
			//bind (bean, name, value) to a new StringProperty 
			cells.add(new SimpleStringProperty(this, name, name));
		}	
	}
	/**
	 * Method for calling the parameterless private constructor {@link #HeadRow()} of {@link HeadRow}.
	 * @return A {@link HeadRow} object representing an empty header of a {@link Spreadsheet}.
	 */
	static HeadRow createHead() {
		return new HeadRow();
	}
	
	/**
	 * Method for calling the one-parameter private constructor {@link #HeadRow(List)} of {@link HeadRow}.
	 * @param head The value and name of the header cells as a {@link List}
	 * @return A {@link HeadRow} object representing the header of a {@link Spreadsheet}.
	 * {@link cells} is filled with the contents of {@code head}. 
	 */
	public static HeadRow createHead(List<String> head) {
		return new HeadRow(head);
		
	}
	
	/**
	 * Sets the property name and value to {@code value} for the cell at {@code index} in {@link #cells}.
	 * 
	 * Is equivalent to {@link #setPropertyName(int, String)} as in a {@link HeadRow} name and
	 * value are set to the same String.
	 */
	@Override
	public void setCell(int index, String value) {
		cells.set(index, new SimpleStringProperty(this, value, value));
	}
	/**
	 * Sets the property name and value to {@code value} for the cell at {@code index} in {@link #cells}.
	 * 
	 * Is equivalent to {@link #setCell(int, String)} as in a {@link HeadRow} name and
	 * value are set to the same String.
	 */
	@Override
	public void setPropertyName(int index, String propName) {
		cells.set(index, new SimpleStringProperty(this, propName, propName));
	}
}
