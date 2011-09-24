package org.ephman.abra.tools.generators;

/**
 * Title:			JValidate <p>
 * Description:  	represents a validation constraint to be <p>
 * Copyright:	Copyright (c) 2001 Paul Bethe and Richie Bielak<p>
 * @author		 	Paul Bethe
 * @version 0.1.0
 */
import org.apache.regexp.*;
import org.ephman.abra.tools.plugins.*;
import org.ephman.abra.tools.*;


public class JValidate extends FieldPluginData {
	public static final int MANDATORY = 4;
	public static final int MANDATORY_IF = 1;
	public static final int MANDATORY_IF_VALUE = 2;
	public static final int REGEX = 3;

	public JValidate (int type) {
		this.type = type;
	}

	public JValidate (String str, String constraintName, int type, String code) {
		this.otherFieldName = str;
		this.constraintName = constraintName;
		this.type = type;
		this.code = code;
	}

	public JValidate (String otherField, String constraintName, String code) {
		this (otherField, constraintName, MANDATORY_IF, code);
	}

	public JValidate (String otherField, String name, String value, String code) {
		this (otherField, name, MANDATORY_IF_VALUE, code);
		this.value = value;
		this.code = code;
	}

	String constraintName;
	String otherFieldName;
	String value = null;
    String code = null;
	int type;

	
	/*
	  public String createMembers (String varName, JField jf) {
	  String result = "";
	  switch (type) {
	  case REGEX:
	  result = "\n\tpublic static RE _RE" + jf.getGetSet () + " = new RE (\""
	  + otherFieldName + "\");\n";
	  break;
	  case MANDATORY_IF:
	  result = ""; //"\t\tif (" varN;
	  break;
	  // others..
	  
	  }
	  return result;
	  }*/

	public String getMember () {
		return fieldMember;
	}
	
	public String fieldMember = "";
	// code to create assertion 
	public String createAssert (String varName, JField jf) throws RESyntaxException {
		String result = "";

		String cS = (!Checks.exists (code)) ? "ValidationCodes.REGEXP_FAILED":
		    code ;
		switch (type) {
		case REGEX:

			RE testRE = new RE (otherFieldName); // check if re is valid
			String regex = otherFieldName; // .replace ("\\","\\\\"); // escape slashes
			for (int i = regex.indexOf ("\\"); i != -1; i = regex.indexOf ("\\", i+2))
			    regex = regex.substring (0, i) + "\\\\" +regex.substring (i+1);
			
			String progName = "_" + jf.getJavaName () + "Program";
			String outName = (!Checks.exists (constraintName)) ? regex
				: constraintName;
			result = "\t\tcheckRegex (" + varName + ".get" + jf.getGetSet () + "(), "
				+ progName + ", \"" + outName + "\", pathWay, \"" + jf.getJavaName ()+ "\", " + cS + ", errors_, " + varName + ");\n";

			fieldMember = "\tpublic static org.apache.regexp.REProgram " + progName
				+ " = getREProgram (\"" + regex + "\");\n";
			break;
		case MANDATORY:
			result = "\t\tassertMandatory (" + varName + ".get" + jf.getGetSet ()
				    + "(), pathWay,  \"" + jf.getJavaName () + "\", errors_, " + varName + ");\n";
			break;
		case MANDATORY_IF:
			result = ""; //"\t\tif (" varN;
			break;
		// others..
		
		}
		return result;
	}

}
