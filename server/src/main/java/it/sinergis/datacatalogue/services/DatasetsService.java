package it.sinergis.datacatalogue.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.sinergis.datacatalogue.bean.jpa.Gsc007DatasetEntity;
import it.sinergis.datacatalogue.common.Constants;
import it.sinergis.datacatalogue.exception.DCException;
import it.sinergis.datacatalogue.persistence.PersistenceServiceProvider;
import it.sinergis.datacatalogue.persistence.services.Gsc007DatasetPersistence;

public class DatasetsService extends ServiceCommons {

	public static String RESPONSE_JSON_STATUS_DONE = "{Status:'Done',Description:'Operation ok'}";
	public static String RESPONSE_JSON_LIST_DATASET = " {dataset:[{name:'Dataset1',realname:'dataset1',datasourcename:'Datasource1',description:'First dataset',tobeingested:'true',refreshinterval:'3'},"
			+ "{name:'Dataset2',realname:'Dataset2',datasourcename:'Datasource2',description:'Second dataset'}]}";
	public static String RESPONSE_JSON_LIST_COLS_METADATA = " {columns:[{name:'Col1',type:'number',alias:'Col 1',visibility:'true'},{name:'Col2',type:'char',alias:'Col 2',visibility:'false'}]}";

	/** Logger. */
	private static Logger logger;

	/** Dao dataset. */
	private Gsc007DatasetPersistence gsc007Dao;

	/** Constructor. */
	public DatasetsService() {
		logger = Logger.getLogger(this.getClass());
		gsc007Dao = PersistenceServiceProvider.getService(Gsc007DatasetPersistence.class);
	}

	public String createDataset(String req) {
		try {
			checkJsonWellFormed(req);
			checkMandatoryParameters(Constants.CREATE_DATASET, req);
			logger.info(req);

			// check if there's another dataset already saved with the same
			// name
			Gsc007DatasetEntity datasetEntity = getDatasetObject(req);

			// if no results found -> add new record
			if (datasetEntity == null) {
				Gsc007DatasetEntity dset = new Gsc007DatasetEntity();
				dset.setJson(req);

				// TODO check if datasource with the given id in the creation
				// request exists

				dset = gsc007Dao.save(dset);

				logger.info("Dataset succesfully created");
				logger.info(req);
				return createJsonStatus(Constants.STATUS_DONE, Constants.DATASETS_CREATED, dset.getId(), req);

				// otherwise an error message will be returned
			} else {
				throw new DCException(Constants.ER700, req);
			}

		} catch (DCException rpe) {
			return rpe.returnErrorString();
		} catch (Exception e) {
			logger.error("create organization service error", e);
			DCException rpe = new DCException(Constants.ER01, req);
			logger.error("createOrganization service: unhandled error " + rpe.returnErrorString());

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
				throw new DCException(Constants.ER703, req);
			} else {

				// Before updating we have to be sure that dataset name
				// doesn't already exist in another record (not the one
				// we're updating)

				// TODO check that the datasource with the given id exists
				entityFound.setJson(req);

				logger.info("Dataset succesfully created");
				logger.info(req);
				return createJsonStatus(Constants.STATUS_DONE, Constants.DATASETS_UPDATED, entityFound.getId(), req);
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

			boolean deleted = gsc007Dao.delete(Long.parseLong(idDataset));
			// TODO delete other tables referencing dataset

			if (deleted) {
				return createJsonStatus(Constants.STATUS_DONE, Constants.DATASETS_DELETED, null, req);
			} else {
				throw new DCException(Constants.ER702, req);
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
			String idDatasource = getFieldValueFromJsonText(req, Constants.DSOURCE_ID_FIELD);

			// idDataset has priority in the research
			if (StringUtils.isNotEmpty(idDataset)) {
				dsets = new ArrayList<Gsc007DatasetEntity>();

				Gsc007DatasetEntity entityFound = gsc007Dao.load(Long.parseLong(idDataset));
				if (entityFound == null) {
					throw new DCException(Constants.ER701, req);
				}
				dsets.add(entityFound);
			} else {
				// We build the query appending parameters
				StringBuilder builderQuery = new StringBuilder();

				if (StringUtils.isNotEmpty(idDatasource)) {
					builderQuery.append("'");
					builderQuery.append(Constants.DSOURCE_ID_FIELD);
					builderQuery.append("' = '");
					builderQuery.append(idDatasource);
					builderQuery.append("'");
				}

				if (StringUtils.isNotEmpty(idDatasource) && StringUtils.isNotEmpty(datasetName)) {
					builderQuery.append(" AND ");
				}

				if (StringUtils.isNotEmpty(datasetName)) {
					builderQuery.append("'");
					builderQuery.append(Constants.DSET_NAME_FIELD);
					builderQuery.append("' LIKE '%");
					builderQuery.append(datasetName);
					builderQuery.append("%'");
				}

				// if the builder produced something not empty
				if (StringUtils.isNotEmpty(builderQuery.toString())) {

					String query = createQuery(builderQuery.toString(), Constants.DATASETS_TABLE_NAME,
							Constants.JSON_COLUMN_NAME, "select");
					dsets = gsc007Dao.getDatasets(query);
				}
				// else we select all datasets
				else {

					dsets = gsc007Dao.loadAll();
				}
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

				String idDataSource = getFieldValueFromJsonText(entity.getJson(), Constants.DSOURCE_ID_FIELD);
				dsetsMap.put(Constants.DSOURCE_ID_FIELD, idDataSource);
				// TODO inserire datasourcename

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
		return RESPONSE_JSON_LIST_COLS_METADATA;
	}

	public String updateColumnsMetadata(String req) {
		return RESPONSE_JSON_STATUS_DONE;
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
	private Gsc007DatasetEntity getDatasetObject(String json) throws DCException {

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
}
