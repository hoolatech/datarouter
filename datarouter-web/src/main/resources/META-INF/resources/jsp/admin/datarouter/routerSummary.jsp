<%@ include file="/jsp/generic/prelude.jspf"%>
<!DOCTYPE html>
<html>
<head>
	<title>Datarouter</title>
	<%@ include file="/jsp/generic/datarouterHead.jsp" %>
	<style>
	.nodeName{
		word-wrap:break-word;
		color:black;
		max-width: 300px;
	}
	</style>
</head>
<body>
	<%@ include file="/jsp/menu/common-navbar.jsp" %>
	<%@ include file="/jsp/menu/dr-navbar.jsp" %>
	<div class="container-fluid">
		<h2>Datarouter</h2>
		<a href="${contextPath}/datarouter">Datarouter Home</a> &nbsp;&nbsp;&#62;&#62;&nbsp;&nbsp; routerName:<b>${param.routerName}</b>
		<h3>Nodes in Router: <b>${param.routerName}</b></h3>
		<table class="table table-striped table-bordered table-hover table-condensed">
			<thead>
				<tr>
					<th>node name</th>
					<th>row count chart</th>
					<th>count keys</th>
					<th>node type</th>
					<th>export to s3 </th>
					<th>import from S3</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${nodeWrappers}" var="nodeWrapper">
					<tr>
						<td class="nodeName">
							<a href="${contextPath}/datarouter/nodes/browseData/browseData?routerName=${param.routerName}&nodeName=${nodeWrapper.urlEncodedName}">
								${nodeWrapper.indentHtml}${nodeWrapper.name}
							</a>
						</td>
						<td>
							<a href="${contextPath}/datarouter/nodewatch/rowCountChart?submitAction=viewRowCountChartWithNodeName&nodeName=${nodeWrapper.urlEncodedName}">
								<span class="glyphicon glyphicon-signal"></span>
							</a>
						</td>
						<td>
							<c:if test="${nodeWrapper.sorted}">
								<a href="${contextPath}/datarouter/nodes/browseData/countKeys?routerName=${param.routerName}&nodeName=${nodeWrapper.urlEncodedName}">
								count keys
								</a>
							</c:if>
						</td>
						<td>${nodeWrapper.urlEncodedName}</td>
						<td>
							<a href="${contextPath}/datarouter/dataMigration/showForm?routerName=${param.routerName}&nodeName=${nodeWrapper.urlEncodedName}">
								export to s3
							</a>
						</td>
						<td>
							<a href="${contextPath}/datarouter/dataMigration/showImportForm?routerName=${param.routerName}&nodeName=${nodeWrapper.urlEncodedName}">
								import from s3
							</a>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
</body>
</html>