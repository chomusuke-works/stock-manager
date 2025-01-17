package types;

public class productDateQuantity {
	public String name;
	public String date;
	public int quantity;

	public productDateQuantity(String name, java.sql.Date date, int quantity) {
		this.name = name;
		this.date = date.toString();
		this.quantity = quantity;
	}
}
