package org.ephman.abra.tools;

import java.io.*;
import java.util.*;

/**  MEANT TO BE COPIED DOWN
 *  this is the Sybase version
 * given a base output directory
 * generate a factory class for get/put when given a JClass descriptor
 * @author Paul M. Bethe
 * @version 0.0.2
 */

public class FactoryGenerator extends GenericFactoryGenerator {

	public static final String DB_NAME = "SQLServer";

	public FactoryGenerator (String outdir, char file_sep, String imp, boolean useProcs) {
		super (outdir, file_sep, imp, useProcs);
	}
	/** this removes the  pk from stmt (auto_increment)*/
	protected boolean setPKInInsertStmt () { return false;}
	
	public String getPrimaryKeyString (JClass currentClass) {
		return  null;
	}

		// override if your db does something different..
	protected String createClobString () { 
		return "NULL";
	}
	// override ..
	protected boolean usePackages () { return false; }
	
	protected String writeOutAField (JField jf, String fieldType, String colName, String varName, String stype) throws IOException, SchemaException {
		if (fieldType.equals ("clob")) {
			// ignore for now
			return "";
		} else
			return super.writeOutAField (jf, fieldType, colName, varName, stype);

	}

	protected void createTypeMap () {
		super.createTypeMap ();
		typeMap.put (BIG_DECIMAL, "numeric");
		typeMap.put (BIG_INTEGER, "numeric");
		typeMap.put (TIMESTAMP, "datetime");
        // do not support clobs
		typeMap.put (CLOB, "varchar(8000)");        
	}

}
