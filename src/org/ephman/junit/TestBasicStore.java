package org.ephman.junit;

import java.sql.Timestamp;

import org.ephman.junit.generated.*;

/**
 * a test to insert some basic items
 * @author PR
 */

public  class TestBasicStore extends DatabaseTest {
	
	public TestBasicStore (String name) {
		super (name);
	}

	AuthorFactory authFact;

	BookFactory bookFact;

	Author auth;

	Book book;

	

	protected void setUp () throws Exception {
		super.setUp ();
		authFact = AuthorFactory.getInstance ();
		bookFact = BookFactory.getInstance ();
		checkStore ();
		deepStore ();
	}

	// Store a simple object with no references to other objects
	public void checkStore () throws Exception {
	    try {
	        dbSess.startTransaction ();
	        // See if the Author already exists
	        auth = authFact.getByLastName (dbSess, "Tolstoy");
	        if (auth == null) {
	            System.out.println ("Tolstoy not found...creating...");
	            auth = new Author ();
	            auth.setFirstName ("Leo");
	            auth.setLastName ("Tolstoy");
	            auth.setSalary (new java.math.BigDecimal (42.000));
	            auth.setWarHero (Boolean.TRUE);
	            //			auth.setBirthday (new Timestamp(System.currentTimeMillis() - 100 * 364 * 24 * 60 * 60));
	            authFact.store (dbSess, auth);
	        }
	        // Check that a cached copy is returned on a query
	        Author a2 = authFact.getByLastName (dbSess, "Tolstoy");
	        // within a transaction the same exact object should be returned
	        assertTrue ("cached version", a2 == auth);
	        // update and assert that update works..
	        authFact.store (dbSess, a2);
	        dbSess.commitTransaction ();
	        System.out.println ("Transaction committed");		
	        assertTrue ("persistent author exists", auth != null && auth.getOid () != 0);
	    } catch (Exception e) {
	        dbSess.rollbackTransaction();
	        throw e;	        
	    }
	}

	// Retrieve a simple object with no references to other objects
	public void testRetrieval () throws Throwable {
	    try {
	        dbSess.startTransaction ();
	        Author a2 = authFact.getByLastName (dbSess, "Tolstoy");
	        assertTrue ("retrieval worked", a2 != null);
	        assertTrue ("not cached copy", a2 != auth);
	        assertTrue ("same object", a2.getOid () == auth.getOid ());
	        assertTrue ("same attr", (a2.getLastName().equals (auth.getLastName ()) &&
	                a2.getFirstName ().equals (auth.getFirstName())));
	        assertTrue ("salary not null", a2.getSalary () != null
	                && a2.getSalary ().intValue() == 42);
	        assertTrue ("hero is set", a2.getWarHero () == Boolean.TRUE);
	        dbSess.commitTransaction ();
	        System.out.println ("Retrieval worked...");
	    } catch (Exception e) {
	        dbSess.rollbackTransaction();
	        throw e;
	    }
	}

	// Store an object that references other objects
	public void deepStore () throws Exception {
	    assertTrue ("have author", auth != null);
	    try {
	        dbSess.startTransaction ();
	        book = bookFact.getByTitle (dbSess, "War and Peace");
	        if (book == null) {
	            System.out.println ("Book doesn't exists ... creating");
	            book = new Book ();
	            book.setTitle ("War and Peace");
	            book.setAuthor (auth);
	            bookFact.store (dbSess, book);
	        }
	        dbSess.commitTransaction ();
	        assertTrue ("book persisted", book != null && book.getOid () != 0);
	    } catch (Exception e) {
	        dbSess.rollbackTransaction();
	        throw e;
	    }
	}

	// Retrieve an object that references other objects
	public void testDeepRetrieval () throws Throwable {
		assertTrue ("have author", auth != null);
		assertTrue ("have book", book != null);
		//dbSess.startTransaction ();
		Book b2 = bookFact.getByTitle (dbSess, "War and Peace");
		// check that all the parts were retrieved properly
		assertTrue ("has right author", b2.getAuthor ().getOid () == auth.getOid ());
		assertTrue ("not cached", b2 != book);

		//dbSess.commitTransaction ();
	}

	/*
	  public void testGMT () throws Throwable {
	  dbSess.startTransaction ();
	  
	  TimeZone.setDefault (TimeZone.getTimeZone ("GMT"));
	  String sql = "insert into testGMT VALUES (?)";
	  Connection c = super.getConnection ();
	  PreparedStatement stmt = c.prepareStatement (sql);
	  Timestamp ts = TimestampConverter.parse ("2002/05/04", "yyyy/MM/dd");
	  stmt.setTimestamp (1, ts);
		stmt.execute ();
		ResultSet rs = c.createStatement().executeQuery ("select * from testGMT");
		assertTrue ("found a date", rs.next ());
		Timestamp ts_two = rs.getTimestamp (1);
		assertTrue ("same millis", ts_two.getTime () == ts.getTime ());
		System.out.println (TimestampConverter.format (ts, "yyyy/MM/dd HH:mm.ss"));
		System.out.println ("Sys TZ=" + TimeZone.getDefault ());
		} */

}
