package org.ephman.junit;

import junit.framework.TestCase;
import org.ephman.abra.database.*;
import org.ephman.junit.generated.*;
import java.sql.*;
import java.io.*;
import java.util.*;

/** a test to try some view operations
 * @author Paul Bethe
 */

public class TestAbraViews extends DatabaseTest {

	public TestAbraViews (String name) {
		super (name);
	}


	public void testViewOverlay () throws Exception {
		Book spellFor = BookFactory.getInstance ().getByTitle (dbSess, "A Spell for Chameleon");
		assertTrue ("found spell for chameleon", spellFor!= null);
		Author anthony = spellFor.getAuthor ();
		assertTrue ("found Anthony", anthony != null);

		EditBook be = new EditBook ();
		be.setTitle ("Ogre, Ogre");
		EditAuthor ea = new EditAuthor ();
		ea.setFirstName ("Jules");
		be.setAuthor (ea);

		spellFor.initializeFromView (be);
		assertTrue ("new title correct", 
					"Ogre, Ogre".equals (spellFor.getTitle()));
		assertTrue ("correct new first name",
					anthony.getFirstName ().equals ("Jules"));
		System.out.println (marshaller.marshal (be));

	}

    /** this test will check that inline objects can be queried using foreign fields / views
     *  through factories
     */
    public void testInlineQueries () throws Exception {
        //assert that ViewLookup is correct
        AuthorFactory aFact = AuthorFactory.getInstance();
        QueryTracer.setTraceLevel(0);

        ComparisonFilter cf = new ComparisonFilter (aFact.lastName, " like ", "%Adams%");
        Vector v = aFact.queryObjects(dbSess, aFact.editLookup, cf);
        assertTrue ("exactly one hit when searching for 'Adams' by editLookup",
                v != null && v.size () == 1);
        assertTrue ( "right class type", v.elementAt(0) instanceof EditAuthor);
        EditAuthor ea = (EditAuthor)v.elementAt(0);
        System.out.println(super.marshaller.marshal(ea));
        assertTrue ("correct value in inline/foriegn field",
                "The restaurant".equals (ea.getHomeAddress()));
        assertTrue ("correct in workAdress (foreign-view/inline)",
                "somewhere".equals(ea.getWorkAddress().getStreet()));
    }

}

