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

	// TODO: figure out how this is done with SQL Server
	public String toString() {
		String result = super.toString();
		return result;
	}
}
