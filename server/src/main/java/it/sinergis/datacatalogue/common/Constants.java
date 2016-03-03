package it.sinergis.datacatalogue.common;

public final class Constants {

	/** GENERIC PARAMETERS*/
	public static final String STATUS_DONE = "done";
	public static final String STATUS_ERROR = "error";
	public static final String REQUEST = "request";
	public static final String ID = "id";
	
	/** TABLES PARAMETERS*/
	public static final String ORGANIZATION_TABLE_NAME = "gsc_001_organization";
	public static final String USER_TABLE_NAME = "gsc_002_user";
	public static final String ROLE_TABLE_NAME = "gsc_003_role";	
	public static final String DATASOURCE_TABLE_NAME = "gsc_006_datasource";
	public static final String DATASETS_TABLE_NAME = "gsc_007_dataset";
	public static final String FUNCTION_TABLE_NAME = "gsc_004_function";
	public static final String LAYER_TABLE_NAME = "gsc_008_layer";
	public static final String GROUP_LAYER_TABLE_NAME = "gsc_009_grouplayer";


	public static final String JSON_COLUMN_NAME = "json";	

	/** JSON FIELDS NAMES */
	public static final String DETAIL_FIELD = "detail";
	public static final String ORG_ID_FIELD = "idorganization";
	public static final String ORG_NAME_FIELD = "organizationname";
	public static final String DESCRIPTION_FIELD = "description";
	public static final String SCHEMA_FIELD = "schema";
	public static final String USERNAME_FIELD = "username";
	public static final String PASSWORD_FIELD = "password";
	public static final String ORGANIZATION_FIELD = "organization";
	public static final String ROLE_NAME_FIELD = "rolename";
	public static final String ORG_FIELD = "organization";
	public static final String STATUS_FIELD = "status";
	public static final String ORGANIZATIONS_FIELD = "organizations";
	public static final String NAME_FIELD = "name";
	public static final String USERS_FIELD = "users";
	public static final String ID_FIELD = "id";
	public static final String DSET_ID_FIELD = "iddataset";
	public static final String DSET_NAME_FIELD = "datasetname";
	public static final String DSET_REALNAME_FIELD = "realname";
	public static final String DSET_TOBEINGESTED_FIELD = "tobeingested";
	public static final String DSET_REFRESHINTERVAL_FIELD = "refreshinterval";
	public static final String DSET_RESULT = "dataset";
	public static final String NAME = "name";
	public static final String TYPE = "type";
	public static final String ALIAS = "alias";
	public static final String VISIBILITY = "visibility";
	public static final String COLUMNS = "columns";	
	public static final String DATA_ORIGIN = "dataorigin";	
	
	/** Datasource field names */
	public static final String DATASOURCES_NAME_FIELD = "datasources";
	public static final String DATASOURCE_ID_FIELD = "iddatasource";
	public static final String DATASOURCE_NAME_FIELD = "datasourcename";
	public static final String DATASOURCE_FIELD = "datasource";
	
	/** Layer field names. */
	public static final String LAYER_NAME_FIELD = "layername";
	public static final String LAYER_ID_FIELD = "idlayer";
	public static final String LAYERS = "layers";
	
	/** GROUP Layer field names. */
	public static final String GROUP_LAYER_NAME_FIELD = "groupname";
	public static final String GROUP_LAYER_ID_FIELD = "idgroup";
	public static final String GROUP_LAYER = "grouplayers";
	
	/** Function field names */
	public static final String FUNC_NAME_FIELD = "functionname";
	public static final String FUNC_ID_FIELD = "idfunction";
	
	/** ERRORS */
	public static final String ER01 = "ER01";
	public static final String ER02 = "ER02";
	public static final String ER03 = "ER03";	
	public static final String ER04 = "ER04";	
	public static final String ER05 = "ER05";	
	public static final String ER06 = "ER06";	
	public static final String ER07 = "ER07";	
	public static final String ER11 = "ER11";
	public static final String ER12 = "ER12";
	public static final String ER13 = "ER13";
	public static final String ER14 = "ER14";
	public static final String ER15 = "ER15";
	
	/** Dataset Errors. */
	public static final String ER700 = "ER700";
	public static final String ER701 = "ER701";
	public static final String ER702 = "ER702";
	public static final String ER703 = "ER703";
	public static final String ER704 = "ER704";
	public static final String ER705 = "ER705";
	public static final String ER706 = "ER706";
	public static final String ER707 = "ER707";
	public static final String ER708 = "ER708";

	/** Organization Errors. */
	public static final String ER101 = "ER101";	
	public static final String ER102 = "ER102";	
	public static final String ER103 = "ER103";	
	public static final String ER104 = "ER104";
	
	/** Datasource Errors. */
	public static final String ER601 = "ER601";	
	public static final String ER602 = "ER602";	
	public static final String ER603 = "ER603";	
	public static final String ER604 = "ER604";
	public static final String ER605 = "ER605";
	public static final String ER606 = "ER606";
	public static final String ER607 = "ER607";
	public static final String ER608 = "ER608";
	public static final String ER609 = "ER609";
	public static final String ER610 = "ER610";
	public static final String ER611 = "ER611";
	public static final String ER612 = "ER612";
	
	/** Function Errors. */
	public static final String ER401 = "ER401";
	public static final String ER402 = "ER402";
	public static final String ER403 = "ER403";
	public static final String ER404 = "ER404";
	public static final String ER405 = "ER405";
	
	/** Layers errors. */
	public static final String ER801 = "ER801";
	public static final String ER802 = "ER802";
	public static final String ER803 = "ER803";
	public static final String ER804 = "ER804";
	public static final String ER805 = "ER805";
	public static final String ER806 = "ER806";
	public static final String ER807 = "ER807";
	
	/** group layer errors. */
	public static final String ER901 = "ER901";
	public static final String ER902 = "ER902";
	public static final String ER903 = "ER903";
	public static final String ER904 = "ER904";
	public static final String ER905 = "ER905";
	public static final String ER906 = "ER906";
	
	/** STATUS MESSAGES*/
	public static final String ORGANIZATION_CREATED = "ORGANIZATION_CREATED";
	public static final String ORGANIZATION_UPDATED = "ORGANIZATION_UPDATED";
	public static final String ORGANIZATION_DELETED = "ORGANIZATION_DELETED";
	public static final String ROLE_CREATED = "ROLE_CREATED";
	public static final String ROLE_DELETED = "ROLE_DELETED";
	public static final String DATASETS_CREATED = "DATASETS_CREATED";
	public static final String DATASETS_DELETED = "DATASETS_DELETED";
	public static final String DATASETS_UPDATED = "DATASETS_UPDATED";
	public static final String DATASOURCE_CREATED = "DATASOURCE_CREATED";
	public static final String DATASOURCE_UPDATED = "DATASOURCE_UPDATED";
	public static final String DATASOURCE_DELETED = "DATASOURCE_DELETED";
	public static final String FUNCTION_CREATED = "FUNCTION_CREATED";
	public static final String FUNCTION_UPDATED = "FUNCTION_UPDATED";
	public static final String FUNCTION_DELETED = "FUNCTION_DELETED";
	public static final String LAYER_CREATED = "LAYER_CREATED";
	public static final String LAYER_UPDATED = "LAYER_UPDATED";
	public static final String LAYER_DELETED = "LAYER_DELETED";
	public static final String GROUP_LAYER_CREATED = "GROUP_LAYER_CREATED";
	public static final String GROUP_LAYER_ASSIGNED = "GROUP_LAYER_ASSIGNED";
	public static final String GROUP_LAYER_DELETED = "GROUP_LAYER_DELETED";
	
		
	/** Services names. */
	public static final String CREATE_ORGANIZATION = "CREATE_ORGANIZATION";
	public static final String UPDATE_ORGANIZATION = "UPDATE_ORGANIZATION";
	public static final String LIST_ORGANIZATION = "LIST_ORGANIZATION";
	public static final String DELETE_ORGANIZATION = "DELETE_ORGANIZATION";
	public static final String CREATE_DATASOURCE = "CREATE_DATASOURCE";
	public static final String UPDATE_DATASOURCE = "UPDATE_DATASOURCE";
	public static final String LIST_DATASOURCE = "LIST_DATASOURCE";
	public static final String DELETE_DATASOURCE = "DELETE_DATASOURCE";
	public static final String DATA_ORIGIN_LIST = "DATA_ORIGIN_LIST";
	public static final String CREATE_DATASET = "CREATE_DATASET";
	public static final String UPDATE_DATASET = "UPDATE_DATASET";
	public static final String LIST_DATASET = "LIST_DATASET";
	public static final String DELETE_DATASET = "DELETE_DATASET";
	public static final String LIST_DATASET_COLUMNS = "LIST_DATASET_COLUMNS";
	public static final String UPDATE_DATASET_COLUMNS = "UPDATE_DATASET_COLUMNS";
	public static final String CREATE_FUNCTION = "CREATE_FUNCTION";
	public static final String UPDATE_FUNCTION = "UPDATE_FUNCTION";
	public static final String DELETE_FUNCTION = "DELETE_FUNCTION";
	public static final String LIST_FUNCTION = "LIST_FUNCTION";
	public static final String CREATE_LAYER = "CREATE_LAYER";
	public static final String UPDATE_LAYER = "UPDATE_LAYER";
	public static final String DELETE_LAYER = "DELETE_LAYER";
	public static final String LIST_LAYER = "LIST_LAYER";
	public static final String CREATE_GROUP_LAYER = "CREATE_GROUP_LAYER";
	public static final String ASSIGN_LAYER_TO_GROUP = "ASSIGN_LAYER_TO_GROUP";
	public static final String DELETE_GROUP_LAYER = "DELETE_GROUP_LAYER";
	public static final String LIST_GROUP_LAYER = "LIST_GROUP_LAYER";
	
	
	/** Values. */
	public static final String TRUE = "true";
	public static final String FALSE = "false";
	public static final String SHAPE = "shape";
	public static final String POSTGIS = "postgis";
	public static final String POSTGRES = "postgres";
	public static final String PATH = "path";
	public static final String URL = "url";
	public static final String PORT = "port";
	public static final String DBTYPE = "dbtype";
	public static final String DATABASE = "database";
	public static final String USER = "user";
	public static final String PASSWD = "passwd";
	public static final String HOST = "host";
	public static final String SHP_EXT = ".shp";
}
