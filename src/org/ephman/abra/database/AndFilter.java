package org.ephman.abra.database;


/**
 * Title:			AndFilter
 * Description:  	to represent and in SQL
 * Copyright   Paul Bethe and Richie Bielak  2000 (c)
 * @author		 	Paul Bethe
 * @version 0.1.0
 */

public class AndFilter extends LRFilter {

	public AndFilter (QueryFilter left, QueryFilter right) {
		super (left, right);
	}

	protected String getTest () {
        return "and";
    }

}
