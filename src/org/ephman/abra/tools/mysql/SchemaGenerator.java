package org.ephman.abra.tools;

import java.io.*;
import java.util.*;
import org.ephman.utils.*;

/**
 * given a base output directory
 * generate a schema file
 * MYSQL specific one !!
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

    protected void addConstraint(JClass currentClass, String consName, JConstraint jcons, String sOut) {
        // do nothing. MySql does not support constraints
    }

    protected void cleanUpConstraint(JClass currentClass, String consName) {
        // MySql daoes not support constraints
    }

    protected void cleanUpIndex(JClass currentClass, JIndex index) {
        cleanupConstraints = "\ndrop index " + index.getName() + " on " +
                currentClass.getTableName() + ";\n ";
    }

	/* override to insert constraint in table definition */
	protected void writeCheckConstraint (JClass currentClass, String cons_name, JField jf)
		throws IOException {
	    /* fix on 2/9/05 PMB -- not supported yet - but maybe soon by mysql*/
		String con = " check (" + jf.getSqlName ()
			+ jf.getConstraint () +")";
			
		schemaFile.write (con);
	}
	/** generic primary in table creation.. */
	protected void writePrimaryKey (JClass currentClass, String sqlName) throws IOException {
	    schemaFile.write ("\n\t" + sqlName + " int not null AUTO_INCREMENT,\n\tprimary key (" + sqlName + ")");		
	}
    /* override to insert constraint in table definition */

    protected void writeForeignKey (JClass currentClass, String cons_name, JField jf,
				    JClass foreignClass) throws IOException {
	String con = ",\n\t constraint " + cons_name + " foreign key ("+jf.getSqlName()+
	    ") references " + foreignClass.getTableName ()
	    + "(" + foreignClass.getPrimaryKey () + ")";
	schemaFile.write (con);
    }	


	// overrid if no sequences..
	boolean needsSequence () { return false; }

    /* In MySQL a VARCHAR can only be 255 characters. Anything
       longer must be of type "text"
       */
    protected String getSqlOutType(JClass currentClass, JField jf) throws SchemaException {
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
        if (len > 255)
            result = "text";
        else
            result = super.getSqlOutType(currentClass, jf);
        return result;
    }
}
