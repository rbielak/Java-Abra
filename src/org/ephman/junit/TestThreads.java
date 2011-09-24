package org.ephman.junit;

import junit.framework.TestCase;
import org.ephman.abra.database.*;
import org.ephman.junit.generated.*;
import java.sql.*;
import java.io.*;
import java.util.*;

/** a test to try some threaded operations (versioning)
 * @author Paul Bethe
 * @version 3/6/02
 */

public  class TestThreads extends DatabaseTest {



	public TestThreads (String name) {
		super (name);
		aFact = AuthorFactory.getInstance ();
	}

	AuthorFactory aFact;

	public void testThreading () throws Throwable {
	    
		DatabaseSession thread2 = DatabaseSessionFactory.getInstance().createNewSession ();

		
		dbSess.startTransaction ();
		createAuthor (dbSess, aFact);
		System.out.println ("Created Author Thread-1");
		thread2.startTransaction ();
		System.out.println ("Trying Author Thread-2");
		AddUser au = new AddUser (thread2);
		Thread t = new Thread(au);
		t.start ();
		Thread.sleep (500);
		if (!au.success) {
			dbSess.commitTransaction ();
			System.out.println ("Commit done for thread-1");
			Thread.sleep (1000);
			assertTrue ("caught exception trying to store second one..",
						au.e != null);
		}
		else 
			assertTrue ("second insert should hang", false);
		assertTrue ("db lock works for insert", !au.success);
		// thread2.commitTransaction ();	
		thread2.disconnect ();
	}

	protected static void createAuthor (DatabaseSession ds, AuthorFactory fact) throws SQLException {
		Author a = new Author ();
		a.setLastName (TestThreads.A_NAME);
		a.setFirstName ("Bill");
		System.out.print ("Trying to store....");
		fact.store (ds, a);		
		System.out.println ("Success");
	}
	
	protected void deleteAuthor () throws Exception {
		dbSess.startTransaction (); 
		Author tan = aFact.getByLastName (dbSess, A_NAME);
		if (tan != null) 			
			aFact.delete (dbSess, tan);		
		dbSess.commitTransaction ();
	}

	protected void setUp () throws Exception {
		super.setUp ();		
		System.out.println("--- tt setup");
		deleteAuthor ();
		System.out.println("--- tt DONE");		
	}

	protected void tearDown () throws Exception {
		super.tearDown ();
	}

	public final static String A_NAME = "Tannenbaum";

}

class AddUser implements Runnable {
	public void run () {
		try {
			TestThreads.createAuthor (dbSess, aFact);
			success = true;
		} catch (SQLException se) {
			e = se;
		}
	}

	public Exception e = null;
	public boolean success = false;

	DatabaseSession dbSess;
	AuthorFactory aFact;

	public AddUser (DatabaseSession db) {
		this.dbSess = db;
		aFact = AuthorFactory.getInstance ();
	}

}
