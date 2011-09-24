
/**
 * Title:        DatabaseConnectorInterface<p>
 * Description:  Describes the interface to an object that manages a connection to a JDBC database<p>
 * Copyright:	Copyright (c) 2001 Paul Bethe and Richie Bielak<p>
 * @author       Richie Bielak
 * @version 1.0
 */
package org.ephman.database;

import java.sql.*;

public interface DatabaseConnectorInterface {

       /** Connect to the named database as user with password */
	public void connect (String db,
  			     String user,
			     String pass) throws SQLException;


	/** Create the first part of the URL needed for specifying
	 *  the database in JDBC db connection.
	 */
	public String jdbc_url ();


	/** Disconnect from this database. If no connection is active
	 *  nothing is done.
	 */
	public void disconnect ();

	/**
	 * Get the value of conn.
	 * @return Value of conn.
	 */
	public Connection getConn();

	/**
	 * Get the value of dbName.
	 * @return Value of dbName.
	 */
	public String getDbName();

	/**
	 * Get the value of dbServerHost.
	 * @return Value of dbServerHost.
	 */
	public String getDbServerHost();

	/**
	 * Set the value of dbServerHost.
	 * @param v  Value to assign to dbServerHost.
	 */
	public void setDbServerHost(String  v);

	/**
	 * Get the value of dbServerPort.
	 * @return Value of dbServerPort.
	 */
	public int getDbServerPort();

	/**
	 * Set the value of dbServerPort.
	 * @param v  Value to assign to dbServerPort.
	 */
	public void setDbServerPort(int  v);



}
