package org.ephman.junit;

import junit.framework.TestCase;
import org.ephman.abra.database.*;
import org.ephman.junit.generated.*;
import java.sql.*;
import java.io.*;
import java.util.*;

/** a test to try features in the marshaller
 * @author Paul Bethe
 */

public  class TestMarshaller extends DatabaseTest {

	public TestMarshaller (String name) {
		super (name);
	}


	protected void setUp () throws Exception {
		super.setUp ();
	}

	public void testMarshaller () throws Throwable {
		//		createBadBook ();

		XMLTest xt = new XMLTest ();
		xt.setWriter (new Poet ());
		marshaller.setWriteRunTimeTypes (true); // for inheritance//
		String output = marshaller.marshal (xt);
		System.out.println (output);
		XMLTest xt_two = (XMLTest)marshaller.unmarshal (new java.io.StringReader(output));
		assertTrue ("correct run-time type", xt_two.getWriter () instanceof Poet);
	}

}
