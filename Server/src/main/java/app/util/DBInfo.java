package app.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public record DBInfo(String URL, String username, String password) {
	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(URL, username, password);
	}
}
