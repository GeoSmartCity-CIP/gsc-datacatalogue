<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="it.sinergis.datacatalogue.common.PropertyReader" %>
<%@ page import="org.apache.commons.lang3.StringEscapeUtils" %>

<!DOCTYPE html>
<html>

<head>
<title>Test Page</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script>
	
	function showFields(keepRequest) {
	    var select = document.getElementById("servizioSelect");
	    
	    if(select && !keepRequest) {
	    	document.getElementById("responseJSONId").value = "";
	    	
		    var selected = select.options[select.selectedIndex].value;
		    
		    if(selected === 'createorg' ){
		    	document.getElementById("text").style.display = "table-row";
		    	document.getElementById("textArea").title = "Insert a JSON organization.";
		    	document.getElementById("textArea").value = document.getElementById("hiddenJSONinputCreateOrgExample").value;
		    	
		    } else if(selected === 'updateorg'){
		    	document.getElementById("text").style.display = "table-row";
		    	document.getElementById("textArea").value = document.getElementById("hiddenJSONinputUpdateOrgExample").value;
		    	
		    } else if(selected === 'deleteorg'){
		    	document.getElementById("text").style.display = "table-row";
		    	document.getElementById("textArea").value = document.getElementById("hiddenJSONinputDeleteOrgExample").value;
		    	
		    } else if(selected === 'listorg'){
		    	document.getElementById("text").style.display = "table-row";
		    	document.getElementById("textArea").value = document.getElementById("hiddenJSONinputListOrgExample").value;
		    	
		    } else if(selected === 'createdataset'){
		    	document.getElementById("text").style.display = "table-row";
		    	document.getElementById("textArea").value = document.getElementById("hiddenJSONinputCreateDatasetExample").value;
		    	
		    } else if(selected === 'listdataset'){
		    	document.getElementById("text").style.display = "table-row";
		    	document.getElementById("textArea").value = document.getElementById("hiddenJSONinputListDatasetExample").value;
		    	
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
		    	document.getElementById("textArea").value = document.getElementById("hiddenJSONinputCreateDataSourceExample").value;
		    	
		    } else if(selected === 'updatedatasrc'){
		    	document.getElementById("text").style.display = "table-row";
		    	document.getElementById("textArea").value = document.getElementById("hiddenJSONinputUpdateDataSourceExample").value;
		    	
		    } else if(selected === 'deletedatasrc'){
		    	document.getElementById("text").style.display = "table-row";
		    	document.getElementById("textArea").value = document.getElementById("hiddenJSONinputDeleteDataSourceExample").value;
		    	
		    } else if(selected === 'listdatasrc'){
		    	document.getElementById("text").style.display = "table-row";
		    	document.getElementById("textArea").value = document.getElementById("hiddenJSONinputListDataSourceExample").value;
		    	
		    }else if(selected === 'createfunc'){
		    	document.getElementById("text").style.display = "table-row";
		    	document.getElementById("textArea").value = document.getElementById("hiddenJSONinputCreateFunctionExample").value;
		    	
		    }else if(selected === 'updatefunc'){
		    	document.getElementById("text").style.display = "table-row";
		    	document.getElementById("textArea").value = document.getElementById("hiddenJSONinputUpdateFunctionExample").value;
		    	
		    }else if (selected === 'deletefunc'){
		    	document.getElementById("text").style.display = "table-row";
		    	document.getElementById("textArea").value = document.getElementById("hiddenJSONinputDeleteFunctionExample").value;
		    	
		    }else if (selected === 'listfunc'){
		    	document.getElementById("text").style.display = "table-row";
		    	document.getElementById("textArea").value = document.getElementById("hiddenJSONinputListFunctionExample").value;
		    }
	    	//document.getElementById("submitButton").style.display = "table-row";
	    	//document.getElementById("response").style.display = "table-row";
	    	
	    	//document.getElementById('responseJSONId').value = JSON.stringify(JSON.parse(document.getElementById('responseJSONId').value), null, 2);
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
<% PropertyReader pr = new PropertyReader("testservices.properties"); %>
<input type="hidden" id="hiddenJSONinputCreateOrgExample" value="<%  out.print(StringEscapeUtils.escapeHtml4(pr.getValue("JSONinputCreateOrgExample").replace("\\","\\\\").replaceAll("\\p{Cntrl}", "")));%>"></input>
<input type="hidden" id="hiddenJSONinputUpdateOrgExample" value="<%  out.print(StringEscapeUtils.escapeHtml4(pr.getValue("JSONinputUpdateOrgExample").replace("\\","\\\\").replaceAll("\\p{Cntrl}", "")));%>"></input>
<input type="hidden" id="hiddenJSONinputDeleteOrgExample" value="<%  out.print(StringEscapeUtils.escapeHtml4(pr.getValue("JSONinputDeleteOrgExample").replace("\\","\\\\").replaceAll("\\p{Cntrl}", "")));%>"></input>
<input type="hidden" id="hiddenJSONinputListOrgExample" value="<%  out.print(StringEscapeUtils.escapeHtml4(pr.getValue("JSONinputListOrgExample").replace("\\","\\\\").replaceAll("\\p{Cntrl}", "")));%>"></input>
<input type="hidden" id="hiddenJSONinputCreateDatasetExample" value="<%  out.print(StringEscapeUtils.escapeHtml4(pr.getValue("JSONinputCreateDatasetExample").replace("\\","\\\\").replaceAll("\\p{Cntrl}", "")));%>"></input>
<input type="hidden" id="hiddenJSONinputListDatasetExample" value="<%  out.print(StringEscapeUtils.escapeHtml4(pr.getValue("JSONinputListDatasetExample").replace("\\","\\\\").replaceAll("\\p{Cntrl}", "")));%>"></input>
<input type="hidden" id="hiddenJSONdeleteDatasetExample" value="<%  out.print(StringEscapeUtils.escapeHtml4(pr.getValue("JSONdeleteDatasetExample").replace("\\","\\\\").replaceAll("\\p{Cntrl}", "")));%>"></input>
<input type="hidden" id="hiddenJSONupdateDatasetExample" value="<%  out.print(StringEscapeUtils.escapeHtml4(pr.getValue("JSONupdateDatasetExample").replace("\\","\\\\").replaceAll("\\p{Cntrl}", "")));%>"></input>
<input type="hidden" id="hiddenJSONlistColsDatasetExample" value="<%  out.print(StringEscapeUtils.escapeHtml4(pr.getValue("JSONlistColsDatasetExample").replace("\\","\\\\").replaceAll("\\p{Cntrl}", "")));%>"></input>
<input type="hidden" id="hiddenJSONupdateColsDatasetExample" value="<%  out.print(StringEscapeUtils.escapeHtml4(pr.getValue("JSONupdateColsDatasetExample").replace("\\","\\\\").replaceAll("\\p{Cntrl}", "")));%>"></input>
<input type="hidden" id="hiddenJSONinputCreateDataSourceExample" value="<%  out.print(StringEscapeUtils.escapeHtml4(pr.getValue("JSONinputCreateDataSourceExample").replace("\\","\\\\").replaceAll("\\p{Cntrl}", "")));%>"></input>
<input type="hidden" id="hiddenJSONinputUpdateDataSourceExample" value="<%  out.print(StringEscapeUtils.escapeHtml4(pr.getValue("JSONinputUpdateDataSourceExample").replace("\\","\\\\").replaceAll("\\p{Cntrl}", "")));%>"></input>
<input type="hidden" id="hiddenJSONinputDeleteDataSourceExample" value="<%  out.print(StringEscapeUtils.escapeHtml4(pr.getValue("JSONinputDeleteDataSourceExample").replace("\\","\\\\").replaceAll("\\p{Cntrl}", "")));%>"></input>
<input type="hidden" id="hiddenJSONinputListDataSourceExample" value="<%  out.print(StringEscapeUtils.escapeHtml4(pr.getValue("JSONinputListDataSourceExample").replace("\\","\\\\").replaceAll("\\p{Cntrl}", "")));%>"></input>
<input type="hidden" id="hiddenJSONinputCreateFunctionExample" value="<%  out.print(StringEscapeUtils.escapeHtml4(pr.getValue("JSONinputCreateFunctionExample").replace("\\","\\\\").replaceAll("\\p{Cntrl}", "")));%>"></input>
<input type="hidden" id="hiddenJSONinputUpdateFunctionExample" value="<%  out.print(StringEscapeUtils.escapeHtml4(pr.getValue("JSONinputUpdateFunctionExample").replace("\\","\\\\").replaceAll("\\p{Cntrl}", "")));%>"></input>
<input type="hidden" id="hiddenJSONinputDeleteFunctionExample" value="<%  out.print(StringEscapeUtils.escapeHtml4(pr.getValue("JSONinputDeleteFunctionExample").replace("\\","\\\\").replaceAll("\\p{Cntrl}", "")));%>"></input>
<input type="hidden" id="hiddenJSONinputListFunctionExample" value="<%  out.print(StringEscapeUtils.escapeHtml4(pr.getValue("JSONinputListFunctionExample").replace("\\","\\\\").replaceAll("\\p{Cntrl}", "")));%>"></input>

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
            <option value="createfunc" <%= servizio.equals("createfunc") ? "selected=\"selected\"" : ""%>>Function - Create</option>
            <option value="updatefunc" <%= servizio.equals("updatefunc") ? "selected=\"selected\"" : ""%>>Function - Update</option>
            <option value="deletefunc" <%= servizio.equals("deletefunc") ? "selected=\"selected\"" : ""%>>Function - Delete</option>
            <option value="listfunc" <%= servizio.equals("listfunc") ? "selected=\"selected\"" : ""%>>Function - List/Search</option>
            							
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
  	<tr id="response">
		<td>Response</td>
		<td><pre><textarea name="responseJSON" id="responseJSONId" cols="120" rows="15"><%
			if (request.getAttribute("responseJSON") != null)
				out.print(request.getAttribute("responseJSON"));%></textarea></pre></td>
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
