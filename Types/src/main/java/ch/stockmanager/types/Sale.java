package ch.stockmanager.types;

import java.sql.Timestamp;

@SuppressWarnings("unused")
public class Sale {
	public Timestamp timestamp;
	public long code;
	public int sold;
	public int thrown;

	public Sale() {}

	public Sale(Timestamp timestamp, long code, int sold, int thrown) {
		this.timestamp = timestamp;
		this.code = code;
		this.sold = sold;
		this.thrown = thrown;
	}


	public Timestamp getTimestamp() {
		return timestamp;
	}

	public long getCode() {
		return code;
	}

	public int getSold() {
		return sold;
	}

	public int getThrown() {
		return thrown;
	}
}
