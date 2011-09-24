package org.ephman.abra.tools;

import java.util.*;
import java.text.SimpleDateFormat;
import java.lang.reflect.*;
import java.io.*;
import java.sql.Timestamp;
import javax.xml.parsers.*;
import org.xml.sax.*;
import org.w3c.dom.*;

import org.ephman.xml.*;
import org.ephman.utils.*;
import org.ephman.abra.utils.*;

    /** A class to marshall from Java classes to XML docs.
     * instantiate with a mapping file conforming to http://charzard/codegen.dtd
package org.ephman use the same package MapToJava to generate code from that map file
     * with approptiate get/set methods and for a Vector foo, addToFoo
     *
     *  @author Paul M. Bethe
     *  @version 0.0.1 (11/3/00)
    */

public class Marshaller extends XmlProcessor {

  /**
 *   constants to handle circular links
 *
 */
    protected static String _ID = "ID";

    protected static String _IDREF = "IDREF";

	protected static String RUN_TIME = "dk-java-type";

    protected ClassLoader classLoader;

	private MapToJava mtj;

    Class string;

    public Marshaller () throws Exception {
		super (false); // don't use abraXmlParser
        classLoader = ClassLoader.getSystemClassLoader();

        string = classLoader.loadClass ("java.lang.String");
        classList = new HashMap ();
        xmlNodeList = new HashMap ();

		// for doing descriptors..
		mtj = new MapToJava ("", false, false, false, false, false);
		TypeMapper.setObjectMap (new JavaClassGenerator ("",' ',"").getTypeMap ());
    }

    public Marshaller (String mapFileName) throws Exception {
        this ();

        addMapFile (mapFileName);
    }

	public void useAbraXmlParser () { 
		mtj.useAbraParser = true; 
		useAbraParser = true;
	}
	
    private HashMap classList;

	// list of formats for unmarshalling
	private String [] dateFormatStrings;
	// when marshalling use this format..
	private String outFormat;

    private HashMap xmlNodeList;

	private boolean idTags;
	/** method to remove "ID" tags (will throw exception if circular ref..)
	 */
	public void setIdTagsOff () {
		idTags = false;
	}

	private boolean writeTypes = false;
	public void setWriteRunTimeTypes (boolean b) { this.writeTypes = b; }

	/** method to allow setting of unmarshal formats..
	 * should be done while initializing mapFiles.. 
	 */
	public void setUnmarshalDateFormats (String [] formats) {
		this.dateFormatStrings = formats;
	}
	/* allows the setting of the string format to use for dates
	 * default is Unix seconds (since Unix 0)
	 */
	public void setMarshalDateFormat (String format) {
		this.outFormat = format;
    }

    /**
     * @param mapFileName the path and file name of an xml file conforming to
     *        codegen.dtd
     *  add a mapfile using a String fileName..
     */
    public void addMapFile (String mapFileName, boolean validating) throws Exception {
        addMapFile (new FileReader(mapFileName), validating, mapFileName);
    }
	/**
	 */
	public void addMapFile (String mapFileName) throws Exception {
		addMapFile (mapFileName, true); // assume validating..
	}
    /** to add a mapfile to a given marshaller
     *  thus extending the set of classes that the marshaller knows how to marshal and unmarshal
     *  @param reader -
	 *  @param validating whether or not to use any dtds to validate..
     *  @throws IOException if file un-openable
     */
    public void addMapFile (Reader reader, boolean validating, String inputName) throws Exception {

        try {
            mtj.applyRules(mtj.mapXMLFile(reader, validating), inputName);
            HashMap classTree = mtj.getClassTree();
            Iterator it = classTree.values().iterator();
			// clear lists..
			classList = new HashMap ();
			xmlNodeList = new HashMap ();
            while (it.hasNext()) {
                JClass jc = (JClass)it.next();
				String cname = jc.hasDescendant () ? jc.getDescendantName() 
					: jc.getPackageName() + "." + jc.getClassName ();
                XmlDescriptor new_desc = new XmlDescriptor (cname, jc.getXmlNodeName());
				//				Debugger.trace ("Added " + cname,
				//			Debugger.SHORT);
                Vector fields = new Vector(jc.getAllFields().values ());
                addDescriptorFields (new_desc, fields);
                classList.put(cname, new_desc);
                xmlNodeList.put(jc.getXmlNodeName(), new_desc);


				Iterator viewList = jc.getViewList ();
				while (viewList.hasNext()) {
					JView jv = (JView)viewList.next ();
					XmlDescriptor view_desc = new XmlDescriptor (jv.getViewName (), jv.getClassName ());
					Vector viewFields = jv.getAllFields ();
					addDescriptorViewFields (view_desc, viewFields);
					classList.put(jv.getViewName (), view_desc);
					xmlNodeList.put(jv.getClassName(), view_desc);
				}
            }
        }
        catch (XmlException e) {
            System.out.println ("Error parsing, " +e.getMessage());
			//            e.printStackTrace();
			throw e;
        }
    }

	/** add a vector of JFields to an XmlDescriptor */
	private void addDescriptorFields (XmlDescriptor new_desc, Vector fields) {
		for (int i = 0; i < fields.size(); i++) {
			JField jf = (JField)fields.elementAt(i);
			if (!jf._noMarshal) {				
				if (jf.isXmlAttribute())
					new_desc.addAttribute (jf);
				else
					new_desc.addElement (jf);
			} else {
				//System.out.println ("not adding unmarshal field.." + jf.getJavaName ());
			}
		}
	}

	private void addDescriptorViewFields (XmlDescriptor new_desc, Vector fields) {
		for (int i = 0; i < fields.size(); i++) {
			JFieldView jfv = (JFieldView)fields.elementAt(i);
			JField jf = jfv.getField ();
			if (jf instanceof JCompositeField && ((JCompositeField)jf).getJClass () != null) {
				JCompositeField jcf = (JCompositeField)jfv.getField ();
				if (jfv.getAsView ()) { // is complete foreign-view
					JCompositeField new_jcf = new JCompositeField ();
					JView for_view = jcf.getJClass ().getView (jfv.getViewFormat ());
					if (for_view != null) {
						new_jcf.setDkType (TypeMapper.getType (for_view.getViewName ()));
						new_jcf.setJavaName (jcf.getJavaName ());

						new_desc.addElement (new_jcf);
					}
				} else { // just foreign field (make a JField node)
					JField for_field = (jcf).getJClass ().getFieldByName (jfv.getViewFormat());
					if (for_field != null) {
						JField new_jf = new JField ();
						new_jf.setJavaName (jcf.getJavaName ());
						new_jf.setDkType (TypeMapper.getType (for_field.getObjectType ()));

						new_desc.addElement (new_jf);
					}
				}
			} else {
				if (jf.isXmlAttribute())
					new_desc.addAttribute (jf);
				else
					new_desc.addElement (jf);
			}
		}
	}

    /** a routine to marshal objects to xml strings
     *
     * uses descriptor class - if not found uses reflection from mapping..
     *
     *
     */
    public String marshal (Object obj) throws Exception {
        Class thisClass = obj.getClass();
        XmlDescriptor desc = (XmlDescriptor)classList.get(thisClass.getName());
        if (desc == null) {
			//Debugger.trace ("Class " + thisClass.getName () + " not found.."
			//				, Debugger.SHORT);
            return reflectMarshal (obj); // if no descriptor class - try to use relection to marshal.
		}
        return this.marshal(obj, new MarshalContext (outFormat), desc.xmlName);
    }

    protected String marshal (Object obj, MarshalContext context, String nodeName) throws Exception {
		return marshal (obj, context, nodeName, null);
	}

    protected String marshal (Object obj, MarshalContext context, String nodeName, String compileTimeType) throws Exception {
        Class thisClass = obj.getClass();
        XmlDescriptor desc = (XmlDescriptor)classList.get(thisClass.getName());
        if (desc == null) {
			// Debugger.trace ("Class " + thisClass.getName () + " not found.."
			//				, Debugger.SHORT);
            return reflectMarshal (obj, context, nodeName); // if no descriptor class - try to use relection to marshal.
		}

        String indent = context.getIndent();
        Integer prev_exist = context.get(obj);
        if (prev_exist != null) {
            return indent + "<" + nodeName + " " +_IDREF+"=\"" + prev_exist.toString() + "\" />\n"; // is a recursive that we have already seen
        }

        int recursiveId = context.put(obj);
        String result = indent + "<" + nodeName + " " + _ID+"=\"" + recursiveId +"\"";
		if (writeTypes && compileTimeType != null &&
			!compileTimeType.equals (thisClass.getName ()))
			// write out run-time type for unmarshalling..
			result += " " + RUN_TIME + "=\""+ thisClass.getName() + "\"";

		if (obj instanceof Versioned)
			result += " " + VERSION_NUMBER + "=\"" 
				+ ((Versioned)obj).getVersion () + "\"";
        Iterator att = desc.attributeList.iterator ();
        while (att.hasNext()) {
            JField jf = (JField)att.next();
            Object value = getValue (jf, obj);
            if (value != null)
                result += " " + jf.getJavaName() + "=\"" + objectOut (jf, value, context)
                    + "\"";
        }
        result += ">\n";

        Iterator els = desc.elementList.values().iterator();
        while (els.hasNext()) {
            JField jf = (JField)els.next();
            Object value = getValue (jf, obj);
            if (value != null) {
                if (jf.isVector()) {
                    // TODO: what about other collections?
                    Vector v = (Vector)value;
                    for (int i = 0; i < v.size(); i++) {
                        Object o = v.elementAt(i);
						            result += makeElementString (o, jf, context);
                    }
                } else if (jf.isArray ()) {
					int length = Array.getLength (value);
					for (int i=0; i < length; i++) {
						Object o = Array.get (value, i);
						if (o!= null)
							result += makeElementString (o, jf, context);
					}							
				} else {
					result += makeElementString (value, jf, context);
                }
            }
        }

        return result + indent + "</" + nodeName + ">\n";
    }

    /** aroutine to find the text output for an object. */
    String objectOut (JField jf, Object value, MarshalContext context) {
		String result = value.toString ();
		if (jf.isDate ()) {
			SimpleDateFormat sdf = context.getDateFormat ();
			if (sdf == null) // the usuals seconds ..
				result = ((java.sql.Timestamp)value).getTime() + "";
			else
				result = sdf.format ((java.sql.Timestamp)value);
		}
		else if (jf.getObjectType ().equals ("char") && result.charAt (0) == (char)0)
			result = "";
		else if (jf.getObjectType ().equals ("double") && result.equals ("NaN"))
			result = "";
		return result;
    }

    /* a routine which makes the string for a single element (either by recursing)
     * or writing out
     *
     */

    private String makeElementString (Object value, JField jf, MarshalContext context) throws Exception {
		String result;
        if (jf instanceof JCompositeField) {
			context.addDepth ();
			result = marshal (value, context, jf.getJavaName(),
							  jf.getObjectType ());
			context.removeDepth ();
		} else
			result = context.getIndent () + "\t<" + jf.getJavaName() + ">" + objectOut (jf, value, context)
				+ "</" + jf.getJavaName() + ">\n";
		return result;
    }

	/** get a value for the field represented in the JField using get<name>
	 * using reflection
	 *   @param obj the current object to marshal
	 *   @param jf the JField that describes the variable to be accessed
	 *
	 *   @return the object representing the value which was stored in the object
	 */
    private Object getValue (JField jf, Object obj) throws Exception {
        Class thisClass = obj.getClass();
        Object value = null;
        Class c = thisClass;
        boolean done = false;
        while (!done) {
            try {
                value = c.getDeclaredMethod ("get" + jf.getGetSet(), null).invoke(obj, null);
                done = true;
            } catch (NoSuchMethodException e) {
                c = c.getSuperclass();
                if (c == null) {
					System.out.println ("Unable to find: " + "get" + jf.getGetSet() + " in class: " + thisClass.getName());
                    throw e;
				}
            }
        }

        return value;
    }


    /** try to use reflection to marshal a Java Class to an XML representation
    *   @param obj any object
    *
    *   @return the String which represents this class in XML
    */
	protected String reflectMarshal (Object obj) throws Exception {
		return reflectMarshal (obj, new MarshalContext ());
	}

    protected Object [] dummyArray = new Object [] {};

    protected boolean isNativeType (String typeName) {
        return typeName.equals ("java.lang.String") || typeName.equals ("String") ||
            typeName.equals("java.lang.Integer") ||
            typeName.equals("java.lang.Long") ||
            typeName.equals("java.lang.Boolean") ||
            typeName.equals("java.lang.Double") ||
            typeName.equals("java.lang.Float") ||
            typeName.equals("java.sql.Timestamp");
    }

    protected String getClassName (Object obj) {
        Class thisClass = obj.getClass();
		String className = thisClass.getName ();
		className = className.substring (className.lastIndexOf (".") + 1);
        return className;
    }

    protected String reflectMarshal (Object obj, MarshalContext context) throws Exception {
        if (obj == null) return "";
		Class thisClass = obj.getClass ();

        if (isNativeType (thisClass.getName ()))
            return obj.toString();
        String className = getClassName (obj);
        return reflectMarshal (obj, context, className);
    }

    protected String reflectMarshal  (Object obj, MarshalContext context, String nodeName) throws Exception {
        if (obj == null) return "";
        Class thisClass = obj.getClass ();

        Integer last = context.get(obj);
        if (last != null)
            return context.getIndent() + "<" + nodeName + " "
                + this._IDREF + "=\"" + last.intValue() + "\"/>\n";

        int id = context.put(obj);
		Method [] methods = thisClass.getDeclaredMethods ();
		String result = "";
        if (obj instanceof List) {
            List v = (List)obj;
            for (int i=0; i <v.size(); i++)
                result += marshal (v.get(i), context, nodeName);
            return result;
        }

        result += context.getIndent() + "<" + nodeName + " " + _ID + "=\"" + id +"\">\n";
        context.addDepth();

        boolean foundAGet = false;
		for (int i=0; i< methods.length; i++) {
			Method aMethod = methods[i];
			if (aMethod.getName ().startsWith ("get") && aMethod.isAccessible ()) { 
				  // is a get method so do..
				if (aMethod.getParameterTypes().length == 0) { // takes no params..
                    foundAGet = true;
                    Object val = aMethod.invoke(obj, dummyArray);
                    if (aMethod.getReturnType().isPrimitive()) { // prim..
                        String valName = getNameFromMethod (aMethod);
                        result += context.getIndent() + "<" + valName + ">";
                        result += val.toString() + "</" + valName + ">\n";
                    }
                    else if (val instanceof Vector)
                        result += marshal (val, context, getNameFromMethod (aMethod));
                    else if (val != null) { // not primitive call toString () or reflect..
                        result += marshal (val, context, getNameFromMethod (aMethod));
                    }
                } // else takes > 0 params.. I don't know what to pass..
			}
			// else ignore..
		}
        context.removeDepth();
        if (!foundAGet)  // try toString..
            return context.getIndent() + obj.toString() + " " + getClassName (obj) + "\n";

        return result + context.getIndent () + "</" + nodeName + ">\n"; // nothing for now
    }


    protected String getNameFromMethod (Method m) throws Exception {
        String result = m.getName().substring(3);
        result = result.substring(0,1).toLowerCase() + result.substring(1);
        return result;
    }

    /** a routine to unmarshal xml text to a Java object -
     * assumes that the class is loadable and that the object is described in the map file
     * @param xmlText an xml text string - whose root node was described in the map file
     * @return Object a new instance with all data in place
     * @throws Exception - when the xml-Java descriptor is not found or the Java object is not found
     * by the class loader - or when the xml is not well formed
    */

    public Object unmarshal (String xmlText) throws Exception {
		return unmarshal (xmlText, true); // default is true..
	}

    public Object unmarshal (String xmlText, boolean ignoreMissingFields) throws Exception {
        return unmarshal (new StringReader (xmlText), ignoreMissingFields);
    }

    public Object unmarshal (Reader r) throws Exception {
		return unmarshal (r, true); // default is true
	}

    public Object unmarshal (Reader r, boolean ignoreMissingFields) throws Exception {
        XmlNode root = super.mapXMLFile(r, false);
        String name = root.getName(); //getTagName
        XmlDescriptor desc = (XmlDescriptor)xmlNodeList.get(name);
        if (desc == null)
            throw new AbraException ("ephman.abra.tools.nodescriptor");

        return this.unmarshal(root, new UnmarshalContext (ignoreMissingFields), desc);
    }


	/** a routine to be called on a composite node - with the current context and the Xml descriptor
	 * for this object
	 * @param thisNode the org.w3c Element
	 * @param context a wrapper containing the ID and IDREF tags
	 * @param  desc for the object to be built containing attributes
	 * and elements..
	 * @return Object a new object as described in descriptor with all fields set
	 */
    protected Object unmarshal (XmlNode thisNode, UnmarshalContext context, XmlDescriptor desc) throws Exception {

        String recurseId = thisNode.getAttribute(_IDREF);
        Object prevInstance = null;
        try {
            Integer key = new Integer (recurseId);
            prevInstance = context.get(key);
        } catch (Exception e) {}    // if caught no id..
        if (prevInstance != null)
            return prevInstance;
        else { // put in list
			String runTimeType = thisNode.getAttribute (RUN_TIME);
			if (Checks.exists (runTimeType)) {
				//				System.out.println ("getting new descriptor for " + runTimeType);
				if (classList.get (runTimeType) != null)
					desc = (XmlDescriptor)classList.get (runTimeType);
			}

            Class thisClass = Class.forName (desc.className);
            Object new_object = thisClass.newInstance();

            try {
                Integer recId = new Integer(thisNode.getAttribute(_ID));
                context.put (recId, new_object);
            } catch (NumberFormatException e) {}

			if (new_object instanceof Versioned) {
				String vNum = thisNode.getAttribute (VERSION_NUMBER);
				try {
					((Versioned)new_object).setVersion (Integer.parseInt (vNum));
				} catch (NumberFormatException e) {}
			}
            setAllAttributes (thisNode, new_object, desc, context);
            recurseOnChildList (thisNode.getChildNodes(), new_object, desc, context);

			// also handle arrays..
			Vector fieldArrays = desc.arrayElList;
			for (int f = 0; f<fieldArrays.size (); f++) {
				JField jf = (JField)fieldArrays.elementAt (f);
				//W3CXmlNode node = new W3CXmlNode (thisNode);
				Vector elNodes = thisNode.getChildNodes (jf.getJavaName ());
				setArray (jf, elNodes, new_object, desc, context);
			}

            return new_object;
        }


        //return null;
    }

	/** a method to set an array described by jf into obj (whose descriptor is desc 
	 */
	protected void setArray (JField jf, Vector nodes, Object obj, XmlDescriptor desc,
							 UnmarshalContext context) throws Exception {
		if (nodes.size () == 0) return;
		Class thisClass = obj.getClass();

		Object theArray = Array.newInstance (getAClass (jf.getObjectType ()), nodes.size ());
		Object value = null;
        Method m = null;

		m = getASetMethod (thisClass, "set" + jf.getGetSet(), theArray.getClass ());

		for (int i=0; i< nodes.size (); i++) {
			// W3CXmlNode ..
			XmlNode thisNode = ((XmlNode)nodes.elementAt (i));
			if (jf instanceof JCompositeField) {
				XmlDescriptor fieldDesc = (XmlDescriptor)this.classList.get (jf.getObjectType());
				value = unmarshal (thisNode, context, fieldDesc);
			}
			else {
				value = getPrimitive (thisNode, jf, context);
				
			}

			Array.set (theArray, i, value);
		}
		m.invoke(obj, new Object[]{theArray});
	}

    protected void setAllAttributes (XmlNode thisNode, Object obj, XmlDescriptor desc, UnmarshalContext context) throws Exception {
        Iterator it = desc.attributeList.iterator();
        while (it.hasNext()) {
            JField jf = (JField)it.next();
            String attValue = thisNode.getAttribute(jf.getJavaName());
            if (attValue != null && !attValue.equals("")) {
	            //for now string only
    	        Class c = obj.getClass();
	            boolean isPrimitive = true;
    	        String paramClassType = jf.getObjectType();

	            if (paramClassType.equals("String")) {
    	            isPrimitive = false;
        	        paramClassType = "java.lang.String";
	            }
	            //classType = getFieldType (classType);
    	        Method m = getASetMethod (c, "set"+jf.getGetSet(), paramClassType);
	            if (!isPrimitive)
        	        m.invoke (obj, new Object[] {attValue});
				else if (jf.isDate () && dateFormatStrings != null) {
					// if no dateFormatStrings 
					// assume number of seconds in date and fall below...
					setDate (obj, m, attValue, context);				   
            	} else { // int or bool etc --
                	//Class nativeClass = getAClass (paramClassType);
	                Object forCons = attValue;
	                Class initClass = null;
	                if (jf.isDate ()) {
	                    forCons = new Long(attValue);
	                    initClass = java.lang.Long.TYPE;
	                } else
	                    initClass = string;
	                Class primClass = getAClass (this.getFieldType(paramClassType));
					/*System.out.println ("Getting constructor for " + initClass.getName ()
										+ " with args " + forCons +"  primClass="+primClass.getName ()
										+ " isDate="+jf.isDate()); */
	                Object primObj = primClass.getDeclaredConstructor(new Class[]{initClass}).newInstance(new Object[]{forCons});

	                m.invoke(obj, new Object[]{primObj});
            	}
			}
        }
    }

	protected void setDate (Object o, Method m, String value, UnmarshalContext context) throws Exception {
		Timestamp result = getDate (value, context);
		m.invoke (o, new Object[]{result});
	}

	// apply date formats and try to get
	protected Timestamp getDate (String value, UnmarshalContext context) {
		SimpleDateFormat sdf = context.getDateFormat ();
		Timestamp result = null;
		if (dateFormatStrings != null) {
			for (int i=0; i < dateFormatStrings.length && result == null; i++) {
				if (dateFormatStrings[i].length () == value.length ()) {
					sdf.applyPattern (dateFormatStrings[i]);
					try {
						result = new Timestamp (sdf.parse(value).getTime ());
					} catch (Exception e) { } // nothing for now ..
				}
			}
		}

		if (result == null) { // still nothing try integer
			result = new Timestamp (Long.parseLong (value));
		}
		return result;
	}
	/** helper method to get a java.lang.Class object given a type (including primitives
	 * like int.class)
	 */

    protected Class getAClass (String className) throws ClassNotFoundException {
        int dot_index = className.lastIndexOf(".");
        if (dot_index == -1) { // native class or string
            if (className.equals ("String"))
                return String.class;
            else if (className.equals ("int"))
                return int.class;
			else if (className.equals ("long"))
                return long.class;
            else if (className.equals ("boolean"))
                return boolean.class;
            else if (className.equals ("char"))
				return char.class;
            else if (className.equals ("double"))
                return double.class;
            else if (className.equals ("Date") || className.equals ("Timestamp"))
                return Class.forName("java.sql.Timestamp");
            else // try native classes
                return Class.forName("java.lang." + className);
        }
        else { //use class loader
            return Class.forName(className);
        }
    }

    private void printMethods (Class c) {
        Method [] allMethods = c.getDeclaredMethods();
        for (int i = 0; i < allMethods.length; i++) {
            Method m = allMethods[i];
            System.out.print (m.getReturnType() + " " + m.getName() + " (");
            Class []params = m.getParameterTypes();
            if (params != null && params.length > 0) {
                System.out.print(params[0].toString());
                for (int j = 1; j < params.length; j++)
                    System.out.print(", " + params[j].toString());
            }
            System.out.println (")");
        }

    }

    /* get a method for a class and parameter - so given class foo.Peron
     * try to get a Method object for methodName (paramClassName) in Class c
     * @param c the class to find the method in
     * @param methodName the method to call
     * @param paramClassName
     * @return a Method to invoke on an instance of this class
     */
    protected Method getASetMethod (Class c, String methodName, String paramClassName) throws Exception {
		return getASetMethod (c, methodName, getAClass (paramClassName));
	}

    protected Method getASetMethod (Class c, String methodName, Class paramClass) throws Exception {
            // printMethods (c); //  debug line for reflection
            Method m = null;
			Class origClass = c;
            while (m == null) {
                try {
					//   Class paramClass = getAClass (paramClassName);

                    m = c.getDeclaredMethod(methodName, new Class[] {paramClass});
                } catch (Exception e) {
                    c = c.getSuperclass();
                    if (c == null) {
						System.out.println ("Unable to find: " + methodName + " ("
											+paramClass.getName () +")in class: " 
											+ origClass.getName());
						throw e;
					}
                }
            }
            return m;
    }


    /* set a field in this object using the current elementNode
     * eg. a parsed <name>colonel exception</name> on Person
     * try to find the node name in desc list
     * and the type of the data - then call setName (String) on the passed object
     *
     * @param thisNode the current element
     * @param obj the current object
     * @param desc the descriptor for the current object (contains attList and elementList)
     * @param context the current state of unmarshalling (with ID IDREF, hashtables - etc)
     *
     */
    protected void setElement (XmlNode thisNode, Object obj, XmlDescriptor desc, UnmarshalContext context) throws Exception {
        String elName = thisNode.getName ();   //getTagName();

        JField jf = (JField)desc.elementList.get(elName);
        if (jf == null)
            throw new FieldNotFoundException ("ephman.abra.tools.nofield", 
											  new Object[]{elName, desc.className});

		if (jf.isArray ()) return ; // will do seperately..
        Class thisClass = obj.getClass();

        Object value = null;
        Method m = null;
        if (jf.isCollection()) {
            m = getASetMethod (thisClass, "addTo" + jf.getGetSet(), jf.getObjectType());
        } else {
            m = getASetMethod (thisClass, "set" + jf.getGetSet(), jf.getObjectType());
        }

        if (jf instanceof JCompositeField) {
            XmlDescriptor fieldDesc = (XmlDescriptor)this.classList.get (jf.getObjectType());
            value = unmarshal (thisNode, context, fieldDesc);
        }
        else {
			value = getPrimitive (thisNode, jf, context);
            
        }

        m.invoke(obj, new Object[]{value});
		//		if (jf.getObjectType ().equals ("char"))
		//System.out.println ("was ok");
    }

	Object getPrimitive (XmlNode thisNode, JField jf, UnmarshalContext context) 
		throws Exception {

		try {
			Object value = null;
			Class primClass = getAClass (getFieldType(jf.getObjectType()));
			//			Node primVal = thisNode.getFirstChild();
			String nodeValue = thisNode.getText ();
			if (Checks.exists (nodeValue)) {
				if (jf.isDate () && dateFormatStrings != null) { // use dformats instead of int
					value = getDate (nodeValue, context);
				} else {
					Object forCons = nodeValue; //primVal.getNodeValue();
					Class initClass = null;
					if (jf.isDate ()) {
						forCons = new Long((String)forCons);
						initClass = java.lang.Long.TYPE;
					} else
						initClass = string;
					
					Constructor con = primClass.getDeclaredConstructor(new Class[]{initClass});
					value = con.newInstance(new Object[]{forCons});
				}
			}
			else if (jf.getObjectType().equals("String"))
				value = "";
			else if (jf.getObjectType ().equals ("double")) 
				return new Double (Double.NaN);
			else if (jf.getObjectType ().equals ("char")) {
				//	System.out.println ("Null Character");
				value = new Character (' ');
			}  	
			return value;
		} catch (Exception e) {
			throw new AbraException ("ephman.abra.tools.badprimitive", new Object []{jf.getJavaName ()}, e);
		}
	}
	
	
    /* look through a nodes element list and recurse on all children
     *
     * @param children the Node list that contains this nodes child elements
     * @param obj the current object
     * @param desc the descriptor for the current object (contains attList and elementList)
     * @param context the current state of unmarshalling (with ID IDREF, hashtables - etc)
     *
     */
	 protected void recurseOnChildList (Vector children, Object obj, XmlDescriptor desc, UnmarshalContext context) throws
	  Exception {
		 if (children == null) return ;
		 for (int i = 0; i < children.size (); i++ )
			 try {
				 setElement ((XmlNode)children.elementAt (i), obj, desc, context);
			 } catch (FieldNotFoundException e) {
				 if (context.ignoreMissingFields)
					 System.err.println (e.getMessage ());
				 else throw e;
			 }
	 }
	/*
	  protected void recurseOnChildList (NodeList children, Object obj, XmlDescriptor desc, UnmarshalContext context) throws
	  Exception {
	  
	  for (int i = 0; i < children.getLength (); i++ )
	  if (children.item (i) instanceof Element) {
	  try {
	  setElement ((Element)children.item (i), obj, desc, context);
	  } catch (FieldNotFoundException e) {
	  if (context.ignoreMissingFields)
	  System.err.println (e.getMessage ());
	  else throw e;
	  }
	  }
	  }*/


    /** get a native type primary class name (so for 'int' return 'java.lang.Integer'
     *
     *
     */

    protected String getFieldType (String fieldType) {
        if (fieldType.equals ("String"))
            return "java.lang.String";
        else if (fieldType.equals("int"))
            return "java.lang.Integer";
		else if (fieldType.equals("long"))
            return "java.lang.Long";
        else if (fieldType.equals("boolean"))
            return "java.lang.Boolean";
        else if (fieldType.equals("char"))
			return "java.lang.Character";
        else if (fieldType.equals("double"))
            return "java.lang.Double";
        else if (fieldType.equals("float"))
            return "java.lang.Float";
        else if (fieldType.equals("Date") || fieldType.equals("Timestamp"))
            return "java.sql.Timestamp";
        else return "";
    }

    // not sure what to do with this ??

    protected void applyRules (Element e, String mapName) throws
		ParserConfigurationException,
		FileNotFoundException,
		IOException,
		SAXException,
		SchemaException {

    }

	protected  void applyRules (XmlNode thisNode, String mapFile) throws
	    XmlException, 
		IOException,
		SchemaException { }

	public static final String VERSION_NUMBER = "version_number"; // SAME_AS GenricFactoryBase

}

