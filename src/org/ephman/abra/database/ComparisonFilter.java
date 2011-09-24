package org.ephman.abra.database;

/**
 * Title:			Comparison Filter <p>
 * Description:  	To represent a column/test/value logic <p>
 * Copyright:	Copyright (c) 2001 Paul Bethe and Richie Bielak<p>
 * @author		 	Paul Bethe
 * @version 0.1.0
 */

import java.sql.Timestamp;

public class ComparisonFilter extends QueryFilter {

	public ComparisonFilter (String columnName, String test, Object value) {
		this.columnName = columnName;
		this.test = test;
		this.value = value;
	}

	public ComparisonFilter (String columnName, String test, int value) {
		this (columnName, test, new Integer (value));
	}
 	public ComparisonFilter (String columnName, String test, double value) {
		this (columnName, test, new Double (value));
	}


	public void addToPreparedQuery (PreparedQuery pq, String tableName) {
		String ali = computeAlias (tableAlias, tableName);
		String qmark = "?";
		if (value instanceof QueryFilter)
			qmark = "";
		pq.add (ali + this.getColumnName () + this.getTest ()+ qmark, value);
	}

	public void addToPreparedQuery (PreparedQuery pq){
		addToPreparedQuery (pq, null);
	}

	public String toString () {
		return toString (null);
	}

	public String toString (String tableName) {
		String ali = computeAlias (tableAlias, tableName);
		String v = value != null ? value.toString () : "null";
		if (value instanceof String || value instanceof Timestamp)
			v = "'" + v + "'";
		return ali + this.getColumnName () + this.getTest () + v;
	}

	public void setTableAlias (String alias) {
		tableAlias = alias;
	}

	public String getTableAlias () {
		return tableAlias;
	}


	protected String tableAlias;

	protected String columnName;

	/**
	   * Get the value of columnName.
	   * @return Value of columnName.
	   */
	public String getColumnName() {return columnName;}

	/**
	   * Set the value of columnName.
	   * @param v  Value to assign to columnName.
	   */
	public void setColumnName(String  v) {this.columnName = v;}


	protected String test;

	/**
	   * Get the value of test.
	   * @return Value of test.
	   */
	public String getTest() {return test;}

	/**
	   * Set the value of test.
	   * @param v  Value to assign to test.
	   */
	public void setTest(String  v) {this.test = v;}


	Object value;

	/**
	   * Get the value of value.
	   * @return Value of value.
	   */
	public Object getValue() {return value;}

	/**
	   * Set the value of value.
	   * @param v  Value to assign to value.
	   */
	public void setValue(Object  v) {this.value = v;}

 
}
