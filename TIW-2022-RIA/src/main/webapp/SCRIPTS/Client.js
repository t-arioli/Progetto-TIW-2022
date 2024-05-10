{
	//declaration of various VIEW elements and session elements
	var personalMessage;
	var quotesList;
	var quoteDetails;
	var productsList;
	var createQuote;
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
			var row, cell_date, cell_price, cell_link, a, p;
			
			while(this.list.firstChild){ //remove outdated list
				this.list.removeChild(this.list.lastChild);
			}
			
			quotes.forEach(function(quote){//creates one row for each quote in the list received by the server
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
					() => {
						quoteDetails.show(quote); //only entry point 
					},
					false);
				a.href = "#";
				row.appendChild(cell_link);
				self.list.appendChild(row); //append each table row <tr> as this.list child
			});
			this.container.style.visibility = "visible";	
		};	
	}
	//display all the quotes info 
	function QuoteDetails(_container, _quote){
		this.quoteHTML = _quote; //the elements in the HTML that display the infos
		this.container = _container;
		this.quoteBean;// the object quote 
		
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
	//list of all products from server
	function ProductsList(_textAlert, _container, _list){
		this.textAlert = _textAlert;
		this.container = _container; //<table id="productsList_container">
		this.list = _list;
		var self = this;
		
		this.start = function(anchor){
			anchor.href = "#";
			var p = document.createTextNode("Refresh products");
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
									console.log(res.responseText);
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
			quoteDetails.reset();
			productsList.load();
			createQuote.reset();
		}; 
	}
}