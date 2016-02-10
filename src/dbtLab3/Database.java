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
	public boolean isUser(String UID){
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
	public Map <String, ArrayList<String>> getPerformances(String movieName){
		Map<String, ArrayList<String>> p = new HashMap <String, ArrayList<String>>();
		//ArrayList<Map<String, String>> performances = new ArrayList<Map<String, String>>();
		PreparedStatement ps = null;
		try {
		String sql = "SELECT * FROM performances WHERE performances.movieName =  ?";
		ps = conn.prepareStatement(sql);
		ps.setString(1, movieName);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			ArrayList<String> pset = new ArrayList<String>();
			pset.add(0,rs.getString("theaterName"));
			pset.add(1,rs.getString("nrSeats"));
			pset.add(2,rs.getString("thedate"));
			p.put(movieName, pset);
			
			//performances.add(rs.getString("theaterName"), rs.getString("thedate"));	
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return p;
	}
	
	/**
	 * A method that returns the number of remaining seats from a specific performance
	 * in order to show it in the GUI
	 */
	public int remainingSeats(String movieName, String date){
		String sql = "SELECT freeSeats FROM Performances WHERE thedate = ? AND movieName = ?";
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, date);
			ps.setString(2, movieName);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int remSeats = rs.getInt("freeSeats");
				return remSeats;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
		
	/**
	 * Tries to make a reservation for the movieName and the date:
	 * Deducts 1 from value freeSeats in Performances and
	 * 
	 */
	public boolean bookTicket(String movieName, String date, String UID){
		PreparedStatement psSeats = null;
		PreparedStatement psReserve = null;
		
		//SQL strings deduct 1 from value freeSeats and insert new reservation entry
		String deductSeat = "UPDATE Performances " + "SET freeSeats = (freaSeats - 1) " + "WHERE movieName = ? and theDate = ?";
		String makeReservation = "INSERT into Reservations(id, perdate, movieName, userName) values(id, date, movieName, UID);";
		
		if(isUser(UID) && (remainingSeats(movieName, date) > 0)){
			try {
				conn.setAutoCommit(false);
				
				psSeats = conn.prepareStatement(deductSeat);	
				psReserve = conn.prepareStatement(makeReservation);
				
				psSeats.setString(1, movieName);
				psSeats.setString(2, date);
				
				psSeats.executeUpdate();
				psReserve.executeUpdate();
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
			finally {
				try {
					if(psSeats != null) psSeats.close();
					if(psReserve != null) psReserve.close();
					conn.setAutoCommit(true);
				} catch(SQLException e2) {
					e2.printStackTrace();
				}
			}
		}
		return true;
	}
	
	
	
	/**
	 * gets the list from the database with all the movies
	 * @return ArrayList movies
	 */
	public ArrayList<String> getMovies(){
		ArrayList<String> movies = new ArrayList<String>();
		String sql = "SELECT * FROM movies";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while(rs.next()){
				movies.add(rs.getString("name"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return movies;	
	}
}
