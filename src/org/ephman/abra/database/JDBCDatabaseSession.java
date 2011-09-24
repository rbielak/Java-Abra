
/**
 * Title:        JDBCDatabaseSession <p>
 * Description:  JDBC style database connection <p>
 * @author Richie Bielak & Paul Bethe
 * @version 0.0.1
 */
package org.ephman.abra.database;

import java.sql.*;
import java.util.*;
import org.ephman.abra.utils.*;
/**
 * JDBC based database connection.
 */

public class JDBCDatabaseSession implements DatabaseSession {

	private boolean inTran;

	private boolean lockingMode;

	private Connection conn;
	
	private Statement currentStatement;

	private HashMap factoryMap;

	/** Create a databasesession from a JDBC connection */
	public JDBCDatabaseSession(Connection conn) throws SQLException {
		this.conn = conn;

		conn.setAutoCommit(false);
		inTran = false;
		lockingMode = false;
		factoryMap = new HashMap ();
		currentStatement = null;
	}

	/** True if we are in a transaction */
	public boolean inTransaction () {
		return inTran;
	}

	/** Commint current transaction */
	public void commitTransaction () throws SQLException {
		if (!inTran) {
			throw new AbraSQLException ("ephman.abra.database.notran", new Object[]{"commit"});
		}
		factoryMap = new HashMap (); // wipe transaction cache clear..
		conn.commit();
		inTran = false;
		lockingMode = false;
		currentStatement = null;
	}

	/** Rollback current transaction */
	public void rollbackTransaction () throws SQLException {
		if (!inTran) {
			throw new AbraSQLException ("ephman.abra.database.notran", new Object[]{"rollback"});
		}
		factoryMap = new HashMap (); // wipe transaction cache clear..
		conn.rollback();
		inTran = false;
		lockingMode = false;
		currentStatement = null;
	}

	/** start a new transaction. */
	public void startTransaction () throws SQLException {
		if (inTran) {
			throw new AbraSQLException ("ephman.abra.database.intran");
		}
		inTran = true;
		factoryMap = new HashMap (); // wipe transaction cache clear..
		lockingMode = false;
		currentStatement = null;
	}

	public void setLockingMode () { lockingMode = true; } 
	public void endLockingMode () { lockingMode = false; } 
	
	public boolean isLockingMode () { return lockingMode; }

	/** try to get an object by oid from a cache for it's factory.. 
	 */
	public Identified getItem (GenericFactoryBase fact, int oid) {
		Identified item = null;
		HashMap factoryCache = (HashMap)factoryMap.get (fact);
		if (factoryCache != null) { // 
			item = (Identified)factoryCache.get (new Integer (oid));
		}
		return item;
	}

	/** put an object by oid into a cache for it's factory.. 
	 */
	public void putItem (GenericFactoryBase fact, Identified item) {
		HashMap factoryCache = (HashMap)factoryMap.get (fact);
		if (factoryCache == null) { // make a new one
			factoryCache = new HashMap ();
			factoryMap.put (fact, factoryCache);
		}
		factoryCache.put (new Integer(item.getOid ()), item);
	}


	/** Disconnect the session from the database */
	public void disconnect () throws SQLException {
		conn.close();
	}

	// WARNING: this method is to be used only by code that needs to do real JDBCs stuff
	// Such code should not call commit. Leave that to the client of this class.
	public Connection getJdbcConnection () {
		return conn;
	}
	
    /**
     * Cancel current query, if one is in progress.
     * 
     * @see org.ephman.abra.database.DatabaseSession#cancelCurrentQuery()
     */
    public void cancelCurrentQuery() throws SQLException {
        if (currentStatement != null) {
            currentStatement.cancel();
        }
    }
    
    public void setCurrentStatement (Statement stmt)  {
        currentStatement = stmt;
    }
    
    public Statement getCurrentStatement () {
        return currentStatement;
    }
}
