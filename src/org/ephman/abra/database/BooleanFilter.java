package org.ephman.abra.database;

/**
 * Title:			Boolean filter <p>
 * Description:  	To represent a test for True/false in SQL
 * Copyright:	Copyright (c) 2001 Paul Bethe and Richie Bielak<p>
 * @author		 	Paul Bethe
 * @version 0.1.0
 */


import java.sql.Timestamp;

public class BooleanFilter extends ComparisonFilter {

	boolean test;

	public BooleanFilter (String columnName, boolean test) {
		super (columnName, "=", (test ? "T" : "F"));
		this.test = test;
	}

}
