package org.ephman.abra.tools;

/**
 * Title:			TypeMap <p>
 * Description:  	To keep track of a single type across - language/db/xml
 * Copyright:	Copyright (c) 2002 Paul Bethe and Richie Bielak<p>
 * @author		 	Paul Bethe
 * @version 0.1.0
 */
import java.util.Vector;

public class TypeMap {

	
	String abraTypeName;
	
	/**
	   * Get the value of AbraTypeName.
	   * @return value of AbraTypeName.
	   */
	public String getAbraTypeName() {return abraTypeName;}
	
	/**
	   * Set the value of AbraTypeName.
	   * @param v  Value to assign to AbraTypeName.
	   */
	public void setAbraTypeName(String  v) {this.abraTypeName = v;}
	
	
	String objectTypeName;
	
	/**
	   * Get the value of objectTypeName.
	   * @return value of objectTypeName.
	   */
	public String getObjectTypeName() {return objectTypeName;}
	
	/**
	   * Set the value of objectTypeName.
	   * @param v  Value to assign to objectTypeName.
	   */
	public void setObjectTypeName(String  v) {this.objectTypeName = v;}

	
	String objectDefaultValue;
	
	/**
	   * Get the value of objectDefaultValue.
	   * @return value of objectDefaultValue.
	   */
	public String getObjectDefaultValue() {return objectDefaultValue;}
	
	/**
	   * Set the value of objectDefaultValue.
	   * @param v  Value to assign to objectDefaultValue.
	   */
	public void setObjectDefaultValue(String  v) {this.objectDefaultValue = v;}
	
	String sqlTypeName;
	
	/**
	   * Get the value of sqlTypeName.
	   * @return value of sqlTypeName.
	   */
	public String getSqlTypeName(JField jf) {
		String result = sqlTypeName;
		if (isHasLengthArg())
			result += "(" + jf.getLength () +")";
		return result;
	}
	
	/**
	   * Set the value of sqlTypeName.
	   * @param v  Value to assign to sqlTypeName.
	   */
	public void setSqlTypeName(String  v) {this.sqlTypeName = v;}

	
	boolean hasLengthArg;
	
	/**
	   * Get the value of hasLengthArg.
	   * @return value of hasLengthArg.
	   */
	public boolean isHasLengthArg() {return hasLengthArg;}
	
	/**
	   * Set the value of hasLengthArg.
	   * @param v  Value to assign to hasLengthArg.
	   */
	public void setHasLengthArg(boolean  v) {this.hasLengthArg = v;}
	
	
	boolean composite;
	
	/**
	   * Get the value of composite.
	   * @return value of composite.
	   */
	public boolean isComposite() {return composite;}
	
	/**
	   * Set the value of composite.
	   * @param v  Value to assign to composite.
	   */
	public void setComposite(boolean  v) {this.composite = v;}

	
	public TypeMap (String dk, String obj, boolean hasLengthArg) {
		this (dk, obj, null, null, hasLengthArg);
	}

	public TypeMap (String dk, String obj) {
		this (dk, obj, null, null, false);
	}

	public TypeMap (String dk, String obj, String def, String sql) {
		this (dk, obj, def, sql, false);
	}

	public TypeMap (String dk, String obj, String def, String sql, boolean hasLengthArg) {
		abraTypeName = dk;
		objectTypeName = obj;
		objectDefaultValue = def;
		sqlTypeName = sql;
		this.hasLengthArg = hasLengthArg;
	}
}
