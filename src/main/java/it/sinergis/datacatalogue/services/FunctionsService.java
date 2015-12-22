package it.sinergis.datacatalogue.services;

public class FunctionsService {

	public static String RESPONSE_JSON_STATUS_DONE =  "{Status:'Done',Description:'Operation ok'}";
	public static String RESPONSE_JSON_LIST_FUNC = " {functions:[{name:'function1',description:'First function'},{name:'function2',description:'second function'},{name:'function3',description:'Third function'}]}";

	public String createFunction(String req){
		return RESPONSE_JSON_STATUS_DONE;
	}
	
	public String updateFunction(String req){
		return RESPONSE_JSON_STATUS_DONE;
	}
	
	public String deleteFunction(String req){
		return RESPONSE_JSON_STATUS_DONE;
	}
	
	public String listFunction(String req){
		return RESPONSE_JSON_LIST_FUNC;
	}
}
