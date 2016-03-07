package it.sinergis.datacatalogue.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.sinergis.datacatalogue.bean.jpa.Gsc001OrganizationEntity;
import it.sinergis.datacatalogue.bean.jpa.Gsc010ApplicationEntity;
import it.sinergis.datacatalogue.common.Constants;
import it.sinergis.datacatalogue.exception.DCException;
import it.sinergis.datacatalogue.persistence.PersistenceServiceProvider;
import it.sinergis.datacatalogue.persistence.services.Gsc001OrganizationPersistence;
import it.sinergis.datacatalogue.persistence.services.Gsc010ApplicationPersistence;

public class ApplicationsService extends ServiceCommons {

	public static String RESPONSE_JSON_STATUS_DONE = "{Status:'Done', Description:'Ok'}";
	public static String RESPONSE_JSON_LIST_APPLICATION = "{applications:[{applicationname:'ClientWeb',layers:[{layername:'Layer1'},{layername:'Layer2'}],groups:[{groupname:'Group1'},{groupname:'Group2'}]},{applicationname:'Application 2',layers:[{layername:'Layer3'},{layername:'Layer4'}],groups:[{groupname:'Group3'},{groupname:'Group4'}]}]}";
	public static String RESPONSE_JSON_GET_CONFIGURATION = "{}";

	/** Logger. */
	private static Logger logger;

	/** Dao organization. */
	private Gsc001OrganizationPersistence gsc001Dao;
	
	/** Dao application. */
	private Gsc010ApplicationPersistence gsc010Dao;
	
	public ApplicationsService() {
		logger = Logger.getLogger(this.getClass());
		gsc001Dao = PersistenceServiceProvider.getService(Gsc001OrganizationPersistence.class);
		gsc010Dao = PersistenceServiceProvider.getService(Gsc010ApplicationPersistence.class);
	}

	public String createApplication(String req) {
		try {
			checkJsonWellFormed(req);
			checkMandatoryParameters(Constants.CREATE_APP, req);
			logger.info(req);

			// check if there's another application already saved with the same
			// name
			Gsc010ApplicationEntity appEntity = getApplicationObjectFromName(req);

			// if no results found -> add new record
			if (appEntity == null) {
				// check if datasource with the given id exists
				String idOrganization = getFieldValueFromJsonText(req, Constants.ORG_FIELD);

				if (StringUtils.isNumeric(idOrganization)) {
					Gsc001OrganizationEntity orgEntity = gsc001Dao.load(Long.parseLong(idOrganization));

					if (orgEntity != null) {
						Gsc010ApplicationEntity app = new Gsc010ApplicationEntity();

						app.setJson(req);

						// request exists

						app = gsc010Dao.save(app);

						logger.info("Application succesfully created");
						return createJsonStatus(Constants.STATUS_DONE, Constants.APPLICATION_CREATED, app.getId(), req);
					} else {
						// Application doesn't exist
						throw new DCException(Constants.ER1002, req);
					}
				} else {
					// Organization id has to be numeric
					throw new DCException(Constants.ER1001, req);
				}

			} else {
				// application with the same name already exists
				throw new DCException(Constants.ER1000, req);
			}

		} catch (DCException rpe) {
			return rpe.returnErrorString();
		} catch (Exception e) {
			logger.error("create application service error", e);
			DCException rpe = new DCException(Constants.ER01, req);
			logger.error("createApplication service: unhandled error " + rpe.returnErrorString());

			return rpe.returnErrorString();
		}
	}

	private Gsc010ApplicationEntity getApplicationObjectFromName(String req) throws DCException {
		String applicationName = getFieldValueFromJsonText(req, Constants.APP_NAME_FIELD);

		try {
			StringBuilder builderQuery = new StringBuilder();
			builderQuery.append("'");
			builderQuery.append(Constants.APP_NAME_FIELD);
			builderQuery.append("' = '");
			builderQuery.append(applicationName);
			builderQuery.append("'");

			String query = createQuery(builderQuery.toString(), Constants.APPLICATION_TABLE_NAME,
					Constants.JSON_COLUMN_NAME, "select");

			logger.debug("Executing query: " + query);
			List<Gsc010ApplicationEntity> entityList = gsc010Dao.getApplications(query);

			if (entityList.size() == 1) {
				return entityList.get(0);
			} else if (entityList.size() == 0) {
				return null;
			} else {
				logger.error("One result expected from query, results found: " + entityList.size());
				throw new DCException(Constants.ER01);
			}
		} catch (Exception e) {
			logger.error("Generic exception occurred ", e);
			throw new DCException(Constants.ER01);
		}
	}

	public String assignToApplication(String req) {
		return RESPONSE_JSON_STATUS_DONE;
	}

	public String deleteApplication(String req) {
		return RESPONSE_JSON_STATUS_DONE;
	}

	public String listApplication(String req) {
		try {
			checkJsonWellFormed(req);
			checkMandatoryParameters(Constants.LIST_APP, req);
			logger.info(req);

			List<Gsc010ApplicationEntity> appEntities = null;
			// Retrieving input parameters
			String idApplication = getFieldValueFromJsonText(req, Constants.APPLICATION_ID);
			String applicationName = getFieldValueFromJsonText(req, Constants.APP_NAME_FIELD);
			String organization = getFieldValueFromJsonText(req, Constants.ORGANIZATION_FIELD);

			// idApplication has priority in the research
			if (StringUtils.isNotEmpty(idApplication)) {
				appEntities = new ArrayList<Gsc010ApplicationEntity>();

				if (StringUtils.isNumeric(idApplication)) {
					Gsc010ApplicationEntity entityFound = gsc010Dao.load(Long.parseLong(idApplication));
					if (entityFound == null) {
						// No dataset found with given parameters.
						throw new DCException(Constants.ER1004, req);
					}
					appEntities.add(entityFound);
				} else {
					// Application id has to be numeric
					throw new DCException(Constants.ER1003, req);
				}
			} else {
				// We build the query appending parameters
				StringBuilder builderQuery = new StringBuilder();

				if (StringUtils.isNotEmpty(organization)) {

					if (StringUtils.isNumeric(organization)) {
						builderQuery.append("'");
						builderQuery.append(Constants.ORGANIZATION_FIELD);
						builderQuery.append("' = '");
						builderQuery.append(organization);
						builderQuery.append("'");
					} else {
						// Organization id has to be numeric
						throw new DCException(Constants.ER1001, req);
					}
				}

				if (StringUtils.isNotEmpty(organization) && StringUtils.isNotEmpty(applicationName)) {
					builderQuery.append(" AND ");
				}

				if (StringUtils.isNotEmpty(applicationName)) {
					builderQuery.append("'");
					builderQuery.append(Constants.APP_NAME_FIELD);
					builderQuery.append("' LIKE '%");
					builderQuery.append(applicationName);
					builderQuery.append("%'");
				}

				// if the builder produced something not empty
				if (StringUtils.isNotEmpty(builderQuery.toString())) {

					String query = createQuery(builderQuery.toString(), Constants.APPLICATION_TABLE_NAME,
							Constants.JSON_COLUMN_NAME, "select");
					appEntities = gsc010Dao.getApplications(query);
				}
				// else we select all datasets
				else {

					appEntities = gsc010Dao.loadAll();
				}
			}

			logger.info("Applications found: " + appEntities.size());

			Map<String, List<Map<String, Object>>> appsResult = new HashMap<String, List<Map<String, Object>>>();

			List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();

			for (Gsc010ApplicationEntity entity : appEntities) {

				Map<String, Object> appMap = new HashMap<String, Object>();
				// ID Application
				appMap.put(Constants.APPLICATION_ID, entity.getId().toString());

				// Application name
				String appNameField = getFieldValueFromJsonText(entity.getJson(), Constants.APP_NAME_FIELD);
				appMap.put(Constants.APP_NAME_FIELD, appNameField);

				//TODO layer and groups

				resultList.add(appMap);
			}

			appsResult.put(Constants.APPLICATION_RESULT, resultList);

			String jsonString;
			try {
				ObjectMapper mapper = new ObjectMapper();
				jsonString = mapper.writeValueAsString(appsResult);
			} catch (IOException e) {
				logger.error("IOException", e);
				throw new DCException(Constants.ER01, req);
			}

			return jsonString;

		} catch (DCException rpe) {
			return rpe.returnErrorString();
		} catch (Exception e) {
			logger.error("list dataset service error", e);
			DCException rpe = new DCException(Constants.ER01, req);
			logger.error("listDataset service: unhandled error " + rpe.returnErrorString());

			return rpe.returnErrorString();
		}
	}

	public String publishToGeoserver(String req) {
		return RESPONSE_JSON_STATUS_DONE;
	}

	public String getConfiguration(String req) {
		return RESPONSE_JSON_GET_CONFIGURATION;
	}
}
