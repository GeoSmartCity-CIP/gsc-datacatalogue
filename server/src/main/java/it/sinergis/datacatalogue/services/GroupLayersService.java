package it.sinergis.datacatalogue.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import it.sinergis.datacatalogue.bean.jpa.Gsc008LayerEntity;
import it.sinergis.datacatalogue.bean.jpa.Gsc009GrouplayerEntity;
import it.sinergis.datacatalogue.common.Constants;
import it.sinergis.datacatalogue.exception.DCException;
import it.sinergis.datacatalogue.persistence.PersistenceServiceProvider;
import it.sinergis.datacatalogue.persistence.services.Gsc008LayerPersistence;
import it.sinergis.datacatalogue.persistence.services.Gsc009GrouplayerPersistence;

public class GroupLayersService extends ServiceCommons {

	/** Logger. */
	private static Logger logger;

	/** Gsc009Grouplayer DAO. */
	Gsc009GrouplayerPersistence groupLayerPersistence;

	/** Gsc008Layer DAO. */
	Gsc008LayerPersistence layerPersistence;
	
	/**
	 * Constructor
	 */
	public GroupLayersService() {
		logger = Logger.getLogger(this.getClass());
		groupLayerPersistence = PersistenceServiceProvider.getService(Gsc009GrouplayerPersistence.class);
		layerPersistence = PersistenceServiceProvider.getService(Gsc008LayerPersistence.class);
	}
	public static String RESPONSE_JSON_STATUS_DONE =  "{Status:'Done', Description:'Ok'}";
	public static String RESPONSE_JSON_LIST_GROUPLAYER = "{groups:[{groupname:'Group1',layers:[{layername:'Layer1'},{layername:'Layer2'}]},{groupname:'Group2',layers:[{layername:'Layer3'},{layername:'Layer4'}]}]}";
	
	/**
	 * Create a new Group layer record into database
	 * 
	 * @param req
	 *            new Group Layer data (JSON format)
	 * @return operation's result (JSON format)
	 */
	public String createGroupLayer(String req) {
		try {
			// preliminary checks on the request parameters
			preliminaryChecks(req, Constants.CREATE_GROUP_LAYER);

			// check if the inserted organization id exists in the organization
			// table. If not throws exception
			checkIdOrganizationValid(req);
			// check if there's another groupLayer already saved with the same
			// name
			Gsc009GrouplayerEntity groupLayer = getGroupLayerObject(req);

			// if no results found -> add new record
			if (groupLayer == null) {
				Gsc009GrouplayerEntity gl = new Gsc009GrouplayerEntity();
				gl.setJson(req);

				gl = groupLayerPersistence.save(gl);

				logger.info("Group layer succesfully created");
				logger.info(req);
				return createJsonStatus(Constants.STATUS_DONE, Constants.GROUP_LAYER_CREATED, gl.getId(), req);

				// otherwise an error message will be return
			} else {
				DCException rpe = new DCException(Constants.ER901, req);
				return rpe.returnErrorString();
			}

		} catch (DCException rpe) {
			return rpe.returnErrorString();
		} catch (NumberFormatException nfe) {
			logger.error("inserted id parameter is not a number", nfe);
			DCException rpe = new DCException(Constants.ER12, req);
			return rpe.returnErrorString();
		} catch (Exception e) {
			logger.error("createGroupLayer service error", e);
			DCException rpe = new DCException(Constants.ER01, req);
			logger.error("createGroupLayer service: unhandled error " + rpe.returnErrorString());

			return rpe.returnErrorString();
		}
	}
	
	/**
	 * Delete existing grouplayer
	 * 
	 * @param req
	 *            grouplayer to delete on json form
	 * @return status on json form
	 */
	public String deleteGroupLayer(String req) {
		try {
			// preliminary checks on the request parameters
			preliminaryChecks(req, Constants.DELETE_GROUP_LAYER);

			// check if there is a grouplayer with that id
			Gsc009GrouplayerEntity grouplayer = getGroupLayerObjectById(
					Long.parseLong(getFieldValueFromJsonText(req, Constants.GROUP_LAYER_ID_FIELD)));

			// if results found -> delete record
			if (grouplayer != null) {
				DeleteService deleteService = new DeleteService();
				deleteService.deleteGroupLayer(null, null, grouplayer.getId(),null);

				logger.info("Group Layer succesfully deleted");
				logger.info(req);
				return createJsonStatus(Constants.STATUS_DONE, Constants.GROUP_LAYER_DELETED, null, req);

				// otherwise error
			} else {
				DCException rpe = new DCException(Constants.ER902,req);
				return rpe.returnErrorString();
			}

		} catch (DCException rpe) {
			return rpe.returnErrorString();
		} catch (NumberFormatException nfe) {
			logger.error("inserted id parameter is not a number", nfe);
			DCException rpe = new DCException(Constants.ER12, req);
			return rpe.returnErrorString();
		} catch (Exception e) {
			logger.error("delete deleteGroupLayer service error", e);
			DCException rpe = new DCException(Constants.ER01, req);
			logger.error("deleteGroupLayer service: unhandled error " + rpe.returnErrorString());

			return rpe.returnErrorString();
		}
	}
	
	/**
	 * finds grouplayers matching the request specifications.
	 * 
	 * @param req
	 * @return
	 */
	public String listGroupLayer(String req){
		try {
			String query = null;
			List<Gsc009GrouplayerEntity> grouplayers = new ArrayList<Gsc009GrouplayerEntity>();

			if (!req.equals("{}")) {
				// preliminary checks on the request parameters
				preliminaryChecks(req, Constants.LIST_GROUP_LAYER);

				// There are two research modes: by id and by organization
				// If both parameters are found in the request an error will be
				// thrown.
				// If a research by organization is performed, users may specify
				// a partial or total grouplayername
				// in order to refine the content of the response.
				String organizationIdParameter = getFieldValueFromJsonText(req, Constants.ORG_FIELD);
				String grouplayerNameParameter = getFieldValueFromJsonText(req, Constants.GROUP_LAYER_NAME_FIELD);
				String grouplayerIdParameter = getFieldValueFromJsonText(req, Constants.GROUP_LAYER_ID_FIELD);
				if (grouplayerIdParameter != null
						&& (grouplayerNameParameter != null || organizationIdParameter != null)) {
					logger.error(
							"Incorrect parameters: perform a request either by grouplayerid or by organizationid (and grouplayername). Both parameters are not allowed at the same time");
					throw new DCException(Constants.ER903, req);
				}
				// if the idlayer parameter is in the request the research
				// will be done by id.
				if (grouplayerIdParameter != null) {
					Gsc009GrouplayerEntity grouplayerFoundById = getGroupLayerObjectById(
							Long.parseLong(grouplayerIdParameter));
					if (grouplayerFoundById != null) {
						grouplayers.add(grouplayerFoundById);
					}
					// otherwise the research is based on the organization
					// parameter.
				} else if (organizationIdParameter != null) {
					String queryText = "'" + Constants.ORG_FIELD + "' =  '" + organizationIdParameter + "'";

					// user may additionally specify a partial or complete
					// grouplayername to refine the research process.
					if (grouplayerNameParameter != null) {
						queryText += " AND '" + Constants.GROUP_LAYER_NAME_FIELD + "' ILIKE '%"
								+ getKeyFromJsonText(req, Constants.GROUP_LAYER_NAME_FIELD) + "%'";
					}

					query = createQuery(queryText, Constants.GROUP_LAYER_TABLE_NAME, Constants.JSON_COLUMN_NAME,
							"select");
					grouplayers = groupLayerPersistence.getGroupLayers(query);

				} else {
					logger.error(
							"Incorrect parameters: either idgrouplayer or organization parameters should be specified.");
					throw new DCException(Constants.ER904, req);
				}
			} else {
				grouplayers = groupLayerPersistence.loadAll();
			}

			logger.info("Group Layers found: " + grouplayers.size());
			logger.info(req);
			if (grouplayers.size() == 0) {
				logger.error("No results found.");
				throw new DCException(Constants.ER13, req);
			}

			ObjectMapper mapper = new ObjectMapper();
			ObjectNode root = JsonNodeFactory.instance.objectNode();
			ArrayNode grouplayerNodeList = JsonNodeFactory.instance.arrayNode();
			for (Gsc009GrouplayerEntity grouoplayer : grouplayers) {
				ObjectNode grouplayerBasic = (ObjectNode) mapper.readTree(grouoplayer.getJson());
				boolean hasLayers = isParameterInJson(grouoplayer.getJson(), Constants.LAYERS);
				ArrayNode layersIdWithinGroup = JsonNodeFactory.instance.arrayNode();
				List<String> idList = new ArrayList<String>();
				if(hasLayers) {
					layersIdWithinGroup = (ArrayNode) grouplayerBasic.path(Constants.LAYERS);
					//get layers name from id
					
					//get all layers with the found ids
									
					for(int i = 0; i < layersIdWithinGroup.size(); i++) {
						idList.add(((ObjectNode) layersIdWithinGroup.get(i)).get(Constants.LAYER_ID_FIELD).asText());
					}
				}
				
				String getLayersNameQuery = loadObjectFromIdList(Constants.LAYER_TABLE_NAME,idList);
				if(StringUtils.isNotEmpty(getLayersNameQuery)) {
					List<Gsc008LayerEntity> layers = layerPersistence.getLayers(getLayersNameQuery);
					//build a map <layerid, layername>
					Map<Long,String> layerIdToName = new HashMap<Long,String>();
					for(Gsc008LayerEntity layer : layers) {
						layerIdToName.put(layer.getId(), getFieldValueFromJsonText(layer.getJson(), Constants.LAYER_NAME_FIELD));
					}
					
					ArrayNode layersIdAndNameWithinGroup = JsonNodeFactory.instance.arrayNode();
					//for each layer in the json response
					for(int i = 0; i < layersIdWithinGroup.size(); i++) {
						ObjectNode layerIdWithinGroup = (ObjectNode) layersIdWithinGroup.get(i);
						Long layerId =  layerIdWithinGroup.get(Constants.LAYER_ID_FIELD).asLong();
						//search for its name in the map and add it to the id value
						String layerName = layerIdToName.get(layerId);
						ObjectNode layerIdAndNameWithinGroup = JsonNodeFactory.instance.objectNode();
						layerIdAndNameWithinGroup.put(Constants.LAYER_ID_FIELD,layerId);
						layerIdAndNameWithinGroup.put(Constants.LAYER_NAME_FIELD,layerName);
						//add the new object to the list
						layersIdAndNameWithinGroup.add(layerIdAndNameWithinGroup);	
					}
					//replace the old layer list only containing ids with the new one containing names as well
					grouplayerBasic.put(Constants.LAYERS, layersIdAndNameWithinGroup);
				}
				
				grouplayerBasic.put(Constants.ID, grouoplayer.getId());
				grouplayerBasic.remove(Constants.DESCRIPTION_FIELD);
				grouplayerBasic.remove(Constants.ORG_FIELD);
				grouplayerNodeList.add(grouplayerBasic);
			}
			root.put(Constants.GROUP_LAYER, grouplayerNodeList);
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
	 * Assign layers to existing group
	 * 
	 * @param req
	 *           idgroup and list of layers to be updated in that group as json
	 * @return status on json form
	 */
	public String assignLayerToGroup(String req) {
		try {
			// preliminary checks on the request parameters
			preliminaryChecks(req, Constants.ASSIGN_LAYER_TO_GROUP);
			
			// Find the group thorugh its id in the request
			// check if there is a grouplayer with that id
			Gsc009GrouplayerEntity grouplayer = getGroupLayerObjectById(
					Long.parseLong(getFieldValueFromJsonText(req, Constants.GROUP_LAYER_ID_FIELD)));
			
			if(grouplayer == null) {
				logger.error(
						"Incorrect parameters: requested group layer does not exist.");
				throw new DCException(Constants.ER902, req);
			}
			
			ObjectMapper mapper = new ObjectMapper();		
			ObjectNode grouplayerToBeUpdated = (ObjectNode) mapper.readTree(grouplayer.getJson());
			ObjectNode requestJson = (ObjectNode) mapper.readTree(req);
			ArrayNode requestLayers =  (ArrayNode) requestJson.findValue(Constants.LAYERS);
			
			//get all the layers id involved
			List<Long> idlayers = getLayersIdFromRequest(requestLayers);
			
			//create the request
			String query = createCheckLayersRequest(idlayers,Long.parseLong(getFieldValueFromJsonText(grouplayer.getJson(),Constants.ORG_FIELD)));
			//execute the request
			Long resultNumber = layerPersistence.countInId(query);
			//if at least one of the specified layers does not exist throw error
			//if the countnumber is less than the id list size one or more records was not found
			if(resultNumber < idlayers.size()) {
				logger.error(
						"Incorrect parameters: one of the requested layers cannot be assigned to the group because it does not exist or it's not associated with the same organization.");
				throw new DCException(Constants.ER906, req);
			}

			//assign the layer list
			grouplayerToBeUpdated.put(Constants.LAYERS, requestLayers);

			String updatedJson = mapper.writeValueAsString(grouplayerToBeUpdated);
			
			grouplayer.setJson(updatedJson);
			groupLayerPersistence.save(grouplayer);
			
			logger.info("layers succesfully assigned to group.");
			logger.info(req);
			return createJsonStatus(Constants.STATUS_DONE, Constants.GROUP_LAYER_ASSIGNED, null, req);
		} catch (DCException rpe) {
			return rpe.returnErrorString();
		} catch (NumberFormatException nfe) {
			logger.error("inserted id parameter is not a number", nfe);
			DCException rpe = new DCException(Constants.ER12, req);
			return rpe.returnErrorString();
		} catch (Exception e) {
			logger.error("assign layers  to group service error", e);
			DCException rpe = new DCException(Constants.ER01, req);
			logger.error("assign layers  to group service: unhandled error " + rpe.returnErrorString());

			return rpe.returnErrorString();
		}
	}

	private List<Long> getLayersIdFromRequest(ArrayNode layerList) {
		List<Long> layersId = new ArrayList<Long>();
		
		for(int i = 0; i < layerList.size(); i++) {
			JsonNode requestedLayerId = layerList.get(i);
			JsonNode layerIdNode = requestedLayerId.findValue(Constants.LAYER_ID_FIELD);
			layersId.add(layerIdNode.asLong());
		}
		
		return layersId;
	}
	
	/**
	 * Creates a query which
	 * Checks that the layers to be added exist and belong to the same organization as the layerGroup thay are being added to
	 * 
	 * @param ids
	 * @param organizationId
	 * @return
	 */
	private String createCheckLayersRequest(List<Long> ids, Long organizationId) {
		StringBuilder sb = new StringBuilder();
		
		//This condition counts the existing layers count matches the count on the layers that were passed as arguments
		sb.append("select count(*) from ");
		sb.append(Constants.LAYER_TABLE_NAME);
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
		
		sb.append(" AND ");
		sb.append(Constants.ID);
		sb.append(" IN (");
		//This condition checks that the id count only matches layers belonging to the same groupLayer organization 
//				select lyrT.id
//				from gscdatacatalogue.gsc_001_organization orgT 
//				inner join gscdatacatalogue.gsc_006_datasource dsT
//				on orgT.id = CAST((dsT.json->>'organization') AS integer)
//				inner join gscdatacatalogue.gsc_007_dataset dstT
//				on dsT.id = CAST((dstT.json->>'iddatasource') AS integer)
//				inner join gscdatacatalogue.gsc_008_layer lyrT
//				on dstT.id = CAST((lyrT.json->>'iddataset') AS integer)
//				where orgT.id = THIS_GROUP_ORGANIZATIONID
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
		sb.append(organizationId);
		sb.append(")");

		
		return sb.toString();
	}
	
	/**
	 * Retrieves the layer group given a layergroup name.
	 * 
	 * @param json
	 * @return
	 * @throws RPException
	 */
	private Gsc009GrouplayerEntity getGroupLayerObject(String json) throws DCException {
		ArrayList<String> params = new ArrayList<String>();
		params.add(Constants.GROUP_LAYER_NAME_FIELD);
		params.add(Constants.ORG_FIELD);
		return (Gsc009GrouplayerEntity) getRowObject(json, Constants.GROUP_LAYER_TABLE_NAME, params,
				groupLayerPersistence);
	}
	
	/**
	 * Retrieves the grouplayer given its Id.
	 * 
	 * @param json
	 * @return
	 * @throws RPException
	 */
	private Gsc009GrouplayerEntity getGroupLayerObjectById(Long id) throws DCException {
		return (Gsc009GrouplayerEntity) groupLayerPersistence.load(id);
	}
	
}
