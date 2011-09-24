package org.ephman.abra.tools.generators;

import org.ephman.abra.tools.*;
import org.ephman.abra.tools.plugins.*;

import java.io.*;
import java.util.*;

import org.apache.regexp.*;
/**
 *
 * generate the PHP-Nuke code for a table
 * @author Paul M. Bethe
 * @version 0.0.1 (2/2005)
 */

public class PhpField extends FieldPluginData {

    String dimensions;
    String in_field; /* usually NULL but represents alternate var. name to use */
    boolean update = true;

    public PhpField (String dim, String in_field) {
	this.dimensions = dim;
	this.in_field = in_field;
    }

    public PhpField (String dim, String in_field, String update) {
	this(dim,in_field);
	if (update != null && update.equals("false"))
	    this.update = false;
    }

    boolean doUpdate () { return update; }

    boolean hasInField () { return in_field != null && in_field.length() > 0; }
    String getInField () {
	return in_field;
    }

}