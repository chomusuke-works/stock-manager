package ch.stockmanager.client.util;

import javafx.scene.control.Alert;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.*;


public class JavaFxHelper {
    public static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static <T> TableView<T> createTable(String[] columnNames, Class<?>[] columnTypes, String[] propertyNames) {
        TableView<T> table = new TableView<>();

        for (int i = 0; i < columnNames.length; i++) {
            TableColumn<T, ?> column = new TableColumn<>(columnNames[i]);
            column.setCellValueFactory(new PropertyValueFactory<>(propertyNames[i]));
            table.getColumns().add(column);
        }

        return table;
    }
}
