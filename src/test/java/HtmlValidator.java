package project3test;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.StringReader;

/**
 * Helper class with method to determine if an HTML document is well formed.
 * source: https://github.com/CS601-F21/code-examples/blob/main/Web/src/main/java/util/HtmlValidator.java
 */
public class HtmlValidator {

    /**
     * Returns true if the html is valid and false otherwise.
     * Taken from Wellformed.java example from
     * http://www.edankert.com/validate.html#Sample_Code
     * @param html
     * @return
     */
    public static boolean isValid(String html) {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(false);
            factory.setNamespaceAware(true);
            SAXParser parser = factory.newSAXParser();
            XMLReader reader = parser.getXMLReader();
            reader.parse(new InputSource(new StringReader(html)));
            return true;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}