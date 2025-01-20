package ch.stockmanager.types;

public class Order {
	public String name;
	public int quantity;

	public Order() {
	}

	public Order(String name, int quantity) {
		this.name = name;
		this.quantity = quantity;
	}
}
