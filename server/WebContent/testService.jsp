<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="it.sinergis.datacatalogue.common.PropertyReader" %>
<%@ page import="org.apache.commons.lang3.StringEscapeUtils" %>

<!DOCTYPE html>
<html>

<head>
<title>Test Page</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script>

	function prettyPrint() {
	    var ugly = document.getElementById('textArea').value;
	    var obj = JSON.parse(ugly);
	    var pretty = JSON.stringify(obj, undefined, 4);
	    document.getElementById('textArea').value = pretty;
	}
	
	function showFields(keepRequest) {
	    var select = document.getElementById("servizioSelect");
	    
	    if(select && !keepRequest) {
	    	
		    var selected = select.options[select.selectedIndex].value;
		    
		    if(selected === 'createorg' ){
		    	document.getElementById("text").style.display = "table-row";
		    	document.getElementById("textArea").title = "Insert a JSON organization.";
		    	document.getElementById("textArea").value = document.getElementById("idCreateOrgExample").value;
		    	
		    } else if(selected === 'updateorg'){
		    	document.getElementById("text").style.display = "table-row";
		    	document.getElementById("textArea").value = document.getElementById("idUpdateOrgExample").value;
		    	
		    } else if(selected === 'deleteorg'){
		    	document.getElementById("text").style.display = "table-row";
		    	document.getElementById("textArea").value = document.getElementById("idDeleteOrgExample").value;
		    	
		    } else if(selected === 'listorg'){
		    	document.getElementById("text").style.display = "table-row";
		    	document.getElementById("textArea").value = document.getElementById("idListOrgExample").value;
		    	
		    } else if(selected === 'createdataset'){
		    	document.getElementById("text").style.display = "table-row";
		    	document.getElementById("textArea").value = document.getElementById("idCreateDatasetExample").value;
		    	
		    } else if(selected === 'listdataset'){
		    	document.getElementById("text").style.display = "table-row";
		    	document.getElementById("textArea").value = document.getElementById("idListDatasetExample").value;
		    	
		    } else if(selected === 'deletedataset'){
		    	document.getElementById("text").style.display = "table-row";
		    	document.getElementById("textArea").value = document.getElementById("hiddenJSONdeleteDatasetExample").value;
		    	
		    } else if(selected === 'updatedataset'){
		    	document.getElementById("text").style.display = "table-row";
		    	document.getElementById("textArea").value = document.getElementById("hiddenJSONupdateDatasetExample").value;
		    	
		    } else if(selected === 'listcols'){
		    	document.getElementById("text").style.display = "table-row";
		    	document.getElementById("textArea").value = document.getElementById("hiddenJSONlistColsDatasetExample").value;
		    	
		    } else if(selected === 'updcolsmetadata'){
		    	document.getElementById("text").style.display = "table-row";
		    	document.getElementById("textArea").value = document.getElementById("hiddenJSONupdateColsDatasetExample").value;
		    	
		    }  else if(selected === 'createdatasrc' ){
		    	document.getElementById("text").style.display = "table-row";
		    	document.getElementById("textArea").value = document.getElementById("idCreateDataSourceExample").value;
		    	
		    } else if(selected === 'updatedatasrc'){
		    	document.getElementById("text").style.display = "table-row";
		    	document.getElementById("textArea").value = document.getElementById("idUpdateDataSourceExample").value;
		    	
		    } else if(selected === 'deletedatasrc'){
		    	document.getElementById("text").style.display = "table-row";
		    	document.getElementById("textArea").value = document.getElementById("idDeleteDataSourceExample").value;
		    	
		    } else if(selected === 'listdatasrc'){
		    	document.getElementById("text").style.display = "table-row";
		    	document.getElementById("textArea").value = document.getElementById("idListDataSourceExample").value;
		    	
		    } else if(selected === 'listdataorigin'){
		    	document.getElementById("text").style.display = "table-row";
		    	document.getElementById("textArea").value = document.getElementById("idListDataOriginExample").value;
		    	
		    } else if(selected === 'createfunc'){
		    	document.getElementById("text").style.display = "table-row";
		    	document.getElementById("textArea").value = document.getElementById("idCreateFunctionExample").value;
		    	
		    } else if(selected === 'updatefunc'){
		    	document.getElementById("text").style.display = "table-row";
		    	document.getElementById("textArea").value = document.getElementById("idUpdateFunctionExample").value;
		    	
		    } else if (selected === 'deletefunc'){
		    	document.getElementById("text").style.display = "table-row";
		    	document.getElementById("textArea").value = document.getElementById("idDeleteFunctionExample").value;
		    	
		    } else if (selected === 'listfunc'){
		    	document.getElementById("text").style.display = "table-row";
		    	document.getElementById("textArea").value = document.getElementById("idListFunctionExample").value;
		    
		    } else if(selected === 'createlyr'){
		    	document.getElementById("text").style.display = "table-row";
		    	document.getElementById("textArea").value = document.getElementById("idCreateLayerExample").value;
		    	
		    } else if(selected === 'updatelyr'){
		    	document.getElementById("text").style.display = "table-row";
		    	document.getElementById("textArea").value = document.getElementById("idUpdateLayerExample").value;
		    	
		    } else if (selected === 'deletelyr'){
		    	document.getElementById("text").style.display = "table-row";
		    	document.getElementById("textArea").value = document.getElementById("idDeleteLayerExample").value;
		    	
		    } else if (selected === 'listlyr') {
		    	document.getElementById("text").style.display = "table-row";
		    	document.getElementById("textArea").value = document.getElementById("idListLayerExample").value;
		    			    		    
		    } else if(selected === 'creategrp'){
		    	document.getElementById("text").style.display = "table-row";
		    	document.getElementById("textArea").value = document.getElementById("idCreateGroupLayerExample").value;
		    	
		    } else if(selected === 'assignlyr'){
		    	document.getElementById("text").style.display = "table-row";
		    	document.getElementById("textArea").value = document.getElementById("idAssignLayerExample").value;
		    	
		    } else if (selected === 'deletegrp'){
		    	document.getElementById("text").style.display = "table-row";
		    	document.getElementById("textArea").value = document.getElementById("idDeleteGroupLayerExample").value;
		    	
		    } else if (selected === 'listgrp'){
		    	document.getElementById("text").style.display = "table-row";
		    	document.getElementById("textArea").value = document.getElementById("idListGroupLayerExample").value;
		    
			} else if (selected === 'createapp') {
		    	document.getElementById("text").style.display = "table-row";
		    	document.getElementById("textArea").value = document.getElementById("idCreateAppExample").value;
		    	
		    } else if (selected === 'assigntoapp') {
		    	document.getElementById("text").style.display = "table-row";
		    	document.getElementById("textArea").value = document.getElementById("idUpdateAppExample").value;
		    	
		    } else if (selected === 'deleteapp') {
		    	document.getElementById("text").style.display = "table-row";
		    	document.getElementById("textArea").value = document.getElementById("idDeleteAppExample").value;
		    	
		    } else if (selected === 'listapp') {
		    	document.getElementById("text").style.display = "table-row";
		    	document.getElementById("textArea").value = document.getElementById("idListAppExample").value;
		    	
		    } else if (selected === 'pubongeoserver') {
		    	document.getElementById("text").style.display = "table-row";
		    	document.getElementById("textArea").value = document.getElementById("idpubongeoserverExample").value;
		    	
		    } else if (selected === 'getconfiguration') {
		    	document.getElementById("text").style.display = "table-row";
		    	document.getElementById("textArea").value = document.getElementById("idgetconfigurationExample").value;
		    	
		    }  else if (selected === 'login') {
		    	document.getElementById("text").style.display = "table-row";
		    	document.getElementById("textArea").value = document.getElementById("idUserLoginExample").value;
		    	
		    } else if (selected === 'reguser') {
		    	document.getElementById("text").style.display = "table-row";
		    	document.getElementById("textArea").value = document.getElementById("idUserRegisterExample").value;
		    	
		    } else if (selected === 'remindpwd') {
		    	document.getElementById("text").style.display = "table-row";
		    	document.getElementById("textArea").value = document.getElementById("idUserRemindPasswordExample").value;
		    	
		    } else if (selected === 'changepwd') {
		    	document.getElementById("text").style.display = "table-row";
		    	document.getElementById("textArea").value = document.getElementById("idUserChangePasswordExample").value;
		    	
		    } else if (selected === 'updateuser') {
		    	document.getElementById("text").style.display = "table-row";
		    	document.getElementById("textArea").value = document.getElementById("idUserUpdateProfileExample").value;
		    	
		    } else if (selected === 'lockuser') {
		    	document.getElementById("text").style.display = "table-row";
		    	document.getElementById("textArea").value = document.getElementById("idUserLockExample").value;
		    	
		    } else if (selected === 'unreguser') {
		    	document.getElementById("text").style.display = "table-row";
		    	document.getElementById("textArea").value = document.getElementById("idUserUnregisterExample").value;	
		    } 
		    else if (selected === 'createrole'){
		    	document.getElementById("text").style.display = "table-row";
		    	document.getElementById("textArea").value = document.getElementById("idCreateRoleExample").value;
		    	
		    } 
		    else if (selected === 'deleterole'){
		    	document.getElementById("text").style.display = "table-row";
		    	document.getElementById("textArea").value = document.getElementById("idDeleteRoleExample").value;
		    } 
		    else if (selected === 'listrole'){
		    	document.getElementById("text").style.display = "table-row";
		    	document.getElementById("textArea").value = document.getElementById("idListRoleExample").value;
		    } 
		    else if (selected === 'assignrole'){
		    	document.getElementById("text").style.display = "table-row";
		    	document.getElementById("textArea").value = document.getElementById("idAssignRoleExample").value;
		    }
		    else if (selected === 'assignperm'){
		    	document.getElementById("text").style.display = "table-row";
		    	document.getElementById("textArea").value = document.getElementById("idAssignPermissionExample").value;
		    } 
		    else if (selected === 'listperm'){
		    	document.getElementById("text").style.display = "table-row";
		    	document.getElementById("textArea").value = document.getElementById("idListPermissionExample").value;
		    }
		    
		    prettyPrint();
   		}
	};
</script>
<STYLE type="text/css">
body,tr,td {
	font-size: 12px;
	font-family: Verdana
}
</STYLE>
</head>
<body>
<table>
	<tr>
		<td><b>
		<h2>Test page</h2>
		</b></td>
	</tr>
</table>
<%!
public String handleEscaping(String jsonExample){
	PropertyReader pr = new PropertyReader("testservices.properties");
	return StringEscapeUtils.escapeHtml4(pr.getValue(jsonExample)).replace("\\","\\\\").replaceAll("\\p{Cntrl}", "");
}
%>
<input type="hidden" id="idCreateOrgExample" value="<%out.print(handleEscaping("createOrgExample"));%>"></input>
<input type="hidden" id="idUpdateOrgExample" value="<%out.print(handleEscaping("updateOrgExample"));%>"></input>
<input type="hidden" id="idDeleteOrgExample" value="<%out.print(handleEscaping("deleteOrgExample"));%>"></input>
<input type="hidden" id="idListOrgExample" value="<%out.print(handleEscaping("listOrgExample"));%>"></input>
<input type="hidden" id="idCreateDatasetExample" value="<%out.print(handleEscaping("createDatasetExample"));%>"></input>
<input type="hidden" id="idListDatasetExample" value="<%out.print(handleEscaping("listDatasetExample"));%>"></input>
<input type="hidden" id="hiddenJSONdeleteDatasetExample" value="<%out.print(handleEscaping("deleteDatasetExample"));%>"></input>
<input type="hidden" id="hiddenJSONupdateDatasetExample" value="<%out.print(handleEscaping("updateDatasetExample"));%>"></input>
<input type="hidden" id="hiddenJSONlistColsDatasetExample" value="<%out.print(handleEscaping("listColsDatasetExample"));%>"></input>
<input type="hidden" id="hiddenJSONupdateColsDatasetExample" value="<%out.print(handleEscaping("updateColsDatasetExample"));%>"></input>
<input type="hidden" id="idCreateDataSourceExample" value="<%out.print(handleEscaping("createDataSourceExample"));%>"></input>
<input type="hidden" id="idUpdateDataSourceExample" value="<%out.print(handleEscaping("updateDataSourceExample"));%>"></input>
<input type="hidden" id="idDeleteDataSourceExample" value="<%out.print(handleEscaping("deleteDataSourceExample"));%>"></input>
<input type="hidden" id="idListDataSourceExample" value="<%out.print(handleEscaping("listDataSourceExample"));%>"></input>
<input type="hidden" id="idListDataOriginExample" value="<%out.print(handleEscaping("listDataOriginExample"));%>"></input>
<input type="hidden" id="idCreateFunctionExample" value="<%out.print(handleEscaping("createFunctionExample"));%>"></input>
<input type="hidden" id="idUpdateFunctionExample" value="<%out.print(handleEscaping("updateFunctionExample"));%>"></input>
<input type="hidden" id="idDeleteFunctionExample" value="<%out.print(handleEscaping("deleteFunctionExample"));%>"></input>
<input type="hidden" id="idListFunctionExample" value="<%out.print(handleEscaping("listFunctionExample"));%>"></input>
<input type="hidden" id="idCreateLayerExample" value="<%out.print(handleEscaping("createLayerExample"));%>"></input>
<input type="hidden" id="idUpdateLayerExample" value="<%out.print(handleEscaping("updateLayerExample"));%>"></input>
<input type="hidden" id="idDeleteLayerExample" value="<%out.print(handleEscaping("deleteLayerExample"));%>"></input>
<input type="hidden" id="idListLayerExample" value="<%out.print(handleEscaping("listLayerExample"));%>"></input>
<input type="hidden" id="idCreateGroupLayerExample" value="<%out.print(handleEscaping("createGroupLayerExample"));%>"></input>
<input type="hidden" id="idAssignLayerExample" value="<%out.print(handleEscaping("assignLayerExample"));%>"></input>
<input type="hidden" id="idDeleteGroupLayerExample" value="<%out.print(handleEscaping("deleteGroupLayerExample"));%>"></input>
<input type="hidden" id="idListGroupLayerExample" value="<%out.print(handleEscaping("listGroupLayerExample"));%>"></input>
<input type="hidden" id="idCreateAppExample" value="<%out.print(handleEscaping("createAppExample"));%>"></input>
<input type="hidden" id="idUpdateAppExample" value="<%out.print(handleEscaping("updateAppExample"));%>"></input>
<input type="hidden" id="idDeleteAppExample" value="<%out.print(handleEscaping("deleteAppExample"));%>"></input>
<input type="hidden" id="idListAppExample" value="<%out.print(handleEscaping("listAppExample"));%>"></input>
<input type="hidden" id="idpubongeoserverExample" value="<%out.print(handleEscaping("pubongeoserverExample"));%>"></input>
<input type="hidden" id="idgetconfigurationExample" value="<%out.print(handleEscaping("getconfigurationExample"));%>"></input>
<input type="hidden" id="idUserLoginExample" value="<%out.print(handleEscaping("userLoginExample"));%>"></input>
<input type="hidden" id="idUserRegisterExample" value="<%out.print(handleEscaping("userRegisterExample"));%>"></input>
<input type="hidden" id="idUserRemindPasswordExample" value="<%out.print(handleEscaping("userRemindPasswordExample"));%>"></input>
<input type="hidden" id="idUserChangePasswordExample" value="<%out.print(handleEscaping("userChangePasswordExample"));%>"></input>
<input type="hidden" id="idUserUpdateProfileExample" value="<%out.print(handleEscaping("userUpdateProfileExample"));%>"></input>
<input type="hidden" id="idUserLockExample" value="<%out.print(handleEscaping("userLockExample"));%>"></input>
<input type="hidden" id="idUserUnregisterExample" value="<%out.print(handleEscaping("userUnregisterExample"));%>"></input>
<input type="hidden" id="idCreateRoleExample" value="<%out.print(handleEscaping("createRoleExample"));%>"></input>
<input type="hidden" id="idDeleteRoleExample" value="<%out.print(handleEscaping("deleteRoleExample"));%>"></input>
<input type="hidden" id="idListRoleExample" value="<%out.print(handleEscaping("listRoleExample"));%>"></input>
<input type="hidden" id="idAssignRoleExample" value="<%out.print(handleEscaping("assignRoleExample"));%>"></input>
<input type="hidden" id="idListPermissionExample" value="<%out.print(handleEscaping("listPermissionExample"));%>"></input>
<input type="hidden" id="idAssignPermissionExample" value="<%out.print(handleEscaping("assignPermissionExample"));%>"></input>


<form action="datacatalogservlet" method="post" id="serviceForm" accept-charset="UTF-8">
<%
String servizio=(String)request.getAttribute("actionName");
if (servizio==null) {
	servizio="";
} else {
	{ %> <script type="text/javascript"> showFields(true); </script> <% }
}

%>

<table>
	<tr>
		<td>Service</td>
		<td><select name="actionName" id="servizioSelect" onchange="showFields(false)">
			<option value="ChooseService" >Choose service...</option>
			<option value="createorg" <%= servizio.equals("createorg") ? "selected=\"selected\"" : ""%>>Organization - Create</option>
			<option value="updateorg" <%= servizio.equals("updateorg") ? "selected=\"selected\"" : ""%>>Organization - Update</option>
            <option value="deleteorg" <%= servizio.equals("deleteorg") ? "selected=\"selected\"" : ""%>>Organization - Delete</option>
            <option value="listorg" <%= servizio.equals("listorg") ? "selected=\"selected\"" : ""%>>Organization - List/Search</option>
            <option value="createdataset" <%= servizio.equals("createdataset") ? "selected=\"selected\"" : ""%>>Dataset - Create</option>
            <option value="listdataset" <%= servizio.equals("listdataset") ? "selected=\"selected\"" : ""%>>Dataset - List/Search</option>
            <option value="deletedataset" <%= servizio.equals("deletedataset") ? "selected=\"selected\"" : ""%>>Dataset - Delete</option>
            <option value="updatedataset" <%= servizio.equals("updatedataset") ? "selected=\"selected\"" : ""%>>Dataset - Update</option>
            <option value="listcols" <%= servizio.equals("listcols") ? "selected=\"selected\"" : ""%>>Dataset - List Columns</option>
            <option value="updcolsmetadata" <%= servizio.equals("updcolsmetadata") ? "selected=\"selected\"" : ""%>>Dataset - Update Columns Metadata</option>
            <option value="createdatasrc" <%= servizio.equals("createdatasrc") ? "selected=\"selected\"" : ""%>>Datasource - Create</option>
            <option value="listdatasrc" <%= servizio.equals("listdatasrc") ? "selected=\"selected\"" : ""%>>Datasource - List/Search</option>
            <option value="deletedatasrc" <%= servizio.equals("deletedatasrc") ? "selected=\"selected\"" : ""%>>Datasource - Delete</option>
            <option value="updatedatasrc" <%= servizio.equals("updatedatasrc") ? "selected=\"selected\"" : ""%>>Datasource - Update</option>
            <option value="listdataorigin" <%= servizio.equals("listdataorigin") ? "selected=\"selected\"" : ""%>>Datasource - List Data Origin</option>
            <option value="createfunc" <%= servizio.equals("createfunc") ? "selected=\"selected\"" : ""%>>Function - Create</option>
            <option value="updatefunc" <%= servizio.equals("updatefunc") ? "selected=\"selected\"" : ""%>>Function - Update</option>
            <option value="deletefunc" <%= servizio.equals("deletefunc") ? "selected=\"selected\"" : ""%>>Function - Delete</option>
            <option value="listfunc" <%= servizio.equals("listfunc") ? "selected=\"selected\"" : ""%>>Function - List/Search</option>
            <option value="createlyr" <%= servizio.equals("createlyr") ? "selected=\"selected\"" : ""%>>Layer - Create</option>
            <option value="updatelyr" <%= servizio.equals("updatelyr") ? "selected=\"selected\"" : ""%>>Layer - Update</option>
            <option value="deletelyr" <%= servizio.equals("deletelyr") ? "selected=\"selected\"" : ""%>>Layer - Delete</option>
            <option value="listlyr" <%= servizio.equals("listlyr") ? "selected=\"selected\"" : ""%>>Layer - List/Search</option>
            <option value="creategrp" <%= servizio.equals("creategrp") ? "selected=\"selected\"" : ""%>>Layer Group - Create</option>
            <option value="assignlyr" <%= servizio.equals("assignlyr") ? "selected=\"selected\"" : ""%>>Layer Group - Assign Layer</option>
            <option value="deletegrp" <%= servizio.equals("deletegrp") ? "selected=\"selected\"" : ""%>>Layer Group - Delete</option>
            <option value="listgrp" <%= servizio.equals("listgrp") ? "selected=\"selected\"" : ""%>>Layer Group - List/Search</option>
            <option value="createapp" <%= servizio.equals("createapp") ? "selected=\"selected\"" : ""%>>Application - Create</option>
            <option value="assigntoapp" <%= servizio.equals("assigntoapp") ? "selected=\"selected\"" : ""%>>Application - Update/Assign layers/group to application</option>
            <option value="deleteapp" <%= servizio.equals("deleteapp") ? "selected=\"selected\"" : ""%>>Application - Delete</option>
            <option value="listapp" <%= servizio.equals("listapp") ? "selected=\"selected\"" : ""%>>Application - List/Search</option>
            <option value="pubongeoserver" <%= servizio.equals("pubongeoserver") ? "selected=\"selected\"" : ""%>>Application - Publish on geoserver</option>
            <option value="getconfiguration" <%= servizio.equals("getconfiguration") ? "selected=\"selected\"" : ""%>>Application - GetConfiguration</option>
            <option value="login" <%= servizio.equals("login") ? "selected=\"selected\"" : ""%>>User - Login</option>
            <option value="reguser" <%= servizio.equals("reguser") ? "selected=\"selected\"" : ""%>>User - Register</option>
            <option value="remindpwd" <%= servizio.equals("remindpwd") ? "selected=\"selected\"" : ""%>>User - Remind Password</option>
            <option value="changepwd" <%= servizio.equals("changepwd") ? "selected=\"selected\"" : ""%>>User - Change Password</option>
            <option value="updateuser" <%= servizio.equals("updateuser") ? "selected=\"selected\"" : ""%>>User - Update Profile</option>
            <option value="lockuser" <%= servizio.equals("lockuser") ? "selected=\"selected\"" : ""%>>User - Lock/Unlock</option>
            <option value="unreguser" <%= servizio.equals("unreguser") ? "selected=\"selected\"" : ""%>>User - Unregister/Delete</option>
            <option value="createrole" <%= servizio.equals("createrole") ? "selected=\"selected\"" : ""%>>Role - Create</option>
            <option value="deleterole" <%= servizio.equals("deleteerole") ? "selected=\"selected\"" : ""%>>Role - Delete</option>
            <option value="listrole" <%= servizio.equals("listrole") ? "selected=\"selected\"" : ""%>>Role - List/Search</option>
            <option value="assignrole" <%= servizio.equals("assignrole") ? "selected=\"selected\"" : ""%>>Role - Assign users</option>
            <option value="listperm" <%= servizio.equals("listperm") ? "selected=\"selected\"" : ""%>>Permission - List</option>
            <option value="assignperm" <%= servizio.equals("assignperm") ? "selected=\"selected\"" : ""%>>Permission - Assign</option>
            								
			<option value=""></option>
		</select></td>
	</tr>
	<tr id="text" style="display:table-row">	 
		<td>Text *</td>
		<td><textarea id="textArea" name="request" cols="120" rows="30"><%if (request.getAttribute("text") != null)
				out.print(request.getAttribute("text"));%></textarea></td>
	</tr>	
	<tr id="submitButton">
		<td></td>
		<td>
		<INPUT type="submit" value="Submit">
		</td>
	</tr>
</table>
<%
servizio=(String)request.getAttribute("actionName");
if (servizio != "") {
	{ %> <script type="text/javascript"> showFields(true); </script> <% }
}

%>
</form>
</body>
</html>
