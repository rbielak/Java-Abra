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
	public boolean dbNeedsId () { return false; }


	/* change these as neccessary */
	protected void setClobs (ResultSet rs, Identified item) throws SQLException {
		throw new AbraSQLException ("ephman.abra.database.noclobs", "DB2");
    }

	protected void setClob (ResultSet rs, String columnName, String value) throws SQLException {
		throw new AbraSQLException ("ephman.abra.database.noclobs", "DB2");
	}

	
	protected int getLastId (DatabaseSession dbSess) throws SQLException {
		throw new AbraSQLException ("ephman.abra.database.nolastid", "DB2");
	}



}
