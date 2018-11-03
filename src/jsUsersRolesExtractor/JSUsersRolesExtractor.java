package jsUsersRolesExtractor;

import java.net.URL;
import java.net.URLConnection;
import java.rmi.UnknownHostException;
import java.util.ArrayList;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.BasicConfigurator;

import java.io.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class JSUsersRolesExtractor {
	private String url, org, username, pwd;
	private ArrayList<String> roles;
	private ArrayList<JSUser> users;
	private float elapsedTime;
	private StringBuilder outputMessage;
	private int[] countRolesUsers;
	
	public JSUsersRolesExtractor(){
		outputMessage = new StringBuilder();
		countRolesUsers = new int[2];
	}
	
	public void setUpConfig(String url, String org, String username, String pwd){
		this.url = url;
		this.org = org;
		this.username = username;
		this.pwd = pwd;
	}
	
	public StringBuilder getOutputMessage(){
		return outputMessage;
	}
	
	public float getElapsedTime(){
		return elapsedTime;
	}
	
	public String setUpLoginReq(){
		return url + "/rest/login?j_username=" + username + "%7C" + org + "&j_password=" + pwd;
	}
	
	public String setUpUsersURL(){
		return url + "/rest_v2/users/";
	}
	
	public String setUpRolesURL(){
		return url + "/rest_v2/roles/";
	}
	
	public String getJSversion(){
		String JSversionURL = url + "/rest_v2/serverInfo/version";
		StringBuilder JSVersion = new StringBuilder();
		//"/rest_v2/serverInfo/build"
		try {
			URL env_url = new URL(JSversionURL);
			URLConnection con = env_url.openConnection();
		    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
		    String line;
		    while ((line = bufferedReader.readLine()) != null)
	    	{
	    		JSVersion.append(line + "\n");
	    	}
			bufferedReader.close();
		}catch (UnknownHostException e) {
			e.printStackTrace();
			System.err.println("Failed to Connect");
		}catch (IOException e) {
			e.printStackTrace();

		}
		return JSVersion.toString().trim();
	}
	
	public String getJSBuild(){
		String JSbuildURL = url + "/rest_v2/serverInfo/build";
		StringBuilder JSBuild = new StringBuilder();
		//"/rest_v2/serverInfo/build"
		try {
			URL env_url = new URL(JSbuildURL);
			URLConnection con = env_url.openConnection();
		    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
		    String line;
		    while ((line = bufferedReader.readLine()) != null)
	    	{
		    	JSBuild.append(line + "\n");
	    	}
			bufferedReader.close();
		}catch (UnknownHostException e) {
			e.printStackTrace();
			System.err.println("Failed to Connect");
		}catch (IOException e) {
			e.printStackTrace();

		}
		return JSBuild.toString().trim();
	}
	
	public StringBuilder extractUsersFromServer(String authUrl, String usersUrl) throws SAXParseException{
		//We open an authenticated session and then while keeping it open we extract the xml of users from the js server by opening a new connection.
		//Once we are done, we close the conneciton for extraction and close the exterior authenticated connection. 
		BasicConfigurator.configure();
	    // Create an instance of HttpClient.
	    HttpClient client = new HttpClient();
	    // Create a method instance.
	    GetMethod method = new GetMethod(authUrl);
	    GetMethod method_users = new GetMethod(usersUrl);

	    StringBuilder st_users = new StringBuilder();

	    // Provide custom retry handler is necessary
	    method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));
	    method_users.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));
	    try {
	      // Execute the method.
	      client.executeMethod(method);
	      int statusCode_users = client.executeMethod(method_users);
	      if (statusCode_users != HttpStatus.SC_OK) {
	    	  System.err.println("Method failed: " + method_users.getStatusLine());
	      }
	      // Read the response body.
	      byte[] responseBody_users = method_users.getResponseBody();
	      // Deal with the response.
	      // Use caution: ensure correct character encoding and is not binary data
	      st_users.append(new String(responseBody_users));
	    }catch (UnknownHostException e) {
		  System.err.println("Failed to establish connection: " + e.getMessage());
		  //outputMessage.append("[Error] Failed to establish connection: " + e.getMessage());
		  e.printStackTrace();
		}catch (HttpException e) {
	      System.err.println("Fatal protocol violation: " + e.getMessage());
		  //outputMessage.append("[Error] Fatal protocol violation: " + e.getMessage());
	      e.printStackTrace();
	    } catch (IOException e) {
	      System.err.println("Fatal transport error: " + e.getMessage());
		  //outputMessage.append("[Error] Fatal transport error: " + e.getMessage());
	      e.printStackTrace();
	    } finally {
	      // Release the connections.
	      method_users.releaseConnection();
	      method.releaseConnection();
	    } 
	    return st_users;
	}
	
	public StringBuilder extractRolesFromServer(String authUrl, String rolesUrl) throws SAXParseException{
		//We open an authenticated session and then while keeping it open we extract the xml of users from the js server by opening a new connection.
		//Once we are done, we close the conneciton for extraction and close the exterior authenticated connection. 
		BasicConfigurator.configure();
	    // Create an instance of HttpClient.
	    HttpClient client = new HttpClient();
	    // Create a method instance.
	    GetMethod method = new GetMethod(authUrl);
	    GetMethod method_roles = new GetMethod(rolesUrl);

	    StringBuilder st_roles = new StringBuilder();

	    // Provide custom retry handler is necessary
	    method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));
	    method_roles.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));
	    try {
	      // Execute the method.
	      client.executeMethod(method);
	      int statusCode_users = client.executeMethod(method_roles);
	      if (statusCode_users != HttpStatus.SC_OK) {
	    	  System.err.println("Method failed: " + method_roles.getStatusLine());
	      }
	      // Read the response body.
	      byte[] responseBody_users = method_roles.getResponseBody();
	      // Deal with the response.
	      // Use caution: ensure correct character encoding and is not binary data
	      st_roles.append(new String(responseBody_users));
	    }catch (UnknownHostException e) {
		  System.err.println("Failed to establish connection: " + e.getMessage());
		  //outputMessage.append("[Error] Failed to establish connection: " + e.getMessage());
		  e.printStackTrace();
		}catch (HttpException e) {
	      System.err.println("Fatal protocol violation: " + e.getMessage());
		  //outputMessage.append("[Error] Fatal protocol violation: " + e.getMessage());
	      e.printStackTrace();
	    } catch (IOException e) {
	      System.err.println("Fatal transport error: " + e.getMessage());
		  //outputMessage.append("[Error] Fatal transport error: " + e.getMessage());
	      e.printStackTrace();
	    } finally {
	      // Release the connections.
	      method_roles.releaseConnection();
	      method.releaseConnection();
	    } 
	    return st_roles;
	}
	
	public void createJSUserXML(StringBuilder sb){
    	try (BufferedWriter bw = new BufferedWriter(new FileWriter("JS_users.txt"))) {
    		bw.append(sb);//Internally it does aSB.toString();
    		bw.flush();
    	} catch (IOException e) {
  		  	//outputMessage.append("[Error] Failed to create XML: " + e.getMessage());
    		e.printStackTrace();
    	}
    }
    
	public void createJSRolesXML(StringBuilder sb){
    	try (BufferedWriter bw = new BufferedWriter(new FileWriter("JS_roles.txt"))) {
    		bw.append(sb);//Internally it does aSB.toString();
    		bw.flush();
    	} catch (IOException e) {
  		  	//outputMessage.append("[Error] Failed to create XML: " + e.getMessage());
    		e.printStackTrace();
    	}
    }
    
    //The roles will be used as column headers in Excel
    public void extractRoles() throws ParserConfigurationException, IOException{
    	roles = new ArrayList<>();
        try {
            File inputFile = new File("JS_roles.txt");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder;

            dBuilder = dbFactory.newDocumentBuilder();

            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            XPath xPath =  XPathFactory.newInstance().newXPath();

            String expression = "/roles/role";	        
            NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);

            for (int i = 0; i < nodeList.getLength(); i++) {
               Node nNode = nodeList.item(i);
               //System.out.println("\nCurrent Element :" + nNode.getNodeName());
               
               if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                  Element eElement = (Element) nNode;
                  //Check if the node emailAddress exists, otherwise leave the email field empty.
                  if(eElement.getElementsByTagName("name").getLength() == 0) {
                	  continue;
                  } else {
                	  roles.add(eElement.getElementsByTagName("name").item(0).getTextContent());
                  }
               }
            }
            System.out.println("Roles #: " + roles.size());
            countRolesUsers[0] = roles.size();
         } catch (ParserConfigurationException e) {
            e.printStackTrace();
         } catch(SAXParseException e){
             e.printStackTrace();
         } catch (SAXException e) {
            e.printStackTrace();
         } catch (IOException e) {
            e.printStackTrace();
         } catch (XPathExpressionException e) {
            e.printStackTrace();
         }
    }
    
    public void extractUsers() throws ParserConfigurationException, IOException{
    	users = new ArrayList<>();
    	ArrayList<String> userRoles;
        try {
            File inputFile = new File("JS_users.txt");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder;

            dBuilder = dbFactory.newDocumentBuilder();

            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            XPath xPath =  XPathFactory.newInstance().newXPath();

            String expression = "/users/user";	        
            NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < nodeList.getLength(); i++) {
               Node nNode = nodeList.item(i);
               //System.out.println("\nCurrent Element :" + nNode.getNodeName());
               userRoles = new ArrayList<String>();
               
               if (nNode.getNodeType() == Node.ELEMENT_NODE) {
            	  JSUser user = new JSUser(); //Create a new user for each user node
                  Element eElement = (Element) nNode;
                  int count = 0;
                  do{
                	  userRoles.add(eElement.getElementsByTagName("name").item(count).getTextContent());
                      count++;
                  } while(eElement.getElementsByTagName("role").item(count) != null);
                  user.setRoles(userRoles); //Assign the list of roles to the user object

                  //Check if the node emailAddress exists, otherwise leave the email field empty.
                  if(eElement.getElementsByTagName("emailAddress").getLength() == 0){
                	  user.setUsername(" ");
                  }
                  else {
                	  user.setUsername(eElement.getElementsByTagName("emailAddress").item(0).getTextContent());
                  }
                  if(eElement.getElementsByTagName("fullName").getLength() == 0){
                	  user.setName(" ");
                  }
                  else {
                	  user.setName(eElement.getElementsByTagName("fullName").item(0).getTextContent());
                  }
                  
                  //System.out.println("Username : " + user.getUsername());
                  //System.out.println("Full Name : " + user.getName());
                  //System.out.println("Roles : " + user.getRoles().toString());
                  users.add(user);
               }
            }
            System.out.println("Users #: " + users.size());
            countRolesUsers[1] = users.size();
         } catch (ParserConfigurationException e) {
            e.printStackTrace();
         } catch (SAXParseException e){
             e.printStackTrace();
         } catch (SAXException e) {
            e.printStackTrace();
         } catch (IOException e) {
            e.printStackTrace();
         } catch (XPathExpressionException e) {
            e.printStackTrace();
         }
    }
    
    public void createExcel() throws OpenExcelFileException{
        //Check if the file is already open before proceeding with creation of the Excel file
        String fileName = "Extracted_JS_users.xlsx";        
        File file = new File(fileName);
        // try to rename the file with the same name
        File sameFileName = new File(fileName);
        File tmpDir = new File("Extracted_JS_users.xlsx");
        boolean exists = tmpDir.exists();
        if(exists && !file.renameTo(sameFileName))//Check if the excel file exists
        	{
        		// if the file didnt accept the renaming operation (i.e. the file is in use/opened)
        		throw new OpenExcelFileException("Excel file is already open and can not be overwritten.");
        	}
        else{
	            // if the file is renamed/closed then proceed with generation of the Excel file
		    	// Create a Workbook
		        Workbook workbook = new XSSFWorkbook(); // new HSSFWorkbook() for generating `.xls` file
		
		        /* CreationHelper helps us create instances of various things like DataFormat, 
		           Hyperlink, RichTextString etc, in a format (HSSF, XSSF) independent way */
		        CreationHelper createHelper = workbook.getCreationHelper();
		
		        // Create a Sheet
		        Sheet sheet = workbook.createSheet("JS_Users");
		
		        // Create a Font for styling header cells
		        Font headerFont = workbook.createFont();
		        headerFont.setBold(true);
		        headerFont.setFontHeightInPoints((short) 14);
		        headerFont.setColor(IndexedColors.BLACK.getIndex());
		
		        // Create a CellStyle with the font
		        CellStyle headerCellStyle = workbook.createCellStyle();
		        headerCellStyle.setFont(headerFont);
		
		        // Create a Row
		        Row headerRow = sheet.createRow(0);
		
		        // Create cells
		        //Take the number of roles and plus 2 columns for username and full name
		        for(int i = 0; i < roles.size() + 2; i++) {
		            Cell cell = headerRow.createCell(i);
		            if(i == 0)
		                cell.setCellValue("Username");
		            else if (i == 1)
		                cell.setCellValue("Name");
		            else{
		            	//The full name and username take the first two columns, but we still want to process all roles starting from 0 (i.e. extract 2)
		            	cell.setCellValue(roles.get(i - 2));
		            }
		            cell.setCellStyle(headerCellStyle);
		        }
		
		        // Create Cell Style for formatting Date
		        CellStyle dateCellStyle = workbook.createCellStyle();
		        dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd-MM-yyyy"));
		
		        // Create Other rows and cells with users data
		        int rowNum = 1; //First row is for column headers
		        for(JSUser user : users){
		            Row row = sheet.createRow(rowNum++);
		            row.createCell(0)
	                .setCellValue(user.getUsername());
		            row.createCell(1)
	                .setCellValue(user.getName());
		            //GO through all roles and if user has that role put *
		            for(int i = 0; i < roles.size(); i++){
		            	if(user.getRoles().contains(roles.get(i))){
		            		row.createCell(i + 2).setCellValue("*");
		            	}
		            	else
			            	if(user.getRoles().contains(roles.get(i)))
			            		row.createCell(i + 2).setCellValue(" ");
		            }
		        }
		        
		        //Resize cell sizes to match the cell content
		        for(int i = 0; i < roles.size() + 2; i++) {
		            sheet.autoSizeColumn(i);
		        }
		
		        // Write the output to a file
		        FileOutputStream fileOut;
	    		try {
	    			fileOut = new FileOutputStream(fileName);
	    		    // try to rename the file with the same name
	    	        workbook.write(fileOut);
	    	        fileOut.close();
	    	        // Closing the workbook
	    	        workbook.close();
	    		} catch (IOException e) {
	    			System.err.println("An error occured when generating the Excel file.");
	    			e.printStackTrace();
	    		}  
	        }
        
    }
    
    public int[] getRolesUsersCount(){
    	return countRolesUsers;
    }
}

