/*
 * Created on 18 dic 2015 ( Time 16:29:07 )
 * Generated by Telosys Tools Generator ( version 2.1.1 )
 */
package it.sinergis.datacatalogue.persistence.services.fake;

import java.util.List;
import java.util.Map;

import it.sinergis.datacatalogue.bean.jpa.Gsc008LayerEntity;
import it.sinergis.datacatalogue.persistence.commons.fake.GenericFakeService;
import it.sinergis.datacatalogue.persistence.services.Gsc008LayerPersistence;

/**
 * Fake persistence service implementation ( entity "Gsc008Layer" )
 *
 * @author Telosys Tools Generator
 */
public class Gsc008LayerPersistenceFAKE extends GenericFakeService<Gsc008LayerEntity> implements Gsc008LayerPersistence {

	public Gsc008LayerPersistenceFAKE () {
		super(Gsc008LayerEntity.class);
	}
	
	protected Gsc008LayerEntity buildEntity(int index) {
		Gsc008LayerEntity entity = new Gsc008LayerEntity();
		// Init fields with mock values
		entity.setId( nextLong() ) ;
		entity.setJson( nextString() ) ;
		return entity ;
	}
	
	
	public boolean delete(Gsc008LayerEntity entity) {
		log("delete ( Gsc008LayerEntity : " + entity + ")" ) ;
		return true;
	}

	public boolean delete( Long id ) {
		log("delete ( PK )") ;
		return true;
	}

	public void insert(Gsc008LayerEntity entity) {
		log("insert ( Gsc008LayerEntity : " + entity + ")" ) ;
	}

	public Gsc008LayerEntity load( Long id ) {
		log("load ( PK )") ;
		return buildEntity(1) ; 
	}

	public List<Gsc008LayerEntity> loadAll() {
		log("loadAll()") ;
		return buildList(40) ;
	}

	public List<Gsc008LayerEntity> loadByNamedQuery(String queryName) {
		log("loadByNamedQuery ( '" + queryName + "' )") ;
		return buildList(20) ;
	}

	public List<Gsc008LayerEntity> loadByNamedQuery(String queryName, Map<String, Object> queryParameters) {
		log("loadByNamedQuery ( '" + queryName + "', parameters )") ;
		return buildList(10) ;
	}

	public Gsc008LayerEntity save(Gsc008LayerEntity entity) {
		log("insert ( Gsc008LayerEntity : " + entity + ")" ) ;
		return entity;
	}

	public List<Gsc008LayerEntity> search(Map<String, Object> criteria) {
		log("search (criteria)" ) ;
		return buildList(15) ;
	}

	@Override
	public long countAll() {
		return 0 ;
	}

}