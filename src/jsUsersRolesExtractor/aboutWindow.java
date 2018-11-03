package jsUsersRolesExtractor;

import java.awt.Font;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class aboutWindow {
	public static void display(String title, String header, String message, String contactEmail, String labelInfo){
		Text messageText = new Text(message);
		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL); 
		window.setTitle(title);
		window.setMinWidth(350);
		TextFlow aboutText = new TextFlow(messageText, new Hyperlink(contactEmail));
		Text programNameHeader = new Text(header);
		programNameHeader.setStyle("-fx-font-weight: bold");
		Label info = new Label(labelInfo);
		
		VBox layout = new VBox();
		
		layout.getChildren().addAll(programNameHeader, aboutText, info);
		layout.setAlignment(Pos.CENTER_LEFT);
		layout.setPadding(new Insets(10,10,10,10));
		
		Scene scene = new Scene(layout);
		window.setScene(scene);
		window.setResizable(false);
		window.showAndWait();		
	}
}
