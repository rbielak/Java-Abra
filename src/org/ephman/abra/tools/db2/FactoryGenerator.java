package org.ephman.abra.tools;

import java.io.*;
import java.util.*;

/**  MEANT TO BE COPIED DOWN
 *  this is the DB2 version
 * given a base output directory
 * generate a factory class for get/put when given a JClass descriptor
 * @author Paul M. Bethe
 * @version 0.0.2
 */

public class FactoryGenerator extends GenericFactoryGenerator {

	public static final String DB_NAME = "DB2";

	public FactoryGenerator (String outdir, char file_sep, String imp, boolean useProcs) {
		super (outdir, file_sep, imp, useProcs);
	}


	// no sequences
	public String getPrimaryKeyString (JClass currentClass) {
		return  "?"; // this will be an argument not a sequence
			//currentClass.getTableName() + "_seq.nextval";
	}

	/** */
	protected void writeSetArgForPK (FileWriter factoryOutFile, String varName, String get_set_name) throws IOException {
		// nothing for most dbs (db2 and any others that don't support sequences can call a KeyService..
		// this is probably close
		String result = "\t\t if (!update) { // get primary key\n"; 
		result += "\t\t\tint p_key = KeyService.getInstance ().getNextKey ();\n";
		result += "\t\t\t" + varName + ".set" + get_set_name + "(p_key); // this will set the pkey \n"; 
		result += "\t\t\tstmt.setInt (i++, p_key);\n\t\t}\n";
		factoryOutFile.write (result);
	}

}
