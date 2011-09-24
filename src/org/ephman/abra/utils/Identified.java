
/**
 * Title:        <p>
 * Description:  <p>
 * Copyright:	Copyright (c) 2001 Paul Bethe and Richie Bielak<p>
 * @author Paul M Bethe & Richie Bielak
 * @version 1.0
 */
package org.ephman.abra.utils;

import org.ephman.utils.*;

public abstract class Identified implements Cloneable {

	/** Set object's ID */
	public abstract void setOid (int v);

	/** Get object's ID */
	public abstract int getOid ();

	protected Identified () {
		_locked = false;
	}

	private boolean _locked;
	
	public void setLocked () { this._locked = true; } // for circular refresh when locking.

	public boolean isLocked () { return this._locked; } // for circular refresh when locking.


	public Object clone () throws CloneNotSupportedException {
		Identified result = (Identified)super.clone ();
		result.setOid (0);
		_locked = false;
		return result;
	}

}
