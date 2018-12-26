package org.insilico.sbmlsheets.editor;

import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.insilico.sbmlsheets.core.Row;
import org.insilico.sbmlsheets.core.Spreadsheet;
import org.sbml.jsbml.SBase;

import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.cell.*;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;

public class SpreadsheetView {

	@Inject
	private Spreadsheet doc;
	
	@PostConstruct
    private void init(BorderPane parent) {
        TableView<Row> table = setUpTable();
        table.setVisible(true);
        parent.setCenter(table);
    }
	
	@SuppressWarnings("restriction")
	private TableView<Row> setUpTable(){
		System.out.println("Setting up SpreadsheetView...");
		
		TableView<Row> table = new TableView<Row>();
		table.setEditable(true);
		ArrayList<TableColumn<Row, String>> columns = new ArrayList<>();
		
		for (int i=0; i<doc.getColCount();i++) {
			StringProperty colHead = doc.getHead().cellProperty(i);
			TableColumn<Row, String> tc = new TableColumn<>(colHead.getValue());
            final int colNo = i;
            tc.setCellValueFactory(new Callback<CellDataFeatures<Row, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(CellDataFeatures<Row, String> p) {
                    return p.getValue().cellProperty(colNo);
                }
            });
            
			columns.add(tc);
			table.getColumns().add(tc);

			
		}
		table.setItems(doc.getData());
		
        
		System.out.println("View Done");
		return table;
	}

}
 