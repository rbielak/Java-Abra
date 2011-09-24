
/**
 * Title:        Weak Cache <p>
 * Description:  to remove objects that are only weakly referenced<p>
 * Copyright:	Copyright (c) 2001 Paul Bethe and Richie Bielak<p>
 * @author    PR
 * @version 1.0
 */
package org.ephman.utils;

import java.util.*;
import java.lang.ref.*;
import org.ephman.abra.utils.*;

public class WeakCache {

	private HashMap hm;


	public WeakCache () {
		hm = new HashMap ();
	}

	public void put (Identified i) {
		WeakReference wr = new WeakReference (i);
		hm.put (new Integer(i.getOid ()), wr);
		// i.setCache (this);
	}

	public Identified get (int oid) {
		WeakReference result = (WeakReference)hm.get (new Integer (oid));
		return result == null ? null : (Identified)result.get();
	}

	public void remove (Identified i) {
		//System.out.println ("REMOVING - " + i +" from cache");
		WeakReference wr = (WeakReference)hm.remove (new Integer (i.getOid ()));
        //if (wr == null)
            //System.out.println ("Error wr object not found == null!");

	}
	public void remove ( int OID) {
		//System.out.println ("REMOVING - " + i +" from cache");
		WeakReference wr = (WeakReference)hm.remove (new Integer (OID));
        //if (wr == null)
          //  System.out.println ("Error wr object not found == null!");

	}

}
