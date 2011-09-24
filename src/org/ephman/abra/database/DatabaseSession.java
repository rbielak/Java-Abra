
/**
 * Title:        DatabaseSession<p>
 * Description:  <p>
 * Copyright:	Copyright (c) 2001 Paul Bethe and Richie Bielak<p>
 * @author 		 Richie Bielak
 * @version 1.0
 */
package org.ephman.abra.database;


import org.ephman.abra.utils.*;
import java.sql.SQLException;
/**
 * DatabaseSession represents a connection to a database. One of these objects is
 * needed for a thread that needs to talk to a database.
 *
 */
public interface DatabaseSession {


	/** Starts a transaction. "inTransaction" must be false before this call */
	public void startTransaction () throws SQLException;

	/** Commit the current transaction. "inTransaction" must be true before this call */
	public void commitTransaction () throws SQLException;


	/** Rollsback the current transaction. "inTransaction" must be true before this call */
	public void rollbackTransaction () throws SQLException;

	/** Returns true if we are in a transaction. It will be set true when "startTransaction" is called
	 *  and it will become false when commit or rollback are called.
	 */
	public boolean inTransaction ();

	/** Returns true if we are locking objects to perform an update.. */
	public boolean isLockingMode ();

	/** Stores object in transacted cache */
	public void putItem  (GenericFactoryBase fac, Identified item);

	/** retrieves object from transacted cache */
	public Identified getItem  (GenericFactoryBase fac, int oid);
	

	/** Disconnect the session from the database */
	public void disconnect () throws SQLException;
	
	/** Cancel the last query that maybe in progress. If no query is
	 * in progress, do not do anything.
	 */
	public void cancelCurrentQuery () throws SQLException;
	
}
