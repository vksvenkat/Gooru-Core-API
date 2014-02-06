<% 
	String callback = request.getParameter("callback");
	if (callback != null) {
%><%=callback%>(${requestScope.model});<% } else  { %>${requestScope.model}<% } %>