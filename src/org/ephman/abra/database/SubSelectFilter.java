package org.ephman.abra.database;

/**
 * Title:			SubSelect
 * Description:  	To represent a sub query  ... where foo.oid in (select oid from x where ..)
 * Copyright:	Copyright (c) 2003 Paul Bethe and Richie Bielak<p>
 * @author		 	Paul Bethe
 * @version 0.1.0
 */

import java.sql.Timestamp;

public class SubSelectFilter extends QueryFilter {

	String column;
	String subTableName; 
	QueryFilter whereClause;

	public SubSelectFilter (String column, String subTableName, QueryFilter whereClause) {
		this.column = column;
		this.subTableName = subTableName;
		this.whereClause = whereClause;
	}

	String tabAlias;
	public void setTableAlias (String alias) { tabAlias = alias; }
		
	
	public String toString (String tableName) {
		return "(select " + column + " from " + tableName + " where "+ whereClause.toString () + ")";
	}

	public void addToPreparedQuery (PreparedQuery pq, String tableName) {
		String ali = computeAlias (tabAlias, tableName);
		pq.appendText ("(select " + column + " from " + subTableName + " where ");
		whereClause.addToPreparedQuery (pq, null);
		pq.appendText (")");
	}

}
