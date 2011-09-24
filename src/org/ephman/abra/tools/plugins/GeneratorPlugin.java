package org.ephman.abra.tools.plugins;

import org.ephman.abra.tools.*;
import java.util.*;
import java.io.IOException;
/** this class will describe the basics for adding a plugin to the 
 * abra generation framework
 * callbacks when parsing xml for information storage
 * and generation calls to run
 *
 * @author Paul M Bethe
 * @version 0.1 (8/16/2002)
 */

public abstract class GeneratorPlugin implements Generator {


	/** returns the names which are registered to this generator
		ie. "class-view" would map to View generator */
	public abstract String [] getClassLevelNodeNames ();
	
	/** returns the names which are registered to this generator
		but at the field level ie.  <view in ... means "view" maps to View generator*/
	public abstract String [] getFieldLevelNodeNames ();

	/** call back when the node 'nodeName' which has been registered by this class is encountered
	 * @param nodeName the node name ie "class-view"
	 * @param attributes all the attributes in the xml node in key-value hmap
	 * @param currentClass the JClass on which this node occured
	 *
	 * @throws SchemaException if the data is invalid.
	 */
	public abstract void handleClassLevelNode (String nodeName, Map attributes, JClass currentClass) 
		throws SchemaException;

	/** call back when the node 'nodeName' which has been registered by this class is encountered
	 * @param nodeName the node name ie "view"
	 * @param attributes all the attributes in the xml node in key-value hmap
	 * @param currentClass the JClass on which this node occured
	 * @param currentField the JField on which this node occured
	 *
	 * @throws SchemaException if the data is invalid.
	 */
	public abstract void handleFieldLevelNode (String nodeName, Map attributes, 
						   JClass currentClass, JField currentField) 
		throws SchemaException;

    /** at the end of generator defined 'handleFieldLevelNode' call to this method adds the next field */
    public  void handleFieldLevelNode (FieldPluginData fd, String format, JClass currentClass) 
	throws SchemaException {
	ClassPluginData plugin = getPlugin (currentClass, format);
	if (plugin == null) 
	    throw new SchemaException ("ephman.abra.tools.generators.noformat", new Object[]{getName (), format});
	    
	plugin.addToFieldList (fd);
    }


	public abstract String getName ();


	/** close and generate are part of Generator inteface */ 
	public abstract void generate (JClass currentClass) 
		throws IOException, SchemaException;
	
	/** incase files need to be closed or whatever */
	public  abstract void close () throws IOException;


	/** create a default plugin (ie base taglib, validator */		
	protected abstract ClassPluginData createDefault (JClass currentClass);

	/** does this plugin need a call to createDefault ? */

	protected abstract boolean hasDefault ();


	public String getDefaultFormatName () { return "DEFAULT-FORMAT"; }

	protected void assertMandatory (String field, String name) throws SchemaException {
		if (!Checks.exists (field))
			throw new SchemaException ("Mandatory node " + name + " for " + getName () + " not set.", "file");
	}

	protected void registerPlugin (ClassPluginData plugin, JClass currentClass) {
		HashMap hm = getNamedMap (currentClass);
		if (hm.get (plugin.getFormatName ()) != null)
			System.out.println ("Already registered '"+plugin.getFormatName ()
								+"'");
		hm.put (plugin.getFormatName (), plugin);
	}

	protected ClassPluginData getPlugin (JClass currentClass, String formatName) {
		HashMap hm = getNamedMap (currentClass);
		ClassPluginData cp = (ClassPluginData)hm.get (formatName);
		if (cp == null && formatName != null 
			&& formatName.equals (getDefaultFormatName ())) { 
			// default format so create
			cp = createDefault (currentClass);
			hm.put (getDefaultFormatName(), cp);
		}
		return cp;
	}


	protected HashMap getNamedMap (JClass currentClass) {
		HashMap plugins = currentClass.getPlugins ();
		HashMap v = (HashMap)plugins.get (getName ());
		if (v == null) {
			v = new HashMap ();
			plugins.put (getName (), v);
		}
		return v;
	}
}
