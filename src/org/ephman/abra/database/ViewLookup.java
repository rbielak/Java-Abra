package org.ephman.abra.database;

import java.lang.reflect.*;
import org.ephman.abra.utils.AbraRuntimeException;

/** A class to wrap a sql join and the method to rip a new view object
 *
 * @author Paul M. Bethe
 * @version 0.0.1 (1/27/01)
 */

public class ViewLookup {

    Method ripMethod;

    String sqlLookup;

    /* the factory that this lookup belongs to */
    FactoryBase factory;

    public FactoryBase getFactory () { return factory; }

	/** provide the factories with a better way of exception handling so 
	 * that they don't have to throw exception 
	 */
	public static Method getRipMethod (String ripperName, FactoryBase factory) 
	{
		try{
			Method m = factory.getClass ().getMethod (ripperName, new Class[]{java.sql.ResultSet.class});
			return m;
		} catch (NoSuchMethodException e) {
			throw new AbraRuntimeException ("ephman.abra.database.badfactinit", new Object[]{factory.getClass().getName (), e.getMessage ()}, e);
		}

	}

	/** use ripperName and factory to get the Method using reflection
	 * then call ViewLookup (String, Method, FactoryBase)
	 */
	public ViewLookup (String sql, String ripperName, FactoryBase factory) {
		this (sql, getRipMethod (ripperName, factory), factory);
	}

	/** create a view lookup to execute and rip correctly
	 * @param sql the query string which build the lookup 
	 * @param ripper the method which can take a result set row and return
	 *    an object
	 * @param factory the factory base on which to perform the method
	 */
    public ViewLookup (String sql, Method ripper, FactoryBase factory) {
        ripMethod = ripper;
        sqlLookup = sql;
        this.factory = factory;
    }

    public Method getExtractMethod () {
        return ripMethod;
    }

    public String getSqlString () {
        return sqlLookup;
    }

}
