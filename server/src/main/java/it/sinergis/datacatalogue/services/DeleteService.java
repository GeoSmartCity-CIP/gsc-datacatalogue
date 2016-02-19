package it.sinergis.datacatalogue.services;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.apache.log4j.Logger;

import it.sinergis.datacatalogue.bean.jpa.Gsc006DatasourceEntity;
import it.sinergis.datacatalogue.bean.jpa.Gsc007DatasetEntity;
import it.sinergis.datacatalogue.common.Constants;
import it.sinergis.datacatalogue.exception.DCException;
import it.sinergis.datacatalogue.persistence.PersistenceConfig;
import it.sinergis.datacatalogue.persistence.PersistenceServiceProvider;
import it.sinergis.datacatalogue.persistence.commons.jpa.GenericJpaService;
import it.sinergis.datacatalogue.persistence.commons.jpa.JpaEnvironment;
import it.sinergis.datacatalogue.persistence.commons.jpa.JpaEnvironments;
import it.sinergis.datacatalogue.persistence.services.Gsc006DatasourcePersistence;
import it.sinergis.datacatalogue.persistence.services.Gsc007DatasetPersistence;
import it.sinergis.datacatalogue.persistence.services.jpa.Gsc006DatasourcePersistenceJPA;
import it.sinergis.datacatalogue.persistence.services.jpa.Gsc007DatasetPersistenceJPA;

public class DeleteService extends ServiceCommons {

	public final String DATASOURCE_ID_NAME = "iddatasource";
	public final String DATASET_ID_NAME = "";
	
	/** Logger. */
	private static Logger logger;
	
	/** Gsc001Datasource  DAO. */
	private Gsc006DatasourcePersistence gsc006Dao;
	
	/** Dao dataset. */
	private Gsc007DatasetPersistence gsc007Dao;
	
	/** jpa environment*/
	JpaEnvironment jpaEnvironment;
	
	/**
	 * Constructor
	 */
	public DeleteService() {
		logger = Logger.getLogger(this.getClass());		
		
		jpaEnvironment = JpaEnvironments.getInstance().getJpaEnvironment(PersistenceConfig.JPA_PERSISTENCE_UNIT_NAME);
		
		gsc006Dao = PersistenceServiceProvider.getService(Gsc006DatasourcePersistence.class);
		gsc007Dao = PersistenceServiceProvider.getService(Gsc007DatasetPersistence.class);
	}

	public void deleteDatasource(String predecessorIdName, List<Long> predecessorsId,Long selfId) throws DCException {
		
		if(selfId != null) {
			EntityManager em = null;
			EntityTransaction transaction = null;
			try {
				em = jpaEnvironment.getEntityManagerFactory().createEntityManager();
				transaction = jpaEnvironment.openTransaction(em);
			
				gsc006Dao.deleteNoTrans(selfId,em);
				
				//we need to explicitly handle deletion of tables that rely on this entity
				List<Long> predIds = new ArrayList<Long>();
				predIds.add(selfId);
				
				//DATASET
				deleteDataset(DATASOURCE_ID_NAME,predIds,null);
				
				jpaEnvironment.commitTransaction(transaction);
			} catch(Exception e) {
				transaction.rollback();
			} finally {
				em.close();
			}
		} else {
			try {
				List<Long> deletedSelfId = new ArrayList<Long>();
				for(Long id : predecessorsId) {
					Gsc006DatasourcePersistenceJPA datasourcePersistencejpa = new Gsc006DatasourcePersistenceJPA();
					List<Object> retrievedDatasources = createSearchIdQuery(Constants.DATASOURCE_TABLE_NAME,predecessorIdName,Constants.JSON_COLUMN_NAME,id,datasourcePersistencejpa);
					for(Object retrievedDatasource : retrievedDatasources) {
						if(retrievedDatasources instanceof Gsc006DatasourceEntity) {
							gsc006Dao.delete(((Gsc006DatasourceEntity) retrievedDatasource).getId());
							deletedSelfId.add(((Gsc006DatasourceEntity) retrievedDatasource).getId());
						}
					}
				}
				//CASCADE DELETIONS
				//DATASET
				deleteDataset(DATASOURCE_ID_NAME,deletedSelfId,null);
			}catch(Exception e) {
				logger.error(e);
				throw new DCException("ER01");
			}
		}		
	}
	
	public void deleteDataset(String predecessorIdName, List<Long> predecessorsId,Long selfId) throws DCException {
			
		if(selfId != null) {
			EntityManager em = null;
			EntityTransaction transaction = null;
			try {
				em = jpaEnvironment.getEntityManagerFactory().createEntityManager();
				transaction = jpaEnvironment.openTransaction(em);
				
				gsc007Dao.delete(selfId);
				//chiamate in cascata
				//TODO
				
				jpaEnvironment.commitTransaction(transaction);
			} catch(Exception e) {
				transaction.rollback();
			} finally {
				em.close();
			}
		} else {
			try {
				List<Long> deletedSelfId = new ArrayList<Long>();
				for(Long id : predecessorsId) {
					Gsc007DatasetPersistenceJPA datasetPersistencejpa = new Gsc007DatasetPersistenceJPA();
					List<Object> retrievedDatasets = createSearchIdQuery(Constants.DATASETS_TABLE_NAME,predecessorIdName,Constants.JSON_COLUMN_NAME,id,datasetPersistencejpa);
					for(Object retrievedDataset : retrievedDatasets) {
						if(retrievedDataset instanceof Gsc007DatasetEntity) {
							gsc007Dao.delete(((Gsc007DatasetEntity) retrievedDataset).getId());
							deletedSelfId.add(((Gsc007DatasetEntity) retrievedDataset).getId());
						}
					}
				}
				for(Long id : deletedSelfId) {
					//TODO
				}
			}catch(Exception e) {
				logger.error(e);
				throw new DCException("ER01");
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
	private List<Object> createSearchIdQuery(String tablename,String idName,String jsonColumnName,Long id,GenericJpaService genericJPA) {
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
}