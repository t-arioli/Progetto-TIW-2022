package it.project.bean;

import java.util.ArrayList;

public class Product {
	private Integer code;
	private String imageUrl;
	private String name;
	private ArrayList<Option> availableOptions;

	public Product() {
		this.code = null;
		this.imageUrl = "default.jpg";
		this.name = null;
		this.availableOptions = null;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public String getImageUrl() {
		return this.imageUrl;
	}

	public void setImageUrl(String url) {
		if (url != null)
			this.imageUrl = url;
	}
	
	public ArrayList<Option> getAvailableOptions(){
		return this.availableOptions;
	}
	
	public void setAvailableOptions(ArrayList<Option> options) {
		this.availableOptions = options;
	}
	
	@Override
	public String toString() {
		String s = "";
		s += this.code;
		s += this.name;
		s+= this.imageUrl;
		for(Option o : this.availableOptions)
			s+= o.getCode()+" "+o.getName();
		return s;
	}
}
