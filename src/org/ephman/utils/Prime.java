/**
 * Title:        Prime<p>
 * Description:  Find prime numbers <p>
 * Copyright:	Copyright (c) 2001 Paul Bethe and Richie Bielak<p>
 * @author Paul Bethe
 * @version 1.0
 */

package org.ephman.utils;

import java.util.*;

/** A class which will compute primes and test if a number is prime
 * @version 1.0
 * @author Paul M. Bethe
 */

public class Prime {

	/** Takes an int and returns the first prime >= n
	 * @param n the starting place
	 * @returns a prime >= n
	 */
	public static int primeAfter (int n)
		// pre: none
		// post: return the next prime >=n
	{
		if ((n % 2) == 0)  n++;
		
		while (!isPrime (n))
			n += 2;
		return n;
	}
	
	/** Detemines if an int is prime
	 * @param n the number to test
	 * @return true/false
	 */
	public static boolean isPrime (int n) {
		long top = Math.round(Math.sqrt((double)n));
		for (int i = 3; i <= top; i += 2)
			if (n % i == 0) return false;
		return true;
	}

}
