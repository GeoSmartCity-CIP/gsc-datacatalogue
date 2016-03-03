package it.sinergis.datacatalogue.services;

import java.util.List;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import it.sinergis.datacatalogue.bean.jpa.Gsc004FunctionEntity;
import it.sinergis.datacatalogue.common.Constants;
import it.sinergis.datacatalogue.exception.DCException;
import it.sinergis.datacatalogue.persistence.PersistenceServiceProvider;
import it.sinergis.datacatalogue.persistence.services.Gsc004FunctionPersistence;

public class FunctionsService extends ServiceCommons {

	public static String RESPONSE_JSON_STATUS_DONE =  "{Status:'Done',Description:'Operation ok'}";
	public static String RESPONSE_JSON_LIST_FUNC = " {functions:[{name:'function1',description:'First function'},{name:'function2',description:'second function'},{name:'function3',description:'Third function'}]}";

	/** Logger. */
	private static Logger logger;
	
	/** Dao functions. */
	private Gsc004FunctionPersistence gsc004dao;
	
	/** Constructor. */
	public FunctionsService(){
		logger = Logger.getLogger(this.getClass());
		gsc004dao = PersistenceServiceProvider.getService(Gsc004FunctionPersistence.class);
	}
	
	/**
	 * Create a new Function into the gsc_004_function table.
	 * @param req
	 * 			New Function data (JSON Format).
	 * @return
	 * 			Final status of the operation.
	 */
	public String createFunction(String req){
		try{			
			preliminaryChecks(req,Constants.CREATE_FUNCTION);
			logger.info(req);
			
			// check if the specified organization id exists in the organization table. If not throws exception
			checkIdOrganizationValid(req);
			
			// check if there' s another Function already saved with the same specs.
			Gsc004FunctionEntity datasetEntity = getFunctionObjectByUniqueKey(req);
			
			// if no results found -> add new record
			if (datasetEntity == null) {
				
				Gsc004FunctionEntity function = new Gsc004FunctionEntity();
				
				function.setJson(req);				
				function = gsc004dao.save(function);
				
				logger.info("Function succesfully created");
				logger.info(req);
				return createJsonStatus(Constants.STATUS_DONE, Constants.FUNCTION_CREATED, function.getId(), req);							
			}else{
				// function with the same name for the specified organization already exists
				throw new DCException(Constants.ER401, req);
			}
								
		}catch(DCException rpe){
			return rpe.returnErrorString();			
		}catch(Exception e){
			logger.error("createFunction service error", e);
			DCException rpe = new DCException(Constants.ER01, req);
			logger.error("createFunction service: unhandled error " + rpe.returnErrorString());

			return rpe.returnErrorString();	
		}

	}
	 
	/**
	 * Update the Function identified by the given functionid into the gsc_004_table.
	 * @param req
	 * 			Function identifier and new Function data (JSON Format).
	 * @return
	 * 			Final Status of the operation.
	 */
	public String updateFunction(String req){
		try{			
			preliminaryChecks(req,Constants.UPDATE_FUNCTION);
			logger.info(req);
			
			// check if the specified organization id exists in the organization table. If not throws exception
			checkIdOrganizationValid(req);
			
			Long functionId = Long.parseLong(getFieldValueFromJsonText(req, Constants.FUNC_ID_FIELD));			
			
			// extract the function with the specified id from the function table 
			Gsc004FunctionEntity functionDb = getFunctionObjetctById(functionId);
			
			// check if a function with the specified id exists in the function table
			if (functionDb != null){

				// check if a function with the specified name already exists for the given organization.
				Gsc004FunctionEntity checkFunction = getFunctionObjectByUniqueKey(req);
				
				if (checkFunction == null || checkFunction.getId().equals(functionId)){										
						
					String updatedJson = removeJsonField(req, Constants.FUNC_ID_FIELD);
						
					functionDb.setJson(updatedJson);
					functionDb = gsc004dao.save(functionDb);
						
					logger.info("Function successfully updated");
					logger.info(req);
					return createJsonStatus(Constants.STATUS_DONE, Constants.FUNCTION_UPDATED, null, req);
				
				}else{					
					throw new DCException(Constants.ER401, req);
				}				
				
			}else{
				throw new DCException(Constants.ER402, req);
			}
			
		}catch(DCException rpe){
			return rpe.returnErrorString();
		}catch (Exception e){
			logger.error("updateFunction service error", e);
			DCException rpe = new DCException(Constants.ER01, req);
			logger.error("updateFunction service: unhandled error" + rpe.returnErrorString());
			
			return rpe.returnErrorString();
		}
	}
	
	/**
	 * Delete from the gsc_004_function table the specified Function.
	 * @param req
	 * 			Function identifier (JSON Format).
	 * @return
	 * 			Final Status of the operation.
	 */
	public String deleteFunction(String req){
		try{
			preliminaryChecks(req, Constants.DELETE_FUNCTION);
			logger.info(req);
			
			Long functionId = Long.parseLong(getFieldValueFromJsonText(req, Constants.FUNC_ID_FIELD));
			
			Gsc004FunctionEntity function = getFunctionObjetctById(functionId);
			
			if (function != null){
				
				boolean deleteResult = gsc004dao.delete(function);
				
				if (!deleteResult){
					throw new DCException(Constants.ER01, req);
				}
				
				logger.info("function successfully deleted");
				return createJsonStatus(Constants.STATUS_DONE, Constants.FUNCTION_DELETED, null, req);
				
			}else{
				throw new DCException(Constants.ER403, req);
			}
			
		}catch(DCException rpe){
			return rpe.returnErrorString();
		}catch (Exception e){
			logger.error("deleteFunction service error", e);
			DCException rpe = new DCException(Constants.ER01, req);
			logger.error("deleteFunction service: unhandeled error"+ rpe.returnErrorString());
			
			return rpe.returnErrorString();
		}
	}
	
	/**
	 * According to the input request, lists all the Functions or search the Function identified by the name in the request, for the specified organization.  
	 * @param req
	 * 			Organization id and optionally Function name (JSON Format).
	 * @return
	 * 			List of the functions according to the input request, or status message in case of error.
	 */
	public String listFunction(String req){
		try{
			preliminaryChecks(req, Constants.LIST_FUNCTION);
			logger.info(req);
			
			// check if the specified organization id exists in the organization table. If not throws exception
			checkIdOrganizationValid(req);
			
			String organization = getFieldValueFromJsonText(req, Constants.ORG_FIELD);
			String functionName = getFieldValueFromJsonText(req, Constants.FUNC_NAME_FIELD);
			
			StringBuilder builderQuery = new StringBuilder();
			builderQuery.append("'");
			builderQuery.append(Constants.ORG_FIELD);
			builderQuery.append("' = '");
			builderQuery.append(organization);
			builderQuery.append("'");
			if (functionName!= null){
				builderQuery.append(" AND ");
				builderQuery.append("'");
				builderQuery.append(Constants.FUNC_NAME_FIELD);
				builderQuery.append("' = '");
				builderQuery.append(functionName);
				builderQuery.append("'");
			}
			
			String query = createQuery(builderQuery.toString(), Constants.FUNCTION_TABLE_NAME, Constants.JSON_COLUMN_NAME, "select");
			
			logger.debug("Executing query: " + query);
			List<Gsc004FunctionEntity> entityList = gsc004dao.getFunction(query);
			
			if (entityList.size() != 0) {
				ObjectMapper mapper = new ObjectMapper();
				ObjectNode root = JsonNodeFactory.instance.objectNode();
				ArrayNode functions = JsonNodeFactory.instance.arrayNode();
				
				for (Gsc004FunctionEntity function : entityList){				
					ObjectNode funcBase = (ObjectNode) mapper.readTree(function.getJson());
					String funcBaseId = Long.toString(function.getId());
					String funcBaseName = funcBase.findValue(Constants.FUNC_NAME_FIELD).asText();
					String funcBaseDesc = funcBase.findValue(Constants.DESCRIPTION_FIELD).asText();
					
					ObjectNode newObj = JsonNodeFactory.instance.objectNode();
					newObj.put(Constants.FUNC_ID_FIELD, funcBaseId);
					newObj.put(Constants.FUNC_NAME_FIELD, funcBaseName);
					newObj.put(Constants.DESCRIPTION_FIELD, funcBaseDesc);
					
					functions.add(newObj);
				}
				
				root.put("functions", functions);
				
				logger.info("List of extracted functions: ");
				logger.info(root.toString());
				return root.toString();
			}else{
				throw new DCException(Constants.ER404, req);
			}
			
		}catch(DCException rpe){
			return rpe.returnErrorString();
		}catch (Exception e){
			logger.error("listFunction service error", e);
			DCException rpe = new DCException(Constants.ER01, req);
			logger.error("listFunction service: unhandeled error"+ rpe.returnErrorString());
			
			return rpe.returnErrorString();
		}
	}
	
	/**
	 * Retrieves the Function identified by the name and the organization specified in the given JSON Request.
	 * 
	 * @param json
	 * @return
	 * @throws RPException
	 */
	private Gsc004FunctionEntity getFunctionObjectByUniqueKey(String json) throws DCException {

		String functionName = getFieldValueFromJsonText(json, Constants.FUNC_NAME_FIELD);
		String organizationId = getFieldValueFromJsonText(json, Constants.ORGANIZATION_FIELD);		

		try {
			StringBuilder builderQuery = new StringBuilder();
			builderQuery.append("'");
			builderQuery.append(Constants.FUNC_NAME_FIELD);
			builderQuery.append("' = '");
			builderQuery.append(functionName);
			builderQuery.append("'");		
			builderQuery.append(" AND ");
			builderQuery.append("'");
			builderQuery.append(Constants.ORGANIZATION_FIELD);
			builderQuery.append("' = '");
			builderQuery.append(organizationId);
			builderQuery.append("'");

			String query = createQuery(builderQuery.toString(), Constants.FUNCTION_TABLE_NAME,
					Constants.JSON_COLUMN_NAME, "select");

			logger.debug("Executing query: " + query);
			List<Gsc004FunctionEntity> entityList = gsc004dao.getFunction(query);

			if (entityList.size() == 1) {
				return entityList.get(0);
			} else if (entityList.size() == 0) {
				return null;
			} else {
				logger.error("One result expected from query, results found: " + entityList.size());
				throw new DCException(Constants.ER01);
			}
		} catch (Exception e) {
			logger.error("Generic exception occurred ", e);
			throw new DCException(Constants.ER01);
		}

	}
	
	/**
	 * Retrieves the Function identified by the id parameter.
	 * 
	 * @param id
	 * @return
	 */
	private Gsc004FunctionEntity getFunctionObjetctById(Long id){
		return (Gsc004FunctionEntity) gsc004dao.load(id);
	}	
}
