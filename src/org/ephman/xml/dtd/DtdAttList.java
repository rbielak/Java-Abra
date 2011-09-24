package org.ephman.xml.dtd;

/** interface to wrap dtd fields
 * should give name of field and type/validation (id string 4c!)
 */
import java.util.Vector;

public class DtdAttList {

	public DtdAttList (String n, String syn) {
		name = n; 
		charSyntax = syn;
	}

	public String name;
	public String charSyntax;

	public String toString () {
		return "<!ATTLIST " + name + " Syntax   CDATA   #FIXED  " + charSyntax + ">"; 
	}

}
