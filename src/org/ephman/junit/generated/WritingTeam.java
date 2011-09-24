package org.ephman.junit.generated;

import java.sql.*;
import java.util.*;

// Do not edit!! generated classes
/** WritingTeam <p>
 * XMLSource: /home/richieb/MyDevelopment/Abra-0.9/src/org/ephman/junit/test.xml
 * @version Thu Jun 30 15:12:42 EDT 2005
 * @author generated by Dave Knull
 */

public class WritingTeam extends org.ephman.abra.utils.Versioned implements java.io.Serializable { 

	private int _oid;

	public int getOid () { return this._oid; }

	public void setOid (int foo) {
		this._oid = foo;
	}

	private String _mastersName;

	public String getMastersName () { return this._mastersName; }

	public void setMastersName (String foo) {
		this._mastersName = foo;
	}

	private org.ephman.junit.generated.Author _master;

	public org.ephman.junit.generated.Author getMaster () { return this._master; }

	public void setMaster (org.ephman.junit.generated.Author foo) {
		this._master = foo;
	}

	private String _apprenticeName;

	public String getApprenticeName () { return this._apprenticeName; }

	public void setApprenticeName (String foo) {
		this._apprenticeName = foo;
	}

	private org.ephman.junit.generated.Author _apprentice;

	public org.ephman.junit.generated.Author getApprentice () { return this._apprentice; }

	public void setApprentice (org.ephman.junit.generated.Author foo) {
		this._apprentice = foo;
	}


	/** the Default constructor for WritingTeam
	* use set... methods to fill in fields
	*/
	public WritingTeam () {
		super ();
		_oid = 0;
		_mastersName = "";
		_master = null;
		_apprenticeName = "";
		_apprentice = null;
	}

} // end of WritingTeam