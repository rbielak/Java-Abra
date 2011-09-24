package org.ephman.abra.database;

import java.util.Vector;

/** class to represent a set lookup
 * i.e where oid in (3,6,9)
 * pass in the column and the vector of ids..
 * @author Paul M. Bethe
 */

public class SetFilter extends QueryFilter {

	Vector values;
	String columnName;
    int [] arrayValues;


	public SetFilter (String columnName, Vector values) {
		this.columnName = columnName;
		this.values = values;
        this.arrayValues = null;
	}

    public SetFilter (String columnName, int [] values) {
		this.columnName = columnName;
		this.arrayValues = values;
        this.values = null;
	}

	public String toString () {
		return toString (null);
	}

	String tableAlias;
	public void setTableAlias (String alias) { this.tableAlias = alias; }

	public String toString (String tableName) {
		String ali = computeAlias (tableAlias, tableName);
		String result = ali + columnName + " in (";
        if (this.values != null) { // use array
            if (values.size () == 0)
                return "1=2";

    		if (values.size () > 0)
    			result += valOf (values.elementAt (0));
    		for (int i = 1; i < values.size(); i++) {
    			result += ", " + valOf (values.elementAt (i));
    		}
        } else if (arrayValues != null) { // use int array
            if (arrayValues.length == 0)
                return "1=2";

            result += arrayValues[0];
            for (int i = 1; i < arrayValues.length; i++)
    			result += ", " + arrayValues[i];
        }
    	return result + ")";
	}


	public void addToPreparedQuery (PreparedQuery pq, String tableName) {
		String ali = computeAlias (tableAlias, tableName);
		pq.appendText (ali + columnName + " in (");
        if (this.values != null) { // use array
			if (values.size () == 0)
				pq.appendText ("1=2");
    		if (values.size () > 0)
    			pq.add ("?", values.elementAt (0));
    		for (int i = 1; i < values.size(); i++) {
    			pq.add (",?", values.elementAt (i));
    		}
        } else if (arrayValues != null) { // use int array
            if (arrayValues.length == 0)
				pq.appendText ("1=2");
            else {
				pq.add ("?", new Integer (arrayValues[0]));
				for (int i = 1; i < arrayValues.length; i++)
					pq.add (",?", new Integer (arrayValues[i]));
			}
        }
    	pq.appendText (")");
	}

	public void addToPreparedQuery (PreparedQuery pq){
		addToPreparedQuery (pq, null);
	}

}
