package ch.stockmanager.types;

public class ProductShelfQuantity {
	public String productName;
	public int quantity;
	public String shelfName;

	public long productCode;
	public int shelfId;

	@SuppressWarnings("unused")
	public ProductShelfQuantity() {}

	public String getProductName() { return productName; }
	public int getQuantity() { return quantity; }
	public String getShelfName() { return shelfName; }
	public long getProductCode() { return productCode; }
	public int getShelfId() { return shelfId; }

	public ProductShelfQuantity(String productName, int quantity, String shelfName, long productCode, int shelfId) {
		this.productName = productName;
		this.quantity = quantity;
		this.shelfName = shelfName;
		this.productCode = productCode;
		this.shelfId = shelfId;
	}
}