package org.ephman.utils;

/**
 * Read an XML file and validate it with respect to it's DTD. Make sure that the name
 * od the DTD in the XML file follows the URL format. It should start with "file:" or
 * "http:" etc...
 *
 * @author Richie Bielak
 * @version 0.1
 * @date August 21, 2000
 */
import javax.xml.parsers.*;
import org.xml.sax.*;
import org.w3c.dom.*;
import java.io.*;

public class Validator {

	private DocumentBuilder domBuilder = null;

	public static void main (String[] argv) {
		boolean valid = true;
		if (argv.length < 1) {
			System.out.println ("Usage: java com.tradinglinx.xml.Validator fname ");
		}
		else {
			Validator v = new Validator ();
			try {
				v.validateXMLFile (argv[0]);
			}
			catch (SAXParseException e) {
				System.out.println ("**** Error: Document not valid:" + e.getMessage ());
				System.out.println ("Line: " + e.getLineNumber () + " Column: " + e.getColumnNumber());
				System.out.println("Entity: " + e.getPublicId ());
				System.out.println("System: " + e.getSystemId ());
				valid = false;
			}
			catch (Exception e) {
				System.out.println("*** Parsing failed:" + e.toString()  + "mess=" + e.getMessage());
				valid = false;
			}
			if (valid) {
				System.out.println ("Success!!! Document was valid.");
			}
		}
	}

	/**
	 * validate - validates that XML read from the "reader"
	 *
	 *	@param fname the name of the file to validate
	 *	@exception SAXParseException - parse error - see the content of exception for detailed info.
	 *	@exception ParserConfigurationException - can't create DOM builder
	 *	@exception IOException - cannot read the input file
	 *	@exception SAXEXecption - some other parsing problem
	 */
	public void validateXMLFile (String fname) throws
						ParserConfigurationException,
						FileNotFoundException,
						IOException,
						SAXException {
		FileReader fr = new FileReader (fname);
		validate (fr, null);
	}

	/**
	 * validate - validates that XML read from the "reader"
	 *
	 *	@param reader input XML is read from here
	 *	@param dtdURI if no null, the URI of the DTD to which this XML is supposed to conform
	 *	@exception SAXParseException - parse error - see the content of exception for detailed info.
	 *	@exception ParserConfigurationException - can't create DOM builder
	 *	@exception IOException - cannot read the input file
	 *	@exception SAXEXecption - some other parsing problem
	 */
	public void validate (Reader reader, String dtdURI) throws
						ParserConfigurationException,
						FileNotFoundException,
						IOException,
						SAXException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(true);
		domBuilder = factory.newDocumentBuilder();
		InputSource source = new InputSource (reader);
		if (dtdURI != null) {
			source.setSystemId(dtdURI);
		}
		Document dom = domBuilder.parse(source);
		dom.getDocumentElement().normalize();
	}

}
