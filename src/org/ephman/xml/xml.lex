package org.ephman.xml;

import java_cup.runtime.*;
import java.io.FileReader;
import java.util.HashMap;

%%

%{

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

%}

%line
%char
%state COMMENT
%state INNODE
%state DOCTYPE
%state XML
%eofval{
    return new Symbol(sym.EOF);
%eofval}

%implements com.omg.tools.Constants
%public
%cup

ALPHA=[A-Za-z_-]
DIGIT=[0-9]
DOT=[.]
SLASH_STAR="/*"
QUOTE="\""
XML_HEADER=[^?>]+
NAME={ALPHA}({ALPHA}|{DIGIT})*
LBRACK_NAME="<"{ALPHA}({ALPHA}|{DIGIT})*
LBRACK_SLASH_NAME="</"{ALPHA}({ALPHA}|{DIGIT})*
NUMBER={DIGIT}*{DOT}?{DIGIT}+
INTEGER={DIGIT}+
NONNEWLINE_WHITE_SPACE_CHAR=[\ \t\b\012]
WHITE_SPACE_CHAR=[\n\ \t\b\012]
STRING_TEXT=(\\\"|[^\n<>\"]|\\{WHITE_SPACE_CHAR}+\\)*
QUOTED_NAME={QUOTE}({STRING_TEXT}|[<>])*{QUOTE}
TEXT=({STRING_TEXT})*

%% 
<YYINITIAL, INNODE> "/" { return (new Symbol(sym.SLASH,yyline, yychar)); }
<YYINITIAL> "<" { yybegin (INNODE); return (new Symbol(sym.LBRACK,yyline, yychar)); }
<YYINITIAL> "<!DOCTYPE" { yybegin (DOCTYPE); }
<YYINITIAL> "<?xml" { yybegin (XML); }
<XML> "?>" { yybegin (YYINITIAL); }
<DOCTYPE> ">" { yybegin (YYINITIAL); }
<XML, DOCTYPE> {XML_HEADER} { }
<INNODE> "<" { yybegin (INNODE); return (new Symbol(sym.UNCLOSED,yyline, yychar)); }
<YYINITIAL, INNODE> ">" { yybegin (YYINITIAL); return (new Symbol(sym.RBRACK,yyline, yychar)); }
<YYINITIAL, INNODE> "=" { return (new Symbol(sym.EQUALS,yyline, yychar)); }

<YYINITIAL, INNODE> {NONNEWLINE_WHITE_SPACE_CHAR}+ { }

 \n { }
 \r { }

<YYINITIAL> "<!--" { yybegin(COMMENT);  }

<COMMENT> "-->" { 
    yybegin(YYINITIAL);
}


<COMMENT> . { /*System.out.println ("Un-Matched:'" + yytext ()+ "'");*/ }

<INNODE> {QUOTED_NAME} {
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
<INNODE> {NAME} {
    return (new Symbol(sym.NAME,yyline,yychar,yytext()));
}
<YYINITIAL, INNODE> {LBRACK_NAME} {
	yybegin (INNODE);	
    return (new Symbol(sym.LBRACK_NAME,yyline,yychar,yytext().substring (1)));
}
<YYINITIAL> {LBRACK_SLASH_NAME} {
	//	yybegin (INNODE);
    return (new Symbol(sym.LBRACK_SLASH_NAME,yyline,yychar,yytext().substring (2)));
}
<YYINITIAL> {TEXT} {
	//	System.out.println ("Text==" + yytext ());
    return (new Symbol(sym.TEXT,yyline,yychar,yytext()));
}	 
. {
    System.out.println("Illegal character: <" + yytext() + ">");	
}
