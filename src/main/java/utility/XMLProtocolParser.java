package utility;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import utility.annotations.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

/**
 * This class is currently only for testing purposes. At the end there will be one class for parsing.
 * I don't want to get in Julian's way with the Stammdaten-Parser.
 *
 * @author Andrej
 */
 @Testing
 @Unfinished
public class XMLProtocolParser {
    //Create New Instance of DocumentBuilderFactory
    static DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

    public static void speechParse2() {
        try {

            //Parsing all XMLs-protocols from last to first one
            DocumentBuilder db = dbf.newDocumentBuilder();
            //access to our downloaded protocol-files
            File[] files = new File("ProtokollXMLs/").listFiles();

            for (int i = files.length - 1; i >= 0; i--) {
                File file = files[i];
                //test if the file is a xml-file
                if (file.isFile() && file.getName().matches(".*\\.xml")) {
                    //Show Progress in console (we won't need later)
                    System.out.println("The file " + file.getName() + " is being edited...");
                    System.out.println("\tSpeeches are collected right now...");

                    //Go through the sessions
                    Document xmlDoc = db.parse("ProtokollXMLs/" + file.getName());
                    //get the root element of the protocol --> (dbtplenarprotokoll)
                    //System.out.println("Root Element: " + xmlDoc.getDocumentElement().getNodeName());





                }
            }

        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }

    }
}
