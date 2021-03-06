<%@ include file="/jsp/generic/prelude.jspf"%>
<!DOCTYPE html>
<html>
<head>
	<title>Datarouter</title>
	<%@ include file="/jsp/generic/datarouterHead.jsp" %>
</head>
<body>
	<%@ include file="/jsp/menu/dr-navbar.jsp" %>
	<div class="container">
		<h2>Datarouter</h2>
		<a href="${contextPath}/datarouter">Datarouter Home</a> &nbsp;&nbsp;&#62;&#62;&nbsp;&nbsp; 
		<a href="${contextPath}/datarouter/memory?submitAction=inspectRouter&routerName=${param.routerName}">Router: ${param.routerName}</a>
		&nbsp;&nbsp;&#62;&#62;&nbsp;&nbsp; client: <b>${client.name}</b>
		<h3 style="width: 100%; border-bottom: 1px solid gray;">Memory Client Summary</h3>
		<b>routerName: </b>${param.routerName}<br /> <b>clientName: </b>${param.clientName}<br />
		<h3 style="width: 100%; border-bottom: 1px solid gray;">Stats</h3>
		TODO: track operation counts<br />
		<h3>Nodes</h3>
		<table class="table table-striped table-bordered table-hover table-condensed ">
			<thead>
				<tr>
					<th>name</th>
					<th>size</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${nodes}" var="node">
					<tr>
						<td>${node.name}</td>
						<td>${node.size}</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
</body>
</html>