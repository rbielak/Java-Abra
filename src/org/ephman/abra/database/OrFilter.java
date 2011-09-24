package org.ephman.abra.database;

/**
 * Title:			Or filter
 * Description:  	To represent 'or' in SQL
 * Copyright:	Copyright (c) 2001 Paul Bethe and Richie Bielak<p>
 * @author		 	Paul Bethe
 * @version 0.1.0
 */

public class OrFilter extends LRFilter {

	public OrFilter (QueryFilter left, QueryFilter right) {
		super (left, right);
	}



    protected String getTest () {
	    return "or";
    }
}
