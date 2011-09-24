package org.ephman.abra.tools;

import java.io.*;
import java.util.*;
import org.ephman.utils.*;

/**
 * given a base output directory
 * generate a schema file
 * PSQL specific one !!
 * @author Paul M. Bethe
 * @version 0.0.2
 */

public class SchemaGenerator extends GenericSchemaGenerator {

	public static String dbPackageName = "PKG_DK";

	public SchemaGenerator (String outFileName) throws IOException, SchemaException {
		super (outFileName);
		int i = outFileName.lastIndexOf ('.');
		procFileName = outFileName.substring (0, i) + PROC_SUFFIX + outFileName.substring (i);
		procFile = new FileWriter (procFileName);
	}

	final String PROC_SUFFIX = "_procs";
	protected String procFileName;
	protected FileWriter procFile;

	public void close () throws IOException, SchemaException {
		this.schemaFile.write (constraints);
		//		this.cleanupFile.write (cleanupConstraints);
		super.close ();
		this.procFile.close ();
	}


	public void generate (JClass currentClass) throws IOException, SchemaException {
		cleanup.write ("\ndrop table " + currentClass.getTableName ());
		cleanup.write (" cascade;\n\n");
		super.generate (currentClass);
		writeProcedures (currentClass);
	}

	/* helper function to get the sql type to be printed. 
	 * override to special case clob..
	 */
	protected String getSqlOutType (JClass currentClass, JField jf) throws SchemaException {
		if (jf.getSqlType ().equals ("clob"))
			return "oid";
		else if (jf.getSqlType ().equals ("date"))
			return "timestamp";
		else
		    return super.getSqlOutType (currentClass, jf);
	}	


	String FUNC_HEADER = "CREATE OR REPLACE FUNCTION ";
	static String INSERT_PREFIX = "FUNC_INS_";

	protected void writeProcedures (JClass currentClass) throws IOException {
		Vector allFields = new Vector(currentClass.getAllFields ().values());
		int vector_count = 0;
		boolean isFirst = true;
		String paramList = "";
		String declareList = ""; // needed for $ aliasing
		String argString = "";
		String updateString = "";
		String valuesString = "";
		String outChar = "?";
		String tabs = "\t\t";

		String procName = INSERT_PREFIX + currentClass.getStoredProcedureName ();
		procFile.write ("\n" + FUNC_HEADER + procName + " (\n");

		//		procBody += ("\n" + FUNC_HEADER + procName + " (\n");
		int aliasNumber = 1;

		if (currentClass.isVersioned ()) {
			isFirst= false;			
			paramList += tabs + " integer";
			declareList = "\n\t" + "P_" + VERSION_NUMBER + " alias for $" + (aliasNumber++)+";";
			argString += tabs + VERSION_NUMBER;
			valuesString += tabs + "P_" + VERSION_NUMBER;
			updateString += tabs + VERSION_NUMBER + " = P_" + VERSION_NUMBER;
		}
		for (int i = 0; i < allFields.size(); i++) {
			JField field = (JField)allFields.elementAt (i);
			if (!field.isDbColumn ())
				vector_count++;
			else {
				Vector hack = null;
				String prefix = "";
				if (field.isInline ()) {
					JCompositeField jcf = (JCompositeField)field;
					JClass fieldClass = jcf.getJClass ();
					prefix = jcf.getPrefix () + "_";
					JField primKey = fieldClass.getFieldByName (fieldClass.getPrimaryKeyJava ());
					hack = new Vector (fieldClass.getAllFields ().values ());
					hack.remove (primKey);
				} else {
					hack = new Vector ();
					hack.add (field);
				}

				for (int j=0; j < hack.size (); j++) {
					JField jf = (JField)hack.elementAt (j);
					if (jf.isDbColumn ()) {
						String type = jf.getSqlType ();
						int lindex = type.indexOf ("(");
						String sqlName = prefix + jf.getSqlName ();
						if (lindex != -1) type = type.substring (0, lindex);
						if (type.equals ("boolean") || type.equals ("Boolean") ) 
							type = "varchar";
						
						String pst = "P_" + sqlName + " " + type;
						String val = "P_" + sqlName; 
						
						if (sqlName.equals (currentClass.getPrimaryKey ())) { // is primary key
							if (!isFirst) {
								valuesString += ",\n";
								argString += ",\n";
							}
							else
								isFirst = false;
							
							argString += tabs + sqlName;
							
							valuesString += "\t\tnextval (''" + currentClass.getTableName() + "_seq'')";
						}
						else { // is not primary key..
							if (!isFirst) {
								
								argString += ",\n";
								valuesString += ",\n";
								if (!isClob (jf)) {
									paramList += ",\n";
									updateString += ",\n";
								}
							}
							else { // first
								isFirst=false;
							}
							
							
							argString += tabs + sqlName;
							valuesString += tabs;
							if (isClob (jf))
								valuesString += (FactoryGenerator.getClobString ());
							else {
								valuesString += val;
								paramList += tabs + type;
								declareList += "\n\t" + val + " alias for $" + (aliasNumber++)+";";
								updateString += tabs + sqlName + " = P_" + sqlName;
							}					
						}
					}
				}
			}
		}

		procFile.write (paramList + ")\n\tRETURNS integer AS '\nDECLARE\n");
		procFile.write ("\ttemp_oid integer; -- for return oid");
		procFile.write (declareList + "\nBEGIN\n");
		procFile.write ("\tinsert into " + currentClass.getTableName () + "(\n" + argString);
		procFile.write (")\n\t" + tabs + "VALUES (\n" + valuesString);
		procFile.write (");\n");
		//   RETURNING " + currentClass.getPrimaryKey () + " INTO temp_oid;\n");
		procFile.write ("\treturn currval(''"+currentClass.getTableName()+
						"_seq'');\nEND;\n' LANGUAGE 'plpgsql';\n");
		// write out stuff

		String updProc = "\nCREATE OR REPLACE FUNCTION PROC_UPD_" + currentClass.getStoredProcedureName () + " (\n"
			+ paramList + ",\n\t\t integer)";
		procFile.write (updProc + " RETURNS INTEGER AS '\nDECLARE");
		procFile.write (declareList + "\n\tP_" + currentClass.getPrimaryKey () +
						" alias for $" + (aliasNumber++) + ";\nBEGIN\n");
		procFile.write ("\tupdate " + currentClass.getTableName () + " set\n" + updateString);
		procFile.write ("\twhere " + currentClass.getPrimaryKey () + " = P_"
						+ currentClass.getPrimaryKey ());
		procFile.write (";\nRETURN 0; -- dummy return\nEND;\n' LANGUAGE 'plpgsql';\n");
	}

	private boolean isClob (JField jf) {
		return jf.getSqlType ().equals ("clob");
	}


	/* override to insert constraint in table definition */
	protected void writeCheckConstraint (JClass currentClass, String cons_name, JField jf)
		throws IOException {
		
		String con = " constraint " + cons_name + " check (" + jf.getSqlName ()
			+ jf.getConstraint () +")";
			
		schemaFile.write (con);
	}

	/* override to insert constraint in table definition 
	   
	protected void writeForeignKey (JClass currentClass, String cons_name, JField jf,
	JClass foreignClass) throws IOException {
	String con = " constraint " + cons_name + " references " + foreignClass.getTableName ()
	+ "(" + foreignClass.getPrimaryKey () + ")";
	schemaFile.write (con);
	} */

}
