package org.ephman.abra.tools.plugins;

import java.util.*;

/** a class for plugins at the class level (ie View, Validator,  Taglib)
* 
* @version 0.0.1 8/16/2002
* @author Paul M. Bethe
*/

public class ClassPluginData {

	
	String className;  // the class name split from name
	String packageName;  // the package split from name
	String name;  // the fully qualified class name

	public String getName() {return name;}
	public void setName(String  v) {
		this.name = v;
		setNames ();
	}
	
	public String getClassName () {return this.className; }
	public String getPackageName () {return this.packageName; }

	String formatName;
	
	/**
	   * Get the value of formatName.
	   * @return Value of formatName.
	   */
	public String getFormatName() {return formatName;}
	
	/**
	   * Set the value of formatName.
	   * @param v  Value to assign to formatName.
	   */
	public void setFormatName(String  v) {this.formatName = v;}
	
	
	Vector fieldList;
	public Vector getFieldList() {return fieldList;}
	public void setFieldList(Vector  v) {this.fieldList = v;}
	/** add to field List
	 */
	public void addToFieldList(FieldPluginData jf) {
		fieldList.addElement (jf);
	}

	// extract the classname
	private void setNames () {
		if (name != null) {
			int i = name.lastIndexOf ('.');
			if (i != -1) {
				this.packageName = name.substring (0, i);
				this.className = name.substring (i+1);
			}
			else
				this.className = name;
		}
	}

	/* @param name the fully qualified class name
	 * @param formatName the alias like "blotter"
	 */
	public ClassPluginData (String name, String formatName) {

		this.className = "";
		this.packageName = "";
		this.formatName = formatName;
		this.fieldList = new Vector ();
		this.parentPlugin = null;
		this.name = name;
		setNames ();
	}
	
	/** for inheritance a view or validator can inherit from the parent by format name */
	ClassPluginData parentPlugin;
	public ClassPluginData getParentPlugin () {return parentPlugin;}
	public void setParentPlugin(ClassPluginData  v) {this.parentPlugin = v;}
	
	public Vector getAllFields () {
		Vector result = new Vector ();
		result.addAll (fieldList);
		
		if (this.getParentPlugin () != null) {
			Vector p_f = this.getParentPlugin ().getAllFields ();            
			result.addAll (p_f);
        }
		return result;
	}
}
