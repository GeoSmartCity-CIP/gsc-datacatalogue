package it.sinergis.datacatalogue.services;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.apache.log4j.Logger;

import it.sinergis.datacatalogue.bean.jpa.Gsc006DatasourceEntity;
import it.sinergis.datacatalogue.bean.jpa.Gsc007DatasetEntity;
import it.sinergis.datacatalogue.bean.jpa.Gsc008LayerEntity;
import it.sinergis.datacatalogue.bean.jpa.Gsc009GrouplayerEntity;
import it.sinergis.datacatalogue.bean.jpa.Gsc010ApplicationEntity;
import it.sinergis.datacatalogue.common.Constants;
import it.sinergis.datacatalogue.exception.DCException;
import it.sinergis.datacatalogue.persistence.PersistenceConfig;
import it.sinergis.datacatalogue.persistence.PersistenceServiceProvider;
import it.sinergis.datacatalogue.persistence.commons.jpa.GenericJpaService;
import it.sinergis.datacatalogue.persistence.commons.jpa.JpaEnvironment;
import it.sinergis.datacatalogue.persistence.commons.jpa.JpaEnvironments;
import it.sinergis.datacatalogue.persistence.services.Gsc001OrganizationPersistence;
import it.sinergis.datacatalogue.persistence.services.Gsc002UserPersistence;
import it.sinergis.datacatalogue.persistence.services.Gsc003RolePersistence;
import it.sinergis.datacatalogue.persistence.services.Gsc006DatasourcePersistence;
import it.sinergis.datacatalogue.persistence.services.Gsc007DatasetPersistence;
import it.sinergis.datacatalogue.persistence.services.Gsc008LayerPersistence;
import it.sinergis.datacatalogue.persistence.services.Gsc009GrouplayerPersistence;
import it.sinergis.datacatalogue.persistence.services.Gsc010ApplicationPersistence;
import it.sinergis.datacatalogue.persistence.services.jpa.Gsc006DatasourcePersistenceJPA;
import it.sinergis.datacatalogue.persistence.services.jpa.Gsc007DatasetPersistenceJPA;
import it.sinergis.datacatalogue.persistence.services.jpa.Gsc008LayerPersistenceJPA;
import it.sinergis.datacatalogue.persistence.services.jpa.Gsc009GrouplayerPersistenceJPA;
import it.sinergis.datacatalogue.persistence.services.jpa.Gsc010ApplicationPersistenceJPA;

public class DeleteService extends ServiceCommons {

	public final String DATASOURCE_ID_NAME = "iddatasource";
	public final String ORGANIZATION_ID_NAME = "organization";
	public final String DATASET_ID_NAME = "iddataset";

	public final String LAYER_PATH = "'layers'";
	public final String LAYER_ID_ASSIGNMENT = "'idlayer' = '";

	public final String GROUP_PATH = "'groups'";
	public final String GROUP_ID_ASSIGNMENT = "'idgroup' = '";
	
	public final String USERS_PATH = "'users'";
	public final String USERS_ID_ASSIGNMENT = "'iduser' = '";
	
	public final String ASSIGNMENT_END = "'";

	/** Logger. */
	private static Logger logger;

	/** Organization DAO. */
	private Gsc001OrganizationPersistence gsc001Dao;
	
	/** Users DAO. */
	private Gsc002UserPersistence gsc002Dao;
	
	/** Roles DAO. */
	private Gsc003RolePersistence gsc003Dao;

	/** Datasource DAO. */
	private Gsc006DatasourcePersistence gsc006Dao;

	/** Dao dataset. */
	private Gsc007DatasetPersistence gsc007Dao;

	/** Dao layer. */
	private Gsc008LayerPersistence gsc008Dao;

	/** Dao layer. */
	private Gsc009GrouplayerPersistence gsc009Dao;

	/** Application layer. */
	private Gsc010ApplicationPersistence gsc010Dao;

	/** jpa environment */
	JpaEnvironment jpaEnvironment;

	/**
	 * Constructor
	 */
	public DeleteService() {
		logger = Logger.getLogger(this.getClass());

		jpaEnvironment = JpaEnvironments.getInstance().getJpaEnvironment(PersistenceConfig.JPA_PERSISTENCE_UNIT_NAME);

		gsc001Dao = PersistenceServiceProvider.getService(Gsc001OrganizationPersistence.class);
		gsc002Dao = PersistenceServiceProvider.getService(Gsc002UserPersistence.class);
		gsc003Dao = PersistenceServiceProvider.getService(Gsc003RolePersistence.class);
		gsc006Dao = PersistenceServiceProvider.getService(Gsc006DatasourcePersistence.class);
		gsc007Dao = PersistenceServiceProvider.getService(Gsc007DatasetPersistence.class);
		gsc008Dao = PersistenceServiceProvider.getService(Gsc008LayerPersistence.class);
		gsc009Dao = PersistenceServiceProvider.getService(Gsc009GrouplayerPersistence.class);
		gsc010Dao = PersistenceServiceProvider.getService(Gsc010ApplicationPersistence.class);
	}
	
	public void deleteUser(Long selfId) throws DCException {
		
		if (selfId != null) {
			EntityManager em = null;
			EntityTransaction transaction = null;
			
			try {
				em = jpaEnvironment.getEntityManagerFactory().createEntityManager();
				transaction = jpaEnvironment.openTransaction(em);
	
				gsc002Dao.deleteNoTrans(selfId, em);
				// need to handle deletion on entities that have this entity as
				// a list within their json
				// ROLES
				String queryRemoveLayerFromGroups = createDeleteFromListQuery(Constants.ROLE_TABLE_NAME,
						Constants.JSON_COLUMN_NAME, USERS_ID_ASSIGNMENT + selfId + ASSIGNMENT_END, USERS_PATH);
				gsc003Dao.deleteFromList(queryRemoveLayerFromGroups, em);
	
				// we need to explicitly handle deletion of tables that rely on this entity
				// NONE
	
				jpaEnvironment.commitTransaction(transaction);
			} catch (Exception e) {
				logger.error("Error in the delete service occurred. Transaction has been rolled back.", e);
				transaction.rollback();
				throw new DCException(Constants.ER16);
			} finally {
				em.close();
			}
		}
	}

	public void deleteApplication(String predecessorIdName, List<Long> predecessorsId, Long selfId, EntityManager em) throws DCException {

		// Application doesn't need to delete other tables, just itself.

		if (selfId != null) {
			boolean deleted = gsc010Dao.deleteNoTrans(selfId, em);

			if (!deleted) {
				logger.error("Error in the delete application occurred.");
				throw new DCException(Constants.ER16);
			}
		} else {
			try {
				for (Long id : predecessorsId) {
					Gsc010ApplicationPersistenceJPA applicationPersistencejpa = new Gsc010ApplicationPersistenceJPA();
					List<Object> retrievedGroupLayers = createSearchIdQuery(Constants.APPLICATION_TABLE_NAME,
							predecessorIdName, Constants.JSON_COLUMN_NAME, id, applicationPersistencejpa);
					for (Object retrievedGroup : retrievedGroupLayers) {
						if (retrievedGroup instanceof Gsc010ApplicationEntity) {
							gsc010Dao.deleteNoTrans(((Gsc010ApplicationEntity) retrievedGroup).getId(),em);
						}
					}
				}
			} catch (Exception e) {
				logger.error(e);
				throw new DCException(Constants.ER01);
			}
		}
	}

	public void deleteLayer(String predecessorIdName, List<Long> predecessorsId, Long selfId, EntityManager em)
			throws DCException {

		if (selfId != null) {
			EntityTransaction transaction = null;
			try {
				em = jpaEnvironment.getEntityManagerFactory().createEntityManager();
				transaction = jpaEnvironment.openTransaction(em);

				gsc008Dao.deleteNoTrans(selfId, em);
				// need to handle deletion on entities that have this entity as
				// a list within their json
				// GROUPLAYERS, APPLICATIONS
				String queryRemoveLayerFromGroups = createDeleteFromListQuery(Constants.GROUP_LAYER_TABLE_NAME,
						Constants.JSON_COLUMN_NAME, LAYER_ID_ASSIGNMENT + selfId + ASSIGNMENT_END, LAYER_PATH);
				int deletedFromLayerGroups = gsc009Dao.deleteFromList(queryRemoveLayerFromGroups, em);
				System.out.print("deleted from layergroups"+deletedFromLayerGroups+"\n");
				String queryRemoveLayerFromApplication = createDeleteFromListQuery(Constants.APPLICATION_TABLE_NAME,
						Constants.JSON_COLUMN_NAME, LAYER_ID_ASSIGNMENT + selfId + ASSIGNMENT_END, LAYER_PATH);
				int deletedFromApplication = gsc010Dao.deleteFromList(queryRemoveLayerFromApplication, em);
				System.out.print("deleted from application"+deletedFromApplication+"\n");
				
				// we need to explicitly handle deletion of tables that rely on
				// this entity
				List<Long> predIds = new ArrayList<Long>();
				predIds.add(selfId);

				// TODO understand table relationships to delete cascade

				jpaEnvironment.commitTransaction(transaction);
			} catch (Exception e) {
				logger.error("Error in the delete service occurred. Transaction has been rolled back.", e);
				transaction.rollback();
				throw new DCException(Constants.ER16);
			} finally {
				em.close();
			}
		} else {
			try {
				List<Long> deletedSelfId = new ArrayList<Long>();
				for (Long id : predecessorsId) {
					Gsc008LayerPersistenceJPA layerPersistencejpa = new Gsc008LayerPersistenceJPA();
					List<Object> retrievedLayers = createSearchIdQuery(Constants.LAYER_TABLE_NAME, predecessorIdName,
							Constants.JSON_COLUMN_NAME, id, layerPersistencejpa);
					for (Object retrievedLayer : retrievedLayers) {
						if (retrievedLayer instanceof Gsc008LayerEntity) {
							gsc008Dao.deleteNoTrans(((Gsc008LayerEntity) retrievedLayer).getId(),em);

							// need to handle deletion on entities that have
							// this entity as a list within their json
							// GROUPLAYERS, APPLICATIONS
							String queryRemoveLayerFromGroups = createDeleteFromListQuery(
									Constants.GROUP_LAYER_TABLE_NAME,
									Constants.JSON_COLUMN_NAME, LAYER_ID_ASSIGNMENT
											+ ((Gsc008LayerEntity) retrievedLayer).getId() + ASSIGNMENT_END,
									LAYER_PATH);
							int deletedFromLayerGroups = gsc009Dao.deleteFromList(queryRemoveLayerFromGroups, em);
							System.out.print("deleted from layergroups"+deletedFromLayerGroups+"\n");
							
							String queryRemoveLayerFromApplication = createDeleteFromListQuery(
									Constants.APPLICATION_TABLE_NAME,
									Constants.JSON_COLUMN_NAME, LAYER_ID_ASSIGNMENT
											+ ((Gsc008LayerEntity) retrievedLayer).getId() + ASSIGNMENT_END,
									LAYER_PATH);
							int deletedFromApplication = gsc010Dao.deleteFromList(queryRemoveLayerFromApplication, em);
							System.out.print("deleted from application"+deletedFromApplication+"\n");
							
							deletedSelfId.add(((Gsc008LayerEntity) retrievedLayer).getId());
						}
					}
				}
				// CASCADE DELETIONS
				// TODO understand table relationships
			} catch (Exception e) {
				logger.error(e);
				throw new DCException(Constants.ER01);
			}
		}
	}

	public void deleteDatasource(String predecessorIdName, List<Long> predecessorsId, Long selfId, EntityManager em)
			throws DCException {

		if (selfId != null) {
			EntityTransaction transaction = null;
			try {
				em = jpaEnvironment.getEntityManagerFactory().createEntityManager();
				transaction = jpaEnvironment.openTransaction(em);

				gsc006Dao.deleteNoTrans(selfId, em);

				// we need to explicitly handle deletion of tables that rely on
				// this entity
				List<Long> predIds = new ArrayList<Long>();
				predIds.add(selfId);

				// DATASET
				deleteDataset(DATASOURCE_ID_NAME, predIds, null, em);

				jpaEnvironment.commitTransaction(transaction);
			} catch (Exception e) {
				logger.error("Error in the delete service occoured. Transaction has been rolled back.", e);
				transaction.rollback();
				throw new DCException(Constants.ER16);
			} finally {
				em.close();
			}
		} else {
			try {
				List<Long> deletedSelfId = new ArrayList<Long>();
				for (Long id : predecessorsId) {
					Gsc006DatasourcePersistenceJPA datasourcePersistencejpa = new Gsc006DatasourcePersistenceJPA();
					List<Object> retrievedDatasources = createSearchIdQuery(Constants.DATASOURCE_TABLE_NAME,
							predecessorIdName, Constants.JSON_COLUMN_NAME, id, datasourcePersistencejpa);
					for (Object retrievedDatasource : retrievedDatasources) {
						if (retrievedDatasource instanceof Gsc006DatasourceEntity) {
							gsc006Dao.deleteNoTrans(((Gsc006DatasourceEntity) retrievedDatasource).getId(),em);
							deletedSelfId.add(((Gsc006DatasourceEntity) retrievedDatasource).getId());
						}
					}
				}
				// CASCADE DELETIONS
				// DATASET
				deleteDataset(DATASOURCE_ID_NAME, deletedSelfId, null, em);
			} catch (Exception e) {
				logger.error(e);
				throw new DCException(Constants.ER01);
			}
		}
	}

	public void deleteGroupLayer(String predecessorIdName, List<Long> predecessorsId, Long selfId, EntityManager em)
			throws DCException {

		if (selfId != null) {
			EntityTransaction transaction = null;
			try {
				em = jpaEnvironment.getEntityManagerFactory().createEntityManager();
				transaction = jpaEnvironment.openTransaction(em);

				gsc009Dao.deleteNoTrans(selfId, em);

				// need to handle deletion on entities that have this entity as
				// a list within their json
				// APPLICATIONS
				String queryRemoveGroupsFromApplication = createDeleteFromListQuery(Constants.APPLICATION_TABLE_NAME,
						Constants.JSON_COLUMN_NAME, GROUP_ID_ASSIGNMENT + selfId + ASSIGNMENT_END, GROUP_PATH);
				gsc010Dao.deleteFromList(queryRemoveGroupsFromApplication, em);

				// we need to explicitly handle deletion of tables that rely on
				// this entity
				List<Long> predIds = new ArrayList<Long>();
				predIds.add(selfId);

				// XXX
				// Nothing apparently

				jpaEnvironment.commitTransaction(transaction);
			} catch (Exception e) {
				logger.error("Error in the delete service occoured. Transaction has been rolled back.", e);
				transaction.rollback();
				throw new DCException(Constants.ER16);
			} finally {
				em.close();
			}
		} else {
			try {
				List<Long> deletedSelfId = new ArrayList<Long>();
				for (Long id : predecessorsId) {
					Gsc009GrouplayerPersistenceJPA grouplayerPersistencejpa = new Gsc009GrouplayerPersistenceJPA();
					List<Object> retrievedGroupLayers = createSearchIdQuery(Constants.GROUP_LAYER_TABLE_NAME,
							predecessorIdName, Constants.JSON_COLUMN_NAME, id, grouplayerPersistencejpa);
					for (Object retrievedGroup : retrievedGroupLayers) {
						if (retrievedGroup instanceof Gsc009GrouplayerEntity) {
							gsc009Dao.deleteNoTrans(((Gsc009GrouplayerEntity) retrievedGroup).getId(),em);

							// need to handle deletion on entities that have
							// this entity as a list within their json
							// APPLICATIONS
							String queryRemoveGroupsFromApplication = createDeleteFromListQuery(
									Constants.APPLICATION_TABLE_NAME, Constants.JSON_COLUMN_NAME,
									GROUP_ID_ASSIGNMENT + ((Gsc009GrouplayerEntity) retrievedGroup).getId()
											+ ASSIGNMENT_END,
									GROUP_PATH);
							gsc010Dao.deleteFromList(queryRemoveGroupsFromApplication, em);

							deletedSelfId.add(((Gsc009GrouplayerEntity) retrievedGroup).getId());
						}
					}
				}
				// CASCADE DELETIONS
				// XXX
				// Nothing apparently
			} catch (Exception e) {
				logger.error(e);
				throw new DCException(Constants.ER01);
			}
		}
	}

	public void deleteDataset(String predecessorIdName, List<Long> predecessorsId, Long selfId, EntityManager em)
			throws DCException {

		if (selfId != null) {
			EntityTransaction transaction = null;
			try {
				em = jpaEnvironment.getEntityManagerFactory().createEntityManager();
				transaction = jpaEnvironment.openTransaction(em);

				gsc007Dao.deleteNoTrans(selfId, em);

				// we need to explicitly handle deletion of tables that rely on
				// this entity
				List<Long> predIds = new ArrayList<Long>();
				predIds.add(selfId);

				// LAYERS
				deleteLayer(DATASET_ID_NAME, predIds, null, em);

				jpaEnvironment.commitTransaction(transaction);
			} catch (Exception e) {
				logger.error("Error in the delete service occoured. Transaction has been rolled back.", e);
				transaction.rollback();
				throw new DCException(Constants.ER16);
			} finally {
				em.close();
			}
		} else {
			try {
				List<Long> deletedSelfId = new ArrayList<Long>();
				for (Long id : predecessorsId) {
					Gsc007DatasetPersistenceJPA datasetPersistencejpa = new Gsc007DatasetPersistenceJPA();
					List<Object> retrievedDatasets = createSearchIdQuery(Constants.DATASETS_TABLE_NAME,
							predecessorIdName, Constants.JSON_COLUMN_NAME, id, datasetPersistencejpa);
					for (Object retrievedDataset : retrievedDatasets) {
						if (retrievedDataset instanceof Gsc007DatasetEntity) {
							gsc007Dao.deleteNoTrans(((Gsc007DatasetEntity) retrievedDataset).getId(),em);
							deletedSelfId.add(((Gsc007DatasetEntity) retrievedDataset).getId());
						}
					}
				}
				// CASCADE DELETIONS
				// LAYERS
				deleteLayer(DATASET_ID_NAME, deletedSelfId, null, em);
			} catch (Exception e) {
				logger.error(e);
				throw new DCException(Constants.ER01);
			}
		}
	}

	public void deleteOrganization(String predecessorIdName, List<Long> predecessorsId, Long selfId)
			throws DCException {

		// Organization doesn't depend on any other entity (no else clause)
		if (selfId != null) {
			EntityManager em = null;
			EntityTransaction transaction = null;
			try {
				em = jpaEnvironment.getEntityManagerFactory().createEntityManager();
				transaction = jpaEnvironment.openTransaction(em);

				gsc001Dao.deleteNoTrans(selfId, em);

				List<Long> predIds = new ArrayList<Long>();
				predIds.add(selfId);

				// we need to explicitly handle deletion of tables that rely on
				// this entity
				// by calling delete methods of the following:

				// DATASET
				deleteDatasource(ORGANIZATION_ID_NAME, predIds, null, em);
				// GROUPLAYER
				deleteGroupLayer(ORGANIZATION_ID_NAME, predIds, null, em);
				// APPLICATION
				deleteApplication(ORGANIZATION_ID_NAME, predIds, null, em);
				// TODO function, role, user

				jpaEnvironment.commitTransaction(transaction);
			} catch (Exception e) {
				logger.error("Error in the delete service occoured. Transaction has been rolled back.", e);
				transaction.rollback();
				throw new DCException(Constants.ER16);
			} finally {
				em.close();
			}
		}
	}

	/**
	 * Returns all ids of the table that are linked to the given id.
	 * 
	 * @param tablename
	 * @param idName
	 * @param jsonColumnName
	 * @param id
	 * @param persistenceClass
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<Object> createSearchIdQuery(String tablename, String idName, String jsonColumnName, Long id,
			GenericJpaService genericJPA) {
		StringBuilder sb = new StringBuilder();
		sb.append("select * from ");
		sb.append(tablename);
		sb.append(" where ");
		sb.append(jsonColumnName);
		sb.append("->>'");
		sb.append(idName);
		sb.append("' = '");
		sb.append(id);
		sb.append("'");
		String queryText = sb.toString();

		return genericJPA.loadByNativeQuery(queryText);

	}

	/**
	 * Generic query creation for deletion of elements within json arrays
	 * 
	 * @param tablename
	 *            the table containing the json to be update
	 * @param jsonColumnName
	 *            the name of the json column in that table
	 * @param idElement
	 *            the id expression that indicates all the elements that have to
	 *            be deleted (e.g. 'idlayer' = '3')
	 * @param arrayPath
	 *            the path to the array within the json (e.g. 'layers' if the
	 *            root element of the array contains an element
	 *            'layers':[{elem1},{elem2},...])
	 * @return
	 */
	private String createDeleteFromListQuery(String tablename, String jsonColumnName, String idElement,
			String arrayPath) {
		StringBuilder sb = new StringBuilder();

		sb.append("WITH rownumber AS (");
		sb.append("SELECT id, CAST((row_number() OVER (PARTITION by id)) AS integer)-1 AS rn, obj, t.json newjson ");
		sb.append("FROM " + tablename + " t, jsonb_array_elements(t." + jsonColumnName + "->" + arrayPath + ") obj) ");
		sb.append("update " + tablename + " t set json = (");
		sb.append("select rownumber.newjson - " + arrayPath + " || jsonb_build_object(" + arrayPath
				+ ",((rownumber.newjson->" + arrayPath + ") - rownumber.rn))");
		sb.append(" from rownumber where rownumber.obj->>" + idElement + " AND rownumber.id = t.id)");
		sb.append("from rownumber where rownumber.id = t.id AND rownumber.obj->>" + idElement);
		System.out.println(sb.toString());
		return sb.toString();
	}
}