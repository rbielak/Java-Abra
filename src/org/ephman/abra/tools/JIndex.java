package org.ephman.abra.tools;

/** a class to represent a database index on a class..
 *
 * @author Paul M. Bethe
 * @version 0.0.1 4/18/2001
 */

public class JIndex {

	String name;
	
	/**
	   * Get the value of name.
	   * @return value of name.
	   */
	public String getName() {return name;}
	
	/**
	   * Set the value of name.
	   * @param v  Value to assign to name.
	   */
	public void setName(String  v) {this.name = v;}

	String fields;
	
	/**
	   * Get the value of fields.
	   * @return value of fields.
	   */
	public String getFields() {return fields;}
	
	/**
	   * Set the value of fields.
	   * @param v  Value to assign to fields.
	   */
	public void setFields(String  v) {this.fields = v;}

	public JIndex (String name, String fields) {
		this.name = name;
		this.fields = fields;
	}
}
