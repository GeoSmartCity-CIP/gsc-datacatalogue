/*
 * Created on 18 dic 2015 ( Time 16:29:06 )
 * Generated by Telosys Tools Generator ( version 2.1.1 )
 */
package it.sinergis.datacatalogue.test.service;


import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.sinergis.datacatalogue.common.Constants;
import it.sinergis.datacatalogue.exception.DCException;
import it.sinergis.datacatalogue.services.DatasetsService;
import it.sinergis.datacatalogue.services.DatasourcesService;
import it.sinergis.datacatalogue.services.LayersService;
import it.sinergis.datacatalogue.services.OrganizationsService;
import it.sinergis.datacatalogue.services.ServiceCommons;

/**
 * JUnit test case for Gsc001Organization service
 * 
 * @author Telosys Tools Generator
 *
 */
public class LayerServiceTest extends ServiceCommons 
{
	
	/** Object Mapper. */
	private ObjectMapper om = new ObjectMapper();
	
	/** lyr. Service. */
	private LayersService lyr_service = new LayersService();
	
	/** ds. Service. */
	private DatasourcesService ds_service = new DatasourcesService();
	
	/** dst. Service. */
	private DatasetsService dst_service = new DatasetsService();

	/** org. service. */
	private OrganizationsService org_service = new OrganizationsService();
	
	/** the organization id to be deleted at the end of each method. */
	private Long org_id;
	
	/** SETUP REQUESTS. */
	public static final String CREATE_ORG_REQ = "{\"organizationname\":\"TestOrg\",\"description\":\"Create org test\"}";
	public static final String CREATE_DS_REQ = "{\"datasourcename\":\"DSShapeTest\",\"type\":\"SHAPE\",\"description\":\"SHAPE file\",\"updated\":\"true\",\"path\":\"T:\\\\MDeMeo\\\\dati\\\\bologna\\\\shape\\\\\",\"organization\":";
	public static final String CREATE_DST_REQ = "{\"datasetname\": \"datasetSHAPETest\",\"realname\": \"zone.shp\",\"description\": \"descrizione\",\"iddatasource\":";
	/** CLEANUP REQUESTS. */
	public static final String DELETE_ORG_REQ = "{\"idorganization\":";
	//request end
	public static final String REQ_END = "}";
	/** LAYER REQUESTS. */
	public static final String CREATE_LYR_REQ = "{\"layername\":\"layerTest\",\"description\":\"unit test for layer creation\",\"iddataset\":";
	public static final String CREATE_LYR_REQ_2 = "{\"layername\":\"layerTestName2\",\"description\":\"unit test for layer creation\",\"iddataset\":";
	public static final String UPDATE_LYR_REQ_PART_1 = "{\"layername\":\"layerTest\",\"description\":\"unit test for layer update\",\"iddataset\":";
	public static final String UPDATE_LYR_REQ_PART_2 = ",\"idlayer\":";
	public static final String DELETE_LYR_REQ = "{\"idlayer\":";	
	public static final String READ_LYR_REQ_1 = "{\"idlayer\":";
	public static final String READ_LYR_REQ_2 = "{\"layername\":\"layer\",\"iddataset\":";
	public static final String READ_LYR_REQ_3_PART_1 = "{\"layername\":\"layer\",\"iddataset\":";
	public static final String READ_LYR_REQ_3_PART_2 = ",\"idlayer\":";
	public static final String READ_LYR_REQ_4 = "{\"layername\":\"layer\"}";
	
	private void deleteOrgRecord(Long id) {
		org_service.deleteOrganization(buildIdRequest(DELETE_ORG_REQ,id));
	}
	
	private String createOrgRecord(String req) {
		return org_service.createOrganization(req);
	}
	
	private String createDSRecord(String req) {
		return ds_service.createDatasource(req);
	}
	
	private String createDSTRecord(String req) {
		return dst_service.createDataset(req);
	}
	
	private String createLYRRecord(String req) {
		return lyr_service.createLayer(req);
	}
	
	private Long getRecordId(String response) throws NumberFormatException, DCException {
		return Long.parseLong(getFieldValueFromJsonText(response,Constants.ID_FIELD));
	}
	
	private String buildIdRequest(String reqBegin,Long id) {
		return reqBegin+id+REQ_END;
	}
	
	private String buildIdRequest(String reqBegin,String reqPart2,Long id_1,Long id_2) {
		return reqBegin+id_1+reqPart2+id_2+REQ_END;
	}
	
	private Long doSetup() throws NumberFormatException, DCException {
		//create an organization record
		String create_org_response = createOrgRecord(CREATE_ORG_REQ);
		System.out.println("CREATE_ORG_RESPONSE:");
		System.out.println(create_org_response);
		//create a datasource record for that organization
		String create_ds_response = createDSRecord(buildIdRequest(CREATE_DS_REQ,getRecordId(create_org_response)));
		System.out.println("CREATE_DS_RESPONSE:");
		System.out.println(create_ds_response);
		//create a dataset record for that datasource
		String create_dst_response = createDSTRecord(buildIdRequest(CREATE_DST_REQ,getRecordId(create_ds_response)));
		System.out.println("CREATE_DST_RESPONSE:");
		System.out.println(create_dst_response);	
		
		//save the org id for future deletion
		this.org_id = getRecordId(create_org_response);
		
		//return the dataset id
		return getRecordId(create_dst_response);
	}
	
	private void successAssertChecks(String response) throws JsonProcessingException, IOException {
		//check if response is a well formed json	
		om.readTree(response);
		//Assert the json response does not have an error field
		Assert.assertTrue(!response.contains("\"status\":\"error\""));
	}
	
	private void faliureAssertChecks(String response) throws JsonProcessingException, IOException {
		//check if response is a well formed json	
		om.readTree(response);
		//Assert the json response has an error field
		Assert.assertTrue(response.contains("\"status\":\"error\""));
	}
	
	/**
	 *  This test is the basic create layer test. 
	 *  The response will be checked to be without errors.
	 */
	@Test
	public void createLYRTest() {
		System.out.println("TEST STARTED: createLYRTest()");
		try {
			Long dst_id = doSetup();
			
			//create a layer record for the created dataset
			String create_lyr_response = createLYRRecord(buildIdRequest(CREATE_LYR_REQ,dst_id));
			System.out.println("CREATE_LYR_RESPONSE:");
			System.out.println(create_lyr_response);	
			
			successAssertChecks(create_lyr_response);
			
		} catch (Exception e) {
			System.out.println(e);
			Assert.fail();
		} finally {
			//cleanup (delete the just inserted records)
			deleteOrgRecord(this.org_id);
			System.out.println("TEST ENDED: createLYRTest()");
		}
	}
	
	/**
	 *  This test is a create layer error test.
	 *  The response will be checked, and it should contain an error.
	 *  This should trigger error ER802
	 *  "A layer with the same name already exists in the same dataset."
	 */
	@Test
	public void createLYRTestFail() {
		System.out.println("TEST STARTED: createLYRTestFail()");
		try {
			Long dst_id = doSetup();
			
			//create a layer record for the created dataset
			String create_lyr_response = createLYRRecord(buildIdRequest(CREATE_LYR_REQ,dst_id));
			System.out.println("CREATE_LYR_RESPONSE:");
			System.out.println(create_lyr_response);	

			//try to create another record with the same name 
			String create_lyr_response_duplicate = createLYRRecord(buildIdRequest(CREATE_LYR_REQ,dst_id));
			System.out.println("CREATE_LYR_RESPONSE DUPLICATE:");
			System.out.println(create_lyr_response_duplicate);	
			
			//check if response is a well formed json	
			om.readTree(create_lyr_response_duplicate);
			//Assert the json response has an error field
			Assert.assertTrue(create_lyr_response_duplicate.contains("\"status\":\"error\""));
			
		} catch (Exception e) {
			System.out.println(e);
			Assert.fail();
		} finally {
			//cleanup (delete the just inserted records)
			deleteOrgRecord(this.org_id);
			System.out.println("TEST ENDED: createLYRTestFail()");
		}
	}
	
	/**
	 *  This test is a create layer error test.
	 *  The response will be checked, and it should contain an error.
	 *  This should trigger error ER801
	 *  "Incorrect parameters: the dataset id doesn't match any existing dataset."
	 */
	@Test
	public void createLYRTestFail2() {
		System.out.println("TEST STARTED: createLYRTestFail2()");
		try {
			Long dst_id = doSetup();

			//create the layer linked to a NON EXISTING dataset
			String create_lyr_response = createLYRRecord(buildIdRequest(CREATE_LYR_REQ,dst_id+1));
			System.out.println(create_lyr_response);
			
			faliureAssertChecks(create_lyr_response);
			
		} catch (Exception e) {
			System.out.println(e);
			Assert.fail();
		} finally {
			//cleanup (delete the just inserted records)
			deleteOrgRecord(this.org_id);
			System.out.println("TEST ENDED: createLYRTestFail2()");
		}
	}
	
	/**
	 *  This test is the basic update layer test. 
	 *  The response will be checked to be without errors.
	 */
	@Test
	public void updateLYRTest() {
		System.out.println("TEST STARTED: updateLYRTest()");
		try {
			Long dst_id = doSetup();
			
			//create a layer record for the created dataset
			String create_lyr_response = createLYRRecord(buildIdRequest(CREATE_LYR_REQ,dst_id));
			System.out.println("CREATE_LYR_RESPONSE:");
			System.out.println(create_lyr_response);	
			
			//Update the record
			String update_lyr_response = lyr_service.updateLayer((buildIdRequest(UPDATE_LYR_REQ_PART_1,UPDATE_LYR_REQ_PART_2,dst_id,getRecordId(create_lyr_response))));
			System.out.println("UPDATE_LYR_RESPONSE:");
			System.out.println(update_lyr_response);
			
			successAssertChecks(update_lyr_response);
		} catch (Exception e) {
			System.out.println(e);
			Assert.fail();
		} finally {
			//cleanup (delete the just inserted records)
			deleteOrgRecord(this.org_id);
			//deleteDsRecord(create_ds_id);
			System.out.println("TEST ENDED: updateLYRTest()");
		}
	}
	
	/**
	 *  This test is a update layer error test.
	 *  The response will be checked, and it should contain an error.
	 *  This should trigger error ER803
	 *  "The layer to update could not be found. The id parameter specified in the request may be wrong."
	 */
	@Test
	public void updateLYRTestFail() {
		System.out.println("TEST STARTED: updateLYRTestFail()");
		try {
			Long dst_id = doSetup();
			
			//create a layer record for the created dataset
			String create_lyr_response = createLYRRecord(buildIdRequest(CREATE_LYR_REQ,dst_id));
			System.out.println("CREATE_LYR_RESPONSE:");
			System.out.println(create_lyr_response);	
			
			//Update the record
			String update_lyr_response = lyr_service.updateLayer((buildIdRequest(UPDATE_LYR_REQ_PART_1,UPDATE_LYR_REQ_PART_2,dst_id,getRecordId(create_lyr_response)+1)));
			System.out.println("UPDATE_LYR_RESPONSE:");
			System.out.println(update_lyr_response);
			
			faliureAssertChecks(update_lyr_response);
			
		} catch (Exception e) {
			System.out.println(e);
			Assert.fail();
		} finally {
			//cleanup (delete the just inserted records)
			deleteOrgRecord(this.org_id);
			System.out.println("TEST ENDED: updateLYRTestFail()");
		}
	}
	
	/**
	 *  This test is a update layer error test.
	 *  The response will be checked, and it should contain an error.
	 *  This should trigger error ER804
	 *  "Another layer with the same name already exists. Specify a different layer new name."
	 */
	@Test
	public void updateTestFail2() {
		System.out.println("TEST STARTED: updateLYRTestFail2()");
		try {
			Long dst_id = doSetup();
			
			//create a layer record for the created dataset
			String create_lyr_response = createLYRRecord(buildIdRequest(CREATE_LYR_REQ,dst_id));
			System.out.println("CREATE_LYR_RESPONSE:");
			System.out.println(create_lyr_response);	
			
			//create another layer record for the created dataset
			String create_lyr_response_2 = createLYRRecord(buildIdRequest(CREATE_LYR_REQ_2,dst_id));
			System.out.println("CREATE_LYR_RESPONSE:");
			System.out.println(create_lyr_response_2);	
			
			//try to update the second record giving it the name of the first one
			String update_lyr_response = lyr_service.updateLayer((buildIdRequest(UPDATE_LYR_REQ_PART_1,UPDATE_LYR_REQ_PART_2,dst_id,getRecordId(create_lyr_response_2))));
			System.out.println("UPDATE_LYR_RESPONSE:");
			System.out.println(update_lyr_response);

			faliureAssertChecks(update_lyr_response);
			
		} catch (Exception e) {
			System.out.println(e);
			Assert.fail();
		} finally {
			//cleanup (delete the just inserted records)
			deleteOrgRecord(this.org_id);
			System.out.println("TEST ENDED: updateLYRTestFail2()");
		}
	}
	
	/**
	 *  This test is the basic delete layer test. 
	 *  The response will be checked to be without errors.
	 */
	@Test
	public void deleteLYRTest() {
		System.out.println("TEST STARTED: deleteLYRTest()");
		try {
			Long dst_id = doSetup();
			
			//create a layer record for the created dataset
			String create_lyr_response = createLYRRecord(buildIdRequest(CREATE_LYR_REQ,dst_id));
			System.out.println("CREATE_LYR_RESPONSE:");
			System.out.println(create_lyr_response);	

			//delete the record
			String delete_lyr_response = lyr_service.deleteLayer(buildIdRequest(DELETE_LYR_REQ,getRecordId(create_lyr_response)));
			System.out.println("DELETE_LYR_RESPONSE:");
			System.out.println(delete_lyr_response);
			
			successAssertChecks(delete_lyr_response);
		} catch (Exception e) {
			System.out.println(e);
			Assert.fail();
		} finally {
			//cleanup (delete the just inserted records)
			deleteOrgRecord(this.org_id);
			System.out.println("TEST ENDED: deleteLYRTest()");
		}
	}
	
	/**
	 *  This test is a delete layer error test.
	 *  The response will be checked, and it should contain an error.
	 *  This should trigger error ER805
	 *  "The layer to delete could not be found. The id parameter specified in the request may be wrong."
	 */
	@Test
	public void deleteLYRTestFail() {
		System.out.println("TEST STARTED: deleteLYRTestFail()");
		try {
			Long dst_id = doSetup();
			
			//create a layer record for the created dataset
			String create_lyr_response = createLYRRecord(buildIdRequest(CREATE_LYR_REQ,dst_id));
			System.out.println("CREATE_LYR_RESPONSE:");
			System.out.println(create_lyr_response);	

			//delete the record
			String delete_lyr_response = lyr_service.deleteLayer(buildIdRequest(DELETE_LYR_REQ,getRecordId(create_lyr_response)+1));
			System.out.println("DELETE_LYR_RESPONSE:");
			System.out.println(delete_lyr_response);
			
			faliureAssertChecks(delete_lyr_response);
			
		} catch (Exception e) {
			System.out.println(e);
			Assert.fail();
		} finally {
			//cleanup (delete the just inserted records)
			deleteOrgRecord(this.org_id);
			System.out.println("TEST ENDED: deleteLYRTestFail()");
		}
	}
	
	/**
	 *  This test is the read by id layer test. 
	 *  The response will be checked to be without errors.
	 */
	@Test
	public void readLYRByLayerIdTest() {
		System.out.println("TEST STARTED: readLYRByLayerIdTest()");
		try {
			Long dst_id = doSetup();
			
			//create a layer record for the created dataset
			String create_lyr_response = createLYRRecord(buildIdRequest(CREATE_LYR_REQ,dst_id));
			System.out.println("CREATE_LYR_RESPONSE:");
			System.out.println(create_lyr_response);	
			
			//research by id
			String read_lyr_response = lyr_service.listLayer(buildIdRequest(READ_LYR_REQ_1,getRecordId(create_lyr_response)));
			System.out.println("LIST_LYR_RESPONSE:");
			System.out.println(read_lyr_response);
			
			successAssertChecks(read_lyr_response);
			
		} catch (Exception e) {
			System.out.println(e);
			Assert.fail();
		} finally {
			//cleanup (delete the just inserted records)
			deleteOrgRecord(this.org_id);
			System.out.println("TEST ENDED: readLYRByLayerIdTest()");
		}
	}
	
	/**
	 *  This test is the read by dataset id and layername layer test. 
	 *  The response will be checked to be without errors.
	 */
	@Test
	public void readLYRByDatasetIdTest() {
		System.out.println("TEST STARTED: readLYRByDatasetIdTest()");
		try {
			Long dst_id = doSetup();
			
			//create a layer record for the created dataset
			String create_lyr_response = createLYRRecord(buildIdRequest(CREATE_LYR_REQ,dst_id));
			System.out.println("CREATE_LYR_RESPONSE:");
			System.out.println(create_lyr_response);	
			
			//research by id
			String read_lyr_response = lyr_service.listLayer(buildIdRequest(READ_LYR_REQ_2,dst_id));
			System.out.println("LIST_LYR_RESPONSE:");
			System.out.println(read_lyr_response);
			
			successAssertChecks(read_lyr_response);
			
		} catch (Exception e) {
			System.out.println(e);
			Assert.fail();
		} finally {
			//cleanup (delete the just inserted records)
			deleteOrgRecord(this.org_id);
			System.out.println("TEST ENDED: readLYRByDatasetIdTest()");
		}
	}
	
	/**
	 *  This test is a list layer error test.
	 *  The response will be checked, and it should contain an error.
	 *  This should trigger error ER13
	 *  "No results found."
	 */
	@Test
	public void readLYRTestFail() {
		System.out.println("TEST STARTED: readLYRTestFail()");
		try {
			Long dst_id = doSetup();
			
			//create a layer record for the created dataset
			String create_lyr_response = createLYRRecord(buildIdRequest(CREATE_LYR_REQ,dst_id));
			System.out.println("CREATE_LYR_RESPONSE:");
			System.out.println(create_lyr_response);	
			
			//research by id (but insert a non-existing id)
			String read_lyr_response = lyr_service.listLayer(buildIdRequest(READ_LYR_REQ_1,getRecordId(create_lyr_response)+1));
			System.out.println("LIST_LYR_RESPONSE:");
			System.out.println(read_lyr_response);
			
			faliureAssertChecks(read_lyr_response);
			
		} catch (Exception e) {
			System.out.println(e);
			Assert.fail();
		} finally {
			//cleanup (delete the just inserted records)
			deleteOrgRecord(this.org_id);
			System.out.println("TEST ENDED: readLYRTestFail()");
		}
	}
	
	
	/**
	 *  This test is a list layer error test.
	 *  The response will be checked, and it should contain an error.
	 *  This should trigger error ER806
	 *  "Incorrect parameters: Perform a request either by layerid or by datasetid (and optionally layername). Both parameters are not allowed at the same time."
	 */
	@Test
	public void readLYRTestFail2() {
		System.out.println("TEST STARTED: readLYRTestFail2()");
		try {
			Long dst_id = doSetup();
			
			//create a layer record for the created dataset
			String create_lyr_response = createLYRRecord(buildIdRequest(CREATE_LYR_REQ,dst_id));
			System.out.println("CREATE_LYR_RESPONSE:");
			System.out.println(create_lyr_response);	
			
			//research including incompatible parameters
			String read_lyr_response = lyr_service.listLayer(buildIdRequest(READ_LYR_REQ_3_PART_1,READ_LYR_REQ_3_PART_2,dst_id,getRecordId(create_lyr_response)));
			System.out.println("LIST_LYR_RESPONSE:");
			System.out.println(read_lyr_response);
			
			faliureAssertChecks(read_lyr_response);
			
		} catch (Exception e) {
			System.out.println(e);
			Assert.fail();
		} finally {
			//cleanup (delete the just inserted records)
			deleteOrgRecord(this.org_id);
			System.out.println("TEST ENDED: readLYRTestFail2()");
		}
	}
	
	/**
	 *  This test is a list layer error test.
	 *  The response will be checked, and it should contain an error.
	 *  This should trigger error ER807
	 *  "Incorrect parameters: either iddataset or idlayer parameters should be specified."
	 */
	@Test
	public void readLYRTestFail3() {
		System.out.println("TEST STARTED: readLYRTestFail3()");
		try {
			Long dst_id = doSetup();
			
			//create a layer record for the created dataset
			String create_lyr_response = createLYRRecord(buildIdRequest(CREATE_LYR_REQ,dst_id));
			System.out.println("CREATE_LYR_RESPONSE:");
			System.out.println(create_lyr_response);	
			
			
			//research without one of the mandatory parameters
			String read_lyr_response = ds_service.listDatasource(READ_LYR_REQ_4);
			System.out.println("LIST_LYR_RESPONSE:");
			System.out.println(read_lyr_response);
			
			faliureAssertChecks(read_lyr_response);
			
		} catch (Exception e) {
			System.out.println(e);
			Assert.fail();
		} finally {
			//cleanup (delete the just inserted records)
			deleteOrgRecord(this.org_id);
			System.out.println("TEST ENDED: readLYRTestFail3()");
		}
	}
}