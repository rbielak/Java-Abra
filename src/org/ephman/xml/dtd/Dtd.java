package org.ephman.xml.dtd;

/** interface to wrap dtd fields
 * should give name of field and type/validation (id string 4c!)
 */
import java.util.*;

public class Dtd {

	public String docTypeString;
	public Vector everything;
	public DtdElement firstElement = null;
	HashMap elements = new HashMap ();
	HashMap attLists = new HashMap ();

	public DtdElement getElement (String name) {
		return (DtdElement)elements.get (name);
	}

	public DtdAttList getAttList (String name) {
		return (DtdAttList)attLists.get (name);
	}

	/** scan everything Vector and sort into HashMaps of elements/attlists
	 */
	public void analyze (String topName) {
		for (int i=0; i < everything.size (); i++) {
			Object obj = everything.elementAt (i);
			if (obj instanceof DtdElement) {
				DtdElement el = (DtdElement)obj;								
				elements.put (el.name, el);
			} else {
				DtdAttList att = (DtdAttList)obj;
				attLists.put (att.name, att);
			}
		}
		firstElement = (DtdElement)elements.get (topName);
	}

	public String toString () {
		String result = docTypeString;
		for (int i=0; everything != null && i < everything.size (); i++) 
			result += "\n" + everything.elementAt (i);
		return result + "\n";
	}
}
