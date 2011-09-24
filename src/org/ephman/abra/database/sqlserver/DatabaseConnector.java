
/**
 * Title:        DatabaseConnector<p>
 * Description:  Class to manage a connection to an SQL Server database.<p>
 * Copyright:	Copyright (c) 2005 Paul Bethe and Richie Bielak<p>
 * @author       Richie Bielak
 * @version 1.0
 */
package org.ephman.abra.database;

import java.sql.*;
import org.ephman.database.DatabaseConnectorInterface;


public class DatabaseConnector extends GenericDatabaseConnector {

	/**
	 *  Create a connector object that will let us connect to
	 *  database server on the named host.
	 *  
	 *  @param host name of the host on which the database server runs
	 */
	public DatabaseConnector(String host) {
		super (host);
		defaultPort = 1433;

		try {
			DriverManager.registerDriver(new com.microsoft.jdbc.sqlserver.SQLServerDriver());
		}
		catch (SQLException e) {
			System.out.println("ERROR:* Unable to load SQL Server driver");
			e.printStackTrace ();
		}
	}


	/** Create the first part of the URL needed for specifying
	 *  the database in JDBC db connection.
	 */
	public String jdbc_url () {
		/* This is a pure Java version of JDBC. Should try to get the oci version working */
		String result = "jdbc:microsoft:sqlserver://" +
			hostName +":" + defaultPort;
		return (result);
	};

}
