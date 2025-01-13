<%@page language="java"%>
<%
response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
%>
<link rel="preconnect" href="https://fonts.googleapis.com" />
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
<link
	href="https://fonts.googleapis.com/css2?family=Montserrat:ital,wght@0,100..900;1,100..900&display=swap"
	rel="stylesheet" />
<link href="https://fonts.googleapis.com/css2?family=Space+Grotesk:wght@300..700&display=swap" rel="stylesheet">
<link rel="stylesheet" href="css/index.css">
<div class="wrapper">
	<h1>Login Form</h1>
	<form action="login" method="post">
		<input type="email" name="email" placeholder="Enter your email: "
			pattern="[a-zA-Z]+(\.)?([a-zA-Z0-9]+)*@[a-zA-Z]+\.[a-zA-Z]{2,3}"
			required /><input type="password" name="password"
			placeholder="Enter your password" required />
		<p class="sign-up-link">
			Don't have an account? <a href="signup">Sign Up</a>
		</p>
		<input type="submit" value="Login" />
	</form>
</div>
<script src="js/script.js"></script>