
/**
 * Title:        GenericDatabaseConnector<p>
 * Description:  Class to manage a connection to a generic JDBC database - extend w/ db 
 specific calls.<p>
 * Copyright:	Copyright (c) 2001 Paul Bethe and Richie Bielak<p>
 * @author       Paul Bethe
 * @version 1.0
 */
package org.ephman.abra.database;

import java.sql.*;
//import org.ephman.database.DatabaseConnectorInterface;

public abstract class GenericDatabaseConnector  {

	protected String dbName;

	protected String hostName;

	protected int defaultPort;

	protected Connection conn;

	public GenericDatabaseConnector(String host) {
		conn = null;
		hostName = host;
	}


	protected String getUrl (String dbName) {
		return jdbc_url() + dbName;
	}
	/** Connect to the named database.
	 *
	 *  @param db name of the database to connect to
	 *  @param user user name with which to login into db server
	 *  @param pass password for the user
	 *  @param port port for db connection
	 *
	 *  @throws SQLException - when connection fails
	 */
	public void connect (String db,
						 String user,
						 String pass,
						 int port) throws SQLException {
		dbName = db;
		defaultPort = port;
		String url = getUrl (dbName);
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

	/** Connect to the named database using default port number.
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


	/** Create the first part of the URL needed for specifying
	 *  the database in JDBC db connection.
	 */
	public abstract String jdbc_url ();

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
