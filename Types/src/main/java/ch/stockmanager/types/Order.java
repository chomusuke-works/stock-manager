package ch.stockmanager.types;

import java.util.Objects;

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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Order that = (Order) o;
		return this.quantity == that.quantity && Objects.equals(this.name, that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, quantity);
	}
}
