
/**
 * Title:        JDBCDatabaseSessionPool <p>
 * Description:  manage a pool of JDBC database sessions<p>
 * Copyright:	Copyright (c) 2001 Paul Bethe and Richie Bielak<p>
 * @author 		 Richie Bielak
 * @version 1.0
 */
package org.ephman.abra.database;

import java.sql.*;

import org.ephman.abra.database.DatabaseConnector;

/**
 * Manage a pool of JDBCDatabaseSessions
 */
public class JDBCDatabaseSessionPool implements DatabaseSessionPool {

	private static JDBCDatabaseSessionPool theInstance;

	// Stupid implementation with just one connection for now.
	private JDBCDatabaseSession pooledConn;

	/**
	 * Create a pool of JDBCSessions.
	 *
	 * @param host host name for the database server
	 * @param database name of the database to which we are connecting
	 * @param user user id for database connection
	 * @param password password for database connection
	 * @param count maximum number of database connections in this pool
	 */
	public JDBCDatabaseSessionPool(String host, String database, String user, String password, int count)
			throws SQLException
	{
		DatabaseConnector dbcon = new DatabaseConnector(host);
		dbcon.connect(database, user, password);
		Connection connection = dbcon.getConn();
		pooledConn = new JDBCDatabaseSession (connection);
	}

	/**
	 * Retrieve the session pool instance.
	 */
	public static JDBCDatabaseSessionPool getInstance (String host, String database, String user, String password, int count)
			throws SQLException {
		if (theInstance == null) {
			theInstance = new JDBCDatabaseSessionPool (host, database, user, password, count);
		}
		return theInstance;
	}

	public static JDBCDatabaseSessionPool getInstance () throws SQLException {
		if (theInstance == null)
			throw new AbraSQLException ("ephman.abra.database.nopool");
		return theInstance;
	}


	/** Get an available Database session */
	public synchronized DatabaseSession get() {
		JDBCDatabaseSession result = pooledConn;
		pooledConn = null;
		return result;
	}

	/**
	 * Return a session to the pool
	 *
	 * @param session session returned to the pool
	 */
	public synchronized void put (DatabaseSession session) throws Exception {
		if (pooledConn != null) {
			throw new Exception ("JDBC database session pool is full");
		}
		pooledConn = (JDBCDatabaseSession)session;
	}

}
