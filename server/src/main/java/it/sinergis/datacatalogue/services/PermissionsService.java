package it.sinergis.datacatalogue.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import it.sinergis.datacatalogue.bean.jpa.Gsc003RoleEntity;
import it.sinergis.datacatalogue.bean.jpa.Gsc004FunctionEntity;
import it.sinergis.datacatalogue.bean.jpa.Gsc005PermissionEntity;
import it.sinergis.datacatalogue.bean.jpa.Gsc008LayerEntity;
import it.sinergis.datacatalogue.common.Constants;
import it.sinergis.datacatalogue.exception.DCException;
import it.sinergis.datacatalogue.persistence.PersistenceServiceProvider;
import it.sinergis.datacatalogue.persistence.services.Gsc003RolePersistence;
import it.sinergis.datacatalogue.persistence.services.Gsc004FunctionPersistence;
import it.sinergis.datacatalogue.persistence.services.Gsc005PermissionPersistence;
import it.sinergis.datacatalogue.persistence.services.Gsc008LayerPersistence;
import it.sinergis.datacatalogue.persistence.services.Gsc010ApplicationPersistence;

public class PermissionsService extends ServiceCommons{

	/** Logger. */
	private static Logger logger;
	
	/** Gsc003Role  DAO. */
	private Gsc003RolePersistence gsc003dao;
	
	/** Gsc004Function DAO */
	private Gsc004FunctionPersistence gsc004dao;
	
	/** Gsc005Permission  DAO. */
	private Gsc005PermissionPersistence gsc005dao;
	
	/** Gsc008Datasource DAO. */
	private Gsc008LayerPersistence gsc008dao;
	
	/** Gsc010Application DAO. */
	private Gsc010ApplicationPersistence gsc010dao;
	
	/**
	 * Constructor
	 */	
	public PermissionsService() {
		logger = Logger.getLogger(this.getClass());		
		gsc003dao = PersistenceServiceProvider.getService(Gsc003RolePersistence.class);		
		gsc004dao = PersistenceServiceProvider.getService(Gsc004FunctionPersistence.class);
		gsc005dao = PersistenceServiceProvider.getService(Gsc005PermissionPersistence.class);		
		gsc008dao = PersistenceServiceProvider.getService(Gsc008LayerPersistence.class);
		gsc010dao = PersistenceServiceProvider.getService(Gsc010ApplicationPersistence.class);

	}
	
	/**
	 * Retrieves the role given its Id.
	 * 
	 * @param json
	 * @return
	 * @throws RPException
	 */
	private Gsc003RoleEntity getRoleObjectById(Long id) throws DCException {
		return (Gsc003RoleEntity) gsc003dao.load(id);
	}
	
	/**
	 * Creates a query which checks that the functions id correspond to existing records.
	 * If fullrecord parameter is true, return a query that will retrieve the list of all corresponding records.
	 * 
	 * @param ids
	 * @param organizationId
	 * @return
	 */
	private String createCountCheckQuery(List<Long> ids,String tableName,boolean fullRecords) {
		StringBuilder sb = new StringBuilder();
		
		//This condition counts the existing users count matches the count on the users that were passed as arguments
		if(fullRecords) {
			sb.append("select * from ");
		} else {
			sb.append("select count(*) from ");
		}
		sb.append(tableName);
		sb.append(" where ");
		sb.append(Constants.ID);
		sb.append(" IN (");
		
		for(int i = 0; i < ids.size(); i++) {
			if(i != 0) {
				sb.append(",");
			}
			sb.append(ids.get(i));
		}
		sb.append(")");
		
		return sb.toString();
	}
	
	private Gsc004FunctionEntity findFunctionWithId(List<Gsc004FunctionEntity> functions, ObjectNode functionNode) {
		Long id = functionNode.get(Constants.FUNC_ID_FIELD).asLong();
		for(Gsc004FunctionEntity function : functions) {
			if(function.getId().longValue() == id.longValue()) {
				return function;
			}
		}
		return null;
	}
	
	private Gsc008LayerEntity findLayerWithId(List<Gsc008LayerEntity> layers, ObjectNode layerNode) {
		Long id = layerNode.get(Constants.LAYER_ID_FIELD).asLong();
		for(Gsc008LayerEntity layer : layers) {
			if(layer.getId().longValue() == id.longValue()) {
				return layer;
			}
		}
		return null;
	}
	
	private void addFunctionNameAndDescription(ObjectNode functionNode,Gsc004FunctionEntity functionEntity) throws DCException {
		String funcName = getFieldValueFromJsonText(functionEntity.getJson(),Constants.FUNC_NAME_FIELD);
		functionNode.put(Constants.FUNC_NAME_FIELD,funcName);
		
		String funcDesc = getFieldValueFromJsonText(functionEntity.getJson(),Constants.DESCRIPTION_FIELD);
		functionNode.put(Constants.DESCRIPTION_FIELD,funcDesc);
	}
	
	private void addLayerName(ObjectNode functionNode,Gsc008LayerEntity layerEntity) throws DCException {
		String layerName = getFieldValueFromJsonText(layerEntity.getJson(),Constants.LAYER_NAME_FIELD);
		functionNode.put(Constants.LAYER_NAME_FIELD,layerName);
//		
//		String layerDesc = getFieldValueFromJsonText(layerEntity.getJson(),Constants.DESCRIPTION_FIELD);
//		functionNode.put(Constants.DESCRIPTION_FIELD,layerDesc);
	}
	
	private void buildResponseNode(ArrayNode functionList,List<Gsc004FunctionEntity> functions,List<Gsc008LayerEntity> layers) throws DCException {
		
		for(int i=0; i< functionList.size(); i++) {
			ObjectNode functionNode = (ObjectNode) functionList.get(i);
			
			addFunctionNameAndDescription(functionNode,findFunctionWithId(functions,functionNode));

			JsonNode idLayer = functionNode.get(Constants.LAYER_ID_FIELD);
			if(idLayer != null) {
				addLayerName(functionNode,findLayerWithId(layers,functionNode));
			}
		}
		
	}
	
	/**
	 * Creates or updates a new permission record.
	 * 
	 * @param req
	 * @return
	 */
	public String assignPermission(String req){
		try {
			// preliminary checks on the request parameters
			preliminaryChecks(req, Constants.ASSIGN_PERMISSION);
			
			// Find the role thorugh its id in the request
			// check if there is a role with that id
			Gsc003RoleEntity role = getRoleObjectById(Long.parseLong(getFieldValueFromJsonText(req, Constants.ROLE_ID_FIELD)));
			if(role == null) {
				logger.error("Incorrect parameters: requested role does not exist.");
				throw new DCException(Constants.ER501, req);
			}

			//check if the functions/layers/application with the specified ids exist
			
			//function id list retrieved from the request
			List<Long> functionIdList = new ArrayList<Long>();
			//layer id list retrieved from the request
			List<Long> layerIdList = new ArrayList<Long>();
			//application id list retrieved from the request
			List<Long> applicationIdList = new ArrayList<Long>();
			//get the json object for the request
			ObjectNode requestJson = (ObjectNode) om.readTree(req);
			
			ArrayNode functionList = JsonNodeFactory.instance.arrayNode();
			
			//fill the id arrays
			functionList = (ArrayNode) requestJson.path(Constants.FUNCTIONS_FIELD);
			for(int i=0;i<functionList.size();i++) {
				ObjectNode functionNode = (ObjectNode) functionList.get(i);
				
				JsonNode functionId = functionNode.get(Constants.FUNC_ID_FIELD);
				if(functionId != null) {
					functionIdList.add(functionId.asLong());
				} else {
					//each array element should have a function id. 
					//preliminary checks should avoid to be in this block
					//but we throw exception anyway
					logger.error("Incorrect parameters: functionid is mandatory in each element of the function array.");
					throw new DCException(Constants.ER502, req);
				}
				
				JsonNode layerId = functionNode.get(Constants.LAYER_ID_FIELD);
				//layer id element is optional. do nothing if not found
				if(layerId != null) {
					layerIdList.add(layerId.asLong());
				}
				
				JsonNode applicationId = functionNode.get(Constants.APPLICATION_ID);
				//application id element is optional. do nothing if not found
				if(applicationId != null) {
					applicationIdList.add(applicationId.asLong());
				}
			}
			
			//check if the function/layers/application specified exist in the corresponding tables
			
			String funcQuery = createCountCheckQuery(functionIdList,Constants.FUNCTION_TABLE_NAME,false);
			Long functionCount = gsc004dao.countInId(funcQuery);
			//if at least one of the specified users does not exist throw error
			//if the countnumber is less than the id list size one or more records was not found
			//Transform to set because one function id may be in more than one element: eg. "functions": [{"idfunction": "1","idapplication": "58"},{ "idfunction": "1","idapplication": "57"}]
			if(functionCount < new HashSet<Long>(functionIdList).size()) {
				logger.error("Incorrect parameters: one of the requested functions does not exist. Check if the correct ids were used.");
				throw new DCException(Constants.ER503, req);
			}

			//XXX check wether to add clause to crosscheck ids layer<->func correspond to the same org
			String layerQuery = createCountCheckQuery(layerIdList,Constants.LAYER_TABLE_NAME,false);
			Long layerCount = 0L;
			if(layerIdList.size() > 0) {
				layerCount = gsc008dao.countInId(layerQuery);
			}
			if(layerCount < new HashSet<Long>(layerIdList).size()) {
				logger.error("Incorrect parameters: one of the requested layers does not exist. Check if the correct ids were used.");
				throw new DCException(Constants.ER504, req);
			}
			
			String applicationQuery = createCountCheckQuery(applicationIdList,Constants.APPLICATION_TABLE_NAME,false);
			Long applicationCount = 0L;
			if(applicationIdList.size() > 0) {
				applicationCount = gsc010dao.countInId(applicationQuery);
			}
			if(applicationCount < new HashSet<Long>(applicationIdList).size()) {
				logger.error("Incorrect parameters: one of the requested application does not exist. Check if the correct ids were used.");
				throw new DCException(Constants.ER505, req);
			}
			
			
			//check if the permission record for that role already exists. If it does ovverride, otherwise create a new one.
			String text = "'" +Constants.ROLE_ID_FIELD +"' = '" + role.getId() +"'";
			String permQuery = createQuery(text,Constants.PERMISSION_TABLE_NAME,Constants.JSON_COLUMN_NAME,"select");
			List<Gsc005PermissionEntity> permissions = gsc005dao.loadByNativeQuery(permQuery);
			
			//save the record
			Gsc005PermissionEntity permission = null;
			if(permissions.size() == 0) {
				permission = new Gsc005PermissionEntity();
			} else if(permissions.size() == 1) {
				permission = permissions.get(0);
			} else {
				//this block should never be active, but throw exception anyway if so.
				logger.error("State error: a role can only have 1 record in the permission table, but it looks like there is more than one. Cannot assign until problem is solved.");
				throw new DCException(Constants.ER01, req);
			}
			permission.setJson(req);
			gsc005dao.save(permission);
			
			logger.info("permission rule succesfully created.");
			logger.info(req);
			
			return createJsonStatus(Constants.STATUS_DONE, Constants.PERMISSION_ASSIGNED, null, req);
		} catch (Exception e) {
			if(e instanceof DCException) {
				DCException dce = new DCException((((DCException) e).getErrorCode()),req);
				return dce.returnErrorString();	
			} else if(e instanceof NumberFormatException) {
				logger.error("inserted id parameter is not a number", e);
				DCException rpe = new DCException(Constants.ER12, req);
				return rpe.returnErrorString();
			} else {
				logger.error("register user service error", e);
				DCException rpe = new DCException(Constants.ER01, req);
				logger.error("register user service: unhandled error " + rpe.returnErrorString());
				return rpe.returnErrorString();
			}
		}
	}
	
	/**
	 * list the functions list for the role provided in the request.
	 * 
	 * @param req
	 * @return
	 */
	public String listPermission(String req){
		try {
			// preliminary checks on the request parameters
			preliminaryChecks(req, Constants.LIST_PERMISSION);
			
			//retrieve the permission record with the requested role id
			String text = "'" + Constants.ROLE_ID_FIELD +"' = '" + getFieldValueFromJsonText(req, Constants.ROLE_ID_FIELD) +"'";
			String query = createQuery(text, Constants.PERMISSION_TABLE_NAME, Constants.JSON_COLUMN_NAME,"select");
			List<Gsc005PermissionEntity> permissions = gsc005dao.loadByNativeQuery(query);
			
			Gsc005PermissionEntity permission = null;
			if(permissions.size() == 0) {
				logger.error("No results found.");
				throw new DCException(Constants.ER506, req);
			} else if(permissions.size() == 1) {
				permission = permissions.get(0);
			} else {
				//this block should never be active, but throw exception anyway if so.
				logger.error("State error: a role can only have 1 record in the permission table, but it looks like there is more than one. Cannot assign until problem is solved.");
				throw new DCException(Constants.ER01, req);
			}
			
			Map<String, Object> appMap = new HashMap<String, Object>();
			// ID Permission
			appMap.put(Constants.PERMISSION_ID, permission.getId().toString());

			// ID Role
			String roleField = getFieldValueFromJsonText(permission.getJson(), Constants.ROLE_ID);
			appMap.put(Constants.ROLE_ID, roleField);			

			//build the response object
			ArrayNode functionList = (ArrayNode) (om.readTree(permission.getJson()).path(Constants.FUNCTIONS_FIELD));
			List<Long> functionIdList = new ArrayList<Long>();
			List<Long> layerIdList = new ArrayList<Long>();
			for(int i=0;i<functionList.size();i++) {
				ObjectNode functionNode = (ObjectNode) functionList.get(i);
				
				JsonNode functionId = functionNode.get(Constants.FUNC_ID_FIELD);
				if(functionId != null) {
					functionIdList.add(functionId.asLong());
				} else {
					//each array element should have a function id. 
					//preliminary checks should avoid to be in this block
					//but we throw exception anyway
					logger.error("Corrupted record found: functionid is mandatory in each element of the function array.");
					throw new DCException(Constants.ER01, req);
				}
				
				JsonNode layerId = functionNode.get(Constants.LAYER_ID_FIELD);
				if(layerId != null) {
					layerIdList.add(layerId.asLong());
				}
			}
			
			String functionQuery = createCountCheckQuery(functionIdList, Constants.FUNCTION_TABLE_NAME, true);
			List<Gsc004FunctionEntity> functions = gsc004dao.getFunction(functionQuery);
			
			String layerQuery = createCountCheckQuery(layerIdList, Constants.LAYER_TABLE_NAME, true);
			List<Gsc008LayerEntity> layers = new ArrayList<Gsc008LayerEntity>();
			if(layerIdList.size() > 0 ) {
				layers = gsc008dao.getLayers(layerQuery);
			}
			
			buildResponseNode(functionList,functions,layers);
			
			appMap.put(Constants.FUNCTIONS, functionList);
			
			String jsonString;
			try {
				ObjectMapper mapper = new ObjectMapper();
				jsonString = mapper.writeValueAsString(appMap);
			} catch (IOException e) {
				logger.error("IOException during the construction of status response", e);
				throw new DCException(Constants.ER01, req);
			}

			return jsonString;
		} catch (Exception e) {
			if(e instanceof DCException) {
				DCException dce = new DCException((((DCException) e).getErrorCode()),req);
				return dce.returnErrorString();	
			} else if(e instanceof NumberFormatException) {
				logger.error("inserted id parameter is not a number", e);
				DCException rpe = new DCException(Constants.ER12, req);
				return rpe.returnErrorString();
			} else {
				logger.error("register user service error", e);
				DCException rpe = new DCException(Constants.ER01, req);
				logger.error("register user service: unhandled error " + rpe.returnErrorString());
				return rpe.returnErrorString();
			}
		}
	}
}
