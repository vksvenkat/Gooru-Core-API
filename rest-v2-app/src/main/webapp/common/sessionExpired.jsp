<%@ include file="/common/taglibs.jsp"%>
<body>	
<script>
 var PAGE_CONTEXT =  '<c:out value="${pageContext.request.contextPath}" />' + '/common/sessionExpired.html';
	document.location.href = PAGE_CONTEXT;
</script>
</body>
