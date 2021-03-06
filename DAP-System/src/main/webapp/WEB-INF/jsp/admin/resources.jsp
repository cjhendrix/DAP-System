<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<c:set value="${pageContext.request.contextPath}" var="ctx" scope="request" />
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet" type="text/css" href="${ctx}/css/style.css" />
</head>
<body>
	<jsp:include page="admin-header.jsp" />
	<h2>Detected CKAN Resources</h2>

	<table>
		<tr>
			<th>Resource Name</th>
			<th>Parrent Dataset</th>
			<th>Revision Id</th>
			<th>Revision Ts</th>
			<th>Workflow State</th>
			<th>Action</th>
		</tr>

		<c:forEach var="ckanResource" items="${it}">
			<tr>
				<td title="Resource Id : ${ckanResource.id.id}"><c:if test="${previousId ne ckanResource.id.id}">${ckanResource.name}</c:if></td>
				<td><c:if test="${previousId ne ckanResource.id.id}">${ckanResource.parentDataset_name}</c:if></td>
				<c:set scope="request" var="previousId" value="${ckanResource.id.id}" />
				<td>${ckanResource.id.revision_id}</td>
				<td><fmt:formatDate value="${ckanResource.revision_timestamp}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
				<td><c:if test="${ckanResource.workflowState eq 'TECH_EVALUATION_SUCCESS' || ckanResource.workflowState eq 'TECH_EVALUATION_FAIL'}">
						<a target="_blank" href="./${ckanResource.id.id}/${ckanResource.id.revision_id}/report">
					</c:if> ${ckanResource.workflowState} <c:if
						test="${ckanResource.workflowState eq 'TECH_EVALUATION_SUCCESS' || ckanResource.workflowState eq 'TECH_EVALUATION_FAIL'}">
						</a>evaluated by ${ckanResource.evaluator}</c:if> <c:if
						test="${ckanResource.workflowState eq 'IMPORT_SUCCESS' || ckanResource.workflowState eq 'IMPORT_FAIL'}"> imported by ${ckanResource.importer}</c:if>
				</td>
				<td><c:if test="${ckanResource.isDownloadable()}">
						<a href="${ctx}/admin/status/manuallyTriggerDownload/${ckanResource.id.id}/${ckanResource.id.revision_id}/">download</a>
					</c:if> <c:if test="${ckanResource.workflowState eq 'DOWNLOADED'}">
						<a href="${ctx}/admin/status/manuallyTriggerEvaluation/${ckanResource.id.id}/${ckanResource.id.revision_id}/">evaluate</a>
					</c:if> <c:if test="${ckanResource.workflowState eq 'TECH_EVALUATION_SUCCESS'}">
						<a href="${ctx}/admin/status/manuallyTriggerImport/${ckanResource.id.id}/${ckanResource.id.revision_id}/">import</a>
					</c:if></td>
			</tr>
		</c:forEach>

	</table>

</body>
</html>