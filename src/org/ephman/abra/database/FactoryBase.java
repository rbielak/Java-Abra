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
				throw new AbraSQLException ("ephman.abra.database.noclobs", "Sybase");
    }
	
	public boolean dbNeedsId () { return true; }

	protected String makeLockSql () {
		// Sybase does not support "select ... for update"
		return  ("select * from " + getTableName () + " where (" + getPrimaryColumn() + "=?)");
	}

	/* This is the Sybase way */
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


    /* (non-Javadoc)
     * @see org.ephman.abra.database.GenericFactoryBase#getResults(org.ephman.abra.database.DatabaseSession, java.lang.String, org.ephman.abra.database.QueryFilter, org.ephman.abra.database.SortCriteria, boolean, java.lang.String)
     */
    protected QueryResult getResults(DatabaseSession dbSess, String sql,
            QueryFilter filter, SortCriteria sc, boolean needsWhereLogic,
            String tableName) throws SQLException {
        QueryResult result = null;
        Connection conn = ((JDBCDatabaseSession)dbSess).getJdbcConnection();
        int limit = 0;
        try {
            if (sc != null && sc instanceof SortAndLimitCriteria) {
                limit = ((SortAndLimitCriteria)sc).getLimit();
                if (limit > 0) {
                    Statement stmt = conn.createStatement();
                    stmt.executeUpdate("set rowcount " + limit);
                    stmt.close();
                }
            }
            result = super.getResults(dbSess, sql, filter, sc, needsWhereLogic,  tableName);
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        finally {
            try {
                if (limit > 0) {
                    Statement stmt = conn.createStatement();
                    stmt.executeUpdate("set rowcount 0");
                    stmt.close();
                }
            } catch (Exception e) {
                // don't care here
                System.out.println ("Exception resetting rowcount: " + e);
            }
        }
        return result;
    }
 
  
}
