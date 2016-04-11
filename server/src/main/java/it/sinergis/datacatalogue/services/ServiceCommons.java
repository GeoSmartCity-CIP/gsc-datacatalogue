package it.sinergis.datacatalogue.services;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import it.sinergis.datacatalogue.bean.jpa.Gsc001OrganizationEntity;
import it.sinergis.datacatalogue.common.Constants;
import it.sinergis.datacatalogue.common.PropertyReader;
import it.sinergis.datacatalogue.exception.DCException;
import it.sinergis.datacatalogue.persistence.PersistenceServiceProvider;
import it.sinergis.datacatalogue.persistence.services.GenericPersistence;
import it.sinergis.datacatalogue.persistence.services.Gsc001OrganizationPersistence;
import it.sinergis.datacatalogue.persistence.services.util.MailUtils;

public class ServiceCommons {

	/** Logger. */
	private static Logger logger;

	/** Jackson object mapper. */
	protected ObjectMapper om;
	
	/** MailUtils. */
	protected MailUtils mailUtils;

	public ServiceCommons() {
		logger = Logger.getLogger(this.getClass());
		om = new ObjectMapper();
		mailUtils = new MailUtils();		
	}

	protected void checkJsonWellFormed(String jsonText) throws DCException {
		if (!isAllKeyLowercase(jsonText)) {
			logger.error("Error: Json keys are not all lowercase");
			throw new DCException(Constants.ER11);
		}
		try {
			om.readTree(jsonText);
		} catch (JsonProcessingException e) {
			logger.error("Error: Json string is not well formed", e);
			throw new DCException(Constants.ER07);
		} catch (Exception e) {
			logger.error("Error checking if json is valid", e);
			DCException rpe = new DCException(Constants.ER01);
			logger.error("Datacatalog service: unhandled error " + rpe.returnErrorString());
			throw rpe;
		}
	}

	/**
	 * Creates the actual research query from the semplified input string given
	 * by the user.
	 * 
	 * @param text
	 * @return
	 */
	protected String createQuery(String text, String tableName, String columnName, String mode) {
		String query = "";
		if ("delete".equalsIgnoreCase(mode)) {
			query += "delete from ";
		} else if ("select".equalsIgnoreCase(mode)) {
			query += "select * from ";
		}
		query += tableName + " where ";

		try {
			String[] pieces = text.split("AND|OR");
			for (int i = 0; i < pieces.length; i++) {
				int lastPieceElement = pieces[i].lastIndexOf("/");
				int firstBracketIndex = pieces[i].indexOf("(");

				String oldPiece = pieces[i];

				if (lastPieceElement != -1) {
					pieces[i] = pieces[i].substring(0, lastPieceElement).trim() + "->>"
							+ pieces[i].substring(lastPieceElement + 1).trim() + " ";
					if (firstBracketIndex != -1) {
						pieces[i] = " " + pieces[i].substring(0, firstBracketIndex + 1).trim() + " " + columnName + "->"
								+ pieces[i].substring(firstBracketIndex + 1).trim() + " ";
					} else {
						pieces[i] = " " + columnName + "->" + pieces[i].trim() + " ";
					}
				} else {
					if (firstBracketIndex != -1) {
						pieces[i] = " " + pieces[i].substring(0, firstBracketIndex + 1).trim() + " " + columnName
								+ "->>" + pieces[i].substring(firstBracketIndex + 1).trim() + " ";
					} else {
						pieces[i] = " " + columnName + "->>" + pieces[i].trim() + " ";
					}
				}
				pieces[i] = pieces[i].replace("/", "->");
				text = StringUtils.replace(text, oldPiece, pieces[i]);
			}
			query += text;
			logger.debug("transformed query:" + query);
			return query;
		} catch (Exception e) {
			logger.error("Error", e);
			logger.error(
					"Error in the research query: research queries must follow the following format: 'jsonNode'/'jsonChildNode'/.../'jsonRequestedNode' = 'requestedValue'");
			return null;
		}
	}

	/**
	 * JSON keys to lowercase. Useful to search key without a specific case of
	 * chars
	 * 
	 * @param text
	 * @return
	 */
	/*
	 * protected String keyToLowerCase(String json) { json =
	 * json.replaceAll("\\s",""); Matcher m =
	 * Pattern.compile("\"\\b\\w{1,}\\b\"\\s*:").matcher(json); StringBuilder
	 * sanitizedJSON = new StringBuilder(); int last = 0; while (m.find()) {
	 * sanitizedJSON.append(json.substring(last, m.start()));
	 * sanitizedJSON.append(m.group(0).toLowerCase()); last = m.end(); }
	 * sanitizedJSON.append(json.substring(last));
	 * 
	 * return sanitizedJSON.toString(); }
	 */

	/**
	 * Verifiy if all JSON keys are in lowercase
	 * 
	 * @param text
	 * @return boolean
	 */
	protected boolean isAllKeyLowercase(String json) {
		json = json.replaceAll("\\s", "");
		Matcher m = Pattern.compile("\"\\b\\w{1,}\\b\"\\s*:").matcher(json);
		while (m.find()) {
			if (!m.group(0).toLowerCase().equals(m.group(0))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns value of a specific field
	 * 
	 * @param json
	 * @param fieldName
	 * @return null if value is null, else the value
	 * @throws DPException
	 */
	protected String getFieldValueFromJsonText(String json, String fieldName) throws DCException {
		try {
			JsonNode rootNode = om.readTree(json);
			JsonNode value = rootNode.findValue(fieldName);

			if (value == null) {
				return null;
			} else {
				return value.asText();
			}
		} catch (Exception e) {
			logger.error("unhandled error: ", e);
			throw new DCException(Constants.ER01);
		}
	}

	/**
	 * Returns the specific object inside a json
	 * 
	 * @param json
	 * @param fieldName
	 * @return null if value is null, else the value
	 * @throws DPException
	 */
	protected String getObjectFromJsonText(String json, String fieldName) throws DCException {
		try {
			JsonNode rootNode = om.readTree(json);
			JsonNode value = rootNode.findValue(fieldName);

			if (value == null) {
				return null;
			} else {
				return value.toString();
			}
		} catch (Exception e) {
			logger.error("unhandled error: ", e);
			throw new DCException(Constants.ER01);
		}
	}

	/**
	 * Returns key within the input json text parameter.
	 * 
	 * @param json
	 * @return
	 * @throws RPException
	 */
	protected String getKeyFromJsonText(String json, String keyField) throws DCException {
		try {
			JsonNode rootNode = om.readTree(json);
			JsonNode key = rootNode.findValue(keyField);
			if (key == null) {
				logger.error(keyField + " parameter is mandatory within the json string.");
				throw new DCException(Constants.ER04);
			}

			// delete quote from value else where clausole doesn't work
			return key.toString().replace("\"", "");
		} catch (DCException rpe) {
			throw rpe;
		} catch (Exception e) {
			logger.error("unhandled error: ", e);
			throw new DCException(Constants.ER01);
		}
	}
	
	/**
	 * Returns true if the key field exists in the json string.
	 * 
	 * @param json
	 * @param keyField
	 * @return
	 * @throws DCException
	 */
	protected boolean isParameterInJson(String json, String keyField) throws DCException {
		try {
			JsonNode rootNode = om.readTree(json);
			JsonNode key = rootNode.findValue(keyField);
			if (key == null) {
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.error("unhandled error: ", e);
			throw new DCException(Constants.ER01);
		}
	}

	/**
	 * Retrieves the row given params.
	 * 
	 * @param json
	 * @return
	 * @throws RPException
	 */
	protected Object getRowObject(String json, String tablename, List<String> params,
			GenericPersistence persistenceClass) throws DCException {

		try {
			if (params.size() > 0) {
				String queryText = "'" + params.get(0) + "' = '" + getKeyFromJsonText(json, params.get(0)) + "'";
				for (int i = 1; i < params.size(); i++) {
					queryText += " AND '" + params.get(i) + "' = '" + getKeyFromJsonText(json, params.get(i)) + "'";
				}

				String query = createQuery(queryText, tablename, Constants.JSON_COLUMN_NAME, "select");
				List<Object> roles = persistenceClass.loadByNativeQueryGenericObject(query);

				if (roles.isEmpty()) {
					return null;
				}
				// research query can only find 1 record at most
				return roles.get(0);
			} else {
				return null;
			}
		} catch (DCException rpe) {
			throw rpe;
		} catch (Exception e) {
			logger.error("unhandled error: ", e);
			throw new DCException(Constants.ER01);
		}
	}

	/**
	 * Create JSON status message.
	 * 
	 * @param status
	 * @param description
	 * @return String
	 */
	protected String createJsonStatus(String status, String descriptionCode, Long id, String request)
			throws DCException {
		PropertyReader pr = new PropertyReader("messages.properties");

		ObjectMapper mapper = new ObjectMapper();
		try {

			JsonNode reqNode = mapper.readTree(request);
			ObjectNode root = JsonNodeFactory.instance.objectNode();
			root.put(Constants.STATUS_FIELD, status);
			root.put(Constants.DESCRIPTION_FIELD, pr.getValue(descriptionCode));
			root.put(Constants.REQUEST, reqNode);
			if (id != null) {
				root.put(Constants.ID, id);
			}
			return mapper.writeValueAsString(root);

		} catch (IOException e) {
			logger.error("IOException during the construction of status response", e);
			throw new DCException(Constants.ER01, request);
		}
	}
	
	/**
	 * Create JSON status message.
	 * 
	 * @param status
	 * @param description
	 * @return String
	 * @throws DCException 
	 */
	protected String createJsonStatus(String status, String descriptionCode) throws DCException {
		PropertyReader pr = new PropertyReader("messages.properties");

		ObjectMapper mapper = new ObjectMapper();
		try {
			ObjectNode root = JsonNodeFactory.instance.objectNode();
			root.put(Constants.STATUS_FIELD, status);
			root.put(Constants.DESCRIPTION_FIELD, pr.getValue(descriptionCode));
			return mapper.writeValueAsString(root);

		} catch (IOException e) {
			logger.error("IOException during the construction of status response", e);
			throw new DCException(Constants.ER01);
		}
	}

	/**
	 * Given a service and a jsonRequest, this method checks if the request has
	 * all the expected mandatory parameters.
	 * 
	 * @param service
	 * @param jsonRequest
	 * @return
	 * @throws DCException
	 * @throws IOException
	 * @throws JsonProcessingException
	 */
	protected void checkMandatoryParameters(String service, String jsonRequest)
			throws DCException, JsonProcessingException, IOException {
		PropertyReader pr = new PropertyReader("service_parameters.properties");

		String mandatoryParameters = pr.getValue(service);

		String[] mandatoryParametersArray = StringUtils.split(mandatoryParameters, ";");

		if (mandatoryParameters.length() > 0) {
			for (String parameter : mandatoryParametersArray) {

				// if we have a list we may want to check one or more mandatory
				// parameters within every object of the list
				if (parameter.contains("-")) {
					// - symbol splits the list name from the mandatory
					// parameters inside it.
					String[] listSplit = StringUtils.split(parameter, "-");

					checkParameter(jsonRequest, listSplit[0], mandatoryParameters, jsonRequest);

					// + symbol splits the parameter list if we have 2 or more
					// mandatory parameters
					String[] listParameterSplit = StringUtils.split(listSplit[1], "+");

					ObjectMapper mapper = new ObjectMapper();
					ObjectNode jsonObject = (ObjectNode) mapper.readTree(jsonRequest);
					ArrayNode listNode = (ArrayNode) jsonObject.findValue(listSplit[0]);
					// we allow the list to be empty []
					for (int i = 0; i < listNode.size(); i++) {
						JsonNode listElementNode = listNode.get(i);
						String listElementNodeJson = mapper.writeValueAsString(listElementNode);
						for (String listParameter : listParameterSplit) {
							checkParameter(listElementNodeJson, listParameter, mandatoryParameters, jsonRequest);
						}
					}
				} else {
					checkParameter(jsonRequest, parameter, mandatoryParameters, jsonRequest);
				}
			}
		}

	}

	private void checkParameter(String jsonRequest, String parameter, String mandatoryParameters,
			String jsonRequestPrint) throws DCException {
		String value = getFieldValueFromJsonText(jsonRequest, parameter);

		if (value == null) {

			logger.error("Mandatory parameter is missing. Expected parameters: " + mandatoryParameters);
			logger.error("parameter " + parameter + " not found in " + jsonRequest);
			throw new DCException(Constants.ER04, jsonRequestPrint);
		}
	}

	/**
	 * Preliminary checks on the request validity.
	 * 
	 * @param jsonRequest
	 * @param serviceName
	 * @throws DCException
	 * @throws IOException
	 * @throws JsonProcessingException
	 */
	protected void preliminaryChecks(String jsonRequest, String serviceName)
			throws DCException, JsonProcessingException, IOException {
		// checks if the json is syntatically correct.
		checkJsonWellFormed(jsonRequest);
		// checks if the request contains all the mandatory parameters
		checkMandatoryParameters(serviceName, jsonRequest);
	}

	/**
	 * Given a jsonRequest and a key, this method remove the key from the
	 * request.
	 * 
	 * @param json
	 * @param key
	 * @return
	 */
	protected String removeJsonField(String json, String key) throws DCException {
		try {
			JsonNode rootNode = om.readTree(json);
			JsonNode keyNode = rootNode.findValue(key);

			if (keyNode != null) {
				((ObjectNode) rootNode).remove(key);
			}
			return rootNode.toString();

		} catch (JsonProcessingException e) {
			logger.error("Error removing the key " + key + " from the given json");
			throw new DCException(Constants.ER01, json);
		} catch (IOException e) {
			throw new DCException(Constants.ER01, json);
		}
	}
	
	/**
	 * Given a jsonRequest and a map of keys and values adds all the given elements to the json.
	 * The parameters will be added at the top level of json.
	 * 
	 * @param json
	 * @param key
	 * @return
	 */
	protected String addJsonFields(String json, Map<String,String> values) throws DCException {
		try {
			ObjectNode rootNode = (ObjectNode) om.readTree(json);
			
			for(Map.Entry<String, String> entry : values.entrySet()) {
				rootNode.put(entry.getKey(),entry.getValue());
			}
			return om.writeValueAsString(rootNode);
		} catch (Exception e) {
			logger.error("Error while adding parameters to json.");
			throw new DCException(Constants.ER01, json);
		}
	}

	/**
	 * Checks if the given parameter for organization matches the id of any
	 * existing organization. 
	 * Returns error if the organization id is not found.
	 * 
	 * @param req
	 * @param fullRequest
	 * @throws NumberFormatException
	 * @throws DCException
	 */
	protected void checkIdOrganizationValid(String req) throws NumberFormatException, DCException {
		Long orgId = Long.parseLong(getKeyFromJsonText(req, Constants.ORG_FIELD));
		Gsc001OrganizationPersistence orgPersistence = PersistenceServiceProvider
				.getService(Gsc001OrganizationPersistence.class);
		Gsc001OrganizationEntity orgEntity = orgPersistence.load(orgId);
		if (orgEntity == null) {
			DCException rpe = new DCException(Constants.ER15, req);
			throw rpe;
		}
	}
	
	/**
	 * Used when the org id is within a list
	 * 
	 * @param req
	 * @param fullRequest
	 * @throws NumberFormatException
	 * @throws DCException
	 */
	protected void checkIdOrganizationValid(String req,String fullRequest) throws NumberFormatException, DCException {
		try {
			checkIdOrganizationValid(req);
		} catch(Exception e) {
			DCException rpe = new DCException(Constants.ER15, fullRequest);
			throw rpe;
		}
	}

	protected String loadObjectFromIdList(String tableName, List<String> ids) {
		StringBuilder sb = new StringBuilder();
		
		if (ids != null && !ids.isEmpty()) {
			sb.append("SELECT * FROM ");
			sb.append(tableName);
			sb.append(" WHERE ");
			sb.append(Constants.ID);
			sb.append(" IN ( ");
			for (int i = 0; i < ids.size(); i++) {
				String id = ids.get(i);
				sb.append("'");
				sb.append(id);
				sb.append("'");
				if(i != ids.size() - 1) {
					sb.append(",");
				}
			}
			sb.append(")");
		}
		return sb.toString();
	}
}
