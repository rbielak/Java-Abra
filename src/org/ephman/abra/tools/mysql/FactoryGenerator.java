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

	public static final String DB_NAME = "MySQL";

	public FactoryGenerator (String outdir, char file_sep, String imp, boolean useProcs) {
		super (outdir, file_sep, imp, useProcs);
		// No stored procs available in MySql
		useProcs = false;
	}
	/** this removes the  pk from stmt (auto_increment)*/
	protected boolean setPKInInsertStmt () { return false;}
	
	public String getPrimaryKeyString (JClass currentClass) {
		return  null;
	}

	// override ..
	protected boolean usePackages () { return false; }

	protected void createTypeMap () {
		super.createTypeMap ();
		typeMap.put (BIG_DECIMAL, "numeric");
		typeMap.put (BIG_INTEGER, "numeric");
		typeMap.put (TIMESTAMP, "datetime");
		typeMap.put (CLOB, "text");
        
	}


}
