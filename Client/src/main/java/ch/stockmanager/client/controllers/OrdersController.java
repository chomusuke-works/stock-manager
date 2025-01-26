package ch.stockmanager.client.controllers;

import javafx.beans.property.ReadOnlyListWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import ch.stockmanager.client.util.JavaFxHelper;
import ch.stockmanager.types.Order;

public class OrdersController extends Controller {
	private final ObservableList<Order> orders = FXCollections.observableArrayList();

	public OrdersController(String serverIp) {
		super(serverIp);
	}

	@Override
	public void update() {
		JavaFxHelper.ObservableListUpdaterTask
			.run(getUrl(), orders, Order.class);
	}

	@Override
	public String getPathPrefix() {
		return "api/products/orders";
	}

	public ObservableList<Order> getOrders() {
		return new ReadOnlyListWrapper<>(orders);
	}

	public void addOrder(Order order) {
		for (Order o : orders) {
			if (o.name.equals(order.name)) {
				o.quantity += order.quantity;

				return;
			}
		}

		// If no existing order for the product has been found
		orders.add(order);
	}
}
