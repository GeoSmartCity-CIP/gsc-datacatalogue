/*
 * Created on 18 dic 2015 ( Time 16:29:07 )
 * Generated by Telosys Tools Generator ( version 2.1.1 )
 */
package it.sinergis.datacatalogue.test.persistence;


import it.sinergis.datacatalogue.bean.jpa.Gsc006DatasourceEntity;
import it.sinergis.datacatalogue.mock.Gsc006DatasourceEntityMock;
import it.sinergis.datacatalogue.persistence.PersistenceServiceProvider;
import it.sinergis.datacatalogue.persistence.services.Gsc006DatasourcePersistence;

import org.junit.Assert;
import org.junit.Test;

/**
 * JUnit test case for Gsc006Datasource persistence service
 * 
 * @author Telosys Tools Generator
 *
 */
public class Gsc006DatasourcePersistenceTest 
{
	@Test
	public void test1() {
		
		System.out.println("Test count ..." );
		
		Gsc006DatasourcePersistence service = PersistenceServiceProvider.getService(Gsc006DatasourcePersistence.class);
		System.out.println("CountAll = " + service.countAll() );
	}
	
	@Test
	public void test2() {
		
		System.out.println("Test Gsc006Datasource persistence : delete + load ..." );
		
		Gsc006DatasourcePersistence service = PersistenceServiceProvider.getService(Gsc006DatasourcePersistence.class);
		
		Gsc006DatasourceEntityMock mock = new Gsc006DatasourceEntityMock();
		
		// TODO : set primary key values here 
		process( service, mock, (long)0  );
		// process( service, mock, ... );
	}

	private void process(Gsc006DatasourcePersistence service, Gsc006DatasourceEntityMock mock, Long id ) {
		System.out.println("----- "  );
		System.out.println(" . load : " );
		Gsc006DatasourceEntity entity = service.load( id );
		if ( entity != null ) {
			// Found 
			System.out.println("   FOUND : " + entity );
			
			// Save (update) with the same values to avoid database integrity errors  
			System.out.println(" . save : " + entity );
			service.save(entity);
			System.out.println("   saved : " + entity );
		}
		else {
			// Not found 
			System.out.println("   NOT FOUND" );
			// Create a new instance 
			entity = mock.createInstance( id ) ;
			Assert.assertNotNull(entity);

			// No reference : insert is possible 
			// Try to insert the new instance
			System.out.println(" . insert : " + entity );
			service.insert(entity);
			System.out.println("   inserted : " + entity );

			System.out.println(" . delete : " );
			boolean deleted = service.delete( id );
			System.out.println("   deleted = " + deleted);
			Assert.assertTrue(deleted) ;
		}		
	}
}