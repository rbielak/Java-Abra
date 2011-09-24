package org.ephman.abra.validation;

/** Description: an error on a specific field which will go into a validation exception 
 * @author Paul M. Bethe
 * @version 0.0.42
 */

public class FieldError  {

	/** construct a field error
	 * @param fieldName the name of the field (foo.bar.accountId)
	 * @param msg a meaningful error message (foo.bar.accountId not in format [0-9]*)
	 */
	private FieldError (String fieldName, String msg) {
		this.msg = msg;
		this.fieldName = fieldName;
	}
	public FieldError (String fieldName, int errorCode, String msg) {
	    this (fieldName, msg);
	    error_code = errorCode;
	}

	public FieldError (String fieldName, int len, int errorCode, String msg) {
	    this (fieldName, errorCode, msg);
	    length = len;
	}
	 
	int length = -1;  // only applies if length validation fails.
    int error_code;
	String fieldName;
	String msg;

	// only applies if was LENGTH_FAILED
    public int getFieldLength () { return length; }

    public int getErrorCode () { return error_code; }
	public String getFieldName () { return fieldName; }
	public String getMessage () { return msg; }
	
	public String toString () { return msg; }
}
