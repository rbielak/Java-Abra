package org.ephman.abra.tools.generators;

import org.ephman.abra.tools.*;
import org.ephman.abra.tools.plugins.*;

import java.io.*;
import java.util.*;

import org.apache.regexp.*;
/**
 *
 * generate the validator for a class
 * @author Paul M. Bethe
 * @version 0.0.2
 */

public class ValidatorGenerator extends GeneratorPlugin {

	final String [] cNames = {"validator"};
	final String [] fNames = {"validate"};

	public String [] getClassLevelNodeNames () { return cNames; }
	public String [] getFieldLevelNodeNames () { return fNames; }

	protected boolean hasDefault () { return true; }

	String getDefaultName (JClass currentClass) {
		return  currentClass.getPackageName() + ".Base"	+ currentClass.getClassName() + "Validator";
	}

	protected ClassPluginData createDefault (JClass currentClass) {
		String name = getDefaultName (currentClass);
		return new ClassPluginData (name, getDefaultFormatName ());
	}

	public String getName () { return "ValidatorGenerator";};
	/** call back when the node 'validator' which has been registered by this class is encountered
	 * @param nodeName should be validator
	 * @param attributes all the attributes in the xml node in key-value hmap
	 *   looking for name="fully-qualified class name" and format="mt54x" etc.
	 * @param currentClass the JClass on which this node occured
	 *
	 * @throws SchemaException if the data is invalid.
	 */
	public void handleClassLevelNode (String nodeName, Map attributes, JClass currentClass) 
		throws SchemaException {
		String name = (String)attributes.get ("name");
		String format = (String)attributes.get ("format");
		assertMandatory (name, "name");
		assertMandatory (format, "format");
		ClassPluginData plugin = new ClassPluginData (name, format);
		registerPlugin (plugin, currentClass);
	}

	/** call back when the node 'nodeName' which has been registered by this class is encountered
	 * @param nodeName the node name ie "view"
	 * @param attributes all the attributes in the xml node in key-value hmap
	 * @param currentClass the JClass on which this node occured
	 * @param currentField the JField on which this node occured
	 *
	 * @throws SchemaException if the data is invalid.
	 */
	public void handleFieldLevelNode (String nodeName, Map attributes, 
											   JClass currentClass, JField currentField) 
		throws SchemaException {

		String name = (String)attributes.get ("name");
		String format = (String)attributes.get ("format");
		if (!Checks.exists (format))
			format = getDefaultFormatName ();
		String code = (String)attributes.get ("code");
		String otherFieldName = (String)attributes.get ("mandatory-if");
		String required = (String)attributes.get ("required");
		JValidate jv = null;
		if (!Checks.exists (otherFieldName)) { // try regex 
			String regex =  (String)attributes.get ("regex");
			if (Checks.exists (regex))
				jv = new JValidate (regex, name, JValidate.REGEX, code);
			else if (Checks.exists (required) && required.equals ("true")) { 
				// required="true"
				jv = new JValidate (JValidate.MANDATORY);
			}					
		} else {
			jv = new JValidate (otherFieldName, name, code);
		}
		jv.setJField (currentField);
		super.handleFieldLevelNode (jv, format, currentClass);
		
	}

	public void close () {
		// nothing
	}

	///
	private String outdir;
	private char fileSeperator;
	
	public ValidatorGenerator () {
		this.outdir = MapToJava.outdir;
		this.fileSeperator = MapToJava.fileSeperator.charAt (0);
	}

	public void generate (JClass currentClass) throws IOException, SchemaException {
		Map validators = getNamedMap (currentClass);
		Iterator valNames = validators.keySet ().iterator ();
		while (valNames.hasNext ()) {
			String thisValName = (String)valNames.next ();
			ClassPluginData thisPlugin = (ClassPluginData)validators.get (thisValName);
			String fname = thisPlugin.getName ().replace ('.', fileSeperator);
			FileWriter outFile = new FileWriter (outdir + fileSeperator + fname+ ".java");
			Debugger.trace ("Generating validator " + outdir + fileSeperator + fname, Debugger.VERBOSE);
			

			writeHeader (currentClass, thisPlugin, outFile);
			
			writeMethods (currentClass, thisPlugin, outFile);
			
			outFile.write ("\n}\n");
			outFile.close ();
		}
	}

	public void writeHeader (JClass currentClass, ClassPluginData thisPlugin, 
							 FileWriter outFile) throws IOException {
		String header = "package " + thisPlugin.getPackageName () + ";\n\n";
		header += "import org.ephman.abra.validation.*;\n";
		header += "import java.util.Vector;\n";

		header += "/** \n* a Validator for " + currentClass.getClassName() + " in " 
			+ thisPlugin.getFormatName () + " format\n";
         
		header += " * @version " + (new Date ()).toString () + "\n";
		header += " * @author generated by Dave Knull\n */\n\n";

		String toExt = " extends ";
		//if (thisPlugin.getFormatName ().equals ("")) // is default
			toExt += "ValidatorBase ";

		if (thisPlugin.getParentPlugin () != null) 
			toExt = "extends " + thisPlugin.getParentPlugin ().getName () + " ";
		header += "public class " + thisPlugin.getClassName ()
			+ toExt + "{\n\n";
		outFile.write (header);
	}

	String helper = "";
	String validate = "";

	public void writeMethods (JClass currentClass, ClassPluginData thisPlugin, 
							  FileWriter outFile) throws IOException, SchemaException
	{
		String varName = currentClass.getClassName ().substring (0,1).toLowerCase ()
			+currentClass.getClassName ().substring (1);
		validate = "\n\tpublic static ValidationResult validate (" + currentClass.getClassName ()
			+ " " + varName + ") {\n"; 
		// overloaded
		validate += "\t\tVector errors_ = new Vector ();\n";
		validate += "\t\tvalidate (" + varName + ", \"\", errors_); // call with empty path\n";
		validate += "\t\treturn new ValidationResult (errors_);";
		validate += "\n\t}\n\n";

		validate += "\n\tpublic static void validate (" + currentClass.getClassName ()
			+ " " + varName + ", Vector errors_) {\n"; 
		validate += "\t\tvalidate (" + varName + ", \"\", errors_);\n\t}\n";

		validate += "\n\tpublic static void validate (" + currentClass.getClassName ()
			+ " " + varName + ", String pathWay, Vector errors_) {\n"; 
		
		validate += "\t\tif (" + varName + " == null) return ;\n";
		validate += "\t\tif (pathWay != null && pathWay.length () > 0)\n" 
			+"\t\t\tpathWay += \".\";\n";
		// ## IM HERE..
		JClass parent = currentClass.getParentClass ();
		if (parent != null)  // use extension
		    validate += "\t\tBase" + parent.getClassName () + "Validator.validate (" + varName + ", pathWay, errors_);\n";
		//		validate += "\t\tVector errors_ = new Vector ();\n";

		// use inheritance instead.. 
		
		// Vector allFields = new Vector(currentClass.getAllFields ().values ());
		if (thisPlugin.getFormatName ().equals (getDefaultFormatName ())) 
		{ // is default ..
			for (currentClass.resetFieldIteration (); currentClass.hasMoreFields (); ) {
				JField jf = currentClass.getNextField ();
				String typ = jf.getObjectType ();
				if (jf.isRequired () && (typ.equals ("String") || typ.equals ("Timestamp") || typ.indexOf (".") != -1))
					validate += "\t\tassertMandatory (" + varName + ".get" + jf.getGetSet ()
						+ "(), pathWay, \"" + jf.getJavaName () + "\", errors_, " + varName + ");\n";
				if (jf instanceof JCompositeField) { // deep validate??
					// nothing yet
				} else { // native field 
					if (typ.equals ("String")) {
						String len = jf.getLength ();
					if (Checks.exists (len))
						validate +="\t\tcheckStringLength (" + varName + ".get" + jf.getGetSet ()
							+ "(), " + len+", pathWay,  \"" + jf.getJavaName () + "\", errors_, " + varName + ");\n";
					}
					
				}
			}
		} else { // not default
			validate += "\t\t"+getDefaultName (currentClass)+".validate (" + varName + ", pathWay, errors_);\n";
		}
		Vector v = thisPlugin.getAllFields ();
		for (int j=0; j < v.size (); j++) {
			JValidate jv = (JValidate)v.elementAt (j);
			writeJValidate (jv, varName, currentClass);
		}
		
		//		validate += "\t\tif (errors_.size () > 0) { // validation errors\n";
		//validate += "\t\t\tthrow new ValidationException (\"Error(s) in class " + 
		//currentClass.getClassName () + ":\", errors_);\n\t\t}
		validate += "\n\t}\n";
		outFile.write (validate);
		outFile.write ("\n\n" + helper);
	}


	void writeJValidate (JValidate jv, String varName, JClass currentClass) 
		throws SchemaException {
		try {			
			validate += jv.createAssert (varName, jv.getJField ());
			helper += jv.getMember ();
			
		} catch (RESyntaxException rs) {
			throw new SchemaException (currentClass, jv.getJField (),
									   rs.getMessage ());
		}
	}
}
