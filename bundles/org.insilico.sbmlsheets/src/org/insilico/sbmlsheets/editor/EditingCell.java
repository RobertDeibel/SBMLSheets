package org.insilico.sbmlsheets.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.insilico.sbmlsheets.core.Row;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;


@SuppressWarnings("restriction")
public class EditingCell extends TextFieldTableCell<Row, String>{
	
	private TextField textField;
	
	public EditingCell() {
	}
	
	@Override
	public void startEdit() {
		super.startEdit();
		
		if (textField == null) {
			createTextField();
		}
		setText(null);
		setGraphic(textField);
		textField.selectAll();
		
	}
	
	@Override
	public void cancelEdit() {
		super.cancelEdit();
		setText(getItem());
		setGraphic(null);
		
	}

	@Override
	public void updateItem(String item, boolean empty) {
		super.updateItem(item, empty);
		
		if (empty) {
			setText(null);
			setGraphic(null);
		} else {
			if (isEditing()) {
				if (textField != null) {
					textField.setText(getString());
				}
				setText(null);
				setGraphic(null);
			}
		}
	}
	
	private void createTextField() {
		textField = new TextField(getString());
		textField.setMinWidth(this.getWidth() - this.getGraphicTextGap()*2);
		textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
		
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (!oldValue) {
					commitEdit(textField.getText());
				}
			}
		});
		
		textField.setOnKeyReleased(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				String value = textField.getText();
				if (event.getCode() == KeyCode.ENTER) {
					if (value != null) {
						commitEdit(value);
					} else {
						commitEdit(null);
					}
				} else if (event.getCode() == KeyCode.ESCAPE) {
					cancelEdit();
				} else if (event.getCode() == KeyCode.TAB)	 {
					commitEdit(value);
					TableColumn<Row, ?> nextColumn = getNextColumn(!event.isShiftDown());
					if (nextColumn != null) {
						getTableView().edit(getTableRow().getIndex(), nextColumn);
					}
				}
			}
		});
		
	}
	
	private String getString() {
		return getItem() == null ? "" :getItem().toString();
	}

	
	private TableColumn<Row, ?> getNextColumn(boolean forward) {
		List<TableColumn<Row, ?>> columns = new ArrayList<>();
		for (TableColumn<Row, ?> column : getTableView().getColumns()) {
			columns.addAll(getLeaves(column));
		}
		//There is no other column that supports editing.
		if (columns.size() < 2) {
			return null;
		}
		int currentIndex = columns.indexOf(getTableColumn());
		int nextIndex = currentIndex;
		if (forward) {
			nextIndex++;
			if (nextIndex > columns.size() - 1) {
				nextIndex = 0;
		    }
		} else {
		    nextIndex--;
		    if (nextIndex < 0) {
		        nextIndex = columns.size() - 1;
		    }
		}
		return columns.get(nextIndex);
		
	}	
	
	private List<TableColumn<Row, ?>> getLeaves(TableColumn<Row, ?> root) {
		List<TableColumn<Row, ?>> columns = new ArrayList<>();
		if (root.getColumns().isEmpty()) {
			if (root.isEditable()) {
				columns.add(root);
			}
			return columns;
		} else {
			for (TableColumn<Row, ?> column : columns) {
				columns.addAll(getLeaves(column));
			}
		}
		
		return columns;
	}


	


}
