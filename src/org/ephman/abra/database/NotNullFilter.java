
/**
 * Title:        null<p>
 * Description:  null<p>
 * Copyright:	Copyright (c) 2001 Paul Bethe and Richie Bielak<p>
 * @author Paul M. Bethe
 * @version 0.0.1
 */
package org.ephman.abra.database;

import java.sql.Timestamp;

public class NotNullFilter extends ComparisonFilter {

	public NotNullFilter (String columnName) {
		super (columnName, " is not ", null);
	}
	
	public void addToPreparedQuery (PreparedQuery pq, String tableName) {
		String ali = computeAlias (tableAlias, tableName);
		pq.appendText (ali + this.getColumnName () + this.getTest ()+ "null");
	}
}
