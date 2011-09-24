
/**
 * Title:		DatabaseSessionFactory <p>
 * Description:  Create Database Sessions<p>
 * Copyright:	Copyright (c) 2001 Paul Bethe and Richie Bielak<p>
 * @author 		 Chris Marty
 * @version 1.0
 */
package org.ephman.abra.database;

import java.sql.*;
import org.ephman.abra.database.DatabaseConnector;

/**
 * Manages creation of DatabaseSessions
 */
public class DatabaseSessionFactory {

	private static DatabaseSessionFactory theInstance;

	private String 	host,
			database,
			user,
			password;

	private int port;
	
	/**
	 * Create a new database session
  	 */
 	public synchronized DatabaseSession createNewSession ()
	throws SQLException
	{
		DatabaseConnector dbcon = new DatabaseConnector(host);
		dbcon.connect(database, user, password, port);
		Connection connection = dbcon.getConn();
		DatabaseSession dbSess = new JDBCDatabaseSession (connection);
		return dbSess;
	}

	/**
	 * Create the instance of the singleton object, should be called only once at initialization
	 * @param host host name for the database server
	 * @param database name of the database to which we are connecting
	 * @param user user id for database connection
	 * @param password password for database connection
	 */
	public synchronized static void init (String host, String database, String user, String password, int port)
	throws SQLException
	{
		if (theInstance == null) {
			theInstance = new DatabaseSessionFactory (host, database, user, password, port);
		} else {
			throw new AbraSQLException ("ephman.abra.database.badinit");
		}
	}

	/**
  	 * Get the instance of the factory,
     * Factory must be initalized first.
  	 */
	public synchronized static DatabaseSessionFactory getInstance ()
	throws SQLException
	{
		if (theInstance == null) {
			throw new AbraSQLException ("ephman.abra.database.noinit");
		}
		return theInstance;
	}

	/**
	 * Return true if the factory is initialized.
	 */
	public synchronized static boolean initialized () {
		return (theInstance != null);
	}

 	private DatabaseSessionFactory(String host, String database, String user, String password, int port)
	{
		this.host = host;
		this.database = database;
		this.user = user;
		this.password = password;
		this.port = port;
	}

}
