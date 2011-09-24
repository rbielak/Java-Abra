package org.ephman.junit;

import org.ephman.junit.generated.*;
import org.ephman.abra.database.*;

/** a different author factory to use stored procs..
 */

public class AuthorProcsFactory extends AbstractAuthorFactory {

	private static AuthorProcsFactory theInstance = null;

	public static AuthorProcsFactory getInstance () {
		if (theInstance == null) {
			synchronized (AuthorProcsFactory.class) {
				if (theInstance == null)
					theInstance = new AuthorProcsFactory ();
			}
		}
		return theInstance;
	}

	// override to test procs..
	protected boolean useStoredProcs () { return true; } 

	public Author getByLastName (DatabaseSession dbSess, String lastName) throws Exception {
		return (Author) getObject (dbSess, this.lastName, lastName);
	}


}
