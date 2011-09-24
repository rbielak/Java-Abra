package org.ephman.abra.tools;

import java.util.*;

/** a class for field views
* to do an abstraction of a field if composite
* 
* @version 0.0.1 1/18/01
* @author Paul M. Bethe
*/

public class JFieldView {

	

	boolean asView = true;
	
	/**
	   * Get the value of asView.
	   * @return Value of asView.
	   */
	public boolean getAsView() {return asView;}
	
	/**
	   * Set the value of asView. if set then use a forein view in format
	   * else use the single field in format..
	   * @param v  Value to assign to asView.
	   */
	public void setAsView(boolean  v) {this.asView = v;}
	

	
	JField field;
	
	/**
	   * Get the value of field.
	   * @return Value of field.
	   */
	public JField getField() {return field;}
	
	/**
	   * Set the value of field.
	   * @param v  Value to assign to field.
	   */
	public void setField(JField  v) {this.field = v;}
	
	
	String viewFormat;
	
	/**
	   * Get the value of viewFormat.
	   * @return Value of viewFormat.
	   */
	public String getViewFormat() {return viewFormat;}
	
	/**
	   * Set the value of viewFormat.
	   * @param v  Value to assign to viewFormat.
	   */
	public void setViewFormat(String  v) {this.viewFormat = v;}
	
	public JFieldView (JField jf, String viewFormat, boolean asView) {
		this.field = jf;
		this.viewFormat = viewFormat;
		this.asView = asView;
	}
	
}
