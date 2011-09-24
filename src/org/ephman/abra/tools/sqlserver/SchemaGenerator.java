package org.ephman.abra.tools;

import java.io.*;
import java.util.*;
import org.ephman.utils.*;

/**
 * given a base output directory
 * generate a schema file
 * Sybase specific one !!
 * @author Paul M. Bethe
 * @version 0.0.2
 */

public class SchemaGenerator extends GenericSchemaGenerator {

	public static String dbPackageName = "PKG_DK";

	public SchemaGenerator (String outFileName) throws IOException, SchemaException {
		super (outFileName);
		//int i = outFileName.lastIndexOf ('.');
		//procFileName = outFileName.substring (0, i) + PROC_SUFFIX + outFileName.substring (i);
		//procFile = new FileWriter (procFileName);
	}


	public void close () throws IOException, SchemaException {
		this.schemaFile.write (constraints);
		this.cleanupFile.write (cleanupConstraints);
		super.close ();
	}


	public void generate (JClass currentClass) throws IOException, SchemaException {
		cleanup.write ("\ndrop table " + currentClass.getTableName ());
		cleanup.write (";\n\n");
		super.generate (currentClass);
		//		writeProcedures (currentClass);
	}

	/* override to insert constraint in table definition */
	protected void writeCheckConstraint (JClass currentClass, String cons_name, JField jf)
		throws IOException {
		
		String con = " constraint " + cons_name + " check (" + jf.getSqlName ()
			+ jf.getConstraint () +")";
			
		schemaFile.write (con);
	}

	/** generic primary in table creation.. */
	protected void writePrimaryKey (JClass currentClass, String sqlName) throws IOException {
	    schemaFile.write ("\n\t" + sqlName + " int identity,\n\tprimary key (" + sqlName + ")");		
	}

	/* override to insert constraint in table definition */
	/*
	protected void writeForeignKey (JClass currentClass, String cons_name, JField jf,
									JClass foreignClass) throws IOException {
		//String con = " constraint " + cons_name + " references " + foreignClass.getTableName ()
		//			+ "(" + foreignClass.getPrimaryKey () + ")";
			//schemaFile.write (con);
	}
	*/

    protected void cleanUpIndex(JClass currentClass, JIndex index) {
		// For SqlServer this is the format of drop index statement
        cleanupConstraints += "\ndrop index " + currentClass.getTableName() + "." + index.getName () + ";\n";
    }


	// overrid if no sequences..
	boolean needsSequence () { return false; }
}
