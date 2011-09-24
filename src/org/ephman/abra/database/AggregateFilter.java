
/**
 * Title:        AggregateFilter<p>
 * Description:  Query filter<p>
 * Copyright:	Copyright (c) 2001 Paul Bethe and Richie Bielak<p>
 * @author Dimitri Taranov
 * @version 0.1
 */
package org.ephman.abra.database;

public class AggregateFilter extends QueryFilter {

    String columnName;
    String aggregateFunction;
    String tableName;
	String tableAlias = null;

	public void setTableAlias (String alias) {
		tableAlias = alias;
	}

    public AggregateFilter(String columnName, String aggregateFunction, String tableName) {
        this.columnName = columnName;
        this.aggregateFunction = aggregateFunction;
        this.tableName = tableName;
    }


	public void addToPreparedQuery (PreparedQuery pq, String tableName) {
		pq.appendText (this.toString (tableName));
	}

	public void addToPreparedQuery (PreparedQuery pq){
		addToPreparedQuery (pq, null);
	}


    public String toString(){
        return columnName + " = (select " + aggregateFunction + "(" + columnName + ") from " + tableName + ")";
    }
    public String toString(String tableName) {
		String ali = tableAlias == null ? (tableName == null ? "" : tableName) : tableAlias;
		if (ali.length () > 0) ali += ".";
        return ali + columnName +  " = (select " + aggregateFunction + "(" + ali+
			columnName + ") from " + tableName + ")";
    }
}
