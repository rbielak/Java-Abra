package org.ephman.abra.database;

/**
 * Title:			postgres specific limit query
 * Description:  	
 * Copyright:	Copyright (c) 2003 Paul Bethe and Richie Bielak<p>
 * @author		 	Paul Bethe
 * @version 0.1.0
 */


import java.sql.Timestamp;
import org.ephman.utils.*;

public class LimitQuery extends QueryFilter {

	int size;
	QueryFilter inner;
	public LimitQuery (QueryFilter inner, int size) {
		this.inner = inner;
		this.size = size;
	}

	String tabAli;
	public void setTableAlias (String alias) {
		tabAli = alias;
	}

	public void addToPreparedQuery (PreparedQuery pq, String tableName) {
		if (inner != null)
			inner.addToPreparedQuery (pq, tableName);
		pq.addWrapperText ("", " limit " + size);
	}

	public String toString (String tableName) {
		return inner.toString () + " limit " + size;
	}
}
