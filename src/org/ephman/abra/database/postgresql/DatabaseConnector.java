
/**
 * Title:        DatabaseConnectorPostgres<p>
 * Description:  Class to manage a connection to a PostgreSQL database.<p>
 * Copyright:	Copyright (c) 2001 Paul Bethe and Richie Bielak<p>
 * @author       Paul Bethe
 * @version 1.0
 */
package org.ephman.abra.database;

import java.sql.*;
import org.ephman.database.DatabaseConnectorInterface;

public class DatabaseConnector extends GenericDatabaseConnector {

	public DatabaseConnector(String host) {
		super (host);
		defaultPort = 5432;

		try {
			/* Load the oracle driver */
			DriverManager.registerDriver(new org.postgresql.Driver());
		}
		catch (SQLException e) {
			System.out.println("ERROR:* Unable to load PostgreSQL driver");
			e.printStackTrace ();
		}
	}

	/** Create the first part of the URL needed for specifying
	 *  the database in JDBC db connection.
	 */
	public String jdbc_url () {
		String result = "jdbc:postgresql://" + hostName +":" + defaultPort + "/";
		return (result);
	}


}
