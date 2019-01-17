package org.insilico.sbmlsheets.editor;

import java.util.ArrayList;
import java.util.List;

import java.io.File;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.swing.plaf.synth.SynthSpinnerUI;

import org.eclipse.e4.core.contexts.Active;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimmedWindow;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
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
import javafx.stage.FileChooser;
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
	
	@Inject 
	MTrimmedWindow mWindow;
	
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
	 * Builds the View of the {@link SheetProject} in {@link #project}.
	 * @param parent The parent {@link BorderPane} {@link Node} provided by Insilico
	 * @return The View representing {@link #project}.
	 */
	private BorderPane makeView(BorderPane parent) {
		
		pathsInDir = FXCollections.observableArrayList(project.readFilesInDir(new File(project.getUri().replace(".sheets", ""))));
		
		//creating a ButtonBar
		ButtonBar bottomButtonBar = new ButtonBar();
		//placing ButtonBar at bottom of screen
		parent.setBottom(bottomButtonBar);
		//Button for compilation of an SBML project
		Button compileButton = new Button("_Compile");
		//enable fireing of button through Alt+C
		compileButton.setMnemonicParsing(true);
		compileButton.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				System.out.print("Compiling...");
				project.compileSBML();
				System.out.println("Done");
			}
		});
		//Button for saving the SheetsProject file to .sheets
		Button saveButton = new Button("_Save");
		saveButton.setMnemonicParsing(true);
		saveButton.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				System.out.print("Saving...");
				project.save();
				System.out.println("Done");
			}
		});
		//Button for reading an SBML file and loading the Sheets/csv files accordingly
		Button addSBMLButton = new Button("_Read from SBML");
		addSBMLButton.setMnemonicParsing(true);
		addSBMLButton.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				System.out.print("Reading SBML file...");
				choseSBMLfile();
				System.out.println("Done");
			}
			
			

		});
		bottomButtonBar.getButtons().addAll(addSBMLButton, saveButton, compileButton);
		
		//main area is a gridpane
		GridPane gp = new GridPane();
				
		parent.setCenter(gp);
		//Text
		Text sbmlSpecificationText = new Text("SBML Specification:");
		sbmlSpecificationText.setTextAlignment(TextAlignment.RIGHT);
		sbmlSpecificationText.setBoundsType(TextBoundsType.LOGICAL);
		//needed to not look like shit
		sbmlSpecificationText.getStyleClass().add("text-id");
		//the SBML specification is defined here
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
		//Button to set SBML specification
		Button sbmlSpecificationChangeButton = new Button("Change");
		sbmlSpecificationChangeButton.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				sbmlSpecification.fireEvent(event);
				gp.requestFocus();
			}
		});
		//HBox combining the SBML specification controls: Button & TextField
		HBox sbmlSpecificationControls = new HBox();
		sbmlSpecificationControls.getChildren().addAll(sbmlSpecification, sbmlSpecificationChangeButton);
		//add to GridPane
		gp.add(sbmlSpecificationText, 0, 0);
		gp.add(sbmlSpecificationControls, 1, 0);
		
		//More text
		Text sbmlFileNameText = new Text("SBML File Name:");
		sbmlFileNameText.setTextAlignment(TextAlignment.RIGHT);
		sbmlFileNameText.setBoundsType(TextBoundsType.LOGICAL);
		//not look like shit
		sbmlFileNameText.getStyleClass().add("text-id");
		
		//the SBML file name is defined here
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
		//Button for changing the SBML file name
		Button sbmlFileNameChangeButton = new Button("Change");
		sbmlFileNameChangeButton.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				sbmlFileName.fireEvent(event);
				gp.requestFocus();
			}
		});
		//HBox combining the SBML file name controls: Button & TextField
		HBox sbmlFileNameControls = new HBox();
		sbmlFileNameControls.getChildren().addAll(sbmlFileName, sbmlFileNameChangeButton);
		gp.add(sbmlFileNameText, 0, 1);
		gp.add(sbmlFileNameControls, 1, 1);
		//VBox for all spreadsheets
		VBox spreadsheetListing = new VBox();
		//check if there are any Spreadsheets in project
		if (project.getPaths().isEmpty()) {
			//add one empty line if not 
			spreadsheetListing.getChildren().add(spreadsheetLine());
		} else {
			//for every defined Spreadsheet add one line, the first without a deleteButton
			boolean toggle = false;		//flag for the deleteButton; first line is without, others are with
			for (String path : project.getPaths()) {
				path = path.split(File.separator)[path.split(File.separator).length - 1];
				spreadsheetListing.getChildren().add(spreadsheetLine(toggle, spreadsheetListing, path, project.getName(path)));
				toggle = true;		//toggle flag
			}
		}
		//include the listing in a ScrollPane to enable scrolling if the listing is to large
		ScrollPane sp = new ScrollPane(spreadsheetListing);
		
		//VBox for the controls of the spreadsheetListing
		VBox spreadsheetControls = new VBox();
		//Text
		Text spreadsheetText = new Text("Spreadsheets:");
		spreadsheetText.setTextAlignment(TextAlignment.RIGHT);
		//not look like shit
		spreadsheetText.getStyleClass().add("text-id");
		//ButtonBar for add and new Buttons
		ButtonBar spreadsheetControlButtonBar = new ButtonBar();
		//Button to create a new csv
		Button newSpreadsheetButton = new Button("new CSV");
		newSpreadsheetButton.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				newSpreadsheetDialog(gp);	
			}


		});
		//Add a listener to the pathsInDir ObservableList to trigger updateList every time a new csv is added
		pathsInDir.addListener(new ListChangeListener<String>() {

			@Override
			public void onChanged(Change<? extends String> c) {
				updateList(spreadsheetListing,c.getList());
			}
		});
		//Button to add a Spreadsheet to the spreadsheetListing
		Button addSpreadsheetButton = new Button("Add");
		addSpreadsheetButton.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				spreadsheetListing.getChildren().add(spreadsheetLine(true, spreadsheetListing));
			}
		});
		
		//add Buttons to ButtonBar
		spreadsheetControlButtonBar.getButtons().addAll(newSpreadsheetButton, addSpreadsheetButton);
		//Add text and ButtonBar to VBox
		spreadsheetControls.getChildren().addAll(spreadsheetText, spreadsheetControlButtonBar);
		//Add VBox to GridPane
		gp.add(spreadsheetControls, 0, 2);
		gp.add(sp, 1, 2);
		
		//View Done
		return parent;
		
	}
	
	/**
	 * Opens a {@link FileChooser} to enable the user to choose an SBML file to generate csv files an the content of .sheets
	 * @param parent The {@link BorderPane} {@link Node} provided by Insilico
	 */
	private void choseSBMLfile() {
		final FileChooser fileChooser = new FileChooser();
		configureFileChooser(fileChooser);
		IEclipseContext context = mWindow.getContext();
		Window window = context.get(Window.class);
		try {
			File file = fileChooser.showOpenDialog(window);
			if (!file.equals(null)) {
				System.out.println("Yeah SBML Laden Yeah");
			}
		} catch (NullPointerException e) {
			System.err.println("No File selected");
		}
	}
	
	/**
	 * Configures the {@link FileChooser} given as parameter; sets Title
	 * sets initial Directory as home; sets Extension filters
	 * @param fileChooser A fileChooser; typically for the SBML file selection.
	 */
	private void configureFileChooser(FileChooser fileChooser) {
		  fileChooser.setTitle("View Pictures");
	      fileChooser.setInitialDirectory(
	            new File(System.getProperty("user.home"))
	    		  );
	      fileChooser.getExtensionFilters().addAll(
	                new FileChooser.ExtensionFilter("SBML", "*.sbml"),
	                new FileChooser.ExtensionFilter("XML", "*.xml"),
	                new FileChooser.ExtensionFilter("All", "*.*")
	    		  );
	}

	/**
	 * Create a spreadsheetLine represented by a {@link HBox}. Calls {@link #spreadsheetLine(boolean, VBox))} and 
	 * further builds on it's return value. This function is used to build spreadsheetLines during instantiation of the {@link SheetProject}.
	 * @param deleteButton Indicates if a delete Button should be added
	 * @param spreadsheetListing The current spreadsheetListing; a {@link VBox} containing one or more {@link HBox} objects
	 * created by {@link #spreadsheetLine()}, {@link #spreadsheetLine(boolean, VBox)} or {@link #spreadsheetLine(boolean, VBox, String, String)}.
	 * @param path The path to be initially set in the {@link ComboBox}.
	 * @param name The name to be initially set in the {@link TextField}.
	 * @return A pre-filled {@link HBox} filled during instantiation.
	 */
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
	
	/**
	 * Sets the lists in the {@link ComboBox} objects to be {@code list} without the entries in {@link org.insilico.sbmlsheets.core.SheetProject#getPaths()}
	 * @param spreadsheetListing A {@link VBox} containing one or more {@link HBox} Objects as generated by {@link #spreadsheetLine()}, {@link #spreadsheetLine(boolean, VBox)}
	 * or {@link #spreadsheetLine(boolean, VBox, String, String)}.
	 * @param list The list of paths to be set.
	 */
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

	/**
	 * Returns the file names of a list of paths, where the file name is the last section of the path.
	 * @param list An {@link ObservableList} of path {@link String}s.
	 * @return The file names of a list of paths.
	 */
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
