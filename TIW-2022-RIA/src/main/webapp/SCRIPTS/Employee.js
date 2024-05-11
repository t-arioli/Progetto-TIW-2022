{
	var personalMessage;
	var quotesList;
	var newQuotes;
	var quoteDetails;
	var pageOrchestrator = new PageOrchestrator();
	var user = JSON.parse(sessionStorage.getItem("user"));
	//window event manager
	window.addEventListener(
		"load", 
		() => {
	    	if (user == null) {
	      		window.location.href = "Login.html";
	    	} else {
	      		pageOrchestrator.start(); // initialize the components
	      		pageOrchestrator.refresh(); //refresh
	    	} // display initial content
	  	}, 
	  	false);
	
	//VIEW COMPONENTS
	//display personal welcome message
	function PersonalMessage(username, messagecontainer) {
		this.username = username;
	    this.show = function() {
	      messagecontainer.textContent = this.username;
	    }
	}
	
		//display all the quotes <div id="quotes"><div class="list">
	function QuotesList(_textAlert, _container, _list){
		this.textAlert = _textAlert; //message with infos <p><span id="quotesList_alert" class="serverMessage"></span><p>
		this.container = _container;//<table id="quotesList_container">
		this.list = _list;//<tbody id="quotesList_list"></tbody>
		var self = this;
		
		this.show = function(){ //gets the quotes from server call
			makeGetCall(
				"GetQuotes",
				function(req) {
					if(req.readyState == XMLHttpRequest.DONE) {	
						if(req.status == 200){
							var quotes = JSON.parse(req.responseText);
							//console.log(quotes);
							self.update(quotes); //update from there, only entry point for that method
							if(quotes.length == 0){
								console.log(self.textAlert);
								self.container.style.visibility = "hidden";	
								self.textAlert.textContent = "No quotes";
								return;
							}
						}else{
							console.log(req.status);
							self.textAlert.textContent = req.responseText;
						}
					}
				}
			);
		};
		
		this.update = function(quotes){//build the table <tbody id="quotesList_list"></tbody>
			var row, cell_id, cell_client, cell_prod, cell_dataC, cell_dataV, cell_price;
			
			while(this.list.firstChild){ //remove outdated list
				this.list.removeChild(this.list.lastChild);
			}
			
			quotes.forEach(function(quote){//creates one row for each quote in the list received by the server
				row = document.createElement("tr");
				
				cell_id = document.createElement("td");
				cell_id.textContent = quote.id;
				row.appendChild(cell_id);
				
				cell_client = document.createElement("td");
				cell_client.textContent = quote.client.username;
				row.appendChild(cell_client);
				
				cell_prod = document.createElement("td");
				cell_prod.textContent = quote.product.name;
				row.appendChild(cell_prod);
				
				cell_dataC = document.createElement("td");
				cell_dataC.textContent = quote.dateCreation;
				row.appendChild(cell_dataC);
				
				cell_price = document.createElement("td");
				cell_price.textContent = quote.price;
				row.appendChild(cell_price);
				
				cell_dataV = document.createElement("td");
				cell_dataV.textContent = quote.dateValidation;
				row.appendChild(cell_dataV);
	
				self.list.appendChild(row); //append each table row <tr> as this.list child
			});
			this.container.style.visibility = "visible";	
		};	
	}
	
	function NewQuotes(_textAlert, _container, _list){
		this.textAlert = _textAlert;
		this.container = _container; //<table id="productsList_container">
		this.list = _list;
		var self = this;
		
		this.start = function(anchor){
			anchor.href = "#";
			var p = document.createTextNode("Refresh quotes");
			anchor.appendChild(p);
			
			anchor.addEventListener(
				"click",
				() => {
					self.load();
				},
				false);
			this.container.style.visibility = "hidden";	
		};
		
		this.load = function(){
			self.reset();
			makeGetCall(
				"GetNewQuotes",
				function(req){
					if(req.readyState == XMLHttpRequest.DONE){
						if(req.status == 200){
							var products = JSON.parse(req.responseText);
							self.update(products);
							if(quotes.length == 0){
								console.log(self.textAlert);
								self.container.style.visibility = "hidden";	
								self.textAlert.textContent = "No products, try later";
								return;
							}
						}else{
							self.textAlert.textContent = req.responseText;
						}
					}
				})
		};

		this.update = function(quotes){
			var row, cell_data, cell_client, cell_prod, cell_link, a, p;
			
			quotes.forEach(function(quote){
				row = document.createElement("tr");
				
				cell_data = document.createElement("td");
				cell_data.textContent = quote.dateCreation;
				row.appendChild(cell_data);
				
				cell_client = document.createElement("td");
				cell_client.textContent = quote.client.username;
				row.appendChild(cell_client);
				
				cell_prod = document.createElement("td");
				cell_prod.textContent = quote.product.name;
				row.appendChild(cell_prod);
				
				cell_link = document.createElement("td");
				a = document.createElement("a");
				cell_link.appendChild(a);
				p = document.createTextNode("Add price");
				a.appendChild(p);
				//a.setAttribute("prodId", prod.code);
				a.addEventListener(
					"click",
					() => {
						quoteDetails.show(quote);
					},
					false);
				a.href = "#";
				row.appendChild(cell_link);
				self.list.appendChild(row);
			});
			this.container.style.visibility = "visible";	
		};
		
		this.reset = function (){
			while(this.list.firstChild){
				this.list.removeChild(this.list.lastChild);
			}
			this.container.style.visibility = "hidden";	
		};
		
		
	}
	
	//display all the quotes info 
	function QuoteDetails(_container, _quote, _alert){
		this.quoteHTML = _quote; //the elements in the HTML that display the infos
		this.container = _container;
		this.alert = _alert;
		this.quoteBean;// the object quote 
		var self = this;
		
		this.show = function(quote){
			this.quoteBean = quote;
			this.update();
		};
		this.update = function(){
			this.quoteHTML.id.textContent = this.quoteBean.id;
			this.quoteHTML.client.textContent = this.quoteBean.client.username;
			this.quoteHTML.dateCreation.textContent = this.quoteBean.dateCreation;
			this.quoteHTML.product.textContent = this.quoteBean.product.code;
			
			var ul, li;
			ul = document.createElement("ul");
			this.quoteBean.options.forEach(function(e){
				var type = "";
				e.onSale ? type = " (On sale)" : type = " (Standard)";
				li = document.createElement("li");
				li.textContent = e.name + type;
				ul.appendChild(li);
			});
			this.quoteHTML.options.removeChild(this.quoteHTML.options.lastChild);//remove outdated list
			this.quoteHTML.options.appendChild(ul);
			
			this.quoteHTML.button.addEventListener(
				"click",
				(e) => {
					var form = e.target.closest("form");
					var prod = document.createElement("input");
					prod.type = "text";
					prod.name = "id";
					prod.value = this.quoteBean.id;
					form.appendChild(prod);
					if(form.checkValidity()){
						makePostCall(
							"AddPrice", 
							form,
							function (res){
								if(res.readyState == XMLHttpRequest.DONE) {	
									//console.log(res.status);
									if(res.status == 200) {
										self.reset();
										quotesList.show();
										newQuotes.load();
									}else{
										self.alert.textContent = res.responseText;
									}
								}
							}
						);
					}
					form.removeChild(form.lastChild); //remove fake input
				});
			this.container.style.visibility = "visible";
		};
		this.reset = function(){
			this.container.style.visibility = "hidden";
			this.alert.textContent = null;
		};
	}
	
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
			  //new quotes LIST
			  newQuotes = new NewQuotes(
				  document.getElementById("newQuotesList_alert"),
				  document.getElementById("newQuotesList_container"),
				  document.getElementById("newQuotesList_list")
			  );
			  newQuotes.start(document.getElementById("newQuotesList_call"));
			  //QUOTES DETAILS
			  quoteDetails = new QuoteDetails(
				  document.getElementById("quote-details"),
				  {
					  id: document.getElementById("quoteDetails_id"),
					  client: document.getElementById("quoteDetails_client"),
					  dateCreation: document.getElementById("quoteDetails_dateCreation"),
					  product: document.getElementById("quoteDetails_product"),
					  options: document.getElementById("quoteDetails_options"),
					  button: document.getElementById("addPriceButton")
				  },
				  document.getElementById("quoteDetails_alert")
			  );
			  //LOGOUT
			  document.getElementById("logout_href").addEventListener(
				  "click", 
				  () => {
					  window.sessionStorage.removeItem("user");
					}
					
				);
		};
		this.refresh = function(){
			quotesList.show();
			newQuotes.load();
			quoteDetails.reset();
		}; 
	}
}