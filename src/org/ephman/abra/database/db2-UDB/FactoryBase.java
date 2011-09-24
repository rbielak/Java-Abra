package org.ephman.abra.database;
// meant to be copied down a level

import java.sql.*;
import java.util.*;
import org.ephman.utils.*;
import org.ephman.abra.utils.*;
import org.ephman.abra.database.*;
/**
 * FactoryBase class descendant for DB2 based architecture
 *
 * @author: Paul Bethe
 * @version 0.1
 */


public abstract class FactoryBase extends GenericFactoryBase{

	/* id will be done in generator */
	public boolean dbNeedsId () { return true; }


	/* change these as neccessary */
	protected void setClobs (DatabaseSession dbSess, ResultSet rs, Identified item) throws SQLException {
		throw new AbraSQLException ("ephman.abra.database.noclobs", "DB2-UDB");
    }

	protected void setClob (DatabaseSession dbSess, ResultSet rs, String columnName, String value) throws SQLException {
		throw new AbraSQLException ("ephman.abra.database.noclobs", "DB2-UDB");
	}

	
	protected int getLastId (DatabaseSession dbSess) throws SQLException {
		String sql = "select MAX(" + getPrimaryColumn () + ") from " 
			+ getTableName ();
		Statement stmt = getConnection(dbSess).createStatement ();
		ResultSet rs = stmt.executeQuery (sql);
		if (rs.next ()) {
			int result = rs.getInt (1);
			QueryTracer.trace (this, "Got new OID for " + getTableName () 
							   + "=" + result );
			return result;
		}
		else
			throw new AbraSQLException ("ephman.abra.database.nomax");

	}



}
