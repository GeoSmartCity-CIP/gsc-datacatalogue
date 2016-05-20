package it.sinergis.datacatalogue.common;

public final class Constants {

	/** GENERIC PARAMETERS */
	public static final String STATUS_DONE = "done";
	public static final String STATUS_ERROR = "error";
	public static final String REQUEST = "request";
	public static final String ID = "id";
	public static final String JSON_COLUMN_NAME = "json";
	
	/** CONFIGURATION PARAMETERS */
	public static final String UPLOAD_PATH = "uploadpath";
	

	/** TABLE NAME PARAMETERS */
	public static final String ORGANIZATION_TABLE_NAME = "gsc_001_organization";
	public static final String USER_TABLE_NAME = "gsc_002_user";
	public static final String ROLE_TABLE_NAME = "gsc_003_role";
	public static final String DATASOURCE_TABLE_NAME = "gsc_006_datasource";
	public static final String DATASETS_TABLE_NAME = "gsc_007_dataset";
	public static final String FUNCTION_TABLE_NAME = "gsc_004_function";
	public static final String LAYER_TABLE_NAME = "gsc_008_layer";
	public static final String GROUP_LAYER_TABLE_NAME = "gsc_009_grouplayer";
	public static final String APPLICATION_TABLE_NAME = "gsc_010_application";
	public static final String PERMISSION_TABLE_NAME = "gsc_005_permission";

	/** JSON FIELDS NAMES */
	public static final String DETAIL_FIELD = "detail";
	public static final String DESCRIPTION_FIELD = "description";
	public static final String SCHEMA_FIELD = "schema";
	public static final String STATUS_FIELD = "status";
	public static final String NAME_FIELD = "name";
	public static final String ID_FIELD = "id";
	public static final String SRS = "srs";
	public static final String INCLUDE_ADMIN_RECORDS = "includeadminrecords";

	/** Organization field names */
	public static final String ORG_NAME_FIELD = "organizationname";
	public static final String ORG_ID_FIELD = "idorganization";
	public static final String ORGANIZATIONS_FIELD = "organizations";
	public static final String ORG_FIELD = "organization";

	/** Dataset field names */
	public static final String DSET_ID_FIELD = "iddataset";
	public static final String DSET_NAME_FIELD = "datasetname";
	public static final String DSET_REALNAME_FIELD = "realname";
	public static final String DSET_TOBEINGESTED_FIELD = "tobeingested";
	public static final String DSET_REFRESHINTERVAL_FIELD = "refreshinterval";
	public static final String DSET_RESULT = "dataset";

	/** Datasource field names */
	public static final String DATASOURCES_NAME_FIELD = "datasources";
	public static final String DATASOURCE_ID_FIELD = "iddatasource";
	public static final String DATASOURCE_NAME_FIELD = "datasourcename";
	public static final String DATASOURCE_FIELD = "datasource";
	public static final String NAME = "name";
	public static final String TYPE = "type";
	public static final String ALIAS = "alias";
	public static final String VISIBILITY = "visibility";
	public static final String COLUMNS = "columns";
	public static final String DATA_ORIGIN = "dataorigin";
	public static final String FILENAME = "filename";
	public static final String REMOTE_RELATIVE_PATH = "remoterelativepath";

	/** Layer field names. */
	public static final String LAYER_NAME_FIELD = "layername";
	public static final String LAYER_ID_FIELD = "idlayer";
	public static final String LAYERS = "layers";

	/** GROUP Layer field names. */
	public static final String GROUP_LAYER_NAME_FIELD = "groupname";
	public static final String GROUP_LAYER_ID_FIELD = "idgroup";
	public static final String GROUP_LAYER = "grouplayers";
	public static final String GROUPS = "groups";

	/** Function field names. */
	public static final String FUNC_NAME_FIELD = "functionname";
	public static final String FUNC_ID_FIELD = "idfunction";
	public static final String FUNCTIONS_FIELD = "functions";

	/** User field names */
	public static final String USER_NAME_FIELD = "username";
	public static final String USER_ID_FIELD = "iduser";
	public static final String USER_EMAIL_FIELD = "email";
	public static final String USERS_FIELD = "users";
	public static final String USERNAME_FIELD = "username";
	public static final String PASSWORD_FIELD = "password";
	public static final String CONFIRM_PASSWORD_FIELD = "confirmpassword";
	public static final String NEW_PASSWORD_FIELD = "newpassword";
	public static final String CONFIRM_NEW_PASSWORD_FIELD = "confirmnewpassword";
	public static final String OLD_PASSWORD_FIELD = "oldpassword";
	public static final String LOCK_FIELD = "lock";
	public static final String TO_BE_VERIFIED = "tobeverified";
	public static final String VERIFIED = "verified";
	public static final String LOCKED = "locked";
	public static final String UUID = "uuid";

	/** Role field names. */
	public static final String ROLE_ID_FIELD = "idrole";
	public static final String ROLES_FIELD = "roles";
	public static final String ROLE_NAME_FIELD = "rolename";

	/** Application field names. */
	public static final String APP_NAME_FIELD = "applicationname";
	public static final String APP_URI = "uri";
	public static final String APPLICATION_ID = "idapplication";
	public static final String APPLICATION_RESULT = "applications";
	public static final String GEOSERVER_PARAMS = "geoserver";

	/** E-mail constants. */
	public static final String SUB_COMPLETE_REGISTRATION = "Datacatalogue Complete Registration Process";
	public static final String SUB_CHANGE_PASSWORD = "Password Change Process";

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
	public static final String ER16 = "ER16";
	public static final String ER17 = "ER17";
	public static final String ER18 = "ER18";
	public static final String ER19 = "ER19";
	public static final String ER20 = "ER20";

	/** Organization Errors. */
	public static final String ER101 = "ER101";
	public static final String ER102 = "ER102";

	/** Users errors. */
	public static final String ER201 = "ER201";
	public static final String ER202 = "ER202";
	public static final String ER203 = "ER203";
	public static final String ER204 = "ER204";
	public static final String ER205 = "ER205";
	public static final String ER206 = "ER206";
	public static final String ER207 = "ER207";
	public static final String ER208 = "ER208";
	public static final String ER209 = "ER209";
	public static final String ER210 = "ER210";
	public static final String ER211 = "ER211";
	public static final String ER212 = "ER212";
	public static final String ER213 = "ER213";
	public static final String ER214 = "ER214";
	public static final String ER215 = "ER215";
	public static final String ER216 = "ER216";

	/** Role errors. */
	public static final String ER301 = "ER301";
	public static final String ER302 = "ER302";
	public static final String ER304 = "ER304";

	/** Function Errors. */
	public static final String ER401 = "ER401";
	public static final String ER402 = "ER402";
	public static final String ER404 = "ER404";
	public static final String ER405 = "ER405";

	/** permission errors codes. */
	public static final String ER501 = "ER501";
	public static final String ER502 = "ER502";
	public static final String ER503 = "ER503";
	public static final String ER504 = "ER504";

	/** Datasource Errors. */
	public static final String ER601 = "ER601";
	public static final String ER602 = "ER602";
	public static final String ER605 = "ER605";
	public static final String ER606 = "ER606";
	public static final String ER607 = "ER607";
	public static final String ER608 = "ER608";
	public static final String ER609 = "ER609";
	public static final String ER610 = "ER610";
	public static final String ER611 = "ER611";
	public static final String ER612 = "ER612";

	/** Dataset Errors. */
	public static final String ER702 = "ER702";
	public static final String ER704 = "ER704";
	public static final String ER705 = "ER705";
	public static final String ER708 = "ER708";

	/** Layers errors. */
	public static final String ER801 = "ER801";
	public static final String ER802 = "ER802";
	public static final String ER803 = "ER803";
	public static final String ER804 = "ER804";
	public static final String ER806 = "ER806";
	public static final String ER807 = "ER807";

	/** group layer errors. */
	public static final String ER901 = "ER901";
	public static final String ER902 = "ER902";
	public static final String ER903 = "ER903";
	public static final String ER904 = "ER904";
	public static final String ER906 = "ER906";

	/** Application errors. */
	public static final String ER1000 = "ER1000";
	public static final String ER1002 = "ER1002";
	public static final String ER1005 = "ER1005";
	public static final String ER1006 = "ER1006";
	public static final String ER1007 = "ER1007";
	public static final String ER1008 = "ER1008";

	/** Publish on geoserver errors. */
	public static final String ER_GEO01 = "ER_GEO01";
	public static final String ER_GEO02 = "ER_GEO02";
	public static final String ER_GEO03 = "ER_GEO03";

	/** STATUS MESSAGES */
	public static final String ORGANIZATION_CREATED = "ORGANIZATION_CREATED";
	public static final String ORGANIZATION_UPDATED = "ORGANIZATION_UPDATED";
	public static final String ORGANIZATION_DELETED = "ORGANIZATION_DELETED";
	public static final String ROLE_CREATED = "ROLE_CREATED";
	public static final String ROLE_DELETED = "ROLE_DELETED";
	public static final String USERS_ASSIGNED_TO_ROLE = "USERS_ASSIGNED_TO_ROLE";
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
	public static final String APPLICATION_CREATED = "APPLICATION_CREATED";
	public static final String APPLICATION_LAYER_GROUP_ASSIGNED = "APPLICATION_LAYER_GROUP_ASSIGNED";
	public static final String APPLICATION_DELETED = "APPLICATION_DELETED";
	public static final String LOGIN_SUCCESFUL = "LOGIN_SUCCESFUL";
	public static final String USER_REGISTERED = "USER_REGISTERED";
	public static final String PASSWORD_SUCCESFULLY_CHANGED = "PASSWORD_SUCCESFULLY_CHANGED";
	public static final String RETRIEVE_PASSWORD_MAIL_SENT = "RETRIEVE_PASSWORD_MAIL_SENT";
	public static final String USER_PROFILE_UPDATED = "USER_PROFILE_UPDATED";
	public static final String USER_LOCKED = "USER_LOCKED";
	public static final String USER_UNLOCKED = "USER_UNLOCKED";
	public static final String USER_UNREGISTERED = "USER_UNREGISTERED";
	public static final String USER_DELETED = "USER_DELETED";
	public static final String USER_EMAIL_VERIFIED = "USER_EMAIL_VERIFIED";
	public static final String PERMISSION_ASSIGNED = "PERMISSION_ASSIGNED";
	public static final String FILE_UPLOADED = "FILE_UPLOADED";

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
	public static final String CREATE_APP = "CREATE_APP";
	public static final String LIST_APP = "LIST_APP";
	public static final String ASSIGN_TO_APP = "ASSIGN_TO_APP";
	public static final String DELETE_APP = "DELETE_APP";
	public static final String LOGIN = "LOGIN";
	public static final String REGISTER_USER = "REGISTER_USER";
	public static final String REMIND_PASSWORD = "REMIND_PASSWORD";
	public static final String CHANGE_PASSWORD = "CHANGE_PASSWORD";
	public static final String UPDATE_USER = "UPDATE_USER";
	public static final String LOCK_USER = "LOCK_USER";
	public static final String UNREGISTER_USER = "UNREGISTER_USER";
	public static final String LIST_USERS = "LIST_USERS";
	public static final String VERIFY_MAIL = "VERIFY_MAIL";
	public static final String CREATE_ROLE = "CREATE_ROLE";
	public static final String DELETE_ROLE = "DELETE_ROLE";
	public static final String ASSIGN_USERS_TO_ROLE = "ASSIGN_USERS_TO_ROLE";
	public static final String ASSIGN_PERMISSION = "ASSIGN_PERMISSION";
	public static final String LIST_PERMISSION = "LIST_PERMISSION";
	public static final String GET_CONFIGURATION = "GET_CONFIGURATION";
	public static final String PUBLISH_ON_GEOSERVER = "PUBLISH_ON_GEOSERVER";
	public static final String FILE_UPLOAD = "FILE_UPLOAD";
	

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
	public static final String SHA1 = "SHA-1";

	/** MAIL PROPERTY FIELDS. */
	public static final String HOST_NAME = "HOST_NAME";
	public static final String PORT_NUMBER = "PORT_NUMBER";
	public static final String MAIL_SMTP_HOST = "mail.smtp.host";
	public static final String MAIL_SMTP_PORT = "mail.smtp.port";
	public static final String SENDER_MAIL_ADDRESS = "SENDER_MAIL_ADDRESS";
	public static final String SENDER_MAIL_PASSWORD = "SENDER_MAIL_PASSWORD";
	public static final String SMTP_AUTH = "mail.smtp.auth";

	/** LOGIN parameters. */
	public static final String ATTEMPTED_LOGINS = "attemptedlogins";
	public static final String LOCK_TIME = "locktime";

	/** Get_configuration constants. */
	public static final String CLASS_NAME = "CLASS_NAME";
	public static final String LON = "lon";
	public static final String LAT = "lat";
	public static final String LEFT = "left";
	public static final String RIGHT = "right";
	public static final String TOP = "top";
	public static final String BOTTOM = "bottom";
	public static final String MAX_EXTENT = "maxExtent";
	public static final String TILE_ORIGIN = "tileOrigin";
	public static final String TILES_ORIGIN = "tilesOrigin";
	public static final String TILED = "tiled";
	public static final String STYLES = "STYLES";
	public static final String FORMAT = "FORMAT";
	public static final String MIN_RESOLUTION = "minResolution";
	public static final String MAX_RESOLUTION = "maxResolution";
	public static final String SINGLE_TILE = "singleTile";
	public static final String IS_BASE_LAYER = "isBaseLayer";
	public static final String NUM_ZOOM_LEVELS = "numZoomLevels";
	public static final String OPTIONS = "options";
	public static final String DISPLAY_IN_LAYER_SWITCHER = "displayInLayerSwitcher";
	public static final String NAMESPACE_URI = "namespaceUri";
	public static final String CONFIGS = "configs";
	public static final String PROPERTIES = "properties";
	public static final String XMLNS = "xmlns";
	public static final String CENTER = "center";
	public static final String DEFAULT_MAP = "defaultMap";
	public static final String ZOOM = "zoom";
	public static final String UNITS = "units";
	public static final String NAMESPACE_PREFIX = "namespacePrefix";
	public static final String PROJ = "proj";
	public static final String PROJ4CODE = "projCode";
	public static final String PROJ4DEF = "proj4Def";
	public static final String PROJECTION = "projection";
	public static final String SERVICE_VERSON = "serviceVersion";
	public static final String BUFFER = "buffer";
	public static final String PARAM = "param";
	public static final String PARAMS = "params";
	public static final String VALUE = "value";
	public static final String SECTION = "section";
	public static final String WMS_LAYERS = "wmsLayers";
	public static final String OVERVIEW = "overview";
	public static final String QUERYABLE = "queryable";
	public static final String PHYSICAL_NAME = "physicalName";
	public static final String MAX_SCALE = "maxScale";
	public static final String EXTRACTABLE = "extractable";
	public static final String EXPLORABLE = "explorable";
	public static final String LOGICAL_NAME = "logicalName";
	public static final String MIN_SCALE = "minScale";
	public static final String PRIORITA_VIS = "prioritaVis";
	public static final String GROUP = "group";
	public static final String FIELDS = "fields";
	public static final String MAPS = "maps";
}
