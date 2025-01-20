package ch.stockmanager.types;

@SuppressWarnings("unused")
public class Order {
	public String name;
	public int quantity;

	public Order() {
	}

	public Order(String name, int quantity) {
		this.name = name;
		this.quantity = quantity;
	}

	public String getName() {
		return name;
	}

	public int getQuantity() {
		return quantity;
	}
}
