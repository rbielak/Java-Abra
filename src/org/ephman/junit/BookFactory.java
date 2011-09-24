package org.ephman.junit;

import org.ephman.junit.generated.*;
import org.ephman.abra.database.*;
import org.ephman.abra.utils.Identified;

import java.sql.*;
import java.util.Vector;

public class BookFactory extends AbstractBookFactory {

	private static BookFactory theInstance = null;

	private PublisherFactory pubFactory;

	private AuthorFactory authFactory;

	BookFactory () {
		authFactory = AuthorFactory.getInstance ();
		pubFactory = PublisherFactory.getInstance ();
	}

	public static BookFactory getInstance () {
		if (theInstance == null) {
			synchronized (BookFactory.class) {
				if (theInstance == null)
					theInstance = new BookFactory ();
			}
		}
		return theInstance;
	}


	/*
	 * PreStorage makes sure that all objects that we point to are stored
	 * first
	 */
	protected void preStorage (DatabaseSession dbSess, Identified item) throws SQLException {
		Book book = (Book)item;
		if (book.getAuthor () != null)
			authFactory.store (dbSess, book.getAuthor());
		if (book.getPublisher () != null)
			pubFactory.store (dbSess, book.getPublisher ());
	}

	/*
	 * Deep retrieval retrieves the objects we point to
	 */
	protected void deepRetrieval (DatabaseSession dbSess, Identified item) throws SQLException {
		Book book = (Book)item;
		if (book.getAuthorOid () != 0)
			book.setAuthor (authFactory.getByOid (dbSess, book.getAuthorOid ()));
		if (book.getPublisherOid () != 0)
			book.setPublisher (pubFactory.getByOid (dbSess, book.getPublisherOid ()));
	}


	/**
	 *  Retrieve a book object by it's title 
	 */
	public Book getByTitle (DatabaseSession dbSess, String title) throws SQLException {
		return (Book)getObject (dbSess, this.title, title);
	}

		/**
	 *  Retrieve a book object by it's title 
	 */
	public Vector getByAuthor (DatabaseSession dbSess, Author author) throws SQLException {
		return queryObjects (dbSess, this.authorOid, author.getOid ());
	}

}
