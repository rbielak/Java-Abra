package org.ephman.abra.database;

/**
 * Title:			Wrapper Filter <p>
 * Description:  	to wrap the main stmt with a select (like Oracle limit query)
 * clause in an SQL query <p>
 * Copyright:	Copyright (c) 2001 Paul Bethe and Richie Bielak<p>
 * @author		 	Paul Bethe
 * @version 0.1.0
 */


import java.sql.Timestamp;
import org.ephman.utils.*;
import java.util.Vector;

public class WrapperFilter extends QueryFilter {

	QueryFilter inner; /* the query that this is wrapping */
	Vector columns;
	QueryFilter whereClause; /* the filter for outer choice */
	SortCriteria outerSort; /* the filter for outer choice */

	public WrapperFilter (QueryFilter inner, Vector columns, QueryFilter whereClause) {
		this (inner, columns,whereClause, null);
	}

	public WrapperFilter (QueryFilter inner, Vector columns, QueryFilter whereClause,
						  SortCriteria outerSort) {
		this.outerSort = outerSort;
		this.columns = columns;
		this.whereClause = whereClause;
		this.inner = inner;
	}
	

	String tabAlias;
	public  void setTableAlias (String alias) {
		tabAlias = alias;
	}


	String getPrep () {
		String prep = "select ";
		if (columns == null || columns.size () == 0)
			prep += "* ";
		else
			for (int i=0; i<columns.size (); i++)
				prep += columns.elementAt(i);
		prep += " from ";
		return prep;
	}

	String getPost (String tableName) {
		String post = ") as ";
		if (tableName != null)
			post += tableName + "1 ";
		else if (tabAlias != null)
			post += tabAlias + " ";
		else
			post += "foo ";
		if (whereClause != null)
			post += whereClause.toString () + " ";
		if (outerSort != null)
			post += outerSort.toString ();
		return post;
	}

	public void addToPreparedQuery (PreparedQuery pq, String tableName) {
		String prep = getPrep ();
		String post = getPost (tableName);
		if (inner != null)
			inner.addToPreparedQuery (pq, tableName);
		pq.addWrapperText (prep + " (", post);
	}

	public String toString (String tableName) {
		return getPrep () + " (" +  inner.toString () + ") " + getPost(tableName);
	}

}
