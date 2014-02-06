<%@page import="org.ednovo.gooru.domain.model.user.InviteCode,java.util.List"%>

<%

List<InviteCode> codeList = (List)request.getAttribute("codes");
String status = (String)request.getAttribute("status");

%>

<h1>List of <%=status %> codes</h1>
<table border="1" cellspacing="0" cellpadding="0">
	<tr>
		<th width="50px" height="30px">
			S.No.
		</th>
		<th width="250px" height="30px">
			Code
		</th>
		<th width="350px" height="30px">
			Date of Expiry
		</th>
		<th width="350px" height="30px">
			Action
		</th>		
	</tr>
	<% for(int i=0; i< codeList.size(); i++) {%>
		<tr>
			<td width="50px" height="30px">
				<%= codeList.get(i).getId() %>
			</td>
			<td width="250px" height="30px">
				<%= codeList.get(i).getCode() %>
			</td>
			<td width="350px" height="30px">
				<%= codeList.get(i).getDateofexpiry() %>
			</td>
			<td width="350px" height="30px">
				<a href="../renew.html?code=<%= codeList.get(i).getCode() %>&sessionToken=db4fd4b2-90df-11e0-9bf5-12313b083ca6">Renew</a> | <a href="../delete.html?code=<%= codeList.get(i).getCode() %>&sessionToken=db4fd4b2-90df-11e0-9bf5-12313b083ca6">Delete</a>
			</td>			
		</tr>
	<%} %>
</table>