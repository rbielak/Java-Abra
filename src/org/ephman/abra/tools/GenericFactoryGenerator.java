package org.ephman.abra.tools;

import org.ephman.utils.*;
import java.io.*;
import java.util.*;

/**  (this is the generic base a db specific descendant will be copied into this package
 * given a base output directory
 * generate a factory class for get/put when given a JClass descriptor
 * @author Paul M. Bethe
 * @version 0.0.2
 */

public abstract class GenericFactoryGenerator implements AbraTypes {
	private String outdir;
	private char fileSeperator;
	private String defaultExtend;
	private boolean useProcs;
	//	private boolean isPostgres;

	/** make a new generator with a base directory of outdir
	 * so for foo.bar.FooBar if outdir = /home/dknull
	 * and file_sep = '/' then a class /home/dknull/foo/bar/AbstractFooBarFactory.java is created
	 * @param outdir the base directory for writing files
	 * @param file_sep the file seperator ('/' for unix/linux , '\\' for Windows)
	 */

	public GenericFactoryGenerator (String outdir, char file_sep, String imp, boolean useProcs) {
		this.outdir = outdir;
		this.fileSeperator = file_sep;
		this.defaultExtend = imp;
		this.useProcs = useProcs;		
	}

	String END_DATE = "end_date";

	String constructorString = "";

	public void generate (JClass currentClass) throws IOException, SchemaException {
		String fname = currentClass.getPackageName().replace('.', fileSeperator) + fileSeperator +"Abstract"
              + currentClass.getClassName() + "Factory.java";
        FileWriter outFile = new FileWriter (outdir + fileSeperator + fname);
		Debugger.trace ("Generating factory " + outdir + fileSeperator + fname, Debugger.VERBOSE);
		constructorString = "";

        writeFactoryHeader (outFile, currentClass);

		Vector allFields = new Vector(currentClass.getAllFields ().values());

		writeFactoryMethods (outFile, currentClass, allFields);

		writeFactoryLinks (outFile, currentClass, allFields);

        writeFactoryTrailer (outFile, currentClass);
        outFile.close ();


	}

	private void writeFactoryLinks (FileWriter outFile, JClass currentClass, Vector allFields) throws IOException, SchemaException {
		outFile.write ("\tpublic String getPrimaryColumn () { ");
		outFile.write ("return \"" + currentClass.getPrimaryKey() + "\"; }\n");
		outFile.write ("\n\tpublic String getTableName () { ");
		outFile.write ("return tableName; }\n");


		outFile.write ("\n\n\t// the variables to map Java to Sql\n\n");
		outFile.write ("\tprotected final String tableName = \"" + currentClass.getTableName () + "\";\n\n");

		for (int i = 0; i < allFields.size (); i++) {
			JField jf = (JField)allFields.elementAt (i);
			String name = jf instanceof JCompositeField ? jf.getJavaName () + "Oid" : jf.getJavaName ();
			if (jf.isInline ()) {
				JCompositeField jcf = (JCompositeField)jf;
				InlineFieldWriter iw = new InlineFieldWriter (jcf, currentClass){
						public void writeOneField (FileWriter outFile, JField subField,
												   String sqlName, String preName,
												   boolean isFirst) 
							throws IOException, SchemaException {

							String v_name = preName + subField.getJavaName ();
							outFile.write ("\tpublic final String " + v_name + " = \"" 
										   + prefix
										   + subField.getSqlName () + "\";\n\n");
						}
					};
				iw.execute (outFile, false);
				/* factored 
				   JClass fieldClass = jcf.getJClass ();
				   Vector fields = new Vector (fieldClass.getAllFields ().values ());
				   String subPrimaryKey = fieldClass.getPrimaryKey ();
				   String prefix = jcf.getPrefix () + "_";
				   for (int j = 0; j < fields.size (); j++) {
				   JField subField = (JField)fields.elementAt (j);
				   String sqlName = subField.getSqlName ();
				   String preName = jf.getJavaName () + "_";
				   
				   if (sqlName.equals (subPrimaryKey)) {
				   // do nothing ..
				   }
				   else if (subField instanceof JCompositeField) {
				   throw new SchemaException ("ephman.abra.tools.nocompinline", 
				   currentClass.getSchemaFileName ()); 
				   }
				   else {
				   name = preName + subField.getJavaName ();
				   outFile.write ("\tpublic final String " + name + " = \"" + prefix
				   + subField.getSqlName () + "\";\n\n");
				   }
				   } */
			}
			else if (jf.isDbColumn ())
				outFile.write ("\tpublic final String " + name + " = \"" + jf.getSqlName () + "\";\n\n");
		}
        if (currentClass.isVersioned())
		    outFile.write ("\tpublic final String " + VERSION_NUMBER + " = \"" + VERSION_NUMBER + "\";\n\n");
	}


	// fac methods down here

	private void writeUpdateMethod (FileWriter factoryOutFile, JClass currentClass, Vector allFields) throws IOException, SchemaException {
		factoryOutFile.write ("\n/** this method returns the update string for the db */\n\n");
		factoryOutFile.write ("\tprotected String makeUpdateString () {\n");
		factoryOutFile.write ("\t\treturn updateString;\n\t}\n");
		factoryOutFile.write ("\tprotected static final String updateString = ");
		factoryOutFile.write ("\"update " + currentClass.getTableName () + " set \" +\n\t\t");

		final String outChar = "?";
		String prim_str = "";
		boolean isFirst = true;
		if (currentClass.isVersioned ()) {
			isFirst = false;
			factoryOutFile.write ("\"" + VERSION_NUMBER + " = " + outChar);
		}
		for (int i = 0; i < allFields.size(); i++) {
			JField jf = (JField)allFields.elementAt (i);
			if (jf.isDbColumn () && !isClob (jf) && !jf.isInline ()) {

				String sqlName = jf.getSqlName ();
				/*	if (jf instanceof JCompositeField) {
					sqlName = ((JCompositeField)jf).getJClass ().getPrimaryKey ();
					}*/
				if (jf.getSqlName ().equals (currentClass.getPrimaryKey ())) {
					prim_str = " where " + jf.getSqlName () + " = ?"; // is primary key
				}
				else {
					if (!isFirst)
						factoryOutFile.write (",\" +\n\t\t\"" + sqlName + " = " + outChar);
					else {
						isFirst = false;
						factoryOutFile.write ("\"" + sqlName + " = " + outChar);
					}
				}
			} else if (jf.isInline ()) {
				JCompositeField jcf = (JCompositeField)jf;
				InlineFieldWriter iw = new InlineFieldWriter (jcf, currentClass) {
						public void writeOneField (FileWriter outFile, JField subField,
												   String sqlName, String preName, 
												   boolean isFirst) 
							throws IOException, SchemaException {

							if (!isFirst)
								outFile.write (",\" +\n\t\t");
							
							outFile.write ("\"" + prefix + 
												  sqlName + " = " + outChar);
						}
					};
				iw.execute (factoryOutFile, isFirst);
				isFirst = false; // at least one inline - field
				/* no more
				   JClass fieldClass = jcf.getJClass ();
				   Vector fields = new Vector (fieldClass.getAllFields ().values ());
				   String subPrimaryKey = fieldClass.getPrimaryKey ();
				   String prefix = jcf.getPrefix ();
				   for (int j = 0; j < fields.size (); j++) {
				   JField subField = (JField)fields.elementAt (j);
				   String sqlName = subField.getSqlName ();
				   
				   if (!subField.isDbColumn() || sqlName.equals (subPrimaryKey)) {
				   // do nothing ..
				   }
				   else if (subField instanceof JCompositeField) {
				   throw new SchemaException ("ephman.abra.tools.nocompinline", 
				   currentClass.getSchemaFileName ()); 
				   }
				   else {
				   
				   }						
				   } */
			} else if (jf instanceof JCompositeField) {
				JCompositeField jcf = ((JCompositeField)jf);
				if (!(jcf.isCollection () || jcf.isTransient ()) && jcf.getJClass () == null) {
					if (jcf.getSqlName().length() > 0 && !jcf.getSqlName().equals (jcf.getJavaName ())) {
						// sql node found on composite (but no foreign table descriptor )
						createErrorMsg (currentClass, jcf, "ephman.abra.tools.noforeign");
					}
					// else ok no sql node
				}
			}
		}
		if (prim_str.equals (""))
			Debugger.trace ("!! No primary key found for class " + currentClass.getClassName (), Debugger.ERROR);
		factoryOutFile.write (prim_str + "\";\n"); 
	}

	private void createErrorMsg (JClass currentClass, JField jf, String msg) throws SchemaException {		
		throw new SchemaException (currentClass, jf, msg);
	}

	private String findSetType (JField field) {

		if (field.getDkType ().isComposite ())
			return null; // is a com.foo.bar
		String typeName =  field.getDkType ().getObjectTypeName ();
		if (nativeClassSet.contains (typeName)) // is Integer, .. etc
			return "Object";
		return typeName.substring (0,1).toUpperCase () + typeName.substring (1);
	}

	private boolean isClob (JField jf) {
		return jf.getSqlType ().equals ("clob");
	}

    private boolean hasClobs (Vector allFields) {
        boolean result = false;
        for (int i = 0; !result && (i < allFields.size()); i++) {
            JField jf = (JField)allFields.get(i);
            result = isClob (jf);
        }
        return result;
    }

	private void writeClobMethods (FileWriter factoryOutFile, JClass currentClass, Vector clobList)
		throws IOException, SchemaException {

		boolean hasClobs = false;
		if (Checks.hasElements (clobList)) {
			hasClobs = true;
			// write setClobs method for each field in list..

			factoryOutFile.write ("\n/** this method updates clobs to the db */\n");
			factoryOutFile.write ("\tprotected void setClobs (DatabaseSession dbSess, ResultSet rs, Identified item) throws SQLException {\n");
			factoryOutFile.write ("\t\t" + currentClass.getClassName () + " foo = (" + 
								  currentClass.getClassName () + ")item;\n\n");

			for (int i=0; i< clobList.size (); i++) {
				JField jf  = (JField)clobList.elementAt (i);
				factoryOutFile.write ("\t\tsuper.setClob (dbSess, rs, " + jf.getJavaName () + ", foo.get"
									  + jf.getGetSet () + " ());\n");
			}
			factoryOutFile.write ("\t}\n\n");
		}

        writeHasClobsMethod(factoryOutFile, hasClobs);

    }

    private void writeHasClobsMethod(FileWriter factoryOutFile, boolean hasClobs) throws IOException {
        factoryOutFile.write ("\n/** this method tells FactoryBase whether or not clobs exist */\n");
        factoryOutFile.write ("\tprotected boolean hasClobs () { return " + hasClobs + "; }\n");
    }

    private void writeSetArgMethod (FileWriter factoryOutFile, JClass currentClass, Vector allFields, int key_loc)
		throws IOException, SchemaException {

		Vector clobList = new Vector ();

		factoryOutFile.write ("\n/** this method sets the args for a prepared statement */\n\n");
		factoryOutFile.write ("\tprotected void setArguments (PreparedStatement stmt, Object"
							  + " obj, boolean update) throws SQLException {\n");
		String varName =currentClass.getClassName ().substring (0,1).toLowerCase ()+currentClass.getClassName ().substring (1);
		factoryOutFile.write ("\t\t" + currentClass.getClassName () + " " + varName + " = (" + currentClass.getClassName ()
							  + ")obj;\n");
		factoryOutFile.write ("\t\tint i = 1;\n");
		factoryOutFile.write ("\t\tif (!update && useStoredProcs ()) // set return val..\n");
		factoryOutFile.write ("\t\t\t((CallableStatement)stmt).registerOutParameter (i++, Types.INTEGER);\n");
		if (currentClass.isVersioned ())
			factoryOutFile.write ("\t\tstmt.setInt (i++, " + varName + ".getVersion ());\n");

		String prim_str = "";

		for (int i = 0; i < allFields.size (); i++) {
			JField jf = (JField)allFields.elementAt (i);
			if (jf.isDbColumn ()) {
				if (isClob (jf))
					clobList.addElement (jf);
				else if (jf.isInline ()) {
					JCompositeField jcf = (JCompositeField)jf;
					InlineFieldWriter iw = new InlineFieldWriter (jcf, currentClass) {
							public void writeOneField (FileWriter outFile, JField subField,
													   String sqlName, String preName, 
													   boolean isFirst) 
								throws IOException, SchemaException {
								String subVarName = this.currentClass.getClassName ().substring (0,1).toLowerCase ()
									+this.currentClass.getClassName ().substring (1);
								preName = subVarName +".get" + this.jcf.getGetSet () + " ().get";
					
								String set_type = findSetType (subField);
								String endMod = "";
								if (subField.isDate ()) set_type = "Timestamp";
								else if (subField.getObjectType ().equals ("boolean")) {
									set_type = "String";
									endMod = "? \"T\" : \"F\"";
								}
								if (subField.getObjectType().equals ("Boolean")) {
									outFile.write ("\t\tstmt.setString (i++, "+
														  "getBooleanAsString("+
														  preName + subField.getGetSet () 
														  + " ())" + endMod + ");\n");
								} else
									outFile.write ("\t\tstmt.set" + set_type +  " (i++, "+
														  preName + subField.getGetSet () + " ()" 
														  + endMod + ");\n");
							}
						};
					iw.execute (factoryOutFile, false);
					/*					
					JClass fieldClass = jcf.getJClass ();
					Vector fields = new Vector (fieldClass.getAllFields ().values ());
					String subPrimaryKey = fieldClass.getPrimaryKey ();
					String prefix = jcf.getPrefix ();
					for (int j = 0; j < fields.size (); j++) {
					JField subField = (JField)fields.elementAt (j);
					String sqlName = subField.getSqlName ();
					String preName = varName +".get" + jf.getGetSet () + " ().get";
					
					if (sqlName.equals (subPrimaryKey)) {
					// do nothing ..
					}
					else if (subField instanceof JCompositeField) {
					throw new SchemaException ("ephman.abra.tools.nocompinline", 
					currentClass.getSchemaFileName ()); 
					}
						else {
							String set_type = findSetType (subField);
							String endMod = "";
							if (subField.isDate ()) set_type = "Timestamp";
							else if (subField.getObjectType ().equals ("boolean")) {
							set_type = "String";
							endMod = "? \"T\" : \"F\"";
							}
							if (subField.getObjectType().equals ("Boolean")) {
							factoryOutFile.write ("\t\tstmt.setString (i++, "+
							"getBooleanAsString("+
							preName + subField.getGetSet () 
							+ " ())" + endMod + ");\n");
							} else
							factoryOutFile.write ("\t\tstmt.set" + set_type +  " (i++, "+
							preName + subField.getGetSet () + " ()" 
							+ endMod + ");\n");
							}						
							} */
				} 
				else {
					String set_type = findSetType (jf);
					String tmp = "";
					String get_set_name = "";
					boolean is_foreign = false;
					if (set_type == null) {
						is_foreign = true;
						set_type = "Int";
						tmp = jf.getJavaName() + "Oid";
					}
					else {
						tmp = jf.getJavaName ();
					}
					get_set_name += tmp.substring (0,1).toUpperCase () + tmp.substring (1);
					
					if (i == key_loc) {
						prim_str = "\t\tif (update)\n\t";
						prim_str += "\t\tstmt.set" + set_type +  " (i++, "+ varName +".get" + get_set_name + " ());\n";
						writeSetArgForPK (factoryOutFile, varName, get_set_name);
						// factoryOutFile.write ("\t\tif (!update)\n\t\t\tstmt.set" + set_type
						// +  " (i++, item.get" + get_set_name + " ());\n");
					}
					else if (jf.isDate ()) {// is date - hack
						factoryOutFile.write ("\t\tstmt.setTimestamp (i++, " +varName+".get" + get_set_name + " ());\n");
					}
					else if (jf.getObjectType().equals ("boolean")) {// is boolean - hack
						factoryOutFile.write ("\t\tstmt.setString (i++, " +varName+".get" + get_set_name + " ()? \"T\" : \"F\");\n");
					}
					else if (jf.getObjectType().equals ("Boolean")) {
						factoryOutFile.write ("\t\tstmt.setString (i++, "+
											  "getBooleanAsString("+
											  varName + ".get" + get_set_name 
											  + " ()));\n");
					}
					else if (jf.getObjectType().startsWith ("java.math")) {
						factoryOutFile.write ("\t\tif (" + varName + ".get" + jf.getGetSet ()+ "() == null) \n");
						factoryOutFile.write ("\t\t\tstmt.setNull (i++, java.sql.Types.");
						if (jf.getObjectType().equals ("java.math.BigDecimal"))
							factoryOutFile.write ("DECIMAL");
						else
							factoryOutFile.write ("INTEGER");
						factoryOutFile.write (");\n\t\telse\n\t\t\tstmt.setObject (i++, "+
											  varName + ".get" + get_set_name 
											  + " ());\n");
					}
					else {
						if (set_type.equals ("Object") || is_foreign) {
							factoryOutFile.write ("\t\tif (" + varName + ".get" + jf.getGetSet ()+ "() == null) {\n");
							if (is_foreign)
								factoryOutFile.write ("\t\t\tif (" + varName + ".get" + jf.getGetSet ()+ "Oid () == 0)\n\t");
							factoryOutFile.write ("\t\t\tstmt.setNull (i++, java.sql.Types.");
						}
						if (set_type.equals ("Object")) {
							//#AMHERE
							NativeObjectMapping nom = (NativeObjectMapping)nativeSetTypes.get (jf.getObjectType());
							factoryOutFile.write (nom.sqlTypes + ");\n\t\t} else");
							factoryOutFile.write ("\t\t\tstmt.set" + nom.setName + " (i++, "+ varName +".get" + get_set_name + " ()."+nom.nativeGetter+");\n");
						}
						else if (is_foreign) {
							factoryOutFile.write ("INTEGER);\n");
							factoryOutFile.write ("\t\t\telse\n");
							factoryOutFile.write ("\t\t\t\tstmt.set" + set_type +  " (i++, "+ varName +".get" + get_set_name + " ());\n");
							factoryOutFile.write ("\t\t} else\n\t");
							factoryOutFile.write ("\t\tstmt.set" + set_type +  " (i++, "+ varName +".get" + jf.getGetSet () + " ().getOid ());\n");
						}
						else
							factoryOutFile.write ("\t\tstmt.set" + set_type +  " (i++, "+ varName +".get" + get_set_name + " ());\n");
					}
				}
			}
			else key_loc++;
		}
		factoryOutFile.write (prim_str);
		factoryOutFile.write ("\t}\n");
		writeClobMethods (factoryOutFile, currentClass, clobList);
	}

	/** methods which should be implemented by descendant generator */
	public abstract String getPrimaryKeyString (JClass currentClass);

	protected void writeSetArgForPK (FileWriter factoryOutFile, String varName, String get_set_name) throws IOException {
		// nothing for most dbs (db2 and any others that don't support sequences can call a KeyService..
	}

	////

	protected void writeFactoryMethods (FileWriter factoryOutFile, JClass currentClass, Vector allFields) throws IOException, SchemaException {


		if (currentClass.getPrimaryKeyJava ().equals ("")) {
			if (!currentClass.isManyToMany ()) {
				Debugger.trace ("!!No primary key found for class " + currentClass.getClassName (), Debugger.ERROR);
			}
			else {
				Debugger.trace (currentClass.getClassName () + " is many-to-many.", Debugger.VERBOSE);
				// many to many
				writeManyToManyMethods (factoryOutFile, currentClass, allFields);
			}
		}
		else {

            // If this a query based factory - no updates are allowed
            if (!currentClass.isQuery()) {
                // All these methods are for updates
			    writeUpdateMethod (factoryOutFile, currentClass, allFields);
        	    // now gen the insert string
      		    int key_loc = writeInsertMethod (factoryOutFile, currentClass, allFields);
    			writeSetArgMethod (factoryOutFile, currentClass, allFields, key_loc);
			    writeProcs (factoryOutFile, currentClass, allFields);
                writeStoreAndDeleteMethods (factoryOutFile, currentClass, allFields);
            }
            else {
                // write methods needed for Query based factory
                writeQueryBasedFactoryMethods (factoryOutFile, currentClass);
                writeHasClobsMethod(factoryOutFile, hasClobs (allFields));
            }

			writeMakeFromRs (factoryOutFile, currentClass, allFields);
			writeViewCode (factoryOutFile, currentClass, allFields);
            writeBasicQuery (factoryOutFile, currentClass);

			//			writeDeepRetrieval (factoryOutFile, currentClass, allFields);
		}
	}

    protected void writeQueryBasedFactoryMethods (FileWriter outFile, JClass currentClass) throws IOException {
        // first write constant that is the query string - append
        outFile.write ("\tprivate String queryString =\n");
        outFile.write ("\t\t\"" + currentClass.getQuery() + "\";\n\n");
        // routine to return the sql to methods that do queries
        outFile.write ("\tprotected String getSelectSql () {return queryString;}\n\n");
        // rotuine to get select sql with one extra predicate on one column
        outFile.write ("\tprotected String getSelectSql (String column) {\n");
        outFile.write ("\t\treturn queryString + \" and (\" + column + \" = ?)\";\n");
        outFile.write ("\t}\n\n");

        // For cursor queries do the same kind of sql as normal query, since
		// we do not know what the primary key may be etc... The user can override
		// in the child class if necessary
        outFile.write ("\tprotected String getCursorQuerySql () {return queryString;}\n\n");

        // Query already has where clause no need to add it in
        outFile.write ("\tprotected boolean defaultNeedsWhereLogic () {return false;};\n\n");

        outFile.write ("\tprotected boolean needsAndBeforeFilter () {return true;};\n\n");

        // the routines below are not yet supported - they throw exceptions
        String exceptionCode = "\t\tthrow new RuntimeException (\"Not supported for query based factories\");\n";
        outFile.write ("\tprotected String getSelectCountSql () {\n" );
        outFile.write (exceptionCode);
        outFile.write ("\t}\n\n");

        outFile.write ("\tprotected boolean useStoredProcs () {\n");
        outFile.write ("\t\treturn false;\n");
        outFile.write ("\t}\n\n");

        // TODO: consider whether it makes sense to split GenricFactoryBase into two. Parent with
        // only queries and descendant with updates. Then the query based factories do not have to do this
        outFile.write ("\tprotected String getDeleteSql () {\n" );
        outFile.write (exceptionCode);
        outFile.write ("\t}\n\n");

        outFile.write ("\tprotected void setArguments (PreparedStatement ps, Object o, boolean update) throws SQLException {\n");
        outFile.write (exceptionCode);
        outFile.write ("\t}\n\n");

        outFile.write ("\tprotected String makeUpdateCall () {\n");
        outFile.write (exceptionCode);
        outFile.write ("\t}\n\n");

        outFile.write ("\tprotected String makeInsertCall () {\n");
        outFile.write (exceptionCode);
        outFile.write ("\t}\n\n");

        outFile.write ("\tprotected String makeUpdateString () {\n");
        outFile.write (exceptionCode);
        outFile.write ("\t}\n\n");

        outFile.write ("\tprotected String makeInsertString () {\n");
        outFile.write (exceptionCode);
        outFile.write ("\t}\n\n");


    }

	protected void writeDeepRetrieval (FileWriter outFile, JClass currentClass, Vector allFields) throws IOException, SchemaException {
		outFile.write ("\t// method to retrieve composite objects\n");
		outFile.write ("\tpublic void deepRetrieval (DatabaseSession dbSess, Identified item) throws SQLException {\n");
		outFile.write ("\t\t" + currentClass.getClassName () + " foo = (" +
					   currentClass.getClassName () + ")item;\n");
		for (int i=0; i < allFields.size (); i++) {
			JField jf = (JField)allFields.elementAt (i);
			if (jf instanceof JCompositeField) {
				JCompositeField jcf = (JCompositeField)jf;
				if (jcf.getJClass () != null) { // is composite and has class(i.e. table)
					JClass jfClass = jcf.getJClass ();
					String m_name = jcf.getGetSet ();
					// so assume <class>Factory exists and we have generated
					outFile.write ("\t\tif (foo.get" + m_name + "Oid() != 0)\n");
					outFile.write ("\t\t\tfoo.set" + m_name +" (" +jfClass.getClassName() + "Factory.getInstance ().getByOid (dbSess, foo.get" + m_name + "Oid()))");
				}
			}
		}
		outFile.write ("\t\tsuper.deleteObject (dbSess, foo);\n\t}\n");
	}

	/** routine which writes store, getByOid and delete */
	
	protected void writeStoreAndDeleteMethods (FileWriter outFile, JClass currentClass, Vector allFields) throws IOException, SchemaException {
		outFile.write ("\t// methods to store and delete\n");
		outFile.write ("\tpublic void delete (DatabaseSession dbSess, " + currentClass.getClassName () 
					   + " foo) throws SQLException {\n");
		outFile.write ("\t\tsuper.deleteObject (dbSess, foo);\n\t}\n");

		outFile.write ("\tpublic void store (DatabaseSession dbSess, " + currentClass.getClassName () 
					   + " foo) throws SQLException {\n");
		if (currentClass.hasDescendant ())
			outFile.write ("\t\tfoo.prepareToStore ();\n");
		outFile.write ("\t\tsuper.storeObject (dbSess, foo);\n\t}\n");

    }

    private void writeBasicQuery(FileWriter outFile, JClass currentClass) throws IOException {
        String className = currentClass.getDescendantName ();
        outFile.write ("\tpublic " + className +  " getByOid (DatabaseSession dbSess, "  + "int object_id) throws SQLException {\n");
        outFile.write ("\t\treturn (" +className + ")super.getObject (dbSess, "
                       + "this.oid, object_id);\n\t}\n");
    }

    // defaults to true can override if false..
	protected boolean usePackages () { return true; }

	/** write procs */
	protected void writeProcs (FileWriter outFile, JClass currentClass, Vector allFields) throws IOException, SchemaException {
		String pkgName = usePackages () ? SchemaGenerator.dbPackageName + "."
			: "";
		String insertProc = "{? = call " + pkgName + "FUNC_INS_"
			+ currentClass.getStoredProcedureName () + "(";
		String updateProc = "{ call " + pkgName + "PROC_UPD_"
			+ currentClass.getStoredProcedureName () + "(";
		String commonProc = "";
		for (int i=0; i < allFields.size (); i++) {
			JField jf = (JField)allFields.elementAt (i);
			if (jf.isInline ()) {
				JCompositeField jcf = (JCompositeField)jf;
				JClass fieldClass = jcf.getJClass ();
				Vector fields = new Vector (fieldClass.getAllFields ().values ());
				for (int j = 0; j < fields.size (); j++) {
					JField subField = (JField)fields.elementAt (j);
					if (subField.isDbColumn())
						commonProc += "?,";
				}
				// take away one for pk
				commonProc = commonProc.substring (0, commonProc.length()-2);
			}
			else if (jf.isDbColumn () && !isClob (jf)) {
				commonProc += "?,";				
			}
		}
		commonProc = commonProc.substring (0, commonProc.length()-1);
		insertProc += commonProc;
		updateProc += commonProc;
		if (!currentClass.isVersioned ()) { // remove one ?
			insertProc = insertProc.substring (0, insertProc.length()-2);
		}
		else
			updateProc += ",?";
		insertProc += ") }";
		updateProc += ") }";
		outFile.write ("\n\tprotected String makeInsertCall () {\n\t\treturn insertCallString;\n\t}\n");
		outFile.write ("\n\tpublic static final String insertCallString = \"" + insertProc + "\";\n");
		outFile.write ("\n\tpublic static final String updateCallString = \"" + updateProc + "\";\n");
		outFile.write ("\n\tprotected String makeUpdateCall () {\n\t\treturn updateCallString;\n\t}\n");
		outFile.write ("\n\tprotected boolean useStoredProcs () { return "+ useProcs +"; }\n");
	}

	protected void writeMakeFromRs (FileWriter outFile, JClass currentClass, Vector allFields) throws IOException, SchemaException {
		String class_name = currentClass.getClassName ();
		String desc_name = currentClass.getDescendantName ();
		outFile.write ("\n\n\t/* a routine to build an object of this type from an sql ResultSet */\n");
		outFile.write ("\n\tprotected Identified makeFromResultSet (ResultSet rs) throws SQLException{\n");
		outFile.write ("\t\t" + class_name + " foo = new " + desc_name + " ();\n");
		outFile.write ("\t\trefreshFromResultSet (foo, rs);\n\t\treturn foo;\n\t}\n\n");
		outFile.write ("\t/* a routine to overwrite an object of this type from an sql ResultSet */\n");
		outFile.write ("\tprotected void refreshFromResultSet (Identified item, ResultSet rs) throws SQLException{\n");
		outFile.write ("\t\t" + class_name + " foo = (" + class_name +")item;\n");
		if (currentClass.isVersioned ())
			outFile.write ("\t\tfoo.setVersion (rs.getInt(" + VERSION_NUMBER + "));\n");

		for (int i = 0; i < allFields.size (); i++) {
			JField jf = (JField)allFields.elementAt (i);
			if (jf instanceof JCompositeField && jf.isDbColumn ()) {
				//System.out.println ("composite lookup??");
				if (jf.isInline ()) {
					JCompositeField jcf = (JCompositeField)jf;
					InlineFieldWriter iw = new InlineFieldWriter (jcf, currentClass) {
							public void writeOneField (FileWriter outFile, JField subField,
													   String sqlName, String preName, 
													   boolean isFirst) 
								throws IOException, SchemaException {
								
								String subVar = this.jcf.getJavaName ();
								outFile.write (writeOutAField (subField, subField.getSqlType (),
									subVar + "_" + subField.getJavaName (), subVar));
							}
							
							public void writeOnce (FileWriter outFile, JClass fieldClass) 
							throws IOException {
								String subVar = this.jcf.getJavaName ();
								outFile.write ("\t\t" + fieldClass.getClassName () + " " + subVar
											   + " = new " + fieldClass.getClassName () + 
											   " ();\n");
								outFile.write ("\t\tfoo.set" + this.jcf.getGetSet () + " ("
											   + subVar + ");\n");
							}
						};
					iw.execute (outFile, false);

					/*
					 JClass fieldClass = jcf.getJClass ();
					 Vector fields = new Vector (fieldClass.getAllFields ().values ());
					 String subPrimaryKey = fieldClass.getPrimaryKey ();
					 String prefix = jcf.getPrefix ();
					  /*
						for (int j = 0; j < fields.size (); j++) {
						 JField subField = (JField)fields.elementAt (j);
						 String sqlName = subField.getSqlName ();
						
						 if (sqlName.equals (subPrimaryKey)) {
						 // do nothing ..
						 }
						 else if (subField instanceof JCompositeField) {
						 throw new SchemaException ("ephman.abra.tools.nocompinline",
						 currentClass.getSchemaFileName ()); 
						 }
						 else { 
						 outFile.write (writeOutAField (subField, subField.getSqlType (),
						 subVar + "_" + subField.getJavaName (), subVar));
						 }						
						 }
					  */
				} else {
					String get_set = jf.getGetSet () + "Oid";
					outFile.write ("\t\tfoo.set" + get_set + " (rs.getInt"
								   + " (" + jf.getJavaName ()+ "Oid));\n");
				}
			}
			else if (jf.isDbColumn ()) {
				writeOutAField (outFile, jf);
			}
		}
		if (currentClass.hasDescendant ())
			outFile.write ("\t\tfoo.makeTransient ();\n");
		outFile.write ("\t}\n\n");
	}


	protected void writeOutAField (FileWriter outFile, JField jf) throws IOException, SchemaException {
		outFile.write (writeOutAField (jf));
	}

	protected String writeOutAField (JField jf) throws IOException, SchemaException {
		return writeOutAField (jf, jf.getSqlType (), jf.getJavaName ());
	}

 	protected String writeOutAField (JField jf, String fieldType, String colName) throws IOException, SchemaException {
		return writeOutAField (jf, fieldType, colName, "foo");
	} 

	protected String writeOutAField (JField jf, String fieldType, String colName, String varName) throws IOException, SchemaException {
		String stype = findSetType (jf);
		return writeOutAField (jf, fieldType, colName, varName, stype);
	}



	protected String writeOutAField (JField jf, String fieldType, String colName, String varName, String stype) throws IOException, SchemaException {
		String result = "";
		if (jf.isDate ()) {
			result +="\t\t" + varName + ".set" + jf.getGetSet () + " (rs.getTimestamp" + " (" + colName+ "));\n";
		}
		else if (jf.getObjectType().equals ("boolean")) {  // boolean hack
			result += "\t\tString b" + jf.getGetSet () + " = rs.getString"
				+ " (" + colName+ ");\n";
			result += "\t\tif (b" + jf.getGetSet () + "!= null)\n";
			result +="\t\t\t" + varName + ".set" + jf.getGetSet () + " (b" +jf.getGetSet () + ".equals (\"T\"));\n";
		} else if (isClob (jf)) { // clob hack
			String clVar = jf.getJavaName () + "_CL";
			result +="\t\tClob " + clVar + " = rs.getClob (" + colName + ");\n";
			result +="\t\t" + varName + ".set" + jf.getGetSet () + " (" + clVar + ".getSubString (1, (int)" 
				+ clVar + ".length ()));\n";
		} else if (jf.getObjectType().startsWith ("java.math")) {
			String cast_this = "(" + jf.getObjectType() + ")";
			result +="\t\t" + varName + ".set" + jf.getGetSet () + " ("+ cast_this
						+"rs.getObject"	+ " (" + colName + "));\n";

		} else { // not date/bool/or clob..
			if (stype != null) {
				String cast_this = "";
				String endBool = "";
				if (stype.equals ("Object")) {
					cast_this = "new "+jf.getObjectType ()+" (";
					endBool = ")";
					NativeObjectMapping nom = (NativeObjectMapping)nativeSetTypes.get (jf.getObjectType ());
					stype = nom.setName;
				}
				
				if (jf.getObjectType ().equals ("Boolean")) { //Boolean object..
					result += "\t\tString t_" + jf.getGetSet () + " = rs.getString ("
						+ colName + ");\n";
					result += "\t\t" + varName + ".set" + jf.getGetSet () 
						+ "(getStringAsBoolean (t_" + jf.getGetSet () + "));\n";
				}
				else
					result +="\t\t" + varName + ".set" + jf.getGetSet () + " ("+ cast_this
						+"rs.get" + stype
						+ " (" + colName + ")"+endBool+");\n";
				
			}
			else
				if (!colName.equals (jf.getJavaName ())) // is some weird thing try String
					result +="\t\t" + varName + ".set" + jf.getGetSet () + " (rs.getString"
						+ " (" + colName + "));\n";
				else
					result +="\t\t// don't know what to do for " + jf.getJavaName () + "\n"; 
		}
		return result;
	}


	protected int writeInsertMethod (FileWriter factoryOutFile, JClass currentClass, Vector allFields) throws IOException, SchemaException {
		factoryOutFile.write ("\n/** this method returns the insert string for the db */\n\n");
		factoryOutFile.write ("\tprotected String makeInsertString () {\n");
		factoryOutFile.write ("\t\treturn insertString;\n\t}\n");
		factoryOutFile.write ("\tprotected static final String insertString = ");
		factoryOutFile.write ("\"insert into " + currentClass.getTableName () + " (\" +\n\t\t");
		int key_loc = -1;
		int vector_count = 0;
		boolean isFirst = true;
		String valuesString = "";
		final String outChar = "?";

		if (currentClass.isVersioned ()) {
			isFirst= false;
			factoryOutFile.write ("\"" + VERSION_NUMBER);
			valuesString += ("\t\t\" " + outChar);
		}

		for (int i = 0; i < allFields.size(); i++) {
			JField jf = (JField)allFields.elementAt (i);
			if (!jf.isDbColumn ())
				vector_count++;
			else if (jf.isInline ()) {
				JCompositeField jcf = (JCompositeField)jf;
				
				JClass fieldClass = jcf.getJClass ();
 				Vector fields = new Vector (fieldClass.getAllFields ().values ());
	 			String subPrimaryKey = fieldClass.getPrimaryKey ();
		 		String prefix = jcf.getPrefix ();
			 	for (int j = 0; j < fields.size (); j++) {
				 	JField subField = (JField)fields.elementAt (j);
					String sqlName = subField.getSqlName ();
					
					if (!subField.isDbColumn() || sqlName.equals (subPrimaryKey)) {
						// do nothing ..
					}
					else if (subField instanceof JCompositeField) {
					 	throw new SchemaException ("ephman.abra.tools.nocompinline",
						 						   currentClass.getSchemaFileName ()); 
					} 
 					else {
						if (!isFirst) {
						 	factoryOutFile.write (",\" +\n\t\t");
							valuesString += ",\" +\n";
						}
						else 
							isFirst = false;
						factoryOutFile.write ("\"" + prefix + "_" + 
											  sqlName);
						valuesString += ("\t\t\"" + outChar);
					}						
				}

			} else {
			
				
				if (jf.getSqlName ().equals (currentClass.getPrimaryKey ())) { // is primary key
					key_loc = i - vector_count;
					if (setPKInInsertStmt ()) { // some dbs support auto-inc
						if (isFirst) 
							isFirst = false;
						else
							valuesString += ",\" + \n";
						valuesString += "\t\t\"" + getPrimaryKeyString (currentClass);	
						if (!isFirst)
							factoryOutFile.write (",\" +\n\t\t\"" + jf.getSqlName ());
						else 					
							factoryOutFile.write ("\"" + jf.getSqlName ());
		
					}
				}
				else { // is not primary key..
					if (!isFirst)
						factoryOutFile.write (",\" +\n\t\t\"" + jf.getSqlName ());
					else 					
						factoryOutFile.write ("\"" + jf.getSqlName ());
					if (!isFirst) {
						valuesString += (",\" +\n\t\t\" ");
						if (isClob (jf))
							valuesString += (createClobString ());
						else
							valuesString += (outChar);	
					}
					else { // first output
						isFirst = false;
						valuesString += ("\t\t\" ");
						if (isClob (jf))
							valuesString += (createClobString ());
						else
							valuesString += (outChar);
					}
				}
			}
		}
		factoryOutFile.write (") values (\" +\n");
		factoryOutFile.write (valuesString);
		factoryOutFile.write (")\";\n");

		return key_loc;
	}

	// override if your db does something different..
	protected String createClobString () { 
		return "EMPTY_CLOB ()";
	}

	/** override to false if auto increment is turned on.. 
	 *
	 */
	protected boolean setPKInInsertStmt () { return true;}



	protected void writeFactoryHeader (FileWriter factoryOutFile, JClass currentClass) throws IOException, SchemaException {
		String facImports[] = new String []{"java.sql.*", "org.ephman.abra.utils.*"};

		factoryOutFile.write ("package " + currentClass.getPackageName () + ";\n\n");
		for (int i = 0; i < facImports.length; i++) {
			factoryOutFile.write ("import " + facImports[i] + ";\n");
		}
		if (!currentClass.getPackageName ().equals ("org.ephman.abra.database"))
			factoryOutFile.write ("import org.ephman.abra.database.*;\n");
		factoryOutFile.write ("\n// Do not edit!! generated classes \n");
        if (!currentClass.isManyToMany()) {
		    factoryOutFile.write ("/** a factory to link the class " + currentClass.getClassName () + "\n");
		    factoryOutFile.write (" * to it's sql code and database table\n");
        }
        else {
            factoryOutFile.write ("/** \n* " + currentClass.getClassDescription() + "\n");
        }
		factoryOutFile.write (" * @version " + (new Date ()).toString () + "\n");
		factoryOutFile.write (" * @author generated by Dave Knull\n */\n\n");


		String toExt = "";
		if (currentClass.hasParentFactory ()) {
			toExt += "extends ";
			if (currentClass.getParentFactory ().equals (""))
				if (currentClass.isManyToMany ())
					toExt += "ManyToManyFactoryBase";
				else if (currentClass.isEndDateable ())
					toExt += "EndDateFactoryBase";
				else
					toExt += defaultExtend;
			else
				toExt += currentClass.getParentFactory ();
		}
		factoryOutFile.write ("public abstract class Abstract" + currentClass.getClassName () + "Factory "
							  + toExt + "{\n\n");


	}

	private void writeFactoryTrailer (FileWriter outFile, JClass currentClass) throws IOException, SchemaException {
		outFile.write ("\tprotected Abstract" + currentClass.getClassName ()
							  + "Factory () ");

		outFile.write ("{\n");
		outFile.write ("\t\tsuper ();\n");
		outFile.write (constructorString);

		outFile.write ("\t}\n\n");
		outFile.write ("\n}\n");
	}

	// View rip and lookup routines

	protected void writeViewCode (FileWriter outFile, JClass currentClass, Vector allFields) throws IOException, SchemaException {
		Iterator i = currentClass.getViewList ();
		while (i.hasNext ()) {
			JView view = (JView)i.next ();
			writeViewString (view, currentClass, outFile);
			//writeViewRs (view, currentClass, outFile);
        }
	}

	protected void writeViewString (JView view, JClass currentClass, FileWriter outFile) throws IOException, SchemaException {
		String u_name = view.getFormatName ();
		u_name = u_name.substring (0,1).toUpperCase () + u_name.substring (1);

		outFile.write ("\tpublic String make" + u_name + "LookupString () {\n");
		outFile.write ("\t\tString result = \"select \"\n");
		String toSelect = "";
		String tables = "";
		String whereClause = "";
		String resultRip = "";

		Vector fieldList = view.getAllFields ();
		for (int index=0; index < fieldList.size (); index++) {
            String expectedVarName = "foo";  // in this scope b/c inline fields may change the value
            // but the next field should revert to 'foo'
			JFieldView fieldView = (JFieldView)fieldList.elementAt (index);
			JField jf = fieldView.getField ();
			if (jf.isDbColumn ()) {
				String elType = findSetType (jf);
				String colName = jf.getJavaName ();
				if (jf instanceof JCompositeField) {
					JCompositeField jcf = (JCompositeField)jf;
					JClass for_class = jcf.getJClass ();

                    if (fieldView.getAsView ()) { // as view foreign table..
                        // TODO: figure out what to do..

                        if (Checks.exists (fieldView.getViewFormat ())) {
                            JView foreignView = for_class.getView (fieldView.getViewFormat ());
                            // now join..
                            Vector viewFields = foreignView.getFieldList ();
                            if (jf.isInline()) { // fields are in this table so we can build ..
                                resultRip += "\t\tfoo.set" + jf.getGetSet() + " (new "
                                        + foreignView.getViewName() + " ());\n";
                                for (int i=0; i< viewFields.size (); i++) {

                                    JFieldView subFieldView = (JFieldView)viewFields.elementAt (i);
                                    JField for_field = subFieldView.getField ();
                                    if (!toSelect.equals (""))
                                        toSelect += ",\"\n\t\t\t+ \"";
                                    //
                                    toSelect += jcf.getPrefix () + "_" + for_field.getSqlName ();
                                    colName = jcf.getJavaName() + "_" + for_field.getJavaName();
                                    String subElType = findSetType (for_field);

                                    resultRip += writeOutAField (for_field, subElType, colName, "foo.get" + jcf.getGetSet ()
                                            + "()", subElType);
                                }
                            } // else TODO: PMB !!  when in seperate table..
                        //toSelect += jf.getJavaName () + "." + for_field.getSqlName () + " as " + jf.getJavaName ();
                        //						elType = null;
                        //						colName += "Oid";
                        }
					}
					else { // single field.. in another table
						String fieldName = fieldView.getViewFormat ();
						JField for_field = for_class.getFieldByName (fieldName);
						if (for_field == null || for_field instanceof JCompositeField) {
							throw new SchemaException (currentClass, jf, 
													   MessageTranslator.translate
													   ("ephman.abra.tools.noview", 
														new Object []{fieldName,
														 for_class.getClassName ()}));
						}

						if (!toSelect.equals (""))
							toSelect += ",\"\n\t\t\t+ \"";

                        elType = findSetType (for_field);
                        if (jf.isInline()) { // field is already in this table
                            toSelect += jcf.getPrefix()+ "_" + for_field.getSqlName ();
                            colName = jcf.getJavaName() + "_" + for_field.getJavaName();
                            //expectedVarName = "foo.get"+jf.getGetSet()+" ()";
                            //jf = for_field;

                        } else  {// do join with foreign table
                            toSelect += jf.getJavaName () + "." + for_field.getSqlName () + " as " + jf.getJavaName ();

                            tables += ", " + for_class.getTableName () + " " + jf.getJavaName ();

                            if (!whereClause.equals (""))
                                whereClause += " and \"\n\t\t\t+ \"";
                            whereClause += "this." + jf.getSqlName() + "=" + jf.getJavaName () + "." + for_class.getPrimaryKey ();
                            if (!jf.isRequired ())
                                whereClause += "(+)";

                            colName = "\"" + jf.getJavaName () + "\"";
                        }
					}
				}
				else {
					if (!toSelect.equals (""))
						toSelect += ",\"\n\t\t\t+ \"";
					toSelect += "this." + jf.getSqlName ();
				} // jf simple type

				if (elType != null) {					
					resultRip += writeOutAField (jf, elType, colName, expectedVarName, elType);
				}
			}
		}
		// now print out stuff..
		outFile.write ("\t\t\t+ \"" + toSelect + "\"\n");
		outFile.write ("\t\t\t+ \" from " + currentClass.getTableName () + " this" + tables + "\"\n");
		outFile.write ("\t\t\t+ \" where " + whereClause + "\";\n");
		outFile.write ("\t\treturn result;\n\t}\n\n");

		// now make from Rs

		outFile.write ("\tpublic " + view.getViewName () + " make" + u_name
					   + "FromResultSet (ResultSet rs) throws SQLException {\n");
		outFile.write ("\t\t" + view.getViewName () + " foo = new " + view.getViewName () + " ();\n");
		outFile.write (resultRip);
		outFile.write ("\t\treturn foo;\n\t}\n\n");

		// now a wrapper
		outFile.write ("\tpublic ViewLookup " + view.getFormatName () + "Lookup;\n\n");
		constructorString += ("\t\t" + view.getFormatName () + "Lookup = new ViewLookup (make"
							  + u_name + "LookupString (),\n");
		constructorString += ("\t\t\t\t\"make" + u_name + "FromResultSet\", "
							  + "\n\t\t\t\tthis);\n");
	}



	// Many to Many routines

	// ok


	protected void writeManyToManyMethods (FileWriter outFile, JClass currentClass, Vector allFields) throws IOException, SchemaException {

		// write many to many code
		try {
			if (allFields.size () != 2)
				throw new SchemaException ("ephman.abra.tools.many2manyne2", 
										   currentClass.getSchemaFileName ());
			JCompositeField field1 = (JCompositeField)allFields.elementAt (0);
			JCompositeField field2 = (JCompositeField)allFields.elementAt (1);

			writeMMCode (outFile, field1, field2, currentClass.getTableName ());

		} catch (Exception e) {
			System.out.println (e.getMessage ());
			System.out.println ("Unable to generate Many to many factory for class " + currentClass.getClassName ());
		}
	}


	private void writeMMCode (FileWriter outFile, JCompositeField field1,
							  JCompositeField field2, String tableName) throws IOException, SchemaException {
		JClass class1 = field1.getJClass ();
		JClass class2 = field2.getJClass ();
		// insert
		outFile.write ("\n\tprotected String makeInsertString () {\n");
		outFile.write ("\t\tString result = \"insert into " + tableName + "\" +\n");
		outFile.write ("\t\t\t\"(" + field1.getSqlName () + "," + field2.getSqlName () + ")");
		outFile.write ("\" +\n\t\t\t\" values (?, ?)\";\n");
		outFile.write ("\t\treturn result;\n\t}\n");

		// remove
		outFile.write ("\n\tprotected String makeDeleteString () {\n");
		outFile.write ("\t\tString result = \"delete from " + tableName + " where \" +\n");
		outFile.write ("\t\t\t\"" + field1.getSqlName () + "=? and "
					   + field2.getSqlName () + "=?\";\n");
		outFile.write ("\t\treturn result;\n\t}\n");

		// check if relationship exists ?
		outFile.write ("\n\tprotected String makeQueryString () {\n");
		outFile.write ("\t\tString result = \"select * from " + tableName + " where \" +\n");
		outFile.write ("\t\t\t\"" + field1.getSqlName () + "=? and "
					   + field2.getSqlName () + "=?\";\n");
		outFile.write ("\t\treturn result;\n\t}\n");

		/* 1 to 2*/
		boolean selfRel = class1.getClassName ().equals (class2.getClassName ());
		writeAtoB (outFile, field1, field2, tableName);
		writeDelete (outFile, field1, field2, tableName, selfRel);
		if (!selfRel) {
			writeAtoB (outFile, field2, field1, tableName);
			writeDelete (outFile, field2, field1, tableName, false);
		}

	}

	private void writeDelete (FileWriter outFile, JCompositeField field1, JCompositeField field2,
							  String tableName, boolean selfRel) throws IOException, SchemaException {
		JClass class1 = field1.getJClass ();
		outFile.write ("\n\tprotected String make" + class1.getClassName () + "DeleteString () {\n");
		outFile.write ("\t\tString result = \"delete from " + tableName + " where \" +\n");
		outFile.write ("\t\t\t\"" + field1.getSqlName () + "=?");
		if (selfRel)
			outFile.write (" or " + field2.getSqlName () + "=?");
		outFile.write ("\";\n");
		outFile.write ("\t\treturn result;\n\t}\n");
	}

	private void writeAtoB (FileWriter outFile, JCompositeField field1,
							  JCompositeField field2, String tableName) throws IOException, SchemaException {
		JClass class1 = field1.getJClass ();

		outFile.write ("\n\tprotected String make"+class1.getClassName ()+"sLookupString () {\n");
		outFile.write ("\t\tString result = \"select * from " + tableName + " mm, \" +\n");
		outFile.write ("\t\t\t\"" +class1.getTableName ()+" ft where \" + \n");
		outFile.write ("\t\t\t\"mm."+ field1.getSqlName () + "=ft." + class1.getPrimaryKey ());
		outFile.write ("\" +\n\t\t\t\" and mm." + field2.getSqlName ()+"=?");
		if (class1.isEndDateable ())
			outFile.write (" and ft." + END_DATE + " is null");
		outFile.write ("\";\n");
		outFile.write ("\t\treturn result;\n\t}\n");
	}

	public static final String VERSION_NUMBER = "version_number";


	static HashSet nativeClassSet = new HashSet ();
	static HashMap nativeSetTypes = new HashMap ();


	// this is the Abra to db type map
	protected HashMap typeMap = null;

	public HashMap getTypeMap () {
		if (typeMap == null)
			createTypeMap ();
		return typeMap;
	}

	protected void createTypeMap () {
		typeMap = new HashMap ();
		typeMap.put (INTEGER, "int");
		typeMap.put (LONG, "int"); // no difference in the db..
		typeMap.put (INTEGER_OBJ, "int");
		typeMap.put (STRING, "varchar"); // needs length arg
		typeMap.put (DOUBLE, "double precision");
		typeMap.put (DOUBLE_OBJ, "double precision");
		typeMap.put (FLOAT, "float");
		typeMap.put (CLOB, "clob");
		typeMap.put (BLOB, "blob");
		typeMap.put (TIMESTAMP, "date");

		typeMap.put (CHARACTER, "char (1)");
		typeMap.put (BOOLEAN, "char(1)");
		typeMap.put (BOOLEAN_OBJ, "char(1)");

		typeMap.put (BIG_DECIMAL, "number"); //needs length arg ie 38,3
		typeMap.put (BIG_INTEGER, "number(38,0)");
	}

	{
		nativeClassSet.add ("Integer");

		nativeClassSet.add ("Double");
		//		nativeClassSet.add ("Character");
		nativeClassSet.add ("Long");

		nativeSetTypes.put ("Integer", new NativeObjectMapping ("Integer",
																"Int",
																"intValue ()",
																"INTEGER"));
		nativeSetTypes.put ("Long", new NativeObjectMapping ("Long",
															 "Long",
															 "longValue ()",
															 "LONG"));
		nativeSetTypes.put ("Double", new NativeObjectMapping ("Double",
															   "Double",
															   "doubleValue ()",
															   "DOUBLE"));
														
	}

	class NativeObjectMapping {
		String javaClass;
		String setName;
		String nativeGetter;
		String sqlTypes;
		public NativeObjectMapping (String c, String s, String g, String t) {
			javaClass = c;
			setName = s;
			nativeGetter = g;
			sqlTypes = t;
		}
	}

	abstract class InlineFieldWriter {
		JCompositeField jcf;
		JClass currentClass;

		InlineFieldWriter (JCompositeField jcf, JClass currentClass) {
			this.jcf = jcf;
			this.currentClass = currentClass;
			prefix = jcf.getPrefix () + "_";
		}

		String prefix = null; 

		public void execute (FileWriter outFile, boolean isFirst) 
			throws SchemaException, IOException {
			JClass fieldClass = jcf.getJClass ();
			Vector fields = new Vector (fieldClass.getAllFields ().values ());
			String subPrimaryKey = fieldClass.getPrimaryKey ();
			writeOnce (outFile, fieldClass);

			for (int j = 0; j < fields.size (); j++) {
				JField subField = (JField)fields.elementAt (j);
				String sqlName = subField.getSqlName ();
				String preName = jcf.getJavaName () + "_";
				
				if (!subField.isDbColumn () || sqlName.equals (subPrimaryKey)) {
					// do nothing ..
				}
				else if (subField instanceof JCompositeField) {
					throw new SchemaException ("ephman.abra.tools.nocompinline", 
											   currentClass.getSchemaFileName ()); 
				}
				else {
					
					writeOneField (outFile, subField, sqlName, preName, isFirst);
					if (isFirst)
						isFirst = false;
				}
			}
		}
		
		public abstract void writeOneField (FileWriter outFile, JField subField,
											String sqlName, String preName, boolean isFirst)
			throws IOException, SchemaException;

		public void writeOnce (FileWriter outFile, JClass fieldClass) throws IOException {
			// override if neccessary
		}
	}
	

}
