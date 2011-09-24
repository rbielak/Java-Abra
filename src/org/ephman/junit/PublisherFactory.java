package org.ephman.junit;

import org.ephman.junit.generated.*;
import org.ephman.abra.database.*;
import org.ephman.abra.utils.Identified;

import java.sql.*;

public class PublisherFactory extends AbstractPublisherFactory {

	private static PublisherFactory theInstance = null;

	public static PublisherFactory getInstance () {
		if (theInstance == null) {
			synchronized (PublisherFactory.class) {
				if (theInstance == null)
					theInstance = new PublisherFactory ();
			}
		}
		return theInstance;
	}

}
