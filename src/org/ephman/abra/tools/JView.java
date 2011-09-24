package org.ephman.abra.tools;

import java.util.*;

/** a class for Java views
* to do an abstraction of a generated class (subset of fields like blotter or short view)
* 
* @version 0.0.1 1/18/01
* @author Paul M. Bethe
*/

public class JView {

	
	String viewName;
	String className;
	String packageName;
	/**
	   * Get the value of viewName.
	   * @return Value of viewName.
	   */
	public String getViewName() {return viewName;}
	
	/**
	   * Set the value of viewName.
	   * @param v  Value to assign to viewName.
	   */
	public void setViewName(String  v) {
		this.viewName = v;
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
	
	/**
	   * Get the value of fieldList.
	   * @return Value of fieldList.
	   */
	public Vector getFieldList() {return fieldList;}
	
	/**
	   * Set the value of fieldList.
	   * @param v  Value to assign to fieldList.
	   */
	public void setFieldList(Vector  v) {this.fieldList = v;}

	/** add to field List
	 */
	public void addToFieldList(JFieldView jf) {
		fieldList.addElement (jf);
	}

	private void setNames () {
		if (viewName != null) {
			int i = viewName.lastIndexOf ('.');
			if (i != -1) {
				this.packageName = viewName.substring (0, i);
				this.className = viewName.substring (i+1);
			}
			else
				this.className = viewName;
		}
	}

	public JView (String viewName, String formatName) {

		this.className = "";
		this.packageName = "";
		this.formatName = formatName;
		this.fieldList = new Vector ();
		this.superView = null;
		this.viewName = viewName;
		setNames ();
	}

	public JView (String formatName) {
		this ("", formatName);
	}

	
	JView superView;
	
	/**
	   * Get the value of superView.
	   * @return Value of superView.
	   */
	public JView getSuperView() {return superView;}
	
	/**
	   * Set the value of superView.
	   * @param v  Value to assign to superView.
	   */
	public void setSuperView(JView  v) {this.superView = v;}
	

	
	public Vector getAllFields () {
		Vector result = new Vector ();
		result.addAll (fieldList);
		
		if (this.getSuperView () != null) {
			Vector p_f = this.getSuperView ().getAllFields ();            
			result.addAll (p_f);
        }
		return result;
	}
}
