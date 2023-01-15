package utility;

import data.impl.Speech_Impl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class is currently only for testing purposes. At the end there will be one class for parsing.
 * I don't want to get in Julians way with the Stammdaten-Parser.
 *
 * @author Andrej
 * @Testing
 * @Unfinished
 */

public class XMLProtocolParser {

    private Map<String, Speech_Impl> speechMap;
    //Create New Instance of DocumentBuilderFactory
    static DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

    public static void speechParse2() {

        /*

         //Create connection to mongoDB
         MongoDBHandler mongoDBHandler = null;
         try {
         mongoDBHandler = new MongoDBHandler();
         } catch (IOException e) {
         e.printStackTrace();
         }
         */


        try {

            //Parsing all XMLs-protocols from last to first one
            DocumentBuilder db = dbf.newDocumentBuilder();
            //access to our downloaded protocol-files
            File[] files = new File("ProtokollXMLs/").listFiles();

            // iterating over all cml-protocols
            for (int i = files.length - 1; i >= 0; i--) {
                File file = files[i];
                //check if the file is a xml-file
                if (file.isFile() && file.getName().matches(".*\\.xml")) {
                    //Show Progress in console (we won't need later)
                    System.out.println(" The file " + file.getName() + " is being edited...");
                    System.out.println("\tSpeeches are collected right now...");

                    //Go through the protocol-sessions
                    Document xmlDoc = db.parse("ProtokollXMLs/" + file.getName());

                    // orientation for the Dom-Parser: https://mkyong.com/java/how-to-read-xml-file-in-java-dom-parser/
                    //get the root element of the protocol --> (dbtplenarprotokoll)
                    NodeList sessionInfoNodes = xmlDoc.getElementsByTagName("dbtplenarprotokoll");
                    //iterate threw all dbtplenarptotokoll nodes
                    for (int a = 0; a < sessionInfoNodes.getLength(); a++) {
                        Node sessionInfoNode = sessionInfoNodes.item(a);
                        if (sessionInfoNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element sessionInfoElement = (Element) sessionInfoNode;

                            int protocolNumber = Integer.parseInt(sessionInfoElement.getAttribute("sitzung-nr"));
                            int electionPeriod = Integer.parseInt(sessionInfoElement.getAttribute("wahlperiode"));
                            String sessionDate = (sessionInfoElement.getAttribute("sitzung-datum"));
                            TimeHelper.convertToISOdate(sessionDate);

                            //Iterate through all Tagesordnungspunkte
                            List<Element> aiElementList = getElementList(sessionInfoElement, "tagesordnungspunkt");
                            for (Element aiElement : aiElementList) {
                                String topid = aiElement.getAttribute("top-id");
                                System.out.println(topid);

                                //Go through all Speeches
                                List<Element> speechElementList = getElementList(aiElement, "rede");
                                for (Element speech : speechElementList) {

                                    String speechID = speech.getAttribute("id");
                                    String speakerID = "";
                                    String speechText = "";
                                    List<String> commentList = new ArrayList<>();
                                    boolean addStatus = false;

                                    //Go through all ChildNodes of a speech
                                    List<Element> speechChildNodeList = getChildElementList(speech);
                                    for (Element speechChild : speechChildNodeList) {
                                        switch (speechChild.getTagName()) {
                                            //If its a <name>-Tag -> So its not a speaker (set addStatus = false) --> The whole text up to this will be added if the tag before was a <p klasse="redner">-Tag (addStatus == true)
                                            case "name":
                                                speakerID = "";
                                                speechText = "";
                                                commentList.clear();
                                                addStatus = false;
                                                break;

                                            //If its a <p klasse="redner">-Tag -> So its a speaker (set addStatus = true) --> The whole text up to this will be added if the tag before was a <p klasse="redner">-Tag (addStatus == true)
                                            case "p":
                                                if (speechChild.getAttribute("klasse").equals("redner")) {
                                                    addStatus = true;
                                                    NodeList speakerNodeList = speechChild.getChildNodes();
                                                    for (int e = 0; e < speakerNodeList.getLength(); e++) {
                                                        Node speakerNode = speakerNodeList.item(e);
                                                        if (speakerNode.getNodeType() == Node.ELEMENT_NODE) {
                                                            Element speaker = (Element) speakerNode;
                                                            if (speaker.getTagName().equals("redner")) {
                                                                speakerID = speaker.getAttribute("id");
                                                            }
                                                        }
                                                    }
                                                    speechText = "";
                                                    commentList.clear();
                                                } else {
                                                    if (speechText.equals("")) {
                                                        speechText = speechText + "" + speechChild.getTextContent();
                                                    } else {
                                                        speechText = speechText + " " + speechChild.getTextContent();
                                                    }
                                                }
                                                break;
                                        }
                                    }
                                    //At the end of a xml speech: The whole text up to this will be added if the tag before was a <p klasse="redner">-Tag (addStatus == true)
                                    addToSpeechMap(speechID, speakerID, speechText, addStatus, topid);
                                }
                            }
                        }
                    }
                }
            }
        } catch (ParserConfigurationException | IOException | SAXException ex) {
            ex.printStackTrace();
        }
    }


    /**
     * A helper method to extract all Elements of a given parent node from the XML Document which is parsed.
     * It recursively scans the document for all occurrences. It is a modified version of a function by
     * G.Abrami.
     *
     * @param parentElement The parent node from which the function starts to recursively scan the xml tree
     * @param tagName       The tag of the Nodes that will be extracted from the Sub-Tree
     * @return List<Element>  A List of the Nodes (Elements) with the given name
     * @author Andrej Artuschenko
     * @author DavidJordan
     */
    private static List<Element> getElementList(Element parentElement, String tagName) {
        // list to store the selected nodes
        NodeList nodeList = parentElement.getElementsByTagName(tagName);
        List<Element> elementList = new ArrayList<>();
        for (int a = 0; a < nodeList.getLength(); a++) {
            Node node = nodeList.item(a);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                elementList.add(element);
            }
        }
        return elementList;
    }

    /**
     * Get the Child Elementlist of a parentElement (Same logic as in the methof getElementList)
     *
     * @param parentNode
     * @return List<Element>
     * @author Andrej Artuschenko
     */
    private static List<Element> getChildElementList(Element parentNode) {
        NodeList nodeList = parentNode.getChildNodes();
        List<Element> elementList = new ArrayList<>();
        for (int a = 0; a < nodeList.getLength(); a++) {
            Node node = nodeList.item(a);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                elementList.add(element);
            }
        }
        return elementList;
    }

    public static int addToSpeechMap(String speechID, String speakerID, String speechText, boolean addStatus, String topid,){
        new Speech_Impl(speechID, speakerID, speechText, topid);
        return 0;
    }

}
