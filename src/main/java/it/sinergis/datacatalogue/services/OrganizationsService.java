package it.sinergis.datacatalogue.services;

import java.util.List;

import it.sinergis.datacatalogue.bean.jpa.Gsc001OrganizationEntity;
import it.sinergis.datacatalogue.exception.DCException;
import it.sinergis.datacatalogue.persistence.PersistenceServiceProvider;
import it.sinergis.datacatalogue.persistence.services.Gsc001OrganizationPersistence;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;

public class OrganizationsService extends ServiceCommons{
	
	public final String ORGANIZATION_TABLE_NAME = "gsc_001_organization";
	public final String ORGANIZATION_COLUMN_NAME = "json";
	
	public final String ORG_NAME_FIELD = "OrganizationName";
	public final String NEW_ORG_NAME_FIELD = "NewOrganizationName";
	
	
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
				return "{\"Status\":\"Done\",\"Description\":\"Organization succesfully created\"}";
				
			//otherwise an error message will be return
			} else {
				DCException rpe = new DCException("ER08");
				return rpe.returnErrorString();				
			}
			
		}
		catch(DCException rpe) {
			return rpe.returnErrorString();
		} catch(Exception e) {
			logger.error("create organization service error",e);
			DCException rpe = new DCException("ER01");
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
				organizationPersistence.save(organization);
				
				logger.info("Organization succesfully updated");
				logger.info(req);
				return "{\"Status\":\"Done\",\"Description\":\"Organization succesfully updated\"}";
				
			//otherwise update current record
			} else {
				DCException rpe = new DCException("ER09");
				return rpe.returnErrorString();				
			}
			
		}
		catch(DCException rpe) {
			return rpe.returnErrorString();
		} catch(Exception e) {
			logger.error("update organization service error",e);
			DCException rpe = new DCException("ER01");
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
				return "{\"Status\":\"Done\",\"Description\":\"Organization succesfully deleted\"}";
				
			//otherwise error
			} else {
				DCException rpe = new DCException("ER10");
				return rpe.returnErrorString();				
			}
			
		}
		catch(DCException rpe) {
			return rpe.returnErrorString();
		} catch(Exception e) {
			logger.error("delete organization service error",e);
			DCException rpe = new DCException("ER01");
			logger.error("deleteOrganization service: unhandled error "+ rpe.returnErrorString());
			
			return rpe.returnErrorString();
		}
	}
	
	public String listOrganization(String req){
		return "{organizations:[{name:'Org1',description:'First organization',users:[{username:'user1'},{username:'user2'}]},{name:'Org2',description:'Second organization',users:[{username:'user3'},{username:'user4'}]}]";
	}
	
	
	
	/**
	 * Retrieves the organization given an organization name.
	 * 
	 * @param json
	 * @return
	 * @throws RPException
	 */
	private Gsc001OrganizationEntity getOrganizationObject(String json) throws DCException {
		
		try {
			String queryText = "'" + ORG_NAME_FIELD + "' = '"+getOrgNameFromJsonText(json)+"'";
			String query = createQuery(queryText, ORGANIZATION_TABLE_NAME,ORGANIZATION_COLUMN_NAME,"select");
			List<Gsc001OrganizationEntity> organizations = organizationPersistence.getOrganizations(query);
			
			if(organizations.isEmpty()) {
				return null;
			}
			//research query can only find 1 record at most
			return organizations.get(0);
		} catch(DCException rpe) {
			throw rpe;
		} catch(Exception e) {
			logger.error("unhandled error: ",e);
			throw new DCException("ER01");
		}
	}
	
	/**
	 * Returns organization name within the input json text parameter.
	 * 
	 * @param json
	 * @return
	 * @throws RPException
	 */
	private String getOrgNameFromJsonText(String json) throws DCException {
		try {
			JsonNode rootNode = om.readTree(keyToLowerCase(json));
			JsonNode orgName = rootNode.findValue(ORG_NAME_FIELD.toLowerCase());
			if(orgName == null) {
				logger.error(ORG_NAME_FIELD + " parameter is mandatory within the json string.");
				throw new DCException("ER04");
			}
						
			//delete quote from value else where clausole doesn't work
			return orgName.toString().replace("\"", "");
		} catch(DCException rpe) {
			throw rpe;
		} catch(Exception e) {
			logger.error("unhandled error: ",e);
			throw new DCException("ER01");
		}
	}
}
