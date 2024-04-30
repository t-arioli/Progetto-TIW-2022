package it.project.bean;

import java.util.ArrayList;
import java.util.Date;

public class Quote {
	private Integer id;
	private User client;
	private User employee;
	private Product product;
	private ArrayList<Option> options;
	private Float price;
	private Date dateCreation;
	private Date dateValidation;
	
	public Quote() {
		this.id = null;
		this.price = null;
		this.dateCreation = null;
		this.dateValidation = null;
		this.client = null;
		this.employee = null;
		this.product = null;
		this.options = null;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public void setClient(User client) {
		this.client = client;
	}
	
	public User getClient() {
		return this.client;
	}
	
	public void setEmployee(User employee) {
		this.employee = employee;
	}
	
	public User getEmployee() {
		return this.employee;
	}
	
	public Product getProduct() {
		return this.product;
	}
	
	public void setProduct(Product code) {
		this.product = code;
	}
	
	public void setPrice(Float price) {
		this.price = price;
	}
	
	public Float getPrice() {
		return price;
	}
	
	public Date getDateCreation() {
		return this.dateCreation;
	}
	
	public void setDateCreation(Date date) {
		this.dateCreation = date;
	}
	
	public Date getDateValidation() {
		return this.dateValidation;
	}
	
	public void setDateValidation(Date date) {
		this.dateValidation = date;
	}
	
	public void setOptions(ArrayList<Option> oIds) {
		this.options = oIds;
	}
	
	public ArrayList<Option> getOptions(){
		return this.options;
	}
	
	@Override
	public String toString() {
		String result = "";
		result.concat((this.id).toString()+" ");
		result.concat(this.product.getName()+" ");
		if(this.price != null)
			result.concat(this.price+" ");
		result.concat(this.client.getUsername()+" ");
		if(this.employee != null)
			result.concat(this.employee.getUsername()+" ");
		for(Option o: this.options)
			result.concat(o.getName()+" ");
		result.concat(this.dateCreation.toString()+" ");
		if(this.dateValidation != null)
			result.concat(this.dateValidation.toString()+" ");
		return result;
	}
	
	

}
