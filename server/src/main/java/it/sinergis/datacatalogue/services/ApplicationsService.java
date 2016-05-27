package it.sinergis.datacatalogue.services;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import it.geosolutions.geoserver.rest.GeoServerRESTReader;
import it.geosolutions.geoserver.rest.decoder.RESTDataStore;
import it.geosolutions.geoserver.rest.decoder.RESTDataStoreList;
import it.geosolutions.geoserver.rest.decoder.RESTNamespace;
import it.geosolutions.geoserver.rest.decoder.RESTWorkspaceList;
import it.geosolutions.geoserver.rest.manager.GeoServerRESTStoreManager;
import it.sinergis.datacatalogue.bean.jpa.Gsc001OrganizationEntity;
import it.sinergis.datacatalogue.bean.jpa.Gsc006DatasourceEntity;
import it.sinergis.datacatalogue.bean.jpa.Gsc007DatasetEntity;
import it.sinergis.datacatalogue.bean.jpa.Gsc008LayerEntity;
import it.sinergis.datacatalogue.bean.jpa.Gsc009GrouplayerEntity;
import it.sinergis.datacatalogue.bean.jpa.Gsc010ApplicationEntity;
import it.sinergis.datacatalogue.common.Constants;
import it.sinergis.datacatalogue.exception.DCException;
import it.sinergis.datacatalogue.persistence.PersistenceServiceProvider;
import it.sinergis.datacatalogue.persistence.services.Gsc001OrganizationPersistence;
import it.sinergis.datacatalogue.persistence.services.Gsc006DatasourcePersistence;
import it.sinergis.datacatalogue.persistence.services.Gsc007DatasetPersistence;
import it.sinergis.datacatalogue.persistence.services.Gsc008LayerPersistence;
import it.sinergis.datacatalogue.persistence.services.Gsc009GrouplayerPersistence;
import it.sinergis.datacatalogue.persistence.services.Gsc010ApplicationPersistence;
import it.sinergis.datacatalogue.persistence.services.util.ServiceUtil;
import it.sinergis.datacatalogue.services.datastore.PostgisDataStore;
import it.sinergis.datacatalogue.services.datastore.PropertyLayer;
import it.sinergis.datacatalogue.services.datastore.SHAPEDataStore;

public class ApplicationsService extends ServiceCommons {

	public static String RESPONSE_JSON_STATUS_DONE = "{Status:'Done', Description:'Ok'}";
	public static String RESPONSE_JSON_LIST_APPLICATION = "{applications:[{applicationname:'ClientWeb',layers:[{layername:'Layer1'},{layername:'Layer2'}],groups:[{groupname:'Group1'},{groupname:'Group2'}]},{applicationname:'Application 2',layers:[{layername:'Layer3'},{layername:'Layer4'}],groups:[{groupname:'Group3'},{groupname:'Group4'}]}]}";
	public static String RESPONSE_JSON_GET_CONFIGURATION = "{}";

	/** Logger. */
	private static Logger logger;

	/** Dao organization. */
	private Gsc001OrganizationPersistence gsc001Dao;

	/** Gsc006Datasource DAO. */
	private Gsc006DatasourcePersistence gsc006Dao;

	/** Gsc007Dataset DAO. */
	private Gsc007DatasetPersistence gsc007Dao;

	/** Gsc008Layer DAO. */
	private Gsc008LayerPersistence gsc008Dao;

	/** Gsc009GroupLayer DAO. */
	private Gsc009GrouplayerPersistence gsc009Dao;

	/** Dao application. */
	private Gsc010ApplicationPersistence gsc010Dao;
	
	private String applicationJson = null;

	public ApplicationsService() {
		logger = Logger.getLogger(this.getClass());
		gsc001Dao = PersistenceServiceProvider.getService(Gsc001OrganizationPersistence.class);
		gsc006Dao = PersistenceServiceProvider.getService(Gsc006DatasourcePersistence.class);
		gsc007Dao = PersistenceServiceProvider.getService(Gsc007DatasetPersistence.class);
		gsc008Dao = PersistenceServiceProvider.getService(Gsc008LayerPersistence.class);
		gsc009Dao = PersistenceServiceProvider.getService(Gsc009GrouplayerPersistence.class);
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
				// check if organization with the given id exists
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
						// organization doesn't exist
						throw new DCException(Constants.ER1002, req);
					}
				} else {
					// Organization id has to be numeric
					throw new DCException(Constants.ER12, req);
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
		try {
			checkJsonWellFormed(req);
			checkMandatoryParameters(Constants.ASSIGN_TO_APP, req);
			logger.info(req);

			// retrieve the application from ID
			String idApplication = getFieldValueFromJsonText(req, Constants.APPLICATION_ID);
			String idOrganization = getFieldValueFromJsonText(req, Constants.ORG_FIELD);

			if (StringUtils.isNotEmpty(idApplication)) {
				Gsc010ApplicationEntity entityFound = gsc010Dao.load(Long.parseLong(idApplication));

				if (entityFound == null) {
					// No application found with given parameters.
					throw new DCException(Constants.ER1002, req);
				}

				if (StringUtils.isNumeric(idOrganization)) {
					Gsc001OrganizationEntity orgEntity = gsc001Dao.load(Long.parseLong(idOrganization));

					if (orgEntity != null) {

						ObjectNode requestJson = (ObjectNode) om.readTree(req);

						// Check layers existence
						ArrayNode requestLayers = (ArrayNode) requestJson.findValue(Constants.LAYERS);

						// get all the layers id involved
						List<Long> idlayers = getIdFromRequest(requestLayers, Constants.LAYER_ID_FIELD);

						if (!idlayers.isEmpty()) {
							// create the request
							String queryLayers = createCheckRequest(idlayers, Constants.LAYER_TABLE_NAME,
									idOrganization, true);
							// execute the request
							Long resultNumberLayers = gsc008Dao.countInId(queryLayers);
							// if at least one of the specified layers does not
							// exist throw error
							// if the countnumber is less than the id list size
							// one
							// or more records was not found
							if (resultNumberLayers < idlayers.size()) {
								logger.error(
										"Incorrect parameters: one of the requested layers cannot be assigned to the application because it does not exist.");
								throw new DCException(Constants.ER1005, req);
							}
						}

						// Check groups existence
						ArrayNode requestGroups = (ArrayNode) requestJson.findValue(Constants.GROUPS);

						// get all the layers id involved
						List<Long> idGroups = getIdFromRequest(requestGroups, Constants.GROUP_LAYER_ID_FIELD);

						if (!idGroups.isEmpty()) {
							// create the request
							String queryGroups = createCheckRequest(idGroups, Constants.GROUP_LAYER_TABLE_NAME,
									idOrganization, false);
							// execute the request
							Long resultNumberGroups = gsc008Dao.countInId(queryGroups);

							// if at least one of the specified layers does not
							// exist throw error
							// if the countnumber is less than the id list size
							// one
							// or more records was not found
							if (resultNumberGroups < idGroups.size()) {
								logger.error(
										"Incorrect parameters: one of the requested groups cannot be assigned to the application because it does not exist.");
								throw new DCException(Constants.ER1006, req);
							}
						}

						entityFound.setJson(req);
						gsc010Dao.save(entityFound);

						logger.info("Application succesfully created");
						return createJsonStatus(Constants.STATUS_DONE, Constants.APPLICATION_LAYER_GROUP_ASSIGNED,
								entityFound.getId(), req);
					} else {
						// organization doesn't exist
						throw new DCException(Constants.ER1002, req);
					}

				} else {
					// Organization id has to be numeric
					throw new DCException(Constants.ER12, req);
				}

			} else {
				// Application id has to be numeric
				throw new DCException(Constants.ER12, req);
			}

		} catch (DCException rpe) {
			return rpe.returnErrorString();
		} catch (Exception e) {
			logger.error("update application service error", e);
			DCException rpe = new DCException(Constants.ER01, req);
			logger.error("update service: unhandled error " + rpe.returnErrorString());

			return rpe.returnErrorString();
		}
	}

	public String deleteApplication(String req) {
		try {
			checkJsonWellFormed(req);
			checkMandatoryParameters(Constants.DELETE_APP, req);
			logger.info(req);

			// Retrieving input parameters
			String idApplication = getFieldValueFromJsonText(req, Constants.APPLICATION_ID);

			if (StringUtils.isNumeric(idApplication)) {

				Gsc010ApplicationEntity entityFound = gsc010Dao.load(Long.parseLong(idApplication));
				if (entityFound == null) {
					// No application found with given parameters.
					throw new DCException(Constants.ER1002, req);
				}

				DeleteService deleteService = new DeleteService();
				deleteService.deleteApplication(null, null, Long.parseLong(idApplication), null);

				logger.info("application succesfully deleted");
				logger.info(req);
				return createJsonStatus(Constants.STATUS_DONE, Constants.APPLICATION_DELETED, null, req);

			} else {
				// Application id has to be numeric
				throw new DCException(Constants.ER12, req);
			}

		} catch (DCException rpe) {
			return rpe.returnErrorString();
		} catch (Exception e) {
			logger.error("delete application service error", e);
			DCException rpe = new DCException(Constants.ER01, req);
			logger.error("delete service: unhandled error " + rpe.returnErrorString());

			return rpe.returnErrorString();
		}
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
			String organization = getFieldValueFromJsonText(req, Constants.ORG_FIELD);

			// idApplication has priority in the research
			if (StringUtils.isNotEmpty(idApplication)) {
				appEntities = new ArrayList<Gsc010ApplicationEntity>();

				if (StringUtils.isNumeric(idApplication)) {
					Gsc010ApplicationEntity entityFound = gsc010Dao.load(Long.parseLong(idApplication));
					if (entityFound == null) {
						// No application found with given parameters.
						throw new DCException(Constants.ER1002, req);
					}
					appEntities.add(entityFound);
				} else {
					// Application id has to be numeric
					throw new DCException(Constants.ER12, req);
				}
			} else {
				// We build the query appending parameters
				StringBuilder builderQuery = new StringBuilder();

				if (StringUtils.isNotEmpty(organization)) {

					if (StringUtils.isNumeric(organization)) {
						builderQuery.append("'");
						builderQuery.append(Constants.ORG_FIELD);
						builderQuery.append("' = '");
						builderQuery.append(organization);
						builderQuery.append("'");
					} else {
						// Organization id has to be numeric
						throw new DCException(Constants.ER12, req);
					}
				}

				if (StringUtils.isNotEmpty(organization) && StringUtils.isNotEmpty(applicationName)) {
					builderQuery.append(" AND ");
				}

				if (StringUtils.isNotEmpty(applicationName)) {
					builderQuery.append("'");
					builderQuery.append(Constants.APP_NAME_FIELD);
					builderQuery.append("' ILIKE '%");
					builderQuery.append(applicationName);
					builderQuery.append("%'");
				}

				// if the builder produced something not empty
				if (StringUtils.isNotEmpty(builderQuery.toString())) {

					String query = createQuery(builderQuery.toString(), Constants.APPLICATION_TABLE_NAME,
							Constants.JSON_COLUMN_NAME, "select");
					appEntities = gsc010Dao.getApplications(query);
				}
				// else we select all applications
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

				// List of layers
				String layers = getObjectFromJsonText(entity.getJson(), Constants.LAYERS);
				if (layers != null) {
					ArrayNode layersNode = (ArrayNode) om.readTree(layers);
					Iterator<JsonNode> layersElement = layersNode.elements();

					List<String> idLayersList = new ArrayList<String>();

					// If the list of layers is not empty, I build a list of
					// layers id
					while (layersElement.hasNext()) {
						JsonNode next = layersElement.next();
						String idLayerValue = getFieldValueFromJsonText(next.toString(), Constants.LAYER_ID_FIELD);

						if (StringUtils.isNumeric(idLayerValue)) {
							idLayersList.add(idLayerValue);

						} else {
							throw new DCException(Constants.ER12, req);
						}
					}

					// I retrieve all layer entities from the id list
					String layerQuery = loadObjectFromIdList(Constants.LAYER_TABLE_NAME, idLayersList);
					List<Gsc008LayerEntity> entitiesFound = StringUtils.isNotEmpty(layerQuery)
							? gsc008Dao.getLayers(layerQuery) : null;

					ArrayNode layersNodeWithLayerName = om.createArrayNode();

					// I build the json object idlayer:id,layername:name
					if (entitiesFound != null && !entitiesFound.isEmpty()) {

						for (Gsc008LayerEntity entityFound : entitiesFound) {

							String layerName = getFieldValueFromJsonText(entityFound.getJson(),
									Constants.LAYER_NAME_FIELD);
							ObjectNode layerToInsert = om.createObjectNode();
							layerToInsert.put(Constants.LAYER_ID_FIELD, entityFound.getId());
							layerToInsert.put(Constants.LAYER_NAME_FIELD, layerName);
							layersNodeWithLayerName.add(layerToInsert);
						}
					}
					// else {
					// logger.error(
					// "There's no record in the layer table associated with one
					// of the layer id assigned to the application.");
					// throw new DCException(Constants.ER1008, req);
					// }
					appMap.put(Constants.LAYERS, om.readTree(om.writeValueAsString(layersNodeWithLayerName)));
				}

				// List of group layers
				String groups = getObjectFromJsonText(entity.getJson(), Constants.GROUPS);
				if (groups != null) {
					ArrayNode groupsNode = (ArrayNode) om.readTree(groups);
					Iterator<JsonNode> groupsElement = groupsNode.elements();

					List<String> idGroupsList = new ArrayList<String>();

					// If the list of group layers is not empty, I build a list
					// of layers id
					while (groupsElement.hasNext()) {
						JsonNode next = groupsElement.next();
						String idGroupValue = getFieldValueFromJsonText(next.toString(),
								Constants.GROUP_LAYER_ID_FIELD);

						if (StringUtils.isNumeric(idGroupValue)) {
							idGroupsList.add(idGroupValue);
						} else {
							throw new DCException(Constants.ER12, req);
						}
					}

					// I retrieve all group layer entities from the id list
					String groupQuery = loadObjectFromIdList(Constants.GROUP_LAYER_TABLE_NAME, idGroupsList);
					List<Gsc009GrouplayerEntity> entitiesFound = StringUtils.isNotEmpty(groupQuery)
							? gsc009Dao.getGroupLayers(groupQuery) : null;
					ArrayNode groupsNodeWithGroupName = om.createArrayNode();

					// I build the json object idgroup:id,groupname:name
					if (entitiesFound != null && !entitiesFound.isEmpty()) {

						for (Gsc009GrouplayerEntity entityFound : entitiesFound) {

							String groupName = getFieldValueFromJsonText(entityFound.getJson(),
									Constants.GROUP_LAYER_NAME_FIELD);
							ObjectNode groupToInsert = om.createObjectNode();
							groupToInsert.put(Constants.GROUP_LAYER_ID_FIELD, entityFound.getId());
							groupToInsert.put(Constants.GROUP_LAYER_NAME_FIELD, groupName);
							groupsNodeWithGroupName.add(groupToInsert);
						}
					}
					// else {
					// logger.error(
					// "There's no record in the group layer table associated
					// with one of the layer id assigned to the application.");
					// throw new DCException(Constants.ER1007, req);
					// }
					appMap.put(Constants.GROUPS, om.readTree(om.writeValueAsString(groupsNodeWithGroupName)));
				}

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
		try {
			checkJsonWellFormed(req);
			checkMandatoryParameters(Constants.PUBLISH_ON_GEOSERVER, req);
			logger.info(req);

			String idApplication = getFieldValueFromJsonText(req, Constants.APPLICATION_ID);
			Gsc010ApplicationEntity application = gsc010Dao.load(Long.parseLong(idApplication));

			if (application == null) {
				// No application found with given parameters.
				throw new DCException(Constants.ER1002, req);
			}

			String geoserverProperties = getObjectFromJsonText(application.getJson(), Constants.GEOSERVER_PARAMS);
			String urlGeoserver = getFieldValueFromJsonText(geoserverProperties, Constants.URL);
			String userGeoserver = getFieldValueFromJsonText(geoserverProperties, Constants.USERNAME_FIELD);
			String pwdGeoserver = getFieldValueFromJsonText(geoserverProperties, Constants.PASSWORD_FIELD);

			try {

				GeoServerRESTReader reader = new GeoServerRESTReader(urlGeoserver, userGeoserver, pwdGeoserver);

				GeoServerRESTPublisher publisher = new GeoServerRESTPublisher(urlGeoserver, userGeoserver,
						pwdGeoserver);

				GeoServerRESTStoreManager storeManager = new GeoServerRESTStoreManager(new URL(urlGeoserver),
						userGeoserver, pwdGeoserver);

				RESTWorkspaceList listaWorkspace = reader.getWorkspaces();
				String workspace_name = getFieldValueFromJsonText(application.getJson(), Constants.APP_NAME_FIELD);
				workspace_name = workspace_name.replace(" ", "_");
				String workspace_uri = getFieldValueFromJsonText(application.getJson(), Constants.APP_URI);
				String srs = getFieldValueFromJsonText(application.getJson(), Constants.SRS);

				boolean found = false;
				if (listaWorkspace != null) {
					List<String> wsList = reader.getWorkspaceNames();

					// creo un nuovo workspace su geoserver con il nome della
					// mappa
					// se esiste un workspace con il nome della mappa allora lo
					// rimuovo da geoserver
					// e vengono rimossi tutti i layer e gli store associati a
					// quel workspace

					for (int i = 0; i < wsList.size(); i++) {
						String ws = wsList.get(i);
						RESTNamespace wsnamespace = reader.getNamespace(ws);
						if (ws.equals(workspace_name) || wsnamespace.getURI().toString().equals(workspace_uri)) {
							logger.debug("Workspace found: " + workspace_name + ".");

							workspace_name = ws;
							found = true;
							break;
						}
					}
				}

				logger.debug("Inizio pubblicazione su geoserver");

				boolean created = false;
				if (!found) {
					logger.debug("Creazione workspace con nome " + workspace_name);
					created = publisher.createWorkspace(workspace_name, new URI(workspace_uri));

					if (!created) {
						throw new DCException(Constants.ER_GEO03);
					}
				}

				RESTDataStoreList databaselist = reader.getDatastores(workspace_name);

				if (databaselist != null) {
					List<String> nameList = databaselist.getNames();

					for (int i = 0; i < nameList.size(); i++) {
						String elestore = nameList.get(i);

						RESTDataStore datastore = reader.getDatastore(workspace_name, elestore);

						String storeType = datastore.getStoreType();

						if ((storeType != null && storeType.toUpperCase().indexOf("POSTGIS") == -1
								&& storeType.toUpperCase().indexOf("SQL SERVER") == -1) || storeType == null) {
							publisher.removeDatastore(workspace_name, elestore, true);
						}

					}
				}

				HashMap<String, Boolean> checkLayerName = new HashMap<String, Boolean>();

				String layersAssigned = getObjectFromJsonText(application.getJson(), Constants.LAYERS);
				JsonNode node = om.readTree(layersAssigned);

				Iterator<JsonNode> iteratorNode = node.elements();

				while (iteratorNode.hasNext()) {

					JsonNode layerNode = iteratorNode.next();
					String layerNodeAsString = om.writeValueAsString(layerNode);
					String layerId = getFieldValueFromJsonText(layerNodeAsString, Constants.LAYER_ID_FIELD);
					Gsc008LayerEntity entityLayer = gsc008Dao.load(Long.parseLong(layerId));

					String srsLayer = getFieldValueFromJsonText(layerNodeAsString, Constants.SRS);
					if (StringUtils.isEmpty(srsLayer)) {
						srsLayer = srs;
					}

					String layerName = getFieldValueFromJsonText(entityLayer.getJson(), Constants.LAYER_NAME_FIELD);
					layerName = layerName.replace(" ", "_");
					// utilizzo di normalized per creare i servizi WMS
					// (garantisce anche l'univocita')
					layerName = ServiceUtil.normalizedLayerName(layerName);

					// il nuovo nome non deve essere gia' utilizzato
					if (checkLayerName.get(layerName) != null) {
						logger.error("The layer " + layerName + " is already in the map.");
						throw new DCException(Constants.ER01, req);
					} else {
						checkLayerName.put(layerName, Boolean.TRUE);
					}

					String datasetId = getFieldValueFromJsonText(entityLayer.getJson(), Constants.DSET_ID_FIELD);
					Gsc007DatasetEntity entityDataset = gsc007Dao.load(Long.parseLong(datasetId));
					String realName = getFieldValueFromJsonText(entityDataset.getJson(), Constants.DSET_REALNAME_FIELD);
					String tablephysicalname = StringUtils.substringBefore(realName, ".");

					String idDatasource = getFieldValueFromJsonText(entityDataset.getJson(),
							Constants.DATASOURCE_ID_FIELD);

					Gsc006DatasourceEntity entityDatasource = gsc006Dao.load(Long.parseLong(idDatasource));
					String urlShapeLocation = getFieldValueFromJsonText(entityDatasource.getJson(), Constants.PATH);
					String nameDatabase = getFieldValueFromJsonText(entityDatasource.getJson(),
							Constants.DATASOURCE_NAME_FIELD);
					nameDatabase = nameDatabase.replace(" ", "_");

					String tablePhysicalPath = "";

					String typeDatasource = getFieldValueFromJsonText(entityDatasource.getJson(), Constants.TYPE);

					if (Constants.SHAPE.equalsIgnoreCase(typeDatasource)) {
						SHAPEDataStore datastoreShapeCreator = new SHAPEDataStore();
						nameDatabase = datastoreShapeCreator.createDatastore(nameDatabase, workspace_name,
								workspace_uri, urlShapeLocation, storeManager, reader, tablephysicalname,
								tablePhysicalPath);
						logger.debug("Created datastore: " + nameDatabase + " within workspace " + workspace_name);
						PropertyLayer pl = new PropertyLayer(layerName, tablephysicalname, tablePhysicalPath, layerName,
								srsLayer, workspace_name + "_" + layerName);

						logger.debug("Publising layer: " + layerName);
						datastoreShapeCreator.publishDBLayer(workspace_name, nameDatabase, pl, publisher);
						logger.debug("Layer published: " + layerName);
					} else if (Constants.POSTGIS.equalsIgnoreCase(typeDatasource)) {
						String nameDatabaseforGeoserver = getFieldValueFromJsonText(entityDatasource.getJson(),
								Constants.DATABASE);
						String port = getFieldValueFromJsonText(entityDatasource.getJson(), Constants.PORT);
						String host = getFieldValueFromJsonText(entityDatasource.getJson(), Constants.URL);
						String schema = getFieldValueFromJsonText(entityDatasource.getJson(), Constants.SCHEMA_FIELD);
						String user = getFieldValueFromJsonText(entityDatasource.getJson(), Constants.USERNAME_FIELD);
						String password = getFieldValueFromJsonText(entityDatasource.getJson(),
								Constants.PASSWORD_FIELD);

						PostgisDataStore datastorePostgisCreator = new PostgisDataStore();
						nameDatabase = datastorePostgisCreator.createDatastore(nameDatabase, workspace_name,
								workspace_uri, storeManager, reader, host, Integer.parseInt(port), user, schema,
								password, nameDatabaseforGeoserver);

						logger.debug("Created datastore: " + nameDatabase + " within workspace " + workspace_name);
						PropertyLayer pl = new PropertyLayer(layerName, tablephysicalname, tablePhysicalPath, layerName,
								srsLayer, workspace_name + "_" + layerName);

						logger.debug("Publising layer: " + layerName);
						datastorePostgisCreator.publishDBLayer(workspace_name, nameDatabase, pl, publisher);
						logger.debug("Layer published: " + layerName);

					} else {
						logger.error("Datasource type not supported");
						throw new DCException(Constants.ER14);
					}

				}

				return createJsonStatus(Constants.STATUS_DONE, Constants.PUBLISH_ON_GEOSERVER, null, req);
			} catch (IOException e) {
				logger.error("IOException", e);
				throw new DCException(Constants.ER01, req);
			}
		} catch (DCException rpe) {
			return rpe.returnErrorString();
		} catch (Exception e) {
			logger.error("publish on geoserver service error", e);
			DCException rpe = new DCException(Constants.ER01, req);
			logger.error("publish on geoserver: unhandled error " + rpe.returnErrorString());

			return rpe.returnErrorString();
		}
	}

	public String getConfiguration(String req) {
		try {
			checkJsonWellFormed(req);
			checkMandatoryParameters(Constants.GET_CONFIGURATION, req);
			logger.info(req);
			// retrieve the application from ID
			String idApplication = getFieldValueFromJsonText(req, Constants.APPLICATION_ID);
			Gsc010ApplicationEntity application = null;

			if (StringUtils.isNotEmpty(idApplication)) {
				if (StringUtils.isNumeric(idApplication)) {
					application = gsc010Dao.load(Long.parseLong(idApplication));
					if (application == null) {
						// No application found with given parameters.
						throw new DCException(Constants.ER1002, req);
					}
				} else {
					// Application id has to be numeric
					throw new DCException(Constants.ER12, req);
				}
			}

			String jsonString;
			try {

				applicationJson = application.getJson();
				
				String layers = getObjectFromJsonText(application.getJson(), Constants.LAYERS);
				JsonNode node = om.readTree(layers);

				Iterator<JsonNode> iteratorNode = node.elements();
				ArrayList<String> listIdLayers = new ArrayList<String>();

				while (iteratorNode.hasNext()) {

					JsonNode layerNode = iteratorNode.next();
					String layerNodeAsString = om.writeValueAsString(layerNode);
					String layerId = getFieldValueFromJsonText(layerNodeAsString, Constants.LAYER_ID_FIELD);
					listIdLayers.add(layerId);
				}

				jsonString = om.writeValueAsString(createGetConfigurationResult(application.getJson(), listIdLayers));

			} catch (IOException e) {
				logger.error("IOException", e);
				throw new DCException(Constants.ER01, req);
			}

			return jsonString;

		} catch (DCException rpe) {
			return rpe.returnErrorString();
		} catch (Exception e) {
			logger.error("getConfiguration service error", e);
			DCException rpe = new DCException(Constants.ER01, req);
			logger.error("getConfiguration service: unhandled error " + rpe.returnErrorString());

			return rpe.returnErrorString();
		}
	}

	private List<Long> getIdFromRequest(ArrayNode objectList, String field) {
		List<Long> listId = new ArrayList<Long>();

		for (int i = 0; i < objectList.size(); i++) {
			JsonNode requestedId = objectList.get(i);
			JsonNode idNode = requestedId.findValue(field);
			listId.add(idNode.asLong());
		}

		return listId;
	}

	private String createCheckRequest(List<Long> ids, String tableName, String organizationID, boolean layer) {
		StringBuilder sb = new StringBuilder();

		sb.append("select count(*) from ");
		sb.append(tableName);
		sb.append(" where ");
		sb.append(Constants.ID);
		sb.append(" IN (");

		for (int i = 0; i < ids.size(); i++) {
			if (i != 0) {
				sb.append(",");
			}
			sb.append(ids.get(i));
		}
		sb.append(")");

		if (layer) {
			sb.append(" AND ");
			sb.append(Constants.ID);
			sb.append(" IN (");
			sb.append("select lyrT.id from ");
			sb.append(Constants.ORGANIZATION_TABLE_NAME);
			sb.append(" orgT inner join ");
			sb.append(Constants.DATASOURCE_TABLE_NAME);
			sb.append(" dsT on orgT.id = CAST((dsT.json->>'organization') AS integer) inner join ");
			sb.append(Constants.DATASETS_TABLE_NAME);
			sb.append(" dstT on dsT.id = CAST((dstT.json->>'iddatasource') AS integer) inner join ");
			sb.append(Constants.LAYER_TABLE_NAME);
			sb.append(" lyrT on dstT.id = CAST((lyrT.json->>'iddataset') AS integer) ");
			sb.append(" where orgT.id = ");
			sb.append(organizationID);
			sb.append(")");
		} else {
			sb.append(" AND ");
			sb.append(Constants.JSON_COLUMN_NAME);
			sb.append("->>'organization' = '");
			sb.append(organizationID);
			sb.append("'");
		}

		return sb.toString();
	}

	private ObjectNode createGetConfigurationResult(String applicationJson, ArrayList<String> listIdLayers)
			throws DCException {
		ObjectNode root = om.createObjectNode();
		ObjectNode maps = om.createObjectNode();
		ObjectNode configs = om.createObjectNode();

		configs.put(Constants.PROPERTIES, createMapsConfigsProperties());
		configs.put(Constants.CLASS_NAME, "MW.Configs");
		maps.put(Constants.CONFIGS, configs);

		maps.put(Constants.XMLNS, "http://schemas.corenet.it/mapwork/mapconfiguration");
		
		ArrayNode map = om.createArrayNode();
		ObjectNode firstMapObject = om.createObjectNode();

		String workspaceName = getFieldValueFromJsonText(applicationJson, Constants.APP_NAME_FIELD).replace(" ", "_");
				
		firstMapObject.put(Constants.NAMESPACE_PREFIX, workspaceName);
		firstMapObject.put(Constants.CLASS_NAME, "MW.Map");
		firstMapObject.put(Constants.CENTER, createTileOrigin(685521.993032, 928689.994033, "OpenLayers.LonLat"));
		
		//XXX those parameter are not mandatory
//		firstMapObject.put(Constants.DEFAULT_MAP, true);
//		firstMapObject.put(Constants.ZOOM, 2);
//		firstMapObject.put(Constants.UNITS, "m");
//		firstMapObject.put(Constants.MAX_RESOLUTION, 90.31);
//		firstMapObject.put(Constants.NUM_ZOOM_LEVELS, 12);
//		firstMapObject.put(Constants.NAME, workspaceName);

		firstMapObject.put(Constants.LAYERS, createMapsMapLayers(listIdLayers));
		firstMapObject.put(Constants.NAMESPACE_URI, getFieldValueFromJsonText(applicationJson, Constants.APP_URI));

		ObjectNode projection = om.createObjectNode();
		projection.put(Constants.PROJ4CODE, getFieldValueFromJsonText(applicationJson, Constants.SRS));
		projection.put(Constants.PROJ, "null");
		projection.put(Constants.PROJ4DEF,
				"+proj=tmerc +ellps=intl +lat_0=0 +lon_0=9 +x_0=500053 +y_0=-3999820 +k=0.9996 +units=m +proj=tmerc +ellps=intl +lat_0=0 +lon_0=9 +x_0=500053 +y_0=-3999820 +k=0.9996 +units=m +towgs84=-104.1,-49.1,-9.9,0.971,-2.917,0.714,-11.68");
		projection.put(Constants.CLASS_NAME, "OpenLayers.Projection");
		firstMapObject.put(Constants.PROJECTION, projection);

		firstMapObject.put(Constants.MAX_EXTENT,
				createMaxExtent(getMaxExtentLeft(), getMaxExtentBottom(), getMaxExtentRight(), getMaxExtentTop(), "OpenLayers.Bounds"));

		map.add(firstMapObject);
		maps.put("map", map);
		
		root.put(Constants.MAPS, maps);
		return root;
	}

	private ObjectNode createWMSLayerContainer() throws DCException {
		ObjectNode layerObject = om.createObjectNode();
		layerObject.put(Constants.NAME, Constants.WMS_LAYERS);
		layerObject.put(Constants.OPTIONS, createLayerOptions());
		layerObject.put(Constants.URL,composeWMSUrl(getUrlGeoserver(),getFieldValueFromJsonText(applicationJson, Constants.APP_NAME_FIELD).replace(" ", "_")));
		layerObject.put(Constants.CLASS_NAME, "MW.Layer.WMS");
		layerObject.put(Constants.PARAMS, createLayerParams());
		layerObject.put(Constants.IS_BASE_LAYER, false);
		layerObject.put(Constants.SINGLE_TILE, true);
		layerObject.put(Constants.VISIBILITY, true);
		layerObject.put(Constants.DISPLAY_IN_LAYER_SWITCHER, false);
		
		ArrayNode arrayNode = om.createArrayNode();
		layerObject.put(Constants.WMS_LAYERS,arrayNode);
		
		return layerObject;
	}
	
	private ObjectNode createBasicLayerContainer() throws DCException {
		ObjectNode layerObject = om.createObjectNode();
		layerObject.put(Constants.NAME, Constants.BASIC_LAYER);
		layerObject.put(Constants.OPTIONS, createLayerOptions());			
		layerObject.put(Constants.URL,composeWMSUrl(getUrlGeoserver(),getFieldValueFromJsonText(applicationJson, Constants.APP_NAME_FIELD).replace(" ", "_")));
		layerObject.put(Constants.CLASS_NAME, "MW.Layer.WMS");
		layerObject.put(Constants.OVERVIEW, true);
		layerObject.put(Constants.PARAMS, createEmptyLayerParams());
		layerObject.put(Constants.MAX_EXTENT,createMaxExtent(getMaxExtentLeft(), getMaxExtentBottom(), getMaxExtentRight(), getMaxExtentTop(), "OpenLayers.Bounds"));			
		layerObject.put(Constants.DISPLAY_IN_LAYER_SWITCHER, true);
		
		ArrayNode arrayNode = om.createArrayNode();
		layerObject.put(Constants.WMS_LAYERS,arrayNode);
		
		return layerObject;
	}
	
	private ArrayNode createMapsMapLayers(ArrayList<String> listIdLayers) throws DCException {
		ArrayNode layers = om.createArrayNode();
		
		ObjectNode wmsLayer = createWMSLayerContainer();
		
		for (String idLayer : listIdLayers) {

			Gsc008LayerEntity layerEntity = gsc008Dao.load(Long.parseLong(idLayer));
			String layerJson = layerEntity.getJson();
			String layerName = getFieldValueFromJsonText(layerJson, Constants.LAYER_NAME_FIELD).replace(" ", "_");
			Long datasetId = Long.parseLong(getFieldValueFromJsonText(layerJson, Constants.DSET_ID_FIELD));
			boolean isQueryable = "true".equalsIgnoreCase(getFieldValueFromJsonText(layerJson, Constants.QUERYABLE)) ? true : false;
			boolean isBasic = "true".equalsIgnoreCase(getFieldValueFromJsonText(layerJson, Constants.BASIC_LAYER)) ? true : false;
			boolean isOverview = "true".equalsIgnoreCase(getFieldValueFromJsonText(layerJson, Constants.OVERVIEW_LAYER)) ? true : false;
			
			if(isBasic) {
				ObjectNode basicLayer = createBasicLayerContainer();
				((ArrayNode) basicLayer.path(Constants.WMS_LAYERS)).add(createWmsLayers(layerName,isBasic,isQueryable,datasetId,isOverview));	
				layers.add(basicLayer);
			} else {	
				((ArrayNode) wmsLayer.path(Constants.WMS_LAYERS)).add(createWmsLayers(layerName,isBasic,isQueryable,datasetId,isOverview));	
			}
			
			
		}
		
		if(wmsLayer.size() > 0 ) {
			layers.add(wmsLayer);
		}
		

		return layers;
	}

	private ObjectNode createLayerOptions() {
		ObjectNode layerOptionsNode = om.createObjectNode();
		layerOptionsNode.put(Constants.MAX_RESOLUTION, 90.31);
		layerOptionsNode.put(Constants.BUFFER, 0);
		return layerOptionsNode;
	}

	private ObjectNode createEmptyLayerParams() {
		ObjectNode layerParamsNode = om.createObjectNode();
		return layerParamsNode;
	}
	
	private ObjectNode createLayerParams() {
		ObjectNode layerParamsNode = om.createObjectNode();
		layerParamsNode.put(Constants.FORMAT, "image/png");
		layerParamsNode.put(Constants.REQUEST.toUpperCase(), "GetMap");
		layerParamsNode.put(Constants.STYLES, "");
		return layerParamsNode;
	}
	
	private ObjectNode createWmsLayers(String layerName,boolean baseLayer, boolean queryableLayer, Long datasetId, boolean isOverview) throws DCException {
		
		ObjectNode wmsLayerNode = om.createObjectNode();

		if(baseLayer) {
			wmsLayerNode.put(Constants.VISIBILITY, true);
		} else {
			wmsLayerNode.put(Constants.VISIBILITY, false);
		}
	
		wmsLayerNode.put(Constants.MIN_SCALE, 0);
		wmsLayerNode.put(Constants.MAX_SCALE, 0);
		wmsLayerNode.put(Constants.PHYSICAL_NAME,composePhysicalName(layerName));
		wmsLayerNode.put(Constants.LOGICAL_NAME,layerName);
		wmsLayerNode.put(Constants.QUERYABLE, queryableLayer);
		wmsLayerNode.put(Constants.OVERVIEW, isOverview);
		wmsLayerNode.put(Constants.CLASS_NAME, "MW.WMSLayer");
		ObjectNode groupWms = om.createObjectNode();
		if(isOverview) {
			groupWms.put(Constants.VISIBILITY, false);
		} else {
			groupWms.put(Constants.VISIBILITY, true);
		}
		groupWms.put(Constants.NAME, Constants.WMS_LAYERS);
		wmsLayerNode.put(Constants.GROUP, groupWms);
		
		
		ArrayNode fieldsNode = createFieldsNode(datasetId);
		wmsLayerNode.put(Constants.FIELDS,fieldsNode);
		
		return wmsLayerNode;
	}

	private ArrayNode createFieldsNode(Long datasetid) throws DCException {
		ArrayNode fieldsNode = om.createArrayNode();
		//retrieve the dataset associated to this layer
		Gsc007DatasetEntity dataset = gsc007Dao.load(datasetid);
		if (dataset == null) {
			// No dataset found with given parameters.
			throw new DCException(Constants.ER702);
		}
		
		try {
			//get the json element containing the table columns
			ArrayNode datasetColumns = getArrayNodeFromJsonText(dataset.getJson(),Constants.COLUMNS);
			
			for(int i = 0; i < datasetColumns.size(); i++) {
				ObjectNode fieldNode = JsonNodeFactory.instance.objectNode();
				
				fieldNode.put(Constants.PHYSICAL_NAME,getFieldValueFromJsonText(datasetColumns.get(i).toString(),Constants.NAME));
				fieldNode.put(Constants.LOGICAL_NAME,getFieldValueFromJsonText(datasetColumns.get(i).toString(),Constants.ALIAS));
				fieldNode.put(Constants.ID,new Long(i));
				fieldNode.put(Constants.VISIBILITY,true);
				
				String fieldType = getFieldValueFromJsonText(datasetColumns.get(i).toString(),Constants.TYPE);
				boolean isGeom = fieldType.equalsIgnoreCase(Constants.MULTIPOINT) || fieldType.equalsIgnoreCase(Constants.MULTILINESTRING) || fieldType.equalsIgnoreCase(Constants.MULTIPOLYGON) ? true : false;
				fieldNode.put(Constants.GEOM,isGeom);
				fieldNode.put(Constants.CLASS_NAME,Constants.WMS_LAYER_FIELD);
				
				fieldsNode.add(fieldNode);			
			}
		} catch (Exception e) {
			logger.error("get configuration service error", e);
			logger.error("get configuration service: unhandled error");
			throw new DCException(Constants.ER01);
		}
		return fieldsNode;
	}
	
	private ObjectNode createMapsConfigsProperties() throws DCException {
		ObjectNode sectionNode = om.createObjectNode();
		ArrayNode section = om.createArrayNode();
		section.add(createMainParamSection());
		sectionNode.put(Constants.SECTION, section);
		return sectionNode;
	}
	
	private ObjectNode createMainParamSection() throws DCException {
		ObjectNode mainParamSection = om.createObjectNode();
		ArrayNode param = om.createArrayNode();
		
		
		ObjectNode firstParamObject = om.createObjectNode();
		firstParamObject.put(Constants.NAME,Constants.MAX_ROWS);
		firstParamObject.put(Constants.VALUE,Long.parseLong(getFieldValueFromJsonText(applicationJson, Constants.MAX_ROWS)));
		param.add(firstParamObject);

		ObjectNode pagingRowsNumber = om.createObjectNode();
		pagingRowsNumber.put(Constants.NAME,Constants.PAGING_ROW_NUMBER);
		pagingRowsNumber.put(Constants.VALUE,Long.parseLong(getFieldValueFromJsonText(applicationJson, Constants.PAGING_ROW_NUMBER)));
		param.add(pagingRowsNumber);
		
		mainParamSection.put(Constants.PARAM, param);
		mainParamSection.put(Constants.NAME,Constants.GENERALE);

		return mainParamSection;
		
	}

	private ObjectNode createMaxExtent(double left, double bottom, double right, double top, String className) {
		ObjectNode maxExtent = om.createObjectNode();
		maxExtent.put(Constants.CLASS_NAME, className);
		maxExtent.put(Constants.LEFT, left);
		maxExtent.put(Constants.BOTTOM, bottom);
		maxExtent.put(Constants.RIGHT, right);
		maxExtent.put(Constants.TOP, top);

		return maxExtent;
	}

	private ObjectNode createTileOrigin(double lon, double lat, String className) {
		ObjectNode tileOrigin = om.createObjectNode();
		
		//XXX center node can be empty
//		tileOrigin.put(Constants.CLASS_NAME, className);
//		tileOrigin.put(Constants.LON, lon);
//		tileOrigin.put(Constants.LAT, lat);
		return tileOrigin;
	}
	
	private String getUrlGeoserver() throws DCException {
		return getFieldValueFromJsonText(getObjectFromJsonText(applicationJson, Constants.GEOSERVER_PARAMS), Constants.URL);
	}
	
	private String composeWMSUrl(String baseUrl,String workspaceName) {
		StringBuilder sb = new StringBuilder();
		sb.append(baseUrl);
		sb.append(workspaceName);
		sb.append("/"+Constants.WMS);
		return sb.toString();
	}
	
	private String composePhysicalName(String layerName) throws DCException {
		StringBuilder sb = new StringBuilder();
		sb.append(getFieldValueFromJsonText(applicationJson, Constants.APP_NAME_FIELD).replace(" ", "_"));
		sb.append(":");
		sb.append(layerName);
		return sb.toString();
	}
	
	private double getMaxExtentTop() throws DCException {
		return getNodeFromJsonText(applicationJson, Constants.MAX_EXTENT_LOWERCASE).get(Constants.TOP).asDouble();
	}
	
	private double getMaxExtentBottom() throws DCException {
		return getNodeFromJsonText(applicationJson, Constants.MAX_EXTENT_LOWERCASE).get(Constants.BOTTOM).asDouble();
	}
	
	private double getMaxExtentLeft() throws DCException {
		return getNodeFromJsonText(applicationJson, Constants.MAX_EXTENT_LOWERCASE).get(Constants.LEFT).asDouble();
	}
	
	private double getMaxExtentRight() throws DCException {
		return getNodeFromJsonText(applicationJson, Constants.MAX_EXTENT_LOWERCASE).get(Constants.RIGHT).asDouble();
	}
}
