package org.ephman.junit;

import junit.framework.*;

/**
 * Test suite for Abra library. Requires JUnit package (see <a href="http://www.junit.org">JUnit</a>
 *  web site).
 *
 * To run the unit tests follow these steps:
 * <li>Run <b>ant junit-generate</b> target. This will create files in 
 * src/org/ephman/junit/generated directory. 
 * <li>Use the test.sql file to define schema in your database (by default we assume
 *  Postgres)
 * <li>Run <b>ant junit-compile</b> to compile all the test code.
 * <li>Use <b>ant junit-run</b> to execute the test. Your database server must be up and
 *  database connection parameters have to be specified in the build.xml file. OR 
 * use -Ddb.props=<props-file>  where the file reads like db.props.example (with your
 * info substituted)
 *
 * @author Richie Bielak
 * @author Paul Bethe
 * @date 2002/01/18
 */
public class TestAll {

	public static void main (String [] argv) {
		if (argv.length > 0) { // argument is class to run..
			System.out.println (">>> Running One unit test ("+argv[0]+")");
			junit.textui.TestRunner.run (oneSuite (argv[0]));
		} else {
			System.out.println (">>> Running Abra's unit tests");
			junit.textui.TestRunner.run (abraSuite ());
		}
		System.out.println (">>> Done with tests");
	}

	final static String PACKAGE_NAME = "org.ephman.junit.";

	public static TestSuite oneSuite (String testName) {
		TestSuite result = new TestSuite ();
		String testClass = testName.indexOf (".") == -1 ? PACKAGE_NAME + testName 
			: testName;
		Class oneTest = null;
		try {
			oneTest = Class.forName (testClass);
		} catch (Exception e) {
			e.printStackTrace ();
			System.out.println ("Unable to instantiate class " + testClass);
			System.exit (0);
		}
		result.addTestSuite (oneTest);
		return result;
	}

	public static TestSuite abraSuite () {
		TestSuite result = new TestSuite ();
		result.addTestSuite (TestConnection.class);
		result.addTestSuite (TestBasicStore.class);
		result.addTestSuite (TestTiming.class);
		result.addTestSuite (TestFilters.class);
		result.addTestSuite (TestThreads.class);
		result.addTestSuite (TestMarshaller.class);
		result.addTestSuite (TestAbraViews.class);
		result.addTestSuite (TestPerf.class);
		result.addTestSuite (TestQueryFactories.class);
		result.addTestSuite (TestDBCursors.class);
		return result;
	}

}
