package org.ephman.abra.tools;

import java.io.*;
import java.util.*;
import org.ephman.utils.*;

/**
 * given a base output directory
 * generate a schema file
 * generic !! copy a db specific generator from sub directory
 * @author Paul M. Bethe
 * @version 0.0.2
 */

public abstract class GenericSchemaGenerator {

	protected String END_DATE = "end_date";
	protected String END_DATE_TYPE = "number(38,0)";
	protected String VERSION_NUMBER = "version_number";

	protected String outdir;
	protected char fileSeperator;
	//	protected boolean isPostgres;

	protected FileWriter schemaFile;
	protected FileWriter cleanupFile;

	protected HashSet constraintList;


	public GenericSchemaGenerator (String outFileName) throws IOException, SchemaException {

		this.constraintList = new HashSet ();
		this.schemaFile = new FileWriter (outFileName);
		int i = outFileName.lastIndexOf ('.');
		String cleanupName = outFileName.substring (0, i) + "_cleanup" + outFileName.substring (i);
		this.cleanupFile = new FileWriter (cleanupName);
		this.cleanup = new StringWriter ();

		this.constraints = "\n\n-- constraints and referential integrity\n\n";
		this.cleanupConstraints = "\n\n-- to cleanup the constraints \n\n";
	}

	protected StringWriter cleanup;
	protected String constraints;
	protected String cleanupConstraints;

	/** generic close if you want to write constraints -- override (see Oracle version) */

	public void close () throws IOException, SchemaException {
		this.schemaFile.close ();
		this.cleanup.close ();

		this.cleanupFile.write (cleanup.toString ());
		this.cleanupFile.close ();
	}

	public void generate (JClass currentClass) throws IOException, SchemaException {

		schemaFile.write ("\ncreate table " + currentClass.getTableName () + "(");

		String primaryKey = currentClass.getPrimaryKey (); // the sql name

		Vector allFields = new Vector (currentClass.getAllFields ().values());

		boolean is_first = true;

		JField primField = null;
		HashSet fieldNames = new HashSet ();

		if (Checks.exists (primaryKey)) { // set prim key first
			primField = currentClass.getFieldByName (currentClass.getPrimaryKeyJava ());
			writeFieldSchema (currentClass, primField, primaryKey, is_first);
			is_first = false;
			fieldNames.add (primField.getSqlName ());
		}


		for (int i = 0; i < allFields.size (); i++) {
			JField jf = (JField)allFields.elementAt (i);
			if (jf.isDbColumn () && jf != primField) {
				if (jf instanceof JCompositeField && ((JCompositeField)jf).isInline ()) {
					// deal with inlining..
					inlineField (currentClass, (JCompositeField)jf, fieldNames); 
				} else {
					if (!fieldNames.contains (jf.getSqlName ()))
						fieldNames.add (jf.getSqlName ());
					else
						Debugger.trace ("Duplicate column " + jf.getSqlName () + " in Class " + currentClass.getClassName (), Debugger.ERROR);
					writeFieldSchema (currentClass, jf, primaryKey, is_first);
				}
				is_first = false;

			}
		}

		if (currentClass.isEndDateable ())
			schemaFile.write (",\n\t" + END_DATE + " " + END_DATE_TYPE);
		if (currentClass.isVersioned ())
			schemaFile.write (",\n\t" + VERSION_NUMBER + " int");	
		schemaFile.write (");\n");

		if (!currentClass.isManyToMany ()) {			
			if (needsSequence ()) {
				cleanup.write (getDropSeqStmt (currentClass));
				schemaFile.write ("\ncreate sequence " + currentClass.getTableName () +"_seq;\n");
			}
		}
        else
        {
            String columns = "";
            boolean first_column = true;
    		for (int i = 0; i < allFields.size (); i++)
            {
    			JField jf = (JField)allFields.elementAt (i);
                if ( !jf.getSqlName().equalsIgnoreCase("") )
                {
                    if (!first_column)
                       columns +=",";
                    else
                       first_column = false;
                   columns += jf.getSqlName();
                }
    		}
            if (allFields.size () > 0  ) //&& !isPostgres)
            {				
    			constraints += ( "\nalter table " + currentClass.getTableName () + " add constraint "
                    + currentClass.getTableName() +"_unq primary key " + " (" + columns + ");\n");
            }
        }

		Iterator cons = currentClass.getConstraints ();
		while (cons.hasNext ()) {
			JConstraint jcons = (JConstraint)cons.next ();
			String sOut = convertFields (jcons.getConstraint (), currentClass);
			String consName = makeConstraintName (jcons.getName ());

            cleanUpConstraint(currentClass, consName);

            addConstraint(currentClass, consName, jcons, sOut);
        }
		Iterator indexes = currentClass.getIndexes ().iterator ();
		while (indexes.hasNext ()) {
			JIndex index = (JIndex)indexes.next ();
			String sOut = convertFields (index.getFields (), currentClass);

            cleanUpIndex(currentClass, index);

            addIndex(currentClass, sOut, index);

        }
	}

    // The routines below can be redefined in specific schema generator
    // to handle custom syntax

    protected void addIndex(JClass currentClass, String sOut, JIndex index) {
        constraints += "\ncreate index " + index.getName () + " on " +
            currentClass.getTableName () + " (" + sOut + ");\n";
    }

    protected void cleanUpIndex(JClass currentClass, JIndex index) {
        cleanupConstraints += "\ndrop index " + index.getName () + ";\n";
    }

    protected void addConstraint(JClass currentClass, String consName, JConstraint jcons, String sOut) {
        constraints += "\nalter table " + currentClass.getTableName () + " add constraint "
            + consName + " " + jcons.getType () + " (" + sOut + ");\n";
    }

    protected void cleanUpConstraint(JClass currentClass, String consName) {
        cleanupConstraints += "\nalter table " + currentClass.getTableName ()
            + " drop constraint " + consName + ";\n";
    }


    // overrid if no sequences..
	boolean needsSequence () { return true; }
	// override if neccessary ..
	protected String getDropSeqStmt (JClass currentClass) {
		return "\ndrop sequence " + currentClass.getTableName () +"_seq;\n";
	}

	protected String convertFields (String fields, JClass currentClass) throws SchemaException, IOException {
		Vector tokens = Tokenizer.tokenize (fields, ",", "/");
		Debugger.trace (fields + " token-size=" + tokens.size (), Debugger.VERBOSE);
		String sOut;
		if (tokens == null || tokens.size () < 1)
			throw new SchemaException ("Invalid constraint specification ", currentClass.getSchemaFileName ());
		else
			sOut = jToSql ((String)tokens.elementAt (0), currentClass);
		for (int j=1;j < tokens.size (); j++) {
			sOut += "," + jToSql ((String)tokens.elementAt (j), currentClass);
		}
		return sOut;
	}


	protected String jToSql (String javaName, JClass currentClass) throws IOException, SchemaException {
		JField jf = currentClass.getFieldByName (javaName);
		if (jf == null)
			throw new SchemaException ("field " + javaName + " not a member of class " + currentClass.getClassName (), currentClass.getSchemaFileName ());

		return jf.getSqlName ();
	}

	// helper function for uniques

	protected void writeUniqueConstraint (JClass currentClass, JField jf) 
		throws IOException, 
			   SchemaException {
		schemaFile.write (" unique");
	}

	protected String makeConstraintName (String consName) throws SchemaException {
		boolean prev_constr = constraintList.contains (consName);
		if (prev_constr) { // need to find new name --
			Debugger.trace ("Changing duplicate constraint name " + consName, Debugger.SHORT);
			int i = 1;
			while (constraintList.contains (consName + i)) 
				i++;
			consName += i;
		}
		constraintList.add (consName);
		return consName;
	}


	/** generic primary in table creation.. */
	protected void writePrimaryKey (JClass currentClass, String sqlName) throws IOException {
	    schemaFile.write ("\n\t" + sqlName + " int not null, \n\tprimary key (" + sqlName + ")");		
	}


	public void inlineField (JClass currentClass, JCompositeField jcf, 
							 HashSet fieldNames) 
		throws IOException,
			   SchemaException {
		
		JClass fieldClass = jcf.getJClass ();
		Vector fields = new Vector (fieldClass.getAllFields ().values ());
		String primaryKey = fieldClass.getPrimaryKey ();
		String prefix = jcf.getPrefix ();
		for (int i = 0; i < fields.size (); i++) {
			JField jf = (JField)fields.elementAt (i);
			String sqlName = jf.getSqlName ();

			if (!jf.isDbColumn () || sqlName.equals (primaryKey)) {
				// do nothing ..
			}
			else if (jf instanceof JCompositeField) {
				throw new SchemaException ("Composite Fields not allowed in inline objects", currentClass.getSchemaFileName ()); 
			}
			else 
				schemaFile.write (",\n\t"+ prefix + "_" + sqlName + " " + 
						  getSqlOutType (currentClass, jf));
			fieldNames.add (prefix + "_" + sqlName);
		}		
	}


	/* helper function to get the sql type to be printed. */
	protected String getSqlOutType (JClass currentClass, JField jf) throws SchemaException {
		return jf.getSqlType ();
	}		

	/* write the schema line for one field*/
	protected void writeFieldSchema (JClass currentClass, JField jf, String primaryKey, boolean is_first) throws IOException, SchemaException {
		String sqlName = jf.getSqlName ();
		String fieldType = jf.getSqlType ();

		String sep = is_first ? "" : ",";
		if (sqlName.equals (primaryKey)) {
			schemaFile.write (sep);
			writePrimaryKey (currentClass, sqlName);
		}
		else {
		    schemaFile.write (sep + "\n\t"+ sqlName + " " + 
				      getSqlOutType (currentClass, jf));
		    if (jf.isUnique ())
			writeUniqueConstraint (currentClass, jf);
		    if (jf.isRequired ())
				schemaFile.write (" not null");
		    else {
				String dbSpecificOptions = getDbSpecificOptions(currentClass, jf, primaryKey);
				if (dbSpecificOptions != null)
					schemaFile.write (" " + dbSpecificOptions);
		    }
		}

		if (jf instanceof JCompositeField && 
			((JCompositeField)jf).getJClass () != null && 
			((JCompositeField)jf).getJClass ().isLeaf ()) {

			// is a foreign key and has a class to reference
			JCompositeField jcf = ((JCompositeField)jf);
			JClass foreignClass = jcf.getJClass ();
			String cons_name = jcf.getConstraintName ();
			if (!Checks.exists (cons_name)) {
				cons_name = currentClass.getClassName () + jf.getJavaName ();
				if (cons_name.length () > 30)
					cons_name = cons_name.substring (cons_name.length() - 28);
			}

			cons_name = makeConstraintName (cons_name);
			writeForeignKey ( currentClass, cons_name, jf, foreignClass);
		
		} else if (Checks.exists (jf.getConstraint ())) {
			String cons_name = jf.getConstraintName ();
			if (!Checks.exists (cons_name)) 
				cons_name = "C_" + jf.getJavaName();
			cons_name = makeConstraintName (cons_name);
			writeCheckConstraint ( currentClass, cons_name, jf);
			   
		}

	}

	/**
     * Return any required DB specific field options. Override as needed.
     */
    protected String getDbSpecificOptions(JClass currentClass, JField jf, String primaryKey) throws SchemaException {
        return null;
    }

    /* override to insert constraint in table definition */
	protected void writeCheckConstraint (JClass currentClass, String cons_name, JField jf)
		throws IOException {

		constraints += "\nalter table " + currentClass.getTableName () + " add constraint "
				+ cons_name + " check (" + jf.getSqlName () + jf.getConstraint () +");\n";
						
			cleanupConstraints += "\nalter table " + currentClass.getTableName () + " drop constraint "
				+ cons_name +" ;\n"; 	
	}

	// standard using 'alter table'  
	// dbs like postgres can override and insert inline..

	protected void writeForeignKey (JClass currentClass, String cons_name, JField jf,
									JClass foreignClass) throws IOException {

		constraints += "\nalter table " + currentClass.getTableName () + " add constraint "
			+ cons_name + " foreign key (" + jf.getSqlName ()+") references "
			+ foreignClass.getTableName () + "(" + foreignClass.getPrimaryKey () + ");\n";
		cleanupConstraints += "\nalter table " + currentClass.getTableName () + " drop constraint "
			+ cons_name +" ;\n";
	}

}
