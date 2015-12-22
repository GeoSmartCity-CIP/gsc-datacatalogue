package it.sinergis.datacatalogue.services;

public class DatasourcesService {

	public static String RESPONSE_JSON_STATUS_DONE =  "{Status:'Done',Description:'Operation ok'}";
	public static String RESPONSE_JSON_LIST_DATASOURCE = " {datasource:[{name:'Datasource1',type:'SHAPE',description:'directory of shape files',updated:'12/10/2015',path:'c:\temp'},{name:'Datasource2',type:'ORACLE',description:'SDO layers',updated:'/09/03/2015',url:'jdbc:postgresql://localhost:5432/postgres',username:'admin'}]}";
	public static String RESPONSE_JSON_LIST_DATAORIGIN = "{dataorigin:[{name:'Dataorigin1',path:'C:\temp'},{name:'Dataorigin2',path:'/opt/temp'}]}";
	
	public String createDatasource(String req){
		return RESPONSE_JSON_STATUS_DONE;
	}
	
	public String updateDatasource(String req){
		return RESPONSE_JSON_STATUS_DONE;
	}
	
	public String deleteDatasource(String req){
		return RESPONSE_JSON_STATUS_DONE;
	}
	
	public String listDatasource(String req){
		return RESPONSE_JSON_LIST_DATASOURCE;
	}
	
	public String uploadDatasource(String req){
		return RESPONSE_JSON_STATUS_DONE;
	}
	
	public String ckanDatasource(String req){
		return RESPONSE_JSON_STATUS_DONE;
	}
	
	public String listDataOrigin(String req){
		return RESPONSE_JSON_LIST_DATAORIGIN;
	}
}
