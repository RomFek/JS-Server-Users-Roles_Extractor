package jsUsersRolesExtractor;

import java.util.ArrayList;

public class JSUser {

	private String username, name;
	private ArrayList<String> roles;
	
	public JSUser(){
	}
	
	public void setUsername(String username){
		this.username = username;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setRoles(ArrayList<String> roles){
		this.roles = roles;
	}
	
	public String getUsername(){
		return username;
	}
	
	public String getName(){
		return name;
	}
	
	public ArrayList<String> getRoles(){
		return roles;
	}
}
