/*
 * Created on Nov 18, 2004
 *
 */
package org.ephman.abra.database;

/**
 *  Sort and limit criteria for Oracle
 */
public class SortAndLimitCriteria extends SortCriteria {

	private int limit = 0;
	
	public SortAndLimitCriteria() {
		super();			
	}
	
	public SortAndLimitCriteria (SortCriteria sorter, int limit) {
		super ();
		if (sorter != null) {
			super.sortExpressions = sorter.getSortExpressions();
			super.tableAlias = sorter.tableAlias;
		}
		this.limit = limit;
	}

	public SortAndLimitCriteria(String column, boolean ascending, int limit) {
		super(column, ascending);
		this.limit = limit;
	}


	public int getLimit () {
		return limit;
	}


	/*
	 * For Oracle we have syntax:
	 * 
	 * 	select ..... where .... and (rownum <= n) order by ...
	 * 
	 * Note this code assumes that there is a where clause.
	 * 
	 */
	public String toString() {
		String result = super.toString();
		if (limit > 0) {
			result = " and (rownum <= " + limit + ") " + result;
		}
		return result;
	}
}
