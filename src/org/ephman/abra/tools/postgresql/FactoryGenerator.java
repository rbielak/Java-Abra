package org.ephman.abra.tools;

import java.io.*;
import java.util.*;

/**  MEANT TO BE COPIED DOWN
 *  this is the PostgreSQL version
 * given a base output directory
 * generate a factory class for get/put when given a JClass descriptor
 * @author Paul M. Bethe
 * @version 0.0.2
 */

public class FactoryGenerator extends GenericFactoryGenerator {

	public static final String DB_NAME = "PostgreSQL";

	public FactoryGenerator (String outdir, char file_sep, String imp, boolean useProcs) {
		super (outdir, file_sep, imp, useProcs);
	}
	
	public String getPrimaryKeyString (JClass currentClass) {
		return  "nextval('" + currentClass.getTableName() + "_seq')";
	}

	// override ..
	protected boolean usePackages () { return false; }

	protected String createClobString () {
		return getClobString();
	}

	protected static String getClobString () {
		return "(select lo_creat(" + LO_READWRITE + "))";
	}   

	public static long LO_READWRITE = 0x00060000;

	protected void createTypeMap () {
		super.createTypeMap ();
		typeMap.put (TIMESTAMP, "timestamp"); // override default behaviour
		typeMap.put (BIG_DECIMAL, "numeric");
		typeMap.put (BIG_INTEGER, "numeric");
	}
}
