function makeCall(method, url, formElement, callBack) {
	var req = new XMLHttpRequest(); 
	var sendF = new FormData(formElement);
	console.log(sendF.values);
	req.onreadystatechange = function() {
		callBack(req)
	}; 
	req.open(method, url);
	if (formElement == null) {
		req.send();
	} else {
	    req.send(sendF);
	    formElement.reset();
	}
}