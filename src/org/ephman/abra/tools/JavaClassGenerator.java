package org.ephman.abra.tools;

import java.io.*;
import java.util.*;

/**
 * given a base output directory
 * generate a class when given a JClass descriptor
 * @author Paul M. Bethe
 * @version 0.0.2
 */

public class JavaClassGenerator implements ClassGenerator, AbraTypes {

	private String outdir;
	private char fileSeperator;
	private String toImplement;

	/** make a new generator with a base directory of outdir
	 * so for foo.bar.FooBar if outdir = /home/dknull
	 * and file_sep = '/' then a class /home/dknull/foo/bar/FooBar.java is created
	 * @param outdir the base directory for writing files
	 * @param file_sep the file seperator ('/' for unix/linux , '\\' for Windows)
	 */

	public JavaClassGenerator (String outdir, char file_sep, String toImp) {
		this.outdir = outdir;
		this.fileSeperator = file_sep;
		this.toImplement = toImp;
	}

	public void close () {} // nothing needed

	public void generate (JClass currentClass) throws IOException, SchemaException {
		boolean isDatabaseClass = MapToJava.makeFactories;
        String fname =  currentClass.getPackageName().replace('.', fileSeperator) + fileSeperator
              + currentClass.getClassName() + ".java";
        FileWriter outFile = new FileWriter (outdir + fileSeperator + fname);
		Debugger.trace ("Generating class " +outdir + fileSeperator + fname, Debugger.VERBOSE);
        writeClassHeader (outFile, currentClass, isDatabaseClass);

        String constructorString = "";
		Debugger.trace ("Class-> " + currentClass.getClassName () +
						" PrimKeyJ: " + currentClass.getPrimaryKeyJava() +
						" PrimKeyS: " + currentClass.getPrimaryKey (),
						Debugger.ALL);

		currentClass.resetFieldIteration();
		while (currentClass.hasMoreFields()) {
            JField jf = currentClass.getNextField();
            writeFieldCode (outFile, jf);
			Debugger.trace ("\tField: " + jf.getJavaName (), Debugger.ALL);
            constructorString += "\t\t_" + jf.getJavaName() + " = " + getDefaultValue(jf) + ";\n";
        }



        // now do views --
		Iterator i = currentClass.getViewList ();
		while (i.hasNext ()) {
			JView view = (JView)i.next ();
			writeView (view, currentClass, outFile);
        }

        writeClassTrailer (outFile, currentClass, constructorString);
		outFile.close ();
    }

	public static String INIT_VIEW_NAME = "initializeFromView";

	private void writeView (JView view, JClass currentClass, FileWriter classFile) throws IOException, SchemaException{
		String fname =  view.getViewName().replace('.', fileSeperator) + ".java";
        FileWriter outFile = new FileWriter (outdir + fileSeperator + fname);
		writeViewHeader (outFile, view, currentClass);
		String constructorString = "";

		String u_name = view.getFormatName ();
		u_name = u_name.substring (0,1).toUpperCase () + u_name.substring (1);

		JView top_view = view;
		if (view.getSuperView () != null ) {
			while (top_view.getSuperView () != null)
				top_view = top_view.getSuperView ();			
		}

		// write code to make w/ constructor //
		classFile.write ("\tpublic void " + INIT_VIEW_NAME + " ("+ top_view.getViewName() +" view) {\n");
		if (view.getSuperView () != null ) 
			classFile.write ("\t\tsuper." + INIT_VIEW_NAME + "(view);\n");
		
		String viewVarName = "view";
		if (top_view != view) {
			classFile.write ("\t\t"+view.getViewName() + " casted_view = "
							 + "("+view.getViewName()+")view;\n");
			viewVarName = "casted_view";
		}

		Vector fieldList = view.getFieldList ();
		for (int i=0; i< fieldList.size (); i++) {
			JFieldView fieldView = (JFieldView)fieldList.elementAt (i);
			JField jf = fieldView.getField ();
			if (jf instanceof JCompositeField) { // foreign 
				if (fieldView.getAsView() &&
					Checks.exists (fieldView.getViewFormat ()) && !jf.isVector ()) {

					classFile.write ("\t\tif (this.get" + jf.getGetSet ()
									 + "() != null && " + viewVarName
									 + ".get" + jf.getGetSet ()+"() != null)");
					
						// is a view
						classFile.write ("\n\t\t\tthis.get" + jf.getGetSet ()
										 + "().initializeFromView ("
										 +viewVarName +".get" + jf.getGetSet ()
										 + "());\n");
					//else if (jf.isVector () // nothing for now
				}
				else {} // single field ??
			}
			else {
				classFile.write ("\t\tthis.set" + jf.getGetSet ()+ " (" +
								 viewVarName + ".get" + jf.getGetSet ()
								 + " ());\n");
			}
		}
		classFile.write ("\t}\n\n");

		// now creation views
		classFile.write ("\tpublic " + top_view.getViewName() + " create" + u_name + "View () {\n");
		classFile.write ("\t\tHashMap viewContext = new HashMap ();\n");
		classFile.write ("\t\treturn this.create"+ u_name + "View (viewContext);\n\t}\n\n");

		classFile.write ("\tpublic " + top_view.getViewName() + " create" + u_name + "View (HashMap _viewContext) {\n");
		classFile.write ("\t\t" + view.getViewName() +" foo = (" +view.getViewName() 
						 +")_viewContext.get (this);\n");
		classFile.write ("\t\tif (foo == null) { //not found\n");
		classFile.write ("\t\t\tfoo = new " + view.getViewName() +" ();\n");
		classFile.write ("\t\t\t_viewContext.put (this, foo);\n");
		classFile.write ("\t\t\tthis.create" + u_name + "View (foo, _viewContext);\n\t\t}\n");
		classFile.write ("\t\treturn foo;\n\t}\n\n");

		classFile.write ("\tprotected void create" + u_name + "View (" + top_view.getViewName() +" foo, HashMap _viewContext) {\n");

		String varName = "foo";
		if (view.getSuperView () != null) {
			classFile.write ("\t\tsuper.create" + u_name + "View (foo, _viewContext);\n");
			classFile.write ("\t\t" + view.getViewName () + " casted_foo = (" + view.getViewName ()+ ")foo;\n");
			varName = "casted_foo";
		}

		//Vector fieldList = view.getFieldList ();
		for (int i=0; i< fieldList.size (); i++) {
			JFieldView fieldView = (JFieldView)fieldList.elementAt (i);
			JField jf = fieldView.getField ();
			writeViewFieldCode (outFile, fieldView, classFile, varName);
			String initVal = getDefaultValue(jf);
			if (!jf.isCollection () && jf instanceof JCompositeField && fieldView.getAsView ())
				initVal = "null";
			else if (jf instanceof JCompositeField && !fieldView.getAsView ()) { // get the field defval ..
				JField for_f = ((JCompositeField)jf).getJClass ().getFieldByName (fieldView.getViewFormat ());
				initVal = getDefaultValue (for_f);
			}
			constructorString += "\t\t_" + jf.getJavaName() + " = " + initVal + ";\n";
		}
		classFile.write ("\t}\n\n");
		writeViewTrailer (outFile, view, constructorString);
		outFile.close();
	}


    private void writeViewTrailer (FileWriter outFile, JView view, String constructorString) throws IOException, SchemaException {
          int i = view.getViewName ().lastIndexOf (".");
        String viewName = "";
        if (i != -1)
            viewName = view.getViewName ().substring(i + 1);
        else
            viewName = view.getViewName ();

        outFile.write ("\tpublic " + viewName + " () {\n");
        outFile.write (constructorString);
        outFile.write ("\t}\n\n} // -- End class " + viewName);
    }

    private void writeViewHeader (FileWriter outFile, JView view, JClass currentClass) throws IOException, SchemaException {
        int i = view.getViewName ().lastIndexOf (".");
        String className = "";
        if (i != -1) {
            String packageName = view.getViewName ().substring(0, i);
            className = view.getViewName ().substring(i + 1);
            outFile.write ("package " + packageName + ";\n\n");
        }
        else
            className = view.getViewName ();

		String ext = view.getSuperView () == null ? "" : "extends " + view.getSuperView ().getViewName () + " ";

		outFile.write ("import java.util.*;\n");
		outFile.write ("import java.sql.*;\n");
        outFile.write ("// Do not edit!! generated classes\n");
		outFile.write ("/** A Viewer to link" + currentClass.getClassName () + " to a message viewing format \n");
		outFile.write (" * @version " + (new Date ()).toString () + "\n");
		outFile.write (" * @author generated by Dave Knull\n */\n\n");
        outFile.write ("public class " + className + " " + ext +"implements org.ephman.abra.utils.Viewable " + "{\n\n");
    }

    private void writeViewFieldCode (FileWriter outFile, JFieldView fieldView, FileWriter classFile, String varName) throws IOException, SchemaException {
		JField jf = fieldView.getField ();
        String fieldName = jf.getJavaName();
        String fieldType = jf.getObjectType();
		String elType = "";
        
		if (jf instanceof JCompositeField && ((JCompositeField)jf).getJClass() != null) {
			if (fieldView.getAsView ()) {
				JView comp_view = ((JCompositeField)jf).getJClass ().getView(fieldView.getViewFormat());
				if (comp_view != null)
					fieldType = comp_view.getViewName();
				else
					fieldType = jf.getObjectType ();
			}
			else {
				JField for_field =  ((JCompositeField)jf).getJClass ().getFieldByName (fieldView.getViewFormat());
				if (for_field == null) {
				    String errMsg = "Foreign field " + fieldView.getViewFormat() + " not found: " + jf.getJavaName ();
					Debugger.trace (errMsg, Debugger.ERROR);
					fieldType = jf.getObjectType ();
				}
				else
					fieldType = for_field.getObjectType ();
				
			}
		}

		if (jf.isVector()) {  
			elType = fieldType; 
			fieldType = "java.util.Vector";
		} else if (jf.isArray ()) {
			elType = fieldType;
			fieldType += " []";
		} if (jf.isArrayList ()) {
      elType = fieldType;
      fieldType = "java.util.ArrayList";
    }

      outFile.write ("\tprivate " + fieldType + " _" + fieldName + ";\n\n");
      outFile.write ("\tpublic " + fieldType + " get" + jf.getGetSet() + " () { return this._"
              + fieldName + "; }\n\n");
      outFile.write ("\tpublic void set" + jf.getGetSet() + " (" + fieldType + " foo) {\n");
      outFile.write ("\t\tthis._"+ fieldName + " = foo;\n");
      outFile.write ("\t}\n\n");

		if (jf.isCollection ()) {
			outFile.write ("\tpublic void addTo" + jf.getGetSet ()
						   + " ("+ elType + " foo) {\n");
      if (jf.isVector()) {
			  outFile.write ("\t\t_" + fieldName + ".addElement (foo);\n\t}\n\n");
      }
      else if (jf.isArrayList()) {
        outFile.write ("\t\t_" + fieldName + ".add (foo);\n\t}\n\n");
      }
      else { // an array
        // TODO: figure out the correct code here
        outFile.write ("\t\t /* this does not work */\n\t}\n\n");
      }
		}

		// now rip code
		if (jf.isCollection ()) {
			if (jf instanceof JCompositeField) {
				String viewFormat = fieldView.getViewFormat ();
				String cast = "";
				JCompositeField jcf = (JCompositeField)jf;
				if (viewFormat != null && !viewFormat.equals ("")){
					viewFormat = viewFormat.substring (0,1).toUpperCase () + viewFormat.substring (1);
					if (fieldView.getAsView ())
						viewFormat = ".create" + viewFormat + "View (_viewContext)";
					else
						viewFormat = ".get" + viewFormat + " ()";
				}
				else {
					viewFormat = "";
					cast = "(" + jcf.getObjectType () + ")"; 
				}
					
				classFile.write ("\t\tfor (int i=0; i< this.get"+ jcf.getGetSet () + "().size(); i++) {\n");
				if (jcf.getJClass() != null) {
					// Figure out the cast that must be made here, in case the View has parent views
					JClass fieldClass = ((JCompositeField)jf).getJClass ();
					JView view = fieldClass.getView (fieldView.getViewFormat ());
					if (view != null)
						cast = "(" + view.getViewName() + ")";

					classFile.write ("\t\t\t"+ varName+ ".addTo" + jcf.getGetSet () + " (" + cast + "((" 
									 + jcf.getJClass().getPackageName () + "." 
									 + jcf.getJClass ().getClassName ()
									 + ")this.get" + jf.getGetSet () + "().elementAt (i))");
					classFile.write (viewFormat + ");\n");
				}
				else {
					classFile.write ("\t\t\t" + varName + ".addTo" + jcf.getGetSet() + "(" + cast + "this.get" + jcf.getGetSet()
									 + "().elementAt (i));\n");
				}
				classFile.write ("\t\t}\n");
				
			}
			else {
				classFile.write ("\t\t" + varName + ".set" + jf.getGetSet () + " (this.get" + jf.getGetSet () + "());\n");
			}
		}
		else if (jf instanceof JCompositeField) {
			String viewFormat = fieldView.getViewFormat ();
			if (viewFormat == null || viewFormat.equals ("")) {
				Debugger.trace ("Format not specified for composite field " + jf.getJavaName (), Debugger.SHORT);
				String cast = "(" +jf.getObjectType () + ")"; 
				classFile.write ("\t\t" + varName + ".set" + jf.getGetSet () + " (" + cast + "this.get" + jf.getGetSet () 
								 + "());\n");
			}
			else {
				// Figure out the cast that must be made here, in case the View has parent views
				JClass fieldClass = ((JCompositeField)jf).getJClass ();
				JView view = fieldClass.getView (fieldView.getViewFormat ());
				String cast = "";
				if (view != null)
					cast = "(" + view.getViewName() + ")";

				viewFormat = viewFormat.substring (0,1).toUpperCase () + viewFormat.substring (1);
				String vv = "this.get" + jf.getGetSet () + "()";
				
				classFile.write ("\t\t" + varName + ".set" + jf.getGetSet () + " (");
				classFile.write (vv + " == null ? ");

				if (fieldView.getAsView ()) { 
					classFile.write ("null : ");
					classFile.write (cast + vv + ".create" + viewFormat + "View (_viewContext));\n");
				}
				else {
					String jDefault = getDefaultValue (fieldClass.getFieldByName (fieldView.getViewFormat()));
					classFile.write (jDefault + " : ");
					classFile.write (cast + vv + ".get" + viewFormat + " ());\n");
				}
			}
		}
		else
			classFile.write ("\t\t" + varName +".set" + jf.getGetSet () + " (this.get" + jf.getGetSet () + "());\n");
    }

	private String getDefaultValue (JField jf) {
		if (jf.isVector ())
			return "new Vector ()";
    else if (jf.isArrayList ())
      return "new ArrayList ()";
		else return jf.getObjectDefaultValue ();
    // TODO: what about Array?
	}
	

	private void writeClassTrailer (FileWriter outFile, JClass currentClass, String constructorString) throws IOException, SchemaException {
		outFile.write ("\n\t/** the Default constructor for "+ currentClass.getClassName () + "\n");
		outFile.write ("\t* use set... methods to fill in fields\n\t*/\n");
		outFile.write ("\tpublic " + currentClass.getClassName () + " () {\n\t\tsuper ();\n");
		outFile.write (constructorString);
		outFile.write ("\t}\n\n} // end of " + currentClass.getClassName () + "\n");
	}

	private void writeFieldCode (FileWriter outFile, JField jf) throws IOException, SchemaException {
        String fieldName = jf.getJavaName();
        String fieldType = jf.getObjectType();

    if (jf.isVector()) fieldType = "java.util.Vector";
    else if (jf.isArrayList()) fieldType = "java.util.ArrayList";
		else if (jf.isArray ())
			fieldType += " []";

		String get_set_name = fieldName.substring (0,1).toUpperCase () + fieldName.substring (1);

		outFile.write ("\tprivate " + fieldType + " _" + fieldName + ";\n\n");
		outFile.write ("\tpublic " + fieldType + " get" + get_set_name + " () { return this._"
					   + fieldName + "; }\n\n");
		outFile.write ("\tpublic void set" + get_set_name + " (" + fieldType + " foo) {\n");

		outFile.write ("\t\tthis._"+ fieldName + " = foo;\n");

		if (jf instanceof JCompositeField && ((JCompositeField)jf).getJClass () != null &&
			!(((JCompositeField)jf).getJClass ().getTableName ().equals ("")) && 
			!jf.isCollection () &&!jf.isTransient ()) {

			String tmp = ((JCompositeField)jf).getJClass ().getPrimaryKeyJava ();
      if (tmp != null && !tmp.equals("")) {
  		   String get_set = tmp.substring (0,1).toUpperCase () + tmp.substring (1);
			   outFile.write ("\t\tthis._"+ fieldName + "Oid = foo != null ? foo.get" + get_set + " () : 0; // set the oid to zero if object == null\n");
      }

			outFile.write ("\t}\n\n");

			outFile.write ("\tprivate int _" + fieldName + "Oid = 0;\n\n");
			outFile.write ("\tpublic int get" + get_set_name + "Oid () { return this._"
					   + fieldName + "Oid; }\n\n");
			outFile.write ("\tpublic void set" + get_set_name + "Oid (int foo) {\n");
			outFile.write ("\t\tthis._"+ fieldName + "Oid = foo;\n\t}\n\n");
		} else
			outFile.write ("\t}\n\n");

		// code for vectors
		if (jf.isCollection()) {
            //int i = jf.getObjectType().lastIndexOf (".");
			//String add_name = i == -1 ? get_set_name.substring(0, get_set_name.length() - 1)
                      //: jf.getObjectType().substring(i + 1);

			outFile.write ("\tpublic void addTo" + get_set_name
						   + " (" + jf.getObjectType ()+ " foo) {\n");
      if (jf.isVector()) {
			  outFile.write ("\t\t_" + fieldName + ".addElement (foo);\n\t}\n\n");
      }
      else if (jf.isArrayList ()) {
         outFile.write ("\t\t_" + fieldName + ".add (foo);\n\t}\n\n");
      }
      else { // an array
        outFile.write ("\t\t /* This does not work */ \n\t}\n\n");
        // TODO: write proper code here
      }
		}
	}

	private void writeClassHeader (FileWriter outFile, JClass jc, boolean isDatabaseClass)
		throws IOException, SchemaException {
		String packageName = jc.getPackageName ();
		JClass parentClass = jc.getParentClass ();
		String extendsClass = "";

		if (parentClass == null) {
			extendsClass = jc.getParentClassName();

        } else {
			// Check if the package of the parent is the same
			if (!jc.getPackageName ().equals (parentClass.getPackageName ()))
				if (parentClass.getPackageName () != null)
					extendsClass = parentClass.getPackageName () + ".";
			extendsClass  += parentClass.getClassName ();
		}

        String extra = "";
        String extendString = "";
        if (!extendsClass.equals("")) {
            extendString = "extends " + extendsClass + " ";
            if (parentClass == null && isDatabaseClass) { // implement Versionable
                 //extra =
            }
        } else if (isDatabaseClass) // none try Versioned..
            if (jc.isVersioned())
			    extendString = "extends org.ephman.abra.utils.Versioned ";
            else
                extendString = "extends org.ephman.abra.utils.Identified ";
        String impString = "";
		String jcImp = jc.getImplements ().replace (';', ',');
        if (!this.toImplement.equals ("")) {
            if (jc.getImplements ().equals (""))
                impString = "implements " + toImplement + " ";
            else
                impString = "implements " + toImplement + ", " + jcImp + " ";
        }
        else if (!jc.getImplements ().equals (""))
            impString = "implements " + jcImp + " ";
		String abst = (jc.hasDescendant  () || jc.isAbstract ()) ? "abstract " : "";

		outFile.write ("package " + packageName + ";\n\n");
		outFile.write ("import java.sql.*;\nimport java.util.*;\n\n");
		outFile.write ("// Do not edit!! generated classes\n");
		outFile.write ("/** " + jc.getClassName () + " " + jc.getClassDescription () + "<p>\n");
		outFile.write (" * XMLSource: " + jc.getSchemaFileName () + "\n");
		outFile.write (" * @version " + (new Date ()).toString () + "\n");
		outFile.write (" * @author generated by Dave Knull\n */\n\n");
		outFile.write ("public " + abst + "class " + jc.getClassName() + " " + extendString + impString + "{ \n\n");

		if (jc.hasDescendant ()) {
			String [] methods = new String [] {"prepareToStore", "makeTransient" };
			for (int i=0; i<methods.length; i++)
				outFile.write ("\tpublic abstract void " + methods[i]
							   + " ();\n\n");
		}
	}

	// implementation of class-generator call 
	public Map getTypeMap () {
		return typeMap;
	}

	private static HashMap typeMap = new HashMap ();
	
	static {
		//		TypeMap tm = null;
		TypeMap tm = new TypeMap (INTEGER, "int", "0", null);
		typeMap.put (INTEGER, tm);
		typeMap.put (STRING, new TypeMap (STRING, "String", "\"\"", null, true));
		typeMap.put (LONG, new TypeMap (LONG, "long", "0", null));
		typeMap.put (DOUBLE, new TypeMap (DOUBLE, "double", "0.0", null));
		typeMap.put (FLOAT, new TypeMap (FLOAT, "float", "0.0", null));
		typeMap.put (CLOB, new TypeMap (CLOB, "String", "\"\"", null));
		typeMap.put (BLOB, new TypeMap (BLOB, "byte []", "null", null));
		typeMap.put (TIMESTAMP, new TypeMap (TIMESTAMP, "Timestamp", "null",
											 null));
		typeMap.put (BOOLEAN, new TypeMap (BOOLEAN, "boolean", "false", null));
		typeMap.put (CHARACTER, new TypeMap (CHARACTER, "char", "' '", null));
		
		typeMap.put (INTEGER_OBJ, new TypeMap (INTEGER_OBJ, "Integer", "null",
											   null));
		typeMap.put (DOUBLE_OBJ, new TypeMap (DOUBLE_OBJ, "Double", "null",
											  null));
		typeMap.put (BOOLEAN_OBJ, new TypeMap (BOOLEAN_OBJ, "Boolean", 
											   "null", null));

		typeMap.put (BIG_DECIMAL, new TypeMap (BIG_DECIMAL, "java.math.BigDecimal", 
											   "null", null, true)); //needs length arg
		typeMap.put (BIG_INTEGER, new TypeMap (BIG_INTEGER, "java.math.BigInteger", 
											   "null", null));
	}
}
