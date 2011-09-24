package org.ephman.junit;

import junit.framework.TestCase;
import org.ephman.abra.database.*;
import org.ephman.junit.generated.*;
import org.ephman.xml.*;

import java.sql.*;
import java.io.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.net.URL;
import java.net.JarURLConnection;

/** a test to try some view operations
 * @author Paul Bethe
 */

public class TestPerf extends DatabaseTest {

    public TestPerf (String name) {
        super (name);
    }

    int TESTS = 1000;
    String jarAndEntry = "test-jars.jar!/test.xml";

    public void donttestJar () throws Exception {
        String name = System.getProperty("user.dir");
        if (!name.endsWith("/"))
            name += "/";
        if (name.indexOf ("junit") == -1) { // running from Abra-0.9 dir
              name += "src/org/ephman/junit/";
        }
        URL u = new URL ("jar:file://" + name + jarAndEntry);
        JarURLConnection jconn = (JarURLConnection)u.openConnection();
        JarEntry je = (JarEntry)jconn.getJarEntry();
        InputStream is = jconn.getJarFile().getInputStream(je);
        Reader r = new InputStreamReader(is);
        marshaller.mapXMLFile(r);
    }

    public void testMarshalSpeed () throws Exception {
        //marshaller.useAbraXmlParser();
        System.out.println (testString);
        tryMarshalSpeed ("Marshaller (using reflection)", new BookTester(){
            public Book execute(String xml) throws Exception {
                return (Book)marshaller.unmarshal(xml);
            }
        });

        tryMarshalSpeed ("custom method.. ", new BookTester(){
            public Book execute(String xml) throws Exception {
                return (Book)buildBook(xml);
            }
        });
    }

    public void tryMarshalSpeed (String method, BookTester tester) throws Exception {
        long marshallerStartTime = System.currentTimeMillis();
        long marshallerEndTime = 0;
        String foo = "";

        for (int i=0; i < TESTS; i++) {
            Book b = tester.execute (testString);
            foo += b.toString();
        }
        marshallerEndTime = System.currentTimeMillis();
        long tDiff = marshallerEndTime - marshallerStartTime;
        double diff = (double)1000 * TESTS / (double)(tDiff);

        System.out.println ("Method="+method+"\nStart="+new Timestamp (marshallerStartTime)+
                "\nFinish="+new Timestamp(marshallerEndTime)+"\nmarshalls/second="+
                " was "+ diff);

    }



    public Book buildBook (String xmlText) throws Exception {
        XmlParser parser = new XercesXmlParser(new StringReader(xmlText), false);
        XmlNode xn = (XmlNode)parser.parseXml();
        assertTrue ("have xml node", xn != null);

        assertTrue ("top is book", "Book".equals (xn.getName()));
        Book result = new Book ();
        result.setAuthor(buildAuthor ((XmlNode)xn.getChildNodes("author").elementAt(0)));
        result.setText(((XmlNode)xn.getChildNodes("text").elementAt(0)).getText());
        return result;
    }

    public Author buildAuthor (XmlNode xn) throws Exception {
        Author result = new Author ();
        result.setFirstName(xn.getAttribute("firstName"));
        result.setLastName(xn.getAttribute("lastName"));
        result.setBirthday(new Timestamp(Long.parseLong (xn.getAttribute("birthday"))));
        result.setHomeAddress(buildAddress ((XmlNode)xn.getChildNodes("homeAddress").elementAt(0)));
        result.setWorkAddress(buildAddress ((XmlNode)xn.getChildNodes("workAddress").elementAt(0)));
        return result;
    }

    public Address buildAddress (XmlNode xn) throws Exception {
        Address result = new Address ();
        result.setState(xn.getAttribute("street"));
        result.setCity(xn.getAttribute("city"));
        result.setState(xn.getAttribute("state"));
        return result;
    }

    String testString = "<Book ID=\"5\" title=\"The source of all magic\" Isbn=\"425532\">"
            + "\t<author ID=\"1\" firstName=\"Douglas\" lastName=\"Adams\" birthday=\"42\">\n" +
            "\t\t<homeAddress street=\"The restaurant\" city=\"The End\" state=\"UN\" />\n" +
            "\t\t<workAddress street=\"somewhere\" city=\"who knows\" state=\"NY\" />\n"
            + "\t</author>\n\t<text>\n" +
            "public class LargeObjectManager extends Object" +"\n\n\tjava.lang.Object"
            + "</text>\n</Book>\n";


    abstract class BookTester {
        public abstract Book execute (String xml) throws Exception ;
    }
}
