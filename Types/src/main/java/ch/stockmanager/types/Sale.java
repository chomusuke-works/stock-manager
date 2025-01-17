package ch.stockmanager.types;

@SuppressWarnings("unused")
public class Sale {
	private String date;
	private long code;
	private int sold;
	private int thrown;

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

	public void setDate(String date) {
		this.date = date;
	}

	public long getCode() {
		return code;
	}

	public void setCode(long code) {
		this.code = code;
	}

	public int getSold() {
		return sold;
	}

	public void setSold(int sold) {
		this.sold = sold;
	}

	public int getThrown() {
		return thrown;
	}

	public void setThrown(int thrown) {
		this.thrown = thrown;
	}
}
