package org.ephman.abra.tools;

import org.ephman.xml.*;
import org.apache.regexp.*;
import java.util.*;

/**
 * encapsulate the properties in the xml file..
 * @author Paul M. Bethe
 * @version 0.0.2
 */

public class PropertyTable {
	
	HashMap pairs = new HashMap ();

	public PropertyTable () throws XmlException {
		try {
			varRegex = new RE (varexp);
		} catch (RESyntaxException re) {
			throw new XmlException (re.getMessage ());
		}
	}

	String mapFileName;

	/** add properties to the table..
	 * @param nodes a Vector of XmlNodes of type <property name="foo" value="42"/>
	 */
	public void addProperties (Vector nodes, String mapFileName) throws SchemaException {
		this.mapFileName = mapFileName;
		if (nodes == null) return ;
		for (int i=0; i< nodes.size (); i++) {
			XmlNode aNode = (XmlNode)nodes.elementAt (i);
			addNode (aNode);
		}
	}

	public void addNode (XmlNode aNode) throws SchemaException {
		if (aNode.getName ().equals ("property")) {
			String name = aNode.getAttribute ("name");
			String value = aNode.getAttribute ("value");
			if (name.equals (""))
				throw new SchemaException ("Error: 'name' attribute must be set in property node",
										   mapFileName);
			if (value.equals ("")) value = "true";
			
			value = replace (value); // try replacing known properties..
			if (varRegex.match (value)) { // have un-resolved link..					
				int start = varRegex.getParenStart (0);
				int end = varRegex.getParenEnd (0);
				String varName = value.substring (start + 2, end -1);				
				addToUnresolved (aNode, varName);
			} 
			else { // add to tree and resolve
				pairs.put (name, value);
				resolve (name);
			}
			Debugger.trace ("Added property '" + name + "'=\""+value +"\"", Debugger.ALL);
		} else {
			throw new SchemaException ("Cannot add '" + aNode.toXml () + "' to the properties table",
									   mapFileName);
		}
	}


	void resolve (String name) throws SchemaException {
		Vector ns = (Vector)un_resolved.get (name);
		if (ns == null) return;
		Debugger.trace ("Trying to resolve nodes for '" + name +"'", Debugger.ALL);
		for (int i=0; i<ns.size (); i++) {
			XmlNode node = (XmlNode)ns.elementAt (i);
			addNode (node);
		}
	}

	HashMap un_resolved = new HashMap ();

	void addToUnresolved (XmlNode node, String varName) throws SchemaException {
		Vector ns = (Vector)un_resolved.get (varName);
		if (ns == null) {
			ns = new Vector ();
			un_resolved.put (varName, ns);
		}
		ns.addElement (node);
		Debugger.trace ("Un-resolved node " + node.toXml () + " depends on ${" + varName + "}", Debugger.ALL);
	}

	// do substitution for all elements in map
	public Map replaceAll (Map map) {
		Iterator it = map.keySet().iterator ();
		while (it.hasNext ()) {
			String key = (String)it.next ();
			String was = (String)map.get(key);
			String now = replace (was);
			map.put (key, now);
		}
		return map;
	}

	/** search input for ${foo} and replace with lookup to 42.. 
	 * @param input the string to use
	 * @return a fixed string (same if no ${}) 
	 */
	public String replace (String input) {
		return replace (input, 0);
	}
	public String replace (String input, int index) {
		boolean match = varRegex.match (input, index);
		if (!match) return input;
		int start = varRegex.getParenStart (0);
		int end = varRegex.getParenEnd (0);
		String varName = input.substring (start + 2, end -1);
		String val = (String)pairs.get (varName);
		if (val == null) // no appropriate value found
			return replace (input, end-1);
		/*	    System.out.println ("Available=" + varRegex.getParenCount ()); */
		//		Debugger.trace ("Match [" + start + ", " + end + "]", Debugger.ALL);
		//      Debugger.trace ("Name='" + varName + "' value='" + val + "'", Debugger.ALL); 
		String output = input.substring (0, start) + val + input.substring (end);
		return replace (output, start + val.length ());
	}

	public static final String varexp = "\\$\\{([a-zA-Z0-9 .\\_\\-])*\\}";
	public RE varRegex = null;

}
