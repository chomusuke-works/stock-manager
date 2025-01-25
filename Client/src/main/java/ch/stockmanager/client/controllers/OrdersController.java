package ch.stockmanager.client.controllers;

import ch.stockmanager.client.util.HTTPHelper;
import ch.stockmanager.types.Order;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class OrdersController extends Controller {
	private final ObservableList<Order> orders = FXCollections.observableArrayList();

	public OrdersController(String serverIp) {
		super(serverIp);
	}

	@Override
	public void update() {
		new Thread(() -> {
			List<Order> orders = HTTPHelper.getList(getUrl(), Order.class);

			this.orders.setAll(orders);
		}).start();
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
