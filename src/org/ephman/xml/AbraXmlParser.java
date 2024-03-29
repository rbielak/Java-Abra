
//----------------------------------------------------
// The following code was generated by CUP v0.10j
// Thu Jun 13 14:14:27 EDT 2002
//----------------------------------------------------

package org.ephman.xml;

import java_cup.runtime.*;
import java.util.Vector;
import java.util.HashMap;
import java.io.Reader;

/** CUP v0.10j generated parser.
  * @version Thu Jun 13 14:14:27 EDT 2002
  */
public class AbraXmlParser extends java_cup.runtime.lr_parser implements XmlParser {

  /** Default constructor. */
  public AbraXmlParser() {super();}

  /** Constructor which sets the default scanner. */
  public AbraXmlParser(java_cup.runtime.Scanner s) {super(s);}

  /** Production table. */
  protected static final short _production_table[][] = 
    unpackFromStrings(new String[] {
    "\000\025\000\002\002\004\000\002\003\006\000\002\003" +
    "\010\000\002\003\007\000\002\003\010\000\002\003\003" +
    "\000\002\003\003\000\002\004\007\000\002\007\004\000" +
    "\002\007\003\000\002\006\002\000\002\006\003\000\002" +
    "\005\004\000\002\005\003\000\002\010\005\000\002\013" +
    "\003\000\002\012\003\000\002\011\003\000\002\015\004" +
    "\000\002\015\003\000\002\014\003" });

  /** Access to production table. */
  public short[][] production_table() {return _production_table;}

  /** Parse-action table. */
  protected static final short[][] _action_table = 
    unpackFromStrings(new String[] {
    "\000\043\000\006\003\006\013\010\001\002\000\014\003" +
    "\ufff7\005\ufff7\006\ufff7\011\016\013\ufff7\001\002\000\012" +
    "\002\ufffc\003\ufffc\013\ufffc\014\ufffc\001\002\000\012\002" +
    "\ufffb\003\ufffb\013\ufffb\014\ufffb\001\002\000\004\002\011" +
    "\001\002\000\014\003\ufff1\005\ufff1\006\ufff1\011\ufff1\013" +
    "\ufff1\001\002\000\004\002\001\001\002\000\012\003\006" +
    "\005\023\006\024\013\010\001\002\000\014\003\ufff6\005" +
    "\ufff6\006\ufff6\011\016\013\ufff6\001\002\000\004\007\017" +
    "\001\002\000\014\003\ufff4\005\ufff4\006\ufff4\011\ufff4\013" +
    "\ufff4\001\002\000\004\007\ufff2\001\002\000\004\010\021" +
    "\001\002\000\014\003\ufff3\005\ufff3\006\ufff3\011\ufff3\013" +
    "\ufff3\001\002\000\014\003\uffed\005\uffed\006\uffed\011\uffed" +
    "\013\uffed\001\002\000\014\003\ufff5\005\ufff5\006\ufff5\011" +
    "\ufff5\013\ufff5\001\002\000\012\003\006\012\036\013\010" +
    "\014\031\001\002\000\004\005\033\001\002\000\010\003" +
    "\ufff8\013\ufff8\014\ufff8\001\002\000\010\003\006\013\010" +
    "\014\031\001\002\000\010\003\ufff9\013\ufff9\014\ufff9\001" +
    "\002\000\004\005\032\001\002\000\004\005\ufff0\001\002" +
    "\000\012\002\ufffa\003\ufffa\013\ufffa\014\ufffa\001\002\000" +
    "\012\002\000\003\000\013\000\014\000\001\002\000\010" +
    "\003\006\013\010\014\031\001\002\000\004\005\043\001" +
    "\002\000\006\012\036\014\uffee\001\002\000\004\014\031" +
    "\001\002\000\004\005\041\001\002\000\012\002\uffff\003" +
    "\uffff\013\uffff\014\uffff\001\002\000\004\014\uffef\001\002" +
    "\000\012\002\ufffe\003\ufffe\013\ufffe\014\ufffe\001\002\000" +
    "\004\005\045\001\002\000\012\002\ufffd\003\ufffd\013\ufffd" +
    "\014\ufffd\001\002" });

  /** Access to parse-action table. */
  public short[][] action_table() {return _action_table;}

  /** <code>reduce_goto</code> table. */
  protected static final short[][] _reduce_table = 
    unpackFromStrings(new String[] {
    "\000\043\000\010\003\006\004\004\012\003\001\001\000" +
    "\012\005\012\006\011\010\014\013\013\001\001\000\002" +
    "\001\001\000\002\001\001\000\002\001\001\000\002\001" +
    "\001\000\002\001\001\000\012\003\024\004\004\007\025" +
    "\012\003\001\001\000\006\010\021\013\013\001\001\000" +
    "\002\001\001\000\002\001\001\000\002\001\001\000\004" +
    "\014\017\001\001\000\002\001\001\000\002\001\001\000" +
    "\002\001\001\000\016\003\024\004\004\007\033\011\034" +
    "\012\003\015\036\001\001\000\002\001\001\000\002\001" +
    "\001\000\012\003\026\004\004\011\027\012\003\001\001" +
    "\000\002\001\001\000\002\001\001\000\002\001\001\000" +
    "\002\001\001\000\002\001\001\000\012\003\026\004\004" +
    "\011\043\012\003\001\001\000\002\001\001\000\004\015" +
    "\041\001\001\000\004\011\037\001\001\000\002\001\001" +
    "\000\002\001\001\000\002\001\001\000\002\001\001\000" +
    "\002\001\001\000\002\001\001" });

  /** Access to <code>reduce_goto</code> table. */
  public short[][] reduce_table() {return _reduce_table;}

  /** Instance of action encapsulation class. */
  protected CUP$AbraXmlParser$actions action_obj;

  /** Action encapsulation object initializer. */
  protected void init_actions()
    {
      action_obj = new CUP$AbraXmlParser$actions(this);
    }

  /** Invoke a user supplied parse action. */
  public java_cup.runtime.Symbol do_action(
    int                        act_num,
    java_cup.runtime.lr_parser parser,
    java.util.Stack            stack,
    int                        top)
    throws java.lang.Exception
  {
    /* call code in generated class */
    return action_obj.CUP$AbraXmlParser$do_action(act_num, parser, stack, top);
  }

  /** Indicates start state. */
  public int start_state() {return 0;}
  /** Indicates start production. */
  public int start_production() {return 0;}

  /** <code>EOF</code> Symbol index. */
  public int EOF_sym() {return 0;}

  /** <code>error</code> Symbol index. */
  public int error_sym() {return 1;}



	public AbraXmlParser (Reader r) {
		this (new Yylex (r));		
	}

	public XmlNode parseXml () throws Exception {
		Symbol s = this.parse ();
		XmlNode result = (XmlNode)s.value;
		((Yylex)getScanner()).close ();
		return result;
	}


}

/** Cup generated class to encapsulate user supplied action code.*/
class CUP$AbraXmlParser$actions {

 
	public void checkName (String n, String e, int line) throws XmlException {
		if (!n.equals (e)) 
			throw new XmlException ("Node " + n + " expects </" + n + ">" 
						+ " not " + e, line);
	}

	public void parseError (String str, Object o, int line) throws Exception {
		
		
		Exception e = new XmlException (str, line);
//		e.printStackTrace ();
		parser.done_parsing (); //report_fatal_error (str + " line=" + line, o);
		throw e;
	}


  private final AbraXmlParser parser;

  /** Constructor */
  CUP$AbraXmlParser$actions(AbraXmlParser parser) {
    this.parser = parser;
  }

  /** Method with the actual generated action code. */
  public final java_cup.runtime.Symbol CUP$AbraXmlParser$do_action(
    int                        CUP$AbraXmlParser$act_num,
    java_cup.runtime.lr_parser CUP$AbraXmlParser$parser,
    java.util.Stack            CUP$AbraXmlParser$stack,
    int                        CUP$AbraXmlParser$top)
    throws java.lang.Exception
    {
      /* Symbol object for return from actions */
      java_cup.runtime.Symbol CUP$AbraXmlParser$result;

      /* select the action based on the action number */
      switch (CUP$AbraXmlParser$act_num)
        {
          /*. . . . . . . . . . . . . . . . . . . .*/
          case 20: // quoted_name ::= QUOTED_NAME 
            {
              String RESULT = null;
		int nleft = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).left;
		int nright = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).right;
		Object n = (Object)((java_cup.runtime.Symbol) CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).value;
		 RESULT = (String)n; 
              CUP$AbraXmlParser$result = new java_cup.runtime.Symbol(10/*quoted_name*/, ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).left, ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).right, RESULT);
            }
          return CUP$AbraXmlParser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 19: // text ::= TEXT 
            {
              String RESULT = null;
		int nleft = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).left;
		int nright = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).right;
		Object n = (Object)((java_cup.runtime.Symbol) CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).value;
		 RESULT = (String)n; 
              CUP$AbraXmlParser$result = new java_cup.runtime.Symbol(11/*text*/, ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).left, ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).right, RESULT);
            }
          return CUP$AbraXmlParser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 18: // text ::= TEXT text 
            {
              String RESULT = null;
		int nleft = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-1)).left;
		int nright = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-1)).right;
		Object n = (Object)((java_cup.runtime.Symbol) CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-1)).value;
		int tleft = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).left;
		int tright = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).right;
		String t = (String)((java_cup.runtime.Symbol) CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).value;
		 RESULT = (String)n + t; 
              CUP$AbraXmlParser$result = new java_cup.runtime.Symbol(11/*text*/, ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-1)).left, ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).right, RESULT);
            }
          return CUP$AbraXmlParser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 17: // lbsl_name ::= LBRACK_SLASH_NAME 
            {
              String RESULT = null;
		int nleft = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).left;
		int nright = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).right;
		Object n = (Object)((java_cup.runtime.Symbol) CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).value;
		 RESULT = (String)n; 
              CUP$AbraXmlParser$result = new java_cup.runtime.Symbol(7/*lbsl_name*/, ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).left, ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).right, RESULT);
            }
          return CUP$AbraXmlParser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 16: // lb_name ::= LBRACK_NAME 
            {
              String RESULT = null;
		int nleft = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).left;
		int nright = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).right;
		Object n = (Object)((java_cup.runtime.Symbol) CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).value;
		 RESULT = (String)n; 
              CUP$AbraXmlParser$result = new java_cup.runtime.Symbol(8/*lb_name*/, ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).left, ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).right, RESULT);
            }
          return CUP$AbraXmlParser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 15: // name ::= NAME 
            {
              String RESULT = null;
		int nleft = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).left;
		int nright = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).right;
		Object n = (Object)((java_cup.runtime.Symbol) CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).value;
		 RESULT = (String)n; 
              CUP$AbraXmlParser$result = new java_cup.runtime.Symbol(9/*name*/, ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).left, ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).right, RESULT);
            }
          return CUP$AbraXmlParser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 14: // attribute ::= name EQUALS quoted_name 
            {
              XmlAttribute RESULT = null;
		int nleft = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-2)).left;
		int nright = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-2)).right;
		String n = (String)((java_cup.runtime.Symbol) CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-2)).value;
		int vleft = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).left;
		int vright = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).right;
		String v = (String)((java_cup.runtime.Symbol) CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).value;
		 RESULT = new XmlAttribute (n,v); 
              CUP$AbraXmlParser$result = new java_cup.runtime.Symbol(6/*attribute*/, ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-2)).left, ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).right, RESULT);
            }
          return CUP$AbraXmlParser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 13: // attr_list ::= attribute 
            {
              Vector RESULT = null;
		int aleft = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).left;
		int aright = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).right;
		XmlAttribute a = (XmlAttribute)((java_cup.runtime.Symbol) CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).value;
		 RESULT = new Vector (); RESULT.add (a); 
              CUP$AbraXmlParser$result = new java_cup.runtime.Symbol(3/*attr_list*/, ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).left, ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).right, RESULT);
            }
          return CUP$AbraXmlParser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 12: // attr_list ::= attr_list attribute 
            {
              Vector RESULT = null;
		int alleft = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-1)).left;
		int alright = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-1)).right;
		Vector al = (Vector)((java_cup.runtime.Symbol) CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-1)).value;
		int aleft = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).left;
		int aright = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).right;
		XmlAttribute a = (XmlAttribute)((java_cup.runtime.Symbol) CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).value;
		 RESULT = al; al.add (a); 
              CUP$AbraXmlParser$result = new java_cup.runtime.Symbol(3/*attr_list*/, ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-1)).left, ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).right, RESULT);
            }
          return CUP$AbraXmlParser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 11: // attr_list_opt ::= attr_list 
            {
              Vector RESULT = null;
		int alleft = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).left;
		int alright = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).right;
		Vector al = (Vector)((java_cup.runtime.Symbol) CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).value;
		 RESULT = al; 
              CUP$AbraXmlParser$result = new java_cup.runtime.Symbol(4/*attr_list_opt*/, ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).left, ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).right, RESULT);
            }
          return CUP$AbraXmlParser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 10: // attr_list_opt ::= 
            {
              Vector RESULT = null;
		 RESULT= null; 
              CUP$AbraXmlParser$result = new java_cup.runtime.Symbol(4/*attr_list_opt*/, ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).right, ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).right, RESULT);
            }
          return CUP$AbraXmlParser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 9: // node_list ::= node 
            {
              Vector RESULT = null;
		int nleft = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).left;
		int nright = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).right;
		AbraXmlNode n = (AbraXmlNode)((java_cup.runtime.Symbol) CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).value;
		 RESULT= new Vector (); RESULT.addElement (n); 
              CUP$AbraXmlParser$result = new java_cup.runtime.Symbol(5/*node_list*/, ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).left, ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).right, RESULT);
            }
          return CUP$AbraXmlParser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 8: // node_list ::= node_list node 
            {
              Vector RESULT = null;
		int nlleft = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-1)).left;
		int nlright = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-1)).right;
		Vector nl = (Vector)((java_cup.runtime.Symbol) CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-1)).value;
		int nleft = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).left;
		int nright = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).right;
		AbraXmlNode n = (AbraXmlNode)((java_cup.runtime.Symbol) CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).value;
		 nl.addElement (n); RESULT=nl; 
              CUP$AbraXmlParser$result = new java_cup.runtime.Symbol(5/*node_list*/, ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-1)).left, ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).right, RESULT);
            }
          return CUP$AbraXmlParser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 7: // error_node ::= lb_name attr_list_opt node_list lbsl_name RBRACK 
            {
              AbraXmlNode RESULT = null;
		int nleft = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-4)).left;
		int nright = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-4)).right;
		String n = (String)((java_cup.runtime.Symbol) CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-4)).value;
		int alleft = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-3)).left;
		int alright = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-3)).right;
		Vector al = (Vector)((java_cup.runtime.Symbol) CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-3)).value;
		int nlleft = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-2)).left;
		int nlright = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-2)).right;
		Vector nl = (Vector)((java_cup.runtime.Symbol) CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-2)).value;
		int endleft = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-1)).left;
		int endright = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-1)).right;
		String end = (String)((java_cup.runtime.Symbol) CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-1)).value;
		 parseError ("Node " + n + " must end with '>'", n, nleft);  
              CUP$AbraXmlParser$result = new java_cup.runtime.Symbol(2/*error_node*/, ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-4)).left, ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).right, RESULT);
            }
          return CUP$AbraXmlParser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 6: // node ::= error 
            {
              AbraXmlNode RESULT = null;
		int eleft = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).left;
		int eright = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).right;
		Object e = (Object)((java_cup.runtime.Symbol) CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).value;
		 parseError ("unparseable ", null,eleft); 
              CUP$AbraXmlParser$result = new java_cup.runtime.Symbol(1/*node*/, ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).left, ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).right, RESULT);
            }
          return CUP$AbraXmlParser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 5: // node ::= error_node 
            {
              AbraXmlNode RESULT = null;
		int eleft = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).left;
		int eright = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).right;
		AbraXmlNode e = (AbraXmlNode)((java_cup.runtime.Symbol) CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).value;
		 RESULT=new AbraXmlNode ("ERROR", null); 
              CUP$AbraXmlParser$result = new java_cup.runtime.Symbol(1/*node*/, ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).left, ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).right, RESULT);
            }
          return CUP$AbraXmlParser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 4: // node ::= lb_name attr_list_opt RBRACK node_list lbsl_name RBRACK 
            {
              AbraXmlNode RESULT = null;
		int nleft = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-5)).left;
		int nright = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-5)).right;
		String n = (String)((java_cup.runtime.Symbol) CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-5)).value;
		int alleft = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-4)).left;
		int alright = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-4)).right;
		Vector al = (Vector)((java_cup.runtime.Symbol) CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-4)).value;
		int nlleft = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-2)).left;
		int nlright = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-2)).right;
		Vector nl = (Vector)((java_cup.runtime.Symbol) CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-2)).value;
		int endleft = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-1)).left;
		int endright = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-1)).right;
		String end = (String)((java_cup.runtime.Symbol) CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-1)).value;
		 checkName (n, end, endleft);
	RESULT=new AbraXmlNode (n, al); RESULT.setList (nl); 
              CUP$AbraXmlParser$result = new java_cup.runtime.Symbol(1/*node*/, ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-5)).left, ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).right, RESULT);
            }
          return CUP$AbraXmlParser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 3: // node ::= lb_name attr_list_opt RBRACK lbsl_name RBRACK 
            {
              AbraXmlNode RESULT = null;
		int nleft = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-4)).left;
		int nright = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-4)).right;
		String n = (String)((java_cup.runtime.Symbol) CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-4)).value;
		int alleft = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-3)).left;
		int alright = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-3)).right;
		Vector al = (Vector)((java_cup.runtime.Symbol) CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-3)).value;
		int endleft = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-1)).left;
		int endright = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-1)).right;
		String end = (String)((java_cup.runtime.Symbol) CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-1)).value;
		 checkName (n, end, endleft); 
	RESULT=new AbraXmlNode (n, al); RESULT.setList (new Vector ());
              CUP$AbraXmlParser$result = new java_cup.runtime.Symbol(1/*node*/, ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-4)).left, ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).right, RESULT);
            }
          return CUP$AbraXmlParser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 2: // node ::= lb_name attr_list_opt RBRACK text lbsl_name RBRACK 
            {
              AbraXmlNode RESULT = null;
		int nleft = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-5)).left;
		int nright = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-5)).right;
		String n = (String)((java_cup.runtime.Symbol) CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-5)).value;
		int alleft = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-4)).left;
		int alright = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-4)).right;
		Vector al = (Vector)((java_cup.runtime.Symbol) CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-4)).value;
		int tleft = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-2)).left;
		int tright = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-2)).right;
		String t = (String)((java_cup.runtime.Symbol) CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-2)).value;
		int endleft = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-1)).left;
		int endright = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-1)).right;
		String end = (String)((java_cup.runtime.Symbol) CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-1)).value;
		 checkName (n, end, endleft); RESULT=new AbraXmlNode (n, al); RESULT.setText (t);
              CUP$AbraXmlParser$result = new java_cup.runtime.Symbol(1/*node*/, ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-5)).left, ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).right, RESULT);
            }
          return CUP$AbraXmlParser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 1: // node ::= lb_name attr_list_opt SLASH RBRACK 
            {
              AbraXmlNode RESULT = null;
		int nleft = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-3)).left;
		int nright = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-3)).right;
		String n = (String)((java_cup.runtime.Symbol) CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-3)).value;
		int alleft = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-2)).left;
		int alright = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-2)).right;
		Vector al = (Vector)((java_cup.runtime.Symbol) CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-2)).value;
		 RESULT = new AbraXmlNode (n, al); 
              CUP$AbraXmlParser$result = new java_cup.runtime.Symbol(1/*node*/, ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-3)).left, ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).right, RESULT);
            }
          return CUP$AbraXmlParser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 0: // $START ::= node EOF 
            {
              Object RESULT = null;
		int start_valleft = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-1)).left;
		int start_valright = ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-1)).right;
		AbraXmlNode start_val = (AbraXmlNode)((java_cup.runtime.Symbol) CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-1)).value;
		RESULT = start_val;
              CUP$AbraXmlParser$result = new java_cup.runtime.Symbol(0/*$START*/, ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-1)).left, ((java_cup.runtime.Symbol)CUP$AbraXmlParser$stack.elementAt(CUP$AbraXmlParser$top-0)).right, RESULT);
            }
          /* ACCEPT */
          CUP$AbraXmlParser$parser.done_parsing();
          return CUP$AbraXmlParser$result;

          /* . . . . . .*/
          default:
            throw new Exception(
               "Invalid action number found in internal parse table");

        }
    }
}

