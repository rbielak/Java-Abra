package org.ephman.abra.tools;

/**
 * Read an XML file and validate it with respect to it's DTD. Make sure that the name
 * od the DTD in the XML file follows the URL format. It should start with "file:" or
 * "http:" etc...
 *
 * @author Paul M. Bethe
 * @version 0.0.1
 * @date August 21, 2000
 */
import javax.xml.parsers.*;
import org.xml.sax.*;
import org.w3c.dom.*;
import java.io.*;

public abstract class SAXXmlProcessor {

	protected DocumentBuilder domBuilder = null;

	/**
	 * maps XML schema to a map file for unmarshalling
	 *
	 *	@param fname the name of the file to validate
	 *	@exception SAXParseException - parse error - see the content of exception for detailed info.
	 *	@exception ParserConfigurationException - can't create DOM builder
	 *	@exception IOException - cannot read the input file
	 *	@exception SAXEXecption - some other parsing problem
	 */
	public Element mapXMLFile (String schemaFname) throws
		          ParserConfigurationException,
		          FileNotFoundException,
		          IOException,
		          SAXException {
		FileReader fr = new FileReader (schemaFname);
		return mapXMLFile (fr);
	}

	public Element mapXMLFile (String schemaFname, boolean validate) throws
		          ParserConfigurationException,
		          FileNotFoundException,
		          IOException,
		          SAXException {
		FileReader fr = new FileReader (schemaFname);
		return mapXMLFile (fr, validate);
	}


	public Element mapXMLFile (Reader reader) throws
						ParserConfigurationException,
						FileNotFoundException,
						IOException,
						SAXException {
		return mapXMLFile (reader, true); // set to validating
	}
	/**
	 *
	 *
	 *	@param reader input XML is read from here
	 *  @param validate whether or not to validate..
	 *	@exception SAXParseException - parse error - see the content of exception for detailed info.
	 *	@exception ParserConfigurationException - can't create DOM builder
	 *	@exception IOException - cannot read the input file
	 *	@exception SAXEXecption - some other parsing problem
	 */
	public Element mapXMLFile (Reader reader, boolean validate) throws
						ParserConfigurationException,
						FileNotFoundException,
						IOException,
						SAXException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(validate);
		domBuilder = factory.newDocumentBuilder();
		InputSource source = new InputSource (reader);

		Document dom = domBuilder.parse(source);
		// dom.getDocumentElement().normalize();
		// now build map file
		Element root = dom.getDocumentElement ();
		//applyRules (root);
        return root;
	}

	protected abstract void applyRules (Element thisNode, String mapFile) throws
		ParserConfigurationException,
		FileNotFoundException,
		IOException,
		SAXException,
		SchemaException;

	protected void recurseOnChildList (NodeList children, String mapFile) throws
		ParserConfigurationException,
		FileNotFoundException,
		IOException,
		SAXException,
		SchemaException	{
		
		for (int i = 0; i < children.getLength (); i++ )
			if (children.item (i) instanceof Element)
				applyRules ((Element)children.item (i), mapFile);
	}

}
