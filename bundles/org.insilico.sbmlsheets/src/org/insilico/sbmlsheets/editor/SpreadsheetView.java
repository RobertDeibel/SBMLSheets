package org.insilico.sbmlsheets.editor;

import java.util.Comparator;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.insilico.sbmlsheets.core.HeadRow;
import org.insilico.sbmlsheets.core.Row;
import org.insilico.sbmlsheets.core.SheetProject;
import org.insilico.sbmlsheets.core.Spreadsheet;

import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;

/**
 * Initializes and sets the View of an injected {@link Spreadsheet} object.
 * @author Robert Deibel
 *
 */
@SuppressWarnings("restriction")
public class SpreadsheetView {
	
	/**
	 * The {@link Spreadsheet} provided by {@link SpreadsheetDocumentLoader} and injected into this class.
	 */
	@Inject
	private Spreadsheet doc;
	
	/**
	 * Is called after {@link #doc} is injected.
	 * Constructs the View and sets it in {@code parent}.
	 * @param parent The parent {@link Node} of the {@link SpreadsheetView}.
	 */
	@PostConstruct
    private void init(BorderPane parent) {
        TableView<Row> table = setUpTable();
        parent.setCenter(table);
    }
	
	/**
	 * Creates a {@link TableView} including the setup of columns and Values.
	 * @return The {@link TableView} representing the {@link Spreadsheet} object in {@link #doc}.
	 */
	private TableView<Row> setUpTable(){
		System.out.println("Setting up SpreadsheetView...");
		
		//set the basic TableView with custom sorting policy and a KeyEvent Handler.
		TableView<Row> table = initTable();
		
		
		//Custom cellFactory to support Editing of cells.
//		Callback<TableColumn<Row, String>, TableCell<Row, String>> cellFactory = new Callback<TableColumn<Row, String>, TableCell<Row, String>>() {
//			@Override
//			public TableCell<Row, String> call(TableColumn<Row, String> p) {
//				return new EditingCell();
//			}
//		};
		
		//for every column create a new TableColumn
		for (int i=0; i<doc.getColCount();i++) {
			//get the property of the head
			StringProperty colHead = doc.getHead().cellProperty(i);
			//set it as initial TableColumn value
			TableColumn<Row, String> tc = new TableColumn<>(colHead.getValue());
			//finalize the column number for use in cellValueFactory(int)
            final int colNo = i;
            //set the CellValueFactory
            tc.setCellValueFactory(cellValueFactory(colNo));
            //own cellFactory to support editing 
            //was not as perfectly working as the delivered TextFieldTableCell so for now not used.
//            tc.setCellFactory(cellFactory);
            //set the CellFactory to allow editing
            tc.setCellFactory(TextFieldTableCell.forTableColumn());
            //set behavior 
            tc.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Row,String>>() {
				
				@Override
				public void handle(CellEditEvent<Row, String> event) {
					((Row) event.getTableView().getItems().get(event.getTablePosition().getRow())).setCell(colNo, event.getNewValue());
					if (event.getTableView().getItems().get(event.getTablePosition().getRow()) instanceof HeadRow) {
						tc.setText(event.getNewValue());
						doc.updatePropertyNames(colNo, event.getNewValue());
						tc.setCellValueFactory(cellValueFactory(colNo));
					}
				}
			});
			table.getColumns().add(tc);

			
		}
		table.setEditable(true);
		table.setItems(doc.getDataAndHead());
		
        
		System.out.println("View Done");
		return table;
	}
	
	/**
	 * Returns a {@link TableView} instance with a custom {@link SortPolicy} and a {@link KeyEvent} {@link EventHandler}.
	 * The custom {@link SortPolicy} sorts the {@link TableColumn} as per default but the {@link HeadRow} is always placed at the top.
	 * The {@link EventHandler} registers {@link KeyEvents} and saves the {@link Spreadsheet} in {@link doc} through {@link org.insilico.sbmlsheets.core.Spreadsheet#save()} when
	 * Ctrl+F11 is detected.
	 * 
	 * @return A {@link TableView} instance with a custom {@link SortPolicy} and a {@link KeyEvent} {@link EventHandler}.
	 */
	private TableView<Row> initTable(){
		TableView<Row> table =  new TableView<Row>();
		
		//custom sorting policy
		//needed so that the row in table associated with HeadRow is always displayed at top
		table.sortPolicyProperty().set(t -> {
			Comparator<Row> comparator = (r1, r2)
					-> r1 instanceof HeadRow ? -1	//if HeadRow is compared: MOVE TO TOP
					: r2 instanceof HeadRow ? 1		//if HeadRow is compared: MOVE TO TOP
					//the numbers (-1, 1) are due to the ordering; to display at the bottom use (1, -1)
					: t.getComparator() == null ? 0
					: t.getComparator().compare(r1, r2);
			FXCollections.sort(table.getItems(), comparator);
			return true;
				
		});
		//enable saving with press of Ctrl+F11
		table.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.F11 && event.isControlDown()) {
					doc.save();
				}
			}
			
		});
		
		return table;
	}
	
	/**
	 * Returns a newly constructed {@link CellValueFactory} for a column of {@link #doc}. 
	 * It binds the typed value to the property in {@link org.insilico.sbmlsheets.core.Row#cellProperty(int)}.
	 * @param colNo The column of {@link #doc}
	 * @return A newly constructed {@link CellValueFactory} for a column of {@link #doc}
	 */
	private static Callback<CellDataFeatures<Row, String>, ObservableValue<String>> cellValueFactory(final int colNo) {
		return new Callback<CellDataFeatures<Row, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(CellDataFeatures<Row, String> p) {
                return p.getValue().cellProperty(colNo);
            }
        };
	}

}
 