
/**
 * Title:        DatabaseConnectorOracle<p>
 * Description:  Class to manage a connection to an Oracle database.<p>
 * Copyright:	Copyright (c) 2001 Paul Bethe and Richie Bielak<p>
 * @author       Richie Bielak
 * @version 1.0
 */
package org.ephman.database;

import java.sql.*;

public class DatabaseConnectorOracle implements DatabaseConnectorInterface {

	protected String dbName;

	protected String hostName;

	protected int defaultPort = 1521;

	protected int port;

	protected Connection conn;

	public DatabaseConnectorOracle(String host) {
		conn = null;
		hostName = host;
		port = defaultPort;

		try {
			/* Load the oracle driver */
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
		}
		catch (SQLException e) {
			System.out.println("ERROR:* Unable to load Oracle driver");
			e.printStackTrace ();
		}
	}

	/** Connect to the named database.
	 *
	 *  @param db name of the database to connect to
	 *  @param user user name with which to login into db server
	 *  @param pass password for the user
	 *
	 *  @throws SQLException - when connection fails
	 */
	public void connect (String db,
						 String user,
						 String pass) throws SQLException {
		connect (db, user, pass, defaultPort);
	}

	/** Connect to the named database.
	 *
	 *  @param db name of the database to connect to
	 *  @param user user name with which to login into db server
	 *  @param pass password for the user
	 *  @param port database port to use
	 *
	 *  @throws SQLException - when connection fails
	 */
	public void connect (String db,
						 String user,
						 String pass, 
						 int port) throws SQLException {

		this.port = port;
		dbName = db;
		String url = jdbc_url() + dbName;
		/* Load the appropriate driver */
		try {
			conn = DriverManager.getConnection (url, user, pass);
		}
		catch (SQLException e) {
			System.out.println ("Unable to connect to: " + url + " - " + e.getMessage());
			e.printStackTrace();
			throw e;
		}
	}


	/** Create the first part of the URL needed for specifying
	 *  the database in JDBC db connection.
	 */
	public String jdbc_url () {
		/* This is a pure Java version of JDBC. Should try to get the oci version working */
		// String result = "jdbc:oracle:oci8:@";
		String result = "jdbc:oracle:thin:@" + hostName +":" + port + ":";
               return (result);
	};


	/** Disconnect from this database. If no connection is active
	 *  nothing is done.
	 */
	public void disconnect () {
		if (conn != null) {
			try {
				conn.close();
			}
			catch (SQLException ignored) {};
                  dbName = null;
		}

	};

	/**
	 * Get the value of conn.
	 * @return Value of conn.
	 */
	public Connection getConn() {return (conn);};

	/**
	 * Get the value of dbName.
	 * @return Value of dbName.
	 */
	public String getDbName() {return (dbName);};

	/**
	 * Get the value of dbServerHost.
	 * @return Value of dbServerHost.
	 */
	public String getDbServerHost() {return (hostName);};

	/**
	 * Set the value of dbServerHost.
	 * @param v  Value to assign to dbServerHost.
	 */
	public void setDbServerHost(String  v) {hostName = v;};

	/**
	 * Get the value of dbServerPort.
	 * @return Value of dbServerPort.
	 */
	public int getDbServerPort() {return (defaultPort);};

	/**
	 * Set the value of dbServerPort.
	 * @param v  Value to assign to dbServerPort.
	 */
	public void setDbServerPort(int  v) {};



}
