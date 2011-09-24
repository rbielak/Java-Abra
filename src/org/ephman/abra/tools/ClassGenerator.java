package org.ephman.abra.tools;

import java.util.Map;
/** just a generator interface
 *
 * @author Paul M Bethe
 * @version 9/19/02
 */

public interface ClassGenerator extends Generator {

	/** listing of mappings from dave-knull types to the type names of that language */
	public Map getTypeMap ();

}
