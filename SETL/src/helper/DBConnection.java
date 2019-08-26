package helper;

import java.sql.Connection;
import java.sql.DriverManager;

import javax.swing.JOptionPane;

public class DBConnection {
	Connection connection = null;
	
	private Connection createConnection(String dbName, String username, String password) {
		try {
			Class.forName("org.postgresql.Driver");
			String urlName = "jdbc:postgresql://localhost:5432/" + dbName;
			connection = DriverManager.getConnection(urlName, username, password);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "DB connection not established");
		}
		return connection;
	}

	public Connection getConnection(String dbName, String username, String password) {
		if (connection == null) {
			return createConnection(dbName, username, password);
		} else {
			return connection;
		}
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}
}
