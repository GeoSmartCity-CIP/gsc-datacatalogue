package it.sinergis.datacatalogue.servlet;

import it.sinergis.datacatalogue.exception.DCException;
import it.sinergis.datacatalogue.services.ApplicationsService;
import it.sinergis.datacatalogue.services.DatasetsService;
import it.sinergis.datacatalogue.services.DatasourcesService;
import it.sinergis.datacatalogue.services.FunctionsService;
import it.sinergis.datacatalogue.services.GroupLayersService;
import it.sinergis.datacatalogue.services.LayersService;
import it.sinergis.datacatalogue.services.OrganizationsService;
import it.sinergis.datacatalogue.services.PermissionsService;
import it.sinergis.datacatalogue.services.RolesService;
import it.sinergis.datacatalogue.services.UsersService;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * Servlet implementation class DataCatalogServlet
 */
//@WebServlet("/DataCatalogServlet")
public class DataCatalogServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static Logger logger;
	
	/**
     * Default constructor. 
     */
    public DataCatalogServlet() {}
    
    
	public void init(ServletConfig config) throws ServletException {
		// Inizializzazione
		logger = Logger.getLogger(this.getClass());		
	}

	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter writer = response.getWriter();
		
		try {
			request.setCharacterEncoding("UTF-8");
			
            String action = request.getParameter("actionName");
            String requestJson = request.getParameter("request");
                        
            if (action != null && !action.equals("")) {
            	response.setContentType("application/json");
            	
            	String serviceResp = null;
            	/* ******************************* USERS ******************************* */
            	if(action.equalsIgnoreCase("login")){
            		UsersService service = new UsersService();
            		serviceResp = service.login(requestJson);            		            		
            	}
            	else if(action.equalsIgnoreCase("reguser")){
            		UsersService service = new UsersService();
            		serviceResp = service.registerUser(requestJson);            		   
            	}
            	else if(action.equalsIgnoreCase("remindpwd")){
            		UsersService service = new UsersService();
            		serviceResp = service.remindPassword(requestJson);
            		   
            	}
            	else if(action.equalsIgnoreCase("changepwd")){
            		UsersService service = new UsersService();
            		serviceResp = service.changePassword(requestJson);            		
            	}
            	else if(action.equalsIgnoreCase("updateuser")){
            		UsersService service = new UsersService();
            		serviceResp = service.updateUser(requestJson);
            	}
            	else if(action.equalsIgnoreCase("lockuser")){
            		UsersService service = new UsersService();
            		serviceResp = service.lockUser(requestJson);
            	}
            	else if(action.equalsIgnoreCase("unreguser")){
            		UsersService service = new UsersService();
            		serviceResp = service.unregisterUser(requestJson);
            	}
            	/* *******************************  ORGANIZATION ******************************* */
            	else if(action.equalsIgnoreCase("createorg")){
            		OrganizationsService service = new OrganizationsService();
            		serviceResp = service.createOrganization(requestJson);            		
            	}
            	else if(action.equalsIgnoreCase("updateorg")){
            		OrganizationsService service = new OrganizationsService();
            		serviceResp = service.updateOrganization(requestJson); 
            	}
            	else if(action.equalsIgnoreCase("deleteorg")){
            		OrganizationsService service = new OrganizationsService();
            		serviceResp = service.deleteOrganization(requestJson); 
            	}
            	else if(action.equalsIgnoreCase("listorg")){
            		OrganizationsService service = new OrganizationsService();
            		serviceResp = service.listOrganization(requestJson); 
            	}
            	/* ******************************* ROLE ******************************* */
            	else if(action.equalsIgnoreCase("createrole")){
            		RolesService service = new RolesService();
            		serviceResp = service.createRole(requestJson);
            	}
            	else if(action.equalsIgnoreCase("deleterole")){
            		RolesService service = new RolesService();
            		serviceResp = service.deleteRole(requestJson);
            	}
            	else if(action.equalsIgnoreCase("listrole")){
            		RolesService service = new RolesService();
            		serviceResp = service.listRole(requestJson);
            	}
            	else if(action.equalsIgnoreCase("assignrole")){
            		RolesService service = new RolesService();
            		serviceResp = service.assignRole(requestJson);
            	}
            	/* ******************************* FUNCTION ******************************* */
            	else if(action.equalsIgnoreCase("createfunc")){
            		FunctionsService service = new FunctionsService();
            		serviceResp = service.createFunction(requestJson);
            	}
            	else if(action.equalsIgnoreCase("updatefunc")){
            		FunctionsService service = new FunctionsService();
            		serviceResp = service.updateFunction(requestJson);
            	}
            	else if(action.equalsIgnoreCase("deletefunc")){
            		FunctionsService service = new FunctionsService();
            		serviceResp = service.deleteFunction(requestJson);
            	}
            	else if(action.equalsIgnoreCase("listfunc")){
            		FunctionsService service = new FunctionsService();
            		serviceResp = service.listFunction(requestJson);
            	}
            	/* ******************************* PERMISSION ******************************* */
            	else if(action.equalsIgnoreCase("assignperm")){
            		PermissionsService service = new PermissionsService();
            		serviceResp = service.assignPermission(requestJson);
            	}
            	else if(action.equalsIgnoreCase("listperm")){
            		PermissionsService service = new PermissionsService();
            		serviceResp = service.listPermission(requestJson);
            	}
            	/* ******************************* DATASOURCE ******************************* */
            	else if(action.equalsIgnoreCase("createdatasrc")){
            		DatasourcesService service = new DatasourcesService();
            		serviceResp = service.createDatasource(requestJson);
            	}
            	else if(action.equalsIgnoreCase("updatedatasrc")){
            		DatasourcesService service = new DatasourcesService();
            		serviceResp = service.updateDatasource(requestJson);
            	}
            	else if(action.equalsIgnoreCase("deletedatasrc")){
            		DatasourcesService service = new DatasourcesService();
            		serviceResp = service.deleteDatasource(requestJson);
            	}
            	else if(action.equalsIgnoreCase("listdatasrc")){
            		DatasourcesService service = new DatasourcesService();
            		serviceResp = service.listDatasource(requestJson);
            	}
            	else if(action.equalsIgnoreCase("uploadfile")){
            		DatasourcesService service = new DatasourcesService();
            		serviceResp = service.uploadDatasource(requestJson);
            	}
            	else if(action.equalsIgnoreCase("pubtockan")){
            		DatasourcesService service = new DatasourcesService();
            		serviceResp = service.ckanDatasource(requestJson);
            	}
            	else if(action.equalsIgnoreCase("listdataorigin")){
            		DatasourcesService service = new DatasourcesService();
            		serviceResp = service.listDataOrigin(requestJson);
            	}
            	/* ******************************* DATASET ******************************* */
            	else if(action.equalsIgnoreCase("createdataset")){
            		DatasetsService service = new DatasetsService();
            		serviceResp = service.createDataset(requestJson);
            	}
            	else if(action.equalsIgnoreCase("updatedataset")){
            		DatasetsService service = new DatasetsService();
            		serviceResp = service.updateDataset(requestJson);
            	}
            	else if(action.equalsIgnoreCase("deletedataset")){
            		DatasetsService service = new DatasetsService();
            		serviceResp = service.deleteDataset(requestJson);
            	}
            	else if(action.equalsIgnoreCase("listdataset")){
            		DatasetsService service = new DatasetsService();
            		serviceResp = service.listDataset(requestJson);
            	}
            	else if(action.equalsIgnoreCase("listcols")){
            		DatasetsService service = new DatasetsService();
            		serviceResp = service.listColumns(requestJson);
            	}
            	else if(action.equalsIgnoreCase("updcolsmetadata")){
            		DatasetsService service = new DatasetsService();
            		serviceResp = service.updateColumnsMetadata(requestJson);
            	}
            	else if(action.equalsIgnoreCase("createcron")){
            		DatasetsService service = new DatasetsService();
            		serviceResp = service.createCronService(requestJson);
            	}
            	/* ******************************* LAYER ******************************* */
            	else if(action.equalsIgnoreCase("createlyr")){
            		LayersService service = new LayersService();
            		serviceResp = service.createLayer(requestJson);
            	}
            	else if(action.equalsIgnoreCase("updatelyr")){
            		LayersService service = new LayersService();
            		serviceResp = service.updateLayer(requestJson);
            	}
            	else if(action.equalsIgnoreCase("deletelyr")){
            		LayersService service = new LayersService();
            		serviceResp = service.deleteLayer(requestJson);
            	}
            	else if(action.equalsIgnoreCase("listlyr")){
            		LayersService service = new LayersService();
            		serviceResp = service.listLayer(requestJson);
            	}
            	/* ******************************* GROUPLAYER ******************************* */
            	else if(action.equalsIgnoreCase("creategrp")){
            		GroupLayersService service = new GroupLayersService();
            		serviceResp = service.createGroupLayer(requestJson);
            	}
            	else if(action.equalsIgnoreCase("assignlyr")){
            		GroupLayersService service = new GroupLayersService();
            		serviceResp = service.assignLayerToGroup(requestJson);
            	}
            	else if(action.equalsIgnoreCase("deletegrp")){
            		GroupLayersService service = new GroupLayersService();
            		serviceResp = service.deleteGroupLayer(requestJson);
            	}
            	else if(action.equalsIgnoreCase("listgrp")){
            		GroupLayersService service = new GroupLayersService();
            		serviceResp = service.listGroupLayer(requestJson);
            	}
            	/* ******************************* APPLICATION ******************************* */
            	else if(action.equalsIgnoreCase("createapp")){
            		ApplicationsService service = new ApplicationsService();
            		serviceResp = service.createApplication(requestJson);
            	}
            	else if(action.equalsIgnoreCase("assigntoapp")){
            		ApplicationsService service = new ApplicationsService();
            		serviceResp = service.assignToApplication(requestJson);
            	}
            	else if(action.equalsIgnoreCase("deleteapp")){
            		ApplicationsService service = new ApplicationsService();
            		serviceResp = service.deleteApplication(requestJson);
            	}
            	else if(action.equalsIgnoreCase("listapp")){
            		ApplicationsService service = new ApplicationsService();
            		serviceResp = service.listApplication(requestJson);
            	}
            	else if(action.equalsIgnoreCase("pubongeoserver")){
            		ApplicationsService service = new ApplicationsService();
            		serviceResp = service.publishToGeoserver(requestJson);
            	}
            	else if(action.equalsIgnoreCase("getconfiguration")){
            		ApplicationsService service = new ApplicationsService();
            		serviceResp = service.getConfiguration(requestJson);
            	}
            	            	
        		writer.write(serviceResp);
        		        		
            }
		}
		catch (Exception e){	
			logger.error("Generic error while executing datacatalogue service",e);
			
			DCException rpe = new DCException("ER01");
			writer.write(rpe.returnErrorString());
			
		} 
		finally {
            if (writer != null) {
            	writer.flush();
        		writer.close();
            }
        }
	}

}
