/**
 * Title:			A Database Cursor class <p>
 * Description:  	To represent a session(cross-thread) set of hits.. <p>
 * Copyright:	Copyright (c) 2001 Paul Bethe and Richie Bielak<p>
 * @author		 	Paul Bethe
 * @version 0.1.0
 */

package org.ephman.abra.database;

import java.util.*;
import java.sql.*;
import java.io.*;

public class DatabaseCursor implements Serializable{

    private static final int DEFAULT_INCREMENT = 10;

    GenericFactoryBase theFactory;

    int lastOid;

    int increment;

    public QueryFilter theSearsh;

    int [] searchResults;

    int hitCount;

	/**
	 * Return the i-th Oid in the result of the cursor. If the index "i" is out of
	 * range, then 0 is returned - which is not a valid oid.
	 * @param i index of the oid to retrieve
	 * @return the i-th oid, or 0 if the index is out of range
	 */
	public int getOidFromSearchResults (int i) {
		int result = 0;
		if ((i >= 0) && (i < searchResults.length)) {
			result = searchResults[i];
		}
		return result;
	}

    public SortCriteria originalSort;

    /** construct a database cursor which contains a Vector of all the items hit
     *  in the last search.
     * @param theFactory where further lookups should be directed
     * @param hits a list of oids that corresponds to the hits
     * @param searsh the query which produced this list (mostly for tracking)
     * @param increment a default increment for the nextObjects method
     */

    public DatabaseCursor (GenericFactoryBase theFactory, Vector hits, QueryFilter searsh, SortCriteria originalSort,
          int hitCount, int increment) {
        super ();
        this.theFactory = theFactory;
        this.increment = increment;
        this.theSearsh = searsh;
        searchResults = new int[hits.size()];
        for (int i = 0; i < hits.size(); i++) {
            searchResults [i] = ((Integer)hits.get(i)).intValue();
        }
        //this.searchResults = hits;
        this.lastOid = 0;
        this.hitCount = hitCount;
        this.originalSort = originalSort;
    }

    public DatabaseCursor (GenericFactoryBase theFactory, Vector hits, QueryFilter searsh, SortCriteria originalSort, int hit_count) {
        this (theFactory, hits, searsh, originalSort, hit_count, DEFAULT_INCREMENT);
    }

    public int getHitCount () { return this.hitCount; }

	/** a routine to refresh the cursor (in case of stale data)
	 * @param dbSess a session to use..
	*/

	public void refreshSearch (DatabaseSession dbSess) throws SQLException {
		DatabaseCursor tmp = theFactory.cursorQuery (dbSess, theSearsh, originalSort);
		this.searchResults = tmp.searchResults;
		this.hitCount = tmp.hitCount;
	}


    /** a routine to get a sub-set of objects from the last query
     * @param dbSess the database Session
     * @param start the initial index (the Java way 0 - (size-1))
     * @param count the maximum number of items to be retrieved (less if not that many available)
     * @throws SQLException if an error occurs while talking to the database
     * @return Vector of the objects returned from the Factory..
     */

    public Vector getObjects (DatabaseSession dbSess, int start, int count) throws SQLException {
		if (searchResults.length == 0)
            return new Vector (); // return an empty vector
		if (count > chunkSize && searchResults.length > chunkSize) // need incremental get
			return getAllObjects (dbSess, start, count);
		SetFilter sf = new SetFilter (theFactory.getPrimaryColumn(), getOids(start, count));
		return theFactory.querySorted (dbSess, sf, originalSort);
    }

	/** due to Oracle restriction need can't get more than 100 at a time through SetFilter..
	 */
	private Vector getAllObjects (DatabaseSession dbSess, int start, int count) throws SQLException {
		Vector result = new Vector (); 

		int size = searchResults.length;
		while ((count > 0) && (start < size)) {
			result.addAll (getObjects (dbSess, start, (count < chunkSize) ? count:chunkSize));
			count -= chunkSize;
			start += chunkSize;
		}
		return result;
	}

	private static final int chunkSize = 90;

    public Vector getNextObjects (DatabaseSession dbSess) throws SQLException {
        return getNextObjects (dbSess, originalSort);
    }

    public Vector getNextObjects (DatabaseSession dbSess, SortCriteria sc) throws SQLException {
        if (searchResults.length == 0)
            return new Vector (); // return an empty vector
        SetFilter sf = new SetFilter (theFactory.getPrimaryColumn(), getOids(lastOid, increment));
        lastOid += increment;
        return theFactory.querySorted (dbSess, sf, sc);
    }

	/* Start must be > 0 and less than searchResult.size() */
    private int[] getOids (int start, int inc) {
        if (searchResults.length < start + inc)
            inc = searchResults.length - start;
        int [] result = new int[inc];
        for (int i = 0; i < inc; i++) {
            result[i] = searchResults[start + i];
        }
        // return new Vector (searchResults.subList (start, start + inc));
        return result;
    }



}
