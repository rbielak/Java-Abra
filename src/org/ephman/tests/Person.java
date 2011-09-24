package org.ephman.tests;

import java.sql.*;

public class Person implements SQLData {

    public String name;
    public int age;
    public int siblings;
    public Movie favoriteMovie;

    public int oid;

    public Person () {
	
    }

    public Person (String name, int age, int siblings, Movie favoriteMovie) {
	this.name = name;
	this.age = age;
	this.siblings = siblings;
	this.favoriteMovie = favoriteMovie;
	//	arrayDesc = ArrayDescriptor.createDescriptor ("STR_ARRAY", c);
    }

    //ArrayDescriptor arrayDesc;

    

    public void writeSQL (SQLOutput stream) throws SQLException {
	stream.writeInt (0);
	stream.writeInt (age);
	stream.writeString (name);
	stream.writeInt (siblings);
	//stream.writeArray (new oracle.sql.ARRAY (arrayDesc, csiblings);
	stream.writeObject (favoriteMovie);
    }

    public void readSQL (SQLInput stream, String objectName) throws SQLException {
	System.out.print ("readSQL");
	oid = stream.readInt ();
	age = stream.readInt ();
	name = stream.readString ();
	siblings = stream.readInt ();
	System.out.println (":4");
	favoriteMovie = (Movie)stream.readObject ();
	System.out.println (":5");
    }

    public String getSQLTypeName () { return "O_PERSON"; }

}
