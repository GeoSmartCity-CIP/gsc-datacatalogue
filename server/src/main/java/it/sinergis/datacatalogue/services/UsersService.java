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
import java.util.Properties;
import java.util.UUID;

import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
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

public class UsersService extends ServiceCommons {

	private static String RESPONSE_JSON_STATUS_DONE = "{Status:'Done',Description:'Operation ok'}";
	
	/** Logger. */
	private static Logger logger;

	/** Dao users. */
	private Gsc002UserPersistence gsc002Dao;
	
	/** Dao users. */
	private Gsc003RolePersistence gsc003Dao;
	
	SimpleDateFormat sdf;

	public UsersService() {
		super();
		logger = Logger.getLogger(this.getClass());
		gsc002Dao = PersistenceServiceProvider.getService(Gsc002UserPersistence.class);
		gsc003Dao = PersistenceServiceProvider.getService(Gsc003RolePersistence.class);
		sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	}
	
	public String verifyMail(String uuid,String id) {
		logger.debug("Verification service ...");
		logger.debug("uuid="+ uuid);
		logger.debug("id="+ id);
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
			
			String username = getFieldValueFromJsonText(req, Constants.USERNAME_FIELD);
			
			//checks if there is a row having the inserted username 
			user = getUserObjectByUsername(username);
			
			//if no results are found throw exception
			if(user == null) {
				logger.error("The specified user does not exist.");
				DCException ex = new DCException(Constants.ER13,req);
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
//					//add 30 mins to current date
//					Date thirtyMinsAhead = addMinutesToDate(new Date(), 30);
					//If lock time has not expired throw an exception
					//otherwise continue execution
					if((new Date()).before(lockTime)) {
						logger.error("User temporary locked due to multiple failed login attempts. Try again later:"+getFieldValueFromJsonText(user.getJson(), Constants.LOCK_TIME));
						DCException ex = new DCException(Constants.ER21,req);
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

			//checks if the user registration is complete: status = 'verified', otherwise throw exception
			if(!Constants.VERIFIED.equalsIgnoreCase(getFieldValueFromJsonText(user.getJson(),Constants.STATUS_FIELD))) {
				logger.error("The user that is trying to login has not completed his/her registration yet by clicking on the link of the mail that has been sent to him/her");
				DCException rpe = new DCException(Constants.ER208, req);
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
			
			//get the organization list for the user
			ArrayNode organizations = (ArrayNode) om.readTree(user.getJson()).path(Constants.ORGANIZATIONS_FIELD);

			//TODO
			//get the list of roles for the user
			ArrayNode rolesNodeList = JsonNodeFactory.instance.arrayNode();
			//FIXME temporary query see structure of roles first. Then build query in a separate method.
			List<Gsc003RoleEntity> roleEntityList = gsc003Dao.getRoles("Select * from gsc_003_role r where r.id IN (select id from gsc_003_role r, jsonb_array_elements(r.json->'users') obj where obj->>'iduser' = '1')");
			for(Gsc003RoleEntity roleEntity : roleEntityList) {
				ObjectNode roleNode = JsonNodeFactory.instance.objectNode();
				roleNode.put(Constants.ROLE_ID_FIELD,roleEntity.getId());
				//TODO
				//for each of those roles get all the functions
				ArrayNode functionsNodeList = JsonNodeFactory.instance.arrayNode();
				ObjectNode functions = JsonNodeFactory.instance.objectNode();
				//TODO
				//add functions to the roles list element
				roleNode.put(Constants.FUNCTIONS_FIELD, functions);
				rolesNodeList.add(roleNode);
			}
			rootNode.put(Constants.USER_ID_FIELD, user.getId());
			rootNode.put(Constants.USERNAME_FIELD, getFieldValueFromJsonText(user.getJson(),Constants.USERNAME_FIELD));
			rootNode.put(Constants.ORGANIZATIONS_FIELD,organizations);
			rootNode.put(Constants.ROLES_FIELD,rolesNodeList);
			
			//before ending the method delete lockTime and attemptedLogins parameters from the record
			//because if the execution got to this point the lock time has already expired
			if(attemptedLoginsString != null) {
				ObjectNode json = (ObjectNode) om.readTree(user.getJson());
				json.remove(Constants.LOCK_TIME);
				json.remove(Constants.ATTEMPTED_LOGINS);
				user.setJson(om.writeValueAsString(json));
				gsc002Dao.save(user);
			}
			
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
				String text = buildTextMessage(uuid,user.getId());
				sendMail(Constants.MAIL_SUBJECT,text,getFieldValueFromJsonText(req, Constants.USER_EMAIL_FIELD));

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

	public String remindPassword(String req) {
		return RESPONSE_JSON_STATUS_DONE;
	}
	
	public String changePassword(String req) {
		return RESPONSE_JSON_STATUS_DONE;
	}
	
	public String updateUser(String req) {
		return RESPONSE_JSON_STATUS_DONE;
	}
	
	public String lockUser(String req) {
		return RESPONSE_JSON_STATUS_DONE;
	}
	
	public String unregisterUser(String req) {
		return RESPONSE_JSON_STATUS_DONE;
	}

	
	/**
	 * Iterates on the organization list specified in the request.
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
			checkIdOrganizationValid(om.writeValueAsString(org));
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
	
	public void sendMail(String subject, String text, String address)
            throws DCException {
        try {  
            Multipart multipart = new MimeMultipart();
            MimeMessage msg = buildMimeMessage(multipart, subject, text, address);        
            msg.setContent(multipart);            
            Transport.send(msg);           
        }
        catch (DCException dce) {
            throw dce;
        } catch(javax.mail.SendFailedException e) {
        	if(e.getMessage().equalsIgnoreCase("Invalid addresses")) {
        		logger.error("Registration unsuccessful: the specified email address does not exist.", e);
                throw new DCException(Constants.ER17);
        	}
			logger.error("registration unsuccessful: error while sending verification mail.", e);
            throw new DCException(Constants.ER18);
		}
        catch (Exception e) {
            logger.error("Error sending mail", e);
            throw new DCException(Constants.ER01);
        }
    }
	
	private MimeMessage buildMimeMessage(Multipart multipart, String subject, String text, String address) throws DCException {
        
		try {

	        InternetAddress from = new InternetAddress(mailPropertyReader.getValue(Constants.SENDER_ADDRESS));
	        Properties prop = new Properties();
	        prop.put(Constants.MAIL_SMTP_HOST,mailPropertyReader.getValue(Constants.MAIL_SMTP_HOST));
	        prop.put(Constants.MAIL_SMTP_PORT,mailPropertyReader.getValue(Constants.MAIL_SMTP_PORT));
	        
	        Session mailSession = Session.getDefaultInstance(prop);
	        MimeMessage msg = new MimeMessage(mailSession);
	        
	        msg.setSubject(subject);
	        msg.setFrom(from);
	        msg.setSender(from);
	        msg.setSentDate(new Date());
	        
	        msg.addRecipient(RecipientType.TO, new InternetAddress(address));
	        
	        BodyPart bodyPartText = new MimeBodyPart();
	        
	        ((MimeBodyPart) bodyPartText).setText(text,"UTF-8", "html");
	        multipart.addBodyPart(bodyPartText);
	        
	        return msg;
		} catch (Exception e) {
			logger.error("Error building mime message", e);
            throw new DCException(Constants.ER01);
		}
    }
	
	private String buildTextMessage(UUID uuid,Long rowId) {
		StringBuilder sb = new StringBuilder();
		sb.append("'http://");
		sb.append(mailPropertyReader.getValue(Constants.HOST_NAME));
		sb.append(":");
		sb.append(mailPropertyReader.getValue(Constants.PORT_NUMBER));
		sb.append("/gsc-datacatalogue/datacatalogservlet?actionName=verifymail&uuid=");
		sb.append(uuid);
		sb.append("&id=");
		sb.append(rowId);
		
		String link = sb.toString();
		logger.debug("generated verification link = "+link);
		
		sb = new StringBuilder();
		
		sb.append("To complete registration click the following link: <a href=");
		sb.append(link);
		sb.append("'>Complete registration</a>");

		return sb.toString();
				
		//return "To complete registration click the following link: <a href='http://localhost:8080/gsc-datacatalogue/datacatalogservlet?actionName=verifymail&uuid="+uuid+"&id="+rowId+"'>Complete registration</a>";
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
	 * Retrieves the user given its Id.
	 * 
	 * @param json
	 * @return
	 * @throws RPException
	 */
	private Gsc002UserEntity getUserObjectByUsername(String username) throws DCException {
		String condition = "'"+Constants.USER_NAME_FIELD+"'='"+username+"'";
		String query = createQuery(condition, Constants.USER_TABLE_NAME, Constants.JSON_COLUMN_NAME,
				"select");
		return (Gsc002UserEntity) gsc002Dao.getUser(query);
	}
	
	/**
	 * 
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
}
