package org.ephman.xml.dtd;

/** interface to wrap dtd fields
 * should give name of field and type/validation (id string 4c!)
 */
import java.util.Vector;

public class DtdException extends Exception {

	public DtdException (String s, int line) {
		super (s);
	}

}
