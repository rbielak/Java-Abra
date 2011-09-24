package org.ephman.abra.database;
//meant to be copied down a level..

import java.sql.*;
import java.util.*;
import org.ephman.abra.utils.*;
import org.ephman.abra.database.*;
import org.postgresql.largeobject.*;
/**
 * FactoryBase class - base for POSTGRESQL
 *
 * @author: Paul Bethe
 * @version 0.1
 */


public abstract class FactoryBase extends GenericFactoryBase {

	/* override this for PSQL */
	protected int getLastId (DatabaseSession dbSess) throws SQLException {
		Connection conn = getConnection (dbSess);
		Statement stmt = conn.createStatement();
		String qs = "select currval('" + getTableName ()+ "_seq')";
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


	protected void setClob (DatabaseSession dbSess, ResultSet rs, String columnName, String value) throws SQLException {
		if (value == null) return;
		org.postgresql.PGConnection conn = (org.postgresql.PGConnection)getConnection(dbSess);
		int oid = rs.getInt (columnName);
		LargeObjectManager lom = conn.getLargeObjectAPI();
		LargeObject lo = lom.open(oid);
		lo.write (value.getBytes ());
		lo.close ();
		//throw new AbraSQLException ("ephman.abra.database.noclobs", "PSQL");
	}

}
