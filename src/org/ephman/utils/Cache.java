
package org.ephman.utils;

import java.util.*;

/** A class to cache any set of objects

 * @version 1.0
 * @author Paul M. Bethe
 */

public abstract class Cache {

  protected HashMap hm;

  private Vector els;

  private int maxElements;

  private int numElements;

  private Random randomGen;

  private final int MINIMUM_SIZE = 17;

	/** constructs a cache of default elements (17)
	 */
  public Cache () {
    this (0);
  }


	/**
	*  a routine to flush out the cache
	*/

	public synchronized void flush () {
		numElements = 0;
    	els = new Vector (maxElements);
    	hm = new HashMap (maxElements);
    }

	/** if cacheSize is greater than 17, initializes the cache with size =
	 * the first prime >= cacheSize
	 * @param cacheSize the desired cache size
	 */

  public Cache (int cacheSize) {
    if (cacheSize < MINIMUM_SIZE)
      maxElements = MINIMUM_SIZE;
    else
      maxElements = Prime.primeAfter(cacheSize);
    numElements = 0;
    els = new Vector (maxElements);
    hm = new HashMap (maxElements);
    randomGen = new Random ();
  }

	/** Determine the number of elements in the cache never > than maxElements
	 * @return the number of elements currently in the cache
	 */
  public int getSize () { return numElements; }

	/** Unsynchronized find of an Object which was hashed on key
	 * @param key the Object whose hashcode will be used in lookup
	 * @return the Object last hashed on that code or null if none is found
	 */
  public Object get (Object key) {
    return hm.get (key);
  }

	/** A synchronized placement of an element into the cache - to guarantee
	 * that no more than 1 copy of an item is cached
	 * @param key the Object whose hashcode should be used to store <b>obj</b>
	 * @param obj the Object to store in cache
	 */
  public synchronized void putForceUpdate (Object key, Object obj)
  // pre:  object to be added to cache - may or may not be in already
  // post: object in Vector and index in HashMap
  {
  	Object o = hm.remove (key);
	if (o == null)
		put (key, obj);
	else if (obj != null) {
		hm.put(key, obj);
	} /* not adding null by decision */
  }

	/** A synchronized placement of an element into the cache - to guarantee
	 * that no more than 1 copy of an item is cached
	 * @param key the Object whose hashcode should be used to store <b>obj</b>
	 * @param obj the Object to store in cache
	 */
  public synchronized void put (Object key, Object obj)
  // pre:  object to be added to cache - may or may not be in already
  // post: object in Vector and index in HashMap
  {
    Object o = this.get (key);
    if (o == null && obj != null) {
      if (numElements < maxElements) {
        els.add (numElements, key);
        hm.put (key, obj);
        numElements++;
      }
      else { /* need to free space for new element */
        int newLoc = randomGen.nextInt (maxElements);
        Object byeKey = els.elementAt(newLoc);
        hm.remove (byeKey);
        // old obj removed now add new one
        els.add (newLoc, key);
        hm.put (key,obj);
      }
    }
  }


}
