package org.ephman.abra.tools;

import java.util.*;
import java.text.SimpleDateFormat;

/** a class to hold all marshalling context info
 * like recursive links - and next unique number etc.
 * @author Paul M. Bethe
 * @version 0.0.1 (11/4/00)
*/


public class MarshalContext {

    private int depth;

    private int nextId;

    private HashMap compositeEls;

	private SimpleDateFormat formatter;

    public MarshalContext (String dateFormat) {
		this ();
		if (dateFormat != null && !dateFormat.equals("")) {
			formatter = new SimpleDateFormat ();
			formatter.setTimeZone (java.util.TimeZone.getTimeZone("GMT"));
			formatter.applyPattern (dateFormat);
		}		
	}

    public MarshalContext () {
        depth=0;
        nextId=0;
        compositeEls = new HashMap ();
    }

	public SimpleDateFormat getDateFormat () {
		return formatter;
	}

    private int getNextId () { return nextId++; }

    public Integer get (Object o) {
        return (Integer)compositeEls.get(o);
    }

    public int put (Object key) {
        Integer i = new Integer (getNextId ());
        compositeEls.put(key, i);
        return i.intValue();
    }

    public void addDepth () { depth++; }

    public void removeDepth () { depth--; }


    public String getIndent () {
        String result = "";
        for (int i = 0; i < depth; i++)
            result += "\t";
        return result;
    }
}

