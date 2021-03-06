<%@ include file="/jsp/generic/prelude.jspf"%>
<!DOCTYPE html>
<html>
<head>
	<title>Memory Statistic</title>
	<%@ include file="/jsp/generic/datarouterHead.jsp" %>
	<style type="text/css">
		body {
			padding-right: 0;
			padding-left: 0;
		}
		.property{
			display: inline-block;
			width: 130px;
			text-align: left;
			vertical-align: top;
		}
		.value{
			display: inline-block;
			width: 120px;
			overflow-wrap: break-word
		}
		.tree-level-1:before, .tree-level-2:before{
			content: '\21B3';
			padding: 0 5px;
		}
		.tree-level-1:before{
			padding-left: 5px;
		}
		.tree-level-2:before{
			padding-left: 20px;
		}
		.block {
			display: inline-block;
			margin: 0 20px 20px;
			text-align: right;
			vertical-align: top;
		}
		.block h6, .block h5, .block h4, .block h3, .block h2, .block h1{
			margin: 10px 0 0;
		}
		.auto-centered-container {
			text-align: center;
		}
		h6, h5, h4, h3, h2, h1{
			text-align: left;
		}
		.panel-heading {
			text-align: left;
		}
		.no-value-on-0 .tree-level-0{
			width: 500px;
		}
		.alert{
			width: 255px;
			margin-top: 20px;
		}
	</style>
	<script>
	require(['jquery'], function($){
		$(function(){
			$('#loading-example-btn').click(function(){
				if(!confirm('Do you really to run the garbage collector on ${serverName}')){
					return false;
				}
				var btn = $(this);
				btn.button('loading');
				var start = new Date().getTime();
				var interval = setInterval(function() {
					var diff = new Date().getTime() - start;
					btn.text('In progress ' + Math.round(diff/100)/10 + 's');
				}, 100);
				$.get("${contextPath}/datarouter/memory/garbageCollector?serverName=${serverName}")
						.done(function(response){
							window.clearInterval(interval);
							btn.text('Run garbage collector').button('reset');
							btn.siblings().remove();
							if(response.success){
								var title = $('<h3>').text('Previous manual run');
								var timeLabel = $('<span>').addClass('property tree-level-0').text('Time');
								var timeValue = $('<span>').addClass('value')
										.text(response.duration/1000 + 's');
								var resultDiv = $('<div>').css('text-align', 'right')
										.append(title)
										.append(timeLabel).append(timeValue).append($('<br>'));
								response.effects.forEach(function(effect){
									var effectNameLabel = $('<span>').addClass('property tree-level-0')
											.text(effect.name);
									var effectNameValue = $('<span>').addClass('value');
									var savedLabel = $('<span>').addClass('property tree-level-1').text('Memory saved');
									var savedValue = $('<span>').addClass('value').text(effect.saved);
									var pctLabel = $('<span>').addClass('property tree-level-1').text('Percentage');
									var pctValue = $('<span>').addClass('value')
											.text(Math.round(100 * effect.pct) + '%');
									resultDiv.append(effectNameLabel).append(effectNameValue).append($('<br>'))
											.append(savedLabel).append(savedValue).append($('<br>'))
											.append(pctLabel).append(pctValue).append($('<br>'));
								});
								btn.parent().append(resultDiv);
							}else{
								btn.parent().append($('<div>').addClass('alert alert-danger')
										.text('The request came from another server. ' 
												+ 'Are you sure you are on an server specific url?'));
							}
						});
			});
		});
	});
	</script>
</head>
<body class="input-no-margin">
<%@ include file="/jsp/menu/common-navbar.jsp"%>
<%@ include file="/jsp/menu/dr-navbar.jsp"%>
<div class="auto-centered-container">
	<div class="block">
		<h2>Server</h2>
		<span class="property tree-level-0">Start time</span>
		<span class="value">${startTime}</span>
		<br>
		<span class="property tree-level-0">Up time</span>
		<span class="value">${upTime}</span>
		<br>
		<span class="property tree-level-0">Name</span>
		<span class="value">${serverName}</span>
		<br>
		<span class="property tree-level-0" style="width:85px">Web server</span>
		<span class="value"  style="width:165px">${serverVersion}</span>
		<br>
		<span class="property tree-level-0">Java version</span>
		<span class="value" title="${jvmVersion}">${javaVersion}</span>
		<br>
		<span class="property tree-level-0">Web application</span>
		<span class="value">${appName}</span>
		<br>
		<span class="property tree-level-0" title="${gitDescribeShort}">Version</span>
		<span class="value"></span>
		<br>
		<span class="property tree-level-1">Branch</span>
		<span class="value">${gitBranch}</span>
		<br>
		<span class="property tree-level-1">Commit</span>
		<span class="value" title="${gitCommitTime} by ${gitCommitUserName}">${gitCommit}</span>
		<br>
		<span class="property tree-level-1">Build time</span>
		<span class="value">${buildTime}</span>
		<br>
		<span class="property tree-level-1">Build id</span>
		<span class="value">${buildId}</span>
		<br>
	</div>
	<div class="block">
		<h2>Threads</h2>
		<span class="property tree-level-0">processors</span>
		<span class="value">${procNumber}</span>
		<br>
		<span class="property tree-level-0">thread</span>
		<span class="value">${threadCount}</span>
		<br>
		<span class="property tree-level-1">daemon</span>
		<span class="value">${daemon}</span>
		<br>
		<span class="property tree-level-1">non daemon</span>
		<span class="value">${nonDaemon}</span>
		<br>
		<span class="property tree-level-0">peak</span>
		<span class="value">${peak}</span>
		<br>
		<span class="property tree-level-0">started</span>
		<span class="value">${started}</span>
		<br>
	</div>
	<div class="block">
		<h2>Memory</h2>
		<div class="panel-group" id="accordion">
			<c:set var="name" value="Heap"/>
			<c:set var="escapedName" value="Heap"/>
			<c:set var="defaultVisible" value="in"/>
			<c:set var="total" value="${heap}"/>
			<c:set var="pools" value="${heaps}"/>
			<%@ include file="memoryPool.jsp" %>

			<c:set var="name" value="Non-Heap"/>
			<c:set var="escapedName" value="Non-Heap"/>
			<c:set var="defaultVisible" value=""/>
			<c:set var="total" value="${nonHeap}"/>
			<c:set var="pools" value="${nonHeaps}"/>
			<%@ include file="memoryPool.jsp" %>
		</div>
	</div>
	<div class="block">
		<h2>Garbage collector</h2>
		<c:forEach items="${gcs}" var="gc">
			<span class="property tree-level-0">${gc.name}</span>
			<span class="value"></span>
			<br>
			<span class="property tree-level-1">Count</span>
			<span class="value">${gc.collectionCount}</span>
			<br>
			<span class="property tree-level-1">Time</span>
			<span class="value">${gc.collectionTime}</span>
			<br>
			<span class="property tree-level-1">Memory Pools</span>
			<span class="value"></span>
			<br>
			<c:forEach items="${gc.memoryPoolNames}" var="memoryPoolName">
				<span>${memoryPoolName}</span>
				<br>
			</c:forEach>
			<br>
		</c:forEach>
		<div style="text-align: left">
			<a class="btn btn-danger" id="loading-example-btn">
				Run garbage collector
			</a>
		</div>
	</div>
</div>
<div class="auto-centered-container">
	<div class="block">
		<c:set var="name" value="Detailed libraries"/>
		<c:set var="escapedName" value="Detailed-libraries"/>
		<c:set var="defaultVisible" value="in"/>
		<c:set var="libs" value="${detailedLibraries}"/>
		<c:set var="map" value="true"/>
		<%@ include file="libraries.jsp" %>
	</div>
	<div class="block">
		<c:set var="name" value="Other libraries"/>
		<c:set var="escapedName" value="Other-libraries"/>
		<c:set var="defaultVisible" value="in"/>
		<c:set var="libs" value="${otherLibraries}"/>
		<c:set var="map" value="false"/>
		<%@ include file="libraries.jsp" %>
	</div>
</div>
</body>
</html>
