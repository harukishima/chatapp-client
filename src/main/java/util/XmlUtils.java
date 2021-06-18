package util;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import model.ListServerInfo;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.print.Doc;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class XmlUtils {
    public static boolean writeToFile(String filepath, ListServerInfo listServer) {
        JAXBContext jaxbContext = null;
        try {
            jaxbContext = JAXBContextFactory.createContext(new Class[] {ListServerInfo.class}, null);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(listServer, new File(filepath));

        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return jaxbContext != null;
    }

    public static ListServerInfo readFromFile(String filepath) {
        ListServerInfo listServerInfo = null;
        try {
            JAXBContext jaxbContext = JAXBContextFactory.createContext(new Class[] {ListServerInfo.class}, null);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            listServerInfo = (ListServerInfo) unmarshaller.unmarshal(new File(filepath));
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return listServerInfo;
    }

    public static Document stringXmlToDocument(String xml) {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        Document document = null;
        try {
            builderFactory.getFeature(XMLConstants.FEATURE_SECURE_PROCESSING);
            DocumentBuilder db = builderFactory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(xml));
            document = db.parse(is);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return document;
    }

    public static List<String> documentToListUser(Document document) {
        List<String> userList = new ArrayList<>();
        if (document == null) {
            return userList;
        }
        document.getDocumentElement().normalize();
        NodeList nodeList = document.getElementsByTagName("client");
        for(int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                String username = element.getElementsByTagName("username").item(0).getTextContent();
                userList.add(username);
            }
        }
        return userList;
    }
}
