package org.ephman.abra.database;

/**
 * Title:			LRFilter <p>
 * Description:  	all left right treed tests (and/or/etc.) <p>
 * Copyright:	Copyright (c) 2001 Paul Bethe and Richie Bielak<p>
 * @author		 	Paul Bethe
 * @version 0.1.0
 */

public abstract class LRFilter extends QueryFilter {
	
	public static final String LPAREN = "(";
	public static final String RPAREN = ")";
	

	protected LRFilter (QueryFilter left, QueryFilter right) {
		this.left = left;
		this.right = right;
	}

	public void setTableAlias (String alias) {
		right.setTableAlias (alias);
		left.setTableAlias (alias);
	}

    protected abstract String getTest ();


	public void addToPreparedQuery (PreparedQuery pq, String tableName) {
		if (this.left == null)
            right.addToPreparedQuery (pq, tableName);
        else if (this.right == null)
            left.addToPreparedQuery (pq, tableName);
		else {
			pq.appendText ("(");
			left.addToPreparedQuery (pq, tableName);
			pq.appendText (" " + getTest () + " ");
			right.addToPreparedQuery (pq, tableName);
			pq.appendText (")");
		}
		//		pq.add (this.getColumnName () + this.getTest ()+ "?", o)
	}

	public void addToPreparedQuery (PreparedQuery pq){
		addToPreparedQuery (pq, null);
	}

    public String toString () {
        return toString (null);
    }

    public String toString (String tableName) {
        if (this.left == null)
            return this.right == null ? "" : this.right.toString(tableName);
        else if (this.right == null)
            return this.left.toString(tableName);
        return "(" + this.getLeft (tableName) + " " + getTest () + " " 
			+ this.getRight (tableName) + ")";
    }

	QueryFilter left;

	/**
	   * Get the value of left.
	   * @return Value of left.
	   */
	public String getLeft() {return left.toString ();}


	public String getLeft(String tableName) {return left.toString (tableName);}
	/**
	   * Set the value of left.
	   * @param v  Value to assign to left.
	   */
	public void setLeft(QueryFilter	 v) {this.left = v;}


	QueryFilter right;

	/**
	   * Get the value of right.
	   * @return Value of right.
	   */
	public String getRight() {return right.toString ();}

	public String getRight(String tableName) {return right.toString (tableName);}
	/**
	   * Set the value of right.
	   * @param v  Value to assign to right.
	   */
	public void setRight(QueryFilter  v) {this.right = v;}


	public QueryFilter getLeftFilter () {return this.left;}

	public QueryFilter getRightFilter () {return this.right;}


}
