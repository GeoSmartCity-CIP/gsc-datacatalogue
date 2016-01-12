package it.sinergis.datacatalogue.services;

import java.util.ArrayList;

import it.sinergis.datacatalogue.bean.jpa.Gsc001OrganizationEntity;
import it.sinergis.datacatalogue.bean.jpa.Gsc003RoleEntity;
import it.sinergis.datacatalogue.common.Constants;
import it.sinergis.datacatalogue.exception.DCException;
import it.sinergis.datacatalogue.persistence.PersistenceServiceProvider;
import it.sinergis.datacatalogue.persistence.services.Gsc003RolePersistence;

import org.apache.log4j.Logger;

public class RolesService extends ServiceCommons{

	/** Logger. */
	private static Logger logger;
	
	/** Gsc003RolePersistence  DAO. */
	Gsc003RolePersistence rolePersistence;
		
	/**
	 * Constructor
	 */	
	public RolesService() {
		logger = Logger.getLogger(this.getClass());		
		rolePersistence = PersistenceServiceProvider.getService(Gsc003RolePersistence.class);
	}

	/**
	 * Create a new role into database	
	 * @param req new role's data (JSON format)
	 * @return role's result (JSON format)
	 */	
	public String createRole(String req){
		try{
			checkJsonWellFormed(req);
			ArrayList<String> params = new ArrayList<String>();
			params.add(Constants.ORG_FIELD);
			params.add(Constants.ROLE_NAME_FIELD);
			
			//check if there's another role already saved with the same name
			Gsc003RoleEntity role = getRoleObject(req);
							
			//if no results found -> add new record
			if(role == null) {
				Gsc003RoleEntity newRole = new Gsc003RoleEntity();
				newRole.setJson(req);
				
				rolePersistence.insert(newRole);
				
				logger.info("Role succesfully created");
				logger.info(req);
				return createJsonStatus(Constants.STATUS_DONE,Constants.ROLE_CREATED);
				
			//otherwise an error message will be return
			} else {
				DCException rpe = new DCException(Constants.ER08);
				return rpe.returnErrorString();				
			}
			
		}
		catch(DCException rpe) {
			return rpe.returnErrorString();
		} catch(Exception e) {
			logger.error("create role service error",e);
			DCException rpe = new DCException(Constants.ER01);
			logger.error("createRole service: unhandled error "+ rpe.returnErrorString());
			
			return rpe.returnErrorString();
		}	
	}
	
	public String deleteRole(String req){
		try{
			checkJsonWellFormed(req);
			
			//check if there's another organization already saved with the same name
			Gsc003RoleEntity role = getRoleObject(req);
							
			//if results found -> delete record
			if(role != null) {
				rolePersistence.delete(role);
				
				logger.info("Role succesfully deleted");
				logger.info(req);
				return createJsonStatus(Constants.STATUS_DONE,Constants.ROLE_DELETED);
				
			//otherwise error
			} else {
				DCException rpe = new DCException(Constants.ER10);
				return rpe.returnErrorString();				
			}
			
		}
		catch(DCException rpe) {
			return rpe.returnErrorString();
		} catch(Exception e) {
			logger.error("delete role service error",e);
			DCException rpe = new DCException(Constants.ER01);
			logger.error("deleteRole service: unhandled error "+ rpe.returnErrorString());
			
			return rpe.returnErrorString();
		}
	}
	
	public String listRole(String req){
		return "";
	}
	
	public String assignRole(String req){
		return "";
	}
	
	/**
	 * Retrieves the role given an organization and role name.
	 * 
	 * @param json
	 * @return
	 * @throws DCException
	 */
	private Gsc003RoleEntity getRoleObject(String json) throws DCException {
		ArrayList<String> params = new ArrayList<String>();
		params.add(Constants.ORG_FIELD);
		params.add(Constants.ROLE_NAME_FIELD);
		
		//check if there's another role already saved with the same name
		return (Gsc003RoleEntity) getRowObject(json, Constants.ROLE_TABLE_NAME, params, rolePersistence);
	}		
}
