package org.ephman.abra.tools;

//meant to be copied down a level
// if editing edit in tools/db2

import java.io.*;
import java.util.*;
import org.ephman.utils.*;

/**
 * given a base output directory
 * generate a schema file
 * Db2 specific one !!
 * @author Paul M. Bethe
 * @version 0.0.2
 */

public class SchemaGenerator extends GenericSchemaGenerator {

	public SchemaGenerator (String outFileName) throws IOException, SchemaException {
		super (outFileName);
	}

	// this gets overrided by MapToJava in the command line switch -procs <some_pkg>
	static String dbPackageName = "PKG_DKNULL";


	public void close () throws IOException, SchemaException {
		this.schemaFile.write (constraints);
		this.cleanupFile.write (cleanupConstraints);
		super.close ();
	}

	public void generate (JClass currentClass) throws IOException, SchemaException {
		cleanup.write ("\ndrop table " + currentClass.getTableName ());
		//		cleanup.write (" cascade constraints");
		cleanup.write (";\n\n");
		super.generate (currentClass);
		// NOTE: db2 may wish to grab the table name/class name and generate something for the 
		// KeyService to be able to generate keys.

		// add stored proc functionality to db2 ??
		//		writeProcedures (currentClass);
	}

	/* may wish to override if functionality other than <some-field> <some-typ> unique is 
	   desired (In Oracle we use "alter table" stmts
	  protected void writeUniqueConstraint (JClass currentClass, JField jf) throws IOException, 
	  SchemaException {
	*/

	/* override to create pk as constraint.. (same as with unique directive)
	 */
	protected void writePrimaryKey (JClass currentClass, String sqlName) throws IOException {
		schemaFile.write (" generated always as identity");
			//, primary key (" + sqlName + ")
		constraints = "\nalter table " + currentClass.getTableName () 
			+ " add constraint " + currentClass.getTableName () + "_pk" 
			+ " primary key ("+ sqlName +");\n" + constraints;
		// need to put before any other constraints..		
	}
	
	// override to add keyword restrict
	protected String getDropSeqStmt (JClass currentClass) {
		return "\ndrop sequence " + currentClass.getTableName () +"_seq restrict;\n";
	}

}
