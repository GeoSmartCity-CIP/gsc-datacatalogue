package it.sinergis.datacatalogue.services;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import it.sinergis.datacatalogue.bean.jpa.Gsc006DatasourceEntity;
import it.sinergis.datacatalogue.bean.jpa.Gsc007DatasetEntity;
import it.sinergis.datacatalogue.common.Constants;
import it.sinergis.datacatalogue.exception.DCException;
import it.sinergis.datacatalogue.persistence.PersistenceServiceProvider;
import it.sinergis.datacatalogue.persistence.services.Gsc006DatasourcePersistence;
import it.sinergis.datacatalogue.persistence.services.Gsc007DatasetPersistence;
import it.sinergis.datacatalogue.persistence.services.util.ServiceUtil;

public class DatasetsService extends ServiceCommons {

	public static String RESPONSE_JSON_STATUS_DONE = "{Status:'Done',Description:'Operation ok'}";
	public static String RESPONSE_JSON_LIST_DATASET = " {dataset:[{name:'Dataset1',realname:'dataset1',datasourcename:'Datasource1',description:'First dataset',tobeingested:'true',refreshinterval:'3'},"
			+ "{name:'Dataset2',realname:'Dataset2',datasourcename:'Datasource2',description:'Second dataset'}]}";
	public static String RESPONSE_JSON_LIST_COLS_METADATA = " {columns:[{name:'Col1',type:'number',alias:'Col 1',visibility:'true'},{name:'Col2',type:'char',alias:'Col 2',visibility:'false'}]}";

	/** Logger. */
	private static Logger logger;

	/** Dao datasource. */
	private Gsc006DatasourcePersistence gsc006Dao;

	/** Dao dataset. */
	private Gsc007DatasetPersistence gsc007Dao;

	/** Constructor. */
	public DatasetsService() {
		logger = Logger.getLogger(this.getClass());
		gsc007Dao = PersistenceServiceProvider.getService(Gsc007DatasetPersistence.class);
		gsc006Dao = PersistenceServiceProvider.getService(Gsc006DatasourcePersistence.class);
	}

	public String createDataset(String req) {
		try {
			checkJsonWellFormed(req);
			checkMandatoryParameters(Constants.CREATE_DATASET, req);
			logger.info(req);

			// check if there's another dataset already saved with the same
			// name
			Gsc007DatasetEntity datasetEntity = getDatasetObjectFromDatasetname(req);

			// if no results found -> add new record
			if (datasetEntity == null) {
				// check if datasource with the given id exists
				String idDatasource = getFieldValueFromJsonText(req, Constants.DATASOURCE_ID_FIELD);

				if (StringUtils.isNumeric(idDatasource)) {
					Gsc006DatasourceEntity datasourceEntity = gsc006Dao.load(Long.parseLong(idDatasource));

					if (datasourceEntity != null) {
						Gsc007DatasetEntity dset = new Gsc007DatasetEntity();
						Gsc007DatasetEntity dsetUpdated = new Gsc007DatasetEntity();

						String datasourceType = getFieldValueFromJsonText(datasourceEntity.getJson(), Constants.TYPE);

						// If the datasource is a SHAPE
						if (datasourceType != null && datasourceType.equalsIgnoreCase(Constants.SHAPE)) {
							
							ObjectNode node = (ObjectNode) om.readTree(req);
							
							//TODO add code here
							String datasetFilename = getFieldValueFromJsonText(req, Constants.DSET_REALNAME_FIELD);
							String URL = getFieldValueFromJsonText(req, Constants.URL);
							//if the update is needed something special has to be specified as datasourcename
							if("auto_update".equalsIgnoreCase(datasetFilename)) {
								//get the datasourcename through the url (if no url specified error is thrown)
								if(URL == null || URL.isEmpty()) {
									logger.error("Since the dataset has to be retrieved from a remote location, the URL parameter needs to be specified in the request.");
									throw new DCException(Constants.ER706, req);
								}
								//create the datasetname through the given url
								int separatorIndex = URL.lastIndexOf("\\") != -1 ? URL.lastIndexOf("\\") +1 : URL.lastIndexOf("/") +1;
								datasetFilename = URL.substring(separatorIndex);
								node.put(Constants.DSET_REALNAME_FIELD,datasetFilename);
								
								//update the json
								dset.setJson(node.toString());
								//call the updateshapeservice update method
								UpdateShapeService updateShapeService = new UpdateShapeService();
								dsetUpdated = updateShapeService.updateFile(dset);
							}
							
							String datasourcePath = getFieldValueFromJsonText(datasourceEntity.getJson(),
									Constants.PATH);

							String datasetFilenameSHP = datasetFilename.substring(0, datasetFilename.lastIndexOf(".")).concat(".shp");
							if (StringUtils.isNotEmpty(datasourcePath) && StringUtils.isNotEmpty(datasetFilenameSHP)) {

								if (!datasourcePath.endsWith("/")) {
									datasourcePath = datasourcePath.concat("/");
								}
								String columnsJson = ServiceUtil
										.createJSONColumnsFromShapeFile(datasourcePath + datasetFilenameSHP);

								//add the last updated field
								DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
								String dateFormatted = formatter.format(System.currentTimeMillis());
								node.put(Constants.LAST_UPDATE_TS, dateFormatted);
								
								node.put(Constants.COLUMNS, om.readTree(columnsJson));

								if(dsetUpdated != null ) {
									dset = dsetUpdated;
								}
								
								dset.setJson(node.toString());

							} else {
								logger.error("Path parameter is mandatory for datasources that handle shape files.");
								throw new DCException(Constants.ER703, req);
							}
							
							gsc007Dao.save(dsetUpdated);
						}
						// if the datasource is POSTGIS
						else if (datasourceType != null && datasourceType.equalsIgnoreCase(Constants.POSTGIS)) {

							String urlDatasource = getFieldValueFromJsonText(datasourceEntity.getJson(), Constants.URL);
							String portDatasource = getFieldValueFromJsonText(datasourceEntity.getJson(),
									Constants.PORT);
							String schema = getFieldValueFromJsonText(datasourceEntity.getJson(),
									Constants.SCHEMA_FIELD);
							String username = getFieldValueFromJsonText(datasourceEntity.getJson(),
									Constants.USERNAME_FIELD);
							String password = getFieldValueFromJsonText(datasourceEntity.getJson(),
									Constants.PASSWORD_FIELD);
							String database = getFieldValueFromJsonText(datasourceEntity.getJson(), Constants.DATABASE);
							String datasetRealname = getFieldValueFromJsonText(req, Constants.DSET_REALNAME_FIELD);

							if (StringUtils.isNotEmpty(urlDatasource) && StringUtils.isNotEmpty(portDatasource)
									&& StringUtils.isNotEmpty(schema) && StringUtils.isNotEmpty(username)
									&& StringUtils.isNotEmpty(password) && StringUtils.isNotEmpty(datasetRealname)) {
								String columnsJson = ServiceUtil.createJSONColumnsFromPostGisDB(Constants.POSTGIS,
										urlDatasource, portDatasource, schema, database, username, password,
										datasetRealname);

								ObjectNode node = (ObjectNode) om.readTree(req);
								node.put(Constants.COLUMNS, om.readTree(columnsJson));

								dset.setJson(node.toString());
							} else {
								dset.setJson(req);
							}
						} else {
							dset.setJson(req);
						}

						// request exists

						dset = gsc007Dao.save(dset);

						logger.info("Dataset succesfully created");
						return createJsonStatus(Constants.STATUS_DONE, Constants.DATASETS_CREATED, dset.getId(), req);
					} else {
						// Datasource doesn't exist
						throw new DCException(Constants.ER705, req);
					}
				} else {
					// Datasource id has to be numeric
					throw new DCException(Constants.ER12, req);
				}

			} else {
				// dataset with the same name already exists
				throw new DCException(Constants.ER704, req);
			}

		} catch (DCException rpe) {
			return rpe.returnErrorString();
		} catch (Exception e) {
			logger.error("create dataset service error", e);
			DCException rpe = new DCException(Constants.ER01, req);
			logger.error("createDataset service: unhandled error " + rpe.returnErrorString());

			return rpe.returnErrorString();
		}
	}

	public String updateDataset(String req) {
		try {
			checkJsonWellFormed(req);
			checkMandatoryParameters(Constants.UPDATE_DATASET, req);
			logger.info(req);

			// Retrieving input parameters
			String idDataset = getFieldValueFromJsonText(req, Constants.DSET_ID_FIELD);

			Gsc007DatasetEntity entityFound = gsc007Dao.load(Long.parseLong(idDataset));
			if (entityFound == null) {
				throw new DCException(Constants.ER702, req);
			} else {

				// Before updating we have to be sure that dataset name
				// doesn't already exist in another record (not the one
				// we're updating)
				Gsc007DatasetEntity datasetEntity = getDatasetObjectFromDatasetname(req);

				// if no results found or the result is-> update record
				if (datasetEntity == null || (datasetEntity.getId().longValue() == entityFound.getId().longValue())) {

					// check that the datasource with the given id exists
					String idDatasource = getFieldValueFromJsonText(req, Constants.DATASOURCE_ID_FIELD);
					if (StringUtils.isNumeric(idDatasource)) {

						Gsc006DatasourceEntity datasourceEntity = gsc006Dao.load(Long.parseLong(idDatasource));

						if (datasourceEntity != null) {
							JsonNode node = om.readTree(req);
							((ObjectNode) node).remove(Constants.DSET_ID_FIELD);

							entityFound.setJson(node.toString());
							gsc007Dao.save(entityFound);

							logger.info("Dataset succesfully updated");
							return createJsonStatus(Constants.STATUS_DONE, Constants.DATASETS_UPDATED,
									entityFound.getId(), req);
						} else {
							// Datasource doesn't exist
							throw new DCException(Constants.ER705, req);
						}
					} else {
						// Datasource id has to be numeric
						throw new DCException(Constants.ER12, req);
					}
				} else {
					// Update failed, dataset with the same name already exists
					throw new DCException(Constants.ER704, req);
				}
			}

		} catch (DCException rpe) {
			return rpe.returnErrorString();
		} catch (Exception e) {
			logger.error("update dataset service error", e);
			DCException rpe = new DCException(Constants.ER01, req);
			logger.error("update service: unhandled error " + rpe.returnErrorString());

			return rpe.returnErrorString();
		}
	}

	public String deleteDataset(String req) {
		try {
			checkJsonWellFormed(req);
			checkMandatoryParameters(Constants.DELETE_DATASET, req);
			logger.info(req);

			// Retrieving input parameters
			String idDataset = getFieldValueFromJsonText(req, Constants.DSET_ID_FIELD);

			if (StringUtils.isNumeric(idDataset)) {
				DeleteService deleteService = new DeleteService();
				deleteService.deleteDataset(null, null, Long.parseLong(idDataset), null);

				logger.info("datasetr succesfully deleted");
				logger.info(req);
				return createJsonStatus(Constants.STATUS_DONE, Constants.DATASETS_DELETED, null, req);

			} else {
				// Dataset id has to be numeric
				throw new DCException(Constants.ER12, req);
			}

		} catch (DCException rpe) {
			return rpe.returnErrorString();
		} catch (Exception e) {
			logger.error("delete dataset service error", e);
			DCException rpe = new DCException(Constants.ER01, req);
			logger.error("delete service: unhandled error " + rpe.returnErrorString());

			return rpe.returnErrorString();
		}
	}

	public String listDataset(String req) {
		try {
			checkJsonWellFormed(req);
			checkMandatoryParameters(Constants.LIST_DATASET, req);
			logger.info(req);

			List<Gsc007DatasetEntity> dsets = null;
			// Retrieving input parameters
			String idDataset = getFieldValueFromJsonText(req, Constants.DSET_ID_FIELD);
			String datasetName = getFieldValueFromJsonText(req, Constants.DSET_NAME_FIELD);
			String idDatasource = getFieldValueFromJsonText(req, Constants.DATASOURCE_ID_FIELD);
			String idOrganization = getFieldValueFromJsonText(req, Constants.ORG_ID_FIELD);

			// Dataset id has to be numeric
			if (StringUtils.isNotEmpty(idDataset) && !StringUtils.isNumeric(idDataset)) {
				throw new DCException(Constants.ER12, req);
			}

			// Datasource id has to be numeric
			if (StringUtils.isNotEmpty(idDatasource) && !StringUtils.isNumeric(idDatasource)) {
				throw new DCException(Constants.ER12, req);
			}

			// Organization id has to be numeric
			if (StringUtils.isNotEmpty(idOrganization) && !StringUtils.isNumeric(idOrganization)) {
				throw new DCException(Constants.ER12, req);
			}

			// load dataset from its id if idDataset is the only parameter used
			// in this search
			if (StringUtils.isNotEmpty(idDataset) && StringUtils.isEmpty(datasetName)
					&& StringUtils.isEmpty(idDatasource) && StringUtils.isEmpty(idOrganization)) {
				dsets = new ArrayList<Gsc007DatasetEntity>();

				Gsc007DatasetEntity entityFound = gsc007Dao.load(Long.parseLong(idDataset));
				if (entityFound == null) {
					// No dataset found with given parameters.
					throw new DCException(Constants.ER702, req);
				}
				dsets.add(entityFound);

			} else if (StringUtils.isNotEmpty(idDataset) || StringUtils.isNotEmpty(datasetName)
					|| StringUtils.isNotEmpty(idDatasource) || StringUtils.isNotEmpty(idOrganization)) {

				String query = createSearchQuery(Constants.DATASETS_TABLE_NAME, Constants.DATASOURCE_TABLE_NAME,
						idDataset, datasetName, idDatasource, idOrganization);
				dsets = gsc007Dao.getDatasets(query);
			}
			// else we select all datasets
			else {

				dsets = gsc007Dao.loadAll();
			}

			logger.info("Datasets found: " + dsets.size());

			Map<String, List<Map<String, Object>>> dsetsResult = new HashMap<String, List<Map<String, Object>>>();

			List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();

			for (Gsc007DatasetEntity entity : dsets) {

				Map<String, Object> dsetsMap = new HashMap<String, Object>();
				// ID dataset
				dsetsMap.put(Constants.DSET_ID_FIELD, entity.getId().toString());

				// Dataset name
				String datasetNameField = getFieldValueFromJsonText(entity.getJson(), Constants.DSET_NAME_FIELD);
				dsetsMap.put(Constants.DSET_NAME_FIELD, datasetNameField);

				// Dataset real name
				String realName = getFieldValueFromJsonText(entity.getJson(), Constants.DSET_REALNAME_FIELD);
				dsetsMap.put(Constants.DSET_REALNAME_FIELD, realName);

				String idDataSource = getFieldValueFromJsonText(entity.getJson(), Constants.DATASOURCE_ID_FIELD);
				dsetsMap.put(Constants.DATASOURCE_ID_FIELD, idDataSource);

				Gsc006DatasourceEntity datasource = gsc006Dao.load(Long.parseLong(idDataSource));
				dsetsMap.put(Constants.DATASOURCE_NAME_FIELD,
						getFieldValueFromJsonText(datasource.getJson(), Constants.DATASOURCE_NAME_FIELD));

				// description
				String description = getFieldValueFromJsonText(entity.getJson(), Constants.DESCRIPTION_FIELD);
				dsetsMap.put(Constants.DESCRIPTION_FIELD, description);

				// tobeingested
				String tobeingested = getFieldValueFromJsonText(entity.getJson(), Constants.DSET_TOBEINGESTED_FIELD);
				dsetsMap.put(Constants.DSET_TOBEINGESTED_FIELD, tobeingested);

				// refreshinterval
				String refreshinterval = getFieldValueFromJsonText(entity.getJson(),
						Constants.DSET_REFRESHINTERVAL_FIELD);
				dsetsMap.put(Constants.DSET_REFRESHINTERVAL_FIELD, refreshinterval);

				resultList.add(dsetsMap);
			}

			dsetsResult.put(Constants.DSET_RESULT, resultList);

			String jsonString;
			try {
				ObjectMapper mapper = new ObjectMapper();
				jsonString = mapper.writeValueAsString(dsetsResult);
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

	public String listColumns(String req) {
		try {
			checkJsonWellFormed(req);
			checkMandatoryParameters(Constants.LIST_DATASET_COLUMNS, req);
			logger.info(req);

			// Retrieving input parameters
			String idDataset = getFieldValueFromJsonText(req, Constants.DSET_ID_FIELD);

			if (StringUtils.isNumeric(idDataset)) {
				Gsc007DatasetEntity entityFound = gsc007Dao.load(Long.parseLong(idDataset));
				if (entityFound == null) {
					// No dataset found with given parameters.
					throw new DCException(Constants.ER702, req);
				} else {

					Map<String, Object> mapColumns = new HashMap<>();
					String columns = getObjectFromJsonText(entityFound.getJson(), Constants.COLUMNS);

					if (columns != null) {
						mapColumns.put(Constants.COLUMNS, om.readTree(columns));
						return om.writeValueAsString(mapColumns);
					} else {
						// No columns found for dataset
						throw new DCException(Constants.ER708, req);
					}
				}
			} else {
				// Dataset id has to be numeric
				throw new DCException(Constants.ER12, req);
			}

		} catch (DCException rpe) {
			return rpe.returnErrorString();
		} catch (Exception e) {
			logger.error("list dataset service error", e);
			DCException rpe = new DCException(Constants.ER01, req);
			logger.error("listDataset service: unhandled error " + rpe.returnErrorString());

			return rpe.returnErrorString();
		}
	}

	public String updateColumnsMetadata(String req) {
		try {
			checkJsonWellFormed(req);
			checkMandatoryParameters(Constants.UPDATE_DATASET_COLUMNS, req);
			logger.info(req);

			// Retrieving input parameters
			String idDataset = getFieldValueFromJsonText(req, Constants.DSET_ID_FIELD);
			String columns = getObjectFromJsonText(req, Constants.COLUMNS);

			if (StringUtils.isNumeric(idDataset)) {
				Gsc007DatasetEntity entityFound = gsc007Dao.load(Long.parseLong(idDataset));
				if (entityFound == null) {
					// No dataset found with given parameters.
					throw new DCException(Constants.ER702, req);
				} else {
					// we don't check columns, we just replace the columns value
					// with the one sent by the client
					ObjectNode node = (ObjectNode) om.readTree(entityFound.getJson());
					node.remove(Constants.COLUMNS);
					node.put(Constants.COLUMNS, om.readTree(columns));

					entityFound.setJson(node.toString());
					gsc007Dao.save(entityFound);

					logger.info("Dataset columns succesfully updated");
					return createJsonStatus(Constants.STATUS_DONE, Constants.DATASETS_UPDATED, entityFound.getId(),
							req);

				}
			} else {
				// Dataset id has to be numeric
				throw new DCException(Constants.ER12, req);
			}

		} catch (DCException rpe) {
			return rpe.returnErrorString();
		} catch (Exception e) {
			logger.error("list dataset service error", e);
			DCException rpe = new DCException(Constants.ER01, req);
			logger.error("listDataset service: unhandled error " + rpe.returnErrorString());

			return rpe.returnErrorString();
		}
	}

	public String createCronService(String req) {
		return RESPONSE_JSON_STATUS_DONE;
	}

	/**
	 * Retrieves the dataset given its name.
	 * 
	 * @param json
	 * @return
	 * @throws RPException
	 */
	private Gsc007DatasetEntity getDatasetObjectFromDatasetname(String json) throws DCException {

		String datasetName = getFieldValueFromJsonText(json, Constants.DSET_NAME_FIELD);

		try {
			StringBuilder builderQuery = new StringBuilder();
			builderQuery.append("'");
			builderQuery.append(Constants.DSET_NAME_FIELD);
			builderQuery.append("' = '");
			builderQuery.append(datasetName);
			builderQuery.append("'");

			String query = createQuery(builderQuery.toString(), Constants.DATASETS_TABLE_NAME,
					Constants.JSON_COLUMN_NAME, "select");

			logger.debug("Executing query: " + query);
			List<Gsc007DatasetEntity> entityList = gsc007Dao.getDatasets(query);

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

	/**
	 *  Query example:
	 * SELECT * FROM gscdatacatalogue.gsc_007_dataset dataset
	 * WHERE dataset.id IN (
	 * SELECT
	 * gsc007.id
	 *	FROM 
	 *	gscdatacatalogue.gsc_007_dataset gsc007,
	 *	gscdatacatalogue.gsc_006_datasource gsc006
	 *	WHERE
	 *	gsc006.id = cast(gsc007.json->>'iddatasource' as integer) 
	 *	AND gsc007.id = '150'
	 *	AND gsc007.json->>'iddatasource' = '269'
	 *	AND gsc007.json->>'datasetname' ILIKE '%new%'
	 *		AND gsc006.json->>'organization' = '665'
	 *	) */
	private String createSearchQuery(String tableNameDataset, String tableNameDatasource, String idDataset,
			String datasetName, String idDatasource, String idOrganization) {

		StringBuilder sb = new StringBuilder();
		sb.append("SELECT * FROM ").append(tableNameDataset).append(" dataset WHERE dataset.id IN ( ");
		sb.append("SELECT gsc007.id FROM ").append(tableNameDataset).append(" gsc007, ").append(tableNameDatasource)
				.append(" gsc006 WHERE ");
		sb.append("gsc006.id = cast(gsc007.json->>'").append(Constants.DATASOURCE_ID_FIELD).append("' as integer) ");

		if (StringUtils.isNotEmpty(idDataset)) {
			sb.append(" AND gsc007.id = ").append(idDataset);
		}

		if (StringUtils.isNotEmpty(datasetName)) {
			sb.append(" AND gsc007.json->>'").append(Constants.DSET_NAME_FIELD).append("' ILIKE '%").append(datasetName)
					.append("%'");
		}

		if (StringUtils.isNotEmpty(idDatasource)) {
			sb.append(" AND gsc007.json->>'").append(Constants.DATASOURCE_ID_FIELD).append("' = '").append(idDatasource).append("'");
		}

		if (StringUtils.isNotEmpty(idOrganization)) {
			sb.append(" AND gsc006.json->>'").append(Constants.ORG_FIELD).append("' = '").append(idOrganization).append("'");
		}

		sb.append(")");
		return sb.toString();
	}
	
	private void forcedUpdate(String newdatasetname,String URL,String datasourceId) {
		//1) if the update is needed something special has to be specified as datasourcename
		//2) get the datasourcename through the url (if no url specified error is thrown)
		//3) save the dataset
		//4) call the updateshapeservice update method
	}
}
