package org.ephman.abra.tools;

import java.io.*;
import java.util.*;

/**  MEANT TO BE COPIED DOWN
 *  this is the DB2-UDB version
 * given a base output directory
 * generate a factory class for get/put when given a JClass descriptor
 * @author Paul M. Bethe
 * @version 0.0.2
 */

public class FactoryGenerator extends GenericFactoryGenerator {

	public static final String DB_NAME = "DB2-UDB";

	public FactoryGenerator (String outdir, char file_sep, String imp, boolean useProcs) {
		super (outdir, file_sep, imp, useProcs);
	}

	/** this is removes the  pk from stmt*/
	protected boolean setPKInInsertStmt () { return false;}

	// no sequences
	public String getPrimaryKeyString (JClass currentClass) {
		return null;  
			//"(select nextval for " + currentClass.getTableName() + "_seq from " + currentClass.getTableName () + ")";
		// UDB
	}

	/** not needed in UDB 
		protected void writeSetArgForPK (FileWriter factoryOutFile, String varName, String get_set_name) throws IOException {
		// nothing for most dbs (db2 and any others that don't support sequences can call a KeyService..
		// this is probably close
		} */

}
