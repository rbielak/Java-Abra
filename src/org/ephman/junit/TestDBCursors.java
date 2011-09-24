/*
 * Created on Mar 23, 2004
 *
 */
package org.ephman.junit;

import java.util.Vector;

import org.ephman.abra.database.DatabaseCursor;
import org.ephman.abra.database.SortAndLimitCriteria;
import org.ephman.junit.generated.Author;
import org.ephman.junit.generated.Book;


/**
 * @author richieb
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class TestDBCursors extends DatabaseTest {

    /**
     * 
     */
    public TestDBCursors() {
        super("TestDBCursors");
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#runTest()
     */
    protected void runTest() throws Throwable {
        testCursors();
    }
    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    public void testCursors () throws Throwable {
        // Create 150 entries in the database that we can query about
        dbSess.startTransaction();
        Author auth = new Author ();
        auth.setLastName("Foo");
        auth.setFirstName("Bar");
        for (int i = 0; i < 150; i++) {
            Book bk = new Book();
            bk.setTitle("Book Of Foo: " + i);
            bk.setAuthor(auth);
            BookFactory.getInstance().store(dbSess, bk);
        }
        dbSess.commitTransaction();
        // now use a cursor to get the books
        DatabaseCursor cursor = BookFactory.getInstance().cursorQuery(dbSess, null, null);
        assertTrue ("cursor right size returned", cursor.getHitCount() >= 150);
        
        Vector books = cursor.getObjects(dbSess, 0, 100);
        assertTrue ("correct number ", books.size() == 100);
        for (int i = 0; i < books.size(); i++) {
            Book bk = (Book)books.get(i);
            assertTrue ("book there", (bk != null) && (bk.getTitle() != null));
        }
        Book last = (Book)books.get(books.size() - 1);
        books = cursor.getObjects(dbSess, 101, 150);
        Book first = (Book)books.get(0);
        assertTrue ("no overlap", last.getOid() != first.getOid ());
        
        // now try a query with a limit criteria to make sure that
        // right number of items are returned
        SortAndLimitCriteria snl = new SortAndLimitCriteria(BookFactory.getInstance().title, true, 10);
        cursor = BookFactory.getInstance().cursorQuery(dbSess, null, snl);
        // make sure we got 10 elements
        assertTrue ("got " + cursor.getHitCount() + " items", cursor.getHitCount() == 10);
        
        
    }
    
    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
