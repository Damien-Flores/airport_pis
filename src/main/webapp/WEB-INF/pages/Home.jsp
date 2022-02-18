<%@ include file="components/jhead.jsp" %>

<!DOCTYPE html>
<html>
	<head>
		<%@ include file="components/head.jsp" %>
		<link rel="stylesheet" href="${ pageContext.request.contextPath }/resources/css/manager.css">
		<link rel="stylesheet" href="${ pageContext.request.contextPath }/resources/css/home.css">
	</head>
	
	<body>
		<%@ include file="components/nav.jsp" %>
		
		<section id="home">
			<div class="logo"></div>
			<h1>Welcome on the <br>Passenger Informations System</h1>
			<div class="display">
				<h2>Select a display to show</h2>
				<div>
					<a class="btn" href="#" title="Load and display PID for the public area">Public Area</a>
					<a class="btn" href="#" title="Load and display PID for the boarding area">Boarding Area</a>		
				</div>
			</div>
			<h2>or</h2>
			<div>
				<h2>Manage data to display</h2>
				<div>
					<a class="btn" href="#" title="Manage Flight">Manage Datas</a>
				</div>
			</div>
		</section>
	</body>
</html>