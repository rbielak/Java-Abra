package org.ephman.xml;
import java_cup.runtime.*;
import java.io.FileReader;
import java.util.HashMap;


public class Yylex implements java_cup.runtime.Scanner {
	private final int YY_BUFFER_SIZE = 512;
	private final int YY_F = -1;
	private final int YY_NO_STATE = -1;
	private final int YY_NOT_ACCEPT = 0;
	private final int YY_START = 1;
	private final int YY_END = 2;
	private final int YY_NO_ANCHOR = 4;
	private final int YY_BOL = 128;
	private final int YY_EOF = 129;

  public void close () throws Exception { 
    if (fr != null)
      fr.close (); // close file handle
  }
    FileReader fr = null;
	public static HashMap ampMappings = new HashMap ();
	static {
	  ampMappings.put ("amp", "&");
	  ampMappings.put ("lt", "<");
	  ampMappings.put ("gt", ">");
	  ampMappings.put ("quot", "\"");
	  // add more if possible ..
	}
    Yylex (String fileName) throws java.io.IOException {
	this ();
	fr = new java.io.FileReader (fileName);
	yy_reader = new java.io.BufferedReader(fr);
    }
	private java.io.BufferedReader yy_reader;
	private int yy_buffer_index;
	private int yy_buffer_read;
	private int yy_buffer_start;
	private int yy_buffer_end;
	private char yy_buffer[];
	private int yychar;
	private int yyline;
	private boolean yy_at_bol;
	private int yy_lexical_state;

	public Yylex (java.io.Reader reader) {
		this ();
		if (null == reader) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(reader);
	}

	public Yylex (java.io.InputStream instream) {
		this ();
		if (null == instream) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(new java.io.InputStreamReader(instream));
	}

	private Yylex () {
		yy_buffer = new char[YY_BUFFER_SIZE];
		yy_buffer_read = 0;
		yy_buffer_index = 0;
		yy_buffer_start = 0;
		yy_buffer_end = 0;
		yychar = 0;
		yyline = 0;
		yy_at_bol = true;
		yy_lexical_state = YYINITIAL;
	}

	private boolean yy_eof_done = false;
	private final int XML = 4;
	private final int INNODE = 2;
	private final int YYINITIAL = 0;
	private final int COMMENT = 1;
	private final int DOCTYPE = 3;
	private final int yy_state_dtrans[] = {
		0,
		47,
		49,
		54,
		55
	};
	private void yybegin (int state) {
		yy_lexical_state = state;
	}
	private int yy_advance ()
		throws java.io.IOException {
		int next_read;
		int i;
		int j;

		if (yy_buffer_index < yy_buffer_read) {
			return yy_buffer[yy_buffer_index++];
		}

		if (0 != yy_buffer_start) {
			i = yy_buffer_start;
			j = 0;
			while (i < yy_buffer_read) {
				yy_buffer[j] = yy_buffer[i];
				++i;
				++j;
			}
			yy_buffer_end = yy_buffer_end - yy_buffer_start;
			yy_buffer_start = 0;
			yy_buffer_read = j;
			yy_buffer_index = j;
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}

		while (yy_buffer_index >= yy_buffer_read) {
			if (yy_buffer_index >= yy_buffer.length) {
				yy_buffer = yy_double(yy_buffer);
			}
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}
		return yy_buffer[yy_buffer_index++];
	}
	private void yy_move_end () {
		if (yy_buffer_end > yy_buffer_start &&
		    '\n' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
		if (yy_buffer_end > yy_buffer_start &&
		    '\r' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
	}
	private boolean yy_last_was_cr=false;
	private void yy_mark_start () {
		int i;
		for (i = yy_buffer_start; i < yy_buffer_index; ++i) {
			if ('\n' == yy_buffer[i] && !yy_last_was_cr) {
				++yyline;
			}
			if ('\r' == yy_buffer[i]) {
				++yyline;
				yy_last_was_cr=true;
			} else yy_last_was_cr=false;
		}
		yychar = yychar
			+ yy_buffer_index - yy_buffer_start;
		yy_buffer_start = yy_buffer_index;
	}
	private void yy_mark_end () {
		yy_buffer_end = yy_buffer_index;
	}
	private void yy_to_mark () {
		yy_buffer_index = yy_buffer_end;
		yy_at_bol = (yy_buffer_end > yy_buffer_start) &&
		            ('\r' == yy_buffer[yy_buffer_end-1] ||
		             '\n' == yy_buffer[yy_buffer_end-1] ||
		             2028/*LS*/ == yy_buffer[yy_buffer_end-1] ||
		             2029/*PS*/ == yy_buffer[yy_buffer_end-1]);
	}
	private java.lang.String yytext () {
		return (new java.lang.String(yy_buffer,
			yy_buffer_start,
			yy_buffer_end - yy_buffer_start));
	}
	private int yylength () {
		return yy_buffer_end - yy_buffer_start;
	}
	private char[] yy_double (char buf[]) {
		int i;
		char newbuf[];
		newbuf = new char[2*buf.length];
		for (i = 0; i < buf.length; ++i) {
			newbuf[i] = buf[i];
		}
		return newbuf;
	}
	private final int YY_E_INTERNAL = 0;
	private final int YY_E_MATCH = 1;
	private java.lang.String yy_error_string[] = {
		"Error: Internal error.\n",
		"Error: Unmatched input.\n"
	};
	private void yy_error (int code,boolean fatal) {
		java.lang.System.out.print(yy_error_string[code]);
		java.lang.System.out.flush();
		if (fatal) {
			throw new Error("Fatal Error.\n");
		}
	}
	private int[][] unpackFromString(int size1, int size2, String st) {
		int colonIndex = -1;
		String lengthString;
		int sequenceLength = 0;
		int sequenceInteger = 0;

		int commaIndex;
		String workString;

		int res[][] = new int[size1][size2];
		for (int i= 0; i < size1; i++) {
			for (int j= 0; j < size2; j++) {
				if (sequenceLength != 0) {
					res[i][j] = sequenceInteger;
					sequenceLength--;
					continue;
				}
				commaIndex = st.indexOf(',');
				workString = (commaIndex==-1) ? st :
					st.substring(0, commaIndex);
				st = st.substring(commaIndex+1);
				colonIndex = workString.indexOf(':');
				if (colonIndex == -1) {
					res[i][j]=Integer.parseInt(workString);
					continue;
				}
				lengthString =
					workString.substring(colonIndex+1);
				sequenceLength=Integer.parseInt(lengthString);
				workString=workString.substring(0,colonIndex);
				sequenceInteger=Integer.parseInt(workString);
				res[i][j] = sequenceInteger;
				sequenceLength--;
			}
		}
		return res;
	}
	private int yy_acpt[] = {
		/* 0 */ YY_NO_ANCHOR,
		/* 1 */ YY_NO_ANCHOR,
		/* 2 */ YY_NO_ANCHOR,
		/* 3 */ YY_NO_ANCHOR,
		/* 4 */ YY_NO_ANCHOR,
		/* 5 */ YY_NO_ANCHOR,
		/* 6 */ YY_NO_ANCHOR,
		/* 7 */ YY_NO_ANCHOR,
		/* 8 */ YY_NO_ANCHOR,
		/* 9 */ YY_NOT_ACCEPT,
		/* 10 */ YY_NO_ANCHOR,
		/* 11 */ YY_NO_ANCHOR,
		/* 12 */ YY_NO_ANCHOR,
		/* 13 */ YY_NO_ANCHOR,
		/* 14 */ YY_NO_ANCHOR,
		/* 15 */ YY_NO_ANCHOR,
		/* 16 */ YY_NO_ANCHOR,
		/* 17 */ YY_NO_ANCHOR,
		/* 18 */ YY_NO_ANCHOR,
		/* 19 */ YY_NO_ANCHOR,
		/* 20 */ YY_NO_ANCHOR,
		/* 21 */ YY_NO_ANCHOR,
		/* 22 */ YY_NO_ANCHOR,
		/* 23 */ YY_NO_ANCHOR,
		/* 24 */ YY_NO_ANCHOR,
		/* 25 */ YY_NO_ANCHOR,
		/* 26 */ YY_NO_ANCHOR,
		/* 27 */ YY_NO_ANCHOR,
		/* 28 */ YY_NO_ANCHOR,
		/* 29 */ YY_NO_ANCHOR,
		/* 30 */ YY_NOT_ACCEPT,
		/* 31 */ YY_NO_ANCHOR,
		/* 32 */ YY_NO_ANCHOR,
		/* 33 */ YY_NO_ANCHOR,
		/* 34 */ YY_NO_ANCHOR,
		/* 35 */ YY_NOT_ACCEPT,
		/* 36 */ YY_NO_ANCHOR,
		/* 37 */ YY_NOT_ACCEPT,
		/* 38 */ YY_NOT_ACCEPT,
		/* 39 */ YY_NOT_ACCEPT,
		/* 40 */ YY_NOT_ACCEPT,
		/* 41 */ YY_NOT_ACCEPT,
		/* 42 */ YY_NOT_ACCEPT,
		/* 43 */ YY_NOT_ACCEPT,
		/* 44 */ YY_NOT_ACCEPT,
		/* 45 */ YY_NOT_ACCEPT,
		/* 46 */ YY_NOT_ACCEPT,
		/* 47 */ YY_NOT_ACCEPT,
		/* 48 */ YY_NOT_ACCEPT,
		/* 49 */ YY_NOT_ACCEPT,
		/* 50 */ YY_NOT_ACCEPT,
		/* 51 */ YY_NOT_ACCEPT,
		/* 52 */ YY_NOT_ACCEPT,
		/* 53 */ YY_NOT_ACCEPT,
		/* 54 */ YY_NOT_ACCEPT,
		/* 55 */ YY_NOT_ACCEPT
	};
	private int yy_cmap[] = unpackFromString(1,130,
"16:8,18:2,19,16:2,20,16:18,18,3,22,16:10,21,16,1,25:10,16:2,2,17,15,11,16,2" +
"4:2,6,4,10,24:9,5,9,24:3,7,24:4,8,24,16,23,16:2,24,16,24:11,14,13,24:10,12," +
"24:2,16:5,0:2")[0];

	private int yy_rmap[] = unpackFromString(1,56,
"0,1,2,3,1,2,4,2,1,5,6,7,1:6,8,9,1,10,1:2,2,1:2,11,1,12,13,14,12,15,16,17,18" +
",19,20,21,22,23,24,25,26,27,28,29,30,31,12,32,33,34,35,36")[0];

	private int yy_nxt[][] = unpackFromString(37,26,
"1,2,3,24:12,4,24,5,6,27,7,24,8,33,24:2,-1:27,24,-1,24:12,-1,24:3,-1,24:2,-1" +
",33,24:2,-1,9,-1,30,10:7,35,10:3,-1:6,10,-1:2,10,-1:2,24,-1,24:12,-1,24:2,6" +
",27,24:2,-1,33,24:2,-1:4,11:7,-1,11:3,-1:6,11,-1:2,11,-1:5,10:7,-1,10:3,-1:" +
"6,10,-1:2,10:2,-1:4,11:7,-1,11:3,-1:6,11,-1:2,11:2,-1:4,10:7,-1,10:3,-1:6,1" +
"0,-1:2,10,-1:5,19:7,-1,19:3,-1:6,19,-1:2,19:2,-1,21:10,-1,21:3,-1,21:10,-1:" +
"18,27:2,-1:7,50:18,-1,50:2,20,51,50:2,-1:4,38,-1:16,39,-1:25,48,-1:5,24,-1," +
"24:12,-1,24:2,36,37,24:3,33,24:2,-1:15,23,-1:22,40,-1:14,24,-1,24:12,-1,24:" +
"2,36,37,24:2,-1,33,24:2,-1:18,37:2,-1:3,24,-1:7,41,-1:41,12,-1:17,42,-1:18," +
"43,-1:33,13,-1:18,44,-1:26,45,-1:26,46,-1:26,14,-1:15,1,15:18,16,28,31,15:4" +
",-1:15,17,-1:10,1,25,18,8,19:7,8,19:3,4,8,26,27:2,28,19,29,8,19,8,-1,50:17," +
"52,53,50:2,32,51,50:2,-1,50:17,52,53,50:2,20,51,50:2,-1:18,53:2,-1:3,50,-1:" +
"2,1,21:10,8,21:3,22,21:10,1,21:10,34,21:3,8,21:10");

	public java_cup.runtime.Symbol next_token ()
		throws java.io.IOException {
		int yy_lookahead;
		int yy_anchor = YY_NO_ANCHOR;
		int yy_state = yy_state_dtrans[yy_lexical_state];
		int yy_next_state = YY_NO_STATE;
		int yy_last_accept_state = YY_NO_STATE;
		boolean yy_initial = true;
		int yy_this_accept;

		yy_mark_start();
		yy_this_accept = yy_acpt[yy_state];
		if (YY_NOT_ACCEPT != yy_this_accept) {
			yy_last_accept_state = yy_state;
			yy_mark_end();
		}
		while (true) {
			if (yy_initial && yy_at_bol) yy_lookahead = YY_BOL;
			else yy_lookahead = yy_advance();
			yy_next_state = YY_F;
			yy_next_state = yy_nxt[yy_rmap[yy_state]][yy_cmap[yy_lookahead]];
			if (YY_EOF == yy_lookahead && true == yy_initial) {

    return new Symbol(sym.EOF);
			}
			if (YY_F != yy_next_state) {
				yy_state = yy_next_state;
				yy_initial = false;
				yy_this_accept = yy_acpt[yy_state];
				if (YY_NOT_ACCEPT != yy_this_accept) {
					yy_last_accept_state = yy_state;
					yy_mark_end();
				}
			}
			else {
				if (YY_NO_STATE == yy_last_accept_state) {
					throw (new Error("Lexical Error: Unmatched Input."));
				}
				else {
					yy_anchor = yy_acpt[yy_last_accept_state];
					if (0 != (YY_END & yy_anchor)) {
						yy_move_end();
					}
					yy_to_mark();
					switch (yy_last_accept_state) {
					case 0:
						{
	//	System.out.println ("Text==" + yytext ());
    return (new Symbol(sym.TEXT,yyline,yychar,yytext()));
}
					case -2:
						break;
					case 1:
						
					case -3:
						break;
					case 2:
						{ return (new Symbol(sym.SLASH,yyline, yychar)); }
					case -4:
						break;
					case 3:
						{ yybegin (INNODE); return (new Symbol(sym.LBRACK,yyline, yychar)); }
					case -5:
						break;
					case 4:
						{ yybegin (YYINITIAL); return (new Symbol(sym.RBRACK,yyline, yychar)); }
					case -6:
						break;
					case 5:
						{ return (new Symbol(sym.EQUALS,yyline, yychar)); }
					case -7:
						break;
					case 6:
						{ }
					case -8:
						break;
					case 7:
						{ }
					case -9:
						break;
					case 8:
						{
    System.out.println("Illegal character: <" + yytext() + ">");	
}
					case -10:
						break;
					case 10:
						{
	yybegin (INNODE);	
    return (new Symbol(sym.LBRACK_NAME,yyline,yychar,yytext().substring (1)));
}
					case -11:
						break;
					case 11:
						{
	//	yybegin (INNODE);
    return (new Symbol(sym.LBRACK_SLASH_NAME,yyline,yychar,yytext().substring (2)));
}
					case -12:
						break;
					case 12:
						{ yybegin(COMMENT);  }
					case -13:
						break;
					case 13:
						{ yybegin (XML); }
					case -14:
						break;
					case 14:
						{ yybegin (DOCTYPE); }
					case -15:
						break;
					case 15:
						{ /*System.out.println ("Un-Matched:'" + yytext ()+ "'");*/ }
					case -16:
						break;
					case 16:
						{ }
					case -17:
						break;
					case 17:
						{ 
    yybegin(YYINITIAL);
}
					case -18:
						break;
					case 18:
						{ yybegin (INNODE); return (new Symbol(sym.UNCLOSED,yyline, yychar)); }
					case -19:
						break;
					case 19:
						{
    return (new Symbol(sym.NAME,yyline,yychar,yytext()));
}
					case -20:
						break;
					case 20:
						{
	String str = yytext();
	str = str.substring (1, str.length() -1);
	int ampIndex = str.indexOf ("&");
	while (ampIndex != -1) {
	  int semiIndex = str.indexOf (";", ampIndex);
	  /*	// hack to replace &quot; */
	  if (semiIndex == -1) {
		throw new java.io.IOException ("unclosed & at line="+yyline+" char="+yychar +
									   " text='"+str+"'");
	  }
	  String internal = str.substring (ampIndex+1,semiIndex);
	  String lookup = (String)ampMappings.get (internal);
	  if (lookup != null) {
		System.out.println ("&"+internal+"; matched '"+lookup+"'");
		str = str.substring (0, ampIndex) + lookup + str.substring (semiIndex+1);
		ampIndex++;
	  }
	  else {
		System.out.println ("&"+internal+"; unmatched..");
		ampIndex += internal.length ();
	  }
	  ampIndex = str.indexOf ("&", ampIndex);
	}
    return (new Symbol(sym.QUOTED_NAME,yyline,yychar,str));
}
					case -21:
						break;
					case 21:
						{ }
					case -22:
						break;
					case 22:
						{ yybegin (YYINITIAL); }
					case -23:
						break;
					case 23:
						{ yybegin (YYINITIAL); }
					case -24:
						break;
					case 24:
						{
	//	System.out.println ("Text==" + yytext ());
    return (new Symbol(sym.TEXT,yyline,yychar,yytext()));
}
					case -25:
						break;
					case 25:
						{ return (new Symbol(sym.SLASH,yyline, yychar)); }
					case -26:
						break;
					case 26:
						{ return (new Symbol(sym.EQUALS,yyline, yychar)); }
					case -27:
						break;
					case 27:
						{ }
					case -28:
						break;
					case 28:
						{ }
					case -29:
						break;
					case 29:
						{
    System.out.println("Illegal character: <" + yytext() + ">");	
}
					case -30:
						break;
					case 31:
						{ /*System.out.println ("Un-Matched:'" + yytext ()+ "'");*/ }
					case -31:
						break;
					case 32:
						{
	String str = yytext();
	str = str.substring (1, str.length() -1);
	int ampIndex = str.indexOf ("&");
	while (ampIndex != -1) {
	  int semiIndex = str.indexOf (";", ampIndex);
	  /*	// hack to replace &quot; */
	  if (semiIndex == -1) {
		throw new java.io.IOException ("unclosed & at line="+yyline+" char="+yychar +
									   " text='"+str+"'");
	  }
	  String internal = str.substring (ampIndex+1,semiIndex);
	  String lookup = (String)ampMappings.get (internal);
	  if (lookup != null) {
		System.out.println ("&"+internal+"; matched '"+lookup+"'");
		str = str.substring (0, ampIndex) + lookup + str.substring (semiIndex+1);
		ampIndex++;
	  }
	  else {
		System.out.println ("&"+internal+"; unmatched..");
		ampIndex += internal.length ();
	  }
	  ampIndex = str.indexOf ("&", ampIndex);
	}
    return (new Symbol(sym.QUOTED_NAME,yyline,yychar,str));
}
					case -32:
						break;
					case 33:
						{
	//	System.out.println ("Text==" + yytext ());
    return (new Symbol(sym.TEXT,yyline,yychar,yytext()));
}
					case -33:
						break;
					case 34:
						{
    System.out.println("Illegal character: <" + yytext() + ">");	
}
					case -34:
						break;
					case 36:
						{
	//	System.out.println ("Text==" + yytext ());
    return (new Symbol(sym.TEXT,yyline,yychar,yytext()));
}
					case -35:
						break;
					default:
						yy_error(YY_E_INTERNAL,false);
					case -1:
					}
					yy_initial = true;
					yy_state = yy_state_dtrans[yy_lexical_state];
					yy_next_state = YY_NO_STATE;
					yy_last_accept_state = YY_NO_STATE;
					yy_mark_start();
					yy_this_accept = yy_acpt[yy_state];
					if (YY_NOT_ACCEPT != yy_this_accept) {
						yy_last_accept_state = yy_state;
						yy_mark_end();
					}
				}
			}
		}
	}
}
