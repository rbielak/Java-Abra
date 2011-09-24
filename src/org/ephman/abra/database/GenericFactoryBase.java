package org.ephman.abra.database;

import java.sql.*;
import java.util.*;
import org.ephman.utils.*;
import org.ephman.abra.utils.*;
/**
 * FactoryBase class - base for all the JDBC based  db factories.
 *
 * @author TPR
 * @version 0.1
 */


public abstract class GenericFactoryBase {

	// Create a default cache for objects. TODO: should be created in
	// a smarter way.

	//	protected WeakCache objectCache = new WeakCache ();


	// TODO: WARNING if classes keep persistent copies of objects
	// they will no longer have the right copy after a cache flush
	public void flushCache () {
		// objectCache.flush ();
	}


	protected Connection getConnection (DatabaseSession dbSess) {
		return ((JDBCDatabaseSession)dbSess).getJdbcConnection();
	}

	protected Identified getObject (DatabaseSession dbSess,
						String columnName, String value)
						throws SQLException
	{
		Identified result = null;
		String sql = getSelectSql(columnName);

		QueryResult qr = getResults (dbSess, sql, new PreparedFilter ("", value), null);
		ResultSet rs = qr.rs;
		if (rs.next ()) {
			result = makeObject (dbSess, rs);
		}
		qr.close ();
		return result;
	}

	protected Identified getObject (DatabaseSession dbSess,
						String columnName, int value)
						throws SQLException
	{
		Identified result = null;
		if (columnName.equals(getPrimaryColumn())) {
			result = dbSess.getItem (this, value);
			if (result != null)
				checkRefresh (dbSess, result);
		}
		if (result == null) {
			String sql = getSelectSql(columnName);
			QueryResult qr = getResults (dbSess, sql, value);
			ResultSet rs = qr.rs;
			if (rs.next ()) {
				result = makeObject (dbSess, rs);
			}
			qr.close ();
		}
		return result;
	}

		/** routine to store an object -- if new putNew is called
	 * else update is called
	 */
	protected void storeObject (DatabaseSession dbSess, Identified item) throws SQLException {
		storeObject (dbSess, item, DEEP); // default to deep store
	}

	/** routine to store an object -- if new putNew is called
	 * else update is called
	 */
	protected void storeObject (DatabaseSession dbSess, Identified item, int storageDepth) throws SQLException {
		if (item.getOid () == 0) // is a new item
			putNewObject (dbSess, item, storageDepth);
		else
			updateObject  (dbSess, item, storageDepth);
	}
	/** routine to place a new object in the db
	 *
	*/

	protected void putNewObject (DatabaseSession dbSess, Identified item) throws SQLException {
		putNewObject (dbSess, item, DEEP); //default to deep-storage
	}

	/** putNewObject stores an object for the first time using insert stmts
	 */
	protected void putNewObject (DatabaseSession dbSess, Identified item, int storageDepth) throws SQLException {
		if (storageDepth == DEEP)
			preStorage (dbSess, item);
        marshalObjects (item);
		PreparedStatement stmt = makeInsertStmt (dbSess);
		// System.out.println (stmt + "foo");
		setArguments (stmt, item, false);
		QueryTracer.trace (this, "putnew");
		((JDBCDatabaseSession)dbSess).setCurrentStatement(stmt);
		boolean isOk = stmt.execute();
		((JDBCDatabaseSession)dbSess).setCurrentStatement(null);
		int count = stmt.getUpdateCount ();
		//		stmt.close ();
		if (count != 1) {
			stmt.close ();
			throw new AbraSQLException ("ephman.abra.database.badinsert", new Integer(count));
		}
		if (dbNeedsId ()) // most dbs will need to check a rtn val from function or call db to get currval
			item.setOid(getLastId (dbSess, stmt));
		stmt.close (); // now close stmt
		dbSess.putItem (this, item);
		if (storageDepth == DEEP)
			deepStorage (dbSess, item);
        if (hasClobs ()) {
            updateClobs (dbSess, item);
        }
		//		QueryTracer.trace (this, "finished w/ put new");
	}

	public abstract boolean dbNeedsId ();

	/** generic. can override in descendant class if procs are supported */

	protected PreparedStatement makeInsertStmt (DatabaseSession dbSess) throws SQLException {
		Connection conn = ((JDBCDatabaseSession)dbSess).getJdbcConnection ();
		if (useStoredProcs ())
			return conn.prepareCall (makeInsertCall ());
		else // just dynamic sql
			return conn.prepareStatement (makeInsertString());
	}
	/* checks for procs if so makes.. */
	protected PreparedStatement makeUpdateStmt (DatabaseSession dbSess) throws SQLException {
		Connection conn = ((JDBCDatabaseSession)dbSess).getJdbcConnection ();
		if (useStoredProcs ())
			return conn.prepareCall (makeUpdateCall ());
		else // just dynamic sql
			return conn.prepareStatement (makeUpdateString());
	}



	protected void deleteObject (DatabaseSession dbSess, Identified item) throws SQLException {
		Connection conn = ((JDBCDatabaseSession)dbSess).getJdbcConnection ();
		String sql = "delete from " + getTableName () + " where " + getPrimaryColumn() + "=?";
		//String sql = " update " + getTablename() + " set ( end_date = " + System.currentTimeMillis() + " ) where " + getPrimaryColumn() + "=?";
		PreparedStatement stmt = conn.prepareStatement (sql);
		stmt.setInt(1, item.getOid());
		QueryTracer.trace(this, sql, item.getOid ());
		((JDBCDatabaseSession)dbSess).setCurrentStatement(stmt);		
		int count = stmt.executeUpdate();
		((JDBCDatabaseSession)dbSess).setCurrentStatement(null);
		stmt.close ();
		if (count != 1)
			throw new AbraSQLException ("ephman.abra.database.baddelete", new Integer(count));
		//objectCache.get(OID);
		// objectCache.remove (item);
		//		QueryTracer.trace(this, "finished w/ delete");
	}

    /** deleteObjects will delete all the rows which match the given filter
     * @param dbSess the database connection
     * @param filter the query filter to apply
     * @return the number of rows affected
    */
    protected int deleteObjects (DatabaseSession dbSess, QueryFilter filter) throws SQLException {
		Connection conn = ((JDBCDatabaseSession)dbSess).getJdbcConnection ();
        if (filter == null)
			throw new AbraSQLException ("ephman.abra.database.nodelfilter");
		String sql = getDeleteSql (filter);
		//String sql = " update " + getTablename() + " set ( end_date = " + System.currentTimeMillis() + " ) where " + getPrimaryColumn() + "=?";
		PreparedStatement stmt = conn.prepareStatement (sql);
		QueryTracer.trace(this, sql);
		((JDBCDatabaseSession)dbSess).setCurrentStatement(stmt);
		int count = stmt.executeUpdate();
		((JDBCDatabaseSession)dbSess).setCurrentStatement(null);
		stmt.close ();
        return count;
	}


	protected void updateObject (DatabaseSession dbSess, Identified item) throws SQLException {
        updateObject (dbSess, item, DEEP);
    }

    protected int SHALLOW = 42;
    protected int DEEP = 34; // constants for deep and shallow retrieval

    protected void updateObject (DatabaseSession dbSess, Identified item, int storageDepth)
            throws SQLException {

        if (storageDepth == DEEP) {
            preStorage (dbSess, item);
        }
        marshalObjects (item);
		PreparedStatement stmt = makeUpdateStmt (dbSess);
		if (item instanceof Versioned) {
			Versioned v = (Versioned)item;
			v.setVersion (v.getVersion () + 1);
			// ((JDBCDatabaseSession)dbSess).addTransactionItem (v);
		}
		setArguments (stmt, item, true);
		QueryTracer.trace (this, "updateObject");
		((JDBCDatabaseSession)dbSess).setCurrentStatement(stmt);		
		int count = stmt.executeUpdate ();
		((JDBCDatabaseSession)dbSess).setCurrentStatement(null);
		stmt.close ();
		if (!useStoredProcs () && count != 1)
			throw new AbraSQLException ("ephman.abra.database.badupdate", new Integer(count));
        if (storageDepth == DEEP)
		    deepStorage (dbSess, item);
        if (hasClobs ()) {
            updateClobs (dbSess, item);
        }
		//		QueryTracer.trace(this, "finished w/ update");
	}

	public Vector queryObjects (DatabaseSession dbSess, String column, int key)
			throws SQLException {
		Vector result = null;
		Connection conn = ((JDBCDatabaseSession)dbSess).getJdbcConnection();
		/*String sql = "select * from " + getTableName () + " where " +
				column + " =?";
		*/
		String sql = this.getSelectSql(column);
		QueryResult qr = getResults (dbSess, sql, key);
		ResultSet rs = qr.rs;
		result = getCollection (dbSess, rs);
		qr.close ();
		return result;
	}

    /**  a routine to get objects with a given DatabaseLookup..
	 * @param dbSess is the in-transaction database session
	 * @param lookup a ViewLookup from the generated descendant of this class
	 * @param filter an option filter to restrict hits
    */
    public Vector queryObjects (DatabaseSession dbSess, ViewLookup lookup, QueryFilter filter)
            throws SQLException {
		return queryObjects (dbSess, lookup, filter, null);
	}

	public Vector queryObjects (DatabaseSession dbSess, ViewLookup lookup,
								QueryFilter filter, SortCriteria sortCriteria)
	  throws SQLException {
		if (lookup.getFactory () != this)
			throw new AbraSQLException ("ephman.abra.database.wronglookfact");
		String sqlString = lookup.getSqlString ();
		if (filter != null) {
			if (!sqlString.endsWith ("where "))
				sqlString += " and ";
		} else if (sqlString.endsWith ("where ")) { //hack
			sqlString = sqlString.substring (0, sqlString.length () - 6);
			// remove 'where ' from string
		}
		QueryResult qr = getResults (dbSess, sqlString, filter, sortCriteria,
									 false, "this");
        ResultSet rs = qr.rs;
        Vector result = new Vector ();
        java.lang.reflect.Method rip = lookup.getExtractMethod ();
		Object [] params = new Object [] {rs};
        while (rs.next ()) {
			Object item = null;
			try {
				item = rip.invoke (this, params);
			}
			catch (Exception e) {
				// e.printStackTrace();
                e.printStackTrace();
				qr.close ();
                if (e instanceof SQLException)
                    throw (SQLException)e;
                else
				  throw new AbraSQLException ("ephman.abra.database.badreflect",
							   new Object[] { rip.getName (),
												  e.getMessage ()});
			}
            result.addElement(item);
        }
		qr.close ();
        return result;
    }

    /** A routine to lock an object and all of its sub parts in the database (thread locking)
	 * which is only released when this thread commits/rollsback
	 *
     * @param dbSess the current merlin session
     * @param item the Identified item to be locked (and even refreshed)
	 * @throws SQLException when db-error occurs
     */
	public void lock (DatabaseSession dbSess, Identified item) throws SQLException {
		if (item.getOid () == 0) {
			throw new AbraSQLException ("ephman.abra.database.badlock");
		}
		String sql = makeLockSql();
		((JDBCDatabaseSession)dbSess).setLockingMode ();
		QueryResult qr = getResults (dbSess, sql, item.getOid());
		ResultSet rs = qr.rs;
		if (item instanceof Versioned) {
			Versioned v = (Versioned)item;
			rs.next ();
			checkRefresh (dbSess, v, rs); // see if object (an sub pieces) need refreshing..
		}
		((JDBCDatabaseSession)dbSess).endLockingMode ();
		qr.close ();
	}

	// separate routine that can be overriden for specific database
	protected String makeLockSql () {
		return  ("select * from " + getTableName () + " where (" + getPrimaryColumn() + "=?) for update");
	}

	// returns whether procs are used for this class..
	protected abstract boolean useStoredProcs ();

	protected abstract Identified makeFromResultSet (ResultSet rs) throws SQLException;

	protected abstract void refreshFromResultSet (Identified item, ResultSet rs) throws SQLException;

	protected abstract String makeUpdateString ();

	protected abstract String makeInsertString ();

	/** call to a stored proc for this object.. */
	protected abstract String makeInsertCall ();

	/** call to a stored proc for this object.. */
	protected abstract String makeUpdateCall ();

	protected abstract String getPrimaryColumn ();

	protected abstract String getTableName ();


	protected abstract void setArguments (PreparedStatement stmt, Object item,
				boolean update) throws SQLException;


 	protected void deepRetrieval (DatabaseSession dbSess, Identified item) throws SQLException {
        	// do nothing - override if your class needs to do specific deep lookups
	}

	protected void deepStorage (DatabaseSession dbSess, Identified item) throws SQLException {
		// do nothing - override if your class needs to do specific deep lookups
	}

	protected void marshalObjects (Identified item) throws SQLException {
        	// do nothing - override if your class needs to marshal objects to xml
	        // text before storing
	}


	protected void preStorage (DatabaseSession dbSess, Identified item) throws SQLException {
        	// override if your object needs save db objects
	        // before storing this object
	}

	public Vector getCollection (DatabaseSession dbSess, ResultSet rs) throws SQLException {
		Connection conn = getConnection (dbSess);
		Vector result = new Vector ();
		while (rs.next()) {
				//int oid = rs.getInt(getPrimaryColumn ());
				Identified item = makeObject (dbSess, rs);
				result.add (item);
		}
		return result;
	}

	// regular queries.. (no cursors.. )
	/** A routine to get a hits given the filter
     * @param dbSess the current merlin session
	 * @param query the filter to delimit hits..
     *
     * @throws SQLException if a db error occurs
     * @return Vector of results
     */

   	public Vector queryObjects (DatabaseSession dbSess, QueryFilter query) throws SQLException {
		return querySorted (dbSess, query, null);
	}

	/** A routine to get a all from the table
     * @param dbSess the current merlin session
     *
     * @throws SQLException if a db error occurs
     * @return Vector of results
     */
	protected Vector queryAll (DatabaseSession dbSess) throws SQLException {
        return querySorted (dbSess, null, null);
	}

	/** A routine to get a all from the table - sorted
     * @param dbSess the current merlin session
     * @param sortBy the sorting criteria
     *
     * @throws SQLException if a db error occurs
     * @return Vector of results
     */

	protected Vector queryAllSorted (DatabaseSession dbSess, SortCriteria sortBy) throws SQLException {
		return querySorted (dbSess, null, sortBy);
	}

	/** A routine to get a objects from a table using the filter and sorted w/ the criteria
     * @param dbSess the current merlin session
     * @param sortBy the sorting criteria
	 * @param query the filter to search with
     *
     * @throws SQLException if a db error occurs
     * @return Vector of results
     */
   	public Vector querySorted (DatabaseSession dbSess, QueryFilter query, SortCriteria sortBy) throws SQLException {
       	Vector result = null;
		String sql = getSelectSql ();//"select * from " + getTableName ();
		QueryResult qr = getResults (dbSess, sql, query, sortBy);
		ResultSet rs = qr.rs;
		result = getCollection (dbSess, rs);
		qr.close ();
		return result;
	}

    /** A routine to get a single object given a filter
     * @param dbSess the current merlin session
     * @param query one which should find 0-1 objects
     *
     * @throws SQLException if a db error occurs or more than 1 object is found on this
     * filter
     * @return Identified
     */

    public Identified getObject (DatabaseSession dbSess, QueryFilter query)
          throws SQLException {

        Identified result = null;
		String sql = getSelectSql ();
		QueryResult qr = getResults (dbSess, sql, query, null);
		ResultSet rs = qr.rs;
        if (rs.next()) {
            result = makeObject (dbSess, rs);
        }
        if (rs.next()) { // extra hit found throw EX
            qr.close();
            throw new AbraSQLException ("ephman.abra.database.morethanone", this.getClass().getName());
        }
		qr.close ();
		return result;
    }


	protected Identified makeObject (DatabaseSession dbSess, ResultSet rs) throws SQLException {
		int oid = rs.getInt(getPrimaryColumn());
		Identified result = dbSess.getItem (this, oid);
		if (result == null) { // retrieve
			result = makeFromResultSet (rs);
			dbSess.putItem(this, result);
			if (dbSess.isLockingMode ()) // fresh copy so is locked..
				result.setLocked ();
			deepRetrieval (dbSess, result);

		} else if (dbSess.isLockingMode ()) { // not null but locking mode
			// check for refresh
			if (!result.isLocked ()) // not locked yet within transaction so check for a refresh
				checkRefresh (dbSess, result, rs);
		}
		return result;
	}

    // Here so that we can override it for query based factories
    protected boolean defaultNeedsWhereLogic () {
        return true;
    }

    protected boolean needsAndBeforeFilter () {
        return false;
    }

	/** get results and set key in first arg.. */
	protected QueryResult getResults (DatabaseSession dbSess, String sql, int key)
		throws SQLException {
		return getResults (dbSess, sql, new PreparedFilter ("", key), null);
	}

	/** a helper routine to build a query and execute */
	protected QueryResult getResults (DatabaseSession dbSess, String sql,
									  QueryFilter filter, SortCriteria sc) throws SQLException {
		return getResults (dbSess, sql, filter, sc, defaultNeedsWhereLogic(), null);
	}

	protected QueryResult getResults (DatabaseSession dbSess, String sql,
									  QueryFilter filter, SortCriteria sc,
									  boolean needsWhereLogic, String tableName)
		throws SQLException {

		Connection conn = ((JDBCDatabaseSession)dbSess).getJdbcConnection ();

		PreparedQuery pq = null;
		if (filter != null) {
			if (tableName == null)
				pq = new PreparedQuery (filter);
			else
				pq = new PreparedQuery (filter, tableName);
			String qs = pq.getSqlString ();
			if (qs.length () > 0) {
				if (!endDates && needsWhereLogic)
					sql += " where ";
                if (needsAndBeforeFilter())
                    sql += " and ";
				sql += qs;
			}
		}
		if (sc != null)
			sql += sc.toString ();
		/* new PMB 6/10/03 -- this allows the pq to deal with wrapper queries like Oracle Limit */
		if (pq != null)
			sql = pq.getWrappedString (sql);

		PreparedStatement stmt = conn.prepareStatement (sql);

		QueryTracer.trace (this, sql, pq);
		if (pq != null)
			pq.setArgs (stmt);

		((JDBCDatabaseSession)dbSess).setCurrentStatement(stmt);
		ResultSet rs = stmt.executeQuery ();
		((JDBCDatabaseSession)dbSess).setCurrentStatement(null);
		return new QueryResult (rs, stmt);
	}


   /** A routine to count the number of rows in a query
     * which returns the count of hits
     *
     * @param dbSess the current merlin session
     * @param filter a QueryFilter
     */

    public int countRows (DatabaseSession dbSess, QueryFilter filter)
          throws SQLException {
		int result = 0;
		String sql = getSelectCountSql();
		QueryResult qr = getResults (dbSess, sql, filter, null);
		ResultSet rs = qr.rs;
		if (rs.next ()) {
			result = rs.getInt (1);
		}
		qr.close (); // close rs and stmt
		return result;
    }
    /** A routine to perform a search given an ordering
     * which returns the Vector of oid hits in a DatabaseCursor object
     * this allows small range retrievals with any type of data abstraction
     *
     * @param dbSess the current merlin session
     * @param sortBy a list of columns on which to sort the data
     */

    public DatabaseCursor cursorQuery (DatabaseSession dbSess, SortCriteria sortBy)
          throws SQLException {
        return cursorQuery (dbSess, null, sortBy);
    }
    /** A routine to perform a search given a filter
     * which returns the Vector of oid hits in a DatabaseCursor object
     * this allows small range retrievals with any type of data abstraction
     *
     * @param dbSess the current merlin session
     * @param query a query filter with any lookup specific criteria
     */

    public DatabaseCursor cursorQuery (DatabaseSession dbSess, QueryFilter query)
          throws SQLException {
        return cursorQuery (dbSess, query, null);
    }

    /** A routine to perform a search given a filter and an ordering
     * which returns the Vector of oid hits in a DatabaseCursor object
     * this allows small range retrievals with any type of data abstraction
     *
     * @param dbSess the current merlin session
     * @param query a query filter with any lookup specific criteria
     * @param sortBy a list of columns on which to sort the data
     */

    public DatabaseCursor cursorQuery (DatabaseSession dbSess, QueryFilter query, SortCriteria sortBy) throws SQLException {
        //String sql = "select " + getPrimaryColumn() + " from " + getTableName();
        String sql = getCursorQuerySql();
		QueryResult qr = getResults (dbSess, sql, query, sortBy);
		ResultSet rs = qr.rs;

        Vector hits = new Vector ();
        int hit_count = 0;
        while (rs.next()) {
            hits.addElement (new Integer (rs.getInt(getPrimaryColumn ())));
            hit_count++;
        }
        // now cleanup
		qr.close ();
        return new DatabaseCursor (this, hits, query, sortBy, hit_count);
    }

    /**
     * Raw query returns DatabaseCursor - to be used in descendant classes
     *
     * @param dbSess
     * @param sql
     * @return
     * @throws SQLException
     */
    protected DatabaseCursor cursorRawQuery (DatabaseSession dbSess, String sql) throws SQLException {
        QueryResult qr = getResults (dbSess, sql, null, null);
        ResultSet rs = qr.rs;
        Vector hits = new Vector ();
        int hit_count = 0;
        while (rs.next()) {
            hits.addElement (new Integer (rs.getInt(getPrimaryColumn ())));
            hit_count++;
        }
        // now cleanup
		qr.close ();
        return new DatabaseCursor (this, hits, null, null, hit_count);
    }


	/**
	 *  Execute a stored proc with some arguments that returns an integer.
	 */
	protected int storedProcCall (DatabaseSession dbSess, String procCall, Vector args) throws SQLException {
		int result = 0;
		Connection conn = getConnection (dbSess);
		CallableStatement cstmt = conn.prepareCall (procCall);
		cstmt.registerOutParameter (1, Types.INTEGER);
		if (args != null) {
			for (int i = 0; i < args.size (); i++) {
				/* Arguments are counted from 1 and the first is the return value */
				cstmt.setObject (i+2, args.elementAt(i));
			}
		}
		((JDBCDatabaseSession)dbSess).setCurrentStatement(cstmt);
		cstmt.execute ();
		((JDBCDatabaseSession)dbSess).setCurrentStatement(null);
		result = cstmt.getInt (1);
		cstmt.close ();
		return result;
	}

    protected DatabaseCursor cursorStoredProcCall (DatabaseSession dbSess,
                                                   String procCall,
                                                   Vector args,
                                                   SortCriteria sort)
            throws SQLException
    {
        DatabaseCursor cursor = null;
        Connection conn = getConnection (dbSess);
        PreparedStatement cstmt = conn.prepareCall(procCall);
        if (args != null) {
            for (int i = 0; i < args.size(); i++) {
                cstmt.setObject(i+1, args.elementAt(i));
            }
        }
    	((JDBCDatabaseSession)dbSess).setCurrentStatement(cstmt);
        ResultSet rs = cstmt.executeQuery();
    	((JDBCDatabaseSession)dbSess).setCurrentStatement(null);
        if (rs.next()) {
            Vector hits = new Vector ();
            int hit_count = 0;
             while (rs.next()) {
                hits.addElement (new Integer (rs.getInt(getPrimaryColumn ())));
                hit_count++;
            }
            cursor = new DatabaseCursor (this, hits, null, null, hit_count);
            cursor.originalSort = sort;
        }
        return cursor;
    }

	// code for refreshing data..
	protected void checkRefresh (DatabaseSession dbSess, Identified item) throws SQLException {
		// check refresh with no rs..  need to make rs..
		if (item instanceof Versioned && !item.isLocked ()) { // versioned object..
			String sql = getSelectSql(getPrimaryColumn ());
			QueryResult qr = getResults (dbSess, sql, item.getOid ());
			ResultSet rs = qr.rs;
			if (rs.next ()) {
				checkRefresh (dbSess, item, rs);
			}
			qr.close ();
		}
	}
	/** routine to check if an object is up to date..
	 * @param dbSess this threads connection
	 * @param item the dbObject in question
	 * @param rs an open ResultSet represneting this object in the db
	 * @throws SQLException on error.
	 */
	protected void checkRefresh (DatabaseSession dbSess, Identified item, ResultSet rs) throws SQLException {
		if (item instanceof Versioned) { // versioned object..
			Versioned v = (Versioned)item;
			if (v.getVersion () != rs.getInt (VERSION_NUMBER) || v.getVersion () == Versioned.INVALID) {
				// out of date..
				refreshFromResultSet (v, rs);
			}
			if (!v.isLocked ()) { // set locked and do deep retrieval..
				v.setLocked ();
				deepRetrieval (dbSess, v); // do an update on all the sub pieces..
			}
		}
	}
// code for clob storage..

    // internal function called by putNew and update to wrap rs call
    private void updateClobs (DatabaseSession dbSess, Identified item) throws SQLException {
        Connection conn = this.getConnection(dbSess);
        String sql = "select * from " + getTableName () + " where (" + getPrimaryColumn() + "=?) for update";
		QueryResult qr = getResults (dbSess, sql, item.getOid());
		ResultSet rs = qr.rs;
        if (!rs.next()) {
			qr.close ();
            throw new AbraSQLException ("ephman.abra.database.noclob", this.getTableName());
		}
        setClobs (dbSess, rs, item);
		qr.close();
    }

	protected int getLastId (DatabaseSession dbSess, PreparedStatement stmt) throws SQLException {
		if (stmt instanceof CallableStatement) {
			int result = ((CallableStatement)stmt).getInt (1); // return val
			//stmt.close ();
			return result;
		} else { // just dynamic
			//stmt.close ();
			return getLastId (dbSess);
		}
	}
    // override if you need to set clobs

    protected void setClobs (DatabaseSession dbSess, ResultSet rs, Identified item) throws SQLException {
    }

    /**  sub factory overrides returning true or false
    */
    protected abstract boolean hasClobs ();


	/** need specific db type libraries to do see oracle/FactoryBase */
	protected abstract void setClob (DatabaseSession dbSess, ResultSet rs, String columnName, String value) throws SQLException;

	/* Get the OID of the last object created */
	protected abstract int getLastId (DatabaseSession dbSess) throws SQLException;

	protected String getSelectSql(String columnName){

		return " select * from " + getTableName() + " where " + columnName + "=?";
	}

	protected String getSelectSql() {
		return "select * from " + getTableName() ;
	}

	protected String getCursorQuerySql () {
		return "select " + this.getPrimaryColumn() + " from " + getTableName();
	}

	protected String getSelectCountSql() {
		return "select count(*) from " + getTableName() ;
	}

    protected String getDeleteSql (QueryFilter filter) {
        return "delete from " + this.getTableName() + " where " + filter.toString();
    }
	public static final String VERSION_NUMBER = "version_number";


	protected String getBooleanAsString (Boolean b) {
		return b == null ? " " : (b.booleanValue () ? "T" : "F");
	}

	protected Boolean getStringAsBoolean (String s) {
		return s != null && s.equals ("T") ? Boolean.TRUE :
			s != null && s.equals ("F") ?Boolean.FALSE : null;
	}

	protected boolean endDates = false;

	void trace (String text) { QueryTracer.trace(this, text); }

	class QueryResult {
		ResultSet rs;
		Statement stmt;
		QueryResult (ResultSet rs, Statement stmt) {
			this.rs = rs; this.stmt = stmt;
		}

		void close () throws SQLException {
			rs.close ();
			stmt.close ();
			trace("finished");
		}
	}
}
