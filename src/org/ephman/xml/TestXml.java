package org.ephman.xml;

import java_cup.runtime.*;
import java.util.Vector;
import java.util.HashMap;
//import org.ephman.abra.tools.MapToJava;
import org.w3c.dom.Element;
/** playing around xml parser */

public class TestXml {

	public static void main (String [] argv) {
		String test = "cv.xml";
		if (argv.length >0)
			test = argv[0];

		parsePrint (test, true);
		try {
			//	testMe (test);
			//testXerces (test);

		} catch (Exception e) { e.printStackTrace (); }
		
		
	}
	static int TO_TIME = 500;

	public static void testXerces (String fileName) throws Exception {
		
		/*MapToJava mtj = new  MapToJava ("", false, false, false, "", false);
		
		long startTime = System.currentTimeMillis ();
		for (int i = 0; i < TO_TIME; i++) {
			Element el = mtj.mapXMLFile (fileName);
		}
		long endTime = System.currentTimeMillis ();
		printResults ("Xerces", startTime, endTime); */
	}

	public static void testMe (String fileName) {
		long startTime = System.currentTimeMillis ();
		for (int i = 0; i < TO_TIME; i++) {
			parsePrint (fileName, false);
		}
		long endTime = System.currentTimeMillis ();
		printResults ("My parser", startTime, endTime);
	}

	public static void printResults (String parserName, long startTime, long endTime) {
		double avgTime = (endTime - startTime)/(TO_TIME);
		System.out.println ("Average time for '" + parserName + "' " + TO_TIME + " runs");
		System.out.println ("\tavg=" + avgTime + " ms");
	}

	public static void parsePrint (String fileName, boolean print) {
		try {
			Yylex lexer = new Yylex (fileName);
			XmlParser p = new AbraXmlParser (lexer);
			// Symbol s = p.parse ();
			XmlNode result = p.parseXml (); //(XmlNode)s.value;
			// lexer.close ();
			if (print) {
				System.out.println ("Parse Success");
				System.out.println (result.toXml ());
			}
		} catch (Exception e ) { e.printStackTrace ();}
	}

}
