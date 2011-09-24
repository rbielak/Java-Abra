
/**
 * Title:        DatabaseConnectorOracleXA<p>
 * Description:  Class to manage a connection to an Oracle database using XA.<p>
 * Copyright:	Copyright (c) 2001 Paul Bethe and Richie Bielak<p>
 * @author       Paul Bethe
 * @version 1.0
 */
package org.ephman.database;

import java.sql.*;
import javax.sql.*;
import oracle.jdbc.xa.client.*;
import javax.transaction.xa.*;
import javax.transaction.*;


public class DatabaseConnectorOracleXA extends DatabaseConnectorOracle {

	public DatabaseConnectorOracleXA (String host) {
		super (host);
		xaConn = null;
	}

	XAConnection xaConn;

	public XAConnection getXAConnection () { return xaConn; }

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
			OracleXADataSource oxds = new OracleXADataSource();
			oxds.setURL(url);
			oxds.setUser (user);
			oxds.setPassword (pass);
			xaConn = oxds.getXAConnection ();
			conn = xaConn.getConnection ();
		}
		catch (SQLException e) {
			System.out.println ("Unable to connect to: " + url + " - " + e.getMessage());
			e.printStackTrace();
			throw e;
		}
	}




}
