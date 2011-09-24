package org.ephman.junit.generated;

import org.ephman.abra.validation.*;
import java.util.Vector;
/** 
* a Validator for BookData in DEFAULT-FORMAT format
 * @version Thu Jun 30 15:12:42 EDT 2005
 * @author generated by Dave Knull
 */

public class BaseBookDataValidator extends ValidatorBase {


	public static ValidationResult validate (BookData bookData) {
		Vector errors_ = new Vector ();
		validate (bookData, "", errors_); // call with empty path
		return new ValidationResult (errors_);
	}


	public static void validate (BookData bookData, Vector errors_) {
		validate (bookData, "", errors_);
	}

	public static void validate (BookData bookData, String pathWay, Vector errors_) {
		if (bookData == null) return ;
		if (pathWay != null && pathWay.length () > 0)
			pathWay += ".";
		checkStringLength (bookData.getTitle(), 80, pathWay,  "title", errors_, bookData);
		checkStringLength (bookData.getLastName(), 40, pathWay,  "lastName", errors_, bookData);
		checkStringLength (bookData.getFirstName(), 40, pathWay,  "firstName", errors_, bookData);

	}


	public static org.apache.regexp.REProgram _IsbnProgram = getREProgram ("[0-9]*");

}
