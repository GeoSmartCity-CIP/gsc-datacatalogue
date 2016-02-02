/*
 * Created on 18 dic 2015 ( Time 16:29:07 )
 * Generated by Telosys Tools Generator ( version 2.1.1 )
 */
package it.sinergis.datacatalogue.persistence.services.fake;

import java.util.List;
import java.util.Map;

import it.sinergis.datacatalogue.bean.jpa.Gsc005PermissionEntity;
import it.sinergis.datacatalogue.persistence.commons.fake.GenericFakeService;
import it.sinergis.datacatalogue.persistence.services.Gsc005PermissionPersistence;

/**
 * Fake persistence service implementation ( entity "Gsc005Permission" )
 *
 * @author Telosys Tools Generator
 */
public class Gsc005PermissionPersistenceFAKE extends GenericFakeService<Gsc005PermissionEntity> implements Gsc005PermissionPersistence {

	public Gsc005PermissionPersistenceFAKE () {
		super(Gsc005PermissionEntity.class);
	}
	
	protected Gsc005PermissionEntity buildEntity(int index) {
		Gsc005PermissionEntity entity = new Gsc005PermissionEntity();
		// Init fields with mock values
		entity.setId( nextLong() ) ;
		entity.setJson( nextString() ) ;
		return entity ;
	}
	
	
	public boolean delete(Gsc005PermissionEntity entity) {
		log("delete ( Gsc005PermissionEntity : " + entity + ")" ) ;
		return true;
	}

	public boolean delete( Long id ) {
		log("delete ( PK )") ;
		return true;
	}

	public void insert(Gsc005PermissionEntity entity) {
		log("insert ( Gsc005PermissionEntity : " + entity + ")" ) ;
	}

	public Gsc005PermissionEntity load( Long id ) {
		log("load ( PK )") ;
		return buildEntity(1) ; 
	}

	public List<Gsc005PermissionEntity> loadAll() {
		log("loadAll()") ;
		return buildList(40) ;
	}

	public List<Gsc005PermissionEntity> loadByNamedQuery(String queryName) {
		log("loadByNamedQuery ( '" + queryName + "' )") ;
		return buildList(20) ;
	}

	public List<Gsc005PermissionEntity> loadByNamedQuery(String queryName, Map<String, Object> queryParameters) {
		log("loadByNamedQuery ( '" + queryName + "', parameters )") ;
		return buildList(10) ;
	}

	public Gsc005PermissionEntity save(Gsc005PermissionEntity entity) {
		log("insert ( Gsc005PermissionEntity : " + entity + ")" ) ;
		return entity;
	}

	public List<Gsc005PermissionEntity> search(Map<String, Object> criteria) {
		log("search (criteria)" ) ;
		return buildList(15) ;
	}

	@Override
	public long countAll() {
		return 0 ;
	}

}