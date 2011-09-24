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

public  class TestConnection extends DatabaseTest {

	public TestConnection (String name) {
		super (name);
	}

	public void testConnected () {
		checkConnection ();
	}

}
