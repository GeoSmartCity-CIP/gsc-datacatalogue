<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="it.sinergis.datacatalogue.common.PropertyReader" %>
<%@ page import="org.apache.commons.lang3.StringEscapeUtils" %>

<!DOCTYPE html>
<html>

<head>
<title>Test Page</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script>
	
	function showFields() {
	    var select = document.getElementById("servizioSelect");
	    if(select) {
		    var selected = select.options[select.selectedIndex].value;
		    
		    if(selected === 'createorg'){
		    	document.getElementById("text").style.display = "table-row";
		    	document.getElementById("textArea").title = "Insert a JSON organization.";
		    	document.getElementById("textArea").value = document.getElementById("hiddenJSONinputCreateOrgExample").value;
		    	
		    } else if(selected === 'updateorg'){
		    	document.getElementById("text").style.display = "table-row";
		    	document.getElementById("textArea").title = "Insert a JSON organization.";
		    	document.getElementById("textArea").value = document.getElementById("hiddenJSONinputUpdateOrgExample").value;
		    	
		    } else if(selected === 'deleteorg'){
		    	document.getElementById("text").style.display = "table-row";
		    	document.getElementById("textArea").title = "Insert a JSON organization.";
		    	document.getElementById("textArea").value = document.getElementById("hiddenJSONinputDeleteOrgExample").value;
		    	
		    } else if(selected === 'listorg'){
		    	document.getElementById("text").style.display = "table-row";
		    	document.getElementById("textArea").title = "Insert a JSON organization.";
		    	document.getElementById("textArea").value = document.getElementById("hiddenJSONinputCreateOrgExample").value;
		    	
		    }
	    	document.getElementById("submitButton").style.display = "table-row";
	    	document.getElementById("response").style.display = "table-row";
	    	
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


<form action="datacatalogservlet" method="post" id="serviceForm" accept-charset="UTF-8">
<%
String servizio=(String)request.getAttribute("servizio");
if (servizio==null) {
	servizio="";
} else {
	{ %> <script type="text/javascript"> showFields(); </script> <% }
}

%>
<table>
	<tr>
		<td>Service</td>
		<td><select name="actionName" id="servizioSelect" onchange="showFields()">
			<option value="ChooseService" >Choose service...</option>
			<option value="createorg" <%= servizio.equals("createorg") ? "selected=\"selected\"" : ""%>>Organization - Create</option>
			<option value="updateorg" <%= servizio.equals("updateorg") ? "selected=\"selected\"" : ""%>>Organization - Update</option>
            <option value="deleteorg <%= servizio.equals("deleteorg") ? "selected=\"selected\"" : ""%>">Organization - Delete</option>
            <option value="listorg" <%= servizio.equals("listorg") ? "selected=\"selected\"" : ""%>>Organization - List/Search</option>
            			
			<option value=""></option>
		</select></td>
	</tr>
	<tr id="text" style="display:table-row">	 
		<td>Text *</td>
		<td><textarea id="textArea" name="request" cols="120" rows="30"><%if (request.getAttribute("text") != null)
				out.print(request.getAttribute("text"));%></textarea></td>
	</tr>	
	<tr style="display:none" id="submitButton">
		<td></td>
		<td>
		<INPUT type="submit" value="Submit">
		</td>
	</tr>
  	<tr style="display:table-row" id="response">
		<td>Response</td>
		<td><pre><textarea name="responseJSON" id="responseJSONId" cols="120" rows="15"><%
			if (request.getAttribute("responseJSON") != null)
				out.print(request.getAttribute("responseJSON"));%></textarea></pre></td>
	</tr> 
</table>
<%
servizio=(String)request.getAttribute("actionName");
if (servizio != "") {
	{ %> <script type="text/javascript"> showFields(); </script> <% }
}

%>
</form>
</body>
</html>
