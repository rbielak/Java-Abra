package org.ephman.abra.tools;

import java.util.*;
import java.text.SimpleDateFormat;

/** a class to hold all marshalling context info
 * like recursive links - and next unique number etc.
 * @author Paul M. Bethe
 * @version 0.0.1 (11/4/00)
*/


public class UnmarshalContext {

    private HashMap compositeEls;
	public boolean ignoreMissingFields;
	private SimpleDateFormat formatter;

    //public UnmarshalContext () {
	//	this (true); // default to true..
    //}

    public UnmarshalContext (boolean ignoreMissingFields) {
        compositeEls = new HashMap ();
		this.ignoreMissingFields = ignoreMissingFields;
		formatter = new SimpleDateFormat ();
		formatter.setTimeZone (java.util.TimeZone.getTimeZone("GMT"));
	}

	public SimpleDateFormat getDateFormat () {
		return formatter;
	}

    public Object get (Integer i) {
        return compositeEls.get (i);
    }

    public void put (Integer key, Object obj) {
        compositeEls.put (key, obj);
    }

}
