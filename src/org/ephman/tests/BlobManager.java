package org.ephman.tests;

import java.sql.*;
import oracle.sql.*;
import oracle.jdbc.driver.*;

/** a test class for blob writes over transactions..  */

public class BlobManager {

	private static BLOB [] _blobs = null;

	static int _blob_count = 10;
	/** get some blobs for testing
	 */
	public static void initialize (OracleConnection c) throws SQLException {
		OracleCallableStatement stmt = (OracleCallableStatement)c.prepareCall ("pkg_foo.proc_make_temp_blobs (?, ?)");
		stmt.setInt (1, _blob_count);
		stmt.registerOutParameter (2, OracleTypes.ARRAY, "BLOB_A");
		stmt.executeUpdate ();
		ARRAY ar = stmt.getARRAY (2);
		System.out.println (ar.getArray ());
		_blobs = (BLOB [])ar.getArray ();
		stmt.close ();
	}


	public static BLOB makeBlob (OracleConnection c, byte [] _bytes) throws SQLException {
		if (_bytes == null)
			throw new SQLException ("Not Initialized");
		BLOB _result = _blobs[0];
		int wrote = _result.putBytes (1, _bytes);
		System.out.println ("Wrote " + wrote + " bytes ([]size== " + _bytes.length+")");
		return _result;
	}

	//	public static RAW makeRaw (OracleConnection c, byte [] bytes) throws SQLException {
		//result = new RAW

	//}

	public static ARRAY makeArray (OracleConnection c, String arrayName, Object elements) 
		throws SQLException {

		ArrayDescriptor _desc = ArrayDescriptor.createDescriptor (arrayName, c);
		return new ARRAY (_desc, c, elements);
	}

	
}

