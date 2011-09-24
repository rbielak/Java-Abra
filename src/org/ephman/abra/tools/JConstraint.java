package org.ephman.abra.tools;


/** A class to wrap constraints.. 
 * @author Paul M. Bethe
 * @version 0.0.1 (1/31/01)
*/


public class JConstraint {

	public JConstraint (String name, String type, String constraint) {
		this.name = name; 
		this.type = type;
		this.constraint = constraint;
	}

	String name;

	public String getName () { return name; }

	String type;

	public String getType () { return type; }

	String constraint;

	public String getConstraint () { return constraint; }


}

