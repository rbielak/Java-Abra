
/**
 * Title:        DatabaseSessionPool <p>
 * Description:  Pool of available database sessions.<p>
 * Copyright:	Copyright (c) 2001 Paul Bethe and Richie Bielak<p>
 * @author 	     Richie Bielak
 * @version 1.0
 */
package org.ephman.abra.database;

public interface DatabaseSessionPool {

	/**
	 * Get an available session from the pool.
	 *
	 * @return database session for use by a thread, null if none are available
	 */
	public DatabaseSession get();

	/**
	 * Return a session to the pool.
	 *
	 * @param session session to return to the pool
	 * @throws an exception if the pool is filled up
	 */
	public void put (DatabaseSession session) throws Exception ;

}
