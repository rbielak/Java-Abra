package org.ephman.junit;

import org.ephman.junit.generated.*;
import org.ephman.abra.database.*;
import org.ephman.abra.utils.Identified;
import java.sql.*;
import java.util.Vector;

public class AuthorFactory extends AbstractAuthorFactory {

	private static AuthorFactory theInstance = null;

	public static AuthorFactory getInstance () {
		if (theInstance == null) {
			synchronized (AuthorFactory.class) {
				if (theInstance == null)
					theInstance = new AuthorFactory ();
			}
		}
		return theInstance;
	}

		// override to test procs..
	protected boolean useStoredProcs () { return false; } 


	// get the authors books.
	protected void deepRetrieval (DatabaseSession dbSess, Identified item) throws SQLException
	{
		Author a = (Author)item;
		//	System.out.println ("Setting titles for " + a.getLastName ());
		a.setTitles (BookFactory.getInstance ().getByAuthor (dbSess, a));
	}


	public Author getByLastName (DatabaseSession dbSess, String lastName) throws SQLException {
		return (Author) getObject (dbSess, this.lastName, lastName);
	}

    public Vector getAll (DatabaseSession dbSess) throws SQLException {
        return super.queryAll(dbSess);
    }


}
