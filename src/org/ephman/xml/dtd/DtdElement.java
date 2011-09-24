package org.ephman.xml.dtd;

/** interface to wrap dtd fields
 * should give name of field and type/validation (id string 4c!)
 */
import java.util.Vector;

public class DtdElement {

	public DtdElement (String n) {
		isPCData = true;
		setName (n);
	}

	public DtdElement (String n, Vector els) {
		isPCData = false;
		setName (n);
		elements = els;
	}
	
	private void setName (String n) {
		if (n.endsWith ("?")) 
			name = n.substring (0, n.length()-1);
		else if (n.endsWith ("*") || n.endsWith ("+")) {
			name = n.substring (0, n.length()-1);
			isVector = true;
		}
		else
			name = n;			
	}

	public boolean isVector = false;
	public String name;
	public boolean isPCData;
	public Vector elements = new Vector ();
	
	public String toString () {
		String result =  "<!ELEMENT " + name + "(";
		if (isPCData)
			result += "#PCDATA";
		else {
			for (int i=0; i < elements.size (); i++)
				result += " " + elements.elementAt (i) + ",";
			if (result.endsWith (","))
				result = result.substring (0, result.length () -1);
		}
		result += ") >";
		return result;
	}
	
}
