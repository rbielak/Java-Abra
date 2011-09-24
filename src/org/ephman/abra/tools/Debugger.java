package org.ephman.abra.tools;

import java.io.*;
import java.util.*;

/**
 * out put debug messages to out or log file as set up
 * @author Paul M. Bethe
 * @version 0.0.1
 */


public class Debugger {

	public static final int ERROR = 0;
	public static final int SHORT = 1;
	public static final int VERBOSE = 2;
	public static final int ALL = 3;


	public static void init (int level) {
		debuglevel = level;
	}

	static int debuglevel = SHORT;

	public static void trace (String msg, int level) throws SchemaException {
		if (level <= debuglevel) { // print
			if (level == ERROR) {
				System.err.println (msg);
				throw new SchemaException ("current file", msg);
			}
			else
				System.out.println (msg);

		}
	}




}
