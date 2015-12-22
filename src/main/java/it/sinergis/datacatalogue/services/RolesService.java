package it.sinergis.datacatalogue.services;

public class RolesService {

	public static String RESPONSE_JSON_STATUS_DONE = "{Status:'Done',Description:'Operation ok'}";
	public static String RESPONSE_JSON_LIST_ROLE = " {roles:[{name:'Role1',description:'Admin role',users:[{username:'Admin'},{username:'Administrator'}]},{name:'Guest',description:'Generic user',users:[]},{name:'Role2',description:'Second role',users:[{username:'user1'}]}]}";
	
	
	public String createRole(String req){
		return RESPONSE_JSON_STATUS_DONE;
	}
	
	public String deleteRole(String req){
		return RESPONSE_JSON_STATUS_DONE;
	}
	
	public String listRole(String req){
		return RESPONSE_JSON_LIST_ROLE;
	}
	
	public String assignRole(String req){
		return RESPONSE_JSON_STATUS_DONE;
	}
}
