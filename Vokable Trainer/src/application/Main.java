package application;
	
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import com.sun.glass.events.KeyEvent;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.util.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;


public class Main extends Application {
	int Wo = 0;
	ArrayList<String> Deutsche = new ArrayList<>();
	ArrayList<String> Andere = new ArrayList<>();
	TableColumn<String, Vokable> Deutsch = new TableColumn<>("Deutsch");
	TableColumn<TextField, Vokable> AndereSprache = new TableColumn<>("Andere Sprache");
	TableView Vokables = new TableView();
	@Override
	public void start(Stage Fenster) {
		try {
			Deutsch.setCellValueFactory(new PropertyValueFactory<>("VokableDeutsch"));
			AndereSprache.setCellValueFactory(new PropertyValueFactory<>("Vokable"));
			Vokables.setPrefWidth(400);
			Vokables.getColumns().addAll(Deutsch,AndereSprache);
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Load?");
			 
			Optional<ButtonType> result1 = alert.showAndWait();
			 
			if ((result1.isPresent()) && (result1.get() == ButtonType.OK)) {
				Load(new FileChooser().showOpenDialog(new Stage()).getAbsolutePath());
			}
			ContextMenu ctx = new ContextMenu();
			MenuItem addItem = new MenuItem("Add");
			ctx.getItems().addAll(addItem);
			addItem.setOnAction(e ->{
				Dialog<Pair<String, String>> dialog = new Dialog<>();
				dialog.setTitle("Add");

				// Set the icon (must be included in the project).

				// Set the button types.
				ButtonType addButtonType = new ButtonType("Add", ButtonData.OK_DONE);
				dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

				// Create the username and password labels and fields.
				GridPane grid = new GridPane();
				grid.setHgap(10);
				grid.setVgap(10);
				grid.setPadding(new Insets(20, 150, 10, 10));

				TextField DeutschText = new TextField();
				DeutschText.setPromptText("Deutsch");
				TextField AndereText = new TextField();
				AndereText.setPromptText("Andere Sprache");

				grid.add(new Label("Deutsch:"), 0, 0);
				grid.add(DeutschText, 1, 0);
				grid.add(new Label("Andere Sprache:"), 0, 1);
				grid.add(AndereText, 1, 1);

				Node addButton = dialog.getDialogPane().lookupButton(addButtonType);
				addButton.setDisable(true);

				// Do some validation (using the Java 8 lambda syntax).
				DeutschText.textProperty().addListener((observable, oldValue, newValue) -> {
				    addButton.setDisable(newValue.trim().isEmpty());
				});

				dialog.getDialogPane().setContent(grid);

				// Request focus on the username field by default.
				Platform.runLater(() -> DeutschText.requestFocus());

				// Convert the result to a username-password-pair when the login button is clicked.
				dialog.setResultConverter(dialogButton -> {
				    if (dialogButton == addButtonType) {
				        return new Pair<>(DeutschText.getText(),AndereText.getText());
				    }
				    return null;
				});

				Optional<Pair<String, String>> result = dialog.showAndWait();

				result.ifPresent(usernamePassword -> {
					Vokables.getItems().add(new Vokable("",usernamePassword.getKey()));
					Deutsche.add(usernamePassword.getKey());
					Andere.add(usernamePassword.getValue());
				    System.out.println("Username=" + usernamePassword.getKey() + ", Password=" + usernamePassword.getValue());
				});
			});
			Vokables.setOnMousePressed(e ->{
				if(e.getButton().equals(MouseButton.SECONDARY)) {
					ctx.show(Fenster,e.getScreenX(),e.getScreenY());
				}
			});
			Vokables.setOnKeyPressed(e ->{
				if(e.getCode().equals(KeyCode.ENTER)) {
					if(((Vokable)Vokables.getItems().get(Wo)).getVokable().getText().equalsIgnoreCase(Andere.get(Wo))) {
						Vokables.getItems().set(Wo, new Vokable(((Vokable)Vokables.getItems().get(Wo)).getVokable().getText() + "✔",Deutsche.get(Wo)));
						Wo++;
					}
				}
			});
			Group root = new Group(Vokables);
			Scene scene = new Scene(root,400,400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			Fenster.setScene(scene);
			Fenster.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public void stop() throws IOException {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Save?");
		 
		Optional<ButtonType> result = alert.showAndWait();
		 
		if ((result.isPresent()) && (result.get() == ButtonType.OK)) {
			Save(new FileChooser().showSaveDialog(new Stage()).getAbsolutePath());
		}
	}
	public void Load(String Path) throws IOException {
		BufferedReader BR = new BufferedReader(new FileReader(new File(Path)));
        var Line = "Hi";
        Line = BR.readLine();
        if(Line.replace("[", "").replace("]", "").contains(",")) {
        	for(String Deutsch: Line.replace("[", "").replace("]", "").split(", ")) {
        		Deutsche.add(Deutsch);
        		Vokables.getItems().add(new Vokable("",Deutsch));
        	}
        }
        else {
        	Deutsche.add(Line.replace("[", "").replace("]", ""));
        	Vokables.getItems().add(new Vokable("",Line.replace("[", "").replace("]", "")));
        }
        Line = BR.readLine();
        if(Line.replace("[", "").replace("]", "").contains(",")) {
        	for(String Anders: Line.replace("[", "").replace("]", "").split(", ")) {
        		Andere.add(Anders);
        	}
        }
        else {
        	Andere.add(Line.replace("[", "").replace("]", ""));
        }
        BR.close();
        Wo = 0;
        System.out.println(Deutsche);
        System.out.println(Andere);
	}
	public void Save(String Path) throws IOException {
		BufferedWriter Datei = new BufferedWriter(new FileWriter(new File(Path)));
        Datei.write(Deutsche.toString());
        Datei.write("\n" + Andere.toString());
        Datei.close();
	}
	public static void main(String[] args) {
		launch(args);
	}
	public class Vokable {

	    private String VokableDeutsch = "";
	    private TextField Vokable = new TextField("");
	    
	    Vokable(String Andere, String Deutsch){
	    	this.Vokable.setText(Andere);
	    	this.VokableDeutsch = Deutsch;
	    }
	    public String getVokableDeutsch() {
	    	return VokableDeutsch;
	    }
	    public TextField getVokable() {
	    	return Vokable;
	    }
	}
}