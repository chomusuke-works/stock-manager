package ch.stockmanager.types;

@SuppressWarnings("unused")
public class Sale {
	public String date;
	public long code;
	public int sold;
	public int thrown;

	public Sale() {}

	public Sale(String date, long code, int sold, int thrown) {
		this.date = date;
		this.code = code;
		this.sold = sold;
		this.thrown = thrown;
	}


	public String getDate() {
		return date;
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
