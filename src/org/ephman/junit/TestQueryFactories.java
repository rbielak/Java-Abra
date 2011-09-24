package org.ephman.junit;

import org.ephman.junit.generated.Author;
import org.ephman.junit.generated.BookData;
import org.ephman.abra.database.ComparisonFilter;
import org.ephman.abra.database.QueryTracer;
import org.ephman.abra.database.DatabaseCursor;

import java.util.Vector;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: richieb
 * Date: Nov 6, 2003
 * Time: 6:51:53 PM
 * To change this template use Options | File Templates.
 */
public class TestQueryFactories extends DatabaseTest {

    public TestQueryFactories () {
        super ("TestQueryFactories");
    }

    protected BookDataFactory bdf;

    protected void setUp() throws Exception {
        super.setUp();
        bdf = BookDataFactory.getInstance();
    }

    public void testRetrieval () throws Throwable {
        Vector authors = getAuthors();

		//        QueryTracer.setTraceLevel(1);
        // retrieve book data for each author
        for (int i = 0; i < authors.size(); i++) {
            Author auth = (Author)authors.get(i);
            Vector books = bdf.getBooksByLastName(dbSess, auth.getLastName());
            System.out.println("author: " + auth.getLastName() + " has books:" + books.size());
            for (int n = 0; n < books.size(); n++) {
                BookData bd = (BookData)books.get(n);
                assertTrue ("correct name", auth.getLastName().equals(bd.getLastName()));
                assertTrue ("has id", bd.getOid() != 0);
            }
        }
    }

    private Vector getAuthors() throws SQLException {
        // Get all authors first
        Vector authors = AuthorFactory.getInstance().getAll (dbSess);
        assertTrue ("has authors", authors.size() > 0);
        return authors;
    }

    public void testOidRetrieval () throws Throwable {
        Vector all = bdf.getAll(dbSess);
        assertTrue ("had book data", all.size() > 0);
        // This will clear up internal caches
        dbSess.startTransaction();
        dbSess.commitTransaction();

        for (int i = 0; i < all.size(); i++) {
            BookData oldBd = (BookData)all.get(i);
            BookData newBd = bdf.getByOid(dbSess, oldBd.getOid());
            assertTrue ("same item retrieved", newBd.getOid() == oldBd.getOid());
        }
    }

    public void testCursor () throws Throwable {
        DatabaseCursor cursor = bdf.getBookData(dbSess, null);
        assertTrue ("has data", cursor.getHitCount() > 0);
        // now try to get chunks of data
        Vector v = cursor.getObjects(dbSess, 0, 1);
        assertTrue ("got object", v.size() > 0);
        int oldOid = ((BookData)v.get(0)).getOid();
        v = cursor.getObjects(dbSess, 2,3);
        assertTrue ("got object", v.size() > 0);
        int newOid = ((BookData)v.get(0)).getOid();
        assertTrue ("different object", newOid != oldOid);
    }


}
