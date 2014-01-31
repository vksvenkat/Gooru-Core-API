#-------------------------------------------------------------------------------
# list.jsp
# rest-v2-app
#  Created by Gooru on 2014
#  Copyright (c) 2014 Gooru. All rights reserved.
#  http://www.goorulearning.org/
#  Permission is hereby granted, free of charge, to any person      obtaining
#  a copy of this software and associated documentation files (the
#  "Software"), to deal in the Software without restriction, including
#  without limitation the rights to use, copy, modify, merge, publish,
#  distribute, sublicense, and/or sell copies of the Software, and to
#  permit persons to whom the Software is furnished to do so,  subject to
#  the following conditions:
#  The above copyright notice and this permission notice shall be
#  included in all copies or substantial portions of the Software.
#  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY  KIND,
#  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE    WARRANTIES OF
#  MERCHANTABILITY, FITNESS FOR A PARTICULAR  PURPOSE     AND
#  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR  COPYRIGHT HOLDERS BE
#  LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
#  OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
#  WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
#-------------------------------------------------------------------------------
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
