package org.ephman.abra.database;

/**
 * Title:			Prepared Filter <p>
 * Description:  	To represent a test with a '?' argument, then set the argument
 * Copyright:	Copyright (c) 2001 Paul Bethe and Richie Bielak<p>
 * @author		 	Paul Bethe
 * @version 0.1.0
 */

import java.sql.Timestamp;
import java.util.Vector;
import org.ephman.abra.utils.AbraRuntimeException;

public class PreparedFilter extends QueryFilter {

	String testString;
	Vector args;

	public PreparedFilter (PreparedQuery q, Vector args) {
		this (q.getSqlString (), args);
	}
	
	public PreparedFilter (String testString, Vector args) {
		this.testString = testString;
		this.args = args;
	}

	public PreparedFilter (String testString, Object value) {
		this.testString = testString;
		args = new Vector ();
		args.addElement (value);
	}
	public PreparedFilter (String testString, int value) {
		this (testString, new Integer (value));
	}
 	public PreparedFilter (String testString, double value) {
		this (testString, new Double (value));
	}

	public void addToPreparedQuery (PreparedQuery pq, String tableName) {
		//		String ali = computeAlias (tableAlias, tableName);
		pq.add (this.testString, args);
	}

	public String toString (String tableName) {
		//String ali = computeAlias (tableAlias, tableName);
		String result = testString;
		for (int i = 0; i < args.size (); i ++) {
			Object value = args.elementAt (i);
			int index = result.indexOf ("?");
			if (index == -1) throw new AbraRuntimeException ("? mismatch in filter");
			String v = value != null ? value.toString () : "null";
			if (value instanceof String || value instanceof Timestamp)
				v = "'" + v + "'";
			result = result.substring (0, index) + v + result.substring (index +1);
		}
		return result;
	}

	public String toString () {
		return toString (null);
	}

	public void setTableAlias (String alias) {
		tableAlias = alias;
	}

	String tableAlias;

}
