package it.sinergis.datacatalogue.services;

public class PermissionsService {

	public static String RESPONSE_JSON_STATUS_DONE =  "{Status:'Done',Description:'Operation ok'}";
	public static String RESPONSE_JSON_LIST_PERM = "{functions:[{name:'function1',description:'first function'},{name:'function2',description:'Second function',layername:'Streets'}]}";

	public String assignPermission(String req){
		return RESPONSE_JSON_STATUS_DONE;
	}
	
	public String listPermission(String req){
		return RESPONSE_JSON_LIST_PERM;
	}
}
