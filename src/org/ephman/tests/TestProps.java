package org.ephman.tests;

import org.ephman.abra.tools.*;
import org.ephman.xml.*;
import java.io.*;
import java.util.*;
import junit.framework.*;
import org.apache.tools.ant.taskdefs.*;

public class TestProps extends TestCase {


	/**
	 * Run this
	 */
	public static void main (String[] argv) {
		junit.textui.TestRunner.run (makeTestSuite ());
		System.out.println ("Tests finished...");
	}
	
	/**
	 * Create a suite of tests to test SN code.
	 */
	protected static TestSuite makeTestSuite () {
		TestSuite suite = new TestSuite ("TestProps");
		// Add new tests here
		suite.addTest (new TestProps ());
		return suite;
	}
	
	
	
	TestProps () {
		super ("TestProps");

		baseDir = "/export/home/bethe/Development/Abra-0.8";
		srcDir = baseDir + "/src";
		outDir = baseDir + "/classes";
	}


	protected void runTest () throws Throwable {
		String fName = System.getProperty ("abra.home") + "/conf/test_props.xml";
		XmlParser parser = new AbraXmlParser (new FileReader (fName));
		XmlNode node = parser.parseXml ();
		PropertyTable pt = new PropertyTable ();
		pt.addProperties (node.getChildNodes (), fName);
		
		String st = "hi/${foo}/bye/${not}${bar}${ok}";
		String result = pt.replace (st);
		System.out.println ("Very cool Result = \"" + result + "\"");
		//tryIntro ();
	}

	String baseDir;
	String srcDir;
	String outDir;

	String className = "org.ephman.tests.Bar";
	String fileName = "org/ephman/tests/Bar";

	protected void tryIntro () throws Throwable {
		BufferedReader br = new BufferedReader (new FileReader (srcDir + "/" + fileName + ".txt"));
		PrintWriter writer = new PrintWriter (new BufferedWriter (new FileWriter (srcDir + "/" +
																				  fileName + ".java")));
		while (br.ready ()) {
			writer.println (br.readLine ());
		}
		writer.close ();
		br.close ();

		Javac compiler = new Javac ();
		//		compiler.setSrcDir (new Path (foo));
		compiler.execute ();
	}

}
