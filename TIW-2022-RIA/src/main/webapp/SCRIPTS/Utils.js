function makeGetCall(url, callBack) {
	
	var req = new XMLHttpRequest(); 
	req.onreadystatechange = function() {
		callBack(req)
	}; 
	req.open("GET", url);
	req.send();
}
function makePostCall(url, formElement, callBack) {
	 var req = new XMLHttpRequest(); // visible by closure
	 var sendF = new FormData(formElement);
	 //console.log(sendF);
	 req.onreadystatechange = function() {
	      callBack(req)
	 }; // closure
	 req.open("POST", url);
	 req.send(sendF);
	 formElement.reset();
}
