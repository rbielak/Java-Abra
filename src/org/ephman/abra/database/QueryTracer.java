
/**
 * Title:        QueryTracer<p>
 * Description:  Class to print out info about database queries. Used for debugging<p>
 * @author 	 Richie Bielak
 * @version 1.0
 */
package org.ephman.abra.database;

import java.util.Date;


public class QueryTracer {

	/** Level 0 no trace, level 1 trace */
	private static int traceLevel = 0;

	private static String currentTime () {
		Date now = new Date ();
		return now.toString ();
	}

	/** Set query trace level */
	public static void setTraceLevel (int lev) {
		traceLevel = lev;
	}


	/** Trace rotuine. Will print stuff if level is greater than 0 */
	public static void trace (GenericFactoryBase fact, String msg, int oid) {
		trace (fact, msg + " (oid="+oid+")");
	}

	public static void trace (GenericFactoryBase fact, String msg, PreparedQuery pq) {
		if (pq != null) msg += pq.getArguments().toString ();
		trace (fact, msg);
	}
	public static void trace (GenericFactoryBase fact, String msg) {
		if (traceLevel > 0) {
			System.out.println("<" + currentTime() + ">:" +
					fact.getClass().getName() + ":" + msg);
		}
	}


}
