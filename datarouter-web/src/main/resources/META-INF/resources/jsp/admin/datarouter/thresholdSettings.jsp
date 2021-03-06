<%@ include file="/jsp/generic/prelude.jspf"%>
<!DOCTYPE html>
<html>
<head>
	<title>Threshold</title>
	<%@ include file="/jsp/generic/datarouterHead.jsp" %>
</head>
<body>
	<%@ include file="/jsp/menu/common-navbar.jsp" %>
	<%@ include file="/jsp/menu/dr-navbar.jsp" %>
	<div class="container">
		<h2>Set Threshold</h2>		

		
		  <form id="validationform" name="validationform" method="GET" action="?">
			<table id="myTable2" class="order-list" class="table table-striped table-bordered table-hover table-condensed">
				<thead>
					<tr>
						<td>Router name</td>
						<td>Node name</td>
						<td>Threshold Value</td>
					</tr>
				</thead>
				<tbody>
				<c:forEach items="${thresholdSettings}" var="setting">
				 <form method="get" action="${contextPath}${thresholdPath}">
					<tr>
						<td><input type="text" name="clientName" value='${setting.key.getClientName()}' readonly></td>
						<td><input type="text" name="tableName" value='${setting.key.getTableName()}' readonly></td>
						<td><input type="text" name="threshold" value='${setting.getMaxRows()}' ></td>
						<td><input type="submit" value="updateThreshold" class="btn btn-warning" name="submitAction" ></td>
					</tr>
					</form>
				</c:forEach>
				</tbody>	
			</table>
			<input type="hidden" value="saveThresholds" name="submitAction" hidden="true">
		</form>
		
		<form id="validationform2" name="validationform2" method="GET" action="?">
		<table  id="myTable" class="order-list" class="table table-striped table-bordered table-hover table-condensed" style="visibility:hidden">
				<thead>
					<tr>
						<td>Router name</td>
						<td>Node name</td>
						<td>Threshold Value</td>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td><input type="text" name="clientName"></td>
						<td><input type="text" name="tableName" ></td>
						<td><input type="text" name="threshold"></td>					
					</tr>
				</tbody>
				
				<tfoot>
				<tr>
					<td><input type="button" value="Add More(+)" name="addrow"
						id="addrow"></td>					
				</tr>
				<tr>
					<td><input type="submit" value="save" />					
				</tr>
			</tfoot>
			</table>
			<input value="saveThresholds" name="submitAction" hidden="true">
		</form>	
	</div>
	
	
</body>
</html>