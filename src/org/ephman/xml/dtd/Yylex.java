package org.ephman.xml.dtd;
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
	private final int XML = 5;
	private final int ATTLIST = 3;
	private final int YYINITIAL = 0;
	private final int COMMENT = 1;
	private final int DOCTYPE = 4;
	private final int ELEMENT = 2;
	private final int yy_state_dtrans[] = {
		0,
		68,
		70,
		75,
		84,
		85
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
		/* 6 */ YY_NOT_ACCEPT,
		/* 7 */ YY_NO_ANCHOR,
		/* 8 */ YY_NO_ANCHOR,
		/* 9 */ YY_NO_ANCHOR,
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
		/* 30 */ YY_NO_ANCHOR,
		/* 31 */ YY_NO_ANCHOR,
		/* 32 */ YY_NO_ANCHOR,
		/* 33 */ YY_NO_ANCHOR,
		/* 34 */ YY_NOT_ACCEPT,
		/* 35 */ YY_NO_ANCHOR,
		/* 36 */ YY_NO_ANCHOR,
		/* 37 */ YY_NO_ANCHOR,
		/* 38 */ YY_NO_ANCHOR,
		/* 39 */ YY_NO_ANCHOR,
		/* 40 */ YY_NOT_ACCEPT,
		/* 41 */ YY_NO_ANCHOR,
		/* 42 */ YY_NO_ANCHOR,
		/* 43 */ YY_NO_ANCHOR,
		/* 44 */ YY_NOT_ACCEPT,
		/* 45 */ YY_NO_ANCHOR,
		/* 46 */ YY_NO_ANCHOR,
		/* 47 */ YY_NOT_ACCEPT,
		/* 48 */ YY_NO_ANCHOR,
		/* 49 */ YY_NO_ANCHOR,
		/* 50 */ YY_NOT_ACCEPT,
		/* 51 */ YY_NO_ANCHOR,
		/* 52 */ YY_NOT_ACCEPT,
		/* 53 */ YY_NOT_ACCEPT,
		/* 54 */ YY_NOT_ACCEPT,
		/* 55 */ YY_NOT_ACCEPT,
		/* 56 */ YY_NOT_ACCEPT,
		/* 57 */ YY_NOT_ACCEPT,
		/* 58 */ YY_NOT_ACCEPT,
		/* 59 */ YY_NOT_ACCEPT,
		/* 60 */ YY_NOT_ACCEPT,
		/* 61 */ YY_NOT_ACCEPT,
		/* 62 */ YY_NOT_ACCEPT,
		/* 63 */ YY_NOT_ACCEPT,
		/* 64 */ YY_NOT_ACCEPT,
		/* 65 */ YY_NOT_ACCEPT,
		/* 66 */ YY_NOT_ACCEPT,
		/* 67 */ YY_NOT_ACCEPT,
		/* 68 */ YY_NOT_ACCEPT,
		/* 69 */ YY_NOT_ACCEPT,
		/* 70 */ YY_NOT_ACCEPT,
		/* 71 */ YY_NOT_ACCEPT,
		/* 72 */ YY_NOT_ACCEPT,
		/* 73 */ YY_NOT_ACCEPT,
		/* 74 */ YY_NOT_ACCEPT,
		/* 75 */ YY_NOT_ACCEPT,
		/* 76 */ YY_NOT_ACCEPT,
		/* 77 */ YY_NOT_ACCEPT,
		/* 78 */ YY_NOT_ACCEPT,
		/* 79 */ YY_NOT_ACCEPT,
		/* 80 */ YY_NOT_ACCEPT,
		/* 81 */ YY_NOT_ACCEPT,
		/* 82 */ YY_NOT_ACCEPT,
		/* 83 */ YY_NOT_ACCEPT,
		/* 84 */ YY_NOT_ACCEPT,
		/* 85 */ YY_NOT_ACCEPT,
		/* 86 */ YY_NO_ANCHOR,
		/* 87 */ YY_NOT_ACCEPT,
		/* 88 */ YY_NO_ANCHOR,
		/* 89 */ YY_NOT_ACCEPT,
		/* 90 */ YY_NO_ANCHOR,
		/* 91 */ YY_NOT_ACCEPT,
		/* 92 */ YY_NO_ANCHOR,
		/* 93 */ YY_NO_ANCHOR,
		/* 94 */ YY_NO_ANCHOR,
		/* 95 */ YY_NO_ANCHOR,
		/* 96 */ YY_NO_ANCHOR,
		/* 97 */ YY_NO_ANCHOR,
		/* 98 */ YY_NO_ANCHOR,
		/* 99 */ YY_NO_ANCHOR,
		/* 100 */ YY_NO_ANCHOR
	};
	private int yy_cmap[] = unpackFromString(1,130,
"22:8,23:2,24,22:2,25,22:18,23,3,38,35,22:4,27,29,42:2,30,26,22,1,41:10,22:2" +
",2,22,21,17,22,9,40,14,12,4,36,40:2,10,40:2,5,6,7,13,16,40:2,11,8,40:3,37,1" +
"5,40,22,39,22:2,40,22,34,40:10,20,19,32,40:5,33,40:3,18,31,40,22,28,22:3,0:" +
"2")[0];

	private int yy_rmap[] = unpackFromString(1,101,
"0,1,2,3,4,2,5,1:8,6,1:5,7:2,1:2,7:2,1,8,1,2,1,9,1,10,11,1,12,13,14,15,16,17" +
",18,19,20,12,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41" +
",42,43,44,45,46,47,48,49,50,12,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65" +
",66,7,67,68,69,70,71,7")[0];

	private int yy_nxt[][] = unpackFromString(72,43,
"1,2,3,30:18,31,30,4,32,5,30:12,31,38,30:3,-1:44,30,-1,30:18,-1,30:2,-1,30:1" +
"3,-1,38,30:3,-1:3,6,-1:13,34,-1:26,30,-1,30:18,-1,30,4,32,30:13,-1,38,30:3," +
"-1:4,44,-1:4,47,-1:2,50,-1:13,52,-1:20,100:2,93,100:10,36,100:3,-1:5,100,-1" +
":4,100:4,-1,100:2,-1:2,100,94,36,-1:4,100:13,36,100:3,-1:5,100,-1:4,100:4,-" +
"1,100:2,-1:2,100,94,36,-1,28:16,-1,28:3,-1,28:21,-1:23,32:2,-1:36,53,-1:50," +
"69,-1:17,77:23,-1,77:13,24,78,77:3,-1,30,-1,30:18,-1,30,42,40,30:14,38,30:3" +
",-1:16,71,-1:49,40:2,-1:14,30,-1:7,100:11,21,100,36,100:3,-1:5,100,-1:4,100" +
":4,-1,100:2,-1:2,100,94,36,-1,30,-1,30:18,-1,30,42,40,30:13,-1,38,30:3,-1:3" +
"6,76,-1:11,54,-1:41,100:11,22,100,36,100:3,-1:5,100,-1:4,100:4,-1,100:2,-1:" +
"2,100,94,36,-1:8,87,-1:38,100:5,25,100:7,36,100:3,-1:5,100,-1:4,100:4,-1,10" +
"0:2,-1:2,100,94,36,-1:21,29,-1:34,55,-1:33,100:13,36,26,100:2,-1:5,100,-1:4" +
",100:4,-1,100:2,-1:2,100,94,36,-1:26,7,-1:35,56,-1:27,57,-1:52,89,-1:48,8,-" +
"1:28,59,-1:41,60,-1:41,62,-1:48,63,-1:47,64,-1:34,65,-1:46,66,-1:47,67,-1:3" +
"4,9,-1:42,10,-1:38,11,-1:38,1,12:23,13,33,35,12:16,-1:21,14,-1:21,1,31:3,15" +
",100:4,86,100:7,31,100:3,16,31,32:2,33,100,17,18,19,20,100:4,39,100:2,31:2," +
"100,31:2,-1:14,72,-1:40,73,-1:39,91,-1:42,23,-1:33,1,31:3,100:7,95,100:2,96" +
",100:2,31,100:3,16,31,32:2,33,100,17,18,19,20,100:4,43,100:2,46,31,100,31:2" +
",-1:10,79,-1:33,77:22,80,81,77:13,37,78,77:3,-1:37,82,-1:6,77:22,80,81,77:1" +
"3,24,78,77:3,-1:23,81:2,-1:14,77,-1:7,83,-1:50,27,-1:30,1,28:16,31,28:3,16," +
"28:21,1,28:16,49,28:3,31,28:21,-1:4,100:3,41,100:9,36,100:3,-1:5,100,-1:4,1" +
"00:4,-1,100:2,-1:2,100,94,36,-1:8,58,-1:38,100:4,45,100:8,36,100:3,-1:5,100" +
",-1:4,100:4,-1,100:2,-1:2,100,94,36,-1:8,61,-1:38,100:4,48,100:8,36,100:3,-" +
"1:5,100,-1:4,100:4,-1,100:2,-1:2,100,94,36,-1:8,74,-1:38,100:13,36,100:3,-1" +
":5,100,-1:4,100:3,51,-1,100:2,-1:2,100,94,36,-1:4,100:12,88,36,100:3,-1:5,1" +
"00,-1:4,100:4,-1,100:2,-1:2,100,94,36,-1:4,100:13,36,100:3,-1:5,100,-1:4,97" +
",100:3,-1,100:2,-1:2,100,94,36,-1:4,100:8,98,100:4,36,100:3,-1:5,100,-1:4,1" +
"00:4,-1,100:2,-1:2,100,94,36,-1:4,100:13,36,100:3,-1:5,100,-1:4,100,99,100:" +
"2,-1,100:2,-1:2,100,94,36,-1:4,100:5,90,100:7,36,100:3,-1:5,100,-1:4,100:4," +
"-1,100:2,-1:2,100,94,36,-1:4,100:13,36,100:3,-1:5,100,-1:4,100:2,92,100,-1," +
"100:2,-1:2,100,94,36");

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
						{
    System.out.println("Illegal character: <" + yytext() + ">");	
}
					case -5:
						break;
					case 4:
						{ }
					case -6:
						break;
					case 5:
						{ }
					case -7:
						break;
					case 7:
						{ yybegin(COMMENT);  }
					case -8:
						break;
					case 8:
						{ yybegin (XML); }
					case -9:
						break;
					case 9:
						{ yybegin (ELEMENT); /*System.out.println ("Starting element");*/ return (new Symbol(sym.ELEMENT,yyline, yychar)); }
					case -10:
						break;
					case 10:
						{ yybegin (ATTLIST); /*System.out.println ("Starting attlist");*/ return (new Symbol(sym.ATTLIST,yyline, yychar)); }
					case -11:
						break;
					case 11:
						{ yybegin (DOCTYPE); }
					case -12:
						break;
					case 12:
						{ /*System.out.println ("Un-Matched:'" + yytext ()+ "'");*/ }
					case -13:
						break;
					case 13:
						{ }
					case -14:
						break;
					case 14:
						{ 
    yybegin(YYINITIAL);
}
					case -15:
						break;
					case 15:
						{
  //  System.out.println ("returning name -> " + yytext ());
    return (new Symbol(sym.NAME_SYMBOL,yyline,yychar,yytext()));
}
					case -16:
						break;
					case 16:
						{ yybegin (YYINITIAL); return (new Symbol(sym.RBRACK,yyline, yychar)); }
					case -17:
						break;
					case 17:
						{
    return (new Symbol(sym.LPAREN,yyline,yychar,yytext()));
}
					case -18:
						break;
					case 18:
						{
    return (new Symbol(sym.OR_BAR,yyline,yychar,yytext()));
}
					case -19:
						break;
					case 19:
						{
    return (new Symbol(sym.RPAREN,yyline,yychar,yytext()));
}
					case -20:
						break;
					case 20:
						{
    return (new Symbol(sym.COMMA,yyline,yychar,yytext()));
}
					case -21:
						break;
					case 21:
						{
    return (new Symbol(sym.ANY,yyline,yychar));
}
					case -22:
						break;
					case 22:
						{
    return (new Symbol(sym.EMPTY,yyline,yychar));
}
					case -23:
						break;
					case 23:
						{
    return (new Symbol(sym.PCDATA,yyline,yychar));
}
					case -24:
						break;
					case 24:
						{
    return (new Symbol(sym.QUOTED_NAME,yyline,yychar,yytext()));
}
					case -25:
						break;
					case 25:
						{
    return (new Symbol(sym.CDATA,yyline,yychar));
}
					case -26:
						break;
					case 26:
						{
    return (new Symbol(sym.SYNTAX,yyline,yychar));
}
					case -27:
						break;
					case 27:
						{
    return (new Symbol(sym.FIXED,yyline,yychar));
}
					case -28:
						break;
					case 28:
						{ }
					case -29:
						break;
					case 29:
						{ yybegin (YYINITIAL); }
					case -30:
						break;
					case 30:
						{
  //	System.out.println ("Text==" + yytext ());
    return (new Symbol(sym.TEXT,yyline,yychar,yytext()));
}
					case -31:
						break;
					case 31:
						{
    System.out.println("Illegal character: <" + yytext() + ">");	
}
					case -32:
						break;
					case 32:
						{ }
					case -33:
						break;
					case 33:
						{ }
					case -34:
						break;
					case 35:
						{ /*System.out.println ("Un-Matched:'" + yytext ()+ "'");*/ }
					case -35:
						break;
					case 36:
						{
  //  System.out.println ("returning name -> " + yytext ());
    return (new Symbol(sym.NAME_SYMBOL,yyline,yychar,yytext()));
}
					case -36:
						break;
					case 37:
						{
    return (new Symbol(sym.QUOTED_NAME,yyline,yychar,yytext()));
}
					case -37:
						break;
					case 38:
						{
  //	System.out.println ("Text==" + yytext ());
    return (new Symbol(sym.TEXT,yyline,yychar,yytext()));
}
					case -38:
						break;
					case 39:
						{
    System.out.println("Illegal character: <" + yytext() + ">");	
}
					case -39:
						break;
					case 41:
						{
  //  System.out.println ("returning name -> " + yytext ());
    return (new Symbol(sym.NAME_SYMBOL,yyline,yychar,yytext()));
}
					case -40:
						break;
					case 42:
						{
  //	System.out.println ("Text==" + yytext ());
    return (new Symbol(sym.TEXT,yyline,yychar,yytext()));
}
					case -41:
						break;
					case 43:
						{
    System.out.println("Illegal character: <" + yytext() + ">");	
}
					case -42:
						break;
					case 45:
						{
  //  System.out.println ("returning name -> " + yytext ());
    return (new Symbol(sym.NAME_SYMBOL,yyline,yychar,yytext()));
}
					case -43:
						break;
					case 46:
						{
    System.out.println("Illegal character: <" + yytext() + ">");	
}
					case -44:
						break;
					case 48:
						{
  //  System.out.println ("returning name -> " + yytext ());
    return (new Symbol(sym.NAME_SYMBOL,yyline,yychar,yytext()));
}
					case -45:
						break;
					case 49:
						{
    System.out.println("Illegal character: <" + yytext() + ">");	
}
					case -46:
						break;
					case 51:
						{
  //  System.out.println ("returning name -> " + yytext ());
    return (new Symbol(sym.NAME_SYMBOL,yyline,yychar,yytext()));
}
					case -47:
						break;
					case 86:
						{
  //  System.out.println ("returning name -> " + yytext ());
    return (new Symbol(sym.NAME_SYMBOL,yyline,yychar,yytext()));
}
					case -48:
						break;
					case 88:
						{
  //  System.out.println ("returning name -> " + yytext ());
    return (new Symbol(sym.NAME_SYMBOL,yyline,yychar,yytext()));
}
					case -49:
						break;
					case 90:
						{
  //  System.out.println ("returning name -> " + yytext ());
    return (new Symbol(sym.NAME_SYMBOL,yyline,yychar,yytext()));
}
					case -50:
						break;
					case 92:
						{
  //  System.out.println ("returning name -> " + yytext ());
    return (new Symbol(sym.NAME_SYMBOL,yyline,yychar,yytext()));
}
					case -51:
						break;
					case 93:
						{
  //  System.out.println ("returning name -> " + yytext ());
    return (new Symbol(sym.NAME_SYMBOL,yyline,yychar,yytext()));
}
					case -52:
						break;
					case 94:
						{
  //  System.out.println ("returning name -> " + yytext ());
    return (new Symbol(sym.NAME_SYMBOL,yyline,yychar,yytext()));
}
					case -53:
						break;
					case 95:
						{
  //  System.out.println ("returning name -> " + yytext ());
    return (new Symbol(sym.NAME_SYMBOL,yyline,yychar,yytext()));
}
					case -54:
						break;
					case 96:
						{
  //  System.out.println ("returning name -> " + yytext ());
    return (new Symbol(sym.NAME_SYMBOL,yyline,yychar,yytext()));
}
					case -55:
						break;
					case 97:
						{
  //  System.out.println ("returning name -> " + yytext ());
    return (new Symbol(sym.NAME_SYMBOL,yyline,yychar,yytext()));
}
					case -56:
						break;
					case 98:
						{
  //  System.out.println ("returning name -> " + yytext ());
    return (new Symbol(sym.NAME_SYMBOL,yyline,yychar,yytext()));
}
					case -57:
						break;
					case 99:
						{
  //  System.out.println ("returning name -> " + yytext ());
    return (new Symbol(sym.NAME_SYMBOL,yyline,yychar,yytext()));
}
					case -58:
						break;
					case 100:
						{
  //  System.out.println ("returning name -> " + yytext ());
    return (new Symbol(sym.NAME_SYMBOL,yyline,yychar,yytext()));
}
					case -59:
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
