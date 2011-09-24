package org.ephman.junit;

import junit.framework.TestCase;
import org.ephman.abra.database.*;
import org.ephman.abra.tools.*;
import java.sql.*;
import java.util.*;
import java.io.*;
/**
 * a test case class for all the tests which need a db connection
 * gets args from -D on invoke line..
*/

public  class DatabaseTest extends TestCase {

	public DatabaseTest (String name) {
		super (name);
	}

	// Database session for use in tests that extend this one
	public DatabaseSession dbSess;

	// marshaller for books and such..
	protected Marshaller marshaller;
	/**
	 * Get database session needed for following test.
	 */
	protected void setUp () throws Exception {
	    System.out.println ("--> Setup test:" + this.getClass().getName());
	    
		String host = null;
		String database = null;
		Properties props = null;
		try {
			String dbProps = System.getProperty ("db.props");
			if (dbProps!= null) {
				props = new Properties ();
				props.load (new FileInputStream (dbProps));
			} else
				props = System.getProperties ();
			host = props.getProperty ("host");
			database = props.getProperty ("database");
			String user = props.getProperty ("user");
			String password = props.getProperty ("password");
			int port = Integer.parseInt (props.getProperty ("port"));

			if (!DatabaseSessionFactory.initialized ()) {
				System.out.println ("Initializing: host: " + host);
				DatabaseSessionFactory.init (host, database, user, password, port);
			}
			dbSess = DatabaseSessionFactory.getInstance ().createNewSession ();

			xmlDirectory = System.getProperty ("xmldir");
			marshaller = new Marshaller ();
			for (int i=0; i<xmlFiles.length; i++)
				marshaller.addMapFile (xmlDirectory + "/" + xmlFiles[i], false);
			marshaller.setUnmarshalDateFormats (new String[]{"yyyyMMdd"});
			marshaller.setMarshalDateFormat ("EEE MMM dd, yyyy");
			//			marshaller.useAbraXmlParser ();
		}
		catch (Exception e) {
			System.out.println ("***Error connecting to db: " + database + " on " + host);
			throw e;
		}
		//		if (this.getClass () == DatabaseTest.class)
		//	checkConnection ();
	}

	protected String xmlDirectory = "";
	protected String [] xmlFiles = new String []{"books.xml", "test.xml"};


	protected Connection getConnection () {
		return ((JDBCDatabaseSession)dbSess).getJdbcConnection ();
	}

	// for cleanup routines and the like..
	protected void execute (String sql) throws Throwable { 
	    try {
	        dbSess.startTransaction ();
	        java.sql.Statement stmt = getConnection().createStatement ();
	        stmt.execute (sql);
	        dbSess.commitTransaction ();
	    } catch (Throwable t) {
	        dbSess.rollbackTransaction();
	        throw t;
	    }
	}

	public void checkConnection ()  {
		// Database connection test
		assertTrue ("connection establised", dbSess != null);
		System.out.println ("Database Connection succeeded");
	}


	public void faketestThrows () throws SQLException {
		dbSess.startTransaction ();
		System.out.println ("waiting 10s (time to disconnect)");

		System.out.println ("preparing call");
		CallableStatement stmt = 
			getConnection().prepareCall ("{ ? = call FUNC_INS_foo"
										 + " (?,?) }");
		System.out.println ("OK - setting values");
		stmt.setDouble (2, 42.69);
		stmt.setString (3, "store_foo");
		stmt.registerOutParameter (1, Types.INTEGER);
		System.out.println ("OK - executing call");
		stmt.executeUpdate ();
		System.out.println ("OK - rolling back call");
	}

	/* FUNC_INS_foo (
	   P_value double precision,
	   P_snec_type varchar,
	   )
	*/

	/** 
	 * Release the database session
	 */
	protected void tearDown () throws Exception {
		dbSess.disconnect();
	}

}
