package org.ephman.examples.family;

/** a test script to create a family and store them then retrieve them..
 */

import org.ephman.junit.DatabaseTest;
import org.ephman.abra.database.*; // abra
import org.ephman.abra.validation.*; // abra validation pkg

import org.ephman.utils.TimestampConverter;
import org.ephman.examples.family.generated.*;
import java.util.*;
import java.io.*;

import junit.framework.*;

public class Test extends DatabaseTest {

	public static void main (String []argv) {
		try {
			Test t = new Test ();
			System.out.println (">>> Running Family test");
			junit.textui.TestRunner.run (t);
		} catch (Exception e) {
			e.printStackTrace ();
		}
		System.out.println (">>> Done with test");
	}

	public void runTest () throws Exception{
		DatabaseSession dbSess = DatabaseSessionFactory.getInstance ().createNewSession ();
		dbSess.startTransaction ();

		PersonFactory pf = PersonFactory.getInstance ();

		Person john = createPerson ("//", 45, null, null);
		john.getAddress ().setState ("NZ");
		ValidationResult vr = PersonValidator.validate (john);
		assertTrue ("invalid", !vr.getPassed ());

		john.setBirthday (TimestampConverter.parse ("1956-01-12", "yyyy-MM-dd"));
		vr = PersonValidator.validate (john);
		if (!vr.getPassed ())
			System.out.println (vr.getErrors ());
		assertTrue ("passed with fix", vr.getPassed ());
		System.out.println ("John validated");
		pf.store (dbSess, john);
		System.out.println ("John created/stored");
		john.setAge (46);
		pf.store (dbSess, john);
		System.out.println ("John modified/updated");
		Person marie = createPerson ("Marie", 42, null, null);
		marie.setBirthday (TimestampConverter.parse ("1959-01-12", "yyyy-MM-dd"));
		pf.store (dbSess, marie);
		System.out.println ("Marie created/stored");
		Person junior = createPerson ("Jr.", 18, john, marie);
		junior.setBirthday (TimestampConverter.parse ("1993-01-12", "yyyy-MM-dd"));	
		junior.getAddress ().setState ("A");

		vr = PersonValidator.validate (junior);
		assertTrue ("had errors", !vr.getPassed ());
		// this should fail use accessor to change
		Vector errors = vr.getErrors ();
		for (int i=0; i< errors.size (); i++) {
			FieldError e = (FieldError)errors.elementAt (i);
			if (e.getErrorCode () == SystemCodes.INVALID_STATE) {
				StringFieldError sf = (StringFieldError)e;
				System.out.println ("Changing " + sf.getStringValue () + " to AL");
				sf.setStringValue ("AL");
			}
			else if (e.getErrorCode () == ValidationCodes.MANDATORY_FAILED
					 && e.getFieldName().equals ("birthday")){
				
			}
			else
				assertTrue ("not expected", false);
		}
	
		vr = PersonValidator.validate (junior); // this should work
		assertTrue ("passed", vr.getPassed ());
		junior.setBirthday (null);
		System.out.println ("Jr. validated");
		pf.store (dbSess, junior);
		System.out.println ("Jr. created/stored");
		dbSess.commitTransaction ();
		dbSess.disconnect ();

		// now get new session and lookup
		dbSess = DatabaseSessionFactory.getInstance ().createNewSession ();
		Person jr = pf.getByAge (dbSess, 18);
		if (jr != null) {
			System.out.println ("Jr. found");
			if (jr.getAge () == junior.getAge ())
				System.out.println ("Same age");
			if (jr.getFather () != null && jr.getFather ().getAge () == john.getAge ())
				System.out.println ("Father found");
			if (!jr.getAddress ().getState ().equals ("AL"))
				throw new Exception ("Juniors country == " + jr.getAddress ().getState ());
		}
	}

	Person createPerson (String name, int age, Person father, Person mother) {
		Person p = new Person ();
		p.setFirstName (name);
		p.setAge (age);
		p.setFather (father);
		p.setMother (mother);
		p.setAddress (new Address ());
        Date now = new Date ();
		//        p.setBirthday(new java.sql.Timestamp (now.getTime()));
		return p;
	}


	public Test () throws Exception {
		super ("FamilyTest");
	}

}
