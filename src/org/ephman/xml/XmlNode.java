package org.ephman.xml;

/** interface to wrap xml nodes..
 */
import java.util.Vector;
import java.util.Map;

public abstract class XmlNode {	
	/** get child nodes whose name = tagName*/
	public Vector getChildNodes (String nodeName) { 
		Vector result = new Vector ();
		Vector childNodeList = getChildNodes ();
		if (childNodeList == null) return result;

		for (int i=0; i<childNodeList.size (); i++) {
			XmlNode node = (XmlNode)childNodeList.elementAt (i);
			if (node.getName ().equals (nodeName))
				result.addElement (node);
		}
		return result; 
	}

	/** get all child nodes*/
	public abstract Vector getChildNodes ();
	public abstract Vector getAttributeList () ;	
	public abstract Map getHashedAttributes () ;	

	public abstract String getAttribute (String key) ;
	public abstract String getName ();
	public abstract String getText ();

	// known methods..
	public String toXml () {
		return toXml ("");
	}

	public String toXml (String tabs) {
		String name = getName ();
		String result = tabs + "<" + name;
		Vector attributeList = getAttributeList ();
		for (int i=0; attributeList!= null && i < attributeList.size (); i++)
			result += " " + ((XmlAttribute)attributeList.elementAt (i));

		Vector childNodeList= getChildNodes ();
		String text = getText ();
		if (childNodeList == null) {
			if (text == null || text.equals (""))
				result += "/>";
			else {
				result += ">\n\t" + tabs + text + "\n";
				result += tabs + "</" + name + ">";	
			}
		}
		else {
			result += ">\n";
			for (int i=0; i < childNodeList.size (); i++) {
				XmlNode n = (XmlNode)childNodeList.elementAt (i);
				result += n.toXml (tabs + "\t") + "\n";
			}
			result += tabs + "</" + name + ">";
		}
		return result;
	}
}
