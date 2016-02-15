package it.sinergis.datacatalogue.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import it.sinergis.datacatalogue.bean.jpa.Gsc001OrganizationEntity;
import it.sinergis.datacatalogue.bean.jpa.Gsc002UserEntity;
import it.sinergis.datacatalogue.common.Constants;
import it.sinergis.datacatalogue.exception.DCException;
import it.sinergis.datacatalogue.persistence.PersistenceServiceProvider;
import it.sinergis.datacatalogue.persistence.services.Gsc001OrganizationPersistence;
import it.sinergis.datacatalogue.persistence.services.jpa.Gsc001OrganizationPersistenceJPA;
import it.sinergis.datacatalogue.persistence.services.jpa.Gsc002UserPersistenceJPA;

public class OrganizationsService extends ServiceCommons{
	
	
	/** Logger. */
	private static Logger logger;
	
	/** Gsc001Organization  DAO. */
	Gsc001OrganizationPersistence organizationPersistence;
	
	/**
	 * Constructor
	 */
	public OrganizationsService() {
		logger = Logger.getLogger(this.getClass());		
		organizationPersistence = PersistenceServiceProvider.getService(Gsc001OrganizationPersistence.class);
	}
	

	/**
	 * Create a new Organization into database	
	 * @param req new organization's data (JSON format)
	 * @return operation's result (JSON format)
	 */
	public String createOrganization(String req){

		try{
			checkJsonWellFormed(req);
			
			//check if there's another organization already saved with the same name
			Gsc001OrganizationEntity organization = getOrganizationObject(req);
							
			//if no results found -> add new record
			if(organization == null) {
				Gsc001OrganizationEntity org = new Gsc001OrganizationEntity();
				org.setJson(req);
				
				org = organizationPersistence.save(org);
				
				logger.info("Organization succesfully created");
				logger.info(req);
				return createJsonStatus(Constants.STATUS_DONE,Constants.ORGANIZATION_CREATED,org.getId(),req);
				
			//otherwise an error message will be return
			} else {
				DCException rpe = new DCException(Constants.ER101);
				return rpe.returnErrorString();				
			}
			
		}
		catch(DCException rpe) {
			return rpe.returnErrorString();
		} catch(Exception e) {
			logger.error("create organization service error",e);
			DCException rpe = new DCException(Constants.ER01);
			logger.error("createOrganization service: unhandled error "+ rpe.returnErrorString());
			
			return rpe.returnErrorString();
		}		
	}
	
	/**
	 * Update organization
	 * @param req json of organization to upadate
	 * @return status on json form
	 */
	public String updateOrganization(String req){
		try{
			checkJsonWellFormed(req);
			
			//check if there's another organization already saved with the same name we want to give to the organization
			Gsc001OrganizationEntity organization = getOrganizationObject(req);
							
			//if no results found -> update record
			if(organization == null) {
				//check if there's another organization already saved with the same ID
				Gsc001OrganizationEntity retrievedOrganization = getOrganizationObjectById(Long.parseLong(getFieldValueFromJsonText(req,Constants.ORG_ID_FIELD)));
				
				if(retrievedOrganization != null) {
					retrievedOrganization.setJson(updateOrganizationJson(req));
					organizationPersistence.save(retrievedOrganization);
					
					logger.info("Organization succesfully updated");
					logger.info(req);
					return createJsonStatus(Constants.STATUS_DONE,Constants.ORGANIZATION_UPDATED,null,req);
				} else {
					DCException rpe = new DCException(Constants.ER102);
					return rpe.returnErrorString();
				}
				
			//otherwise throw exception
			} else {
				DCException rpe = new DCException(Constants.ER104);
				return rpe.returnErrorString();				
			}
			
		}
		catch(DCException rpe) {
			return rpe.returnErrorString();
		} catch(Exception e) {
			logger.error("update organization service error",e);
			DCException rpe = new DCException(Constants.ER01);
			logger.error("updateOrganization service: unhandled error "+ rpe.returnErrorString());
			
			return rpe.returnErrorString();
		}
	}
	
	/**
	 * Delete existing organization
	 * @param req organization to delete on json form
	 * @return status on json form
	 */
	public String deleteOrganization(String req){
		try{
			checkJsonWellFormed(req);
			
			//check if there's another organization already saved with the same name
			Gsc001OrganizationEntity organization = getOrganizationObjectById(Long.parseLong(getFieldValueFromJsonText(req,Constants.ORG_ID_FIELD)));
							
			//if results found -> delete record
			if(organization != null) {
				organizationPersistence.delete(organization);
				
				//TODO
				//we need to explicitly handle deletion of tables that rely on this entity
				//by calling delete methods of the following:
				//datasource, application, grouplayer, function, role, user
				
				logger.info("Organization succesfully deleted");
				logger.info(req);
				return createJsonStatus(Constants.STATUS_DONE,Constants.ORGANIZATION_DELETED,null,req);
				
			//otherwise error
			} else {
				DCException rpe = new DCException(Constants.ER103);
				return rpe.returnErrorString();				
			}
			
		}
		catch(DCException rpe) {
			return rpe.returnErrorString();
		} catch(Exception e) {
			logger.error("delete organization service error",e);
			DCException rpe = new DCException(Constants.ER01);
			logger.error("deleteOrganization service: unhandled error "+ rpe.returnErrorString());
			
			return rpe.returnErrorString();
		}
	}
	
	/**
	 * Get a list of organizations 
	 * @param req search criteria on json fomr
	 * @return list of organizations or error message in json form
	 */
	public String listOrganization(String req){
		try{
			String query = null;
			List<Gsc001OrganizationEntity> orgs = null;
			
			if(!req.equals("{}")){
				checkJsonWellFormed(req);
				
				String queryText = "'" + Constants.ORG_NAME_FIELD + "' LIKE '"+getKeyFromJsonText(req,Constants.ORG_NAME_FIELD)+"%'";
				query = createQuery(queryText, Constants.ORGANIZATION_TABLE_NAME, Constants.JSON_COLUMN_NAME,"select");
				
				orgs = organizationPersistence.getOrganizations(query);
			}
			else
				orgs = organizationPersistence.loadAll();
									
			logger.info("Organizations found: " + orgs.size());
			
			logger.info(req);
			
			Gsc002UserPersistenceJPA userJpa = new Gsc002UserPersistenceJPA();
			
			ObjectNode root = JsonNodeFactory.instance.objectNode();
			//List<JsonNode> organizationNodeList = new ArrayList<JsonNode>();
			ArrayNode organizationNodeList =  JsonNodeFactory.instance.arrayNode();
			//select users for each organization
			for (int i =0; i< orgs.size();i++) {
				Gsc001OrganizationEntity org = orgs.get(i);
				organizationNodeList.add(createSearchJsonStatus(org.getId(),query,org,userJpa));			
			}
			root.put(Constants.ORGANIZATIONS_FIELD, organizationNodeList);
			
			ObjectMapper mapper = new ObjectMapper();
		    String jsonString;
		    try {
		        jsonString = mapper.writeValueAsString(root);
		    } catch (IOException e) {
		        jsonString = "fail"; 
		    }
		    
			return jsonString;
			
		}
		catch(DCException rpe) {
			return rpe.returnErrorString();
		} catch(Exception e) {
			logger.error("list organization service error",e);
			DCException rpe = new DCException(Constants.ER01);
			logger.error("listOrganization service: unhandled error "+ rpe.returnErrorString());
			
			return rpe.returnErrorString();
		}
	}
	
	/**
	 * Retrieves the organization given an organization name.
	 * 
	 * @param json
	 * @return
	 * @throws RPException
	 */
	private Gsc001OrganizationEntity getOrganizationObject(String json) throws DCException {
		ArrayList<String> params = new ArrayList<String>();
		params.add(Constants.ORG_NAME_FIELD);
		return (Gsc001OrganizationEntity) getRowObject(json, Constants.ORGANIZATION_TABLE_NAME, params, organizationPersistence);
	}	
	
	/**
	 * Retrieves the organization given an organization Id.
	 * 
	 * @param json
	 * @return
	 * @throws RPException
	 */
	private Gsc001OrganizationEntity getOrganizationObjectById(Long id) throws DCException {
		ArrayList<String> params = new ArrayList<String>();
		params.add(Constants.ORG_ID_FIELD);
		Gsc001OrganizationPersistenceJPA jpa = new Gsc001OrganizationPersistenceJPA();
		return (Gsc001OrganizationEntity) jpa.load(id);
	}	
	
	/**
	 * Update organization json
	 * @param newJson new json
	 * @return json updated
	 * @throws DCException
	 */
	private String updateOrganizationJson(String newJson) throws DCException {
		try {
			JsonNode newRootNode = om.readTree(newJson);
			
			JsonNode newOrgName = newRootNode.findValue(Constants.ORG_NAME_FIELD);
			if(newOrgName == null) {
				logger.error(Constants.ORG_NAME_FIELD + " parameter is mandatory within the json string.");
				throw new DCException(Constants.ER04);
			}
			
			((ObjectNode) newRootNode).put(Constants.ORG_NAME_FIELD, newOrgName.toString().replace("\"", ""));
			
			((ObjectNode) newRootNode).remove(Constants.ORG_ID_FIELD);
								
			return newRootNode.toString();
		} catch(DCException rpe) {
			throw rpe;
		} catch(Exception e) {
			logger.error("unhandled error: ",e);
			throw new DCException(Constants.ER01);
		}			
	
	}	
	
	private JsonNode createSearchJsonStatus(Long id,String query,Gsc001OrganizationEntity org,Gsc002UserPersistenceJPA userJpa) throws JsonProcessingException, IOException, DCException {
		ObjectMapper mapper = new ObjectMapper();
		
		ObjectNode organizationBasic = (ObjectNode) mapper.readTree(org.getJson());
		if(id != null) {
			organizationBasic.put(Constants.ID,id);
        }
		organizationBasic.put(Constants.USERS_FIELD, createUserListNode(org,query,userJpa));
		return organizationBasic;
		
	}
	
	private JsonNode createUserListNode(Gsc001OrganizationEntity org,String query,Gsc002UserPersistenceJPA userJpa) throws DCException {
		ObjectNode usersNodeList = JsonNodeFactory.instance.objectNode();
		//FIXME check back when users entity is done
		//Select user of current organization								
		String queryText = "'" + Constants.ORGANIZATION_FIELD + "' = '"+getKeyFromJsonText(org.getJson(),Constants.ORG_NAME_FIELD)+"'";
		query = createQuery(queryText, Constants.USER_TABLE_NAME, Constants.JSON_COLUMN_NAME,"select");
		List<Gsc002UserEntity> users = userJpa.loadByNativeQuery(query);
		
		if (users.size()> 0) {					
			for(int j = 0; j< users.size();j++) {
				Gsc002UserEntity user = users.get(j);
				usersNodeList.put(Constants.ID, user.getId());
			}			
		}
		return usersNodeList;
	}
}
