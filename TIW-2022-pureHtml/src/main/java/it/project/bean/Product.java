package it.project.bean;

public class Product {
	private Integer code;
	private String imageUrl;
	private String name;
	
	public Product() {
		this.code = null;
		this.imageUrl = "default.jpg";
		this.name = null;
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
		if(url != null)
			this.imageUrl = url;
	}
}
