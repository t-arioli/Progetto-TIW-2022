package it.project.bean;

public class Option {
	private Integer code;
	private Integer productCode;
	private boolean onSale;
	private String name;

	public Option() {
		this.code = null;
		this.productCode = null;
		this.onSale = false;
		this.name = null;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public int getCode() {
		return this.code;
	}

	public void setProduct(int code) {
		this.productCode = code;
	}

	public int getProduct() {
		return productCode;
	}

	public boolean isOnSale() {
		return this.onSale;
	}

	public void setStatus(boolean onSale) {
		this.onSale = onSale;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

}
