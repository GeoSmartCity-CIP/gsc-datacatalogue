package it.sinergis.datacatalogue.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import it.sinergis.datacatalogue.bean.jpa.Gsc007DatasetEntity;
import it.sinergis.datacatalogue.bean.jpa.Gsc008LayerEntity;
import it.sinergis.datacatalogue.common.Constants;
import it.sinergis.datacatalogue.exception.DCException;
import it.sinergis.datacatalogue.persistence.PersistenceServiceProvider;
import it.sinergis.datacatalogue.persistence.services.Gsc007DatasetPersistence;
import it.sinergis.datacatalogue.persistence.services.Gsc008LayerPersistence;

public class LayersService extends ServiceCommons {

	/** Logger. */
	private static Logger logger;

	/** Gsc001Datasource DAO. */
	Gsc008LayerPersistence layerPersistence;

	/**
	 * Constructor
	 */
	public LayersService() {
		logger = Logger.getLogger(this.getClass());
		layerPersistence = PersistenceServiceProvider.getService(Gsc008LayerPersistence.class);
	}

	
	public static String RESPONSE_JSON_STATUS_DONE =  "{Status:'Done'}";
	public static String RESPONSE_JSON_LIST_LAYER = "  {layers:[{name:'Streets',datasetname:'Dataset1',description:'Main streets',metadata:'',sld:'streets.sld'},{name:'Trees',datasetname:'Dataset2',description:'trees',metadata:'',sld:'trees.sld'}]}";
	
	
	/**
	 * Creates a new Layer.
	 * 
	 * @param req
	 * @return
	 */
	public String createLayer(String req){
		try {
			// preliminary checks on the request parameters
			preliminaryChecks(req, Constants.CREATE_LAYER);

			// check if the inserted layer id exists in the dataset
			// table. If not throws exception
			checkIdDatasetValid(req);
			// check if there's another layer already saved with the same
			// name
			Gsc008LayerEntity layer = getDatasourceObject(req);

			// if no results found -> add new record
			if (layer == null) {
				Gsc008LayerEntity lyr = new Gsc008LayerEntity();
				lyr.setJson(req);

				lyr = layerPersistence.save(lyr);

				logger.info("Layer succesfully created");
				logger.info(req);
				return createJsonStatus(Constants.STATUS_DONE, Constants.LAYER_CREATED, lyr.getId(), req);

				// otherwise an error message will be return
			} else {
				DCException rpe = new DCException(Constants.ER802, req);
				return rpe.returnErrorString();
			}

		} catch (DCException rpe) {
			return rpe.returnErrorString();
		} catch (NumberFormatException nfe) {
			logger.error("inserted id parameter is not a number", nfe);
			DCException rpe = new DCException(Constants.ER12, req);
			return rpe.returnErrorString();
		} catch (Exception e) {
			logger.error("createLayer service error", e);
			DCException rpe = new DCException(Constants.ER01, req);
			logger.error("createLayer service: unhandled error " + rpe.returnErrorString());

			return rpe.returnErrorString();
		}
	}
	
	/**
	 * Updates an existing layer.
	 * 
	 * @param req
	 * @return
	 */
	public String updateLayer(String req){
		try {

			// preliminary checks on the request parameters
			preliminaryChecks(req, Constants.UPDATE_LAYER);

			// check if the inserted layer id exists in the dataset
			// table. If not throws exception
			checkIdDatasetValid(req);

			Gsc008LayerEntity layer = getDatasourceObject(req);
			Long requestedId = Long.parseLong(getFieldValueFromJsonText(req, Constants.LAYER_ID_FIELD));

			// check if there's another layer already saved with the
			// same ID
			Gsc008LayerEntity retrievedLayer = getLayerObjectById(requestedId);

			if (retrievedLayer != null) {
				// if no layer with the specified name exists or if the only
				// record found with the same name is the record to be updated
				// itself -> update record
				if (layer == null || layer.getId().longValue() == requestedId.longValue()) {
				
					retrievedLayer.setJson(updateLayerJson(req));
					layerPersistence.save(retrievedLayer);

					logger.info("Layer succesfully updated");
					logger.info(req);
					return createJsonStatus(Constants.STATUS_DONE, Constants.LAYER_UPDATED, null, req);
				} else {
					DCException rpe = new DCException(Constants.ER804, req);
					return rpe.returnErrorString();
				}

				// otherwise throw exception
			} else {
				DCException rpe = new DCException(Constants.ER803, req);
				return rpe.returnErrorString();
			}

		} catch (DCException rpe) {
			return rpe.returnErrorString();
		} catch (NumberFormatException nfe) {
			logger.error("inserted id parameter is not a number", nfe);
			DCException rpe = new DCException(Constants.ER12, req);
			return rpe.returnErrorString();
		} catch (Exception e) {
			logger.error("update layer service error", e);
			DCException rpe = new DCException(Constants.ER01, req);
			logger.error("updateDatasource service: unhandled error " + rpe.returnErrorString());

			return rpe.returnErrorString();
		}
	}
	
	/**
	 * Delete a layer.
	 * 
	 * @param req
	 * @return
	 */
	public String deleteLayer(String req){
		try {
			// preliminary checks on the request parameters
			preliminaryChecks(req, Constants.DELETE_LAYER);

			// check if there is a layer with that id
			Gsc008LayerEntity layer = getLayerObjectById(
					Long.parseLong(getFieldValueFromJsonText(req, Constants.LAYER_ID_FIELD)));

			// if results found -> delete record
			if (layer != null) {
				DeleteService deleteService = new DeleteService();
				deleteService.deleteLayer(null, null, layer.getId());

				logger.info("Layer succesfully deleted");
				logger.info(req);
				return createJsonStatus(Constants.STATUS_DONE, Constants.LAYER_DELETED, null, req);

				// otherwise error
			} else {
				DCException rpe = new DCException(Constants.ER805,req);
				return rpe.returnErrorString();
			}

		} catch (DCException rpe) {
			return rpe.returnErrorString();
		} catch (NumberFormatException nfe) {
			logger.error("inserted id parameter is not a number", nfe);
			DCException rpe = new DCException(Constants.ER12, req);
			return rpe.returnErrorString();
		} catch (Exception e) {
			logger.error("delete layer service error", e);
			DCException rpe = new DCException(Constants.ER01, req);
			logger.error("deleteDatasource service: unhandled error " + rpe.returnErrorString());

			return rpe.returnErrorString();
		}
	}
	
	/**
	 * finds layers matching the request specifications.
	 * 
	 * @param req
	 * @return
	 */
	public String listLayer(String req){
		try {
			String query = null;
			List<Gsc008LayerEntity> layers = new ArrayList<Gsc008LayerEntity>();

			if (!req.equals("{}")) {
				// preliminary checks on the request parameters
				preliminaryChecks(req, Constants.LIST_LAYER);

				// There are two research modes: by id and by dataset
				// If both parameters are found in the request an error will be
				// thrown.
				// If a research by dataset is performed, users may specify
				// a partial or total layername
				// in order to refine the content of the response.
				String datasetIdParameter = getFieldValueFromJsonText(req, Constants.DSET_ID_FIELD);
				String layerNameParameter = getFieldValueFromJsonText(req, Constants.LAYER_NAME_FIELD);
				String layerIdParameter = getFieldValueFromJsonText(req, Constants.LAYER_ID_FIELD);
				if (layerIdParameter != null
						&& (layerNameParameter != null || datasetIdParameter != null)) {
					logger.error(
							"Incorrect parameters: perform a request either by layerid or by datasetid (and layername). Both parameters are not allowed at the same time");
					throw new DCException(Constants.ER806, req);
				}
				// if the idlayer parameter is in the request the research
				// will be done by id.
				if (layerIdParameter != null) {
					Gsc008LayerEntity layerFoundById = getLayerObjectById(
							Long.parseLong(layerIdParameter));
					if (layerFoundById != null) {
						layers.add(layerFoundById);
					}
					// otherwise the research is based on the dataset
					// parameter.
				} else if (datasetIdParameter != null) {
					String queryText = "'" + Constants.DSET_ID_FIELD + "' =  '" + datasetIdParameter + "'";

					// user may additionally specify a partial or complete
					// layername to refine the research process.
					if (layerNameParameter != null) {
						queryText += " AND '" + Constants.LAYER_NAME_FIELD + "' LIKE '%"
								+ getKeyFromJsonText(req, Constants.LAYER_NAME_FIELD) + "%'";
					}

					query = createQuery(queryText, Constants.LAYER_TABLE_NAME, Constants.JSON_COLUMN_NAME,
							"select");
					layers = layerPersistence.getLayers(query);

				} else {
					logger.error(
							"Incorrect parameters: either iddataset or idlayer parameters should be specified.");
					throw new DCException(Constants.ER807, req);
				}
			} else {
				layers = layerPersistence.loadAll();
			}

			logger.info("Layers found: " + layers.size());
			logger.info(req);
			if (layers.size() == 0) {
				logger.error("No results found.");
				throw new DCException(Constants.ER13, req);
			}

			ObjectMapper mapper = new ObjectMapper();
			ObjectNode root = JsonNodeFactory.instance.objectNode();
			ArrayNode layerNodeList = JsonNodeFactory.instance.arrayNode();
			for (Gsc008LayerEntity layer : layers) {
				ObjectNode layerBasic = (ObjectNode) mapper.readTree(layer.getJson());
				layerBasic.put(Constants.ID, layer.getId());
				layerNodeList.add(layerBasic);
			}
			root.put(Constants.LAYERS, layerNodeList);
			root.put(Constants.REQUEST, mapper.readTree(req));
			String jsonString;
			try {
				jsonString = mapper.writeValueAsString(root);
			} catch (IOException e) {
				logger.error("IOException during the construction of status response", e);
				throw new DCException(Constants.ER01, req);
			}

			return jsonString;

		} catch (DCException rpe) {
			return rpe.returnErrorString();
		} catch (NumberFormatException nfe) {
			logger.error("inserted id parameter is not a number", nfe);
			DCException rpe = new DCException(Constants.ER12, req);
			return rpe.returnErrorString();
		} catch (Exception e) {
			logger.error("list layers service error", e);
			DCException rpe = new DCException(Constants.ER01, req);
			logger.error("list layers service: unhandled error " + rpe.returnErrorString());

			return rpe.returnErrorString();
		}
	}
	
	/**
	 * Checks if the given parameter for dataset matches the id of any
	 * existing dataset.
	 * 
	 * @param datasetId
	 * @return
	 * @throws DCException
	 * @throws NumberFormatException
	 */
	private void checkIdDatasetValid(String req) throws NumberFormatException, DCException {
		Long datasetId = Long.parseLong(getKeyFromJsonText(req, Constants.DSET_ID_FIELD));
		Gsc007DatasetPersistence datasetPersistence = PersistenceServiceProvider
				.getService(Gsc007DatasetPersistence.class);
		Gsc007DatasetEntity datasetEntity = datasetPersistence.load(datasetId);
		if (datasetEntity == null) {
			DCException rpe = new DCException(Constants.ER801, req);
			throw rpe;
		}
	}
	
	/**
	 * Retrieves the layer given an datasource name.
	 * 
	 * @param json
	 * @return
	 * @throws RPException
	 */
	private Gsc008LayerEntity getDatasourceObject(String json) throws DCException {
		ArrayList<String> params = new ArrayList<String>();
		params.add(Constants.LAYER_NAME_FIELD);
		params.add(Constants.DSET_ID_FIELD);
		return (Gsc008LayerEntity) getRowObject(json, Constants.LAYER_TABLE_NAME, params,
				layerPersistence);
	}
	
	/**
	 * Retrieves the layer given an layer Id.
	 * 
	 * @param json
	 * @return
	 * @throws RPException
	 */
	private Gsc008LayerEntity getLayerObjectById(Long id) throws DCException {
		return (Gsc008LayerEntity) layerPersistence.load(id);
	}
	
	/**
	 * Update layer json
	 * 
	 * @param newJson
	 *            new json
	 * @return json updated
	 * @throws DCException
	 */
	private String updateLayerJson(String newJson) throws DCException {
		try {
			JsonNode newRootNode = om.readTree(newJson);

			JsonNode newLYRName = newRootNode.findValue(Constants.LAYER_NAME_FIELD);
			if (newLYRName == null) {
				logger.error(Constants.LAYER_NAME_FIELD + " parameter is mandatory within the json string.");
				throw new DCException(Constants.ER04,newJson);
			}

			((ObjectNode) newRootNode).put(Constants.LAYER_NAME_FIELD, newLYRName.toString().replace("\"", ""));

			((ObjectNode) newRootNode).remove(Constants.LAYER_ID_FIELD);

			return newRootNode.toString();
		} catch (DCException rpe) {
			throw rpe;
		} catch (Exception e) {
			logger.error("unhandled error: ", e);
			throw new DCException(Constants.ER01, newJson);
		}

	}
}
