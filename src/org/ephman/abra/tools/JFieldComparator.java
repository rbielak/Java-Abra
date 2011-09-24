package org.ephman.abra.tools;

/**
 * Title:			JFieldComparator <p>
 * Description:  	Uses Comparator in TreeMap to define how to sort JFields<p>
 * Copyright:	Copyright (c) 2001 Paul Bethe and Richie Bielak<p>
 * @author		 	Paul Bethe
 * @version 0.1.0
 */

import java.util.Comparator;

public class JFieldComparator implements Comparator {

  public boolean equals (Object obj1, Object obj2) {
      return obj1.equals(obj2);
  }


  public int compare (Object obj1, Object obj2) {

      String st_one = (String)obj1;
      String st_two = (String)obj2;
      return st_two.compareTo(st_one);
  }


}
