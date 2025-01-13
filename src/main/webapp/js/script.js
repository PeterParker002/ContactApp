

document.querySelector(".message")?.addEventListener("click", (e) => {
	e.target.style.display = "none";
});

document.querySelector(".close-email-form")?.addEventListener("click", () => {
	document.querySelector(".add-email-form").classList.toggle("hide");
});

document.querySelector(".close-mobile-form")?.addEventListener("click", () => {
	document.querySelector(".add-mobile-form").classList.toggle("hide");
});

document.querySelector(".close-contact-form")?.addEventListener("click", () => {
	document.querySelector(".add-contact-form").classList.toggle("hide");
});

document.querySelector(".close-edit-contact-form")?.addEventListener("click", () => {
	document.querySelector(".edit-contact-form").classList.toggle("hide");	
});

document.querySelector(".close-group-form")?.addEventListener("click", () => {
	document.querySelector(".add-group-form").classList.toggle("hide");
});

document.querySelector(".add-contact-popup")?.addEventListener("click", () => {
	document.querySelector(".add-contact-form").classList.toggle("hide");
});

document.querySelector(".add-group-popup")?.addEventListener("click", () => {
	document.querySelector(".add-group-form").classList.toggle("hide");
});

document.querySelector(".add-email-icon")?.addEventListener("click", () => {
	document.querySelector(".add-email-form").classList.toggle("hide");
});

document.querySelector(".add-mobile-icon")?.addEventListener("click", () => {
	document.querySelector(".add-mobile-form").classList.toggle("hide");
});

document
	.querySelector(".close-group-contact-form")
	?.addEventListener("click", () => {
		document.querySelector(".add-group-contact-form").classList.toggle("hide");
	});

document.querySelector(".edit-profile")?.addEventListener("click", () => {
	document.querySelector(".edit-profile-form").classList.toggle("hide");
});

document.querySelector(".close-profile-form")?.addEventListener("click", () => {
	document.querySelector(".edit-profile-form").classList.toggle("hide");
});

document.querySelector(".add-mail-btn")?.addEventListener("click", () => {
	let noOfEmails = document.querySelectorAll(".email-field").length;
	if (+noOfEmails + 1 > 5) {
		const msg = document.createElement("div");
		msg.className = "message";
		msg.innerText = "Maximum Entries Reached";
		msg?.addEventListener("click", (e) => {
			e.target.style.display = "none";
		});
		document.body.insertBefore(msg, document.body.firstChild);
		document.querySelector(".add-mail-btn").setAttribute("disabled", true);

		setTimeout(() => {
			msg.remove();
		}, 2000);
	} else {
		const div = document.createElement("div");
		div.className = "field";
		const field = document.createElement("input");
		field.type = "email";
		field.name = "email";
		field.placeholder = "Enter another email";
		field.className = "email-field";
		field.pattern = "[a-zA-Z]+(\.)?([a-zA-Z0-9]+)*@[a-zA-Z]+\.[a-zA-Z]{2,3}";
		field.title = "Email should look like: example@gmail.com";
		field.required = true;
		const deleteBtn = document.createElement("span");
		deleteBtn?.addEventListener("click", (e) => {
			document.querySelector(".email-fields").removeChild(field.parentElement);
			noOfEmails = document.querySelectorAll(".email-field").length;
			document.querySelector(".add-mail-btn").removeAttribute("disabled");
		});
		deleteBtn.innerHTML =
			'<img src="assets/close-line-svgrepo-com.svg" alt="close-btn" width="32">';
		div.appendChild(field);
		div.appendChild(deleteBtn);
		document.querySelector(".email-fields").appendChild(div);
		noOfEmails = document.querySelectorAll(".email-field").length;
	}
});

document.querySelector(".add-mobile-btn")?.addEventListener("click", () => {
	let noOfMobiles = document.querySelectorAll(".mobile-field").length;
	if (+noOfMobiles + 1 > 5) {
		const msg = document.createElement("div");
		msg.className = "message";
		msg.innerText = "Maximum Entries Reached";
		msg?.addEventListener("click", (e) => {
			e.target.style.display = "none";
		});
		document.body.insertBefore(msg, document.body.firstChild);
		document.querySelector(".add-mobile-btn").setAttribute("disabled", true);

		setTimeout(() => {
			msg.remove();
		}, 2000);
	} else {
		const div = document.createElement("div");
		div.className = "field";
		const field = document.createElement("input");
		field.type = "mobile";
		field.name = "mobile";
		field.placeholder = "Enter another mobile number";
		field.className = "mobile-field";
		field.pattern = "[0-9]{10}";
		field.title = "Mobile Number should look like: 1234567890";
		field.required = true;
		const deleteBtn = document.createElement("span");
		deleteBtn?.addEventListener("click", (e) => {
			document.querySelector(".mobile-fields").removeChild(field.parentElement);
			noOfMobiles = document.querySelectorAll(".mobile-field").length;
			document.querySelector(".add-mobile-btn").removeAttribute("disabled");
		});
		deleteBtn.innerHTML =
			'<img src="assets/close-line-svgrepo-com.svg" alt="close-btn" width="32">';
		div.appendChild(field);
		div.appendChild(deleteBtn);
		document.querySelector(".mobile-fields").appendChild(div);
		noOfMobiles = document.querySelectorAll(".mobile-field").length;
	}
});

let currentContact = "";
let isOpen = false;
let currentContactInfo = {};

const generateMailComponent = (id, mail) => {
	const mailDiv = document.createElement("div");
	mailDiv.className = "mail";
	mailDiv.innerHTML = `	<span title="${mail}" class="c-mail">${mail}</span> 
					<span class="right-side">
					<a href="deleteContactMail/${id}">
								<img src="assets/delete-icon.svg" alt="delete-mail" width="24">
							</a>
					</span>`;
	document.querySelector(".contact-mails").appendChild(mailDiv);
}

const generateMobileComponent = (id, mobile) => {
	const mobileDiv = document.createElement("div");
	mobileDiv.className = "mail";
	mobileDiv.innerHTML = `	<span class="c-mobile">${mobile}</span> 
					<span class="right-side">
					<a href="deleteContactMobile/${id}">
								<img src="assets/delete-icon.svg" alt="delete-mail" width="24">
							</a>
					</span>`;
	document.querySelector(".contact-mobiles").appendChild(mobileDiv);
}

document.querySelectorAll(".contact-name").forEach((name) =>
	name?.addEventListener("click", (e) => {
		if (currentContact === e.target.dataset.id) {
			document.querySelector(".contact-container").classList.toggle("hide");
			isOpen = false;
		} else {
			currentContact = e.target.dataset.id;
			let con_id = e.target.dataset.id;
			fetch("contact/" + con_id)
				.then((res) => res.json())
				.then((data) => {
					currentContactInfo = data;
					//document.querySelector(".contact-fname").innerText = data.fname + " " + data.mname + " " + data.lname;
					document.querySelector(".contact-fname").innerText = data.fname;
					document.querySelector(".c-fname").innerText = data.fname;
					document.querySelector(".c-mname").innerText = data.mname;
					document.querySelector(".c-lname").innerText = data.lname;
					document.querySelector(".c-gender").innerText = data.gender;
					document.querySelector(".c-notes").innerText = data.notes;
					document.querySelector(".c-home").innerText = data.home;
					document.querySelector(".c-work").innerText = data.work;
					document.querySelector(".c-dob").innerText = data.dob;
					document.querySelector(".contact-mails").innerHTML = `<div class="title">
											Emails: <span class="add-contact-mail-icon"><img
												src="assets/pencil-cursor-svgrepo-com.svg" width='16'
												alt="add-email"></span>
										</div>`;
					document.querySelector(".contact-mobiles").innerHTML = `<div class="title">
																Mobile Numbers: <span class="add-contact-mobile-icon"><img
																	src="assets/pencil-cursor-svgrepo-com.svg" width='16'
																	alt="add-email"></span>
															</div>`;
					data.mails.forEach((m) => {
						generateMailComponent(m.id, m.mail);
					});
					data.mobiles.forEach((mo) => {
						generateMobileComponent(mo.id, mo.mobile);
					});
					document.querySelectorAll(".add-contact-mail-icon").forEach((btn) => {
						btn?.addEventListener("click", () => {
							document.querySelector(".add-email-form").querySelector("#hidden-type").value = "contact";
							document.querySelector(".add-email-form").querySelector("#hidden-contact-id").value = currentContact;
							document.querySelector(".add-email-form").classList.toggle("hide");
						});
					});
					document.querySelectorAll(".add-contact-mobile-icon").forEach((btn) => {
						btn?.addEventListener("click", () => {
							document.querySelector(".add-mobile-form").querySelector("#hidden-type").value = "contact";
							document.querySelector(".add-mobile-form").querySelector("#hidden-contact-id").value = currentContact;
							document.querySelector(".add-mobile-form").classList.toggle("hide");
						});
					});
				});
			if (!isOpen) {
				document.querySelector(".contact-container").classList.toggle("hide");
				isOpen = true;
			}
		}
	})
);

document.querySelectorAll(".edit-contact").forEach((contact) => {
	console.log(contact);
	contact.addEventListener("click", () => {		
	const editContactForm = document.querySelector(".edit-contact-form");
	editContactForm.classList.toggle("hide");
    editContactForm.querySelector("#hidden-contact-id").value = currentContact;
	console.log(currentContact);
	editContactForm.querySelector(".fname").value = currentContactInfo.fname;
	editContactForm.querySelector(".mname").value = currentContactInfo.mname;
	editContactForm.querySelector(".lname").value = currentContactInfo.lname;
	if (currentContactInfo.gender.toLowerCase() == "male") {
		editContactForm.querySelector(".male").setAttribute("checked", true);
	} else {
		editContactForm.querySelector(".female").setAttribute("checked", true);
	}
	editContactForm.querySelector(".dob").value = currentContactInfo.dob;
	editContactForm.querySelector(".notes").value = currentContactInfo.notes;
	editContactForm.querySelector(".home").value = currentContactInfo.home;
	editContactForm.querySelector(".work").value = currentContactInfo.work;
	});
});

document.querySelectorAll(".add-contact-mail-icon").forEach((btn) => {
	btn?.addEventListener("click", () => {
		document.querySelector(".add-email-form").querySelector("#hidden-type").value = "contact";
		document.querySelector(".add-email-form").querySelector("#hidden-contact-id").value = currentContact;
		document.querySelector(".add-email-form").classList.toggle("hide");
	});
});

document.querySelectorAll(".add-contact-mobile-icon").forEach((btn) => {
	btn?.addEventListener("click", () => {
		document.querySelector(".add-mobile-form").querySelector("#hidden-type").value = "contact";
		document.querySelector(".add-mobile-form").querySelector("#hidden-contact-id").value = currentContact;
		document.querySelector(".add-mobile-form").classList.toggle("hide");
	});
});

document.querySelectorAll(".group-name").forEach((group) =>
	group?.addEventListener("click", (e) => {
		const growAnime = [{ transform: "scaleY(0)" }, { transform: "scaleY(1)" }];

		const growTiming = {
			duration: 250,
			iterations: 1,
		};
		const shrinkAnime = [
			{ transform: "scaleY(1)" },
			{ transform: "scaleY(0)" },
		];

		const shrinkTiming = {
			duration: 250,
			iterations: 1,
		};

		let cardNumber = e.target.dataset.order;
		let container = document.querySelector(`.c${cardNumber}`);

		if (container.style.minHeight === "75px") {
			container.style.minHeight = "0";
			container.style.padding = "0";
			container.animate(shrinkAnime, shrinkTiming);
			container.querySelectorAll(".contact").forEach((c) => {
				c.animate(shrinkAnime, shrinkTiming);
			});
		} else {
			container.style.minHeight = "75px";
			container.style.padding = "1rem";
			container.animate(growAnime, growTiming);
			container.querySelectorAll(".contact").forEach((c) => {
				c.animate(growAnime, growTiming);
			});
		}
		container.scrollIntoView({ behavior: "smooth", block: "start" });
	})
);

document.querySelectorAll(".add-group-contact-btn").forEach((user) =>
	user?.addEventListener("click", (e) => {
		let availableContacts = document
			.querySelector(".add-group-contact-form")
			.querySelectorAll("contact-option").length;
		availableContacts = 1;
		let group_id = e.target.dataset.id;
		fetch("getContactsByGroup/" + group_id)
			.then((res) => res.json())
			.then((d) => {
				console.log(d.contacts);
				if (d.status == 1) {
					document.querySelector(".current-group-name").innerText = d.name;
					document.querySelector("#hidden-group-id").value = group_id;
					document.querySelector(".filtered-contacts").innerHTML = "";
					d.contacts.forEach((cont) => {
						const div = document.createElement("div");
						div.className = "contact-option";
						const inp = document.createElement("input");
						inp.type = "checkbox";
						inp.name = "contact";
						inp.id = "contact" + cont.id;
						inp.value = "" + cont.id;
						const lab = document.createElement("label");
						lab.htmlFor = "contact" + cont.id;
						lab.innerText = cont.fname;
						availableContacts++;
						div.appendChild(inp);
						div.appendChild(lab);
						document.querySelector(".filtered-contacts").appendChild(div);
					});
					document
						.querySelector(".add-group-contact-form")
						.classList.toggle("hide");
				} else {
					const msg = document.createElement("div");
					msg.className = "message";
					msg.innerText = "No Contact Left to Add.";
					msg?.addEventListener("click", (e) => {
						e.target.style.display = "none";
					});
					document.body.insertBefore(msg, document.body.firstChild);
					setTimeout(() => {
						msg.remove();
					}, 2000);
				}
			});
	})
);

const msg = document.querySelector('.message');

setTimeout(() => {
	msg.remove();
}, 2000);