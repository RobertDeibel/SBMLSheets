package org.insilico.sbmlsheets.editor;


import java.io.File;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimmedWindow;
import org.insilico.sbmlsheets.core.Constants;
import org.insilico.sbmlsheets.core.SheetProject;
import org.insilico.sbmlsheets.core.SheetWriter;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextBoundsType;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

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
	
	/**
	 * The {@link MWindow} is a Model Window of the Insilico application. It is injected at instantiation and available
	 * inside the object.
	 */
	@Inject 
	MTrimmedWindow mWindow;
	
	/**
	 * The paths of all csv files in this directory. Needed for the drop-down menus.
	 */
	ObservableList<String> pathsInDir;
	
	private final int PREF_WIDTH = 400;
	
	private final int PREF_HEIGHT_SCROLL = 700;
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
		sbmlSpecification.setPrefWidth(PREF_WIDTH);
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
		sbmlFileName.setPrefWidth(PREF_WIDTH);
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
		sp.setPrefHeight(PREF_HEIGHT_SCROLL);
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
				newSpreadsheetDialog();	
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
			if (child.getId().equals("fileSelection")) {
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
		//for all lines in the Listing
		for (Node n : spreadsheetListing.getChildren()) {
			//check for HBox
			if(n instanceof HBox) {
				//for the fields in the HBox
				for (Node field : ((HBox) n).getChildren()) {
					//check if has fileSelection ID 
					if (field.getId().equals("fileSelection")) {
						ObservableList<String> temp = FXCollections.observableArrayList(list);
						temp.removeAll(project.getPaths());
						ObservableList<String> fileNames = getFileNames(temp);
						//since ID is "fileSelection" field is a ComboBox
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
	 * Creates a HBox containing defining entries for {@link Spreadsheet} objects for {@link #project}. Adds file selection as
	 * {@link ComboBox}, name selection as {@link TextField}, and table selection as {@link ComboBox}. Adds a clear and add {@link Button}
	 * @return A HBox containing defining entries for {@link Spreadsheet} objects for {@link #project}
	 */
	private HBox spreadsheetLine() {
		//collection of the selection fields and buttons
		HBox sheetLine = new HBox();
		//for file selection
		ComboBox<String> fileSelection = new ComboBox<>();
		//set an ID representing the field
		fileSelection.setId("fileSelection");
		//for name definintion
		TextField sheetName = new TextField();
		sheetName.setId("sheetName");
		//for table selection
		ComboBox<String> tableSelection = new ComboBox<>();
		tableSelection.setId("tableSelection");
		//configuration 
		fileSelection.setPromptText("CSV file");
		fileSelection.setEditable(true);
		fileSelection.setFocusTraversable(true);
		//set the drop-down list of fileSelection as returned by getFileNames()
		ObservableList<String> fileNames = getFileNames();
		fileSelection.setItems(fileNames);
		//on ENTER the entries of the fields should be sent to the SheetProject in project
		//only if all of the fields in the line are filled
		fileSelection.setOnKeyReleased(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				//check if ENTER is pressed
				if (event.getCode() == KeyCode.ENTER) {
					sendSheetDef(fileSelection, sheetName, tableSelection);
				}
			}
			
		});
		//configuration
		sheetName.setPromptText("Enter the Spreadsheet name");
		/*same as before:
		 *on ENTER the entries of the fields should be sent to the SheetProject in project
		 *only if all of the fields in the line are filled
		 */
		sheetName.setOnKeyReleased(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				if(event.getCode() == KeyCode.ENTER) {
					//first commit the value in the TextField sheetName
					sheetName.commitValue();
					sendSheetDef(fileSelection, sheetName, tableSelection);
				}
			}
		});
		//configuration
		tableSelection.setPromptText("Select Table Type");
		tableSelection.setEditable(true);
		tableSelection.setFocusTraversable(true);
		//set the drop-down list of tableSelection as Constants.TABLE_TYPES
		tableSelection.setItems(FXCollections.observableArrayList(Constants.TABLE_TYPES));
		/*same as before:
		 *on ENTER the entries of the fields should be sent to the SheetProject in project
		 *only if all of the fields in the line are filled
		 */
		tableSelection.setOnKeyReleased(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					sendSheetDef(fileSelection, sheetName, tableSelection);
				}
			}
		});
		//the addButton has the same functionality as pressing ENTER in one of the fields 
		Button addButton = new Button("Add");
		addButton.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				sendSheetDef(fileSelection, sheetName, tableSelection);
			}
		});
		//the clearButton clears the whole selection line
		Button clearButton = new Button("Clear");
		clearButton.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				project.removeAssociated(fileSelection.getValue());
				fileSelection.setValue(null);
				sheetName.clear();
				tableSelection.setValue(null);
			}

		});
		//add the fields and buttons to the line
		sheetLine.getChildren().addAll(fileSelection, sheetName, tableSelection, addButton, clearButton);

		return sheetLine;
	}

	/**
	 * check if the provided fields are not empty and add the selections to project. Request focus of empty component
	 * @param fileSelection The fileSelection {@link ComboBox}
	 * @param sheetName The {@link TextField} containing the name selection.
	 * @param tableSelection The {@link ComboBox} containing the table type.
	 */
	private void sendSheetDef(ComboBox<String> fileSelection, TextField sheetName, ComboBox<String> tableSelection) {
		//check if fields are filled
		if (selectionNotNull(fileSelection, sheetName, tableSelection)) {
			//check if selections are valid
			if (checkSelections(fileSelection, tableSelection)) {
				project.addPathNameType(fileSelection.getValue(), sheetName.getText(),tableSelection.getValue());
				fileSelection.getParent().requestFocus();
			}
		//request focus for unfinished entries
		} else if (fileSelection.getValue().equals(null)){
			fileSelection.requestFocus();
		} else if (sheetName.getText().equals(null)) {
			sheetName.requestFocus();
		} else {
			tableSelection.requestFocus();
		}
		
	}
	
	/**
	 * Check if one of the values of the parameters is null. 
	 * @param fileSelection The fileSelection {@link ComboBox}
	 * @param sheetName The {@link TextField} containing the name selection.
	 * @param tableSelection The {@link ComboBox} containing the table type.
	 * @return {@code true} if none of the parameter values are null, else {@code false}.
	 */
	private boolean selectionNotNull(ComboBox<String> fileSelection, TextField sheetName,
			ComboBox<String> tableSelection) throws NullPointerException{
		return !fileSelection.getValue().equals(null) && !sheetName.getText().equals(null) && !tableSelection.getValue().equals(null);
	}

	/**
	 * Check if selections for the {@link ComboBox} parameters is possible.
	 * @param fileSelection The fileSelection {@link ComboBox}
	 * @param tableSelection The {@link ComboBox} containing the table type.
	 * @return {@code true} if the selections are possible (contained in the items of the corresponding {@link ComboBox}, {@code false} otherwise.
	 */
	private boolean checkSelections(ComboBox<String> fileSelection, ComboBox<String> tableSelection) {
		return pathsInDir.contains(fileSelection.getValue()) && tableSelection.getItems().contains(tableSelection.getValue());
	}

	/**
	 * Returns the file names of the paths in {@link #pathsInDir}. A file name is the last segment of the path.
	 * @return The file names of the paths in {@link #pathsInDir}.
	 */
	private ObservableList<String> getFileNames() {
		ObservableList<String> fileNames = FXCollections.observableArrayList();
		for (String path : pathsInDir) {
			String[] splitPath = path.split(File.separator);
			fileNames.add(splitPath[splitPath.length - 1]);
		}		
		return fileNames;
	}

	/**
	 *  Creates an HBox containing defining entries for {@link Spreadsheet} objects for {@link #project}. 
	 *  Calls {@link #spreadsheetLine()} and adds a delete {@link Button} if the parameter is set accordingly.
	 *  Calling {@code spreadsheetLine(false, VBox)} results in the same behavior as {@link #spreadsheetLine()}.
	 * @param deleteButton Whether a delete {@link Button} should be added
	 * @param spreadsheetListing The complete list of sheetLines generated by {@link #spreadsheetLine()}, {@link #spreadsheetLine(boolean, VBox)} or 
	 * {@link #spreadsheetLine(boolean, VBox, String, String)}.
	 * @return An HBox containing defining entries for {@link Spreadsheet} objects for {@link #project}. 
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
	 * Deletes the spreadsheet line this function is called from.
	 * @param spreadsheetListing The complete list of sheetLines generated by {@link #spreadsheetLine()}, {@link #spreadsheetLine(boolean, VBox)} or 
	 * {@link #spreadsheetLine(boolean, VBox, String, String)}.
	 * @param sheetLine The {@link HBox} to be deleted
	 */
	private void deleteLine(VBox spreadsheetListing, HBox sheetLine) {
		spreadsheetListing.getChildren().remove(sheetLine);
		for (Node node : sheetLine.getChildrenUnmodifiable()){
			if (node.getId().equals("fileSelection")) {
				String path = ((ComboBox<String>) node).getValue();
				project.removeAssociated(path);
			}
		}
		
	}
	
	/**
	 * Creates a dialog to enter a name of a new csv file
	 */
	private void newSpreadsheetDialog() {
		//the dialog is a new stage
		final Stage dialog = new Stage();
		//content is stored in a VBox
		VBox content = new VBox();
		//text
		Text dialogText = new Text("Create a New CSV File:");
		//HBox for controls
		HBox controls = new HBox();
		content.getChildren().addAll(dialogText,controls);
		//pressing on the 'background' disabled
		dialog.initModality(Modality.APPLICATION_MODAL);
		//a TextField for the new file name
		TextField fileName = new TextField("New File Name");
		fileName.setOnKeyReleased(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					//first commit the current value
					fileName.commitValue();
					//create the new csv
					newCSV(fileName.getText());
					dialog.hide();
				}
			}

		});
		
		//the createButton has the same functionality as pressing ENTER in the TextField
		Button createButton = new Button("Create");
		createButton.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				newCSV(fileName.getText());
				dialog.hide();
				
			}
		});
		
		//the cancelButton closes the dialog
		Button cancelButton = new Button("Cancel");
		cancelButton.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				dialog.hide();
			}
		});
		
		//add the controls and filed
		controls.getChildren().addAll(fileName, createButton, cancelButton);
		//set graphical position as the center of the screen
		double xPosn = (Screen.getPrimary().getVisualBounds().getWidth() / 2) - (controls.getWidth() / 2);
		double yPosn = (Screen.getPrimary().getVisualBounds().getHeight() / 2) - (controls.getHeight() / 2);
		dialog.setScene(new Scene(content));
		/*set the dialog to a utility scene
		 * especially for window managers this enables floating of the scene
		 */
		dialog.initStyle(StageStyle.UTILITY);
		//set the positions (has actually no function)
		dialog.setX(xPosn);
		dialog.setY(yPosn);
		//wait until closed to continue
		dialog.showAndWait();
	}

	/**
	 * Create a new csv file in the current directory.
	 * @param fileName The name of the new csv file. It is irrelevant if it ends with .csv
	 */
	private void newCSV(String fileName) {
		String filePath = project.getUri().replace(".sheets", "") + fileName + (fileName.endsWith("csv") ? "" :".csv");
		if (!pathsInDir.contains(filePath)) {
			SheetWriter.newCSV(filePath);
			pathsInDir.add(filePath);
		}
	}
	

	

}
