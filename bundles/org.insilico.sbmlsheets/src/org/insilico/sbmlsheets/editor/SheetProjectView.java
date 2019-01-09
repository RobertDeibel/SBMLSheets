package org.insilico.sbmlsheets.editor;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.insilico.sbmlsheets.core.SheetProject;
import org.insilico.sbmlsheets.core.SheetWriter;

import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextBoundsType;

import org.insilico.ui.themes.light.*;
import org.w3c.dom.css.CSSStyleDeclaration;

public class SheetProjectView {
	
	@Inject
	SheetProject project;
	
	@PostConstruct
	private void init(BorderPane parent) {
		parent = makeView(parent);
		
	}
	

	private BorderPane makeView(BorderPane parent) {
		double width = parent.getWidth();
		//creating a ButtonBar
		ButtonBar bottomButtonBar = new ButtonBar();
		//placing ButtonBar at bottom of screen
		parent.setBottom(bottomButtonBar);
		Button createFileButton = new Button("_Compile");
		createFileButton.setMnemonicParsing(true);
		createFileButton.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				project.compileSBML();
			}
		});
		bottomButtonBar.getButtons().add(createFileButton);
		
		GridPane gp = new GridPane();
				
		parent.setCenter(gp);
		Text sbmlSpecificationText = new Text("SBML Specification:");
		sbmlSpecificationText.setTextAlignment(TextAlignment.RIGHT);
		sbmlSpecificationText.setBoundsType(TextBoundsType.LOGICAL);
		sbmlSpecificationText.getStyleClass().add("text-id");
		
		double secondColWidth = width *0.75;
		TextField sbmlSpecification = new TextField("http://www.sbml.org/sbml/level3/version2/core");
		sbmlSpecification.setPromptText("Enter SBML specification");
		sbmlSpecification.setAlignment(Pos.CENTER);
		gp.add(sbmlSpecificationText, 0, 0);
		gp.add(sbmlSpecification, 1, 0);
		
		Text sbmlFileNameText = new Text("SBML File Name:");
		sbmlFileNameText.setTextAlignment(TextAlignment.RIGHT);
		sbmlFileNameText.setBoundsType(TextBoundsType.LOGICAL);
		sbmlFileNameText.getStyleClass().add("text-id");
		TextField sbmlFileName = new TextField();
		sbmlFileName.setPromptText("Enter SBML file name");
		sbmlFileName.setAlignment(Pos.CENTER);
		gp.add(sbmlFileNameText, 0, 1);
		gp.add(sbmlFileName, 1, 1);
		
		
		VBox spreadsheetListing = new VBox(spreadsheetLine());
		ScrollPane sp = new ScrollPane(spreadsheetListing);
		
		VBox spreadsheetControls = new VBox();
		Text spreadsheetText = new Text("Spreadsheets:");
		spreadsheetText.setTextAlignment(TextAlignment.RIGHT);
		spreadsheetText.getStyleClass().add("text-id");
		ButtonBar spreadsheetControlButtonBar = new ButtonBar();
		Button newSpreadsheetButton = new Button("new CSV");
		newSpreadsheetButton.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				SheetWriter.newCSV();
			}
		});
		Button addSpreadsheetButton = new Button("Add");
		addSpreadsheetButton.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				spreadsheetListing.getChildren().add(spreadsheetLine(true, spreadsheetListing));
			}
		});
		
		spreadsheetControlButtonBar.getButtons().addAll(newSpreadsheetButton, addSpreadsheetButton);
		
		spreadsheetControls.getChildren().addAll(spreadsheetText, spreadsheetControlButtonBar);
		gp.add(spreadsheetControls, 0, 2);
		gp.add(sp, 1, 2, 2, 1);
		return parent;
		
	}

	
	private HBox spreadsheetLine() {
		HBox sheetLine = new HBox();
		ComboBox<StringProperty> fileSelection = new ComboBox<>(project.getPaths());
		fileSelection.setPromptText("CSV file");
		fileSelection.setEditable(true);
		fileSelection.setFocusTraversable(true);

		fileSelection.setOnKeyReleased(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					sheetLine.requestFocus();
				}
			}
			
		});
		TextField sheetName = new TextField();
		sheetName.setPromptText("Enter the Spreadsheet name");
		sheetName.setOnKeyReleased(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				if(event.getCode() == KeyCode.ENTER) {
					sheetName.commitValue();
					sheetLine.requestFocus();
				}
			}
		});
		Button clearButton = new Button("Clear");
		clearButton.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				fileSelection.setValue(null);
				sheetName.clear();
			}

		});
		sheetLine.getChildren().addAll(fileSelection, sheetName, clearButton);

		return sheetLine;
	}


	private HBox spreadsheetLine(boolean deleteButton, VBox spreadsheetListing) {
		HBox sheetLine = spreadsheetLine();
		if (deleteButton) {
			Button delButton = new Button("Delete");
			delButton.setOnAction(new EventHandler<ActionEvent>() {
				
				@Override
				public void handle(ActionEvent event) {
					deleteLine(spreadsheetListing, sheetLine);
				}
			});
			sheetLine.getChildren().add(delButton);
		}
		return sheetLine;
	}


	private void deleteLine(VBox spreadsheetListing, HBox sheetLine) {
		spreadsheetListing.getChildren().remove(sheetLine);
	}
	

	

}
