package org.ephman.abra.tools;

/**
 * Title:			TypeMapper <p>
 * Description:  	To keep track of types across - language/db/xml
 * Copyright:	Copyright (c) 2002 Paul Bethe and Richie Bielak<p>
 * @author		 	Paul Bethe
 * @version 0.1.0
 */
import java.util.*;

public class TypeMapper {
	static HashMap _types = new HashMap ();

	public static void setObjectMap (Map objs) {
		_types.putAll (objs);
	}

	/** add the database mappings */
	public static void setDbMap (Map dbTypes, String dbName) throws SchemaException {
		Iterator it = dbTypes.keySet ().iterator ();
		while (it.hasNext ()) {
			String abraName = (String)it.next ();
			TypeMap tm = (TypeMap)_types.get (abraName);
			String dbFieldName = (String)dbTypes.get (abraName);
			if (tm == null)
				throw new SchemaException ("ephman.abra.tools.notypemap",
										   new Object[]{abraName, dbName});
			tm.setSqlTypeName (dbFieldName);
		}
	}
	
	public static TypeMap getType (String fieldType) {
		TypeMap tm = (TypeMap)_types.get (fieldType);
		if (tm == null) { // is composite type..
			tm = new TypeMap (fieldType, fieldType, null, "int"); // objects are ints in the db
			tm.setComposite (true);
			_types.put (fieldType, tm);
		}
		return tm;
	}

}
