/*
 * Created on 18 dic 2015 ( Time 16:29:07 )
 * Generated by Telosys Tools Generator ( version 2.1.1 )
 */
package it.sinergis.datacatalogue.persistence.services;

import java.util.List;
import java.util.Map;

import it.sinergis.datacatalogue.bean.jpa.Gsc005PermissionEntity;

/**
 * Basic persistence operations for entity "Gsc005Permission"
 * 
 * This Bean has a basic Primary Key : Long
 *
 * @author Telosys Tools Generator
 *
 */
public interface Gsc005PermissionPersistence {

	/**
	 * Deletes the given entity <br>
	 * Transactional operation ( begin transaction and commit )
	 * @param gsc005Permission
	 * @return true if found and deleted, false if not found
	 */
	public boolean delete(Gsc005PermissionEntity gsc005Permission) ;

	/**
	 * Deletes the entity by its Primary Key <br>
	 * Transactional operation ( begin transaction and commit )
	 * @param id
	 * @return true if found and deleted, false if not found
	 */
	public boolean delete(Long id) ;

	/**
	 * Inserts the given entity and commit <br>
	 * Transactional operation ( begin transaction and commit )
	 * @param gsc005Permission
	 */
	public void insert(Gsc005PermissionEntity gsc005Permission) ;

	/**
	 * Loads the entity for the given Primary Key <br>
	 * @param id
	 * @return the entity loaded (or null if not found)
	 */
	public Gsc005PermissionEntity load(Long id) ;

	/**
	 * Loads ALL the entities (use with caution)
	 * @return
	 */
	public List<Gsc005PermissionEntity> loadAll() ;

	/**
	 * Loads a list of entities using the given "named query" without parameter 
	 * @param queryName
	 * @return
	 */
	public List<Gsc005PermissionEntity> loadByNamedQuery(String queryName) ;

	/**
	 * Loads a list of entities using the given "named query" with parameters 
	 * @param queryName
	 * @param queryParameters
	 * @return
	 */
	public List<Gsc005PermissionEntity> loadByNamedQuery(String queryName, Map<String, Object> queryParameters) ;

	/**
	 * Saves (create or update) the given entity <br>
	 * Transactional operation ( begin transaction and commit )
	 * @param gsc005Permission
	 * @return
	 */
	public Gsc005PermissionEntity save(Gsc005PermissionEntity gsc005Permission) ;

	/**
	 * Search the entities matching the given search criteria
	 * @param criteria
	 * @return
	 */
	public List<Gsc005PermissionEntity> search( Map<String, Object> criteria ) ;

	/**
	 * Count all the occurrences
	 * @return
	 */
	public long countAll();
	
}