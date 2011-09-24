package org.ephman.abra.database;

/**
 * Title:			Prepared Query 
 * Description:  	When building queries - produce a string
 *  with '?' in order to 'prepare' the statement.  Then set args using a 
 *  vector of Objects
 *
 * Copyright:	Copyright (c) 2001 Paul Bethe and Richie Bielak<p>
 * @author		 	Paul Bethe
 * @version 0.1.0
 */


import java.sql.*;
import java.util.Vector;
import org.ephman.utils.*;

public  class PreparedQuery {

	public PreparedQuery () {
	}

	public PreparedQuery (QueryFilter filter) {
		filter.addToPreparedQuery (this);
	}

	public PreparedQuery (QueryFilter filter, String tableName) {
		filter.addToPreparedQuery (this, tableName);
	}

	StringBuffer frontWrap = new StringBuffer ();
	StringBuffer endWrap = new StringBuffer ();

	StringBuffer sqlString = new StringBuffer ();
	Vector args = new Vector ();

	public String getWrappedString (String query) {
		return frontWrap.toString () + query + endWrap.toString();
	}

	public String getSqlString () {
		return sqlString.toString ();
	}

	public Vector getArguments () {
		return args;
	}

	public void addWrapperText (String toPrepend, String toAppend) {
		frontWrap.insert (0, toPrepend);
		endWrap.append (toAppend);
	}

	// for adding things like '(' and 'and'
	public void appendText (String newText) {
		sqlString.append (newText);
	}

	public void add (String newText, Vector manyArgs) {
		sqlString.append (newText);
		args.addAll (manyArgs);
	}

	public void add (String newText, Object arg) {
		sqlString.append (newText);
		if (arg instanceof QueryFilter)
			((QueryFilter)arg).addToPreparedQuery (this, null);
		else
			args.addElement (arg);
	}

	/** set the arguments into the stmt starting at 1*/
	public void setArgs (PreparedStatement stmt) throws SQLException { 
		setArgs (stmt, 1); 
	}
	/** set the arguments into the stmt starting at startLoc */
	public void setArgs (PreparedStatement stmt, int startLoc) throws SQLException {
		for (int i=0; i < args.size (); i++) {
			Object o = args.elementAt (i);
			stmt.setObject (startLoc + i, o);
		}
	}


}

