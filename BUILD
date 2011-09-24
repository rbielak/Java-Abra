NOTES ON BUILDING:

You can build a library with Oracle, PostgreSQL, MySQL, db2-UDB. 

You also have to install the Apache "ant" tool (http://jakarta.apache.org/ant).
you also have to download the Apache "regexp" libraries (http://jakarta.apache.org/regexp),
and a JAX/SAX XML parser.

Before building you will have to assign the following environment variables:

	ABRA_HOME 	- this is the directory that contains the Abra distribution
	JDBC_JAR	- the path to the Jar file containing the appropriate JDBC 
		library
	XML_JAR		- the path to the Jar file containing the XML parser
	REGEXP_JAR  - the path containing the regexp 'jar' file.

Ant will complain if one of these is not set.

To build with Postgres do:

	cd <wherever the top directory for Abra is> (ie ~paulb/Abra-0.9)
	ant postgresql

or in general 
	ant <db-name>    (where db-name=postgresql, oracle, db2-UDB, mysql)

The jar files will be in $ABRA_HOME/lib.


OPTIONAL:
	To build the abra xml compiler and/or dtd converter you need
JavaCUP which can be found at
http://www.cs.princeton.edu/~appel/modern/java/CUP/ and Java Lexer at
http://www.cs.princeton.edu/~appel/modern/java/JLex  
	You must compile both and put in a jar file.
	Then set JCUP_JAR=<path-to the jar>

	or if you want a pre-built jar which will include both CUP and
JLex go and download from my page at
http://wso.williams.edu/~pbethe/java_cup.jar
	
	Either way you then set JCUP_JAR=<path-to the jar>
	and can run 'ant compile-xml' and 'ant compile-dtd'

	


