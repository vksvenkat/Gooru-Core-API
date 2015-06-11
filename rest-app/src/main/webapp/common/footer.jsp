<%@ include file="/common/taglibs.jsp" %>
<style>
 .colorBlue {
	color:#1f83b7;
 }	 
</style>
<!--OS-50:Changed the Admininstration menu tab to link in the footer and updated the release text in the footer.  -->
<p>
<script>
	currentMenuStr='<c:out value="${currentMenu}"/>';
</script>
<!--OS-50: Authoring only admin role to view administration link-->
<authz:authorize ifAllGranted="admin">
	<c:if test="${currentMenu ne 'Admin'}">
		<a class='colorBlue' href="/rel/admin.p">Administration </a> |
	</c:if>
</authz:authorize>
Version: PR 9 | Dated: 22 June 10 | Copyright © 2011</p>
    <!-- Built on Monday Jul 07, 2008 at 11:33 AM IST by preeti -->