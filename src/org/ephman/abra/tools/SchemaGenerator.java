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
		//		this.procFile.close ();
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

    protected void cleanUpIndex(JClass currentClass, JIndex index) {
        cleanupConstraints += "\ndrop index " + currentClass.getTableName() + "." + index.getName () + ";\n";
    }


	/** generic primary in table creation.. */
	protected void writePrimaryKey (JClass currentClass, String sqlName) throws IOException {
	    schemaFile.write ("\n\t" + sqlName + " numeric identity,\n\tprimary key (" + sqlName + ")");		
	}
	/* override to insert constraint in table definition */

	protected void writeForeignKey (JClass currentClass, String cons_name, JField jf,
									JClass foreignClass) throws IOException {
		//String con = " constraint " + cons_name + " references " + foreignClass.getTableName ()
		//			+ "(" + foreignClass.getPrimaryKey () + ")";
			//schemaFile.write (con);
	}

	/* helper function to get the sql type to be printed. 
	 * override to special case clob..
	 */
	protected String getSqlOutType (JClass currentClass, JField jf) throws SchemaException {
	    String result;
	    int len = 0;
	    TypeMap tm = jf.getDkType();
	    if (tm.isHasLengthArg() && (tm.getAbraTypeName().equalsIgnoreCase("string"))) {
		try {
		    len = Integer.parseInt(jf.getLength());
		} catch (NumberFormatException ne) {
		    throw new SchemaException (currentClass, jf, "ephman.abra.tools.nolen");
		}
	    }
	    if (len > 255) {
		result = "text";
	    }
	    else
		result = super.getSqlOutType(currentClass, jf);
	    return result;
	}	
	
	/* 
	 * For Sybase make sure that we add "null" qualifier to fields that are
	 * foreign refs and datatime fields
	 */
    protected String getDbSpecificOptions(JClass currentClass, JField jf, String primaryKey) throws SchemaException {
        String result = null;
        String sqlName = jf.getSqlName();
        String sqlType = this.getSqlOutType(currentClass, jf);
        if (!sqlName.equals(primaryKey)) {
            // only consider columns that are not primary keys
            // only consider columns that are not primary keys
            if (jf.isDate() || sqlType.equals("int") || sqlType.startsWith("numeric") || sqlType.equals("text") || sqlType.startsWith("varchar")) {
                result = "null";
            }
        }
        return result;
    }

	// overrid if no sequences..
	boolean needsSequence () { return false; }
}
