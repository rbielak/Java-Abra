package org.ephman.junit;

import junit.framework.TestCase;
import org.ephman.abra.database.*;
import org.ephman.abra.tools.FactoryGenerator;
import org.ephman.junit.generated.*;
import java.sql.*;
import java.io.*;
import java.util.*;

/** a test to try some query filters..
 * @author Paul Bethe
 */

public  class TestFilters extends DatabaseTest {



	public TestFilters (String name) {
		super (name);
	}

	public void testComparison () throws Throwable {
		dbSess.startTransaction ();

		System.out.println ("testing filters.. ");
		Vector v = authorFactory.queryObjects (dbSess, new ComparisonFilter (authorFactory.lastName, " like ", "A%"));
		assertTrue ("found 2", v.size () == 2);

		Author a0 = (Author)v.elementAt (0);
		Author a1 = (Author)v.elementAt (1);


		v = bookFactory.queryObjects (dbSess, new ComparisonFilter (bookFactory.Isbn, "<>", "77"));
		assertTrue ("found at least 3 books (by isbn)", v.size () > 2);
		assertTrue ("author " + a0.getLastName () +" has a book",
					a0.getTitles().size() >0);		
		assertTrue ("got a book by (Anthony | Adams)",
					v.contains (a0.getTitles().elementAt(0)));

		v = bookFactory.queryObjects (dbSess, new NotNullFilter (bookFactory.authorOid));
		assertTrue ("found at least 3 books (w/ not null)", v.size () > 2);

		int [] oids = new int[3];
		for (int i=0; i <3; i++)
			oids[i] = ((Book)v.elementAt(i)).getOid ();
		
		QueryFilter qf = new AndFilter (new IsNullFilter (bookFactory.publisherOid), new SetFilter (bookFactory.oid, oids));
		v = bookFactory.queryObjects (dbSess, qf);
		assertTrue ("found all 3 books (w/ is null Publisher && set filter)", v.size () == 3);

		Vector args = new Vector ();
		args.addElement ("425532");
		String sqlQ = bookFactory.Isbn + "=?";
		v = bookFactory.queryObjects (dbSess, new PreparedFilter (sqlQ, args));
		assertTrue ("found source of magic.. ", v.size() == 1);
		Book source = (Book)v.elementAt(0);		
		assertTrue ("clob text returned", source.getText ().length () > 0);
		//		System.out.println (source.getText ());

		/*		
			qf = new LimitQuery (qf, 1);
			
			System.out.println (qf.toString(null));
			v = bookFactory.queryObjects (dbSess, qf);
			assertTrue ("found only 1 book when limit in place", v.size () == 1);
		*/
		qf = new ComparisonFilter (authorFactory.lastName, " = ", 
								   "Adams");
		qf = new ComparisonFilter (bookFactory.authorOid, "=", 
								   new SubSelectFilter (authorFactory.oid, 
												 authorFactory.getTableName (),
														qf));
		QueryTracer.setTraceLevel (1);
		v = bookFactory.queryObjects (dbSess, qf);
		assertTrue ("found 1 book using subselect", v.size () == 1);

		/** Richie wants to do the following..
			select * from (select * from <tab> order by oid limit 10) order by <other>
		*/
		SortCriteria oidSc = new SortCriteria (bookFactory.oid, true);
		SortCriteria lnameSc = new SortCriteria (bookFactory.Isbn, false);
		qf = new LimitQuery (null, 2);
		
		// not implemented for Sybase and SQLServer
		if (!FactoryGenerator.DB_NAME.equals("Sybase") && !FactoryGenerator.DB_NAME.equals("SQLServer")) {
		    qf = new WrapperFilter (qf, null, null, lnameSc);
		    qf.setTableAlias ("bar");
		    System.out.println (qf.toString("bar"));
		    v = bookFactory.querySorted (dbSess, qf, oidSc);
		    assertTrue ("two items returned", v.size () == 2);
		}
		QueryTracer.setTraceLevel (0);
		dbSess.commitTransaction ();
	}


	AuthorFactory authorFactory;
	BookFactory bookFactory;


	public void createAuthors () throws Exception {
		authorFactory = AuthorFactory.getInstance ();
		bookFactory = BookFactory.getInstance ();
		Catalog c = (Catalog)marshaller.unmarshal (new FileReader 
												   (xmlDirectory + "/" +
													"feed_books.xml"));
		Vector authors = c.getAuthors ();		
		for (int i=0; i< authors.size (); i++) {
			Author a = (Author)authors.elementAt(i);
			if (null == authorFactory.getByLastName (dbSess,
													 a.getLastName())) {
				System.out.println ("Stored author\n" + marshaller.marshal (a));
				authorFactory.store (dbSess, a);	
			}
		}

		Vector books = c.getBooks ();		
		for (int i=0; i< books.size (); i++) {
			Book a = (Book)books.elementAt(i);
			if (null == bookFactory.getByTitle (dbSess, a.getTitle())) {
			    QueryTracer.setTraceLevel(0);
				bookFactory.store (dbSess, a);
			}
		}

	}

	/** get the connection to the db 
	 */
	protected void setUp () throws Exception {
		super.setUp ();		
		try {
		    System.out.println ("---- start transaction");
		    dbSess.startTransaction ();
		
		    createAuthors ();
		    System.out.print("-------- Commiting");
		    dbSess.commitTransaction ();
		    System.out.println(".... done");
		}
		catch (Exception e) {
		    dbSess.rollbackTransaction();
		    e.printStackTrace();
		    throw e;
		}
	}

	protected void tearDown () throws Exception {
		//		dbSess.commitTransaction ();
		super.tearDown ();
	}

}
