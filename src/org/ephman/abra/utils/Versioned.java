
/**
 * Title:        <p>
 * Description:  <p>
 * Copyright:	Copyright (c) 2001 Paul Bethe and Richie Bielak<p>
 * @author Paul M Bethe & Richie Bielak
 * @version 1.0
 */
package org.ephman.abra.utils;

public abstract class Versioned extends Identified implements java.io.Serializable {

	private int versionNum;

	/** Set object's Version */
	public void setVersion (int v) { this.versionNum = v; }

	/** Get object's Version */
	public int getVersion () {
		return this.versionNum;
	}

	public Versioned () {
		super ();
		this.versionNum = 0;
	}

	/* invalid versioned objects have this state.. */
	public static final int INVALID = -42;

}
