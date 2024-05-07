{
	var personalMessage;
	var quotesList;
	var quoteDetails;
	var productsList;
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
		
		this.show = function(){
			makeGetCall(
				"GetQuotes",
				function(req) {
					if(req.readyState == XMLHttpRequest.DONE) {	
						if(req.status == 200){
							var quotes = JSON.parse(req.responseText);
							self.update(quotes);
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
			this.container.style.visibility = "visible";	
		};	
	}
	
	function QuoteDetails(_textAlert, _container, _quote){
		this.textAlert = _textAlert;
		this.quote = _quote;
		this.container = _container;
		
		this.show = function(prevId){
			var self = this;
			makeGetCall(
				"GetQuoteDetails?prevId=" + prevId,
				function(req){
					if(req.readyState == XMLHttpRequest.DONE){
						if(req.status == 200){
							var details = JSON.parse(req.responseText);
							self.update(details);
						}
					}
				}
			)
		};
		this.update = function(_quote){
			this.quote.product.textContent = _quote.product.name;
			if(_quote.product.imageUrl == null){
				this.quote.image.src = "IMAGES/default.jpg";
			}else{
				this.quote.image.src = "IMAGES/"+_quote.product.imageUrl;
			}
			this.quote.dateCreation.textContent = _quote.dateCreation;
			this.quote.dateValidation.textContent = _quote.dateValidation;
			this.quote.price.textContent = _quote.price;
			
			var ul, li;
			ul = document.createElement("ul");
			_quote.options.forEach(function(e){
				var type = "";
				e.onSale ? type = " (On sale)" : type = " (Standard)";
				li = document.createElement("li");
				li.textContent = e.name + type;
				ul.appendChild(li);
			});
			this.quote.options.removeChild(this.quote.options.lastChild);
			this.quote.options.appendChild(ul);

			this.container.style.visibility = "visible";
		};
		this.reset = function(){
			this.container.style.visibility = "hidden";
		};
	}
	
	function ProductsList(_textAlert, _container, _list){
		this.textAlert = _textAlert;
		this.container = _container;
		this.list = _list;
		var self = this;
		
		this.start = function(anchor){
			anchor.href = "#";
			var p = document.createTextNode("Load Products");
			anchor.appendChild(p);
			
			anchor.addEventListener(
				"click",
				() => {
					self.load();
					//console.log("this works");
				},
				false);
			this.container.style.visibility = "hidden";	
		};
		
		this.load = function(){
			self.reset();
			makeGetCall(
				"GetProductsList",
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

		this.update = function(products){
			var row, cell_name, cell_img, cell_link, a, p, img;
			
			products.forEach(function(prod){
				row = document.createElement("tr");
				
				cell_name = document.createElement("td");
				cell_name.textContent = prod.name;
				row.appendChild(cell_name);
				
				cell_img = document.createElement("td");
				img = document.createElement("img");
				img.src = prod.imageUrl;
				cell_img.appendChild(img);
				row.appendChild(cell_img);
				
				cell_link = document.createElement("td");
				a = document.createElement("a");
				cell_link.appendChild(a);
				p = document.createTextNode("Choose");
				a.appendChild(p);
				a.setAttribute("prodId", prod.id);
				a.addEventListener(
					"click",
					() => {
						//wizard for create quote
						//ChoseProduct(prodId)
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
/*	
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
			  //QUOTES DETAILS
			  quoteDetails = new QuoteDetails(
				  null,
				  document.getElementById("quote-details"),
				  {
					  product: document.getElementById("quoteDetails_product"),
					  image: document.getElementById("quoteDetails_img"),
					  dateCreation: document.getElementById("quoteDetails_dateCreation"),
					  dateValidation: document.getElementById("quoteDetails_dateValidation"),
					  price: document.getElementById("quoteDetails_price"),
					  options: document.getElementById("quoteDetails_options")
				  }
			  );
			  //PRODUCTS LIST
			  productsList = new ProductsList(
				  //document.getElementById("productsList_call"),
				  document.getElementById("productsList_alert"),
				  document.getElementById("productsList_container"),
				  document.getElementById("productsList_list")
			  );
			  productsList.start(document.getElementById("productsList_call"));
			  //WIZARD
		};
		this.refresh = function(){
			quotesList.show();
			quoteDetails.reset();
			productsList.reset();
		}; 
	}
	
}