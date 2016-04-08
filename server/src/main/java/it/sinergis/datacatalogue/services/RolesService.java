package it.sinergis.datacatalogue.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import it.sinergis.datacatalogue.bean.jpa.Gsc002UserEntity;
import it.sinergis.datacatalogue.bean.jpa.Gsc003RoleEntity;
import it.sinergis.datacatalogue.common.Constants;
import it.sinergis.datacatalogue.exception.DCException;
import it.sinergis.datacatalogue.persistence.PersistenceServiceProvider;
import it.sinergis.datacatalogue.persistence.services.Gsc002UserPersistence;
import it.sinergis.datacatalogue.persistence.services.Gsc003RolePersistence;

public class RolesService extends ServiceCommons{

	/** Logger. */
	private static Logger logger;
	
	/** Gsc002UserPersistence  DAO. */
	private Gsc002UserPersistence gsc002dao;
	
	/** Gsc003RolePersistence  DAO. */
	private Gsc003RolePersistence gsc003dao;
		
	/**
	 * Constructor
	 */	
	public RolesService() {
		logger = Logger.getLogger(this.getClass());		
		gsc002dao = PersistenceServiceProvider.getService(Gsc002UserPersistence.class);
		gsc003dao = PersistenceServiceProvider.getService(Gsc003RolePersistence.class);
	}

	/**
	 * Create a new role into database	
	 * @param req new role's data (JSON format)
	 * @return role's result (JSON format)
	 */	
	public String createRole(String req){
		try{
			preliminaryChecks(req,Constants.CREATE_ROLE);
//			ArrayList<String> params = new ArrayList<String>();
//			params.add(Constants.ORG_FIELD);
//			params.add(Constants.ROLE_NAME_FIELD);
			
			checkIdOrganizationValid(req);
			
			//check if there's another role already saved with the same name
			Gsc003RoleEntity role = getRoleObject(req);
							
			//if no results found -> add new record
			if(role == null) {
				Gsc003RoleEntity newRole = new Gsc003RoleEntity();
				newRole.setJson(req);
				
				newRole = gsc003dao.save(newRole);
				
				logger.info("Role succesfully created");
				logger.info(req);
				return createJsonStatus(Constants.STATUS_DONE,Constants.ROLE_CREATED,newRole.getId(),req);
				
			//otherwise an error message will be return
			} else {
				DCException rpe = new DCException(Constants.ER301, req);
				return rpe.returnErrorString();				
			}
			
		}
		catch(DCException rpe) {
			return rpe.returnErrorString();
		} catch(Exception e) {
			logger.error("create role service error",e);
			DCException rpe = new DCException(Constants.ER01, req);
			logger.error("createRole service: unhandled error "+ rpe.returnErrorString());
			
			return rpe.returnErrorString();
		}	
	}
	
	/**
	 * Delete a role from database	
	 * @param req role's data (JSON format). Required JSON parameters : roleId
	 * @return role's result (JSON format)
	 */		
	public String deleteRole(String req){
		try{
			preliminaryChecks(req,Constants.DELETE_ROLE);
			
			Long roleId = Long.parseLong(getFieldValueFromJsonText(req, Constants.ROLE_ID_FIELD));
			
			//check if there's another organization already saved with the same name
			Gsc003RoleEntity role = getRoleObjetctById(roleId);
							
			//if results found -> delete record
			if(role != null) {
				
				DeleteService deleteService = new DeleteService();
				deleteService.deleteRole(null,null,role.getId(),null);
				
				logger.info("Role succesfully deleted");
				logger.info(req);
				return createJsonStatus(Constants.STATUS_DONE,Constants.ROLE_DELETED,null,req);
				
			//otherwise error
			} else {
				DCException rpe = new DCException(Constants.ER302, req);
				return rpe.returnErrorString();				
			}
			
		}
		catch(DCException rpe) {
			return rpe.returnErrorString();
		} catch(Exception e) {
			logger.error("delete role service error",e);
			DCException rpe = new DCException(Constants.ER01, req);
			logger.error("deleteRole service: unhandled error "+ rpe.returnErrorString());
			
			return rpe.returnErrorString();
		}
	}
	
	/**
	 * Returns a list of the roles belonging to the specified organization. If a partial or total rolename is
	 * give as an additional parameter refine the search by only returning those who match it.
	 * For each found role returns additional information about each user having that role assigned.
	 * 
	 * @param req
	 * @return
	 */
	public String listRole(String req){
		try{
			String query = null;
			List<Gsc003RoleEntity> roles = null;
			
			if(!req.equals("{}")){
				checkJsonWellFormed(req);
				
				String queryText = "";
				//if adminRecords parameter is true add all admin functions to the result list
				boolean searchAdminRecords = isParameterInJson(req,Constants.INCLUDE_ADMIN_RECORDS);
				if(searchAdminRecords && "true".equalsIgnoreCase(getKeyFromJsonText(req,Constants.INCLUDE_ADMIN_RECORDS))) {
					queryText += "(";
				}
				//add the main clause (search by orgId and partial name)
				queryText += "'" + Constants.ORG_FIELD + "' = '"+ getKeyFromJsonText(req,Constants.ORG_FIELD) +"' AND '" + Constants.ROLE_NAME_FIELD + "' LIKE '%"+getKeyFromJsonText(req,Constants.ROLE_NAME_FIELD)+"%'";

				//add the admin clause to the query
				if(searchAdminRecords && "true".equalsIgnoreCase(getKeyFromJsonText(req,Constants.INCLUDE_ADMIN_RECORDS))) {
					queryText += ") OR '" + Constants.ORG_FIELD + "' IS NULL";
				}
				
				query = createQuery(queryText, Constants.ROLE_TABLE_NAME, Constants.JSON_COLUMN_NAME,"select");
				
				roles = gsc003dao.loadByNativeQuery(query);

			}
			else
				roles = gsc003dao.loadAll();
									
			logger.info("Roles found: " + roles.size());
			logger.info(req);
			
			if (roles.size() == 0) {
				logger.error("No results found.");
				throw new DCException(Constants.ER13, req);
			}

			
			ObjectNode root = JsonNodeFactory.instance.objectNode();
			ArrayNode rolesNodeList = JsonNodeFactory.instance.arrayNode();
			for (Gsc003RoleEntity role : roles) {
				
				//get the role json
				ObjectNode roleJson = (ObjectNode) om.readTree(role.getJson());
				
				//ADD USER INFO
				//check if there are users having this role assigned...
				boolean hasUsers = isParameterInJson(role.getJson(), Constants.USERS_FIELD);
				ArrayNode usersIdHavingRole = JsonNodeFactory.instance.arrayNode();
				List<String> idList = new ArrayList<String>();
				//..if so get their ids
				if(hasUsers) {
					usersIdHavingRole = (ArrayNode) roleJson.path(Constants.USERS_FIELD);
					
					for(int i = 0; i < usersIdHavingRole.size(); i++) {
						idList.add(((ObjectNode) usersIdHavingRole.get(i)).get(Constants.USER_ID_FIELD).asText());
					}
				}
				
				String getUsersNameQuery = loadObjectFromIdList(Constants.USER_TABLE_NAME,idList);
				if(StringUtils.isNotEmpty(getUsersNameQuery)) {
					List<Gsc002UserEntity> users = gsc002dao.getUsers(getUsersNameQuery);
					//build a map <iduser, username>
					Map<Long,String> userIdToName = new HashMap<Long,String>();
					for(Gsc002UserEntity user : users) {
						userIdToName.put(user.getId(), getFieldValueFromJsonText(user.getJson(), Constants.USER_NAME_FIELD));
					}
					
					ArrayNode usersIdAndNameHavingRole = JsonNodeFactory.instance.arrayNode();
					//for each layer in the json response
					for(int i = 0; i < usersIdHavingRole.size(); i++) {
						ObjectNode userIdHavingRole = (ObjectNode) usersIdHavingRole.get(i);
						Long userId =  userIdHavingRole.get(Constants.USER_ID_FIELD).asLong();
						//search for its name in the map and add it to the id value
						String userName = userIdToName.get(userId);
						ObjectNode userIdAndNameHavingRole = JsonNodeFactory.instance.objectNode();
						userIdAndNameHavingRole.put(Constants.USER_ID_FIELD,userId);
						userIdAndNameHavingRole.put(Constants.USER_NAME_FIELD,userName);
						//add the new object to the list
						usersIdAndNameHavingRole.add(userIdAndNameHavingRole);	
					}
					//replace the old layer list only containing ids with the new one containing names as well
					roleJson.put(Constants.USERS_FIELD, usersIdAndNameHavingRole);
				}
				roleJson.put(Constants.ID, role.getId());	
				rolesNodeList.add(roleJson);
			}
			//add the list to the rootNode
			root.put(Constants.ROLES_FIELD,rolesNodeList);
			root.put(Constants.REQUEST, om.readTree(req));
			
			String jsonString;
			try {
				jsonString = om.writeValueAsString(root);
			} catch (IOException e) {
				logger.error("IOException during the construction of status response", e);
				throw new DCException(Constants.ER01, req);
			}

			return jsonString;
			
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
	
	/**
	 * Assigns a list of users to a role. The previously saved user list will be overridden.
	 * 
	 * @param req
	 * @return
	 */
	public String assignRole(String req){
		try {
			// preliminary checks on the request parameters
			preliminaryChecks(req, Constants.ASSIGN_USERS_TO_ROLE);
			
			// Find the role thorugh its id in the request
			// check if there is a role with that id
			Gsc003RoleEntity role = getRoleObjectById(
					Long.parseLong(getFieldValueFromJsonText(req, Constants.ROLE_ID_FIELD)));

			
			if(role == null) {
				logger.error(
						"Incorrect parameters: requested role does not exist.");
				throw new DCException(Constants.ER302, req);
			}
			
			ObjectNode roleToBeUpdated = (ObjectNode) om.readTree(role.getJson());
			ObjectNode requestJson = (ObjectNode) om.readTree(req);
			ArrayNode requestUsers =  (ArrayNode) requestJson.findValue(Constants.USERS_FIELD);
			
			//get all the users id involved
			List<Long> idusers = getUsersIdFromRequest(requestUsers);
			
			//create the request
			String query = createCheckUsersRequest(idusers,Long.parseLong(getFieldValueFromJsonText(role.getJson(),Constants.ORGANIZATION_FIELD)));
			//execute the request
			Long resultNumber = gsc002dao.countInId(query);
			//if at least one of the specified users does not exist throw error
			//if the countnumber is less than the id list size one or more records was not found
			if(resultNumber < idusers.size()) {
				logger.error(
						"Incorrect parameters: one of the requested users cannot be assigned to the role because it does not exist.");
				throw new DCException(Constants.ER304, req);
			}

			//assign the layer list
			roleToBeUpdated.put(Constants.USERS_FIELD, requestUsers);

			String updatedJson = om.writeValueAsString(roleToBeUpdated);
			
			role.setJson(updatedJson);
			gsc003dao.save(role);
			
			logger.info("users succesfully assigned to the role.");
			logger.info(req);
			return createJsonStatus(Constants.STATUS_DONE, Constants.USERS_ASSIGNED_TO_ROLE, null, req);
		} catch (DCException rpe) {
			return rpe.returnErrorString();
		} catch (NumberFormatException nfe) {
			logger.error("inserted id parameter is not a number", nfe);
			DCException rpe = new DCException(Constants.ER12, req);
			return rpe.returnErrorString();
		} catch (Exception e) {
			logger.error("assign users to role service error", e);
			DCException rpe = new DCException(Constants.ER01, req);
			logger.error("assign users to role service: unhandled error " + rpe.returnErrorString());

			return rpe.returnErrorString();
		}
	}
	
	/**
	 * Retrieves the Role identified by the id parameter.
	 * 
	 * @param id
	 * @return
	 */
	private Gsc003RoleEntity getRoleObjetctById(Long id){
		return (Gsc003RoleEntity) gsc003dao.load(id);
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
		return (Gsc003RoleEntity) getRowObject(json, Constants.ROLE_TABLE_NAME, params, gsc003dao);
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
	 * Extracts users id list from the request parameter
	 * 
	 * @param usersList
	 * @return
	 */
	private List<Long> getUsersIdFromRequest(ArrayNode usersList) {
		List<Long> usersId = new ArrayList<Long>();
		
		for(int i = 0; i < usersList.size(); i++) {
			JsonNode requestedUserId = usersList.get(i);
			JsonNode userIdNode = requestedUserId.findValue(Constants.USER_ID_FIELD);
			usersId.add(userIdNode.asLong());
		}
		
		return usersId;
	}
	
	/**
	 * Creates a query which
	 * Checks that the users to be added exist and belong to the same organization as the role they are being added to
	 * 
	 * @param ids
	 * @param organizationId
	 * @return
	 */
	private String createCheckUsersRequest(List<Long> ids, Long organizationId) {
		StringBuilder sb = new StringBuilder();
		
		//This condition counts the existing users count matches the count on the users that were passed as arguments
		sb.append("select count(*) from ");
		sb.append(Constants.USER_TABLE_NAME);
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
}
