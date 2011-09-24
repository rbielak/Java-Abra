package org.ephman.junit;

import junit.framework.TestCase;
import org.ephman.abra.database.*;
import org.ephman.junit.generated.*;
import org.ephman.abra.tools.FactoryGenerator;
import java.sql.*;

/** a test to try timing in stored procs and dyn sql for dbs..
 * @author Paul Bethe
 */

public  class TestTiming extends DatabaseTest {

	public TestTiming (String name) {
		super (name);
	}


	protected void setUp () throws Exception {
	    System.out.println ("Setup TestTiming");
		super.setUp ();
	}

	public void testTiming () throws Throwable {
		//		createBadBook ();
		cleanAuthors ();
		doTiming (AuthorFactory.getInstance (), false);
		if (FactoryGenerator.DB_NAME.equals ("MySQL")) {
			System.out.println ("MySql does not support stored procs");
		}
		else if (FactoryGenerator.DB_NAME.equals("Sybase")) {
		    System.out.println ("Sybase procs not supported");
		}
		else if (FactoryGenerator.DB_NAME.equals("SQLServer")) {
		    System.out.println ("SQLServer procs not supported");
		}
		else
			doTiming (AuthorProcsFactory.getInstance (), true);
	}

	void createBadBook () throws Throwable {
		dbSess.startTransaction ();
		AuthorFactory.getInstance ().delete (dbSess, new Author () );
		dbSess.commitTransaction ();
	}
	// time w/ rpocs and w/o procs
	void doTiming (AbstractAuthorFactory aFact, boolean isProcs) throws Throwable {
		long start = System.currentTimeMillis ();
		for (int i =0; i < RUNS; i++) {
			dbSess.startTransaction ();
			Author a = createAuthor (i, isProcs);
			aFact.store (dbSess, a);
			dbSess.commitTransaction ();
		}
		long end = System.currentTimeMillis ();
		double avgTime = (end-start) / RUNS;
		System.out.println ("Succesful storage " + (isProcs ? "w/ stored procs" : "dyn SQL"));
		System.out.println ("\taverage time: " + avgTime + "(ms) [" + (end-start) 
							+ " for " + RUNS + " stores]");							
	}

	Author createAuthor (int num, boolean isProcs) {
		Author result = new Author ();
		result.setLastName (TIMING + num + (isProcs?"p":""));
		result.setFirstName ("Bob");
		return result;
	}

	void cleanAuthors () throws Throwable {
		super.execute ("delete from db_author where " + 
					   AuthorFactory.getInstance ().lastName + " like '" + TIMING + "%'");

	}
	
	static final int RUNS = 100;
	static final String TIMING = "timing";

	// disconnect from db..
	protected void tearDown () throws Exception {
		super.tearDown ();
	}

}
