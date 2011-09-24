package org.ephman.xml.dtd;

/** 
 * a test for the parser
 * @author Paul Bethe
 */
import java.util.Vector;
import java.io.*;
import junit.framework.*;

public class TestDtdParser extends TestCase {

	public static void main (String [] argv) {
		if (argv.length >0)
			fileName = argv[0];
		System.out.println (">>> Running Dtd tests");
		junit.textui.TestRunner.run (suite ());
	}	
	public static TestSuite suite () {
		TestSuite result = new TestSuite ();
		result.addTestSuite (TestDtdParser.class);
		return result;
	}


	public TestDtdParser (String name) {
		super (name);
	}

	static String fileName = null;
	static final String DEFAULT_FILE = "/home/paulb/Development/CTMXML/dtds/MultiTradeDetailResponse.dtd";

	public void testParser () throws Throwable {
		FileReader fr = new FileReader (fileName == null?
										DEFAULT_FILE : fileName);
		DtdParser p = new DtdParser (fr);
		Dtd d = p.parseDtd ();
		assertTrue ("dtd not null", d != null);
		System.out.println (d);
		
	}

}

