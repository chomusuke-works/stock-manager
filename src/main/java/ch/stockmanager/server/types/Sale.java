package ch.stockmanager.server.types;

import java.sql.Timestamp;

@SuppressWarnings("unused")
public class Sale {
	public Timestamp timestamp;
	public long productCode;
	public String productName;
	public int sold;
	public int thrown;

	public Sale() {}

	public Sale(Timestamp timestamp, long productCode, String productName, int sold, int thrown) {
		this.timestamp = timestamp;
		this.productCode = productCode;
		this.productName = productName;
		this.sold = sold;
		this.thrown = thrown;
	}


	public Timestamp getTimestamp() {
		return timestamp;
	}

	public long getProductCode() {
		return productCode;
	}

	public String getProductName() {
		return productName;
	}

	public int getSold() {
		return sold;
	}

	public int getThrown() {
		return thrown;
	}
}
