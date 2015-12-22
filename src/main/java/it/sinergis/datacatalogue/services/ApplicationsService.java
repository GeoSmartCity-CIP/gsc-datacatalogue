package it.sinergis.datacatalogue.services;

public class ApplicationsService {

	public static String RESPONSE_JSON_STATUS_DONE =  "{Status:'Done', Description:'Ok'}";
	public static String RESPONSE_JSON_LIST_APPLICATION = "{applications:[{applicationname:'ClientWeb',layers:[{layername:'Layer1'},{layername:'Layer2'}],groups:[{groupname:'Group1'},{groupname:'Group2'}]},{applicationname:'Application 2',layers:[{layername:'Layer3'},{layername:'Layer4'}],groups:[{groupname:'Group3'},{groupname:'Group4'}]}]}";
	public static String RESPONSE_JSON_GET_CONFIGURATION = "{}";
	
	public String createApplication(String req){
		return RESPONSE_JSON_STATUS_DONE;
	}
	
	public String assignToApplication(String req){
		return RESPONSE_JSON_STATUS_DONE;		
	}
	
	public String deleteApplication(String req){
		return RESPONSE_JSON_STATUS_DONE;
	}
	
	public String listApplication(String req){
		return RESPONSE_JSON_LIST_APPLICATION;
	}
	
	public String publishToGeoserver(String req){
		return RESPONSE_JSON_STATUS_DONE;
	}
	
	public String getConfiguration(String req){
		return RESPONSE_JSON_GET_CONFIGURATION;
	}
}
