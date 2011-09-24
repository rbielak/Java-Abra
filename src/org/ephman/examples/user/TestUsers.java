package org.ephman.examples.user;

import org.ephman.junit.DatabaseTest;
import junit.framework.*;
import org.ephman.abra.database.*;
import org.ephman.examples.user.generated.*;
import java.sql.*;

/** a test to try timing in stored procs and dyn sql for dbs..
 * @author Paul Bethe
 */

public class TestUsers extends DatabaseTest {

	public TestUsers (String name) {
		super (name);
	}

	public static void main (String [] argv) {
		junit.textui.TestRunner.run (suite());
	}

	public static TestSuite suite () {
		TestSuite result = new TestSuite ();
		result.addTestSuite (TestUsers.class);
		return result;
	}


	public void testStoring () throws Throwable {
		System.out.println ("Storing...???");
		User lazar = new User ();
		lazar.setUserId ("LZ");
		lazar.setUserName ("Lazar");
		dbSess.startTransaction ();
		UserFactory.getInstance ().store (dbSess, lazar);
		dbSess.commitTransaction ();
		System.out.println ("Done");
		
	}

}
