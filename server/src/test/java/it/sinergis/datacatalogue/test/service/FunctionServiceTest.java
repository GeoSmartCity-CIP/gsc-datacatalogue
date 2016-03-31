package it.sinergis.datacatalogue.test.service;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import it.sinergis.datacatalogue.common.Constants;
import it.sinergis.datacatalogue.exception.DCException;
import it.sinergis.datacatalogue.services.FunctionsService;
import it.sinergis.datacatalogue.services.OrganizationsService;
import it.sinergis.datacatalogue.services.ServiceCommons;

/**
 * JUnit test case for Gsc004Function service
 * 
 * @author Sinergis s.r.l.
 *
 */
public class FunctionServiceTest extends ServiceCommons{
	
	/** Organization service. */
	private OrganizationsService org_service = new OrganizationsService();
	
	/** Function service. */
	private FunctionsService func_service = new FunctionsService();
	
	//create organization request
	public static final String CREATE_ORG_REQ_1 = "{\"organizationname\":\"TestOrg\",\"description\":\"Create test succes\"}";
	//create function request
	public static final String CREATE_FUNCTION_REQ_1 = "{\"functionname\":\"TestFunction\",\"organization\":\"$ID_ORG\",\"type\":\"test\",\"description\":\"Create test function success\"}";	
	//create function request
	public static final String CREATE_FUNCTION_REQ_2 = "{\"functionname\":\"TestFunction2\",\"organization\":\"$ID_ORG\",\"type\":\"test\",\"description\":\"Create second test function success\"}";
	//update function request
	public static final String UPDATE_FUNCTION_REQ_1 = "{\"idfunction\":\"$ID_FUNC\",\"functionname\":\"UpdatedTestFunction\",\"organization\":\"$ID_ORG\",\"type\":\"test\",\"description\":\"Update test function success\"}";
	//update function request
	public static final String UPDATE_FUNCTION_REQ_2 = "{\"idfunction\":\"$ID_FUNC\",\"functionname\":\"TestFunction\",\"organization\":\"$ID_ORG\",\"type\":\"test\",\"description\":\"Update test function error\"}";
	//delete organization request
	public static final String DELETE_ORG_REQ_1 = "{\"idorganization\":";	
	//delete function request
	public static final String DELETE_FUNC_REQ_1 = "{\"idfunction\":";
	//list functions request
	public static final String LIST_FUNCTION_REQUEST_1 = "{\"organization\":\"$ID_ORG\"}";
	//list functions request
	public static final String LIST_FUNCTION_REQUEST_2 = "{\"organization\":\"$ID_ORG\",\"functionname\":\"TestFunction\"}";
	//request end
	public static final String REQ_END = "}";
	
	
	private Long getRecordId(String response) throws NumberFormatException, DCException {
		return Long.parseLong(getFieldValueFromJsonText(response,Constants.ID_FIELD));
	}
	
	/**
	 * Create a new function.
	 * Expected status: done.
	 */
	@Test
	public void createFunctionTest(){
		System.out.println("TEST STARTED: createFunctionTest()");
		Long orgId = null;
		Long funcId = null;
		try{
			//create the test organization 
			String createOrgResp = org_service.createOrganization(CREATE_ORG_REQ_1);
			orgId = getRecordId(createOrgResp);
			System.out.println(createOrgResp);
			
			//create the test function linked to the organization test
			String createFuncResp = func_service.createFunction(CREATE_FUNCTION_REQ_1.replace("$ID_ORG", orgId.toString()));
			System.out.println("CreateFunction Response: "+createFuncResp);
			
			//get the new function ID
			funcId = getRecordId(createFuncResp);
			
			//check if the createFuncResp is a well formed JSON object.			
			checkJsonWellFormed(createFuncResp);
			
			//Assert the JSON response does not have an error field
			Assert.assertTrue(!createFuncResp.contains("\"status\":\"error\""));
			
		}catch(Exception e){
			e.printStackTrace();
			Assert.fail();
		}finally{			
			//delete the test organization created
			org_service.deleteOrganization(DELETE_ORG_REQ_1+orgId+REQ_END);

			System.out.println("TEST ENDED: createFunctionTest()");
		}
	}
	
	/**
	 * Try to create a function twice for the same organization.
	 * Expected status: error.
	 */
	@Test
	public void createDuplicateFunctionTestFail(){
		System.out.println("TEST STARTED: createDuplicateFunctionTestFail()");
		Long orgId = null;
		Long funcId = null;
		try{
			//create the test organization
			String createOrgResp = org_service.createOrganization(CREATE_ORG_REQ_1);
			orgId=getRecordId(createOrgResp);
			System.out.println(createOrgResp);
			
			//create the test function linked to the organization test
			String createFuncResp = func_service.createFunction(CREATE_FUNCTION_REQ_1.replace("$ID_ORG", orgId.toString()));
			System.out.println("CreateFunction Response: "+createFuncResp);
			
			//get the new function ID
			funcId = getRecordId(createFuncResp);
			
			//check if the createFuncResp is a well formed JSON object.
			checkJsonWellFormed(createFuncResp);
			
			//try to create another function with the same name for the same organization
			String reCreateFuncResp = func_service.createFunction(CREATE_FUNCTION_REQ_1.replace("$ID_ORG", orgId.toString()));
			System.out.println("CreateFunction Response: "+reCreateFuncResp);
			
			Assert.assertTrue(reCreateFuncResp.contains("\"status\":\"error\""));
						
		}catch (Exception e ){			
			Assert.fail(e.getMessage());
		}finally{
			//delete the test organization created
			org_service.deleteOrganization(DELETE_ORG_REQ_1+orgId+REQ_END);
			
			System.out.println("TEST ENDED: createDuplicateFunctionTestFail()");
		}
		
	}
	
	/**
	 * Try to create a new function for an unexisting organization.
	 * Expected status: error.
	 */
	@Test
	public void createWrongOrgFunctionTest(){
		System.out.println("TEST STARTED: createWrongOrgFunctionTest()");
		Long orgId = null;		
		try{
			
			//create the test organization
			String createOrgResp = org_service.createOrganization(CREATE_ORG_REQ_1);
			orgId = getRecordId(createOrgResp);
			System.out.println(createOrgResp);
			
			Long wrongOrg = orgId+1;
			
			//create the test function linked to an unexisting organization.
			String createFuncResp = func_service.createFunction(CREATE_FUNCTION_REQ_1.replace("$ID_ORG", wrongOrg.toString()));
			System.out.println("CreateFunction Response: "+createFuncResp);
			
			//check if the createFuncResp is a well formed JSON object.
			checkJsonWellFormed(createFuncResp);
			
			Assert.assertTrue(createFuncResp.contains("\"status\":\"error\""));
			
		}catch(Exception e){
			Assert.fail(e.getMessage());
		}finally{
			//delete the test organization created
			org_service.deleteOrganization(DELETE_ORG_REQ_1+orgId+REQ_END);	
			System.out.println("TEST ENDED: createWrongOrgFunctionTest()");
		}
	}
	
	/**
	 * Update the function name.
	 * Expected status: done.
	 */
	@Test
	public void updateNameFunctionTest(){
		System.out.println("TEST STARTED: updateFunctionTest()");
		Long orgId = null;
		Long funcId = null;
		try{
			//create the test organization
			String createOrgResp = org_service.createOrganization(CREATE_ORG_REQ_1);
			orgId = getRecordId(createOrgResp);
			System.out.println(createOrgResp);
			
			//create the test function linked to the organization test
			String createFuncResp = func_service.createFunction(CREATE_FUNCTION_REQ_1.replace("$ID_ORG", orgId.toString()));
			System.out.println("CreateFunction Response: "+createFuncResp);
			
			//get the new function ID
			funcId = getRecordId(createFuncResp);
			
			//check if the createFuncResp is a well formed JSON object.			
			checkJsonWellFormed(createFuncResp);
			
			//update the new function name
			String updateFuncResp = func_service.updateFunction(UPDATE_FUNCTION_REQ_1.replace("$ID_FUNC", funcId.toString()).replace("$ID_ORG", orgId.toString()));
			System.out.println("UpdateFunction Response: "+updateFuncResp);
			
			checkJsonWellFormed(updateFuncResp);

			Assert.assertTrue(!updateFuncResp.contains("\"status\":\"error\""));
		}catch (Exception e){
			Assert.fail(e.getMessage());
		}finally{
			//delete the test organization created
			org_service.deleteOrganization(DELETE_ORG_REQ_1+orgId+REQ_END);

			System.out.println("TEST ENDED: updateFunctionTest()");
		}
	}	
	
	/**
	 * Try to update the function name causing a collision in the same organization.
	 * Expected status: error.
	 */
	@Test
	public void updateNameFunctionTestFail(){
		System.out.println("TEST STARTED: updateNameFunctionTestFail()");
		Long orgId = null;
		Long func1Id = null;
		Long func2Id = null;
		try{
			//create the test organization
			String createOrgResp = org_service.createOrganization(CREATE_ORG_REQ_1);
			orgId = getRecordId(createOrgResp);
			System.out.println(createOrgResp);
			
			//create the test function linked to the organization test
			String createFuncResp = func_service.createFunction(CREATE_FUNCTION_REQ_1.replace("$ID_ORG", orgId.toString()));
			System.out.println("First CreateFunction Response: "+createFuncResp);
			
			//get the new function ID
			func1Id = getRecordId(createFuncResp);
			
			//check if the createFuncResp is a well formed JSON object.		
			checkJsonWellFormed(createFuncResp);
			
			//create a new test function (with a different name) linked to the same organization
			String createFunc2Resp = func_service.createFunction(CREATE_FUNCTION_REQ_2.replace("$ID_ORG", orgId.toString()));
			System.out.println("Second CreateFunction Response: "+createFunc2Resp);
			
			//get the new function ID
			func2Id = getRecordId(createFunc2Resp);
			
			//check if the createFuncResp is a well formed JSON object.	
			checkJsonWellFormed(createFunc2Resp);
			
			//try to rename the function2 with the name of function1.
			String updateFuncResp = func_service.updateFunction(UPDATE_FUNCTION_REQ_2.replace("$ID_FUNC", func2Id.toString()).replace("$ID_ORG", orgId.toString()));
			System.out.println("UpdateFunction Response: "+updateFuncResp);
			
			checkJsonWellFormed(updateFuncResp);
			
			Assert.assertTrue(updateFuncResp.contains("\"status\":\"error\""));
			
		}catch (Exception e){
			Assert.fail(e.getMessage());
		}finally{
			//delete the test organization created
			org_service.deleteOrganization(DELETE_ORG_REQ_1+orgId+REQ_END);
			//delete the first test function created
			func_service.deleteFunction(DELETE_FUNC_REQ_1+func1Id+REQ_END);
			//delete the second test function created
			func_service.deleteFunction(DELETE_FUNC_REQ_1+func2Id+REQ_END);
			System.out.println("TEST ENDED: updateNameFunctionTestFail()");
		}
	}
	
	/**
	 * Try to update an unexisting function.
	 * Expected status: error.
	 */
	@Test
	public void updateFunctionTestFail(){
		System.out.println("TEST STARTED: updateFunctionTestFail()");
		Long orgId = null;
		Long funcId = null;
		try{
			//create the test organization
			String createOrgResp = org_service.createOrganization(CREATE_ORG_REQ_1);
			orgId = getRecordId(createOrgResp);
			System.out.println(createOrgResp);
			
			//create the test function linked to the organization test
			String createFuncResp = func_service.createFunction(CREATE_FUNCTION_REQ_1.replace("$ID_ORG", orgId.toString()));
			System.out.println("CreateFunction Response: "+createFuncResp);
			
			//get the new function ID
			funcId = getRecordId(createFuncResp);
			
			//check if the createFuncResp is a well formed JSON object.			
			checkJsonWellFormed(createFuncResp);
			
			Long wrongFuncId = funcId+1;
			
			//update an unexisting function.
			String updateFuncResp = func_service.updateFunction(UPDATE_FUNCTION_REQ_1.replace("$ID_FUNC", wrongFuncId.toString()).replace("$ID_ORG", orgId.toString()));
			System.out.println("UpdateFunction Response: "+updateFuncResp);
			
			checkJsonWellFormed(updateFuncResp);

			Assert.assertTrue(updateFuncResp.contains("\"status\":\"error\""));
		}catch (Exception e){
			Assert.fail(e.getMessage());
		}finally{
			//delete the test organization created
			org_service.deleteOrganization(DELETE_ORG_REQ_1+orgId+REQ_END);
			//delete the test function created
			func_service.deleteFunction(DELETE_FUNC_REQ_1+funcId+REQ_END);
			System.out.println("TEST ENDED: updateFunctionTestFail()");
		}
	}
	
	/**
	 * Delete a function.
	 * Expected status: done.
	 */
	@Test
	public void deleteFunctionTest(){
		System.out.println("TEST STARTED: deleteFunctionTest()");
		Long orgId = null;
		Long funcId = null;
		try{
			//create the test organization
			String createOrgResp = org_service.createOrganization(CREATE_ORG_REQ_1);
			orgId = getRecordId(createOrgResp);
			System.out.println(createOrgResp);
			
			//create the test function linked to the organization test
			String createFuncResp = func_service.createFunction(CREATE_FUNCTION_REQ_1.replace("$ID_ORG", orgId.toString()));
			System.out.println("CreateFunction Response: "+createFuncResp);
			
			//get the new function ID
			funcId = getRecordId(createFuncResp);
			
			//check if the createFuncResp is a well formed JSON object.			
			checkJsonWellFormed(createFuncResp);
			
			//delete the test function created
			String deleteFuncResp = func_service.deleteFunction(DELETE_FUNC_REQ_1+funcId+REQ_END);
			System.out.println("DeleteFunction Response: "+deleteFuncResp);
			
			//check if the deleteFuncResp is a well formed JSON object.
			checkJsonWellFormed(deleteFuncResp);

			Assert.assertTrue(!deleteFuncResp.contains("\"status\":\"error\""));
		}catch (Exception e){
			Assert.fail(e.getMessage());
		}finally{
			//delete the test organization created
			org_service.deleteOrganization(DELETE_ORG_REQ_1+orgId+REQ_END);			
			System.out.println("TEST ENDED: deleteFunctionTest()");
		}
	}
	
	/**
	 * Try to delete an unexisting function.
	 * Expected status: error.
	 */
	@Test
	public void deleteFunctionTestFail(){
		System.out.println("TEST STARTED: deleteFunctionTestFail()");
		Long orgId = null;
		Long funcId = null;
		try{
			//create the test organization
			String createOrgResp = org_service.createOrganization(CREATE_ORG_REQ_1);
			orgId = getRecordId(createOrgResp);
			System.out.println(createOrgResp);
			
			//create the test function linked to the organization test
			String createFuncResp = func_service.createFunction(CREATE_FUNCTION_REQ_1.replace("$ID_ORG", orgId.toString()));
			System.out.println("CreateFunction Response: "+createFuncResp);
			
			//get the new function ID
			funcId = getRecordId(createFuncResp);
			
			//check if the createFuncResp is a well formed JSON object.			
			checkJsonWellFormed(createFuncResp);
			
			Long wrongFuncId = funcId+1;
			
			//delete the test function created
			String deleteFuncResp = func_service.deleteFunction(DELETE_FUNC_REQ_1+wrongFuncId+REQ_END);
			System.out.println("DeleteFunction Response: "+deleteFuncResp);
			
			//check if the deleteFuncResp is a well formed JSON object.
			checkJsonWellFormed(deleteFuncResp);

			Assert.assertTrue(deleteFuncResp.contains("\"status\":\"error\""));
		}catch (Exception e){
			Assert.fail(e.getMessage());
		}finally{
			//delete the test organization created
			org_service.deleteOrganization(DELETE_ORG_REQ_1+orgId+REQ_END);						
			//delete the test function created
			func_service.deleteFunction(DELETE_FUNC_REQ_1+funcId+REQ_END);
			System.out.println("TEST ENDED: deleteFunctionTestFail()");
		}
	}
	
	/**
	 * List all functions of an organization
	 * Expected status: done.
	 */
	@Test
	public void listAllFunctionsTest(){
		System.out.println("TEST STARTED: listAllFunctionsTest()");
		Long orgId = null;
		Long func1Id = null;
		Long func2Id = null;
		try{
			//create the test organization
			String createOrgResp = org_service.createOrganization(CREATE_ORG_REQ_1);
			orgId = getRecordId(createOrgResp);
			System.out.println(createOrgResp);
			
			//create the test function linked to the organization test
			String createFuncResp = func_service.createFunction(CREATE_FUNCTION_REQ_1.replace("$ID_ORG", orgId.toString()));
			System.out.println("CreateFunction Response: "+createFuncResp);
			
			//get the new function ID
			func1Id = getRecordId(createFuncResp);
			
			//check if the createFuncResp is a well formed JSON object.		
			checkJsonWellFormed(createFuncResp);
			
			//create a new test function (with a different name) linked to the same organization
			String createFunc2Resp = func_service.createFunction(CREATE_FUNCTION_REQ_2.replace("$ID_ORG", orgId.toString()));
			System.out.println("Second CreateFunction Response: "+createFunc2Resp);
			
			//get the new function ID
			func2Id = getRecordId(createFunc2Resp);
			
			//check if the createFuncResp is a well formed JSON object.	
			checkJsonWellFormed(createFunc2Resp);
			
			//list all the function linked to the created organization
			String listFuncResponse = func_service.listFunction(LIST_FUNCTION_REQUEST_1.replace("$ID_ORG", orgId.toString()));
			System.out.println("ListFunction Response: "+listFuncResponse);
			
			//check if the listFuncResponse is a well formed JSON object.
			checkJsonWellFormed(listFuncResponse);
			
			Assert.assertTrue(!listFuncResponse.contains("\"status\":\"error\"") && extractFuncNumber(listFuncResponse) == 2);
			
		}catch (Exception e){
			Assert.fail(e.getMessage());
		}finally{
			//delete the test organization created
			org_service.deleteOrganization(DELETE_ORG_REQ_1+orgId+REQ_END);						
			//delete the first test function created
			func_service.deleteFunction(DELETE_FUNC_REQ_1+func1Id+REQ_END);
			//delete the first test function created
			func_service.deleteFunction(DELETE_FUNC_REQ_1+func2Id+REQ_END);
			System.out.println("TEST ENDED: listAllFunctionsTest()");
		}
	}
	
	/**
	 * Search a function identified by the name in the organization's functions.
	 * Expected status: done.
	 */
	@Test
	public void searchSingleFunctionTest(){
		System.out.println("TEST STARTED: searchSingleFunctionTest()");
		Long orgId = null;
		Long func1Id = null;
		Long func2Id = null;
		try{
			//create the test organization
			String createOrgResp = org_service.createOrganization(CREATE_ORG_REQ_1);
			orgId = getRecordId(createOrgResp);
			System.out.println(createOrgResp);
			
			//create the test function linked to the organization test
			String createFuncResp = func_service.createFunction(CREATE_FUNCTION_REQ_1.replace("$ID_ORG", orgId.toString()));
			System.out.println("CreateFunction Response: "+createFuncResp);
			
			//get the new function ID
			func1Id = getRecordId(createFuncResp);
			
			//check if the createFuncResp is a well formed JSON object.		
			checkJsonWellFormed(createFuncResp);
			
			//create a new test function (with a different name) linked to the same organization
			String createFunc2Resp = func_service.createFunction(CREATE_FUNCTION_REQ_2.replace("$ID_ORG", orgId.toString()));
			System.out.println("Second CreateFunction Response: "+createFunc2Resp);
			
			//get the new function ID
			func2Id = getRecordId(createFunc2Resp);
			
			//check if the createFuncResp is a well formed JSON object.	
			checkJsonWellFormed(createFunc2Resp);
			
			//list the specified function linked to the created organization
			String listFuncResponse = func_service.listFunction(LIST_FUNCTION_REQUEST_2.replace("$ID_ORG", orgId.toString()));
			System.out.println("ListFunction Response: "+listFuncResponse);
			
			//check if the listFuncResponse is a well formed JSON object.
			checkJsonWellFormed(listFuncResponse);
			
			Assert.assertTrue(!listFuncResponse.contains("\"status\":\"error\"") && extractFuncNumber(listFuncResponse) >= 1);
			
		}catch (Exception e){
			Assert.fail(e.getMessage());
		}finally{
			//delete the test organization created
			org_service.deleteOrganization(DELETE_ORG_REQ_1+orgId+REQ_END);						
			//delete the first test function created
			func_service.deleteFunction(DELETE_FUNC_REQ_1+func1Id+REQ_END);
			//delete the first test function created
			func_service.deleteFunction(DELETE_FUNC_REQ_1+func2Id+REQ_END);
			System.out.println("TEST ENDED: searchSingleFunctionTest()");
		}
	}
	
	
	//XXX not working, possibly not a useful test case (because matching is done with %LIKE%)
//	/**
//	 * Try to search an unexisting function.
//	 * Expected status: error.
//	 */
//	@Test
//	public void searchUnexistingFunctionTestFail(){
//		System.out.println("TEST STARTED: searchUnexistingFunctionTestFail()");
//		Long orgId = null;
//		Long funcId = null;
//		try{
//			//create the test organization
//			String createOrgResp = org_service.createOrganization(CREATE_ORG_REQ_1);
//			orgId = getRecordId(createOrgResp);
//			System.out.println(createOrgResp);
//						
//			//create a test function linked to the same organization
//			String createFuncResp = func_service.createFunction(CREATE_FUNCTION_REQ_2.replace("$ID_ORG", orgId.toString()));
//			System.out.println("CreateFunction Response: "+createFuncResp);
//			
//			//get the new function ID
//			funcId = getRecordId(createFuncResp);
//			
//			//check if the createFuncResp is a well formed JSON object.	
//			checkJsonWellFormed(createFuncResp);
//			
//			//list the specified function linked to the created organization
//			String listFuncResponse = func_service.listFunction(LIST_FUNCTION_REQUEST_2.replace("$ID_ORG", orgId.toString()));
//			System.out.println("ListFunction Response: "+listFuncResponse);
//			
//			//check if the listFuncResponse is a well formed JSON object.
//			checkJsonWellFormed(listFuncResponse);
//			
//			Assert.assertTrue(listFuncResponse.contains("\"status\":\"error\""));
//			
//		}catch (Exception e){
//			Assert.fail(e.getMessage());
//		}finally{
//			//delete the test organization created
//			org_service.deleteOrganization(DELETE_ORG_REQ_1+orgId+REQ_END);						
//			//delete the test function created
//			func_service.deleteFunction(DELETE_FUNC_REQ_1+funcId+REQ_END);
//			System.out.println("TEST ENDED: searchUnexistingFunctionTestFail()");
//		}
//	}
	
	/**
	 * Try to search all the functions for an unexisting organization.
	 * Expected status: error.
	 */
	@Test
	public void searchFunctionTestFail(){
		System.out.println("TEST STARTED: searchFunctionTestFail()");
		Long orgId = null;
		Long funcId = null;
		try{
			//create the test organization
			String createOrgResp = org_service.createOrganization(CREATE_ORG_REQ_1);
			orgId = getRecordId(createOrgResp);
			System.out.println(createOrgResp);
		
			//create a test function linked to the same organization
			String createFuncResp = func_service.createFunction(CREATE_FUNCTION_REQ_2.replace("$ID_ORG", orgId.toString()));
			System.out.println("CreateFunction Response: "+createFuncResp);
			
			//get the new function ID
			funcId = getRecordId(createFuncResp);
			
			//check if the createFuncResp is a well formed JSON object.	
			checkJsonWellFormed(createFuncResp);
		
			Long wrongOrgId = orgId+1;
			
			//list the specified function linked to the created organization
			String listFuncResponse = func_service.listFunction(LIST_FUNCTION_REQUEST_1.replace("$ID_ORG", wrongOrgId.toString()));
			System.out.println("ListFunction Response: "+listFuncResponse);
			
			//check if the listFuncResponse is a well formed JSON object.
			checkJsonWellFormed(listFuncResponse);
			
			Assert.assertTrue(listFuncResponse.contains("\"status\":\"error\""));
			
		}catch (Exception e){
			Assert.fail(e.getMessage());
		}finally{
			//delete the test organization created
			org_service.deleteOrganization(DELETE_ORG_REQ_1+orgId+REQ_END);						
			//delete the test function created
			func_service.deleteFunction(DELETE_FUNC_REQ_1+funcId+REQ_END);
			System.out.println("TEST ENDED: searchFunctionTestFail()");
		}
	}
	
	/**
	 * Count all the function objects in a JSON string. 
	 * @param json
	 * @return
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	private int extractFuncNumber(String json) throws JsonProcessingException, IOException{
		ObjectMapper om = new ObjectMapper();
		JsonNode root = om.readTree(json);
		ArrayNode functions = (ArrayNode) root.findValue("functions");
		return functions.size();		
	}
	
}
