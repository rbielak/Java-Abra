package org.ephman.abra.database;
//meant to be copied down a level..

import java.sql.*;
import java.util.*;
import org.ephman.abra.utils.*;
import org.ephman.abra.database.*;
/**
 * FactoryBase class - base for MySQL
 *
 * @author: Paul Bethe
 * @version 0.1
 */


public abstract class FactoryBase extends GenericFactoryBase {

	/* override this for PSQL */
	protected int getLastId (DatabaseSession dbSess) throws SQLException {
		Connection conn = getConnection (dbSess);
		Statement stmt = conn.createStatement();
		String qs = "select LAST_INSERT_ID()";
		//QueryTracer.trace (this, qs);
		ResultSet rs = stmt.executeQuery(qs);
		int result = 0;
		if (rs.next())
			result = rs.getInt (1);
		rs.close ();
		stmt.close ();
		//QueryTracer.trace (this, "finished");
		return (result);
	}

	public boolean dbNeedsId () { return true; }


	protected void setClobs (DatabaseSession dbSess, ResultSet rs, Identified item) throws SQLException {
		throw new AbraSQLException ("ephman.abra.database.noclobs", "MySQL");
    }

	protected void setClob (DatabaseSession dbSess, ResultSet rs, String columnName, String value) throws SQLException {
		throw new AbraSQLException ("ephman.abra.database.noclobs", "MySQL");
	}

}
