<%@page language="java"%>
<%
response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); %>
<link rel="preconnect" href="https://fonts.googleapis.com" />
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
<link
	href="https://fonts.googleapis.com/css2?family=Montserrat:ital,wght@0,100..900;1,100..900&display=swap"
	rel="stylesheet" />
<link href="https://fonts.googleapis.com/css2?family=Space+Grotesk:wght@300..700&display=swap" rel="stylesheet">
<link rel="stylesheet" href="css/index.css" />
<link rel="stylesheet" href="css/signup.css" />
<div class="wrapper">
	<h1>Sign Up Form</h1>
	<form action="signup" method="post">
		<div class="form-input-fields">
			<div class="username">
				<input type="text" name="username"
					placeholder="Enter your full name" required />
			</div>
			<div class="firstname">
				<input type="text" name="fname" placeholder="Enter your first name"
					required />
			</div>
			<div class="middlename">
				<input type="text" name="midname"
					placeholder="Enter your middle name(optional)" />
			</div>
			<div class="lastname">
				<input type="text" name="lname"
					placeholder="Enter your last name(optional)" />
			</div>
			<div class="gender">
				Gender:
				<div class="opt">
					<input type="radio" name="gender" value="Male" id="male" checked /><label
						for="male">Male</label>
				</div>
				<div class="opt">
					<input type="radio" name="gender" value="Female" id="female" /><label
						for="female">Female</label>
				</div>
			</div>
			<div class="email">
				<input type="email" name="email" class="custom-field"
					placeholder="Enter your email"
					pattern="[a-zA-Z]+(\.)?([a-zA-Z0-9]+)*@[a-zA-Z]+\.[a-zA-Z]{2,3}"
					title="Email should look like: example@gmail.com" required />
			</div>
			<div class="dob">
				<input type="date" name="dob" id="dob"
					title="Enter your date of birth here" required />
			</div>
			<div class="notes">
				<input type="text" name="notes" id="notes"
					placeholder="Tell us about yourself" />
			</div>
			<div class="Home-Address">
				<input type="text" name="home" id="home"
					placeholder="Enter your home address" required />
			</div>
			<div class="Work-Address">
				<input type="text" name="work" id="work"
					placeholder="Enter your work address" required />
			</div>
			<div class="mobile-number">
				<input type="tel" pattern="[0-9]{10}" name="mobile" id="mobile"
					class="custom-field" placeholder="Enter your mobile number"
					title="Phone number should look like: 1234567890" required />
			</div>
			<div class="password">
				<input type="password" name="password" minlength="6"
					placeholder="Enter your password" required />
			</div>
		</div>
		<p class="sign-up-link">
			Already Have an account? <a href="login">Login</a>
		</p>
		<input type="submit" value="Sign Up" />
	</form>
</div>
<script src="js/script.js"></script>
