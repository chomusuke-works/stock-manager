package app.types;

public class ProductShelfQuantity {
	public String productName;
	public int quantity;
	public String shelfName;

	public ProductShelfQuantity(String productName, int quantity, String shelfName) {
		this.productName = productName;
		this.quantity = quantity;
		this.shelfName = shelfName;
	}
}
