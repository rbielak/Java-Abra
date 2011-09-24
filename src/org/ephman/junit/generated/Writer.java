package org.ephman.junit.generated;

import java.sql.*;
import java.util.*;

// Do not edit!! generated classes
/** Writer <p>
 * XMLSource: /home/richieb/MyDevelopment/Abra-0.9/src/org/ephman/junit/test.xml
 * @version Thu Jun 30 15:12:42 EDT 2005
 * @author generated by Dave Knull
 */

public class Writer extends org.ephman.abra.utils.Versioned implements java.io.Serializable { 

	private boolean _warVeteran;

	public boolean getWarVeteran () { return this._warVeteran; }

	public void setWarVeteran (boolean foo) {
		this._warVeteran = foo;
	}

	private Boolean _warHero;

	public Boolean getWarHero () { return this._warHero; }

	public void setWarHero (Boolean foo) {
		this._warHero = foo;
	}

	private java.util.Vector _titles;

	public java.util.Vector getTitles () { return this._titles; }

	public void setTitles (java.util.Vector foo) {
		this._titles = foo;
	}

	public void addToTitles (org.ephman.junit.generated.Book foo) {
		_titles.addElement (foo);
	}

	private java.math.BigDecimal _salary;

	public java.math.BigDecimal getSalary () { return this._salary; }

	public void setSalary (java.math.BigDecimal foo) {
		this._salary = foo;
	}

	private org.ephman.junit.generated.Publisher [] _publishers;

	public org.ephman.junit.generated.Publisher [] getPublishers () { return this._publishers; }

	public void setPublishers (org.ephman.junit.generated.Publisher [] foo) {
		this._publishers = foo;
	}

	public void addToPublishers (org.ephman.junit.generated.Publisher foo) {
		 /* This does not work */ 
	}

	private int _oid;

	public int getOid () { return this._oid; }

	public void setOid (int foo) {
		this._oid = foo;
	}

	private String _lastName;

	public String getLastName () { return this._lastName; }

	public void setLastName (String foo) {
		this._lastName = foo;
	}

	private String _firstName;

	public String getFirstName () { return this._firstName; }

	public void setFirstName (String foo) {
		this._firstName = foo;
	}

	private Timestamp _birthday;

	public Timestamp getBirthday () { return this._birthday; }

	public void setBirthday (Timestamp foo) {
		this._birthday = foo;
	}

	public void initializeFromView (org.ephman.junit.generated.EditWriter view) {
		this.setFirstName (view.getFirstName ());
	}

	public org.ephman.junit.generated.EditWriter createEditView () {
		HashMap viewContext = new HashMap ();
		return this.createEditView (viewContext);
	}

	public org.ephman.junit.generated.EditWriter createEditView (HashMap _viewContext) {
		org.ephman.junit.generated.EditWriter foo = (org.ephman.junit.generated.EditWriter)_viewContext.get (this);
		if (foo == null) { //not found
			foo = new org.ephman.junit.generated.EditWriter ();
			_viewContext.put (this, foo);
			this.createEditView (foo, _viewContext);
		}
		return foo;
	}

	protected void createEditView (org.ephman.junit.generated.EditWriter foo, HashMap _viewContext) {
		foo.setFirstName (this.getFirstName());
	}


	/** the Default constructor for Writer
	* use set... methods to fill in fields
	*/
	public Writer () {
		super ();
		_warVeteran = false;
		_warHero = null;
		_titles = new Vector ();
		_salary = null;
		_publishers = null;
		_oid = 0;
		_lastName = "";
		_firstName = "";
		_birthday = null;
	}

} // end of Writer