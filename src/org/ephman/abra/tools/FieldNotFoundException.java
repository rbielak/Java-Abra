package org.ephman.abra.tools;



  /** An exception representing a lookup for a field returning null
   *
   *  @author Paul M. Bethe
   *  @version 0.0.1 (11/3/00)
   */

public class FieldNotFoundException extends org.ephman.abra.utils.AbraException {


	/*
	 * This provides the same functionality to SQLException
	 * @param error Error string (in resource bundle..
	 */
	public FieldNotFoundException(String error)
	{
		super(error);
	}

	/*
	 * A more generic entry point.
	 * @param error Error string or standard message id
	 * @param args Array of arguments
	 */
	public FieldNotFoundException(String error, Object[] args)
	{
		super(error, args);
	}

	/*
	 * Helper version for 1 arg
	 */
	public FieldNotFoundException(String error, Object arg)
	{
		super(error, arg);
	}

}
