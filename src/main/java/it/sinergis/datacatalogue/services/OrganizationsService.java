package it.sinergis.datacatalogue.services;

import java.util.ArrayList;
import java.util.List;

import it.sinergis.datacatalogue.bean.jpa.Gsc001OrganizationEntity;
import it.sinergis.datacatalogue.bean.jpa.Gsc002UserEntity;
import it.sinergis.datacatalogue.exception.DCException;
import it.sinergis.datacatalogue.persistence.PersistenceServiceProvider;
import it.sinergis.datacatalogue.persistence.services.Gsc001OrganizationPersistence;
import it.sinergis.datacatalogue.persistence.services.jpa.Gsc002UserPersistenceJPA;
import it.sinergis.datacatalogue.common.Constants;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

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
				
				organizationPersistence.insert(org);
				
				logger.info("Organization succesfully created");
				logger.info(req);
				return createJsonStatus(Constants.STATUS_DONE,Constants.ORGANIZATION_CREATED);
				
			//otherwise an error message will be return
			} else {
				DCException rpe = new DCException(Constants.ER08);
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
	
	public String updateOrganization(String req){
		try{
			checkJsonWellFormed(req);
			
			//check if there's another organization already saved with the same name
			Gsc001OrganizationEntity organization = getOrganizationObject(req);
							
			//if results found -> update record
			if(organization != null) {
				organization.setJson(updateOrganizationJson(req));
				organizationPersistence.save(organization);
				
				logger.info("Organization succesfully updated");
				logger.info(req);
				return createJsonStatus(Constants.STATUS_DONE,Constants.ORGANIZATION_UPDATED);
				
			//otherwise update current record
			} else {
				DCException rpe = new DCException(Constants.ER09);
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
	
	public String deleteOrganization(String req){
		try{
			checkJsonWellFormed(req);
			
			//check if there's another organization already saved with the same name
			Gsc001OrganizationEntity organization = getOrganizationObject(req);
							
			//if results found -> delete record
			if(organization != null) {
				organizationPersistence.delete(organization);
				
				logger.info("Organization succesfully deleted");
				logger.info(req);
				return createJsonStatus(Constants.STATUS_DONE,Constants.ORGANIZATION_DELETED);
				
			//otherwise error
			} else {
				DCException rpe = new DCException(Constants.ER10);
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
			
			StringBuilder orgsJson = new StringBuilder();
			orgsJson.append("{\"organizations\":[");
			//select users for each organization
			for (int i =0; i< orgs.size();i++) {
				Gsc001OrganizationEntity org = orgs.get(i);
				
				orgsJson.append("{");
				
				String orgName = getFieldValueFromJsonText(org.getJson(),Constants.ORG_NAME_FIELD);				
				orgsJson.append("\"name\":" + orgName);
				
				String description = getFieldValueFromJsonText(org.getJson(),Constants.DESCRIPTION_FIELD);
				if(description != null) {
					orgsJson.append(",");
					orgsJson.append("\"description\":" + description);
				}
				
				//Select user of current organization								
				String queryText = "'" + Constants.ORGANIZATION_FIELD + "' = '"+getKeyFromJsonText(org.getJson(),Constants.ORG_NAME_FIELD)+"'";
				query = createQuery(queryText, Constants.USER_TABLE_NAME, Constants.JSON_COLUMN_NAME,"select");
				List<Gsc002UserEntity> users = userJpa.loadByNativeQuery(query);
				if (users.size()> 0) {
					orgsJson.append(",");
					orgsJson.append("\"users\": [");
					for(int j = 0; j< users.size();j++) {
						Gsc002UserEntity user = users.get(j);
						
						orgsJson.append("{");
						orgsJson.append("\"username\":"+ getFieldValueFromJsonText(user.getJson(),Constants.USERNAME_FIELD));
						
						orgsJson.append("}");
						if(j < users.size() -1) 
							orgsJson.append(",");
					}
					orgsJson.append("]");
				}
				
				orgsJson.append("}");
				
				if(i < orgs.size() - 1)
					orgsJson.append(",");			
				
			}
			orgsJson.append("]}");
			
			return orgsJson.toString();
			
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
	
	
	private String updateOrganizationJson(String newJson) throws DCException {
		try {
			JsonNode newRootNode = om.readTree(newJson);
			
			JsonNode newOrgName = newRootNode.findValue(Constants.NEW_ORG_NAME_FIELD);
			if(newOrgName == null) {
				logger.error(Constants.NEW_ORG_NAME_FIELD + " parameter is mandatory within the json string.");
				throw new DCException(Constants.ER04);
			}
			
			((ObjectNode) newRootNode).put(Constants.ORG_NAME_FIELD, newOrgName.toString().replace("\"", ""));
			
			((ObjectNode) newRootNode).remove(Constants.NEW_ORG_NAME_FIELD);
								
			return newRootNode.toString();
		} catch(DCException rpe) {
			throw rpe;
		} catch(Exception e) {
			logger.error("unhandled error: ",e);
			throw new DCException(Constants.ER01);
		}			
	
	}	
}
