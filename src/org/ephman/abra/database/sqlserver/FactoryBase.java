package org.ephman.abra.database;
// meant to be copied down a level

import java.sql.*;
import java.util.*;
import org.ephman.utils.*;
import org.ephman.abra.utils.*;
import org.ephman.abra.database.*;
import org.ephman.abra.database.GenericFactoryBase.QueryResult;
/**
 * FactoryBase class - base for all the JDBC based Merlin db factories.
 *
 * @author: TPR
 * @version 0.1
 */


public abstract class FactoryBase extends GenericFactoryBase{


	/** 
	 * @param rs an open resultset which needs this value
	 * @param columnName the column to get  aclob
	 * @param value the string to set in that column
	 */
	protected void setClob (DatabaseSession dbSess, ResultSet rs, String columnName, String value) throws SQLException {
				throw new AbraSQLException ("ephman.abra.database.noclobs", "SQLServer");
    }
	
	public boolean dbNeedsId () { return true; }


   protected QueryResult getResults(DatabaseSession dbSess, String sql,
									QueryFilter filter, SortCriteria sc, boolean needsWhereLogic,
									String tableName) throws SQLException {
	   QueryResult result = null;
	   String queryString = sql;
	   if (sc != null && sc instanceof SortAndLimitCriteria) {
	       queryString = queryString.trim();
	       int firstBlankPos = queryString.indexOf(' ');
	       // insert "top <limit>" after the select
	       queryString = "select top " + ((SortAndLimitCriteria)sc).getLimit()
	       		+ queryString.substring(firstBlankPos);
		   
	   }
	   result = super.getResults(dbSess, queryString, filter, sc, needsWhereLogic,  tableName);
	   return result;	   
   }

	protected String makeLockSql () {
		return  ("select * from " + getTableName () + " with (holdlock) where (" + getPrimaryColumn() + "=?)");
	}


	protected int getLastId (DatabaseSession dbSess) throws SQLException {
		Connection conn = getConnection (dbSess);
		Statement stmt = conn.createStatement();
		String qs = "select MAX ("+getPrimaryColumn()+") from "
			+ getTableName ();
		QueryTracer.trace (this, qs);
		ResultSet rs = stmt.executeQuery(qs);
		int result = 0;
		if (rs.next())
			result = rs.getInt (1);
		rs.close ();
		stmt.close ();
		QueryTracer.trace (this, "finished");
		return (result); 
	}


}
