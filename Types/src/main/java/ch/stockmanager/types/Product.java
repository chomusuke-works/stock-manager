package ch.stockmanager.types;

public class Product {
	public long code;
	public String name;
	public double price;
	public int supplierId;

	public Product(long code, String name, double price, int supplierId) {
		this.code = code;
		this.name = name;
		this.price = price;
		this.supplierId = supplierId;
	}
}
