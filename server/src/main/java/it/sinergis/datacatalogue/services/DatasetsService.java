package it.sinergis.datacatalogue.services;

public class DatasetsService {

	public static String RESPONSE_JSON_STATUS_DONE =  "{Status:'Done',Description:'Operation ok'}";
	public static String RESPONSE_JSON_LIST_DATASET = " {dataset:[{name:'Dataset1',realname:'dataset1',datasourcename:'Datasource1',description:'First dataset',tobeingested:'true',refreshinterval:'3'},"
			+ "{name:'Dataset2',realname:'Dataset2',datasourcename:'Datasource2',description:'Second dataset'}]}";
	public static String RESPONSE_JSON_LIST_COLS_METADATA = " {columns:[{name:'Col1',type:'number',alias:'Col 1',visibility:'true'},{name:'Col2',type:'char',alias:'Col 2',visibility:'false'}]}";
	
	public String createDataset(String req){
		return RESPONSE_JSON_STATUS_DONE;
	}
	
	public String updateDataset(String req){
		return RESPONSE_JSON_STATUS_DONE;
	}
	
	public String deleteDataset(String req){
		return RESPONSE_JSON_STATUS_DONE;
	}
	
	public String listDataset(String req){
		return RESPONSE_JSON_LIST_DATASET;
	}
	
	public String listColumns(String req){
		return RESPONSE_JSON_LIST_COLS_METADATA;
	}
	
	public String updateColumnsMetadata(String req){
		return RESPONSE_JSON_STATUS_DONE;
	}
	
	public String createCronService(String req){
		return RESPONSE_JSON_STATUS_DONE;
	}
}
