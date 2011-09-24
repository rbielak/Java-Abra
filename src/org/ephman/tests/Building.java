package org.ephman.tests;

import oracle.sql.*;
import java.sql.*;
import oracle.jdbc.driver.*;
import java.io.*;

public class Building implements CustomDatum, CustomDatumFactory, Serializable {

    public Building () {
    }
	
    public Building (String name, String [] occupants, Movie [] movies) {
		this.name = name;
		this.occupants = occupants;
		this.movies = movies;
    }
    public String name;
    public String [] occupants;
    public Movie [] movies;
	public int [] shifts;
	//	public byte [] data;
	
	public String toString () {
		return "Building:\n\t" +
			name + occupants + movies;
	}

    public Datum toDatum (OracleConnection c) throws SQLException {
		StructDescriptor buildingDesc = StructDescriptor.createDescriptor ("O_BUILDING",
																		   c);		
		return new STRUCT (buildingDesc, c, 
						   new Object [] {new Integer(0), name,
										  BlobManager.makeArray (c, "STR_ARRAY", occupants),
										  BlobManager.makeArray (c, "MV_ARRAY", movies),
										  BlobManager.makeArray (c, "INT_ARRAY", 
																 shifts)
										  });
    }
    
    public static CustomDatumFactory getFactory () {
	if (theFactory == null) {
	    synchronized (Building.class) {
		if (theFactory == null) //double check for existence..
		    theFactory = new Building ();
	    }
	}
	return theFactory;
    }
    private static Building theFactory = null;

    public static final int _SQL_TYPECODE = OracleTypes.STRUCT;

    public static final String _SQL_NAME = "O_BUILDING";


    // creation
    public CustomDatum create (Datum dat, int i) throws SQLException {
		Object [] attributes = ((STRUCT)dat).getAttributes ();
		System.out.println ("Building::create");
		String [] ar = (String [])((ARRAY)attributes[2]).getArray();
		Movie [] mr = m_copy ((Object [])((ARRAY)attributes[3]).getArray());
		shifts = int_copy ((Object [])((ARRAY)attributes[4]).getArray());
		
		Building result = new Building ((String)attributes[1], ar, mr);
		result.shifts = shifts;
		//		result.data = (byte [])attributes[5];
		return result;
    }
    
	int [] int_copy (Object [] oa) {
		int [] result = new int [oa.length];
		for (int i=0; i< oa.length; i++) {
			result [i] = ((java.math.BigDecimal)oa[i]).intValue ();
		}
		return result;
	
    }

    Movie [] m_copy (Object [] oa) {
	Movie [] result = new Movie [oa.length];
	for (int i=0; i< oa.length; i++) {
	    result [i] = (Movie)oa[i];
	}
	return result;
	
    }

}
