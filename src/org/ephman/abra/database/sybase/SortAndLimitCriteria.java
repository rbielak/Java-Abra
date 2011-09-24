package org.ephman.abra.database;

/**
 *  Sort and limit criteria for Sybase
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
	 * Sybase does not support any kind of limit. A command "set rowcount n" is used 
	 * to define the limit for a single query;
	 */
	public String toString() {
		String result = super.toString();
		return result;
	}
}
