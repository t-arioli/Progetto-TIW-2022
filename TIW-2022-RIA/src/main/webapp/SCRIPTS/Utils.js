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
	 console.log(sendF);
	 req.onreadystatechange = function() {
	      callBack(req)
	 }; // closure
	 req.open("POST", url);
	 req.send(sendF);
	 formElement.reset();
}
/*function makeJSONPostCall(url, data, callback){
	var req = new XMLHttpRequest();
	
	var sendData = JSON.stringify(data);
	
	req.onreadystatechange = function(){
		callback(req);
	}
	req.open("POST", url);
	req.setRequestHeader("Content-Type", "application/json")
	req.send(sendData);
	
}*/