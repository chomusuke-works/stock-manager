package ch.stockmanager.server.types;

public class Product {
	private long code;
	private String name;
	private double price;
	private int supplierId;

	@SuppressWarnings("unused")
	public Product() {
	}

	public Product(long code, String name, double price, int supplierId) {
		this.code = code;
		this.name = name;
		this.price = price;
		this.supplierId = supplierId;
	}

	public long getCode() {
		return code;
	}

	public void setCode(long code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(int supplierId) {
		this.supplierId = supplierId;
	}
}
