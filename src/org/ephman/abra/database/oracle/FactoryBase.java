package org.ephman.abra.database;
// meant to be copied down a level

import java.sql.*;
import java.util.*;
import org.ephman.utils.*;
import org.ephman.abra.utils.*;
import org.ephman.abra.database.*;
/**
 * FactoryBase class - base for all the JDBC based Merlin db factories.
 *
 * @author: TPR
 * @version 0.1
 */


public abstract class FactoryBase extends GenericFactoryBase{


	/* Stupid way to find out the last ID. Do a query. At least it works */
	//	private int getLastId (DatabaseSession dbSess) throws SQLException {
	// is defined in Generic

	/** oracle way to set a clob*/
	/** a routine to get an oracle clob stream and set the value here
	 * @param rs an open resultset which needs this value
	 * @param columnName the column to get  aclob
	 * @param value the string to set in that column
	 */
	protected void setClob (DatabaseSession dbSess, ResultSet rs, String columnName, String value) throws SQLException {
        oracle.sql.CLOB clob = (oracle.sql.CLOB)rs.getClob(columnName);
        try {
            java.io.OutputStream os = clob.getAsciiOutputStream();
            os.write(value.getBytes());
            os.flush();
            os.close();
        } catch (java.io.IOException ie) {
            throw new SQLException (ie.getMessage());
        }
    }
	
	public boolean dbNeedsId () { return true; }

	/* This is the oracle way */
	protected int getLastId (DatabaseSession dbSess) throws SQLException {
		Connection conn = getConnection (dbSess);
		Statement stmt = conn.createStatement();
		String qs = "select " + getTableName ()+ "_seq.currval from dual";
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
