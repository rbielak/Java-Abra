package org.ephman.junit;

import org.ephman.junit.generated.AbstractBookDataFactory;
import org.ephman.abra.database.QueryFilter;
import org.ephman.abra.database.DatabaseSession;
import org.ephman.abra.database.ComparisonFilter;
import org.ephman.abra.database.DatabaseCursor;

import java.util.Vector;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: richieb
 * Date: Nov 6, 2003
 * Time: 6:41:40 PM
 * To change this template use Options | File Templates.
 */
public class BookDataFactory extends AbstractBookDataFactory {

    private static BookDataFactory theInstance = null;

    public static BookDataFactory getInstance () {
        if (theInstance == null) {
            synchronized (BookDataFactory.class) {
                if (theInstance == null)
                    theInstance = new BookDataFactory();
            }
        }
        return theInstance;
    }

    public Vector getBooksByLastName (DatabaseSession sess, String lname) throws SQLException {
        ComparisonFilter cf = new ComparisonFilter (super.lastName, "=", lname);
        return queryObjects (sess, cf);
    }

    public DatabaseCursor getBookData (DatabaseSession sess, QueryFilter filter) throws SQLException {
        DatabaseCursor cursor = super.cursorQuery(sess, filter);
        return cursor;
    }

    public Vector getAll (DatabaseSession sess) throws SQLException {
        return super.queryAll(sess);
    }


}
