package org.ephman.abra.tools;

import java.io.*;
import java.util.*;
import org.ephman.utils.*;

/**
 * given a base output directory
 * generate a schema file
 * ORACLE specific one !!
 * @author Paul M. Bethe
 * @version 0.0.2
 */

public class SchemaGenerator extends GenericSchemaGenerator {

	public SchemaGenerator (String outFileName) throws IOException, SchemaException {
		super (outFileName);
		int i = outFileName.lastIndexOf ('.');
		procFileName = outFileName.substring (0, i) + PROC_SUFFIX + outFileName.substring (i);
		procFile = new FileWriter (procFileName);
		procFile.write ("CREATE OR REPLACE PACKAGE " + dbPackageName + " AS\n\n");
	}

	static String dbPackageName = "PKG_DKNULL";

	final String PROC_SUFFIX = "_procs";
	protected String procFileName;
	protected FileWriter procFile;

	String procBody = "";
	String closeStmt = "\nEND " + dbPackageName +";\n/\nshow errors\n";

	public void close () throws IOException, SchemaException {
		this.schemaFile.write (constraints);
		this.cleanupFile.write (cleanupConstraints);
		procFile.write (closeStmt);
		procFile.write ("CREATE OR REPLACE PACKAGE BODY " + dbPackageName + " AS\n\n");
		procFile.write (procBody + closeStmt);
		this.procFile.close ();
		super.close ();
	}

	public void generate (JClass currentClass) throws IOException, SchemaException {
		cleanup.write ("\ndrop table " + currentClass.getTableName ());
		cleanup.write (" cascade constraints");
		cleanup.write (";\n\n");
		super.generate (currentClass);
		writeProcedures (currentClass);
	}

	protected void writeUniqueConstraint (JClass currentClass, JField jf) throws IOException, 
																				 SchemaException {
		String consName = jf.getSqlName () + "_unq";
		consName = makeConstraintName (consName);
		constraints += "\nalter table " + currentClass.getTableName () + " add constraint " + consName
			+ " unique (" + jf.getSqlName () + ");\n";
		cleanupConstraints += "\nalter table " + currentClass.getTableName () + " drop constraint " 
			+ consName + ";\n";
	}

	/* override to create pk as constraint.. */
	protected void writePrimaryKey (JClass currentClass, String sqlName) throws IOException {
		constraints = "\nalter table " + currentClass.getTableName () 
			+ " add constraint " + currentClass.getTableName () + "_pk" 
			+ " primary key ("+ sqlName +");\n" + constraints;
		// need to put before any other constraints..		
	}

	String FUNC_HEADER = "FUNCTION ";
	static String INSERT_PREFIX = "FUNC_INS_";

	protected void writeProcedures (JClass currentClass) throws IOException {
		Vector allFields = new Vector(currentClass.getAllFields ().values());
		int vector_count = 0;
		boolean isFirst = true;
		String paramList = "";
		String argString = "";
		String updateString = "";
		String valuesString = "";
		String outChar = "?";
		String tabs = "\t\t";

		String procName = INSERT_PREFIX + currentClass.getStoredProcedureName ();
		procFile.write ("\n" + FUNC_HEADER + procName + " (\n");

		procBody += ("\n" + FUNC_HEADER + procName + " (\n");


		if (currentClass.isVersioned ()) {
			isFirst= false;			
			paramList += tabs + "P_" + VERSION_NUMBER + " integer";
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
						if (type.equals ("boolean") || type.equals ("Boolean"))
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
							valuesString += "\t\t" + currentClass.getTableName() + "_seq.nextval";
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
								valuesString += ("EMPTY_CLOB ()");
							else {
								valuesString += val;
								paramList += tabs + pst;
								updateString += tabs + sqlName + " = P_" + sqlName;
							}					
						}
					}
				}
			}
		}

		procFile.write (paramList + ")\n\tRETURN integer;\n\n");
		procBody +=  (paramList + ")\n\tRETURN integer\nAS\n");
		procBody +=  ("\ttemp_oid integer; -- for return oid\nBEGIN\n");
		procBody +=  ("\tinsert into " + currentClass.getTableName () + "(\n" + argString);
		procBody +=  (")\n\t" + tabs + "VALUES (\n" + valuesString);
		procBody +=  (") RETURNING " + currentClass.getPrimaryKey () + " INTO temp_oid;\n");
		procBody +=  ("\treturn temp_oid;\nEND " + procName +" ; \n");
		// write out stuff

		String updProc = "\nPROCEDURE PROC_UPD_" + currentClass.getStoredProcedureName () + " (\n"
			+ paramList + ",\n\t\tP_" + currentClass.getPrimaryKey () + " integer)";
		procFile.write (updProc + ";\n\n");
		procBody += updProc + "\nAS\nBEGIN\n";
		procBody += "\tupdate " + currentClass.getTableName () + " set\n" + updateString;
		procBody += "\twhere " + currentClass.getPrimaryKey () + " = P_"+ currentClass.getPrimaryKey ();
		procBody += ";\nEND PROC_UPD_" + currentClass.getStoredProcedureName () + ";\n\n";
	}

	private boolean isClob (JField jf) {
		return jf.getSqlType ().equals ("clob");
	}

}
