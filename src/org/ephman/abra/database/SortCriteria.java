
/**
 * Title:        SortCriteria - describe how to sort data returned by the database queries<p>
 * @author 		Richie Bielak
 * @version 1.0
 */
package org.ephman.abra.database;

import java.util.Vector;

public class SortCriteria {

	// Names of columns to sort on
	protected Vector sortExpressions;
	protected String tableAlias;

	/**
	 * Create a new SortCriteria - this object will be used to sort result of
	 * a query in a specific way.
	 */
	public SortCriteria() {
		sortExpressions = new Vector ();
	}

	/** create a new sc with an inital column, asc */
	public SortCriteria(String column, boolean ascending) {
		this ();
		tableAlias = null;
		addCriteria (column, ascending);
	}


	public void setTableAlias (String alias) {
		tableAlias = alias;
	}

	/**
	 * Return a vector of sort expressions
	 */
	public Vector getSortExpressions () {
		return sortExpressions;
	}

	/**
	 * Add a sort criteria.
	 *
	 * @param column name of the column to sort on
	 * @param ascending if true, the sort will use ascending order
	 */
	public void addCriteria (String column, boolean ascending) {
		String sortExpr = column;
		if (ascending)
			sortExpr += " asc";
		else
			sortExpr += " desc";
		sortExpressions.add (sortExpr);
	}

	/** Return the SQL string that can be used in a query to sort
	 *  the query result. The format of the string is:
	 *  	'order by colA ascending, colB descending, ...' etc
	 *
	 * If no sort criteria has been specified, an empty string will be
	 * returned.
	 */
	public String toString () {
		if (sortExpressions.size() > 0) {
			String result = " order by ";
			for (int i = 0; i < sortExpressions.size() ; i++) {
				result += ((tableAlias == null)? "":(tableAlias+".")) +  (String)sortExpressions.elementAt(i);
				if (i < sortExpressions.size() - 1)
					result += ", ";
			}
			return result;
		}
		else {
			return "";
		}
	}

}
