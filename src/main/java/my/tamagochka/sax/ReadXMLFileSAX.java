package my.tamagochka.sax;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;

public class ReadXMLFileSAX {

    public static Object read(String xmlFile) {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            SAXHandler handler = new SAXHandler();
            parser.parse(xmlFile, handler);
            return handler.getObject();
        } catch(ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
