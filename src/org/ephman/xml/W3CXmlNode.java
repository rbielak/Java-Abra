package org.ephman.xml;

/** interface to wrap xml nodes..
 */
import java.util.*;
import javax.xml.parsers.*;
import org.xml.sax.*;
import org.w3c.dom.*;
import java.io.*;

public class W3CXmlNode extends XmlNode {
	
	public W3CXmlNode (Element el) {
		this.element = el;
	}

	Element element;
	Vector childNodes = null;
	public Element getElement () { return element; }

	Vector attrs = null;
	public Vector getAttributeList () {
		if (attrs == null) {
			attrs = new Vector ();
			NamedNodeMap nm = element.getAttributes ();
			for (int i=0; i < nm.getLength (); i++) {
				Attr a = (Attr)nm.item(i);
				attrs.add (new XmlAttribute (a.getName (), a.getValue ()));
			}
		}
		return attrs;
	}

	HashMap attr_hash = null;
	public Map getHashedAttributes () {
		if (attr_hash == null) {
			attr_hash = new HashMap ();
			NamedNodeMap nm = element.getAttributes ();
			for (int i=0; i < nm.getLength (); i++) {
				Attr a = (Attr)nm.item(i);
				attr_hash.put (a.getName (), a.getValue ());
			}
		}
		return attr_hash;
	}

	public Vector getChildNodes () {
		if (childNodes == null) {
			childNodes = new Vector ();
			NodeList children = element.getChildNodes ();
			for (int i = 0; i < children.getLength (); i++ )
				if (children.item (i) instanceof Element) {
					childNodes.addElement (new W3CXmlNode ((Element)children.item (i)));
				}
		}
		return childNodes;
	}
	
	public String getAttribute (String key) {
		return element.getAttribute (key);
	}
	public String getName () {
		return element.getTagName ();
	}

	public String getText () {
		String result = "";
		Node n = element.getFirstChild();
		if (n != null)
			result = n.getNodeValue ();
		return result;
	}
}
