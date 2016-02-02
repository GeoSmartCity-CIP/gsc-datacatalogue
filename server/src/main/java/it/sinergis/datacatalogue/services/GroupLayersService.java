package it.sinergis.datacatalogue.services;

public class GroupLayersService {

	public static String RESPONSE_JSON_STATUS_DONE =  "{Status:'Done', Description:'Ok'}";
	public static String RESPONSE_JSON_LIST_GROUPLAYER = "{groups:[{groupname:'Group1',layers:[{layername:'Layer1'},{layername:'Layer2'}]},{groupname:'Group2',layers:[{layername:'Layer3'},{layername:'Layer4'}]}]}";
	
	public String createGroupLayer(String req){
		return RESPONSE_JSON_STATUS_DONE;
	}
	
	public String assignLayerToGroup(String req){
		return RESPONSE_JSON_STATUS_DONE;
	}
	
	public String deleteGroupLayer(String req){
		return RESPONSE_JSON_STATUS_DONE;
	}
	
	public String listGroupLayer(String req){
		return RESPONSE_JSON_LIST_GROUPLAYER;
	}
	
}
