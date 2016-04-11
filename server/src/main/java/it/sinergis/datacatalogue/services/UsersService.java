package it.sinergis.datacatalogue.services;

import java.io.IOException;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import it.sinergis.datacatalogue.bean.jpa.Gsc002UserEntity;
import it.sinergis.datacatalogue.bean.jpa.Gsc003RoleEntity;
import it.sinergis.datacatalogue.bean.jpa.Gsc005PermissionEntity;
import it.sinergis.datacatalogue.common.Constants;
import it.sinergis.datacatalogue.exception.DCException;
import it.sinergis.datacatalogue.persistence.PersistenceServiceProvider;
import it.sinergis.datacatalogue.persistence.services.Gsc002UserPersistence;
import it.sinergis.datacatalogue.persistence.services.Gsc003RolePersistence;
import it.sinergis.datacatalogue.persistence.services.Gsc005PermissionPersistence;

public class UsersService extends ServiceCommons {

	/** Logger. */
	private static Logger logger;

	/** Dao users. */
	private Gsc002UserPersistence gsc002Dao;
	
	/** Dao users. */
	private Gsc003RolePersistence gsc003Dao;
	
	/** Dao users. */
	private Gsc005PermissionPersistence gsc005Dao;
	
	SimpleDateFormat sdf;

	public UsersService() {
		super();
		logger = Logger.getLogger(this.getClass());
		gsc002Dao = PersistenceServiceProvider.getService(Gsc002UserPersistence.class);
		gsc003Dao = PersistenceServiceProvider.getService(Gsc003RolePersistence.class);
		gsc005Dao = PersistenceServiceProvider.getService(Gsc005PermissionPersistence.class);
		
		sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	}
	
	public String verifyMail(String jsonRequest) throws DCException {
		return verifyMail(getFieldValueFromJsonText(jsonRequest, Constants.UUID),getFieldValueFromJsonText(jsonRequest, Constants.ID));
	}
	
	/**
	 * This method is called by clicking on the link in the complete registration process.
	 * If the link is correct the system will complete the user registration.
	 * 
	 * @param uuid
	 * @param id
	 * @return
	 */
	public String verifyMail(String uuid,String id) {
		logger.info("Verification service ...");
		logger.info("uuid="+ uuid);
		logger.info("id="+ id);
		try {
			//get the record with the given id
			Gsc002UserEntity retrievedUser = getUserObjectById(Long.parseLong(id));
			//check if the UUID exists, if the status is to be verified and if request UUID is equal to it
			if(retrievedUser == null) {
				logger.error("The user id in the request does not match any existing user.");
				DCException rpe = new DCException(Constants.ER205);
				throw rpe;
			} else {
				String retrievedUUID = getFieldValueFromJsonText(retrievedUser.getJson(),Constants.UUID);
				if(retrievedUUID == null) {
					logger.error("This user has already verified his/her email.");
					DCException rpe = new DCException(Constants.ER206);
					throw rpe;
				} else if(!retrievedUUID.equalsIgnoreCase(uuid)) {
					logger.error("Security error: the uuid code does not match for this user.");
					DCException rpe = new DCException(Constants.ER207);
					throw rpe;
				} else {
					//perform update
					ObjectNode node = (ObjectNode) om.readTree(retrievedUser.getJson());
					node.put(Constants.STATUS_FIELD,Constants.VERIFIED);
					node.remove(Constants.UUID);
					retrievedUser.setJson(om.writeValueAsString(node));
					gsc002Dao.save(retrievedUser);
					
					logger.info("User email succesfully verified");
					
					return createJsonStatus(Constants.STATUS_DONE, Constants.USER_EMAIL_VERIFIED);
				}
			}
		} catch (DCException rpe) {
			return rpe.returnErrorString();
		} catch (NumberFormatException nfe) {
			logger.error("inserted id parameter is not a number", nfe);
			DCException rpe = new DCException(Constants.ER12);
			return rpe.returnErrorString();
		} catch (Exception e) {
			logger.error("register user service error", e);
			DCException rpe = new DCException(Constants.ER01);
			logger.error("register user service: unhandled error " + rpe.returnErrorString());

			return rpe.returnErrorString();
		}		
	}
	
	/**
	 * This method checks whether the user is registered. If the login is successful the user 
	 * will be able to edit data for any of his organizations.
	 * 
	 * @param req
	 * @return
	 */
	public String login(String req) {
		Gsc002UserEntity user = null;
		try {
			// preliminary checks on the request parameters
			preliminaryChecks(req, Constants.LOGIN);
			
			//checks if there is a row having the inserted username 
			user = getUserObject(req,true,false);
			
			//if no results are found throw exception
			if(user == null) {
				logger.error("The specified user does not exist.");
				DCException ex = new DCException(Constants.ER216,req);
				throw ex;
			}

			//check first if the user has been temporary blocked
			String attemptedLoginsString = getFieldValueFromJsonText(user.getJson(), Constants.ATTEMPTED_LOGINS);
			Long attemptedLogins = null;
			if(attemptedLoginsString != null && StringUtils.isNumeric(attemptedLoginsString)) {
				attemptedLogins = Long.parseLong(attemptedLoginsString);
				//if so user has been already temporary blocked
				if(attemptedLogins >= 3) {
					//check if the lockTime parameter time has expired. 
					String lockTimeString = getFieldValueFromJsonText(user.getJson(), Constants.LOCK_TIME);
					Date lockTime = sdf.parse(lockTimeString);
					//If lock time has not expired throw an exception
					//otherwise continue execution
					if((new Date()).before(lockTime)) {
						logger.error("User temporary locked due to multiple failed login attempts. Try again later:"+getFieldValueFromJsonText(user.getJson(), Constants.LOCK_TIME));
						DCException ex = new DCException(Constants.ER215,req);
						throw ex;
					} else {
						//delete lockTime and attemptedLogins parameters from the record
						//because if the execution got to this point the lock time has already expired
						if(attemptedLoginsString != null) {
							ObjectNode json = (ObjectNode) om.readTree(user.getJson());
							json.remove(Constants.LOCK_TIME);
							json.remove(Constants.ATTEMPTED_LOGINS);
							user.setJson(om.writeValueAsString(json));
							gsc002Dao.save(user);
						}
					}
				}
			}

			//checks if the user registration is complete: status = 'verified', otherwise throw exception+
			String statusField = getFieldValueFromJsonText(user.getJson(),Constants.STATUS_FIELD);
			if(!Constants.VERIFIED.equalsIgnoreCase(statusField)) {
				DCException rpe = null;
				if(statusField.equalsIgnoreCase(Constants.TO_BE_VERIFIED)) {
					logger.error("The user that is trying to login has not completed his/her registration yet by clicking on the link of the mail that has been sent to him/her");
					rpe = new DCException(Constants.ER208, req);
				} else if(statusField.equalsIgnoreCase(Constants.LOCKED)) {
					logger.error("Access denied: this user profile has been locked.");
					rpe = new DCException(Constants.ER211, req);
				}
				throw rpe;
			}
			//checks if the password values correspond, otherwise throw exception
			String encryptedPassword = encryptPassword(getFieldValueFromJsonText(req, Constants.PASSWORD_FIELD),Constants.SHA1);
			if(!encryptedPassword.equalsIgnoreCase(getFieldValueFromJsonText(user.getJson(),Constants.PASSWORD_FIELD))) {
				logger.error("Access denied: incorrect password.");
				DCException rpe = new DCException(Constants.ER209, req);
				throw rpe;
			}
			
			
			//create response
			ObjectNode rootNode = JsonNodeFactory.instance.objectNode();

			//get the list of roles for the user
			ArrayNode rolesNodeList = JsonNodeFactory.instance.arrayNode();			
			List<Gsc003RoleEntity> roleEntityList = gsc003Dao.getRoles(getQueryText(user.getId()));
					
			//for each role get its functions
			for(Gsc003RoleEntity roleEntity : roleEntityList) {
				ObjectNode roleNode = JsonNodeFactory.instance.objectNode();
				roleNode.put(Constants.ROLE_ID_FIELD,roleEntity.getId());

				String queryText = "'" +Constants.ROLE_ID_FIELD+ "' = '" +roleEntity.getId() +"'";
				String query = createQuery(queryText, Constants.PERMISSION_TABLE_NAME, Constants.JSON_COLUMN_NAME, "select");
				List<Gsc005PermissionEntity> permissionList = gsc005Dao.loadByNativeQuery(query);
				
				ArrayNode functionsNodeList = JsonNodeFactory.instance.arrayNode();
				
				for(Gsc005PermissionEntity permission : permissionList) {
					ArrayNode functionsPerEntity = (ArrayNode) (om.readTree(permission.getJson()).path(Constants.FUNCTIONS_FIELD));
					for (int i=0;i<functionsPerEntity.size();i++) {
						ObjectNode functionNode = (ObjectNode) functionsPerEntity.get(i);
						functionNode.get(Constants.FUNC_ID_FIELD).asLong();
						
						ObjectNode info = JsonNodeFactory.instance.objectNode();
						JsonNode layerId = functionNode.get(Constants.LAYER_ID_FIELD);
						if(layerId != null) {
							info.put(Constants.LAYER_ID_FIELD,functionNode.get(Constants.LAYER_ID_FIELD).asText());
						}
						info.put(Constants.FUNC_ID_FIELD,functionNode.get(Constants.FUNC_ID_FIELD).asText());
						
						functionsNodeList.add(info);
					}
				}
				
				roleNode.put(Constants.FUNCTIONS_FIELD, functionsNodeList);
				rolesNodeList.add(roleNode);
			}
			rootNode.put(Constants.USER_ID_FIELD, user.getId());
			rootNode.put(Constants.USERNAME_FIELD, getFieldValueFromJsonText(user.getJson(),Constants.USERNAME_FIELD));
			
			//if the user is not an admin (admins have no organizations)
			//get the organization list for the user
			if(isParameterInJson(user.getJson(), Constants.ORGANIZATIONS_FIELD)) {
				ArrayNode organizations = (ArrayNode) om.readTree(user.getJson()).path(Constants.ORGANIZATIONS_FIELD);
				rootNode.put(Constants.ORGANIZATIONS_FIELD,organizations);
				rootNode.put(Constants.ROLES_FIELD,rolesNodeList);
			}
			
			//before ending the method delete lockTime and attemptedLogins parameters from the record
			//because if the execution got to this point the lock time has already expired
			if(attemptedLoginsString != null) {
				ObjectNode json = (ObjectNode) om.readTree(user.getJson());
				json.remove(Constants.LOCK_TIME);
				json.remove(Constants.ATTEMPTED_LOGINS);
				user.setJson(om.writeValueAsString(json));
				gsc002Dao.save(user);
			}
			
			logger.info("User succesfully logged");
			logger.info(req);
			
			return om.writeValueAsString(rootNode);
			//return createJsonStatus(Constants.STATUS_DONE, Constants.LOGIN_SUCCESFUL, user.getId(), req);
		} catch(Exception e) {
			setFailedLoginCount(user);
			
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
	 * This method allows users to register themself into the system.
	 * Users will be administrators if no organization is specified in their request, otherwise ordinary users.
	 * User data will be saved, but the registration status will not be completed yet.
	 * A mail will be sent to the provided email address. 
	 * By clicking on the link that will be in the mail the verify method will be called, and the registration will be completed.
	 * 
	 * 
	 * @param req
	 * @return
	 */
	public String registerUser(String req) {
		Gsc002UserEntity user = null;
		try {
			// preliminary checks on the request parameters
			preliminaryChecks(req, Constants.REGISTER_USER);
 
			Gsc002UserEntity userEntityByName = null;
			Gsc002UserEntity userEntityByMail = null;
			
			// check if there's another user already saved with the same name
			userEntityByName = getUserObject(req,true,false);
			//check if the specified email is already used
			userEntityByMail = getUserObject(req,false,true);
			
			//Admin users don't specify organizations
			boolean isSimpleUser = isParameterInJson(req, Constants.ORG_FIELD);
			if(isSimpleUser) {
				// check if the inserted organizations id exists in the organization
				// table. If not throws exception
				checkIdOrganizationsValid(req);
			}

			// if no results found -> add new record
			if (userEntityByName == null && userEntityByMail == null) {
				
				//Generates a new UUID
				UUID uuid = UUID.randomUUID();
				
				//The system will save user data and will wait for mail verification to be completed
				//it will add to the record a 'status' parameter which will be initially put as 'toBeVerified'.
				//After the verification is completed the status will change to 'verified'.
				//The system will also add the UUID parameter that will be checked in the verification process and later deleted.

				
				//if the password field and the confirm password field are different throw exception
				if(!getFieldValueFromJsonText(req, Constants.PASSWORD_FIELD).equalsIgnoreCase(getFieldValueFromJsonText(req, Constants.CONFIRM_PASSWORD_FIELD))) {
					logger.error("password and confirmpassword parameters do not match.");
					DCException rpe = new DCException(Constants.ER210, req);
					throw rpe;
				}
				//Save the new user
				user = new Gsc002UserEntity();
				Map<String,String> values = new HashMap<String,String>();
				values.put(Constants.STATUS_FIELD, Constants.TO_BE_VERIFIED);
				values.put(Constants.UUID, uuid.toString());
				//replace the plain text password with its SHA1 encrypted version
				values.put(Constants.PASSWORD_FIELD,encryptPassword(getFieldValueFromJsonText(req, Constants.PASSWORD_FIELD),Constants.SHA1));
				String userJson = addJsonFields(req, values);
				//remove the confirmpassword field
				userJson = removeJsonField(userJson, Constants.CONFIRM_PASSWORD_FIELD);
				user.setJson(userJson);
				user = gsc002Dao.save(user);
				
				//send verification mail with the link to the verifyEmail service (with the UUID parameter)
				//the user can complete his registration by clicking the given link
				String text = mailUtils.buildTextMessage(uuid,user.getId());
				
				mailUtils.sendMail(Constants.SUB_COMPLETE_REGISTRATION,text,getFieldValueFromJsonText(req, Constants.USER_EMAIL_FIELD));

				logger.info("User registration done. To complete the register process check your email and click on the given link.");
				logger.info(req);
				
				return createJsonStatus(Constants.STATUS_DONE, Constants.USER_REGISTERED, user.getId(), req);

				// otherwise an error message will be return
			} else {
				DCException rpe = null;
				if(userEntityByName != null && userEntityByMail != null) {
					logger.error("Username is not avaliable and specified email is already in use.");
					rpe = new DCException(Constants.ER203, req);
				} else if(userEntityByName != null) {
					logger.error("Username is not avaliable. Choose a different one.");
					rpe = new DCException(Constants.ER201, req);
				} else if(userEntityByMail != null) {
					logger.error("Email address is already in use.");
					rpe = new DCException(Constants.ER202, req);
				}
				throw rpe;
			}
		} catch(Exception e) {
			//deletes the newly created record if the mail service didn't send.
			undoCreation(user);	

			if(e instanceof DCException) {
				return ((DCException) e).returnErrorString();	
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
	 * This method allows users who forgot their password to get a new one.
	 * The new password will be mailed to the provided email address.
	 * 
	 * @param req
	 * @return
	 */
	public String remindPassword(String req) {
		Gsc002UserEntity user = null;
		try {
			// preliminary checks on the request parameters
			preliminaryChecks(req, Constants.REMIND_PASSWORD);
			
			
			String username = getFieldValueFromJsonText(req, Constants.USERNAME_FIELD);
			String email = getFieldValueFromJsonText(req, Constants.USER_EMAIL_FIELD);
			
			// If both username and email are specified throw exception
			if(username != null && email != null) {
				logger.error("Bad request: cannot specify both username and email parameters.");
				DCException rpe = new DCException(Constants.ER212, req);
				throw rpe;
			//if username is specified, look for the corresponding record and get its email address
			} else if(username != null) {
				user = getUserObject(req,true,false);
				if(user == null) {
					logger.error("The specified user does not exist.");
					DCException ex = new DCException(Constants.ER216,req);
					throw ex;
				}
				email = getFieldValueFromJsonText(user.getJson(),Constants.USER_EMAIL_FIELD);
			
			//if the email is not null get the user record corresponding to the specified mail
			} else if(email != null) {
				user = getUserObject(req,false,true);
				if(user == null) {
					logger.error("No user is associated to the specified mail.");
					DCException ex = new DCException(Constants.ER214,req);
					throw ex;
				}
			//if none of the two parameters were specified throw exception
			} else {
				logger.error("Bad request: it is mandatory to specify one among username and email parameters.");
				DCException rpe = new DCException(Constants.ER213, req);
				throw rpe;
			}
			
			//generate random new password
			String newPassword = RandomStringUtils.randomAlphanumeric(10);
			//Send mail containing the new password
			String text = "Your new password is:"+newPassword;
			mailUtils.sendMail(Constants.SUB_CHANGE_PASSWORD,text,email);
			//update the record with the new generated password (after encryption)
			Map<String,String> values = new HashMap<String,String>();
			values.put(Constants.PASSWORD_FIELD,encryptPassword(newPassword,Constants.SHA1));
			String userJson = addJsonFields(user.getJson(), values);
			user.setJson(userJson);
			user = gsc002Dao.save(user);
			
			logger.info("new user password succesfully sent");
			logger.info(req);
			
			return createJsonStatus(Constants.STATUS_DONE, Constants.RETRIEVE_PASSWORD_MAIL_SENT, user.getId(), req);
			
		} catch(Exception e) {
			if(e instanceof DCException) {
				return ((DCException) e).returnErrorString();	
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
	 * Allows users to change their password.
	 * 
	 * @param req
	 * @return
	 */
	public String changePassword(String req) {
		try {
			// preliminary checks on the request parameters
			preliminaryChecks(req, Constants.CHANGE_PASSWORD);
			
			//Check if the username exists
			Gsc002UserEntity user = getUserObject(req,true,false);
			if(user == null) {
				logger.error("The specified user does not exist.");
				DCException ex = new DCException(Constants.ER216,req);
				throw ex;
			}
			
			//check if new password and confirm new password are the same
			if(!getFieldValueFromJsonText(req, Constants.NEW_PASSWORD_FIELD).equalsIgnoreCase(getFieldValueFromJsonText(req, Constants.CONFIRM_NEW_PASSWORD_FIELD))) {
				logger.error("new password and confirm new password parameters do not match.");
				DCException rpe = new DCException(Constants.ER210, req);
				throw rpe;
			}
			
			//check oldPassword is correct
			String oldPasswordRetrieved = getFieldValueFromJsonText(user.getJson(), Constants.PASSWORD_FIELD);
			String oldPasswordParameter = encryptPassword(getFieldValueFromJsonText(req, Constants.OLD_PASSWORD_FIELD), Constants.SHA1);
			if(!oldPasswordRetrieved.equalsIgnoreCase(oldPasswordParameter)) {
				logger.error("Access denied: incorrect password.");
				DCException rpe = new DCException(Constants.ER209, req);
				throw rpe;
			}
			
			//UpdateRecord change old password to new encrypted one
			ObjectNode jsonNode = (ObjectNode) om.readTree(user.getJson());
			String encryptedNewPassword = encryptPassword(getFieldValueFromJsonText(req, Constants.NEW_PASSWORD_FIELD),Constants.SHA1);
			jsonNode.put(Constants.PASSWORD_FIELD,encryptedNewPassword);
			user.setJson(om.writeValueAsString(jsonNode));
			gsc002Dao.save(user);
			
			logger.info("User password succesfully changed");
			logger.info(req);
			
			return createJsonStatus(Constants.STATUS_DONE, Constants.PASSWORD_SUCCESFULLY_CHANGED, user.getId(), req);
			
		} catch(Exception e) {
			if(e instanceof DCException) {
				return ((DCException) e).returnErrorString();	
			} else {
				logger.error("register user service error", e);
				DCException rpe = new DCException(Constants.ER01, req);
				logger.error("register user service: unhandled error " + rpe.returnErrorString());
				return rpe.returnErrorString();
			}
		}		
	}
	
	/**
	 * This method allows users to update his/her profile information. All previous content will be replaced
	 * by the new one exception made for system information (status fields) and password which has its own modify method.
	 * 
	 * @param req
	 * @return
	 */
	public String updateUser(String req) {
		try {
			// preliminary checks on the request parameters
			preliminaryChecks(req, Constants.UPDATE_USER);
			
			//Check if the username exists
			Gsc002UserEntity user = getUserObject(req,true,false);
			if(user == null) {
				logger.error("The specified user does not exist.");
				DCException ex = new DCException(Constants.ER216,req);
				throw ex;
			}

			//Check if all the specified organizations exist
			ArrayNode orgs = (ArrayNode) om.readTree(req).path(Constants.ORGANIZATIONS_FIELD);
			for(JsonNode org : orgs) {
				checkIdOrganizationValid(om.writeValueAsString(org));
			}
			
			//Update profile
			//we assume that all the parameters given in the request are all the parameters we need.
			//so we put everything we find in the new record
			//adding password and status fields as found in the old one
			ObjectNode jsonNode = (ObjectNode) om.readTree(req);
			jsonNode.put(Constants.PASSWORD_FIELD,getFieldValueFromJsonText(user.getJson(), Constants.PASSWORD_FIELD));
			jsonNode.put(Constants.STATUS_FIELD,getFieldValueFromJsonText(user.getJson(), Constants.STATUS_FIELD));
			user.setJson(om.writeValueAsString(jsonNode));
			gsc002Dao.save(user);
			
			logger.info("User succesfully updated");
			logger.info(req);
			
			return createJsonStatus(Constants.STATUS_DONE, Constants.USER_PROFILE_UPDATED, user.getId(), req);
			
		} catch(Exception e) {
			if(e instanceof DCException) {
				return ((DCException) e).returnErrorString();	
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
	 * This method allows an administrator user to lock or unlock the user specified in the request.
	 * 
	 * @param req
	 * @return
	 */
	public String lockUser(String req) {
		try {
			// preliminary checks on the request parameters
			preliminaryChecks(req, Constants.LOCK_USER);
			
			//Check if the username exists
			Gsc002UserEntity user = getUserObject(req,true,false);
			if(user == null) {
				logger.error("The specified user does not exist.");
				DCException ex = new DCException(Constants.ER216,req);
				throw ex;
			}
			
			//Lock the user (set status = locked)
			ObjectNode jsonNode = (ObjectNode) om.readTree(user.getJson());
			String lockField = getFieldValueFromJsonText(req, Constants.LOCK_FIELD);
			if("true".equalsIgnoreCase(lockField)) {
				jsonNode.put(Constants.STATUS_FIELD,Constants.LOCKED);
			} else if("false".equalsIgnoreCase(lockField)) {
				jsonNode.put(Constants.STATUS_FIELD,Constants.VERIFIED);
			} else {
				logger.error("Lock can only be either true or false.");
				DCException ex = new DCException(Constants.ER204,req);
				throw ex;
			}
			user.setJson(om.writeValueAsString(jsonNode));
			gsc002Dao.save(user);
			
			String statusDescription = lockField.equalsIgnoreCase("true") ? Constants.USER_LOCKED : Constants.USER_UNLOCKED;
			
			logger.info("User succesfully locked/unlocked");
			logger.info(req);
			
			return createJsonStatus(Constants.STATUS_DONE, statusDescription, user.getId(), req);
			
		} catch(Exception e) {
			if(e instanceof DCException) {
				return ((DCException) e).returnErrorString();	
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
	 * This methd allows any user to unregister himself from the system. If so the user must provide his own password 
	 * in the request. Otherwise it can allow an administrator user to delete any other user. The administrator user
	 * will not provide his credentials.
	 * 
	 * @param req
	 * @return
	 */
	public String unregisterUser(String req) {
		try {
			// preliminary checks on the request parameters
			preliminaryChecks(req, Constants.UNREGISTER_USER);
			
			//Check if the username exists
			Gsc002UserEntity user = getUserObject(req,true,false);
			if(user == null) {
				logger.error("The specified user does not exist.");
				DCException ex = new DCException(Constants.ER216,req);
				throw ex;
			}
			
			//if the user is trying to unregister he will have to provide his own password
			//if so check if the password corresponds to the retrieved user pass
			//if the password parameter is not provided it means that the admin user is deleting the profile. If so no checks are performed.
			String providedPassword = getFieldValueFromJsonText(req, Constants.PASSWORD_FIELD);
			if(providedPassword != null) {
				String retrievedPassword = getFieldValueFromJsonText(user.getJson(), Constants.PASSWORD_FIELD);
				if(!encryptPassword(providedPassword, Constants.SHA1).equalsIgnoreCase(retrievedPassword)) {
					logger.error("Access denied: incorrect password.");
					DCException ex = new DCException(Constants.ER209,req);
					throw ex;
				}
			}
			
			//delete the record
			DeleteService deleteService = new DeleteService();
			deleteService.deleteUser(user.getId());
			
			//how to check if it's the user unregistering or another user (admin) deleting--> check the password parameter
			String statusDescription = providedPassword == null ? Constants.USER_DELETED : Constants.USER_UNREGISTERED;
			
			logger.info("User succesfully deleted/unregistered");
			logger.info(req);
			
			return createJsonStatus(Constants.STATUS_DONE, statusDescription, user.getId(), req);
		} catch(Exception e) {
			if(e instanceof DCException) {
				return ((DCException) e).returnErrorString();	
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
	 * Iterates on the organization list specified in the request.
	 * For each of those throws an exception if it does not exist.
	 * 
	 * @param req
	 * @throws JsonProcessingException
	 * @throws IOException
	 * @throws NumberFormatException
	 * @throws DCException
	 */
	private void checkIdOrganizationsValid(String req) throws JsonProcessingException, IOException, NumberFormatException, DCException {
		ArrayNode orgs = (ArrayNode) om.readTree(req).path(Constants.ORGANIZATIONS_FIELD);
		for(JsonNode org : orgs) {
			checkIdOrganizationValid(om.writeValueAsString(org),req);
		}
	}
	
	/**
	 * Retrieves the user given its email and/or name.
	 * 
	 * @param json
	 * @param byName
	 * @param byMail
	 * @return
	 * @throws DCException
	 */
	private Gsc002UserEntity getUserObject(String json,boolean byName,boolean byMail) throws DCException {
		ArrayList<String> params = new ArrayList<String>();
		if(byMail) {
			params.add(Constants.USER_EMAIL_FIELD);
		}
		if(byName) {
			params.add(Constants.USER_NAME_FIELD);
		}
		return (Gsc002UserEntity) getRowObject(json, Constants.USER_TABLE_NAME, params,
				gsc002Dao);
	}

	/**
	 * Retrieves the user given its Id.
	 * 
	 * @param json
	 * @return
	 * @throws RPException
	 */
	private Gsc002UserEntity getUserObjectById(Long id) throws DCException {
		return (Gsc002UserEntity) gsc002Dao.load(id);
	}
	
	/**
	 * If the user was created but an error occurred in the mail service delete the record
	 * 
	 * @param user
	 */
	private void undoCreation(Gsc002UserEntity user) {
		//if the user was created but an error occurred in the mail service delete the record
		if(user != null && user.getId() != null) {
			gsc002Dao.delete(user.getId());
		}
	}
	
	private String encryptPassword(String plainPassword,String algorthm) throws DCException {
		try {
			byte[] hash = MessageDigest.getInstance(algorthm).digest(plainPassword.getBytes());
		        
	        StringBuilder builder = new StringBuilder();
	        for (int i = 0; i < hash.length; i++) {
	            builder.append(Integer.toString((hash[i] & 0xff) + 0x100, 16).substring(1));
	        }
	        return builder.toString();
		} catch(Exception e) {
			logger.error("Error in the encrypt password process.", e);
            throw new DCException(Constants.ER19);
		}   
	}

	
	private Date addMinutesToDate(Date date,int minutes) {	
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MINUTE, minutes);
		return cal.getTime();
	}
	
	/**
	 * This method will update the user record at the end of a failed login process.
	 * It will increase the attempted login counts, and if this is greater than 3 will set 
	 * a temporary 30 mins lock for the user.
	 * 
	 * @param user
	 */
	private void setFailedLoginCount(Gsc002UserEntity user) {
		try {
			if(user != null) {
				String attemptedLogins = getFieldValueFromJsonText(user.getJson(),Constants.ATTEMPTED_LOGINS);
				Long newAttemptedLogins = null;
				Date lockTime = null;
				if(attemptedLogins != null) {
					newAttemptedLogins = Long.parseLong(attemptedLogins)+1;
				} else {
					newAttemptedLogins = 1L;
				}
				
				//if this parameter is == 3 set a lockTime parameter as the current time plus 30 minutes
				if(newAttemptedLogins == 3) {
					lockTime = addMinutesToDate(new Date(), 30);
				}
				
				Map<String,String> values = new HashMap<String,String>();
				values.put(Constants.ATTEMPTED_LOGINS,newAttemptedLogins.toString());
				if(lockTime != null) {
					values.put(Constants.LOCK_TIME, sdf.format(lockTime));
				}
				String userJson = addJsonFields(user.getJson(),values);
				//update the record
				user.setJson(userJson);
				gsc002Dao.save(user);
			}
		} catch(DCException e) {
			//no need to throw another exception here since we re already on the catch block of the login method
			logger.error("Unable to update the failed login count...");
		}
	}
	
	/**
	 * Creates queries strings such as
	 * 
	 * "Select * from gsc_003_role r where r.id IN (select id from gsc_003_role r, jsonb_array_elements(r.json->'users') obj where obj->>'iduser' = '1');
	 * 
	 * @param id
	 * @return
	 */
	private String getQueryText(Long id) {
		StringBuilder sb = new StringBuilder();
		sb.append("Select * from ");
		sb.append(Constants.ROLE_TABLE_NAME);
		sb.append(" r where r.id IN (select id from ");
		sb.append(Constants.ROLE_TABLE_NAME);
		sb.append(", jsonb_array_elements(r.json->'users') obj where obj->>'iduser' = '");
		sb.append(id);
		sb.append("')");
		
		return sb.toString();
	}
}
