package ch.stockmanager.client.util;

import java.util.List;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.*;


public class JavaFxHelper {
	public static <T> TableView<T> getTable(String[] columnNames, String[] propertyNames) {
		if (columnNames.length != propertyNames.length)
			throw new IllegalArgumentException("Must have an equal number of column and property names.");

		TableView<T> table = new TableView<>();

		for (int i = 0; i < columnNames.length; i++) {
			TableColumn<T, ?> column = new TableColumn<>(columnNames[i]);
			column.setCellValueFactory(new PropertyValueFactory<>(propertyNames[i]));
			table.getColumns().add(column);
		}

		return table;
	}

	public static class ObservableListUpdaterTask<T> extends Task<List<T>> {
		private final String url;
		private final ObservableList<T> destination;
		private final Class<T> dataType;

		public static <U> void run(String url, ObservableList<U> destination, Class<U> dataType) {
			Task<List<U>> task = new ObservableListUpdaterTask<>(url, destination, dataType);
			new Thread(task).start();
		}

		public ObservableListUpdaterTask(String url, ObservableList<T> destination, Class<T> dataType) {
			this.url = url;
			this.destination = destination;
			this.dataType = dataType;
		}

		@Override
		protected List<T> call() {
			return HTTPHelper.getList(url, dataType);
		}

		@Override
		protected void succeeded() {
			super.succeeded();

			try {
				List<T> result = get();

				Platform.runLater(() -> destination.setAll(result));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
}
