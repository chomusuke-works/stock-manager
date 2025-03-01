package ch.stockmanager.server.types;

import java.sql.Timestamp;

@SuppressWarnings("unused")
public class Sale {
	public Timestamp timestamp;
	public long productCode;
	public int sold;
	public int thrown;

	public Sale() {}

	public Sale(Timestamp timestamp, long productCode, int sold, int thrown) {
		this.timestamp = timestamp;
		this.productCode = productCode;
		this.sold = sold;
		this.thrown = thrown;
	}


	public Timestamp getTimestamp() {
		return timestamp;
	}

	public long getProductCode() {
		return productCode;
	}

	public int getSold() {
		return sold;
	}

	public int getThrown() {
		return thrown;
	}
}
