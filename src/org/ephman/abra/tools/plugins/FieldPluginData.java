package org.ephman.abra.tools.plugins;

import org.ephman.abra.tools.JField;

/** a class for plugins at the class level (ie View, Validator,  Taglib)
* 
* @version 0.0.2 2/6/2005
* @author Paul M. Bethe
*/

public class FieldPluginData {

	JField theField = null ; // the field on which the validate is for..
	public void setJField (JField jf) { theField = jf; }
	public JField getJField () { return theField; }

}
