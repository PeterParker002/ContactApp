<%@page language="java"%>
<% 
response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
 %>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<title>My Contacts</title>
<link rel="preconnect" href="https://fonts.googleapis.com" />
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
<link
	href="https://fonts.googleapis.com/css2?family=Montserrat:ital,wght@0,100..900;1,100..900&display=swap"
	rel="stylesheet" />
<link rel="stylesheet" href="css/index.css" />
</head>
<body>
	<div class="wrapper">
		<h1>My Contacts App</h1>
		<div class="container">
			<a href="login.jsp">Login</a> <a href="signup.jsp">Sign Up</a>
		</div>
	</div>
	<script src="js/script.js"></script>
</body>
</html>
