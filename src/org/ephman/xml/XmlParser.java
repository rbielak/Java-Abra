package org.ephman.xml;

/** interface to allow plug-in xml parsers
 * xerces of my own etc. 
 */

public interface XmlParser {
	
	public XmlNode parseXml () throws Exception ;

}
