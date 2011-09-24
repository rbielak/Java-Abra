package org.ephman.xml.dtd;

/** interface to wrap dtd fields
 * should give name of field and type/validation (id string 4c!)
 */
import java.util.*;
import java.io.*;
import org.ephman.abra.tools.*;
import org.ephman.xml.*;

public class DtdConverter {

	static void printUsage () {
		System.out.println ("DtdConverter <props-file> [<dtd-file>*] ");
	}

	static String packageName = "foo.bar";

	final static String DTD_NAME = "dtdName";
	final static String ENTRY_POINT = "entryPoint";
	final static String MAP_FILES = "mapFiles";

	final static String OUTPUT_DIR = "outDir";
	final static String DATE_FORMATS = "dateFormats";
	final static String PACKAGE = "package";
	final static String EXTENDS_CLASS = "extends";

	static String extendsClass = null;

	static PrintStream outStream = System.out;
	static HashSet dateFormatStrings = new HashSet ();;

	public static void main (String []argv) {
		if (argv.length < 3) {
			printUsage ();
			System.exit (0);
		}
		try {
			String propsName = argv[0];
			Properties props = new Properties ();
			props.load (new FileInputStream (propsName));
			
			//			String dtdName = argv[1];
			//			String topName = argv[2];
			packageName = props.getProperty (PACKAGE, packageName); // default to foo.bar
			String dateFormatText = props.getProperty (DATE_FORMATS, "");
			if (dateFormatText.length () > 0) {
				Vector v = org.ephman.utils.Tokenizer.tokenize (dateFormatText, ",", "\"");
				for (int i=0; i< v.size (); i++) 
					dateFormatStrings.add ("\"" + (String)v.elementAt (i) + "\"");
			}
			
			String outDirName = props.getProperty (OUTPUT_DIR, "");
						
			extendsClass = props.getProperty (EXTENDS_CLASS);
			
			DtdConverter inst = new DtdConverter ();
			
			for (int i=1; i < argv.length ; i++) {
				String dtdName = argv[i];
				String topName = dtdName.substring (0, dtdName.indexOf ("."));
				int index = topName.lastIndexOf ("/");
				if (index != -1)
					topName = topName.substring (index +1);

				if (outDirName.length () > 0) { // use file else sys.out
					if (!outDirName.endsWith ("/")) outDirName += "/";
					String outFileName = outDirName + topName + ".xml";
					outStream = new PrintStream (new FileOutputStream (outFileName));
				}
				inst.initClassTree (props.getProperty (MAP_FILES), argv);
				System.out.println ("Processing " + topName);
				inst.processDtd (dtdName, topName);
			}
		} catch (Exception e) {
			e.printStackTrace ();
		}	   
	}

	HashMap classTree = new HashMap ();
	HashMap hashedClasses = new HashMap ();

	void initClassTree (String fileList, String [] argv) throws Exception {		
		if (fileList != null && !fileList.trim ().equals ("")) {
			StringTokenizer tokens = new StringTokenizer (fileList, ",");			
			FakeMTJ mtj = new FakeMTJ ();
			while (tokens.hasMoreTokens ()) {
				String mapFileName = tokens.nextToken ();
				mtj.applyRules(mtj.mapXMLFile(mapFileName, false), mapFileName);
			}
			/*			for (int i=3; i < argv.length; i++) {
						String mapFileName = argv[i];
						System.out.println ("Adding file::"+mapFileName );
						mtj.applyRules(mtj.mapXMLFile(mapFileName, false), mapFileName);
						}*/
			classTree = mtj.getClassTree();
			Iterator it = classTree.values().iterator (); // for lookups..
			while (it.hasNext()) {
                JClass jc = (JClass)it.next();
				hashedClasses.put (getTypedHashKey (jc), jc);
			}
		}
	}

		// take the dtd parse and compare to class list produce xml-map-file
	void processDtd (String dtdName, String topName) throws Exception {
		//take Dtd object parse -> take an element and create JClass for it
		// -> check members/types and see if already have a type in old or new map ..
		//for PCDATA fields create JField nodes in currentClass.
		dtd = new DtdParser (new FileReader (dtdName)).parseDtd ();
		dtd.analyze (topName);  // simple hashing ..
		//		String topName = dtd.firstElement.name;
		newClasses = new HashMap ();
		JClass topClass = processJClass (topName);		
		//		System.out.println (topClass.toFileString ());
		Iterator it = newClasses.values().iterator ();
		outStream.println ("<mapping>\n");
		outStream.println ("<property name=\"gen.pkg\" value=\""
							+ packageName + "\"/>");
		while (it.hasNext ())
			outStream.println (((JClass)it.next ()).toFileString ());
		outStream.println ("\n</mapping>\n");
		outStream.close ();
		
	}

	HashMap newClasses;

	JClass processJClass (String elementName) throws Exception {
		DtdElement element = dtd.getElement (elementName);
		JClass jc = new JClass (elementName, "${gen.pkg}");
		if (extendsClass != null) jc.setParentClassName (extendsClass);
		//		Vector simpleEls = new Vector ();
		for (int i=0; i<element.elements.size (); i++) {
			String subElementName = (String)element.elements.elementAt (i);
			//			System.out.println (subElementName);
			DtdElement subElement = dtd.getElement (getName (subElementName));
			JField jf = null;
			if (subElement.isPCData) { // simple type
				jf = new JField ();
				setJavaType (subElement, jf);
			} else { // composite type..
				JCompositeField jcf = new JCompositeField ();
				jf = jcf;
				JClass sub_jc = (JClass)newClasses.get (getName (subElementName));
				jcf.setJClass (sub_jc == null ? processJClass (getName (subElementName))
							   : sub_jc);
				jcf.setDkType (TypeMapper.getType (jcf.getJClass().getPackageName () + "."+
								 jcf.getJClass().getClassName ()));
				jcf.setMapFileType (jcf.getJClass().getPackageName () + "."+
								 jcf.getJClass().getClassName ());
			}
			setGrouping (subElementName, jf);
			jf.setJavaName (getName (subElementName));
			jc.addField (jf);
		}
		JClass preMadeClass = (JClass)hashedClasses.get (getTypedHashKey (jc));
		if (preMadeClass != null) return preMadeClass;
		hashedClasses.put (getTypedHashKey (jc), jc);
		newClasses.put (jc.getClassName (), jc);
		return jc;
	}


	void setGrouping (String n, JField jf) {
		if (n.endsWith ("?")) { }			
		else if (n.endsWith ("*") || n.endsWith ("+")) 
			jf.setToVector ();
		else
			jf.setRequired ();
	}
	// name
	private String getName (String n) {
		String name;
		if (n.endsWith ("?") ||n.endsWith ("*") || n.endsWith ("+")) 
			name = n.substring (0, n.length()-1);
		else
			name = n;			
		return name;
	}


	void setJavaType (DtdElement el, JField jf) {
		DtdAttList attList = dtd.getAttList (el.name);
		String syn = attList == null ? "c"
			: attList.charSyntax;
		String jType = "String";
		String mType = "string";
		if (syn.indexOf ("c") != -1 || syn.indexOf ("z") != -1 || syn.indexOf ("a") != -1 ||
			syn.indexOf ("a") != -1 || syn.indexOf ("s") != -1 ) // string .. nothing
			{ }
		else if (syn.indexOf ("n") != -1)	{ // number or date
			
			//			System.out.println (syn + " != " + test);

			if (dateFormatStrings.contains (syn)) {
				jf.setAsDate ();
				mType = "date";
				jType = "Timestamp";
			}
			else {
				mType = "integer";
				jType = "int";
			}
		}
		else if (syn.indexOf ("d") != -1 )	{ // double
			jType = "double";
			mType = "double";
		}
		jf.setMapFileType (mType);
		jf.setDkType (TypeMapper.getType (jType));
	}

	Dtd dtd = null;
	/** method to take a type like Amount with fields sign:String;curr:String;value:double 
	 * and create hash index from that..
	 */
	String getTypedHashKey (JClass jc) {
		// use Tree Map -- ordered by name to
		// create String key..
		Iterator it = jc.getAllFields ().values ().iterator ();
		StringBuffer sb = new StringBuffer (EMPTY_STRING);
		while (it.hasNext ()) {
			JField jf = (JField)it.next ();
			String vecS = jf.isVector () ? "]V" : "";
			sb.append (jf.getJavaName () +":"+jf.getObjectType()+ vecS +
					   ";");
		}
		//		System.out.println ("Created-HK>>"+sb.toString ());
		return sb.toString ();
	}

	static final String EMPTY_STRING = "";

	class FakeMTJ extends MapToJava {
		protected void applyRules (XmlNode xn, String fileName) 
			throws XmlException, IOException, SchemaException
		{
			super.applyRules (xn, fileName);
		}
		public XmlNode mapXMLFile (String name) throws XmlException, FileNotFoundException {
			return super.mapXMLFile (name);
		}
		public HashMap getClassTree () {
			return super.getClassTree ();
		}
		FakeMTJ () {
			super ("", false, false, false, false, false);
		}
		
	}
}
  
