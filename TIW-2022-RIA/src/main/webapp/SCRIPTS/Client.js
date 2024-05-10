{
	var personalMessage;
	var quotesList;
	var quoteDetails;
	var productsList;
	var createQuote;
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
							//console.log(quotes);
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
			
			while(this.list.firstChild){
				this.list.removeChild(this.list.lastChild);
			}
			
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
						quoteDetails.show(quote);
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
		this.quoteHTML = _quote;
		this.container = _container;
		this.quoteBean;
		//var self = this;
		
		this.show = function(quote){
			this.quoteBean = quote;
			this.update();
		};
		this.update = function(){
			this.quoteHTML.product.textContent = this.quoteBean.product.name;
			if(this.quoteBean.product.imageUrl == null){
				this.quoteHTML.image.src = "IMAGES/default.jpg";
			}else{
				this.quoteHTML.image.src = "IMAGES/"+this.quoteBean.product.imageUrl;
			}
			this.quoteHTML.dateCreation.textContent = this.quoteBean.dateCreation;
			this.quoteHTML.dateValidation.textContent = this.quoteBean.dateValidation;
			this.quoteHTML.price.textContent = this.quoteBean.price;
			
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
				//a.setAttribute("prodId", prod.code);
				a.addEventListener(
					"click",
					() => {
						createQuote.show(prod);
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
	
	function CreateQuote(_container, _quote){
		this.container = _container;
		this.quoteHTML = _quote; //the quote render on DOM
		var self = this;
		this.product;
		this.availableOptions;
		this.chosenOptions = new Array();
		
		
		this.show = function(_product){
			self.product = _product;
			self.update();
			self.showOptions();
		};
		
		this.update = function(){
			this.quoteHTML.product.textContent = this.product.name;
			if(this.product.imageUrl == null){
				this.quoteHTML.image.src = "IMAGES/default.jpg";
			}else{
				this.quoteHTML.image.src = "IMAGES/"+this.product.imageUrl;
			}
			
			var ul, li;
			ul = document.createElement("ul");
			if(!this.chosenOptions.empty){
				this.chosenOptions.forEach(function(e){
					var type = "";
					e.onSale ? type = " (On sale)" : type = " (Standard)";
					li = document.createElement("li");
					li.textContent = e.name + type;
					ul.appendChild(li);
				});
			}
			this.quoteHTML.options.removeChild(this.quoteHTML.options.lastChild); //remove old outdated list
			this.quoteHTML.options.appendChild(ul);
			this.container.style.visibility = "visible";
		}
		
		this.showOptions = function() {	
			this.product.availableOptions.forEach(function(e){
				var type = "";
				var li, a;
				e.onSale ? type = " (On sale)" : type = " (Standard)";
				li = document.createElement("li");
				a = document.createElement("a");
				li.appendChild(a);
				a.textContent = e.name + type;
				a.setAttribute("id", e.code)
				a.addEventListener(
					"click",
					(e) => {
						self.addOption(self.product.availableOptions.find((x) => x.code == e.target.id));
					},
					false
				);
				a.href = "#";
				self.quoteHTML.availableOptions.appendChild(li);
			});
			
			this.quoteHTML.button.addEventListener(
				"click",
				() => {
					if(self.product != null && self.chosenOptions.length != 0){
						var form, prod, opt;
						//<form action = '#'>
						form = document.createElement("form");
						form.action = "#";
						//<input type="number" name="prodId">
						prod = document.createElement("input");
						prod.type = "text";
						prod.name = "prodId";
						prod.value = self.product.code;
						form.appendChild(prod);
						//<input type="number" name="opt">
						self.chosenOptions.forEach((o) => {
							opt = document.createElement("input");
							opt.type="text";
							opt.name="optId";
							opt.value = o.code;
							form.appendChild(opt);
						});
						//console.log(form);
						makePostCall(
							"CreateQuote",
							form, 
							function(res){
								if(res.status == 200){
									self.reset();
									quotesList.show();
								}else{
									console.log(res.status);
								}
							}
						);
					}
				}
			);			
		};
		
		this.addOption = function(option) {
			//very old school code
			var found = false;
			for(let i = 0; i < this.chosenOptions.length; i++){
				if(this.chosenOptions[i].code == option.code){
					found = true;
					break;
				}
			}
			if(!found){
				this.chosenOptions.push(option);
				self.update();
			}
		};
		
		this.reset = function(){
			this.container.style.visibility = "hidden";
			this.product = null;
			this.availableOptions = null;
			this.chosenOptions = [];
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
			  createQuote = new CreateQuote(
				  document.getElementById("create-quote"),
				  {
					  product: document.getElementById("createQuote_product"),
					  image: document.getElementById("createQuote_img"),
					  options: document.getElementById("createQuote_options"),
					  availableOptions: document.getElementById("createQuote_availableOptions"),
					  button: document.getElementById("createQuoteButton")
				  }
			  );
		};
		this.refresh = function(){
			quotesList.show();
			quoteDetails.reset();
			productsList.reset();
			createQuote.reset();
		}; 
	}
}