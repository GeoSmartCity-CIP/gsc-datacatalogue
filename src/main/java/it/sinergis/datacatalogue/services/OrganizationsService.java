package it.sinergis.datacatalogue.services;

public class OrganizationsService {

	public static String RESPONSE_JSON_STATUS_DONE =  "{Status:'Done',Description:'Operation ok'}";
	public static String RESPONSE_JSON_LIST_ORG = "{organizations:[{name:'Org1',description:'First organization',users:[{username:'user1'},{username:'user2'}]},{name:'Org2',description:'Second organization',users:[{username:'user3'},{username:'user4'}]}]";
	
	public String createOrganization(String req){
		return RESPONSE_JSON_STATUS_DONE;
	}
	
	public String updateOrganization(String req){
		return RESPONSE_JSON_STATUS_DONE;
	}
	
	public String deleteOrganization(String req){
		return RESPONSE_JSON_STATUS_DONE;
	}
	
	public String listOrganization(String req){
		return RESPONSE_JSON_LIST_ORG;
	}
}
