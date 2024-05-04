function makeGetCall(url, callBack) {
	
	var req = new XMLHttpRequest(); 
	req.onreadystatechange = function() {
		callBack(req)
	}; 
	req.open("GET", url);
	req.send();
}

function makePostCall(url, formElement, callBack) {
	
	var req = new XMLHttpRequest(); 
	var sendF = new FormData(formElement);
	
	req.onreadystatechange = function() {
		callBack(req)
	}; 
	req.open("POST", url);
	req.send(sendF);
	formElement.reset();
}