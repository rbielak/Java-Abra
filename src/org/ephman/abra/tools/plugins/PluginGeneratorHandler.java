package org.ephman.abra.tools.plugins;

import org.ephman.abra.tools.*;
import java.util.*;
import java.io.IOException;
import org.ephman.abra.utils.AbraRuntimeException;
/** this class will accept
 * callbacks when parsing xml for information storage
 * and generation calls to run
 * friend to MapToJava
 *
 * @author Paul M Bethe
 * @version 0.1 (8/16/2002)
 */

public class PluginGeneratorHandler {

	/** call back when the node 'nodeName' which has been registered by this class is encountered
	 * @param nodeName the node name ie "class-view"
	 * @param attributes all the attributes in the xml node in key-value hmap
	 * @param currentClass the JClass on which this node occured
	 *
	 * @throws SchemaException if the data is invalid.
	 */
	public void handleClassLevelNode (String nodeName, Map attributes, JClass currentClass) 
		throws SchemaException {
		GeneratorPlugin gen = (GeneratorPlugin)classLevelMap.get (nodeName);
		if (gen == null)
			Debugger.trace ("No generator for " + nodeName + " registered",
						 0);

		gen.handleClassLevelNode (nodeName, attributes, currentClass);
	}

	/** call back when the node 'nodeName' which has been registered by this class is encountered
	 * @param nodeName the node name ie "view"
	 * @param attributes all the attributes in the xml node in key-value hmap
	 * @param currentClass the JClass on which this node occured
	 * @param currentField the JField on which this node occured
	 *
	 * @throws SchemaException if the data is invalid.
	 */
	public  void handleFieldLevelNode (String nodeName, Map attributes, 
											   JClass currentClass, JField currentField) 
		throws SchemaException {

		GeneratorPlugin gen = (GeneratorPlugin)fieldLevelMap.get (nodeName);
		if (gen == null)
			Debugger.trace ("No (field-level) generator for " + nodeName 
						 + " registered",
						 0);

		gen.handleFieldLevelNode (nodeName, attributes, currentClass, 
								  currentField);
	}


	/** call each registered generator for the given class
	 * and allow them to add any default PluginData 
	 * @param currentClass the class which should be passed to each registered generator
	 */
	public void addDefault (JClass currentClass) throws SchemaException {
			// call on each generator
		for (int i=0; i < pluginList.size (); i++) {
			GeneratorPlugin gen = (GeneratorPlugin)pluginList.elementAt (i);
			if (gen.hasDefault ()) 
				gen.getPlugin (currentClass, gen.getDefaultFormatName ()); // add default..
		}			
	}


	/** call each registered generator for the given class
	 * @param currentClass the class which should be passed to each registered generator
	 */
	public void generate (JClass currentClass) throws IOException, SchemaException {
		// call on each generator-plugin
		for (int i=0; i < pluginList.size (); i++)
			((GeneratorPlugin)pluginList.elementAt (i)).generate (currentClass);

		for (int i=0; i < justGeneratorList.size (); i++)
			((Generator)justGeneratorList.elementAt (i)).generate (currentClass);
	}

	/** At the end of the run allow each generator to close/release any resources
	 * or files
	 */
	public void close () throws IOException {
		// call on each generator
		for (int i=0; i < pluginList.size (); i++)
			((GeneratorPlugin)pluginList.elementAt (i)).close ();

		for (int i=0; i < justGeneratorList.size (); i++)
			((Generator)justGeneratorList.elementAt (i)).close ();
	}

	/** before maptoJava works on an XML Doc -> register custom 
	 * generators here so that later calls to handle{Class,Field}, or generate
	 * will include then
	 * @param gen the generator which should be registered
	 */ 
	public void registerGenerator (Generator generator)  {
		if (generator instanceof GeneratorPlugin) { //needs registering
			GeneratorPlugin gen = (GeneratorPlugin)generator;
			String [] c_names = gen.getClassLevelNodeNames ();
			String [] f_names = gen.getFieldLevelNodeNames ();
			for (int i=0; i< c_names.length; i++) {
				if (classLevelMap.get (c_names[i]) != null) {
					Object [] args = new Object[]
						{gen.getClass().getName (), "class", c_names[i],
						 classLevelMap.get (c_names[i]).getClass().getName ()};
					throw new AbraRuntimeException ("ephman.abra.tools.alreadyreg", 
													args);
				}
				classLevelMap.put (c_names[i], gen);
			}
			
			// now same for field
			for (int i=0; i< f_names.length; i++) {
				if (fieldLevelMap.get (f_names[i]) != null) {
					Object [] args = new Object[]
						{gen.getClass().getName (), "field", f_names[i],
						 fieldLevelMap.get (f_names[i]).getClass().getName ()};
					throw new AbraRuntimeException ("ephman.abra.tools.alreadyreg", 
										 args);
				}
				fieldLevelMap.put (f_names[i], gen);
			}  	
			pluginList.addElement (gen);
		} else
			justGeneratorList.addElement (generator);

	}


	///////
	private HashMap classLevelMap = new HashMap ();
	private HashMap fieldLevelMap = new HashMap ();
	private Vector pluginList = new Vector ();
	private Vector justGeneratorList = new Vector ();

}

