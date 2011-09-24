
/**
 * Title:        Merlin Project<p>
 * Description:  <p>
 * Copyright:	Copyright (c) 2001 Paul Bethe and Richie Bielak<p>
 * @author Tom Maciejewski (& Paul Bethe)
 * @version 1.0
 */
package org.ephman.abra.database;

import java.sql.PreparedStatement;
import org.ephman.abra.utils.Identified;
import java.sql.*;
import java.util.*;

public abstract class EndDateFactoryBase extends FactoryBase {

	protected EndDateFactoryBase () {
		endDates = true; // flag for FactoryBase to exclude where clause
	}

	final private String ENDDATE = "end_date";
	QueryFilter eFilter = new IsNullFilter(ENDDATE);

	protected String getSelectSql(String columnName){
		return " select * from " + getTableName() + " where " + columnName + "=? and " + ENDDATE + " is null ";
	}

	protected String getSelectSql() {
		return "select * from " + getTableName() + " where " +  ENDDATE + " is null ";
	}
	protected String getSelectCountSql() {
		return "select count(*) from " + getTableName() + " where " +  ENDDATE + " is null ";
	}
    protected String getDeleteSql (QueryFilter filter) {
        return "update " + this.getTableName() + " set " + getEndDate () + " where " + filter.toString()
            + " and " + ENDDATE + " is null";
    }

	// since all queries go here -- make sure to the is null filter..
	public Vector querySorted (DatabaseSession dbSess, QueryFilter query, SortCriteria sortBy) throws SQLException {
		query = query == null ? eFilter : new AndFilter(query,eFilter);
		return super.querySorted (dbSess, query, sortBy);
	}
    public Identified getObject (DatabaseSession dbSess, QueryFilter query)throws SQLException {
        query = query == null ? eFilter : new AndFilter(query,eFilter);
        return super.getObject(dbSess, query);
    }

	public DatabaseCursor cursorQuery (DatabaseSession dbSess, QueryFilter query, SortCriteria sortBy) throws SQLException {
		query = query == null ? eFilter : new AndFilter(query,eFilter);
		return super.cursorQuery (dbSess, query, sortBy);
	}

    String getEndDate () { return ENDDATE+ "=" + System.currentTimeMillis(); }

    protected void deleteObject (DatabaseSession dbSess, Identified item) throws SQLException {
        Connection conn = ((JDBCDatabaseSession)dbSess).getJdbcConnection ();
		String sql = "update " + getTableName() + " set "+ getEndDate ()
              + " where " + getPrimaryColumn() + "=?";
		PreparedStatement stmt = conn.prepareStatement (sql);
		stmt.setInt(1, item.getOid());
		((JDBCDatabaseSession)dbSess).setCurrentStatement(stmt);	
		int count = stmt.executeUpdate();
		((JDBCDatabaseSession)dbSess).setCurrentStatement(null);	
		stmt.close ();
		if (count != 1)
			throw new AbraSQLException ("ephman.abra.database.badenddate", new Integer(count));
		//		objectCache.remove (item);
    }

}
