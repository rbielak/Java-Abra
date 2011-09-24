package org.ephman.abra.utils;

import java.io.*;
import org.ephman.utils.MessageTranslator;

/*
 * This class extends runtimeException, and provides our internationalisation handling 
 * copied pattern from Postgres
 * @author Paul M Bethe
 * @version 0.1 (7/10/02)
 */
public class AbraRuntimeException extends RuntimeException
{
	private String message;

	protected Exception previousException;

	/*
	 * This provides the same functionality to Exception
	 * @param error Error string
	 */
	public AbraRuntimeException(String error)
	{
		super();
		translate(error, null);
	}

	/*
	 * A more generic entry point.
	 * @param error Error string or standard message id
	 * @param args Array of arguments
	 */
	public AbraRuntimeException(String error, Object[] args)
	{
		//super();
		translate(error, args);
	}

	/** incase a previous exception was caught and new info was appended
	 */

	public AbraRuntimeException(String error, Object[] args, Exception prevEx)
	{
		this (error, args);
		this.previousException = prevEx;
		if (previousException != null) {
			message += " ::previousException>>" + previousException.getMessage ();
		}
	}

	public void printStackTrace () {
		super.printStackTrace ();
		if (previousException != null) 
			previousException.printStackTrace ();
	}

	/*
	 * Helper version for 1 arg
	 */
	public AbraRuntimeException(String error, Object arg)
	{
		super();
		Object[] argv = new Object[1];
		argv[0] = arg;
		translate(error, argv);
	}

	private void translate(String error, Object[] args)
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

