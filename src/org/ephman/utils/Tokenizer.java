package org.ephman.utils;

import java.util.*;
import java.io.*;

/** a class to split strings by a given token
 * while skipping comments denoted by the given string literal
 * @author Paul M. Bethe
 * @version 0.0.1
 */



public class Tokenizer {
/*
    public static void main (String argv[]) {
        // a silly test of this
        String tstMsg = " Hello , world, how are, you";
        //tstMsg += "\nwhat, else\nis, there?";
        String aLine = "";
        try {
            FileLineReader fr = new FileLineReader ("c:\\Development\\play\\Senior_Execs_in_Securities_May_98.csv");
            for (int i = 0; i < 2; i++)
                aLine = fr.readLine();
        }
        catch (IOException e) { System.out.println ("Phoeey!"); }
        Vector v = Tokenizer.tokenize(aLine, ",", "\"");
        for (int i = 0; i < v.size(); i++)
            System.out.println ("'" + v.elementAt(i) + "'");
    }
*/
	/** tokenize will split theLine using seperator but ignoring string literal
	 * e.g.  line = "Hi mom, I really miss you, 'Thanks, for everything'"
	 * tokenize (line, ",", "'"); returns ["Hi mom", "I really miss you", "Thanks, for everything"]
	 *
	 */

    public static Vector tokenize (String theLine, String seperator, String literal) {
        Vector tokens = new Vector ();

        // while text on line make tokens
        int sepSize = seperator.length();
        int lastI = 0;

        while (theLine != null && theLine !="" && lastI < theLine.length()) {
            int i = theLine.indexOf (seperator, 0);
			int lit = theLine.indexOf (literal, 0 );
            String aToken = "";
            if (i != -1 && (lit == -1 || i < lit)) {
                aToken = theLine.substring(0, i);
                theLine = theLine.substring (i + sepSize);
            }
			else if (lit < i) { // a literal is found
				int endLit = theLine.indexOf (literal, lit + literal.length ());
				if (endLit != -1) {
					aToken = theLine.substring (lit, endLit + 1);
                    if (theLine.length() > endLit + 2)
					  theLine = theLine.substring (endLit + 2);   // pass this sep..
                    else
                      theLine = null;
				}
				else { // poorly closed literal take rest of line
					aToken = theLine.substring(lit);
					theLine = null;
				}
			}
            else { // no lit and no sep
                aToken = theLine;
                theLine = null;
            }
            tokens.addElement(aToken.trim());

        }
        return tokens;
    }


/*
 *  String has a trim() function - this deprecated
 *
    private static String stripWhite (String foo) {
        while (foo.startsWith(" "))
            foo = foo.substring (1);
        while (foo.endsWith(" "))
            foo = foo.substring (0, foo.length() - 1);
        return foo;
    }
*/

	/*
	public static void main( String[] args)
 	{
      	System.out.println( tokenize( "Hi mom , I really miss you  , ' Thanks, for everything'", ",", "\""));
    }
	*/
}
