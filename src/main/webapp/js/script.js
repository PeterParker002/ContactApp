let initialFormValues = {};

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

document.querySelector(".close-sync-contact-form")?.addEventListener("click", () => {
	document.querySelector(".available-sync-mails").classList.toggle("hide");
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

	const form = document.getElementById("edit-profile-form");
	const inputs = form.querySelectorAll("input");
	inputs.forEach(input => {
		if (input.type === "radio") {
			console.log(input.name, input.checked);
            if (input.checked) {
                initialFormValues[input.name] = input.value;
            }
        } else {
            initialFormValues[input.name] = input.value;
        }
		input.addEventListener("input", () => {
			let isChanged = false;
			inputs.forEach(input => {
				if (input.type === "radio" & input.checked) {
					if (initialFormValues[input.name] !== input.value) {
		                isChanged = true;
		            }
				}
				else if (initialFormValues[input.name] !== input.value & input.type !== "radio") {
					console.log(initialFormValues[input.name], input.value);
					isChanged = true;
				}
			});
			console.log(isChanged);
			form.getElementsByClassName("submitBtn")[0].disabled = !isChanged;
		});
	});
	console.log(initialFormValues);
	form.getElementsByClassName("submitBtn")[0].disabled = true;
});

function submitForm(event) {
    event.preventDefault();
    const form = event.target;
    const inputs = form.querySelectorAll("input");
    const formData = {};

    inputs.forEach(input => {
		if (input.type === "hidden") {
			formData[input.name] = input.value;
		} else if (input.type === "radio") {
            if (input.checked) {
				console.log(initialFormValues[input.name], input.value, initialFormValues[input.name] != input.value);
				if (initialFormValues[input.name] != input.value) {					
                	formData[input.name] = input.value;
				}
            }
        } else {
			console.log(initialFormValues[input.name], input.value, initialFormValues[input.name] != input.value);
			if (initialFormValues[input.name] != input.value) {
				formData[input.name] = input.value;
			}
        }
    });
	console.log(formData);
	if (Object.keys(formData).length !== 0) {
    fetch(form.action, {
        method: form.method,
		headers: {
	        "Content-Type": "application/json",
	    },
        body: JSON.stringify(formData)
    })
    .then(response => response.text())
    .then(data => {
		//document.querySelector(".edit-profile-form").classList.toggle("hide");
		sessionStorage.setItem("message", "Profile Updated successfully!");
		location.reload();
    })
    .catch(error => console.error("Error:", error));
	} else {
		const msg = document.createElement("div");
		msg.className = "message";
		msg.innerText = "Don't Submit the same data.";
		msg?.addEventListener("click", (e) => {
			e.target.style.display = "none";
		});
		document.body.insertBefore(msg, document.body.firstChild);
		document.querySelector(".add-mail-btn").setAttribute("disabled", true);

		setTimeout(() => {
			msg.remove();
		}, 2000);
	}
}

document.addEventListener("DOMContentLoaded", () => {
    const message = sessionStorage.getItem("message");
    if (message) {
		const msg = document.createElement("div");
		msg.className = "message";
		msg.textContent = message;
		msg?.addEventListener("click", (e) => {
			e.target.style.display = "none";
		});
		document.body.insertBefore(msg, document.body.firstChild);
		setTimeout(() => {
			msg.remove();
		}, 2000);
        sessionStorage.removeItem("message");
    }
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
					if (data.mails.length > 0) {						
					data.mails.forEach((m) => {
						generateMailComponent(m.id, m.mail);
					});
					} else {
						const emptyMailContainer = document.createElement("small");
						emptyMailContainer.className = "no-mail";
						emptyMailContainer.innerText = "No Emails Found 🫤";
						document.querySelector(".contact-mails").appendChild(emptyMailContainer);
					}
					if (data.mobiles.length > 0) {						
					data.mobiles.forEach((m) => {
						generateMobileComponent(m.id, m.mobile);
					});
					} else {
						const emptyMobileContainer = document.createElement("small");
						emptyMobileContainer.className = "no-mail";
						emptyMobileContainer.style.textAlign = "center";
						emptyMobileContainer.innerText = "No Mobile Numbers Found 🫤";
						document.querySelector(".contact-mobiles").appendChild(emptyMobileContainer);
					}
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
	//editContactForm.classList.toggle("hide");
    editContactForm.querySelector("#hidden-contact-id").value = currentContact;
	console.log(currentContact);
	editContactForm.querySelector(".fname").value = currentContactInfo.fname;
	editContactForm.querySelector(".mname").value = currentContactInfo.mname;
	editContactForm.querySelector(".lname").value = currentContactInfo.lname;
	if (currentContactInfo.gender != null) {	
	if (currentContactInfo.gender.toLowerCase() == "male") {
		editContactForm.querySelector(".male").setAttribute("checked", true);
	} else {
		editContactForm.querySelector(".female").setAttribute("checked", true);
	}
	}
	editContactForm.querySelector(".dob").value = currentContactInfo.dob;
	editContactForm.querySelector(".notes").value = currentContactInfo.notes;
	editContactForm.querySelector(".home").value = currentContactInfo.home;
	editContactForm.querySelector(".work").value = currentContactInfo.work;
	editContactFormHandler(editContactForm);
	});
});

const editContactFormHandler = (form) => {
	initialFormValues = {};
	form.classList.toggle("hide");
	
	const inputs = form.querySelectorAll("input");
	inputs.forEach(input => {
		if (input.type === "radio") {
			console.log(input.name, input.checked);
            if (input.checked) {
                initialFormValues[input.name] = input.value;
            }
        } else {
            initialFormValues[input.name] = input.value;
        }
		input.addEventListener("input", () => {
			let isChanged = false;
			inputs.forEach(input => {
				if (input.type === "hidden") {
					isChanged = false;
				}
				if (input.type === "radio" & input.checked) {
					if (initialFormValues[input.name] !== input.value) {
		                isChanged = true;
		            }
				}
				else if (initialFormValues[input.name] !== input.value & input.type !== "radio") {
					console.log(initialFormValues[input.name], input.value);
					isChanged = true;
				}
			});
			console.log(isChanged);
			form.getElementsByClassName("submitBtn")[0].disabled = !isChanged;
		});
	});
	console.log(initialFormValues);
	form.getElementsByClassName("submitBtn")[0].disabled = true;
}

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

document.querySelector(".sync-contact-btn").addEventListener('click', () => {
	const modal = document.querySelector(".available-sync-mails");
	modal.classList.toggle("hide");
	modal.querySelector(".sync-mails").innerHTML = "";
	fetch("/getAvailableSyncMailsServlet").then(res => res.json()).then(data => {
		if (Object.keys(data).length > 0) {
		Object.entries(data).forEach(([id, mail]) => {
			const linkEl = document.createElement("a");
			linkEl.href = "/GoogleContactSyncServlet?id="+id;
			linkEl.className = "mail-btn";
			linkEl.innerText = mail;
			modal.querySelector(".sync-mails").appendChild(linkEl);
		});
		} else {
			const noContactsEl = document.createElement("div");
			noContactsEl.classList.add("no-contact");
			noContactsEl.classList.add("c-white");
			noContactsEl.innerText = "There is no account currently in sync!";
			modal.querySelector(".sync-mails").appendChild(noContactsEl);
			modal.querySelector(".sync-mails").classList.add("center");
		}
	});
});

const msg = document.querySelector('.message');

let currentLink = "";

document.querySelectorAll(".delete-link").forEach(function(link) {
    link.addEventListener("click", function(event) {
        event.preventDefault();
        
        currentLink = link.href;
		console.log(link.href);
        document.querySelector(".consent-screen").classList.toggle("hide");
    });
});

document.getElementById("yes-btn").addEventListener("click", function() {
	console.log(currentLink);
    if (currentLink) {
        window.location.href = currentLink;
    }
	document.querySelector(".consent-screen").classList.toggle("hide");
});

document.getElementById("no-btn").addEventListener("click", function() {
	document.querySelector(".consent-screen").classList.toggle("hide");
});

setTimeout(() => {
	msg.remove();
}, 2000);