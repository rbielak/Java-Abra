package org.ephman.junit.generated;

import java.sql.*;
import java.util.*;

// Do not edit!! generated classes
/** Book <p>
 * XMLSource: /home/richieb/MyDevelopment/Abra-0.9/src/org/ephman/junit/test.xml
 * @version Thu Jun 30 15:12:42 EDT 2005
 * @author generated by Dave Knull
 */

public class Book extends org.ephman.abra.utils.Versioned implements java.io.Serializable { 

	private String _title;

	public String getTitle () { return this._title; }

	public void setTitle (String foo) {
		this._title = foo;
	}

	private String _text;

	public String getText () { return this._text; }

	public void setText (String foo) {
		this._text = foo;
	}

	private org.ephman.junit.generated.Publisher _publisher;

	public org.ephman.junit.generated.Publisher getPublisher () { return this._publisher; }

	public void setPublisher (org.ephman.junit.generated.Publisher foo) {
		this._publisher = foo;
		this._publisherOid = foo != null ? foo.getOid () : 0; // set the oid to zero if object == null
	}

	private int _publisherOid = 0;

	public int getPublisherOid () { return this._publisherOid; }

	public void setPublisherOid (int foo) {
		this._publisherOid = foo;
	}

	private int _oid;

	public int getOid () { return this._oid; }

	public void setOid (int foo) {
		this._oid = foo;
	}

	private org.ephman.junit.generated.Author _author;

	public org.ephman.junit.generated.Author getAuthor () { return this._author; }

	public void setAuthor (org.ephman.junit.generated.Author foo) {
		this._author = foo;
		this._authorOid = foo != null ? foo.getOid () : 0; // set the oid to zero if object == null
	}

	private int _authorOid = 0;

	public int getAuthorOid () { return this._authorOid; }

	public void setAuthorOid (int foo) {
		this._authorOid = foo;
	}

	private String _Isbn;

	public String getIsbn () { return this._Isbn; }

	public void setIsbn (String foo) {
		this._Isbn = foo;
	}

	public void initializeFromView (org.ephman.junit.generated.EditBook view) {
		this.setTitle (view.getTitle ());
		if (this.getAuthor() != null && view.getAuthor() != null)
			this.getAuthor().initializeFromView (view.getAuthor());
	}

	public org.ephman.junit.generated.EditBook createEditView () {
		HashMap viewContext = new HashMap ();
		return this.createEditView (viewContext);
	}

	public org.ephman.junit.generated.EditBook createEditView (HashMap _viewContext) {
		org.ephman.junit.generated.EditBook foo = (org.ephman.junit.generated.EditBook)_viewContext.get (this);
		if (foo == null) { //not found
			foo = new org.ephman.junit.generated.EditBook ();
			_viewContext.put (this, foo);
			this.createEditView (foo, _viewContext);
		}
		return foo;
	}

	protected void createEditView (org.ephman.junit.generated.EditBook foo, HashMap _viewContext) {
		foo.setTitle (this.getTitle());
		foo.setAuthor (this.getAuthor() == null ? null : (org.ephman.junit.generated.EditAuthor)this.getAuthor().createEditView (_viewContext));
	}


	/** the Default constructor for Book
	* use set... methods to fill in fields
	*/
	public Book () {
		super ();
		_title = "";
		_text = "";
		_publisher = null;
		_oid = 0;
		_author = null;
		_Isbn = "";
	}

} // end of Book
