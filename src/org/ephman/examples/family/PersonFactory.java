package org.ephman.examples.family;

import java.sql.*;
import org.ephman.abra.database.*;
import org.ephman.abra.utils.*;
import org.ephman.examples.family.generated.*;

public class PersonFactory extends AbstractPersonFactory {
	
	private static PersonFactory theInstance;

	public static synchronized PersonFactory getInstance () {
		if (theInstance == null)
			theInstance = new PersonFactory ();
		return theInstance;
	}

	/** this method describes what must be retrived for completeness*/
	protected void deepRetrieval (DatabaseSession dbSess, Identified item) throws SQLException {
		Person p = (Person)item;
		p.setFather (getByOid (dbSess, p.getFatherOid ()));
		p.setMother (getByOid (dbSess, p.getMotherOid ()));
	}

	public Person getByAge (DatabaseSession dbSess, int age) throws SQLException {
		return (Person)super.getObject (dbSess, super.age, age);
	}

}
