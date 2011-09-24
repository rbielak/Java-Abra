package org.ephman.tests;

import java.sql.*;
import oracle.sql.*;
import oracle.jdbc.driver.*;

/** a test class for blob writes over transactions..  */

public class Foo implements CustomDatum, CustomDatumFactory {

    public String name;
	public byte [] data;

    public Foo () {}

    public Foo (String name){
		this.name = name;
    }

    //Oracle spec
    public Datum toDatum (OracleConnection c) throws SQLException {
		StructDescriptor buildingDesc = StructDescriptor.createDescriptor ("T_SN_FOO",
																		   c);
		return new STRUCT (buildingDesc, c, 
						   new Object [] {name, 
						   BlobManager.makeBlob (c, data)});
    }

    public static CustomDatumFactory getFactory () {
		if (theFactory == null) {
			synchronized (Foo.class) {
				if (theFactory == null) //double check for existence..
					theFactory = new Foo ();
			}
		}
		return theFactory;
    }
    private static Foo theFactory = null;
	
    public static final int _SQL_TYPECODE = OracleTypes.STRUCT;

    public static final String _SQL_NAME = "O_MOVIE";
	
    public CustomDatum create (Datum dat, int i) throws SQLException {
		Object [] attributes = ((STRUCT)dat).getAttributes ();
		Foo result = new Foo ((String)attributes[0]);
		BLOB tmp_blob = ((BLOB)attributes[1]);
		result.data = tmp_blob.getBytes (1, (int)tmp_blob.length ());
		return result;
    }
}
