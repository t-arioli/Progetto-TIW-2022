{
	var personalMessage;
	var quotesList;
	var quoteDetails;
	var pageOrchestrator = new PageOrchestrator();
	var user = JSON.parse(sessionStorage.getItem("user"));
	
	window.addEventListener(
		"load", 
		() => {
	    	if (user == null) {
	      		window.location.href = "Login.html";
	    	} else {
	      		pageOrchestrator.start(); // initialize the components
	      		pageOrchestrator.refresh();
	    	} // display initial content
	  	}, 
	  	false);
	
	//VIEW COMPONENTS
	
	function PersonalMessage(username, messagecontainer) {
		this.username = username;
	    this.show = function() {
	      messagecontainer.textContent = this.username;
	    }
	}
	
	function QuotesList(_textAlert, _container, _list){
		this.textAlert = _textAlert;
		this.container = _container;
		this.list = _list;
		var self = this;
		
		//this.show = function(next){
		this.show = function(){
			makeGetCall(
				"GetQuotes",
				function(req) {
					if(req.readyState == XMLHttpRequest.DONE) {	
						if(req.status == 200){
							var quotes = JSON.parse(req.responseText);
							//console.log(quotes);
							self.update(quotes);
							//console.log(quotes);
							if(quotes.length == 0){
								console.log(self.textAlert);
								self.textAlert.textContent = "No quotes";
								return;
							}
							/*if(next){
								next();
							}*/
						}else{
							console.log(req.status);
							self.textAlert.textContent = req.responseText;
						}
					}
				}
			);
		};
		
		this.reset = function(){
			
		};
		
		this.update = function(quotes){
			var row, cell_date, cell_price, cell_link, a, p;
			
			quotes.forEach(function(quote){
				row = document.createElement("tr");
				
				cell_date = document.createElement("td");
				cell_date.textContent = quote.dateCreation;
				row.appendChild(cell_date);
				
				cell_price = document.createElement("td");
				cell_price.textContent = quote.price;
				row.appendChild(cell_price);
				
				cell_link = document.createElement("td");
				a = document.createElement("a");
				cell_link.appendChild(a);
				p = document.createTextNode("Show");
				a.appendChild(p);
				a.setAttribute("prevId", quote.id);
				a.addEventListener(
					"click",
					(e) => {
						quoteDetails.show(e.target.getAttribute("prevId"));
					},
					false);
				a.href = "#";
				row.appendChild(cell_link);
				self.list.appendChild(row);
			});
			this.container.visibility = "visible";	
		};	
	}
	
/*	function QuotesDetails(){
		
	}
	
	function ProductsList(){
		
	}
	
	function CreateQuote(){
		
	}
*/	
	//like a main()
  	function PageOrchestrator(){
		  
		this.start = function(){
			//PERSONAL MESSAGE
			personalMessage = new PersonalMessage(
				user.username,
	        	document.getElementById("username"));
	      	personalMessage.show();
	      	//QUOTES LIST
	      	quotesList = new QuotesList(
				  document.getElementById("quotesList_alert"),
				  document.getElementById("quotesList_container"),
				  document.getElementById("quotesList_list")
			  );
		};
		this.refresh = function(){
			quotesList.show();
		}; 
	}
	
}