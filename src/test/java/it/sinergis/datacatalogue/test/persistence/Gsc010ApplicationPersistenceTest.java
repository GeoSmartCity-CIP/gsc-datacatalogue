/*
 * Created on 18 dic 2015 ( Time 16:29:08 )
 * Generated by Telosys Tools Generator ( version 2.1.1 )
 */
package it.sinergis.datacatalogue.test.persistence;


import it.sinergis.datacatalogue.bean.jpa.Gsc010ApplicationEntity;
import it.sinergis.datacatalogue.mock.Gsc010ApplicationEntityMock;
import it.sinergis.datacatalogue.persistence.PersistenceServiceProvider;
import it.sinergis.datacatalogue.persistence.services.Gsc010ApplicationPersistence;

import org.junit.Assert;
import org.junit.Test;

/**
 * JUnit test case for Gsc010Application persistence service
 * 
 * @author Telosys Tools Generator
 *
 */
public class Gsc010ApplicationPersistenceTest 
{
	@Test
	public void test1() {
		
		System.out.println("Test count ..." );
		
		Gsc010ApplicationPersistence service = PersistenceServiceProvider.getService(Gsc010ApplicationPersistence.class);
		System.out.println("CountAll = " + service.countAll() );
	}
	
	@Test
	public void test2() {
		
		System.out.println("Test Gsc010Application persistence : delete + load ..." );
		
		Gsc010ApplicationPersistence service = PersistenceServiceProvider.getService(Gsc010ApplicationPersistence.class);
		
		Gsc010ApplicationEntityMock mock = new Gsc010ApplicationEntityMock();
		
		// TODO : set primary key values here 
		process( service, mock, (long)0  );
		// process( service, mock, ... );
	}

	private void process(Gsc010ApplicationPersistence service, Gsc010ApplicationEntityMock mock, Long id ) {
		System.out.println("----- "  );
		System.out.println(" . load : " );
		Gsc010ApplicationEntity entity = service.load( id );
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