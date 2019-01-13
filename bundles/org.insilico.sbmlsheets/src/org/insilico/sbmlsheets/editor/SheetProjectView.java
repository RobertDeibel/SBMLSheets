package org.insilico.sbmlsheets.editor;

import java.util.ArrayList;
import java.util.List;

import java.io.File;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.insilico.sbmlsheets.core.SheetProject;
import org.insilico.sbmlsheets.core.SheetWriter;

import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import org.insilico.ui.themes.light.*;
import org.w3c.dom.css.CSSStyleDeclaration;
/**
 * Displays the representation of the {@link SheetProject} class, handles inputs, buttons and events
 * @author Robert Deibel
 *
 */
public class SheetProjectView {
	/**
	 * The {@link Sheetproject} injected after {@link SheetProjectDocumentLoader} successfully loaded the {@link SheetProject}.
	 */
	@Inject
	SheetProject project;
	
	ObservableList<String> pathsInDir;
	/**
	 * Called after injection of {@link #project}. Generates the GUI.
	 * @param parent The {@link BorderPane} provided by Insilico.
	 */
	@PostConstruct
	private void init(BorderPane parent) {
		parent = makeView(parent);
		
	}
	
	/**
	 * 
	 * @param parent
	 * @return
	 */
	private BorderPane makeView(BorderPane parent) {
		
		pathsInDir = FXCollections.observableArrayList(project.readFilesInDir(new File(project.getUri().replace(".sheets", ""))));
		
		//creating a ButtonBar
		ButtonBar bottomButtonBar = new ButtonBar();
		//placing ButtonBar at bottom of screen
		parent.setBottom(bottomButtonBar);
		Button compileButton = new Button("_Compile");
		compileButton.setMnemonicParsing(true);
		compileButton.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				System.out.println("Compiling...");
				project.compileSBML();
			}
		});
		Button saveButton = new Button("Save");
		saveButton.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				System.out.println("Syving...");
				project.save();
			}
		});
		bottomButtonBar.getButtons().addAll(compileButton, saveButton);
		
		GridPane gp = new GridPane();
				
		parent.setCenter(gp);
		Text sbmlSpecificationText = new Text("SBML Specification:");
		sbmlSpecificationText.setTextAlignment(TextAlignment.RIGHT);
		sbmlSpecificationText.setBoundsType(TextBoundsType.LOGICAL);
		sbmlSpecificationText.getStyleClass().add("text-id");
		
		TextField sbmlSpecification = new TextField(project.getSpecification());
		sbmlSpecification.setPromptText("Enter SBML specification");
		sbmlSpecification.setAlignment(Pos.CENTER);
		sbmlSpecification.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				project.setSpecification(sbmlSpecification.getText());;
				gp.requestFocus();
			}
		});
		
		Button sbmlSpecificationChangeButton = new Button("Change");
		sbmlSpecificationChangeButton.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				sbmlSpecification.fireEvent(event);
				gp.requestFocus();
			}
		});
		HBox sbmlSpecificationControls = new HBox();
		sbmlSpecificationControls.getChildren().addAll(sbmlSpecification, sbmlSpecificationChangeButton);
		gp.add(sbmlSpecificationText, 0, 0);
		gp.add(sbmlSpecificationControls, 1, 0);
		
		Text sbmlFileNameText = new Text("SBML File Name:");
		sbmlFileNameText.setTextAlignment(TextAlignment.RIGHT);
		sbmlFileNameText.setBoundsType(TextBoundsType.LOGICAL);
		sbmlFileNameText.getStyleClass().add("text-id");
		TextField sbmlFileName = new TextField(project.getSbmlFileName());
		sbmlFileName.setPromptText("Enter SBML file name");
		sbmlFileName.setAlignment(Pos.CENTER);
		sbmlFileName.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				project.setSbmlFileName(sbmlFileName.getText());
				gp.requestFocus();
			}
		});
		
		Button sbmlFileNameChangeButton = new Button("Change");
		sbmlFileNameChangeButton.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				sbmlFileName.fireEvent(event);
				gp.requestFocus();
			}
		});
		
		HBox sbmlFileNameControls = new HBox();
		sbmlFileNameControls.getChildren().addAll(sbmlFileName, sbmlFileNameChangeButton);
		gp.add(sbmlFileNameText, 0, 1);
		gp.add(sbmlFileNameControls, 1, 1);
		VBox spreadsheetListing = new VBox();

		if (project.getPaths().isEmpty()) {
			spreadsheetListing.getChildren().add(spreadsheetLine());
		} else {
			boolean toggle = false;
			for (String path : project.getPaths()) {
				path = path.split(File.separator)[path.split(File.separator).length - 1];
				spreadsheetListing.getChildren().add(spreadsheetLine(toggle, spreadsheetListing, path, project.getName(path)));
				toggle = true;
			}
		}
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
				newSpreadsheetDialog(gp);	
			}


		});
		pathsInDir.addListener(new ListChangeListener<String>() {

			@Override
			public void onChanged(Change<? extends String> c) {
				updateList(spreadsheetListing,c.getList());
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
		gp.add(sp, 1, 2);
		return parent;
		
	}


	private HBox spreadsheetLine(boolean deleteButton, VBox spreadsheetListing, String path, String name) {
		HBox spreadsheetLine = spreadsheetLine(deleteButton, spreadsheetListing);
		for (Node child : spreadsheetLine.getChildren()) {
			if (child instanceof ComboBox<?>) {
				((ComboBox<String>) child).setValue(path);
			} else if (child instanceof TextField) {
				((TextField) child).setText(name);
			}
		}
		
		return spreadsheetLine;
	}

	private void updateList(VBox spreadsheetListing, ObservableList<? extends String> list) {
		for (Node n : spreadsheetListing.getChildren()) {
			if(n instanceof HBox) {
				for (Node field : ((HBox) n).getChildren()) {
					if (field instanceof ComboBox<?>) {
						ObservableList<String> temp = FXCollections.observableArrayList(list);
						temp.removeAll(project.getPaths());
						ObservableList<String> fileNames = getFileNames(temp);
						((ComboBox<String>) field).setItems(fileNames);
					}
				}
			}
		}
	}

	private ObservableList<String> getFileNames(ObservableList<String> list) {
		ObservableList<String> fileNames = FXCollections.observableArrayList();
		for (String path : list) {
			String[] splitPath = path.split(File.separator);
			fileNames.add(splitPath[splitPath.length - 1]);
		}		
		return fileNames;
	}

	/**
	 * 
	 * @return
	 */
	private HBox spreadsheetLine() {
		HBox sheetLine = new HBox();
		ComboBox<String> fileSelection = new ComboBox<>(project.getPaths());
		TextField sheetName = new TextField();
		fileSelection.setPromptText("CSV file");
		fileSelection.setEditable(true);
		fileSelection.setFocusTraversable(true);
		ObservableList<String> fileNames = getFileNames();
		fileSelection.setItems(fileNames);
		fileSelection.setOnKeyReleased(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					if (!sheetName.getText().equals("")) {
						if (pathsInDir.contains(fileSelection.getValue())) {
							project.addPathAndName(fileSelection.getValue(), sheetName.getText());
							sheetLine.requestFocus();
						}
					} else {
						sheetName.requestFocus();
					}
					

				}
			}
			
		});
		sheetName.setPromptText("Enter the Spreadsheet name");
		sheetName.setOnKeyReleased(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				if(event.getCode() == KeyCode.ENTER) {
					sheetName.commitValue();
					if(!fileSelection.getValue().equals("")) {
						sheetLine.requestFocus();
					} else {
						fileSelection.requestFocus();
					}
				}
			}
		});
		Button addButton = new Button("Add");
		addButton.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				if (!sheetName.getText().equals("")) {
					ObservableList<String> fileNames = getFileNames();
					if (fileNames.contains(fileSelection.getValue())) {
						project.addPathAndName(fileSelection.getValue(), sheetName.getText());
					}
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
		sheetLine.getChildren().addAll(fileSelection, sheetName, addButton, clearButton);

		return sheetLine;
	}

	private ObservableList<String> getFileNames() {
		ObservableList<String> fileNames = FXCollections.observableArrayList();
		for (String path : pathsInDir) {
			String[] splitPath = path.split(File.separator);
			fileNames.add(splitPath[splitPath.length - 1]);
		}		
		return fileNames;
	}

	/**
	 * 
	 * @param deleteButton
	 * @param spreadsheetListing
	 * @return
	 */
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

	/**
	 * 
	 * @param spreadsheetListing
	 * @param sheetLine
	 */
	private void deleteLine(VBox spreadsheetListing, HBox sheetLine) {
		spreadsheetListing.getChildren().remove(sheetLine);
	}
	/**
	 * 
	 * @param gp
	 */
	private void newSpreadsheetDialog(GridPane gp) {
		final Stage dialog = new Stage();
		VBox content = new VBox();
		
		Text dialogText = new Text("Create a New CSV File:");
		HBox controls = new HBox();
		content.getChildren().addAll(dialogText,controls);
		dialog.initModality(Modality.APPLICATION_MODAL);
		TextField fileName = new TextField("New File Name");
		fileName.setOnKeyReleased(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					fileName.commitValue();
					newCSV(dialog, fileName.getText());
				}
			}

		});
		
		Button createButton = new Button("Create");
		createButton.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				newCSV(dialog, fileName.getText());
				
				
			}
		});
		
		Button cancelButton = new Button("Cancel");
		cancelButton.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				dialog.hide();
			}
		});
		
		controls.getChildren().addAll(fileName, createButton, cancelButton);
		double xPosn = (Screen.getPrimary().getVisualBounds().getWidth() / 2) - (controls.getWidth() / 2);
		double yPosn = (Screen.getPrimary().getVisualBounds().getHeight() / 2) - (controls.getHeight() / 2);
		dialog.setScene(new Scene(content));
		dialog.initStyle(StageStyle.UTILITY);
		dialog.setX(xPosn);
		dialog.setY(yPosn);
		dialog.showAndWait();
	}

	/**
	 * 
	 * @param dialog
	 * @param fileName
	 */
	private void newCSV(Stage dialog, String fileName) {
		String filePath = project.getUri().replace(".sheets", "") + fileName + (fileName.endsWith("csv") ? "" :".csv");
		if (!pathsInDir.contains(filePath)) {
			SheetWriter.newCSV(filePath);
			pathsInDir.add(filePath);
			dialog.hide();
		}
	}
	

	

}
