package org.ephman.xml;

import java.util.*;

/** playing around xml parser */

public class AbraXmlNode extends XmlNode {

	String name;
	String text;
	Vector attributeList;
	HashMap attributeHash;
	HashMap nameValueHash;
	Vector childNodeList;

	public AbraXmlNode (String name, Vector attributeList) {
		this.name = name;
		this.attributeList = attributeList;
		addAttrs ();
	}

	void addAttrs () {
		attributeHash = new HashMap ();
		nameValueHash = new HashMap ();
		for (int i=0; attributeList != null && i < attributeList.size (); i++) {
			XmlAttribute a = (XmlAttribute)attributeList.elementAt (i);
			attributeHash.put (a.name, a);
			nameValueHash.put (a.name, a.value);
		}
	}

	public Map getHashedAttributes () {
		return nameValueHash;
	}

	public Vector getChildNodes () { return childNodeList; }
	public Vector getAttributeList () { return attributeList; }

	public String getAttribute (String key) {
		XmlAttribute xa = (XmlAttribute)attributeHash.get (key);
		if (xa == null) return "";
		else return xa.getValue ();
	}

	public String getName () {
		return name;
	}

	public String getText () {
		return text;
	}
	public void setText (String t) {
		this.text = t;
	}

	public void setList (Vector list) {
		this.childNodeList = list;
	}


}
