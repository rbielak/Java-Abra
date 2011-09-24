package org.ephman.abra.database;

import java.util.*;
import java.sql.*;

import org.ephman.abra.utils.Identified;
/** a class to implement all the many to many operations
 *
 * @author Paul M. Bethe
 *@version 0.0.1 10/17/00
 */

public abstract class ManyToManyFactoryBase {




	protected Vector executeQuery (DatabaseSession dbSess, String query, int oid,
							FactoryBase fact) throws SQLException {

		Connection conn = ((JDBCDatabaseSession)dbSess).getJdbcConnection();
		PreparedStatement stmt = conn.prepareStatement(query);
		stmt.setInt(1, oid);
		((JDBCDatabaseSession)dbSess).setCurrentStatement(stmt);	
		ResultSet rs = stmt.executeQuery();
		((JDBCDatabaseSession)dbSess).setCurrentStatement(null);	
		Vector result = fact.getCollection (dbSess, rs);
		rs.close ();
		stmt.close();
		return result;
	}

    protected abstract String getTableName ();

	protected void addRelationship (DatabaseSession dbSess, String insert, int oid1, int oid2) throws SQLException {

		Connection conn = ((JDBCDatabaseSession)dbSess).getJdbcConnection();
		PreparedStatement stmt = conn.prepareStatement(insert);
		stmt.setInt(1, oid1);
		stmt.setInt(2, oid2);
		((JDBCDatabaseSession)dbSess).setCurrentStatement(stmt);	
		int count = stmt.executeUpdate ();
		((JDBCDatabaseSession)dbSess).setCurrentStatement(null);	
		if (count  != 1)
			throw new AbraSQLException ("ephman.abra.database.badinsert", 
										new Integer (count));
		stmt.close();

	}

	protected void removeRelationship (DatabaseSession dbSess, String delete, int oid1, int oid2) throws SQLException {

		Connection conn = ((JDBCDatabaseSession)dbSess).getJdbcConnection();
		PreparedStatement stmt = conn.prepareStatement(delete);
		stmt.setInt(1, oid1);
		stmt.setInt(2, oid2);
		((JDBCDatabaseSession)dbSess).setCurrentStatement(stmt);	  		
		stmt.executeUpdate ();
		((JDBCDatabaseSession)dbSess).setCurrentStatement(null);	
		//if (stmt.executeUpdate () != 1)
		//	throw new SQLException ("Delete from'" + getTableName () + "'returned count != 1");
		stmt.close();

	}

 	protected void removeAllRelationships (DatabaseSession dbSess, String delete, Identified item) throws SQLException {

		Connection conn = ((JDBCDatabaseSession)dbSess).getJdbcConnection();
		PreparedStatement stmt = conn.prepareStatement(delete);
		stmt.setInt(1, item.getOid());
		// need to check if two ?s (self m2m)
		if (delete.indexOf ('?') != delete.lastIndexOf ('?')) // two '?'s
			stmt.setInt(2, item.getOid());
		((JDBCDatabaseSession)dbSess).setCurrentStatement(stmt);	  		
		stmt.executeUpdate ();
		((JDBCDatabaseSession)dbSess).setCurrentStatement(null);	
		//if (stmt.executeUpdate () < 1)
		//	throw new SQLException ("Delete from'" + getTableName () + "'returned count < 1");
		stmt.close();
	}


    protected boolean hasRelationship (DatabaseSession dbSess, int oid1, int oid2) throws SQLException {
        Connection conn = ((JDBCDatabaseSession)dbSess).getJdbcConnection();
		PreparedStatement stmt = conn.prepareStatement(makeQueryString ());
		stmt.setInt(1, oid1);
		stmt.setInt(2, oid2);
		((JDBCDatabaseSession)dbSess).setCurrentStatement(stmt);			
        ResultSet rs = stmt.executeQuery();
        ((JDBCDatabaseSession)dbSess).setCurrentStatement(null);	
        boolean result = false;
        if (rs.next())
            result = true;
        rs.close();
        stmt.close();
        return result;
    }

    protected abstract String makeQueryString ();
}
