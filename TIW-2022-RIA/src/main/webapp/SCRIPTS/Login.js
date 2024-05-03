{
	document
	.getElementById("loginButton")
	.addEventListener(
		'click', 
		(e) => {
			if(e.target.closest("form").checkValidity()){
				makeCall(
					"POST", 
					"Login", 
					e.target.closest("form"),
					function (res){
						if(res.readyState == XMLHttpRequest.DONE) {	
							switch (res.status) {
								case 200:
									var user = JSON.parse(req.responseText);
									sessionStorage.setItem('user', user);
									if (user.isAdmin)
										window.location.href = "EmployeeHome.html";
									else
										window.location.href = "ClientHome.html;"
									break;
								case 400: // bad request
									document.getElementById("loginMessage").textContent = res.responseText;
									break;
								case 401: // unauthorized
									document.getElementById("loginMessage").textContent = res.responseText;
									break;
								case 500: // server error
									document.getElementById("loginMessage").textContent = res.responseText;
									break;
							}
						}
					});
			}
	});
	
	document
	.getElementById("registerButton")
	.addEventListener(
		'click', 
		(e) => {
			if(e.target.closest("form").checkValidity()){
				makeCall(
					"POST", 
					"Register", 
					e.target.closest("form"),
					function (res){
						if(res.readyState == XMLHttpRequest.DONE) {	
							switch (res.status) {
								case 200:
									document.getElementById("registerMessage").textContent = res.responseText;
									break;
								case 400: // bad request
									document.getElementById("registerMessage").textContent = res.responseText;
									break;
								case 401: // unauthorized
									document.getElementById("registerMessage").textContent = res.responseText;
									break;
								case 500: // server error
									document.getElementById("registerMessage").textContent = res.responseText;
									break;
							}
						}
					});
			}
	});
}
