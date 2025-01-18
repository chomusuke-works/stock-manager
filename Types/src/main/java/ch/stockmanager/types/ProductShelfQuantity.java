package ch.stockmanager.types;

public class ProductShelfQuantity {
	public String productName;
	public int quantity;
	public String shelfName;

	@SuppressWarnings("unused")
	public ProductShelfQuantity() {}

	public String getProductName() { return productName; }
	public int getQuantity() { return quantity; }
	public String getShelfName() { return shelfName; }

	public ProductShelfQuantity(String productName, int quantity, String shelfName) {
		this.productName = productName;
		this.quantity = quantity;
		this.shelfName = shelfName;
	}
}