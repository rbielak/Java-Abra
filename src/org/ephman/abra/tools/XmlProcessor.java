package org.ephman.abra.tools;

/**
 * try to "optimize" hehe using my own xml parser..
 *
 * @author Paul M. Bethe
 * @version 0.0.3
 * @date Dec 3, 2001
 */
import org.ephman.xml.*;
import java.io.*;
import java.util.Vector;
import java.lang.reflect.Constructor;

public abstract class XmlProcessor {

	private Class xmlParserClass = null;

	private Constructor xmlPCon;

	XmlProcessor (boolean useAbraParser) {
		this.useAbraParser = useAbraParser;
		if (useAbraParser) {
			loadAbraParser ();
		}
	}

	/** internal method to use Class.forName / reflection
	 *  to load the abra parser and constructor
	 *  @return true if successful load..
	 */
	private boolean loadAbraParser () {
		if (xmlPCon != null) return true;
		try {
			// Load the Abra parser dynamically, so that we don't have
			// to depend on Java Cup libs at compile time.
			xmlParserClass = Class.forName ("org.ephman.xml.AbraXmlParser");
			xmlPCon = xmlParserClass.getDeclaredConstructor (new Class [] {Reader.class});
			return true; // if we get here OK
		}
		catch (Exception e) {
			System.out.println ("**Warning: cannot find Abra XML Parser. Using Xerces");
			this.useAbraParser = false; // can't use..
		}
		return false;
	}

	/**
	 * maps XML schema to a map file for unmarshalling
	 *
	 *	@param fname the name of the file to validate
	 *	@exception XmlEXecption - some parsing problem
	 */
	public XmlNode mapXMLFile (String schemaFname) throws
		          XmlException, FileNotFoundException {
		FileReader fr = new FileReader (schemaFname);
		return mapXMLFile (fr);
	}

	public XmlNode mapXMLFile (String schemaFname, boolean validate) throws
		         XmlException, FileNotFoundException {
		FileReader fr = new FileReader (schemaFname);
		return mapXMLFile (fr, validate);
	}


	public XmlNode mapXMLFile (Reader reader) throws
						XmlException, FileNotFoundException {
		return mapXMLFile (reader, false); // set to non-validating
	}
	/**
	 *
	 *
	 *	@param reader input XML is read from here
	 *  @param validate whether or not to validate..
	 *	@exception XmlExecption - some parsing problem
	 */
	public XmlNode mapXMLFile (Reader reader, boolean validate) throws
					XmlException, FileNotFoundException {

		try {
			XmlParser p = null;
			Object [] args = null;
			if (useAbraParser && loadAbraParser ()) {
				//make sure constructor exists..
				args = new Object[] {reader};
				p = (XmlParser)xmlPCon.newInstance (args);
				// System.out.println ("USING ABRA-PARSER");
			}
			else { // use xerces
				p = new XercesXmlParser (reader, validate);
			}
			XmlNode root = p.parseXml ();
			// now build map file
			return root;
		} catch (Exception e) { 
			//			e.printStackTrace ();
			if (!(e instanceof XmlException))
				throw new XmlException (e);
			throw (XmlException)e;
		}
	}

	
	protected boolean useAbraParser = false;

	protected abstract void applyRules (XmlNode thisNode, String mapFile) throws
	    XmlException, 
		IOException,
		SchemaException;

	protected void recurseOnChildList (Vector children, String mapFile) throws
	    XmlException, 
		IOException, 
		SchemaException	{
		
		if (children == null) return;
		for (int i = 0; i < children.size (); i++ )
				applyRules ((XmlNode)children.elementAt (i), mapFile);
	}

}
