import java.sql.*;

public class Server {
	private static final String HOST = "localhost";
	private static final int PORT = 5666;
	private static final String DATABASE = "bdr";

	private static final String USER = "postgres";
	private static final String PASSWORD = "trustno1";

	public static void main(String[] args) throws SQLException {
		Connection conn = DriverManager.getConnection(
			String.format("jdbc:postgresql://%s:%d/%s",
				HOST,
				PORT,
				DATABASE),
			USER,
			PASSWORD
		);

		PreparedStatement st = conn.prepareStatement("select * from personne");
		ResultSet rs = st.executeQuery();
		while (rs.next()) {
			System.out.print("Column 1 returned ");
			System.out.println(rs.getString(3));
		}

		rs.close();
		st.close();
		conn.close();
	}
}
