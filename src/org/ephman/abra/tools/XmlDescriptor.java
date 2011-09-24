package org.ephman.abra.tools;

import java.util.*;

/** a class to describe an xml node <-> class relationship
 *
 * @author Paul M. Bethe
 * @version 0.0.1 (11/3/00)
*/

public class XmlDescriptor {

    public XmlDescriptor (String className, String xmN) {
        attributeList = new Vector ();
		arrayElList = new Vector ();
        elementList = new HashMap ();
        this.className = className;
        this.xmlName = xmN;
    }

    public String xmlName;

    public String className;

    public void addAttribute (JField jf) {
        attributeList.addElement (jf);
    }

    public Vector attributeList;

    public HashMap elementList;
	public Vector arrayElList;

    public void addElement (JField jf) {
		if (jf.isArray ())
			arrayElList.addElement (jf);
        elementList.put(jf.getJavaName(), jf);
    }

}
