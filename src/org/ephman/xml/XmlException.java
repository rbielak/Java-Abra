package org.ephman.xml;


public class XmlException extends Exception {

	public XmlException (String msg, int lineno) {
		super (msg + " line=" + lineno);
	} 

	public XmlException (String msg) {
		super (msg);
	}

	Exception prev;
	public XmlException (Exception e) {
		super (e.getMessage ());
		prev = e;
	}
	
	public void printStackTrace () {
		System.out.println (this + ": " + this.getMessage ());
		super.printStackTrace ();
		if (prev != null)
			prev.printStackTrace ();
	}

}
