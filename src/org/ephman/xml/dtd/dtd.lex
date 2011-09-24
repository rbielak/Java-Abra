package org.ephman.xml.dtd;

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


    Yylex (String fileName) throws java.io.IOException {
	this ();
	fr = new java.io.FileReader (fileName);
	yy_reader = new java.io.BufferedReader(fr);
    }

%}

%line
%char
%state COMMENT
%state ELEMENT
%state ATTLIST
%state DOCTYPE
%state XML
%eofval{
    return new Symbol(sym.EOF);
%eofval}


%public
%cup

ALPHA=[A-Za-z_-]
DIGIT=[0-9]
DOT=[.]
REGEX_SYMBOL=[?+*]
SLASH_STAR="/*"
QUOTE="\""
XML_HEADER=[^?>]+
NAME={ALPHA}({ALPHA}|{DIGIT})*
NAME_SYMBOL={ALPHA}({ALPHA}|{DIGIT})*{REGEX_SYMBOL}?
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
<YYINITIAL> "/" { return (new Symbol(sym.SLASH,yyline, yychar)); }
<YYINITIAL> "<!ELEMENT" { yybegin (ELEMENT); /*System.out.println ("Starting element");*/ return (new Symbol(sym.ELEMENT,yyline, yychar)); }
<YYINITIAL> "<!ATTLIST" { yybegin (ATTLIST); /*System.out.println ("Starting attlist");*/ return (new Symbol(sym.ATTLIST,yyline, yychar)); }
<YYINITIAL> "<!DOCTYPE" { yybegin (DOCTYPE); }
<YYINITIAL> "<?xml" { yybegin (XML); }
<XML> "?>" { yybegin (YYINITIAL); }
<DOCTYPE, ELEMENT, ATTLIST> ">" { yybegin (YYINITIAL); return (new Symbol(sym.RBRACK,yyline, yychar)); }
<XML, DOCTYPE> {XML_HEADER} { }


<YYINITIAL, ELEMENT, ATTLIST> {NONNEWLINE_WHITE_SPACE_CHAR}+ { }

 \n { }
 \r { }

<YYINITIAL> "<!--" { yybegin(COMMENT);  }

<COMMENT> "-->" { 
    yybegin(YYINITIAL);
}


<COMMENT> . { /*System.out.println ("Un-Matched:'" + yytext ()+ "'");*/ }



<ATTLIST, ELEMENT> "(" {
    return (new Symbol(sym.LPAREN,yyline,yychar,yytext()));
}
<ATTLIST, ELEMENT> "|" {
    return (new Symbol(sym.OR_BAR,yyline,yychar,yytext()));
}
<ATTLIST, ELEMENT> ")" {
    return (new Symbol(sym.RPAREN,yyline,yychar,yytext()));
}
<ATTLIST, ELEMENT> "," {
    return (new Symbol(sym.COMMA,yyline,yychar,yytext()));
}
<ATTLIST> "Syntax" {
    return (new Symbol(sym.SYNTAX,yyline,yychar));
}
<ATTLIST> "CDATA" {
    return (new Symbol(sym.CDATA,yyline,yychar));
}
<ATTLIST> "#FIXED" {
    return (new Symbol(sym.FIXED,yyline,yychar));
}
<ATTLIST> {QUOTED_NAME} {
    return (new Symbol(sym.QUOTED_NAME,yyline,yychar,yytext()));
}
<ELEMENT> "#PCDATA" {
    return (new Symbol(sym.PCDATA,yyline,yychar));
}
<ELEMENT> "ANY" {
    return (new Symbol(sym.ANY,yyline,yychar));
}
<ELEMENT> "EMPTY" {
    return (new Symbol(sym.EMPTY,yyline,yychar));
}

<ATTLIST, ELEMENT> {NAME_SYMBOL} {
  //  System.out.println ("returning name -> " + yytext ());
    return (new Symbol(sym.NAME_SYMBOL,yyline,yychar,yytext()));
}
<ATTLIST, ELEMENT> {NAME} {
  //  System.out.println ("Name==" + yytext ());
    return (new Symbol(sym.NAME,yyline,yychar,yytext()));
}

<YYINITIAL> {TEXT} {
  //	System.out.println ("Text==" + yytext ());
    return (new Symbol(sym.TEXT,yyline,yychar,yytext()));
}	 
. {
    System.out.println("Illegal character: <" + yytext() + ">");	
}
