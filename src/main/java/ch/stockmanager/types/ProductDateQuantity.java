package ch.stockmanager.types;

@SuppressWarnings("unused")
public class ProductDateQuantity {
	public String name;
	public String date;
	public int quantity;

	public ProductDateQuantity() {}

	public ProductDateQuantity(String name, java.sql.Date date, int quantity) {
		this.name = name;
		this.date = date.toString();
		this.quantity = quantity;
	}

	public String getName() {
		return name;
	}

	public String getDate() {
		return date;
	}

	public int getQuantity() {
		return quantity;
	}
}
