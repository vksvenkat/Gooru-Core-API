<%@page import="org.ednovo.gooru.domain.model.user.InviteCode"%>

<%

InviteCode code = (InviteCode)request.getAttribute("invite");

%>

<h1>Code Details:</h1>
<b>Code:</b> <%= code.getCode() %><br/>
<b>Valid Till:</b> <%= code.getDateofexpiry() %><br/>
