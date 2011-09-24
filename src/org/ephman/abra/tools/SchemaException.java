package org.ephman.abra.tools;

/** Exceptions to throw if invalid Xml schema is parsed.. 
 * (duplicate columns or constraint names..
 * @author Paul M. Bethe
 * @version (3/2/01)
 */
import java.text.ParseException;
import org.ephman.utils.*;
import org.ephman.abra.utils.AbraException;


public class SchemaException extends AbraException {

	public SchemaException (String error, String fileName) {
		super (error, fileName);
		this.fileName = fileName;
		//		this.lineNum = linenum;
	}

	public SchemaException (String error, Object [] params) {
		super (error, params);
	} 

	/** incase another exception was caught and wrapped.. */
	public SchemaException (JClass currentClass, JField jf, String msg, Exception prevEx) 
	{
		this (currentClass,jf, msg);
		super.previousException = prevEx;
	}

	public SchemaException (JClass currentClass, JField jf, String msg) {
		super ("ephman.abra.tools.fatal", new Object[]{currentClass.getSchemaFileName (),
														  currentClass.getClassName (),
														  jf.getJavaName (),
														  jf.getObjectType (),
														  MessageTranslator.translate (msg, null)
														  });
		this.fileName = currentClass.getSchemaFileName ();
	}

	String fileName;
	
	/**
	   * Get the value of fileName.
	   * @return Value of fileName.
	   */
	public String getFileName() {return fileName;}
	
	/**
	   * Set the value of fileName.
	   * @param v  Value to assign to fileName.
	   */
	public void setFileName(String  v) {this.fileName = v;}
	
	int lineNum;
	
	/**
	   * Get the value of lineNum.
	   * @return Value of lineNum.
	   */
	public int getLineNum() {return lineNum;}
	
	/**
	   * Set the value of lineNum.
	   * @param v  Value to assign to lineNum.
	   */
	public void setLineNum(int  v) {this.lineNum = v;}

}
