package it.sinergis.datacatalogue.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
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
			//preliminary checks on the request parameters
			preliminaryChecks(req,Constants.CREATE_ORGANIZATION);
			
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
				DCException rpe = new DCException(Constants.ER101,req);
				return rpe.returnErrorString();				
			}
			
		}
		catch(DCException rpe) {
			return rpe.returnErrorString();
		} catch(Exception e) {
			logger.error("create organization service error",e);
			DCException rpe = new DCException(Constants.ER01,req);
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
			//preliminary checks on the request parameters
			preliminaryChecks(req,Constants.UPDATE_ORGANIZATION);
			
			//check if there's another organization already saved with the same name we want to give to the organization
			Gsc001OrganizationEntity organization = getOrganizationObject(req);
			Long requestedId = Long.parseLong(getFieldValueFromJsonText(req,Constants.ORG_ID_FIELD));	
			//if the only record found with the same name is the record to be updated itself -> update record
			if(organization == null || organization.getId().longValue() == requestedId.longValue()) {
				//check if there's another organization already saved with the same ID
				Gsc001OrganizationEntity retrievedOrganization = getOrganizationObjectById(requestedId);
				
				if(retrievedOrganization != null) {
					retrievedOrganization.setJson(updateOrganizationJson(req));
					organizationPersistence.save(retrievedOrganization);
					
					logger.info("Organization succesfully updated");
					logger.info(req);
					return createJsonStatus(Constants.STATUS_DONE,Constants.ORGANIZATION_UPDATED,null,req);
				} else {
					DCException rpe = new DCException(Constants.ER102,req);
					return rpe.returnErrorString();
				}
				
			//otherwise throw exception
			} else {
				DCException rpe = new DCException(Constants.ER101,req);
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
			logger.error("update organization service error",e);
			DCException rpe = new DCException(Constants.ER01,req);
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
			//preliminary checks on the request parameters
			preliminaryChecks(req,Constants.DELETE_ORGANIZATION);
			
			//check if there's another organization already saved with the same name
			Gsc001OrganizationEntity organization = getOrganizationObjectById(Long.parseLong(getFieldValueFromJsonText(req,Constants.ORG_ID_FIELD)));
							
			//if results found -> delete record
			if(organization != null) {
				
				DeleteService deleteService = new DeleteService();
				deleteService.deleteOrganization(null, null, organization.getId());
				//organizationPersistence.delete(organization);
				
				logger.info("Organization succesfully deleted");
				logger.info(req);
				return createJsonStatus(Constants.STATUS_DONE,Constants.ORGANIZATION_DELETED,null,req);
				
			//otherwise error
			} else {
				DCException rpe = new DCException(Constants.ER102);
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
			logger.error("delete organization service error",e);
			DCException rpe = new DCException(Constants.ER01,req);
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
				
				//preliminary checks on the request parameters
				checkJsonWellFormed(req);
				// checks if the request contains all the mandatory parameters
				checkMandatoryParameters(Constants.LIST_ORGANIZATION, req);
				
				String organizationNameField = getFieldValueFromJsonText(req,Constants.ORG_NAME_FIELD);
				String idOrganization = getFieldValueFromJsonText(req,Constants.ORG_ID_FIELD);
				
				if(StringUtils.isNotEmpty(idOrganization) && StringUtils.isNotEmpty(organizationNameField)) {
					StringBuilder sb = new StringBuilder();

					String queryText = "'" + Constants.ORG_NAME_FIELD + "' ILIKE '%"+getKeyFromJsonText(req,Constants.ORG_NAME_FIELD)+"%'";
					sb.append(createQuery(queryText, Constants.ORGANIZATION_TABLE_NAME, Constants.JSON_COLUMN_NAME,"select"));
					
					sb.append(" AND ").append(Constants.ID).append(" = ").append(idOrganization);
					query = sb.toString();
					orgs = organizationPersistence.getOrganizations(query);
				}
				else if(StringUtils.isNotEmpty(idOrganization)) {
					Gsc001OrganizationEntity orgEntity = organizationPersistence.load(Long.parseLong(idOrganization));
					orgs = new ArrayList<Gsc001OrganizationEntity>();
					if (orgEntity!= null)
					{
						orgs.add(orgEntity);
					}
					else
					{
						logger.error("No organization found.");
						throw new DCException(Constants.ER102, req);
					}
				} else if(StringUtils.isNotEmpty(organizationNameField)) {
					String queryText = "'" + Constants.ORG_NAME_FIELD + "' ILIKE '%"+getKeyFromJsonText(req,Constants.ORG_NAME_FIELD)+"%'";
					query = createQuery(queryText, Constants.ORGANIZATION_TABLE_NAME, Constants.JSON_COLUMN_NAME,"select");
					orgs = organizationPersistence.getOrganizations(query);
				}
				
			}
			else {
				orgs = organizationPersistence.loadAll();
			}
									
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
			logger.error("list organization service error",e);
			DCException rpe = new DCException(Constants.ER01,req);
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
		return (Gsc001OrganizationEntity) organizationPersistence.load(id);
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
			throw new DCException(Constants.ER01,newJson);
		}			
	
	}	
	
	/**
	 * Creates the organization node
	 * 
	 * @param id
	 * @param query
	 * @param org
	 * @param userJpa
	 * @return
	 * @throws JsonProcessingException
	 * @throws IOException
	 * @throws DCException
	 */
	private JsonNode createSearchJsonStatus(Long id,String query,Gsc001OrganizationEntity org,Gsc002UserPersistenceJPA userJpa) throws JsonProcessingException, IOException, DCException {
		ObjectMapper mapper = new ObjectMapper();
		
		ObjectNode organizationBasic = (ObjectNode) mapper.readTree(org.getJson());
		if(id != null) {
			organizationBasic.put(Constants.ID,id);
        }
		organizationBasic.put(Constants.USERS_FIELD, createUserListNode(org,query,userJpa));
		return organizationBasic;
		
	}
	
	/**
	 * Creates the username json node
	 * 
	 * @param org
	 * @param query
	 * @param userJpa
	 * @return
	 * @throws DCException
	 */
	private JsonNode createUserListNode(Gsc001OrganizationEntity org,String query,Gsc002UserPersistenceJPA userJpa) throws DCException {
		ArrayNode usersNodeList = JsonNodeFactory.instance.arrayNode();

		query = createUserSearchWithinOrganizationQuery(org.getId());
		
		List<Gsc002UserEntity> users = userJpa.getUsers(query);
		
		if (users.size()> 0) {					
			for(int j = 0; j< users.size();j++) {
				Gsc002UserEntity user = users.get(j);
				ObjectNode node = JsonNodeFactory.instance.objectNode();
				node.put(Constants.ID, user.getId());
				usersNodeList.add(node);
			}			
		}
		return usersNodeList;
	}
	
	/**
	 * This query will be used to retrieve all the users id of users belonging to an organization.
	 * 
	 * An example of the created query could be:
	 * 
	 *  select * from gscdatacatalogue.gsc_002_user usr 
	 *  where usr.id IN (
	 *		Select id from gscdatacatalogue.gsc_002_user usr,
	 *		jsonb_array_elements(usr.json->'organizations') org 
	 *		where org.json->>'organization' = '1'
	 *	)
	 * 
	 * 
	 * 
	 * @param id
	 */
	private String createUserSearchWithinOrganizationQuery(Long id) {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("select * from "); 
		sb.append(Constants.USER_TABLE_NAME);
		sb.append(" usr ");
		sb.append("where usr.id IN ( Select id from ");
		sb.append(Constants.USER_TABLE_NAME);
		sb.append(" usr, ");
		sb.append("jsonb_array_elements(usr.");
		sb.append(Constants.JSON_COLUMN_NAME);
		sb.append("->'");
		sb.append(Constants.ORGANIZATIONS_FIELD);
		sb.append("') org where org.");
		sb.append(Constants.JSON_COLUMN_NAME);
		sb.append("->>");
		sb.append("'");
		sb.append(Constants.ORG_FIELD);
		sb.append("' = '");
		sb.append(id);
		sb.append("')");
		
		return sb.toString();
	}
	
}
