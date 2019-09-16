package model;

import java.sql.Connection;
import java.sql.DriverManager;

import javax.swing.JOptionPane;

public class DatabaseConnection {
	
	Connection connection = null;
	
	private Connection createConnection(String url, String username, String password) {
		
		Connection con = null;
		try {
			Class.forName("org.postgresql.Driver");
			//con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/employee", "postgres", "13701005");
			url ="jdbc:postgresql://localhost:5432/"+url;
			System.out.println(url+" "+username +" "+password);
			con = DriverManager.getConnection(url, username, password);
			return con;
			
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "DB connection not established");
		}
		return con;		
	}

	public  Connection getConnection(String url, String username, String password)
	{
		if(connection!=null)
			return connection;
		else{
			
			connection = createConnection(url, username, password);
			return connection;
		}
	}
	
	public static void main (String [] args)
	{
		DatabaseConnection con = new DatabaseConnection ();
		con.getConnection("Payment", "postgres",  "postgres");
		
	}
	
}
