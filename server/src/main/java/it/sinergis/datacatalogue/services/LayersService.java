package it.sinergis.datacatalogue.services;

public class LayersService {

	public static String RESPONSE_JSON_STATUS_DONE =  "{Status:'Done'}";
	public static String RESPONSE_JSON_LIST_LAYER = "  {layers:[{name:'Streets',datasetname:'Dataset1',description:'Main streets',metadata:'',sld:'streets.sld'},{name:'Trees',datasetname:'Dataset2',description:'trees',metadata:'',sld:'trees.sld'}]}";
	
	
	public String createLayer(String req){
		return RESPONSE_JSON_STATUS_DONE;
	}
	
	public String updateLayer(String req){
		return RESPONSE_JSON_STATUS_DONE;
	}
	
	public String deleteLayer(String req){
		return RESPONSE_JSON_STATUS_DONE;
	}
	
	public String listLayer(String req){
		return RESPONSE_JSON_LIST_LAYER;
	}
}
