package org.ephman.abra.validation;
// meant to be copied down a level

import java.util.*;
import org.ephman.utils.*;
import org.ephman.abra.utils.*;
import org.apache.regexp.*;
/**
 * ValidatorBase class - base for all classes with validators..
 *
 * @author: Paul Bethe
 * @version 0.1
 */


public abstract class ValidatorBase{

	//public void 

	public static void checkStringLength (String value, int length, String pathWay, String fieldName, Vector errors, Object accessor)
	{
		
		if (value != null && value.length() > length)
			errors.add (new StringFieldError (pathWay + fieldName, length, ValidationCodes.LENGTH_FAILED, "String field '" + fieldName + "' is too Large"
										   + "'" + value + "'(" + value.length () +">"+length+")", accessor));
	}

	public static void assertMandatory (Object obj, String pathWay, String fieldName, Vector errors, Object accessor)  {
		if (obj == null)
			errors.add (new FieldError (pathWay + fieldName, ValidationCodes.MANDATORY_FAILED, "Mandatory field '" + fieldName + "' not found"));
		// ignore accessor// 
	}

	public static void assertMandatory (String value, String pathWay, String fieldName, Vector errors, Object accessor) {
		if (value == null || value.equals (""))
			errors.add (new StringFieldError (pathWay + fieldName, ValidationCodes.MANDATORY_FAILED, "Mandatory field '" + fieldName + "' not found", accessor));
	}

	/** optimization for pre-compiled reprograms..
	 */
	public static void checkRegex (String value, REProgram prog, String errorName, String pathWay, String fieldName, int errorCode, Vector errors, Object accessor) 
		 {
	
		checkRegex (value, new RE (prog), errorName, pathWay, fieldName, errorCode, errors, accessor);		
	}

	/** simple way of passing the regex string and checking for a match
	 */
	public static void checkRegex (String value, String regex, String errorName, String pathWay, String fieldName, int errorCode, Vector errors, Object accessor) {
		 
		try {
			checkRegex (value, new RE (regex), errorName, pathWay, fieldName, errorCode, errors, accessor);
		} catch (RESyntaxException r) {			
			throw new IllegalArgumentException ("Expression '" + regex + "' invalid.");
		}
	}

	/** factored method of checking against an RE (whether precompiled or dynamic
	 * @param value the value to validate
	 * @param re the regular expression object (jakarta.regexp)
	 * @param errorName a defined name (like 'country-codes')
	 * @param fieldName the name of the field for error msg creation.
	 * @param errors add errors to this vector.
	 */
	public static void checkRegex (String value, RE re, String errorName, String pathWay, String fieldName, int errorCode, Vector errors, Object accessor) 
	{

		if (value == null || value.equals ("")) return;
			boolean match = re.match (value);
			match = match && (re.getParenStart (0) == 0 && re.getParenEnd (0) == value.length ());
			if (!match) errors.add (new StringFieldError (pathWay + fieldName, errorCode, "Field '" + fieldName + "'=(" + value +
													") not"
													+ " in required format '" + errorName + "'", accessor));
	}


	/** method to get an REProgram (precompiled regex..)
	 */
	public static REProgram getREProgram (String regex) {
		REProgram prog = null;
		try {
			RECompiler compiler = new RECompiler ();
			prog = compiler.compile (regex);
		} catch (RESyntaxException res) {
			// who knows?
		}			
		return prog;
	}

}
