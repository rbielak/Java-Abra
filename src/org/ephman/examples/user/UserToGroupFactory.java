package org.ephman.examples.user;

import org.ephman.examples.user.generated.*;
import org.ephman.abra.database.*;
import java.sql.SQLException;

public class UserToGroupFactory extends AbstractUserToGroupFactory {

	private UserToGroupFactory () {
	}

	static UserToGroupFactory theInstance = null;
	public static UserToGroupFactory getInstance () { 
		if (theInstance == null) 
			synchronized (UserToGroupFactory.class) {
				if (theInstance == null) // still null
					theInstance = new UserToGroupFactory ();
			}
		return theInstance;
	}

	public void addRelationship (DatabaseSession dbSess, User user, Group group) throws SQLException {
		super.addRelationship (dbSess, makeInsertString (), user.getOid (), group.getOid ());
	}

}
