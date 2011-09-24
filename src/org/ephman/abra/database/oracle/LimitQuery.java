package org.ephman.abra.database;

/**
 * Title:			Oracle specific wrapper query
 * Description:  	
 * Copyright:	Copyright (c) 2003 Paul Bethe and Richie Bielak<p>
 * @author		 	Paul Bethe
 * @version 0.1.0
 */


import java.sql.Timestamp;
import org.ephman.utils.*;

public class LimitQuery extends WrapperFilter {

	int size;
	public LimitQuery (QueryFilter inner, int size) {
		super (inner, null, new ComparisonFilter ("rownum", "<=", size));
		this.size = size;
	}

}
