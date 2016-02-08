package dbtLab3;

import java.sql.*;
import java.util.*;

/**
 * Database is a class that specifies the interface to the movie database. Uses
 * JDBC and the MySQL Connector/J driver.
 */
public class Database {
	/**
	 * The database connection.
	 */
	private Connection conn;

	/**
	 * Create the database interface object. Connection to the database is
	 * performed later.
	 */
	public Database() {
		conn = null;
	}

	/**
	 * Open a connection to the database, using the specified user name and
	 * password.
	 * 
	 * @param userName
	 *            The user name.
	 * @param password
	 *            The user's password.
	 * @return true if the connection succeeded, false if the supplied user name
	 *         and password were not recognized. Returns false also if the JDBC
	 *         driver isn't found.
	 */
	public boolean openConnection(String userName, String password) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(
					"jdbc:mysql://puccini.cs.lth.se/" + userName, userName,
					password);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Close the connection to the database.
	 */
	public void closeConnection() {
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
		}
		conn = null;
	}

	/**
	 * Check if the connection to the database has been established
	 * 
	 * @return true if the connection has been established
	 */
	public boolean isConnected() {
		return conn != null;
	}
	public boolean isUser(String UID) throws SQLException{
		//Creates the statement needed to see if the user exists
		PreparedStatement prepStmt = null;
		try{
			String sql = "SELECT * FROM Users WHERE UserName =" + UID + ")";
			prepStmt = conn.prepareStatement(sql);
			//Checks if the result Set has 1 "next" or a first object, hence whether its empty or not
			return prepStmt.getResultSet().next();
		}catch(SQLException e){
			System.out.println("Det gick inte att kolla om användaren existerar:" + " "); 
			e.printStackTrace();
			return false;
		}
		//If it is not possible to "log in" or find the user one should always close the statement
		finally{
			try{
				prepStmt.close();
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
		
	}
	/**
	 * Gets the performances of the movie
	 * 
	 * @param string movieName
	 * @return list of performances
	 */
	public ArrayList <Map<String, String>> getPerformances(String movieName){
		ArrayList<Map<String, String>> performances = new ArrayList<Map<String, String>>();
		PreparedStatement ps = null;
		try {
		String sql = "SELECT * FROM performances WHERE performances.movieName =  ?";
		ps = conn.prepareStatement(sql);
		ps.setString(1, movieName);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			//performances.add(rs.getString("theaterName"), rs.getString("thedate"));	
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return performances;
	}
}
