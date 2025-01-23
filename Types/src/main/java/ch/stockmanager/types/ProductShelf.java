package ch.stockmanager.types;

@SuppressWarnings("unused")
public class ProductShelf {
	public long productCode;
	public String productName;
	public int shelfId;
	public String shelfName;
	public boolean isStock;

	public ProductShelf() {}

	public ProductShelf(long productCode, String productName, int shelfId, String shelfName, boolean isStock) {
		this.productCode = productCode;
		this.productName = productName;
		this.shelfId = shelfId;
		this.shelfName = shelfName;
		this.isStock = isStock;
	}

	public long getProductCode() { return productCode; }
	public String getProductName() { return productName; }
	public int getShelfId() { return shelfId; }
	public String getShelfName() { return shelfName; }
	public boolean getIsStock() { return isStock; }


}