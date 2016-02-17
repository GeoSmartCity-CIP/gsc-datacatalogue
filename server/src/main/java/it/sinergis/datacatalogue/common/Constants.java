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

	public static final String JSON_COLUMN_NAME = "json";	

	/** JSON FIELDS NAMES */
	public static final String DETAIL_FIELD = "detail";
	public static final String ORG_ID_FIELD = "idorganization";
	public static final String ORG_NAME_FIELD = "organizationname";
	public static final String DESCRIPTION_FIELD = "description";
	public static final String USERNAME_FIELD = "username";
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
	
	/** Datasource field names */
	public static final String DATASOURCES_NAME_FIELD = "datasources";
	public static final String DATASOURCE_ID_FIELD = "iddatasource";
	public static final String DATASOURCE_NAME_FIELD = "datasourcename";
	public static final String DATASOURCE_FIELD = "datasource";
	
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
	
	/** Dataset Errors. */
	public static final String ER700 = "ER700";
	public static final String ER701 = "ER701";
	public static final String ER702 = "ER702";
	public static final String ER703 = "ER703";
	public static final String ER704 = "ER704";
	public static final String ER705 = "ER705";
	public static final String ER706 = "ER706";
	public static final String ER707 = "ER707";

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
	
		
	/** Services names. */
	public static final String CREATE_DATASET = "CREATE_DATASET";
	public static final String UPDATE_DATASET = "UPDATE_DATASET";
	public static final String LIST_DATASET = "LIST_DATASET";
	public static final String DELETE_DATASET = "DELETE_DATASET";
	
	/** Values. */
	public static final String TRUE = "true";
	public static final String FALSE = "false";
	public static final String SHAPE = "shape";
	public static final String PATH = "path";
}
