/*
 * DatabaseConnector.java - 2000/5/24
 */
   
package org.ephman.abra.database;

import java.sql.*;

/**
 * DatabaseConnector - objects of this type represent a JDBC
 *   connection to a MySQL server 
 *
 * @version: 0.1 2000/5/24
 * @author: Richie Bielak
 */

public class DatabaseConnector extends GenericDatabaseConnector {

	/** Create a connector, by setting attributes and loading
	 *  the correct JDBC driver.
	 *
	 * @param host host where the database server is running
	 * @param port port for communication with the db server
	 */
	public DatabaseConnector (String host) {
		super (host);
		defaultPort=3306;
		/* Load the driver for JDBC interface */
		try {
			// DriverManager.registerDriver((Driver)Class.forName ("org.gjt.mm.mysql.Driver").newInstance());
			//DriverManager.registerDriver((Driver)Class.forName ("com.mysql.jdbc.Driver").newInstance());
		    /* 2/14/05 Let's explicitly register this driver so that bad JDBC_JAR pointers 
		     * will be caught at compile time */
		    DriverManager.registerDriver(new com.mysql.jdbc.Driver ());
		}
		catch (Exception e) {
			System.out.println ("DatabaseConnector: Unable to load MySQL driver. ");
			e.printStackTrace ();
		}
	}

	/** Create the first part of the URL needed for specifying
	 *  the database in JDBC db connection. This one is specific
	 *  to MySQL.
	 */
	public String jdbc_url () {
		String url = "jdbc:mysql://" + hostName;
		url = url + ":" + defaultPort + "/";
		return (url);
	}

}
