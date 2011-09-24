package org.ephman.abra.tools.plugins;


/*  For plugin data errors..
 * This class extends AbraException, which provides our internationalisation handling
 * copied pattern from Postgres
 * @author Paul M Bethe
 * @version 0.1 (2/15/02)
 */
public class PluginDataException extends org.ephman.abra.utils.AbraException
{
	public PluginDataException(String error)
	{
		super (error);
	}

	/*
	 * A more generic entry point.
	 * @param error Error string or standard message id
	 * @param args Array of arguments
	 */
	public PluginDataException(String error, Object[] args)
	{
		super(error, args);
	}

	/** incase a previous exception was caught and new info was appended
	 */

	public PluginDataException(String error, Object[] args, Exception prevEx)
	{
		super (error, args, prevEx);
	}


}
