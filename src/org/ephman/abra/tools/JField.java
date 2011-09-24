package org.ephman.abra.tools;

/**
 * Title:			JField <p>
 * Description:  	Represents a java field - and its mapping to SQL and XML <p>
 * Copyright:	Copyright (c) 2001 Paul Bethe and Richie Bielak<p>
 * @author		 	Paul Bethe
 * @version 0.1.0
 */
import java.util.Vector;

public class JField {

	// JCF will override with a value
	public boolean isInline () { return false; }

	String constraintName;
	
	/**
	   * Get the value of constraint.
	   * @return Value of constraint.
	   */
	public String getConstraintName() {return constraintName;}
	
	/**
	   * Set the value of constraint.
	   * @param v  Value to assign to constraint.
	   */
	public void setConstraintName(String  v) {this.constraintName = v;}

	String constraint;
	
	/**
	   * Get the value of constraint.
	   * @return Value of constraint.
	   */
	public String getConstraint() {return constraint;}
	
	/**
	   * Set the value of constraint.
	   * @param v  Value to assign to constraint.
	   */
	public void setConstraint(String  v) {this.constraint = v;}

	
	String description;
	
	/**
	   * Get the value of description.
	   * @return value of description.
	   */
	public String getDescription() {return description;}
	
	/**
	   * Set the value of description.
	   * @param v  Value to assign to description.
	   */
	public void setDescription(String  v) {this.description = v;}
	

	boolean unique;
	
	/**
	   * Get the value of unique.
	   * @return Value of unique.
	   */
	public boolean isUnique() {return unique;}
	
	/**
	   * Set the value of unique.
	   * @param v  Value to assign to unique.
	   */
	public void setUnique(boolean  v) {this.unique = v;}
	

    public boolean isDate () {
        return this._isdate;
    }
    public void setAsDate () { _isdate = true; }

    private boolean _isdate = false;

	public boolean isDbColumn () {
		return !this.isCollection () &&  !this.isTransient();
	}


	String getSet = null;

	public String getGetSet () {
		if (getSet == null)
			getSet = javaName.substring (0,1).toUpperCase () + javaName.substring (1);
		return getSet;
	}

	String javaName;

	/**
	   * Get the value of javaName.
	   * @return Value of javaName.
	   */
	public String getJavaName() {return javaName;}

	/**
	   * Set the value of javaName.
	   * @param v  Value to assign to javaName.
	   */
	public void setJavaName(String  v) {this.javaName = v;}

	
	TypeMap dkType;
	
	/**
	   * Get the value of dkType.
	   * @return value of dkType.
	   */
	public TypeMap getDkType() {return dkType;}
	
	/**
	   * Set the value of dkType.
	   * @param v  Value to assign to dkType.
	   */
	public void setDkType(TypeMap  v) {
		this.dkType = v;
		if (this.dkType.getAbraTypeName ().equals (AbraTypes.TIMESTAMP))
			setAsDate ();
	}

	public String getObjectType() {return dkType.getObjectTypeName ();}


	String mapFileType;
	public String getMapFileType() {return mapFileType;}
	public void setMapFileType(String  v) {this.mapFileType = v;}

	public String getObjectDefaultValue() {return dkType.getObjectDefaultValue ();}

	String sqlName;

	/**
	   * Get the value of sqlName.
	   * @return Value of sqlName.
	   */
	public String getSqlName() {return sqlName;}

	/**
	   * Set the value of sqlName.
	   * @param v  Value to assign to sqlName.
	   */
	public void setSqlName(String  v) {this.sqlName = v;}

	public boolean isTransient () {return (sqlName == null) || (sqlName == "");}

	public String getSqlType() {return dkType.getSqlTypeName (this); }

  // Collection information
//	protected boolean _isAVector;
//	public boolean isVector () { return _isAVector; }

	// This routine is needed in DTD Converter
	public void setToVector () { 
		_isCollection = true;
		_collectionType = "vector";
	}
//	protected boolean _isArray = false;
//	public boolean isArray () { return _isArray; }
//	public void setToArray () { _isArray = true; }

  protected boolean _isCollection;
  protected String _collectionType;

  public boolean isCollection() {
    return _isCollection;
  }

  public void setCollection(boolean _isCollection) {
    this._isCollection = _isCollection;
  }

  public String getCollectionType() {
    return _collectionType;
  }

  public void setCollectionType(String _collectionType) {
    this._collectionType = _collectionType;
  }

  // These rotuines are here for backwards compatibility
  public boolean isVector () {
    return _isCollection && (_collectionType.equals ("vector"));
  }

  public boolean isArray () {
    return _isCollection && _collectionType.equals ("array");
  }

  public boolean isArrayList () {
    return _isCollection && _collectionType.equals ("ArrayList");
  }

	protected boolean _noMarshal = false;

    public boolean canMarshal () { return !_noMarshal; }

    protected boolean _xmlAttribute = false;

    public boolean isXmlAttribute () { return _xmlAttribute; }

	public boolean isRequired () { return this._required; }
	
	public void setRequired () { this._required = true; }
	
	boolean _required = false;

	//	public void addValidator (JValidate jv) { this.validators.add (jv); }
	//	public Vector getValidators () { return validators; }
	//	Vector validators = new Vector ();

	public String getLength () { return length; }
	public void setLength (String len) { this.length= len; }
	String length = "";


	public String toFileString () {
		
		StringBuffer result = new StringBuffer ("\t<field name=\"" + javaName + "\" type=\"" 
												+ mapFileType + "\"");
		if (exists (length))
			result.append (" len=\""+length+"\"");
		if (_required)
			result.append (" required=\"true\"");
		if (isCollection ())
			result.append (" collection=\"" + _collectionType + "\"");
		String nodes = "";
		if (exists (sqlName))
			nodes +="\n\t\t<sql name=\""+sqlName+"\"/>";
		if (_xmlAttribute)
			nodes +="\n\t\t<xml node=\"attribute\"/>";
		// Validators..
		if (exists (nodes))
			result.append (">" + nodes + "\n\t</field>");
		else
			result.append ("/>");
		return result.toString ();
	}
		
	boolean exists (String n) { return n != null && !n.equals(""); }
}
