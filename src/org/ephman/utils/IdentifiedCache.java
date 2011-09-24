
/**
 * Title:        IdentifiedCache<p>
 * Description:  uses the oid in cache as the primary key<p>
 * Copyright:	Copyright (c) 2001 Paul Bethe and Richie Bielak<p>
 * @author Paul Bethe & Richie Bielak
 * @version 1.0
 */
package org.ephman.utils;

import java.util.*;
import org.ephman.abra.utils.*;

public class IdentifiedCache extends RandomCache {

	public IdentifiedCache(int size) {
		super (size);
	}

	public Identified get (int oid) {
		return (Identified)super.get (new Integer (oid));
	}

	public void put (Identified i) {
		super.put (new Integer (i.getOid ()), i);
	}

}
