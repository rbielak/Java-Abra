package org.ephman.xml;

import java.util.Vector;
import java.util.HashMap;
/** playing around xml parser */

public class XmlAttribute {

	String name;
	String value;

	public XmlAttribute (String name, String value) {
		this.name = name;
		this.value = value;
	}

	public String getValue () { return value; }

	public String toString () {
		return name + "=\"" + value + "\"";
	}

}
