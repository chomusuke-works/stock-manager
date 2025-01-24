package ch.stockmanager.types;

import java.util.Objects;

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

	@Override
	public int hashCode() {
		return Objects.hash(productCode, shelfId);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj.getClass() != getClass()) return false;
		if (this == obj) return true;

		// The product code and shelf code are the primary keys in the database model
		ProductShelf other = (ProductShelf) obj;
		return this.productCode == other.productCode && this.shelfId == other.shelfId;
	}
}