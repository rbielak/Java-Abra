package org.ephman.examples.family;

/** a hand-coded validators to call Base + address
 */

import org.ephman.abra.validation.*; // abra
import java.util.Vector;
import org.ephman.examples.family.generated.*;

public class PersonValidator extends BasePersonValidator {

	public static ValidationResult validate (Person p) {
		Vector errors_ = new Vector ();
		BasePersonValidator.validate (p, errors_);
		
		BaseAddressValidator.validate (p.getAddress (), "address", errors_);
		return new ValidationResult (errors_);
	}

}
