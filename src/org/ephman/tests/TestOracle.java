package org.ephman.tests;

/** silly test for oracle structs.. */

import java.sql.*;
import java.util.*;
import java.io.*;

import junit.framework.*;
import org.ephman.abra.database.*;
import org.ephman.junit.DatabaseTest;

//oracle
import oracle.sql.*;
import oracle.jdbc.driver.*;

public class TestOracle extends DatabaseTest { 

	/**
	 * Run all the SN tests
	 */
	public static void main (String[] argv) {
		junit.textui.TestRunner.run (makeTestSuite ());
		System.out.println ("Tests finished...");
	}
	
	/**
	 * Create a suite of tests to test SN code.
	 */
	protected static TestSuite makeTestSuite () {
		TestSuite suite = new TestSuite ("TestOracle");
		// Add new tests here
		suite.addTest (new TestOracle ());
		return suite;
	}
	
	
	
	TestOracle () {
		super ("TestOracle");
	}
	protected void setUp() throws Exception{
		System.out.println ("opening " + 
							System.getProperty ("abra.home") + "/conf/db.props");
		System.setProperty ("db.props",
							System.getProperty ("abra.home") + "/src/org/ephman/junit/db.props.oracle");
		System.setProperty ("xmldir", System.getProperty ("abra.home") + "/src/org/ephman/junit");
		super.setUp ();
	}
	

	protected void runTest () throws Exception {
		oracleTest ();
		//rawTest ();
		//jdbcTest ();
		//blobTest ();
	}
	
	// for specific oracle calls..
	protected void oracleTest () throws Exception {

		dbSess.startTransaction ();
		// test code	
		Connection conn = ((JDBCDatabaseSession)dbSess).getJdbcConnection ();
		OracleConnection oraconn = (OracleConnection)conn;
		
		oraconn.getTypeMap().put ("O_BUILDING", Building.class);
		oraconn.getTypeMap().put ("O_MOVIE", Movie.class);
		
		String [] occupants = new String [] {"Larry", "Curly"};
		Building emp = new Building ("Empire State Building", 
									 null,
									 new Movie[] {new Movie ("King Kong")});
		emp.shifts = new int [] {1,2,3};
		ByteArrayOutputStream bostream = new ByteArrayOutputStream ();
		ObjectOutputStream ostream = new ObjectOutputStream (bostream);
		ostream.writeObject (emp);
		//		emp.data = make_bytes (30000);
		
		OracleCallableStatement stmt = (OracleCallableStatement)oraconn.prepareCall 
			("{ call storeBuilding (?, ?) }");
		System.out.println ("Binding");
		stmt.setObject (1, emp, OracleTypes.STRUCT);
		stmt.registerOutParameter (2, OracleTypes.INTEGER);
		stmt.executeUpdate ();
		int oid = stmt.getInt (2);
		stmt.close ();
		dbSess.commitTransaction ();
		
		System.out.println ("Storage complete\n");
		//dbSess.startTransaction ();
		stmt = (OracleCallableStatement)oraconn.prepareCall 
			("{ call getBuilding (?, ?) }");
		stmt.setInt (1, oid);
		stmt.registerOutParameter (2, Types.STRUCT, "O_BUILDING");
		stmt.executeUpdate ();
		Building b = (Building)stmt.getObject (2); //, OracleTypes.STRUCT, "O_BUILDING");
		System.out.println ("Found '" + b.name + "'");
		if (b.occupants != null && b.occupants.length >= 2)
			System.out.println ("Occupants: " + b.occupants[0] + ", " + b.occupants[1]);
		System.out.println ("Movie: " + b.movies[0].name);	
		System.out.println ("Testing byte []");
		//	assert ("same array lengths", b.data.length == emp.data.length);
		assert ("same array shifts", b.shifts.length == emp.shifts.length);
		//		for (int i = 0; i < b.data.length; i++) 
		//			assert ("byte " +i + " same", b.data[i] == emp.data[i]);
			
		/*ByteArrayInputStream bistream = new ByteArrayInputStream (b.data);
		  ObjectInputStream istream = new ObjectInputStream (bistream);
		  b = (Building)istream.readObject ();		
		  System.out.println ("Found de-serialized '" + b.name + "'");
		  System.out.println ("Occupants: " + b.occupants[0] + ", " + b.occupants[1]);
		  System.out.println ("Movie: " + b.movies[0].name);	*/

		// try cursor call
		stmt = (OracleCallableStatement)oraconn.prepareCall 
			("{ ? = call getBuildings }");
		stmt.registerOutParameter (1, OracleTypes.CURSOR);
		stmt.execute ();
		ResultSet rs = (ResultSet)stmt.getObject (1);
		System.out.println ("RS found");
		while (rs.next ())
			System.out.println (rs.getObject (1) + ", "+ rs.getObject (2) + 
							  ", " + rs.getObject (3));
		System.out.println ("\n");
		rs.close ();
		dbSess.disconnect ();
	}

	protected void blobTest () throws Exception {
		DatabaseSession dbSess = DatabaseSessionFactory.getInstance ().createNewSession ();
		dbSess.startTransaction ();
		OracleConnection conn = (OracleConnection)
			((JDBCDatabaseSession)dbSess).getJdbcConnection ();
		byte [] testArray = make_bytes (5000);
		Foo foo = new Foo ("theo1");
		foo.data = testArray;
		BlobManager.initialize (conn);
		dbSess.commitTransaction ();
		dbSess.startTransaction ();
		CallableStatement stmt = conn.prepareCall ("{ call pkg_foo.proc_store_foo (?) }");
		stmt.setObject (1, foo);
		stmt.executeUpdate ();
		stmt.close ();
		dbSess.commitTransaction ();
		//	dbSess.close ();
	}
	
	byte [] make_bytes (int size) {
		byte [] result = new byte[size];
		for (int i=0; i < size; i++) 
			result[i] = (byte)(size & 0xff);

		return result;
	}

	protected void rawTest () throws Exception {
		DatabaseSession dbSess = DatabaseSessionFactory.getInstance ().createNewSession ();
		dbSess.startTransaction ();
		byte [] testArray = make_bytes (10000); //new byte []{0xA, 0x8};
		//printBytes (testArray);
		// test code	
		Connection conn = ((JDBCDatabaseSession)dbSess).getJdbcConnection ();
		CallableStatement stmt = conn.prepareCall 
			("{ call storeRaw (?) }");
		stmt.setBytes (1, testArray);
		stmt.executeUpdate ();
		System.out.println ("Retrieving");
		stmt = conn.prepareCall 
			("{ call getRaw (?) }");
		stmt.registerOutParameter (1, OracleTypes.RAW);
		stmt.executeUpdate ();
		
		byte [] rtn_val;
		rtn_val = stmt.getBytes (1);
		//printBytes (rtn_val);
		dbSess.commitTransaction ();
	}
	
	void printBytes (byte [] rtn_val) {
		if (rtn_val.length > 0)
			System.out.print ("(" + rtn_val[0]);
		for (int i = 1; i < rtn_val.length; i++)
			System.out.print (", " + rtn_val[i]);
		System.out.println (")");
	}
	
	protected void jdbcTest () throws Exception {
		DatabaseSession dbSess = DatabaseSessionFactory.getInstance ().createNewSession ();
		Connection conn = ((JDBCDatabaseSession)dbSess).getJdbcConnection ();
		dbSess.startTransaction ();
		Map map = conn.getTypeMap ();
		map.put ("O_PERSON", Class.forName ("org.ephman.tests.Person"));
		map.put ("O_MOVIE", Class.forName ("org.ephman.tests.Movie"));
		// test code
		
		
		System.out.println ("Storing");
		//		Person fedor = new Person ("Fedor", 5, 1, null);
		Movie jaws = new Movie ("STAR WARS");
		Person p = new Person ("Jason", 27, 42, jaws);
		CallableStatement stmt = conn.prepareCall ("{ ? = call storePerson (?) }");
		System.out.println ("Binding");
		stmt.setObject (2, p);
		stmt.registerOutParameter (1, Types.INTEGER);
		stmt.executeUpdate (); 
		p.oid = stmt.getInt (1);
		System.out.println ("Stored");
		stmt.close ();
		//
		dbSess.commitTransaction ();
		
		// now retrieve
		
		dbSess.startTransaction ();
		
		stmt = conn.prepareCall ("{ ? = call getPerson (?) }");
		stmt.setInt (2, 1);
		stmt.registerOutParameter(1, Types.STRUCT, "O_PERSON"); 
		stmt.execute ();
		
		Person query = (Person)stmt.getObject (1);
		System.out.println ("Person name='" + query.name + "'" +
							"\nMovie name='" + query.favoriteMovie.name + "'");
		dbSess.commitTransaction ();
		//dbSess.disconnect ();
		
	}	 
	
}
