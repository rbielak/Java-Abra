package org.ephman.abra.database;

import java.io.*;
import java.sql.*;
import org.ephman.utils.MessageTranslator;

/*
 * This class extends SQLException, and provides our internationalisation handling
 * copied pattern from PostgreSQL
 * @author Paul M Bethe
 * @version 0.1 (2/15/02)
 */
public class AbraSQLException extends SQLException
{
	private String message;

	/*
	 * This provides the same functionality to SQLException
	 * @param error Error string
	 */
	public AbraSQLException(String error)
	{
		super();
		translate(error, null);
	}

	/*
	 * A more generic entry point.
	 * @param error Error string or standard message id
	 * @param args Array of arguments
	 */
	public AbraSQLException(String error, Object[] args)
	{
		//super();
		translate(error, args);
	}

	/*
	 * Helper version for 1 arg
	 */
	public AbraSQLException(String error, Object arg)
	{
		super();
		Object[] argv = new Object[1];
		argv[0] = arg;
		translate(error, argv);
	}

	protected void translate(String error, Object[] args)
	{
		message = MessageTranslator.translate(error, args);
	}

	/*
	 * Overides Throwable
	 */
	public String getLocalizedMessage()
	{
		return message;
	}

	/*
	 * Overides Throwable
	 */
	public String getMessage()
	{
		return message;
	}

	/*
	 * Overides Object
	 */
	public String toString()
	{
		return message;
	}
}
