package org.ephman.abra.tools;

import java.util.*;

/** a class for Java classes !!
* to describe a Java class and all it's fields with mapping to
* XML and SQL
* @version 0.0.1
* @author Paul M. Bethe
*/

public class JClass {

	HashMap _plugins = new HashMap ();
	public HashMap getPlugins () { return _plugins; }

	boolean versioned = true;
	public void setVersioned (boolean v) { versioned = v; }
	public boolean isVersioned () { return versioned; }

	Vector constraintList = new Vector ();

	public void addConstraint (JConstraint jcons) {
		constraintList.addElement (jcons);
	}

	public Iterator getConstraints () {
		return constraintList.iterator ();
	}

	Vector indexes;
	
	/**
	   * Get the value of indexes.
	   * @return value of indexes.
	   */
	public Vector getIndexes() {return indexes;}
	
	/**
	   * Add an index.
	   * @param ji the new Jindex
	   */
	public void addIndex(JIndex  ji) {this.indexes.addElement (ji);}
	
	HashMap viewList;

	public void addView (JView jv) {
		viewList.put (jv.getFormatName (), jv);
	}

	public void addToView (String formatName, JField jf, String viewFormat, boolean asView) {
		JView jv = (JView)viewList.get (formatName);
		if (jv == null) {
			jv = new JView (formatName);
			viewList.put (formatName, jv);
		}
		jv.addToFieldList (new JFieldView (jf, viewFormat, asView));
	}

	public JView getView (String formatName) {
		return (JView)viewList.get (formatName);
	}

	public Iterator getViewList () {
		return viewList.values ().iterator ();
	}

	protected JClass superClass;
	protected String superClassName;

	protected TreeMap fieldList;

	protected String className;
	protected String xmlName;

    protected String viewName;

	protected String packageName;

	protected String tableName;

	protected String primaryKey;

	protected String primaryKeyJava;

    protected String classDescription;

    protected boolean _manyToMany;

	protected boolean _isLeaf;

	private int fieldIndex = -1;

	public JClass (String className, String packageName) {
		this.className = className;
		this.packageName = packageName;
		this.superClass = null;
		fieldList = new TreeMap (new JFieldComparator ());
		this._isLeaf = true; // assume a leaf
		this.classDescription = "";
		this.primaryKeyJava = "";
        this._manyToMany = false;
		this._hasParentFactory = true;
		this.parentFactory = "";
        this.tableName = "";
		this._implements = "";
        this.viewName = "";
		this.schemaFileName = "";
		this.viewList = new HashMap ();
		this.indexes = new Vector ();
	}

    boolean is_abstract = false;

    public void setAbstract (boolean toSet) {
        is_abstract = toSet;
    }

    public boolean isAbstract () { return is_abstract; }

	String descendantName;
	public void setDescendantName (String des) {
		//		System.out.println (className + " has descendant " + des);
		this.descendantName = des;
	}
	public boolean hasDescendant () { 
		return Checks.exists (descendantName);
	}
	public String getDescendantName () {
		return !hasDescendant() ? className : descendantName;
	}

	public String getConstructorLine (String varName) {
		String result = "\t\t" + className + " " + varName + " = new ";
		result += getDescendantName () + " ();\n";
		return result;
	}

	String schemaFileName;

	/**
	   * Get the value of schemaFile.
	   * @return Value of schemaFile.
	   */
	public String getSchemaFileName() {return schemaFileName;}

	/**
	   * Set the value of schemaFile.
	   * @param v  Value to assign to schemaFile.
	   */
	public void setSchemaFileName(String  v) {this.schemaFileName = v;}


	boolean endDate;

	/**
	   * Get the value of endDate.
	   * @return Value of endDate.
	   */
	public boolean isEndDateable() {return endDate;}

	/**
	   * Set the value of endDate.
	   * @param v  Value to assign to endDate.
	   */
	public void setEndDate(boolean  v) {this.endDate = v;}


    public String getViewName () { return this.viewName; }

    public void setViewName (String foo) { this.viewName = foo; }

	protected String _implements;

	/**
	   * Get the value of implements.
	   * @return Value of implements.
	   */
	public String getImplements() {return _implements;}

	/**
	   * Set the value of implements.
	   * @param v  Value to assign to implements.
	   */
	public void setImplements(String  v) { this._implements = v;}


	public void setPrimaryKeyJava (String foo){ this.primaryKeyJava = foo; }

	public String getPrimaryKeyJava () { return this.primaryKeyJava; }

	public String getPackageName () { return this.packageName; }

	public String getClassName () { return this.className; }


     /* For mapping classes to queries instead of tables */
    String queryString = null;

    public String getQuery () {return queryString;}

    public void setQuery (String q) {queryString = q;}

    public boolean isQuery () {return exists (queryString);}

	public void setXmlNodeName (String name) { this.xmlName = name; }

	public String getXmlNodeName () { 
		if (this.xmlName != null && this.xmlName.length () > 0) {
			//			System.out.println ("Returning real XmlName of " + xmlName);
			return this.xmlName;

		}
		return this.className; 
	}

	public String getHashKey () {
		return this.packageName + "." + this.className;
	}

    public void setManyToMany () { this._manyToMany = true; }

    public boolean isManyToMany () { return this._manyToMany; }

	public void setClassDescription (String foo){ this.classDescription = foo; }

	public String getClassDescription () { return this.classDescription; }


	public boolean isLeaf () { return this._isLeaf; }

	public void addField (JField foo) {
		fieldList.put (foo.getJavaName (), foo);
	}

	private Iterator fieldIterator;

	public void resetFieldIteration () { fieldIterator = fieldList.values().iterator(); }

        public boolean hasMoreFields () {
            return fieldIterator.hasNext();
        }

	public JField getNextField () {
        return (JField)fieldIterator.next();
	}

	public JField getFieldByName (String name) {
		JField result = (JField)fieldList.get (name);
		if (result == null && this.superClass != null) {
			result = superClass.getFieldByName (name);
		}
		return result;
	}

	public boolean isInlineOnly () { return this._inlineOnly; }
	public void setInlineOnly (boolean b) { this._inlineOnly = b; }
	boolean _inlineOnly = false;

    protected void setInternal () { this._isLeaf = false; }
	/**
	   * Get the value of tableName.
	   * @return Value of tableName.
	   */
	public String getTableName() {return tableName;}

	/**
	   * Set the value of tableName.
	   * @param v  Value to assign to tableName.
	   */
	public void setTableName(String  v) {this.tableName = v;}

	protected String parentFactory;

	/**
	   * Get the value of parentFactory.
	   * @return Value of parentFactory.
	   */
	public String getParentFactory() {return parentFactory;}

	/**
	   * Set the value of parentFactory.
	   * @param v  Value to assign to parentFactory.
	   */
	public void setParentFactory(String  v) {this.parentFactory = v;}


	protected boolean _hasParentFactory;

	public void setHasParentFactory (boolean foo) {
		this._hasParentFactory = foo;
	}

	public boolean hasParentFactory () { return this._hasParentFactory; }

	/**
	   * Get the value of primaryKey.
	   * @return Value of primaryKey.
	   */
	public String getPrimaryKey() {
        if (primaryKey == null) {
            JField pf = getFieldByName (primaryKeyJava);
			if (pf != null)
				primaryKey = pf.getSqlName ();
		}
        return primaryKey;
    }

	/**
	   * Set the value of primaryKey.
	   * @param v  Value to assign to primaryKey.
	   */
	public void setPrimaryKey(String v) {this.primaryKey = v;}

	public void setParentClass (JClass parent) {
            if (parent != null) {
                this.superClass = parent;
                parent.setInternal ();
            }
        }

	public JClass getParentClass () { return this.superClass; }

	public void setParentClassName (String name) {
		this.superClassName = name;
	}

	public String getParentClassName () { return this.superClassName; }

	String storedProcedureName = null;

	public String getStoredProcedureName () {
		if (storedProcedureName == null)
			storedProcedureName = getNewProcName (this);
		return storedProcedureName;
	}
	public static int NAME_LENGTH = 20; // for oracle will Psql soon.
	public static HashSet procNamesMap;

	public static String getNewProcName (JClass jc) {
		if (procNamesMap == null) 
			procNamesMap = new HashSet ();
		String name = jc.getClassName ();
		if (name.length () > NAME_LENGTH) { // trunc..
			name = name.substring (0, NAME_LENGTH);
		}
		// check unique
		int index = 1;
		while (procNamesMap.contains (name)) {
			// do something..
			if (name.length () == NAME_LENGTH)
				name = name.substring (0, NAME_LENGTH-1);
			name += index;
		}
		procNamesMap.add (name);
		return name;
	}


	public TreeMap getAllFields () {
		TreeMap result = null;

		if (this.getParentClass () != null) 
			result = this.getParentClass ().getAllFields();
        else
			result = new TreeMap (new JFieldComparator ());

        result.putAll (this.fieldList);
		return result;
	}

	public String toFileString () {
		StringBuffer result = new StringBuffer("\n<class name=\"" + packageName + "." + className + "\"");
		if (exists(superClassName))
			result.append (" extends=\""+superClassName+"\"");
		if (exists(_implements))
			result.append (" implements=\""+_implements+"\"");
		result.append (">\n");
		if (exists (tableName))
			result.append ("\t<map-to table=\""+tableName+"\"/>");
		Iterator it = fieldList.values().iterator();
		while (it.hasNext ())
			result.append (((JField)it.next()).toFileString ()+"\n");
		// more
		result.append ("</class>\n");
		return result.toString ();
	}

	boolean exists (String n) { return n != null && !n.equals(""); }
}
