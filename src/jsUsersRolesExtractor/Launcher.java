package jsUsersRolesExtractor;

import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.time.Duration;
import java.time.Instant;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.w3c.dom.Node;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Launcher extends Application{
	Button button;
	Stage window;
	TextArea stats = new TextArea();
	StringBuilder outputMessages = new StringBuilder();
    TextArea outputLog = new TextArea();

	public static void main(String[] args) {
		launch(args); 
	}

	private String getCurrentDirectory(){
		try {
			String dir = new File(Launcher.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
			return dir.substring(0, dir.indexOf("bin"));
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return null;
		}

	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		StringBuilder outputMessages = new StringBuilder();
		window = primaryStage;
		window.setTitle("Jaspersoft Server Users/Roles Extractor");
		window.setOnCloseRequest(e->{
			e.consume();//We tell java not to close by default. Without it the window would close in either case.
			closeProgram();
		});
		StackPane layout = new StackPane(); 
		button = new Button(); 
		button.setText("Extract");
		button.setMaxWidth(Double.MAX_VALUE);

		button.setStyle("-fx-background-color: radial-gradient(radius 180%, #2ca16e, derive(#2ca16e, -30%), derive(#2ca16e, 30%)); -fx-text-fill: white;");
        registerHandler(button, "#87deb8", "#2ca16e", "white");

        
		layout.getChildren().addAll(button);
		

		HBox topmenu = new HBox(); 
		Menu menuFile = new Menu("File");
		MenuItem fileItem1 = new MenuItem("Open extraction folder");
		fileItem1.setOnAction(e -> {
			try {
				Desktop.getDesktop().open(new File(getCurrentDirectory()));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
		MenuItem fileItem2 = new MenuItem("Exit");
		fileItem2.setOnAction(e->{
			closeProgram();
		});
		SeparatorMenuItem separator1 = new SeparatorMenuItem();
		menuFile.getItems().add(fileItem1);
		menuFile.getItems().add(separator1);
		menuFile.getItems().add(fileItem2);
		
		Menu menuHelp = new Menu("Help");
		MenuItem item1 = new MenuItem("About");
		item1.setOnAction(e->{aboutWindow.display("About JS Server Roles/Users extractor", "Version: JS Server Roles/Users Extractor (1.0.0)",
				"This program extracts users and roles from Jaspersoft server.\nAn Excel file is generated based on the extracted data for further review of access rights.\n\nFor more information please contact:", "fekolkins@gmail.com",
				"Program created in 2018.");});
		menuHelp.getItems().add(item1);
		MenuBar menuBar = new MenuBar();
		menuBar.getMenus().addAll(menuFile,menuHelp);
		topmenu.getChildren().addAll(menuBar);

	    BorderStroke borderStroke = new BorderStroke(Color.LIGHTGRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0,0,1,0));
	    topmenu.setBorder(new Border(borderStroke));
	    
		VBox lowerSection = new VBox();
	    ProgressBar pb = new ProgressBar(0);
        pb.setMinWidth(610);
        pb.setStyle("-fx-accent: green;"); 

		outputLog.setMaxHeight(150);
		outputLog.setEditable(false);
		outputLog.textProperty().addListener(new ChangeListener<Object>() {
		    @Override
		    public void changed(ObservableValue<?> observable, Object oldValue,
		            Object newValue) {
		    	outputLog.setScrollTop(Double.MAX_VALUE); //this will scroll to the bottom
		    }
		});
		
		lowerSection.setAlignment(Pos.CENTER);
		lowerSection.getChildren().addAll(pb, outputLog, button);
		
		HBox middleSection = new HBox();
		VBox statsSection = new VBox();
		
		stats.setMaxWidth(statsSection.getMaxWidth());
		stats.setEditable(false);
		statsSection.getChildren().add(stats);
		statsSection.setPadding(new Insets(20, 20, 20, 20));

		VBox inputSection = new VBox();
		HBox urlInputRow = new HBox();
		Label urlLabel = new Label("JS Server URL: ");
		urlLabel.setMinWidth(100);
		TextField urlInput = new TextField();
		urlInput.setMinWidth(200);
		urlInputRow.getChildren().addAll(urlLabel, urlInput);
		
		HBox orgInputRow = new HBox();
		Label orgLabel = new Label("Organization: ");
		orgLabel.setMinWidth(100);
		TextField orgInput = new TextField();
		orgInput.setMinWidth(200);
		orgInputRow.getChildren().addAll(orgLabel, orgInput);
		
		HBox usernameInputRow = new HBox();
		Label usernameLabel = new Label("Username: ");
		usernameLabel.setMinWidth(100);
		TextField usernameInput = new TextField();
		usernameInput.setMinWidth(200);
		usernameInputRow.getChildren().addAll(usernameLabel, usernameInput);
		
		HBox passwordInputRow = new HBox();
		Label passwordLabel = new Label("Username: ");
		passwordLabel.setMinWidth(100);
		PasswordField passwordInput = new PasswordField();

		//TextField passwordInput = new TextField();
		passwordInput.setMinWidth(200);
		passwordInputRow.getChildren().addAll(passwordLabel, passwordInput);

		HBox configSetupRow = new HBox();
		Button loadConfigButton = new Button("Load Saved Settings");
		Button saveConfigButton = new Button("Save Settings");
		Button clearCongifButton = new Button("Clear Settings");
		//clearCongifButton.setStyle("-fx-background-color: radial-gradient(radius 180%, #ffe0e5, derive(#ffe0e5, -30%), derive(#ffe0e5, 30%)); -fx-text-fill: black;");

		loadConfigButton.setMinWidth(150);
		saveConfigButton.setMinWidth(150);
		configSetupRow.getChildren().addAll(saveConfigButton, loadConfigButton);
		
		HBox configSetupRow2 = new HBox();
		clearCongifButton.setMinWidth(150);
		configSetupRow2.getChildren().addAll(clearCongifButton);
		
		inputSection.getChildren().addAll(urlInputRow, orgInputRow, usernameInputRow, passwordInputRow, configSetupRow, configSetupRow2);
		inputSection.setPadding(new Insets(20, 20, 20, 20));
		middleSection.getChildren().addAll(inputSection, statsSection);
		BorderPane borderPane = new BorderPane();
		borderPane.setTop(topmenu);
		borderPane.setBottom(lowerSection);
		borderPane.setCenter(middleSection);
		borderPane.setStyle("-fx-background-color: #dfdfdf;");
		
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {   
            		outputLog.clear();
                    //Create new Task and Thread -  Bind Progress Property to Task Progress
                    Task task = taskCreator(urlInput.getText(), orgInput.getText(), usernameInput.getText(),passwordInput.getText());
                    new Thread(task).start();
                    pb.progressProperty().unbind();
                    pb.progressProperty().bind(task.progressProperty());
            }
        });
		
		saveConfigButton.setOnAction(e -> {
			if(urlInput.getText().length() == 0 || orgInput.getText().length() == 0 || usernameInput.getText().length() == 0 || passwordInput.getText().length() == 0){
				outputLog.setText("[Error] Configuration settings are not specified.");
				outputLog.appendText("");
			}
			else{
				saveConfigSettings(urlInput.getText(), orgInput.getText(), usernameInput.getText(), passwordInput.getText());
				outputMessages.append("Configuration Settings Saved.\n");
				outputLog.setText(outputMessages.toString());
				outputLog.appendText("");
			}
		});
		
		loadConfigButton.setOnAction(e -> {
			try{
				urlInput.setText(loadConfigSettings()[0]);
				orgInput.setText(loadConfigSettings()[1]);
				usernameInput.setText(loadConfigSettings()[2]);
				passwordInput.setText(loadConfigSettings()[3]);
				outputMessages.append("Configuration Settings Loaded Succesfully.\n");
				outputLog.setText(outputMessages.toString());
			}catch(NullPointerException e1){
  				outputMessages.append("[Error] Could not find the configuration file. Please try saving new configuration settings.\n");
   				outputLog.setText(outputMessages.toString());
			}
		});
		
		clearCongifButton.setOnAction(e -> {
			urlInput.clear();
			orgInput.clear();
			usernameInput.clear();
			passwordInput.clear();
			outputLog.clear();
			outputMessages.setLength(0);
			outputMessages.append("Configuration Settings Cleared.\n");
			outputLog.setText(outputMessages.toString());
			outputLog.appendText("");
		});
		
		Scene scene = new Scene(borderPane, 600, 400);
		scene.setFill(Color.BLUE);
		window.setScene(scene);
		window.setResizable(false);
		window.show();
	}
	
    private Task taskCreator(String url, String org, String username, String pwd){
        return new Task() {
                   @Override
                   protected Object call() throws Exception {
          			outputMessages.setLength(0);
               		//Create a class for JS extractor and move all methods here. Keep the Initiator class to just create the extractor class.
               		//THe constructor should invoke a run() method that will invoke all methods that are currently in the main.
               		JSUsersRolesExtractor ex = new JSUsersRolesExtractor();
       				Instant start = Instant.now();
       				ex.setUpConfig(url, org, username, pwd);	
       				updateProgress(0, 1.0);
       				String urlReq = ex.setUpLoginReq();
       				updateProgress(0.025, 1.0);
               		//ALSO CHECK WHEN CONNECTED TO VPN
               		try{
               			if(!isNetworkAvailable()){
               				updateProgress(0.0, 1.0);
              				outputMessages.append("[Error] Could not connect to the network\n");
               				outputLog.setText(outputMessages.toString());
               				outputLog.appendText(""); 
               			}
               			else if(!checkInputValidity(url, org, username, pwd)){
               				updateProgress(0.0, 1.0);
              				outputMessages.append("[Error] Invalid input. Verify validity of the configuration settings.\n");
               				outputLog.setText(outputMessages.toString());
               				outputLog.appendText(""); 
               			}
               			else if(!canConnectToJSServer(urlReq)){
               				updateProgress(0.0, 1.0);
              				outputMessages.append("[Error] Could not connect to the Jaspersoft server. Verify validity of the authentication credentials.\n");
               				outputLog.setText(outputMessages.toString());
               				outputLog.appendText(""); 
               			}
               			else{
               				String usersUrl = ex.setUpUsersURL();
               				updateProgress(0.05, 1.0);
               				String rolesUrl = ex.setUpRolesURL();
               				updateProgress(0.1, 1.0);
               				outputMessages.append("Configuration set up.\nExtracting data from the server...\n");
               				outputLog.setText(outputMessages.toString());
               				StringBuilder usersXml = ex.extractUsersFromServer(urlReq, usersUrl);
               				updateProgress(0.3, 1.0);
               				outputMessages.append("Users raw data extracted.\n");
               				outputLog.setText(outputMessages.toString());
               				StringBuilder rolesXml = ex.extractRolesFromServer(urlReq, rolesUrl);
               				updateProgress(0.5, 1.0);
              				outputMessages.append("Roles raw data extracted.\n");
               				outputLog.setText(outputMessages.toString());
               				ex.createJSRolesXML(rolesXml);
               				updateProgress(0.6, 1.0);
              				outputMessages.append("XML generated for roles.\n");
               				outputLog.setText(outputMessages.toString());
               				ex.createJSUserXML(usersXml);
               				updateProgress(0.7, 1.0);
              				outputMessages.append("XML generated for users.\n");
               				outputLog.setText(outputMessages.toString());
               				outputLog.appendText(""); 

               				try {
               					ex.extractUsers();
                   				updateProgress(0.8, 1.0);
                  				outputMessages.append("Users extracted from XML.\n");
                   				outputLog.setText(outputMessages.toString());
                   				outputLog.appendText(""); 
               					ex.extractRoles();
                   				updateProgress(0.9, 1.0);
                  				outputMessages.append("Roles extracted from XML.\nCreating Excel file...\n");
                   				outputLog.setText(outputMessages.toString());
                   				outputLog.appendText(""); 
               					ex.createExcel();
                   				updateProgress(1.0, 1.0);
                  				outputMessages.append("Excel file created succesfully!.\n");
                   				outputLog.setText(outputMessages.toString());
                   				outputLog.appendText(""); 
                   				outputMessages.append("Extraction completed.\n");
                   				outputLog.setText(outputMessages.toString());
                   				outputLog.appendText(""); //To trigger the event listener assigned to the text area for log output
               				} catch(OpenExcelFileException e1){
                   				updateProgress(0.0, 1.0);
                   				outputMessages.setLength(0);
               					outputLog.clear();
                  				outputMessages.append("[Error] Can't overwrite the existing Excel file. Please close the Excel file first and try again.\n");
                   				outputLog.setText(outputMessages.toString());
                   				outputLog.appendText(""); 
               				} catch (ParserConfigurationException | IOException e) {
               					updateProgress(0.0, 1.0);
               					outputLog.clear();
                  				outputMessages.append("[Error] Extraction failed\n");
                   				outputLog.setText(outputMessages.toString());
               				}
               			}
               		}catch (SAXParseException | IllegalArgumentException e){
               			//System.err.println("[Error] Invalid input. Verify validity of the configuration settings.");
           				updateProgress(0.0, 1.0);
           				outputMessages.setLength(0);
       					outputLog.clear();
          				outputMessages.append("[Error] Invalid input. Verify validity of the configuration settings.\n");
           				outputLog.setText(outputMessages.toString());
               		} 
               		Instant end = Instant.now();
       				Duration d = Duration.between(start, end);
       				float elapsedTime = (float)d.toMillis()/1000;
       				stats.setText("Elapsed time: " + elapsedTime
       				+ " sec\n" + "JS Server Version: " + ex.getJSversion() 
       				+ "\nJS Server Build: " + ex.getJSBuild()
       				+ "\nUsers extracted: " + ex.getRolesUsersCount()[1]
       				+ "\nRoles extracted: " + ex.getRolesUsersCount()[0]);
                    return true;
                   }
               };
    }

    private void registerHandler(Button b, String colorEnter, String colorExit, String textColor) {
        b.setOnMouseEntered( e -> b.setStyle("-fx-background-color: radial-gradient(radius 180%," + colorEnter + ", derive(" + colorEnter + ", -30%), derive(" + colorEnter + ", 30%)); -fx-text-fill:" + textColor + ";"));
        b.setOnMouseExited(e ->  b.setStyle("-fx-background-color: radial-gradient(radius 180%," + colorExit + ", derive(" + colorExit + ", -30%), derive(" + colorExit + ", 30%)); -fx-text-fill:" + textColor + ";"));
    }
    
    private void saveConfigSettings(String url, String org, String username, String pwd) {
    	Element root=new Element("configuration");
    	Document doc=new Document();  
    	Element child1=new Element("url");
    	child1.addContent(url);
    	Element child2=new Element("organization");
    	child2.addContent(org);
    	Element child3=new Element("username");
    	child3.addContent(username);
    	Element child4=new Element("password");
    	child4.addContent(pwd);

    	root.addContent(child1);
    	root.addContent(child2);
    	root.addContent(child3);
    	root.addContent(child4);

    	doc.setRootElement(root);

    	XMLOutputter outter=new XMLOutputter();
    	outter.setFormat(Format.getPrettyFormat());
    	try {
			outter.output(doc, new FileWriter(new File("configSettings.xml")));
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    private String[] loadConfigSettings() throws NullPointerException{
    	File inputFile = new File("configSettings.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        String[] config = new String[4];
        try {
			dBuilder = dbFactory.newDocumentBuilder();
	        org.w3c.dom.Document doc = dBuilder.parse(inputFile);
	        doc.getDocumentElement().normalize();
	        XPath xPath =  XPathFactory.newInstance().newXPath();
	        String expression = "/configuration";	        
	        NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);
	        Node nNode = nodeList.item(0);
	        org.w3c.dom.Element eElement = (org.w3c.dom.Element) nNode;
	        config[0] = eElement.getElementsByTagName("url").item(0).getTextContent();
	        config[1] = eElement.getElementsByTagName("organization").item(0).getTextContent();
	        config[2] = eElement.getElementsByTagName("username").item(0).getTextContent();
	        config[3] = eElement.getElementsByTagName("password").item(0).getTextContent();
	        return config;
		} catch (ParserConfigurationException | NullPointerException | SAXException | IOException | XPathExpressionException e) {
			return null;
		}
    }
    
	private void closeProgram(){
		Boolean answer = confirmWindow.display("Exit", "Sure you want to exit?");
		if(answer){ 
			System.out.println("Program exited!");
			window.close();//Close the window
			System.exit(0);//Terminate execution
			
		}
	}
	
	private boolean canConnectToJSServer(String loginReq){	
	    try { 
	        final URL url = new URL(loginReq);
	        final URLConnection conn = url.openConnection();
	        conn.connect();
	        conn.getInputStream().close();
	        return true;
	    } catch (MalformedURLException e) {
	        throw new RuntimeException(e);
	    } catch (IOException e) {
	        return false;
	    }
	}
	
	private boolean isNetworkAvailable(){	
	    try { 
	        final URL url = new URL("http://www.google.com");
	        final URLConnection conn = url.openConnection();
	        conn.connect();
	        conn.getInputStream().close();
	        return true;
	    } catch (MalformedURLException e) {
	        throw new RuntimeException(e);
	    } catch (IOException e) {
	        return false;
	    }
	}
	
    private boolean checkInputValidity(String url, String org, String username, String pwd){
    	if(url.length() == 0 || org.length() == 0 || username.length() == 0 || pwd.length() == 0)
    		return false;
    	else
    		return true;
    }
	

}
