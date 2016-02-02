package it.sinergis.datacatalogue.services;

import java.util.ArrayList;
import java.util.List;

import it.sinergis.datacatalogue.bean.jpa.Gsc001OrganizationEntity;
import it.sinergis.datacatalogue.bean.jpa.Gsc002UserEntity;
import it.sinergis.datacatalogue.bean.jpa.Gsc003RoleEntity;
import it.sinergis.datacatalogue.common.Constants;
import it.sinergis.datacatalogue.exception.DCException;
import it.sinergis.datacatalogue.persistence.PersistenceServiceProvider;
import it.sinergis.datacatalogue.persistence.services.Gsc003RolePersistence;
import it.sinergis.datacatalogue.persistence.services.jpa.Gsc002UserPersistenceJPA;

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
	
	/**
	 * Delete a role from database	
	 * @param req role's data (JSON format). Required JSON parameters : rolename and organization
	 * @return role's result (JSON format)
	 */		
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
		try{
			String query = null;
			List<Gsc003RoleEntity> roles = null;
			
			if(!req.equals("{}")){
				checkJsonWellFormed(req);
				
				String queryText = "'" + Constants.ORG_FIELD + "' = '"+ getKeyFromJsonText(req,Constants.ORG_FIELD) +"' AND '" + Constants.ROLE_NAME_FIELD + "' LIKE '"+getKeyFromJsonText(req,Constants.ROLE_NAME_FIELD)+"%'";
				query = createQuery(queryText, Constants.ROLE_TABLE_NAME, Constants.JSON_COLUMN_NAME,"select");
				
				roles = rolePersistence.loadByNativeQuery(query);
			}
			else
				roles = rolePersistence.loadAll();
									
			logger.info("Roles found: " + roles.size());
			
			logger.info(req);
			
			Gsc002UserPersistenceJPA userJpa = new Gsc002UserPersistenceJPA();
			
			StringBuilder rolesJson = new StringBuilder();
			rolesJson.append("{\"roles\":[");
			//select users for each role
			for (int i =0; i< roles.size();i++) {
				Gsc003RoleEntity role = roles.get(i);
				rolesJson.append(role.getJson());
				
				/*rolesJson.append("{");
				
				String orgName = getFieldValueFromJsonText(role.getJson(),Constants.ORG_NAME_FIELD);				
				rolesJson.append("\"name\":" + orgName);
				
				String description = getFieldValueFromJsonText(role.getJson(),Constants.DESCRIPTION_FIELD);
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
					orgsJson.append(",");	*/		
				
			}
			rolesJson.append("]}");
			
			return rolesJson.toString();
			
		}
		catch(DCException rpe) {
			return rpe.returnErrorString();
		} catch(Exception e) {
			logger.error("list roles service error",e);
			DCException rpe = new DCException(Constants.ER01);
			logger.error("listRoles service: unhandled error "+ rpe.returnErrorString());
			
			return rpe.returnErrorString();
		}
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
