package it.sinergis.datacatalogue.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import it.sinergis.datacatalogue.bean.jpa.Gsc001OrganizationEntity;
import it.sinergis.datacatalogue.bean.jpa.Gsc006DatasourceEntity;
import it.sinergis.datacatalogue.common.Constants;
import it.sinergis.datacatalogue.exception.DCException;
import it.sinergis.datacatalogue.persistence.PersistenceServiceProvider;
import it.sinergis.datacatalogue.persistence.services.Gsc001OrganizationPersistence;
import it.sinergis.datacatalogue.persistence.services.Gsc006DatasourcePersistence;

public class DatasourcesService extends ServiceCommons {

	/** Logger. */
	private static Logger logger;
	
	/** Gsc001Datasource  DAO. */
	Gsc006DatasourcePersistence datasourcePersistence;
	
	/**
	 * Constructor
	 */
	public DatasourcesService() {
		logger = Logger.getLogger(this.getClass());		
		datasourcePersistence = PersistenceServiceProvider.getService(Gsc006DatasourcePersistence.class);
	}
	
	public static String RESPONSE_JSON_STATUS_DONE =  "{Status:'Done',Description:'Operation ok'}";
	public static String RESPONSE_JSON_LIST_DATASOURCE = " {datasource:[{name:'Datasource1',type:'SHAPE',description:'directory of shape files',updated:'12/10/2015',path:'c:\temp'},{name:'Datasource2',type:'ORACLE',description:'SDO layers',updated:'/09/03/2015',url:'jdbc:postgresql://localhost:5432/postgres',username:'admin'}]}";
	public static String RESPONSE_JSON_LIST_DATAORIGIN = "{dataorigin:[{name:'Dataorigin1',path:'C:\temp'},{name:'Dataorigin2',path:'/opt/temp'}]}";
	
	/**
	 * Create a new Datasource into database	
	 * @param req new Datasource data (JSON format)
	 * @return operation's result (JSON format)
	 */
	public String createDatasource(String req){
		try{
			//preliminary checks on the request parameters
			preliminaryChecks(req,Constants.CREATE_DATASOURCE);
			
			//check if the inserted organization id exists in the organization table. If not throws exception
			checkIdOrganizationValid(req); 
			//check if there's another datasource already saved with the same name
			Gsc006DatasourceEntity datasource = getDatasourceObject(req);
							
			//if no results found -> add new record
			if(datasource == null) {
				Gsc006DatasourceEntity ds = new Gsc006DatasourceEntity();
				ds.setJson(req);
				
				ds = datasourcePersistence.save(ds);
				
				logger.info("Datasource succesfully created");
				logger.info(req);
				return createJsonStatus(Constants.STATUS_DONE,Constants.DATASOURCE_CREATED,ds.getId(),req);
				
			//otherwise an error message will be return
			} else {
				DCException rpe = new DCException(Constants.ER601,req);
				return rpe.returnErrorString();				
			}
			
		} catch(DCException rpe) {
			return rpe.returnErrorString();
		} catch(NumberFormatException nfe) {
			logger.error("inserted id parameter is not a number",nfe);
			DCException rpe = new DCException(Constants.ER12,req);
			return rpe.returnErrorString();
		} catch(Exception e) {
			logger.error("createDatasource service error",e);
			DCException rpe = new DCException(Constants.ER01,req);
			logger.error("createDatasource service: unhandled error "+ rpe.returnErrorString());
			
			return rpe.returnErrorString();
		}		
	}
	/**
	 * Update datasource
	 * @param req json of datasource to upadate
	 * @return status on json form
	 */
	public String updateDatasource(String req){
		try{
			
			//preliminary checks on the request parameters
			preliminaryChecks(req,Constants.UPDATE_DATASOURCE);
			
			//check if the inserted organization id exists in the organization table. If not throws exception
			checkIdOrganizationValid(req); 
			
			Gsc006DatasourceEntity datasource = getDatasourceObject(req);
			Long requestedId = Long.parseLong(getFieldValueFromJsonText(req,Constants.DATASOURCE_ID_FIELD));			
			
			//if no datasource with the specified name exists or if the only record found with the same name is the record to be updated itself -> update record
			if(datasource == null || datasource.getId() == requestedId) {
				//check if there's another datasource already saved with the same ID
				Gsc006DatasourceEntity retrievedDatasource = getDatasourceObjectById(requestedId);
				
				if(retrievedDatasource != null) {
					retrievedDatasource.setJson(updateDatasourceJson(req));
					datasourcePersistence.save(retrievedDatasource);
					
					logger.info("Datasource succesfully updated");
					logger.info(req);
					return createJsonStatus(Constants.STATUS_DONE,Constants.DATASOURCE_UPDATED,null,req);
				} else {
					DCException rpe = new DCException(Constants.ER602,req);
					return rpe.returnErrorString();
				}
				
			//otherwise throw exception
			} else {
				DCException rpe = new DCException(Constants.ER604,req);
				return rpe.returnErrorString();				
			}
			
		}
		catch(DCException rpe) {
			return rpe.returnErrorString();
		} catch(NumberFormatException nfe) {
			logger.error("inserted id parameter is not a number",nfe);
			DCException rpe = new DCException(Constants.ER12,req);
			return rpe.returnErrorString();
		} catch(Exception e) {
			logger.error("update datasource service error",e);
			DCException rpe = new DCException(Constants.ER01,req);
			logger.error("updateDatasource service: unhandled error "+ rpe.returnErrorString());
			
			return rpe.returnErrorString();
		}
	}
	
	/**
	 * Delete existing datasource
	 * @param req datasource to delete on json form
	 * @return status on json form
	 */
	public String deleteDatasource(String req){
		try{
			//preliminary checks on the request parameters
			preliminaryChecks(req,Constants.DELETE_DATASOURCE);
			
			//check if there is a datasource with that id
			Gsc006DatasourceEntity datasource = getDatasourceObjectById(Long.parseLong(getFieldValueFromJsonText(req,Constants.DATASOURCE_ID_FIELD)));
							
			//if results found -> delete record
			if(datasource != null) {
				datasourcePersistence.delete(datasource);
				
				//TODO
				//we need to explicitly handle deletion of tables that rely on this entity
				//by calling delete methods of the following:
				//dataset
				
				logger.info("Datasource succesfully deleted");
				logger.info(req);
				return createJsonStatus(Constants.STATUS_DONE,Constants.DATASOURCE_DELETED,null,req);
				
			//otherwise error
			} else {
				DCException rpe = new DCException(Constants.ER603);
				return rpe.returnErrorString();				
			}
			
		}
		catch(DCException rpe) {
			return rpe.returnErrorString();
		} catch(NumberFormatException nfe) {
			logger.error("inserted id parameter is not a number",nfe);
			DCException rpe = new DCException(Constants.ER12,req);
			return rpe.returnErrorString();
		} catch(Exception e) {
			logger.error("delete datasource service error",e);
			DCException rpe = new DCException(Constants.ER01,req);
			logger.error("deleteDatasource service: unhandled error "+ rpe.returnErrorString());
			
			return rpe.returnErrorString();
		}
	}
	
	/**
	 * Get a list of datasources 
	 * @param req search criteria on json fomr
	 * @return list of datasources or error message in json form
	 */
	public String listDatasource(String req){
		try{
			String query = null;
			List<Gsc006DatasourceEntity> datasources = new ArrayList<Gsc006DatasourceEntity>();
			
			if(!req.equals("{}")){
				//preliminary checks on the request parameters
				preliminaryChecks(req,Constants.LIST_DATASOURCE);
				
				//There are two research modes:
				String iddatasourceParameter = getFieldValueFromJsonText(req,Constants.DATASOURCE_ID_FIELD);
				String datasourcenameParameter = getFieldValueFromJsonText(req,Constants.DATASOURCE_NAME_FIELD);
				//if the iddatasource parameter is in the request the research will be done by id. THIS has priority over other research types.
				if(iddatasourceParameter != null) {
					Gsc006DatasourceEntity datasourceFoundById = getDatasourceObjectById(Long.parseLong(iddatasourceParameter));
					if(datasourceFoundById != null) {
						datasources.add(datasourceFoundById);
					}
				//otherwise the research is based on the datasourcename parameter.
				} else if(datasourcenameParameter != null) {
					String queryText = "'" + Constants.DATASOURCE_NAME_FIELD + "' LIKE '%"+getKeyFromJsonText(req,Constants.DATASOURCE_NAME_FIELD)+"%'";
					
					//user may additionally specify a given organization for the request
					String organizationidParameter = getFieldValueFromJsonText(req,Constants.ORG_FIELD);
					if(organizationidParameter != null) {
						queryText += " AND '"+Constants.ORG_FIELD +"' =  '"+organizationidParameter+"'";
					}
					
					query = createQuery(queryText, Constants.DATASOURCE_TABLE_NAME, Constants.JSON_COLUMN_NAME,"select");
					datasources = datasourcePersistence.getDatasources(query);

				} else {
					logger.error("Incorrect parameters: either datasourcename or iddatasource parameters should be specified or else use an empty request {}");
	                throw new DCException(Constants.ER605, req);
				}
			}
			else {
				datasources = datasourcePersistence.loadAll();
			}
									
			logger.info("Datasources found: " + datasources.size());
			logger.info(req);
			if(datasources.size() == 0) {
				logger.error("No results found.");
	            throw new DCException(Constants.ER13, req);
			}
			
			ObjectMapper mapper = new ObjectMapper();
			ObjectNode root = JsonNodeFactory.instance.objectNode();
			ArrayNode datasourceNodeList =  JsonNodeFactory.instance.arrayNode();
			for(Gsc006DatasourceEntity datasource : datasources) {
				ObjectNode datasourceBasic = (ObjectNode) mapper.readTree(datasource.getJson());
				
				
				//user may specify detail level of the response. We assume true if not specified (verbose response).
				String detailParameter = getFieldValueFromJsonText(req,Constants.DETAIL_FIELD);
				if(detailParameter == null || "true".equalsIgnoreCase(detailParameter)) {
					datasourceBasic.put(Constants.ID,datasource.getId());
					datasourceNodeList.add(datasourceBasic);
				//short response (only name and id)
				} else if("false".equalsIgnoreCase(detailParameter)){
					ObjectNode modifiedDatasource = JsonNodeFactory.instance.objectNode();
					//copies datasourcename value 
					modifiedDatasource.put(Constants.DATASOURCE_NAME_FIELD, datasourceBasic.get(Constants.DATASOURCE_NAME_FIELD));
					modifiedDatasource.put(Constants.ID,datasource.getId());
					datasourceNodeList.add(modifiedDatasource);
				} else {
					logger.error("Incorrect parameters: detail parameter value can only be 'true' or 'false' if omitted it will be considered as true");
	                throw new DCException(Constants.ER606, req);
				}	
			}
			root.put(Constants.DATASOURCES_NAME_FIELD,datasourceNodeList);
		    String jsonString;
		    try {
		        jsonString = mapper.writeValueAsString(root);
		    } catch (IOException e) {
        		logger.error("IOException during the construction of status response", e);
               throw new DCException(Constants.ER01, req);
            }
		    
			return jsonString;
			
		}
		catch(DCException rpe) {
			return rpe.returnErrorString();
		} catch(NumberFormatException nfe) {
			logger.error("inserted id parameter is not a number",nfe);
			DCException rpe = new DCException(Constants.ER12,req);
			return rpe.returnErrorString();
		} catch(Exception e) {
			logger.error("list datasource service error",e);
			DCException rpe = new DCException(Constants.ER01,req);
			logger.error("listDatasource service: unhandled error "+ rpe.returnErrorString());
			
			return rpe.returnErrorString();
		}
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
	
	/**
	 * Retrieves the datasource given an datasource name.
	 * 
	 * @param json
	 * @return
	 * @throws RPException
	 */
	private Gsc006DatasourceEntity getDatasourceObject(String json) throws DCException {
		ArrayList<String> params = new ArrayList<String>();
		params.add(Constants.DATASOURCE_NAME_FIELD);
		params.add(Constants.ORGANIZATION_FIELD);
		return (Gsc006DatasourceEntity) getRowObject(json, Constants.DATASOURCE_TABLE_NAME, params, datasourcePersistence);
	}
	
	/**
	 * Retrieves the datasource given an datasource Id.
	 * 
	 * @param json
	 * @return
	 * @throws RPException
	 */
	private Gsc006DatasourceEntity getDatasourceObjectById(Long id) throws DCException {
		return (Gsc006DatasourceEntity) datasourcePersistence.load(id);
	}	
	
	/**
	 * Update datasource json
	 * @param newJson new json
	 * @return json updated
	 * @throws DCException
	 */
	private String updateDatasourceJson(String newJson) throws DCException {
		try {
			JsonNode newRootNode = om.readTree(newJson);
			
			JsonNode newDSName = newRootNode.findValue(Constants.DATASOURCE_NAME_FIELD);
			if(newDSName == null) {
				logger.error(Constants.DATASOURCE_NAME_FIELD + " parameter is mandatory within the json string.");
				throw new DCException(Constants.ER04);
			}
			
			((ObjectNode) newRootNode).put(Constants.DATASOURCE_NAME_FIELD, newDSName.toString().replace("\"", ""));
			
			((ObjectNode) newRootNode).remove(Constants.DATASOURCE_ID_FIELD);
								
			return newRootNode.toString();
		} catch(DCException rpe) {
			throw rpe;
		} catch(Exception e) {
			logger.error("unhandled error: ",e);
			throw new DCException(Constants.ER01,newJson);
		}			
	
	}
	
	/**
	 * Checks if the given parameter for organization matches the id of any existing organization.
	 * @param orgId
	 * @return
	 * @throws DCException 
	 * @throws NumberFormatException 
	 */
	private void checkIdOrganizationValid(String req) throws NumberFormatException, DCException {
		Long orgId = Long.parseLong(getKeyFromJsonText(req,Constants.ORG_FIELD));
		Gsc001OrganizationPersistence orgPersistence = PersistenceServiceProvider.getService(Gsc001OrganizationPersistence.class); 
		Gsc001OrganizationEntity orgEntity = orgPersistence.load(orgId);
		if(orgEntity == null) {
			DCException rpe = new DCException(Constants.ER607,req);
			throw rpe;		
		}
	}
}
