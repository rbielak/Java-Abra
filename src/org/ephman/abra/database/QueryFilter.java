package org.ephman.abra.database;

/**
 * Title:			Query Filter <p>
 * Description:  	The base of all filters - which represent the 'where' 
 * clause in an SQL query <p>
 * Copyright:	Copyright (c) 2001 Paul Bethe and Richie Bielak<p>
 * @author		 	Paul Bethe
 * @version 0.1.0
 */


import java.sql.Timestamp;
import org.ephman.utils.*;

public abstract class QueryFilter {


	public abstract void setTableAlias (String alias);


	public abstract void addToPreparedQuery (PreparedQuery pq, String tableName);
	/* defaults to call addToPreparedQuery with null.. */ 
	public void addToPreparedQuery (PreparedQuery pq){
		addToPreparedQuery (pq, null);
	}

	public abstract String toString (String tableName);

	protected String valOf (Object value) {
		String v = value != null ? value.toString () : "null";
		if (value instanceof String)
        {
            int start_index = v.indexOf("'") ;
            int times = 0;
            if ( start_index!=-1 )
            {
                StringBuffer sb = new StringBuffer(v);
                sb.insert(start_index,"'");
                while( (start_index = v.indexOf("'",start_index+1))!=-1)
                {
                    times++;
                    sb.insert(start_index +times,"'" );
                }
                v = sb.toString();
            }
			v = "'" + v + "'";
        }
        else if (value instanceof Timestamp) {
            String dt = TimestampConverter.format ((Timestamp)value);
            v = "TO_DATE('" + dt + "', 'YYYY-MM-DD HH24:MI:SS')";
        }
		return v;
	}

	protected String computeAlias (String tableAlias, String tableName) {
		String ali = tableAlias == null ? (tableName == null ? "" : tableName) : tableAlias;
		if (ali.length () > 0) ali += ".";
		return ali;
	}
}
