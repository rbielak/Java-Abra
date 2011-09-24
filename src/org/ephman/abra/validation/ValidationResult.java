package org.ephman.abra.validation;

/** Description: describes the results of validations
 * and includes error if it failed..
 * which can occur during generated validation 
 * @author Paul M. Bethe
 * @version 0.0.42
 */
import java.util.Vector;

public class ValidationResult {

	/** with no args -> means succesful
	 */
	public ValidationResult () {
		passed = true;
	}

	public ValidationResult (Vector errors) {
		if (errors == null || errors.size () == 0)
			passed = true;
		else {
			passed = false;
			this.errors = errors;
		}
	}

	boolean passed = false;
	Vector errors = null;

	public boolean getPassed () { return passed; }
	public void setPassed (boolean b) { passed = b; }
	public void setErrors (Vector v) { errors = v; }
	public Vector getErrors () { return errors; }


}
