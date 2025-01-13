<%@page import="com.contacts.cache.SessionCache"%>
<%@page language="java"%>
<%@page
	import="java.util.ArrayList,com.contacts.dao.UserDAO,com.contacts.dao.ContactDAO,java.sql.ResultSet,com.contacts.model.User,com.contacts.model.Contact,com.contacts.model.UserMail,com.contacts.model.Mail,com.contacts.model.UserMobile,com.contacts.model.MobileNumber,com.contacts.model.Group,com.contacts.model.Session"%>
<%
response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
UserDAO u = new UserDAO();
ContactDAO c = new ContactDAO();
String sessionId = u.getSessionIdFromCookie(request, "session");
Session userSession = u.getUserSession(sessionId);
int user_id = userSession.getUserId();
//User user = u.getUserInfo(user_id);
User user = SessionCache.userCache.get(user_id);
ArrayList<Group> groups = (ArrayList<Group>) u.getGroups(user_id);
ArrayList<Contact> contacts = c.getContacts(user_id);
%>
<html>
<head>
<link rel="preconnect" href="https://fonts.googleapis.com" />
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
<link
	href="https://fonts.googleapis.com/css2?family=Montserrat:ital,wght@0,100..900;1,100..900&display=swap"
	rel="stylesheet" />
<link href="https://fonts.googleapis.com/css2?family=Space+Grotesk:wght@300..700&display=swap" rel="stylesheet">
<script src="https://cdn.jsdelivr.net/npm/driver.js@1.0.1/dist/driver.js.iife.js"></script>
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/driver.js@1.0.1/dist/driver.css"/>
<link rel="stylesheet" href="css/index.css">
<link rel="stylesheet" href="css/home.css">
<title>Contacts - Home</title>
</head>
<body>
	<%
	if (session.getAttribute("message") != null) {
		String msg = (String) session.getAttribute("message");
	%>
	<div class="message" id="message"><%=msg%></div>
	<%
	session.removeAttribute("message");
	}
	%>
	<div class="edit-profile-form modal hide">
		<div class="form-wrapper">
			<span class="close-profile-form close-btn"><img
				src="assets/close-line-svgrepo-com.svg" alt="close-btn" width="48"></span>
			<form id="edit-profile-form" action="edit-profile" method="post">
				<div class="edit-profile-fields profile-fields">
					<input type="text" name="fname" placeholder="Enter your first name"
						value="<%=user.getFirstName()%>" required /> <input
						type="text" name="midname"
	placeholder="Enter your middle name(optional)"
						value="<%=user.getMiddleName()%>" /> <input type="text"
						name="lname" placeholder="Enter your last name(optional)"
						value="<%=user.getLastName()%>" />
					<%
					if (user.getGender().equalsIgnoreCase("male")) {
					%>
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
					<%
					} else {
					%>
					<div class="gender">
						Gender:
						<div class="opt">
							<input type="radio" name="gender" value="Male" id="male" /><label
								for="male">Male</label>
						</div>
						<div class="opt">
							<input type="radio" name="gender" value="Female" id="female"
								checked /><label for="female">Female</label>
						</div>
					</div>
					<%
					}
					%>
					<input type="date" name="dob" id="dob"
						title="Enter your date of birth here"
						value="<%=user.getDateOfBirth()%>" required /> <input
						type="text" name="notes" id="notes"
	placeholder="Tell us about yourself"
						value="<%=user.getNotes()%>" /> <input type="text"
						name="home" id="home" placeholder="Enter your home address"
						value="<%=user.getHomeAddress()%>" required /> <input
						type="text" name="work" id="work"
	placeholder="Enter your work address"
						value="<%=user.getWorkAddress()%>" required />

				</div>
				<input type="submit" value="Edit Profile">
			</form>
		</div>
	</div>
	<div class="edit-contact-form modal hide">
		<div class="form-wrapper">
			<span class="close-edit-contact-form close-btn"><img
				src="assets/close-line-svgrepo-com.svg" alt="close-btn" width="48"></span>
			<form id="edit-contact-form" action="edit-contact" method="post">
				<div class="edit-profile-fields profile-fields">
				<input type="hidden" name="contact-id" id="hidden-contact-id">
					<input type="text" name="fname" class="fname" placeholder="Enter your first name"
						value="" required /> <input
						type="text" name="midname" class="mname"
	placeholder="Enter your middle name(optional)"
						value="" /> <input type="text" class="lname"
						name="lname" placeholder="Enter your last name(optional)"
						value="" />
					<div class="gender">
						Gender:
						<div class="opt">
							<input type="radio" name="gender" class="male" value="Male" id="male" /><label
								for="male">Male</label>
						</div>
						<div class="opt">
							<input type="radio" name="gender" class="female" value="Female" id="female" /><label
								for="female">Female</label>
						</div>
					</div>
					<input type="date" class="dob" name="dob" id="dob"
						title="Enter your date of birth here"
						value="" required /> <input class="notes"
						type="text" name="notes" id="notes"
	placeholder="Tell us about yourself"
						value="" /> <input type="text" class="home"
						name="home" id="home" placeholder="Enter your home address"
						value="" required /> <input class="work"
						type="text" name="work" id="work"
	placeholder="Enter your work address"
						value="" required />

				</div>
				<input type="submit" value="Edit Profile">
			</form>
		</div>
	</div>
	<div class="add-contact-form modal hide">
		<div class="form-wrapper">
			<span class="close-contact-form close-btn"><img
				src="assets/close-line-svgrepo-com.svg" alt="close-btn" width="48"></span>
			<form id="add-contact-form" action="add-contact" method="post">
				<div class="add-contact-fields">
					<div class="firstname">
						<input type="text" name="fname"
		placeholder="Enter your first name" required />
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
		pattern="^[a-zA-Z]+(\.)?([a-zA-Z0-9]+)*@[a-zA-Z]+\.[a-zA-Z]{2,3}"
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
					<div class="homeaddress">
						<input type="text" name="home" id="home"
		placeholder="Enter your home address" required />
					</div>
					<div class="workaddress">
						<input type="text" name="work" id="work"
		placeholder="Enter your work address" required />
					</div>
					<div class="mobilenumber">
						<input type="tel" pattern="[0-9]{10}" name="mobile" id="mobile"
							class="custom-field" placeholder="Enter your mobile number"
							title="Phone number should look like: 1234567890" required />
					</div>
				</div>
				<input type="submit" value="Add Contact">
			</form>
		</div>
	</div>
	<div class="add-email-form modal hide">
		<div class="form-wrapper">
			<span class="close-email-form close-btn"><img
				src="assets/close-line-svgrepo-com.svg" alt="close-btn" width="48"></span>
			<form action="add-email" method="post">
				<div class="email-fields">
				<input type="hidden" name="role" id="hidden-type" value="user">
				<input type="hidden" name="contact-id" id="hidden-contact-id">
					<input type="email" name="email" class="email-field"
	placeholder="Enter your email"
	pattern="^[a-zA-Z]+(\.)?([a-zA-Z0-9]+)*@[a-zA-Z]+\.[a-zA-Z]{2,3}"
						title="Email should look like: example@gmail.com" required />
				</div>
				<div class="btn-group">
					<button class="add-mail-btn">
						Add Field <img src="assets/add-row-svgrepo-com.svg" alt="mail-add"
							width="32">
					</button>
					<button type="submit">
						Add Email <img src="assets/add-email-svgrepo-com.svg"
							alt="mail-add" width="32">
					</button>
				</div>
			</form>
		</div>
	</div>
	<div class="add-mobile-form modal hide">
		<div class="form-wrapper">
			<span class="close-mobile-form close-btn"><img
				src="assets/close-line-svgrepo-com.svg" alt="close-btn" width="48"></span>
			<form action="add-mobile-number" method="post">
				<div class="mobile-fields">
				<input type="hidden" name="role" id="hidden-type" value="user">
				<input type="hidden" name="contact-id" id="hidden-contact-id">
					<input type="mobile" name="mobile" class="mobile-field"
	placeholder="Enter your Mobile Number" pattern="[0-9]{10}"
						title="Mobile Number should look like: 1234567890" required />
				</div>
				<div class="btn-group">
					<button class="add-mobile-btn">
						Add Field <img src="assets/add-row-svgrepo-com.svg"
							alt="mobile-add" width="32">
					</button>
					<button type="submit">
						Add Mobile <img src="assets/phone-add-svgrepo-com.svg"
							alt="mobile-add" width="32">
					</button>
				</div>
			</form>
		</div>
	</div>
	<div class="add-group-form modal hide">
		<div class="form-wrapper">
			<span class="close-group-form close-btn"><img
				src="assets/close-line-svgrepo-com.svg" alt="close-btn" width="48"></span>
			<form action="add-group" method="post">
				<div class="group-fields">
					<input type="text" name="name" id="name"
	placeholder="Enter your group name" required>
					<div class="contacts-list">
						<%
						int i = 1;
						for (Contact cont : contacts) {
						%>
						<div class="contact-option">
							<input type="checkbox" name="contact" id="all-contact<%=i%>"
								value="<%=cont.getContactId()%>"><label
								for="all-contact<%=i%>"><%=cont.getFirstName()%></label>
						</div>
						<%
										i++;
										}
						%>
					</div>
				</div>
				<div class="btn-group">
					<button type="submit">
						Create Group <img src="assets/add-group.svg" alt="add-group"
							width="32">
					</button>
				</div>
			</form>
		</div>
	</div>
	<div class="add-group-contact-form modal hide">
		<div class="form-wrapper">
			<span class="close-group-contact-form close-btn"><img
				src="assets/close-line-svgrepo-com.svg" alt="close-btn" width="48"></span>
			<form action="add-group-contact" method="post">
				<div class="group-fields">
					<div class="current-group-name"></div>
					<input type="hidden" name="group-id" id="hidden-group-id">
					<div class="filtered-contacts contacts-list"></div>
				</div>
				<div class="btn-group">
					<button type="submit">
						Add Contact <img src="assets/add-group.svg" alt="add-group"
							width="32">
					</button>
				</div>
			</form>
		</div>
	</div>
	<div class="main-container">
		<div class="left-container">
			<h1 class="heading">Profile</h1>
			<div class="profile-img">
				<div class="photo"></div>
				<p><%=user.getUsername()%></p>
			</div>
			<div class="user-info">
				<div class="top">
					<span>General Information</span> <span class="edit-profile"><img
						src="assets/pencil-cursor-svgrepo-com.svg" width='24'
						alt="add-email"></span>
				</div>
				<p class="profile-detail">
					First Name:
					<%=user.getFirstName()%></p>
				<p class="profile-detail">
					Middle Name:
					<%=user.getMiddleName()%></p>
				<p class="profile-detail">
					Last Name:
					<%=user.getLastName()%></p>
				<p class="profile-detail">
					Gender:
					<%=user.getGender()%></p>
				<p class="profile-detail">
					Date Of Birth:
					<%=user.getDateOfBirth()%></p>
				<p class="profile-detail">
					Notes:
					<%=user.getNotes()%></p>
				<p class="profile-detail">
					Home Address:
					<%=user.getHomeAddress()%></p>
				<p class="profile-detail">
					Work Address:
					<%=user.getWorkAddress()%></p>
				<div class="mail-container">
					<div class="title">
						Emails: <span class="add-email-icon"><img
							src="assets/pencil-cursor-svgrepo-com.svg" width='16'
							alt="add-email"></span>
					</div>
					<%
								for (UserMail mail : user.getEmail()) {
					%>
					<div class="mail">
						<span class="left-side"> <%

  if (mail.getIsPrimary()) {
 %> <a href="#" class="primary"><img
								src="assets/star-svgrepo-com.svg" alt="primary" width="24" /></a> <%
  } else {
 %> <a href="makePrimary/<%=mail.getId()%>" class="non-primary"><img
								src="assets/star-gray.svg" alt="primary" width="24" /></a> <%
  }
 %> <span><%=mail.getEmail()%></span>
						</span> <span class="right-side"> <%
  if (!mail.getIsPrimary()) {
 %> <a href="deleteMail/<%=mail.getId()%>"> <img
								src="assets/delete-icon.svg" alt="delete-mail" width="24">
						</a> <%
  }
 %>
						</span>
					</div>
					<%
								}
					%>
				</div>
				<div class="mobile-container">
					<div class="title">
						Mobile Numbers: <span class="add-mobile-icon"><img
							src="assets/pencil-cursor-svgrepo-com.svg" width='16'
							alt="add-mobile-number"></span>
					</div>
					<%
								for (MobileNumber mobile : user.getMobileNumber()) {
					%>
					<div class="mobile">
						<span class="left-side"> <span><%=mobile.getMobileNumber()%></span>
						</span> <span class="right-side"> <a
							href="deleteMobile/<%=mobile.getId()%>"> <img
								src="assets/delete-icon.svg" alt="delete-mail" width="24">
						</a>
						</span>
					</div>
					<%

								}
					%>
				</div>
			</div>
		</div>
		<div class="right-container">
			<div class="all-contacts container">
				<div class="header">
					<p class="heading">All Contacts</p>
					<div class="btn-group">
						<button class="add-contact-popup" style="border: none;">Add
							Contact</button>
						<a class="logout" href="/logout">Logout</a>
					</div>
				</div>
				<div class="contact-list list-container">
					<%
								for (Contact cont : contacts) {
					%>
					<div class="contact-card">
						<div class="card-title">
							<span class="contact-name" data-id="<%=cont.getContactId()%>"><%=cont.getFirstName()%>
								<%=cont.getMiddleName()%> <%=cont.getLastName()%></span> <a
								href="deleteContact/<%=cont.getContactId()%>"
								class="delete-contact delete-icon"><img
								src="assets/delete-icon.svg" alt="delete-contact" width="24"></a>
						</div>
						<div class="card-details">
							<div class="emails">
								Email ID:
								<%=cont.getEmail().get(0).getEmail()%>
							</div>
							<div class="mobile-numbers">
								Mobile Number:
								<%=cont.getMobileNumber().get(0).getMobileNumber()%>
							</div>
						</div>
					</div>
					<%
								}
					%>
				</div>
			</div>
			<div class="groups-container container">
				<div class="header">
					<p class="heading">Groups</p>
					<div class="btn-group">
						<button class="add-group-popup" style="border: none;">Add
							Group</button>
					</div>
				</div>
				<div class="groups list-container">
				<%
								int j = 1;
								for (Group g : u.getGroups(user.getUserId())) {
					%>
					<div class="card">
						<div class="card-title">
							<span class="left-side"> <span data-order="<%=j%>"
								class="group-name"> <%=g.getGroupName()%>
							</span>
							</span>
							<div class="btn-group">
								<span data-id="<%=g.getGroupId()%>"
									class="add-group-contact-btn"><img
									src="assets/add-user.svg" alt="add-contact" width="24"></span>
								<form action="delete-group" method="post"
									id="delete-group-form-<%=j%>">
									<input type="hidden" name="group-id"
										value="<%=g.getGroupId()%>"> <span
										class="delete-contact delete-icon"
										onclick="document.forms['delete-group-form-<%=j%>'].submit()"><img
										src="assets/delete-icon.svg" alt="delete-contact" width="24"></span>
								</form>
							</div>
						</div>
					</div>
					<div class="collapse-container c<%=j%>">
						<%
										if (g.getContact().size() > 0) {
											for (Contact contact : g.getContact()) {
						%>
						<div class="contact">
							<span class="contact-name" data-id="<%=contact.getContactId()%>"><%=contact.getFirstName()%></span>
							<form action="delete-group-contact" method="post"
								id="delete-group-contact-form-<%=j%>">
								<input type="hidden" name="contact-id"
									value="<%=contact.getContactId()%>"> <input
									type="hidden" name="group-id" value="<%=g.getGroupId()%>">
								<span class="delete-group-contact" title="Delete Contact From Group"
									onclick="document.forms['delete-group-contact-form-<%=j%>'].submit()"><img
									src="assets/remove-user.svg" alt="delete-group-contact"
									width="16"></span>
							</form>
						</div>
						<%
						j++;
						}
						} else {
						%>
						<div class="no-contact">No Contacts Found</div>
						<%
						}
						%>
					</div>
					<%
					j++;
					}
					%>
				</div>
			</div>
		</div>
		<div class="left-container contact-container hide">
			<div class="heading">Contact</div>
			<div class="profile-img">
				<div class="photo"></div>
				<p class="contact-fname"></p>
			</div>
			<div class="user-info">
				<div class="top">
					<span>General Information</span> <span class="edit-contact"><img
						src="assets/pencil-cursor-svgrepo-com.svg" width='24'
						alt="add-email"></span>
				</div>
				<p>
					First Name: <span class="c-fname"></span>
				</p>
				<p>
					Middle Name: <span class="c-mname"></span>
				</p>
				<p>
					Last Name: <span class="c-lname"></span>
				</p>
				<p>
					Gender: <span class="c-gender"></span>
				</p>
				<p>
					Notes: <span class="c-notes"></span>
				</p>
				<p>
					DOB: <span class="c-dob"></span>
				</p>
				<p>
					Home Address: <span class="c-home"></span>
				</p>
				<p>
					Work Address: <span class="c-work"></span>
				</p>
				<div class="mail-container contact-mails">
				
				</div>
				<div class="mobile-container contact-mobiles">
				
					
				</div>
			</div>
		</div>
	</div>
	<script src="js/script.js"></script>
</body>
</html>