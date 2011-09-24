package org.ephman.abra.tools;

/** assertions
 */
import java.util.Vector;

public class Checks {

	/** check not null or empty */
	public static  boolean exists (String foo) {
		return foo != null && foo.length () > 0;
	}

	public static boolean hasElements (Vector v) {
		return v != null & v.size () >0;
	}


}
