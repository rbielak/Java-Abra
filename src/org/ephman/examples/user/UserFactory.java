package org.ephman.examples.user;

import java.sql.*;
import org.ephman.abra.database.*;
import org.ephman.abra.utils.*;
import org.ephman.examples.user.generated.*;

public class UserFactory extends AbstractUserFactory {
	
	private static UserFactory theInstance;

	public static synchronized UserFactory getInstance () {
		if (theInstance == null)
			theInstance = new UserFactory ();
		return theInstance;
	}

	public User getByUserId (DatabaseSession dbSess, String userId) throws SQLException {
		return (User)super.getObject (dbSess, super.userId, userId);
	}

	protected void preStorage (DatabaseSession dbSess, Identified item) throws SQLException {
		User user = (User)item;
		if (user.getCompany () != null) {
			CompanyFactory.getInstance().store (dbSess, user.getCompany ());
		}
	}
	

	protected void deepRetrieval (DatabaseSession dbSess, Identified item) throws SQLException {
		User user = (User)item;
		user.setCompany(CompanyFactory.getInstance().getByOid (dbSess, user.getCompanyOid()));
	}

}
