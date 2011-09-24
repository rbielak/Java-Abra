package org.ephman.abra.tools;

/**
 * Title:			JCompositeField <p>
 * Description:  	For composite objects (ie User has a Company as a field) <p>
 * Copyright:	Copyright (c) 2001 Paul Bethe and Richie Bielak<p>
 * @author		 	Paul Bethe
 * @version 0.1.0
 */

public class JCompositeField extends JField {

	public boolean isDbColumn () {
		return super.isDbColumn () && jClass != null;
	}

	public JCompositeField () {
		super ();
		jClass = null;
        _xmlAttribute = false;
	}

	/** override in base class.. check for inline.. */
	public String getObjectDefaultValue() {
		if (!inline)
			return super.getObjectDefaultValue ();
		else
			return "new " + super.getObjectType () + " ()";
	}

	boolean inline = false;
	public void setInline () { this.inline = true; }
	public boolean isInline () { return inline; }

	String prefix = "";
	public void setPrefix (String p) { this.prefix = p; }
	public String getPrefix () { return prefix; }


	JClass jClass;

	/**
	   * Get the value of jClass.
	   * @return Value of jClass.
	   */
	public JClass getJClass() {return jClass;}

	/**
	   * Set the value of jClass.
	   * @param v  Value to assign to jClass.
	   */
	public void setJClass(JClass  v) {this.jClass = v;}



}
