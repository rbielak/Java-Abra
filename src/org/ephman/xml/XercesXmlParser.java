package org.ephman.xml;

/** class to allow plug-in xml parsers
 * for xerces (IBM)
 */
import javax.xml.parsers.*;
import org.xml.sax.*;
import org.w3c.dom.*;
import java.io.*;


public class XercesXmlParser implements XmlParser {
	
	public XercesXmlParser (Reader reader, boolean validate) {
		this.reader = reader;
		this.validate = validate;
	}

	Reader reader;
	boolean validate;

	protected DocumentBuilder domBuilder = null;

	public XmlNode parseXml () throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(validate);
		domBuilder = factory.newDocumentBuilder();
		InputSource source = new InputSource (reader);
		
		Document dom = domBuilder.parse(source);
		// dom.getDocumentElement().normalize();
		// now build map file
		Element root = dom.getDocumentElement ();
		//modify
		return new W3CXmlNode (root);
	}
}
