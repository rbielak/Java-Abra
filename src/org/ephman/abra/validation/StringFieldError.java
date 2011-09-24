package org.ephman.abra.validation;

/** Description: an error on a specific field which will go into a validation exception 
 * @author Paul M. Bethe
 * @version 0.0.42
 */
import java.lang.reflect.*;

public class StringFieldError extends FieldError {

	/** construct a field error
	 * @param fieldName the name of the field (foo.bar.accountId)
	 * @param msg a meaningful error message (foo.bar.accountId not in format [0-9]*)
	 */
	public StringFieldError (String fieldName, int len, int errorCode, String msg, Object pointer) {
	    super (fieldName, len, errorCode, msg);
		this.pointer = pointer;
	}
	 

	public StringFieldError (String fieldName, int errorCode, String msg, Object pointer) {
		super (fieldName, errorCode, msg);
		this.pointer = pointer;
	}
	/** this can be accessed via reflection with get/set<fieldName> */
	protected Object pointer;

	/** allows business code to access the value which caused the problem
	 */
	public String getStringValue () throws Exception {
		Method m = getMethod (true); // get
		if (m == null) throw new Exception ("CTM Sucks");
		String result = null;
		try {
			result = (String)m.invoke (pointer, null);
		} catch (Exception ie) {
			throw ie ;
		}
		return result;
	}

	/** allows business code to change the value 
	 */
	public void setStringValue (String value) {
		Method m = getMethod (false); // set
		try {
			m.invoke (pointer, new Object[]{value});
		} catch (Exception ie) {
			throw new NullPointerException ();
		}	
	}

	// implementation of reflective calls..
	Method getter;
	Method setter;
	
	Method getMethod (boolean isGet) {		
		if (isGet) {
			if (getter != null) return getter;
			else {
				getter = getMethod ("get", EMPTY_CLASS_A);
				return getter;
			}
		} else {
			if (setter != null) return setter;
			else {
				setter = getMethod ("set", STRING_A);
				return setter;
			}
		}
	}

	final static Object [] EMPTY_OBJECT_A = {};
	final static Class [] EMPTY_CLASS_A = {};
	final static Class [] STRING_A = {String.class};

	Method getMethod (String prefix, Class [] args) {
		if (fieldName == null || fieldName.equals (""))
			return null;

		int index = fieldName.lastIndexOf (".");
		String name = index != -1 ? fieldName.substring (index + 1) : fieldName;

		String mname = prefix + name.substring (0,1).toUpperCase () +
			name.substring (1);
		Method result = null;
		Class c = pointer.getClass ();
		while (c != null && result ==null) {
			try {
				result = c.getDeclaredMethod (mname, args);
			} catch (Exception e) {
				c = c.getSuperclass ();
			}
		}
		if (result == null) throw new NullPointerException ("Unable to find method " + mname + " in class " + pointer.getClass ());
		return result;
	}
}
