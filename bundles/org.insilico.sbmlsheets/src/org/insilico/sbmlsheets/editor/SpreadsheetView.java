package org.insilico.sbmlsheets.editor;

import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.insilico.sbmlsheets.core.Row;
import org.insilico.sbmlsheets.core.Spreadsheet;

import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.*;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;

@SuppressWarnings("restriction")
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
		
		TableView<Row> table = new TableView<Row>();
		
		//Custom cellFactory to support Editing of cells.
//		Callback<TableColumn<Row, String>, TableCell<Row, String>> cellFactory = new Callback<TableColumn<Row, String>, TableCell<Row, String>>() {
//			@Override
//			public TableCell<Row, String> call(TableColumn<Row, String> p) {
//				return new EditingCell();
//			}
//		};
		
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
            //own cellFactory to support editing 
            //was not as perfectly working as the delivered TextFieldTableCell so for now not used.
//            tc.setCellFactory(cellFactory);
            tc.setCellFactory(TextFieldTableCell.forTableColumn());
            tc.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Row,String>>() {
				
				@Override
				public void handle(CellEditEvent<Row, String> event) {
					((Row) event.getTableView().getItems().get(event.getTablePosition().getRow())).setCell(colNo, event.getNewValue());
				}
			});
			table.getColumns().add(tc);

			
		}
		table.setEditable(true);
		table.setItems(doc.getData());
		
        
		System.out.println("View Done");
		return table;
	}

}
 