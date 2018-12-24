package org.insilico.sbmlsheets.editor;

import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.insilico.sbmlsheets.core.Row;
import org.insilico.sbmlsheets.core.Spreadsheet;
import org.sbml.jsbml.SBase;

import javafx.scene.layout.BorderPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.*;
import javafx.beans.property.*;

public class SpreadsheetView {

	@Inject
	private Spreadsheet doc;
	
	@PostConstruct
    private void init(BorderPane parent) {
        TableView<Row> table = setUpTable();
        parent.setCenter(table);
    }
	
	private TableView<Row> setUpTable(){
		System.out.println("Setting up SpreadsheetView...");
		
		TableView<Row> table = new TableView<>();
		table.setItems(doc.getDataAndHead());
		
		ArrayList<TableColumn<Row, String>> columns = new ArrayList<>();
		for (StringProperty col : doc.getHead()) {
			columns.add(new TableColumn<>(col.getValue()));
		}
		for (int i=0; i<columns.size();i++) {
			columns.get(i).setCellValueFactory(new PropertyValueFactory<>(doc.getHead().cellProperty(i).getName()));
		}
		table.getColumns().addAll(columns);
        
		System.out.println("View Done");
		return table;
	}

}
 