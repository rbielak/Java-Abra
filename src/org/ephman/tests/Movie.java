package org.ephman.tests;

import java.sql.*;
import oracle.sql.*;
import oracle.jdbc.driver.*;
import java.io.*;

public class Movie implements SQLData, CustomDatum, CustomDatumFactory, Serializable {

    public String name;

    public Movie () {}

    public Movie (String name){
	this.name = name; 
    }

    public void writeSQL (SQLOutput stream) throws SQLException {
	stream.writeInt (0);
	stream.writeString (name);
    }


    public void readSQL (SQLInput stream, String objectName) throws SQLException {
	System.out.println ("Movie::readSQL");
	stream.readInt ();
	name = stream.readString ();
    }

    public String getSQLTypeName () { return "O_MOVIE"; }


    //Oracle spec
    public Datum toDatum (OracleConnection c) throws SQLException {
	StructDescriptor buildingDesc = StructDescriptor.createDescriptor ("O_MOVIE",
									   c);
	return new STRUCT (buildingDesc, c, 
			   new Object [] {new Integer (0), name});
    }

    public static CustomDatumFactory getFactory () {
	if (theFactory == null) {
	    synchronized (Movie.class) {
		if (theFactory == null) //double check for existence..
		    theFactory = new Movie ();
	    }
	}
	return theFactory;
    }
    private static Movie theFactory = null;

    public static final int _SQL_TYPECODE = OracleTypes.STRUCT;

    public static final String _SQL_NAME = "O_MOVIE";

    public CustomDatum create (Datum dat, int i) throws SQLException {
	Object [] attributes = ((STRUCT)dat).getAttributes ();
	return new Movie ((String)attributes[1]);
    }
}
