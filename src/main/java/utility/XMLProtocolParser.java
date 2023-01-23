package utility;

import data.impl.*;
import org.apache.uima.UIMAException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import utility.annotations.Testing;
import utility.annotations.Unfinished;
import utility.uima.ProcessedSpeech;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

/**
 * This class is currently only for testing purposes. At the end there will be one class for parsing.
 * I don't want to get in Julians way with the Stammdaten-Parser.
 *
 * @author Andrej
 */
@Unfinished("Some reason")
public class XMLProtocolParser {

    private static Map<String, Speech_Impl> speechMap;
    private static Map<String, Comment_Impl> commentMap;

    //Create New Instance of DocumentBuilderFactory
    private static DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

    public static void speechParse() throws FileNotFoundException, UIMAException {

        speechMap = new HashMap<>();
        commentMap = new HashMap<>();

        //Reset Maps
        //Create connection to mongoDB
        MongoDBHandler mongoDBHandler = null;
        try {
            mongoDBHandler = new MongoDBHandler();
        } catch (IOException e) {
            e.printStackTrace();
        }
        UIMAPerformer uima = new UIMAPerformer();

        try {
            ArrayList<Person_Impl> persons = mongoDBHandler.getPersons();

            //Parsing all XMLs-protocols from last to first one
            DocumentBuilder db = dbf.newDocumentBuilder();
            //access to our downloaded protocol-files
            String path = XMLProtocolParser.class.getClassLoader().getResource("").getPath();
            File[] files = new File(path + "ProtokollXMLs/Protokolle/").listFiles();


            // iterating over all xml-protocols
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                //check if the file is a xml-file
                if (file.isFile() && file.getName().matches(".*\\.xml")) {

                    //Go through the protocol-sessions
                    Document xmlDoc = db.parse(path + "ProtokollXMLs/Protokolle/" + file.getName());

                    // orientation for the Dom-Parser: https://mkyong.com/java/how-to-read-xml-file-in-java-dom-parser/
                    // get the root element of the protocol --> (dbtplenarprotokoll)
                    NodeList sessionInfoNodes = xmlDoc.getElementsByTagName("dbtplenarprotokoll");

                    //iterate through all dbtplenarptotokoll nodes
                    for (int a = 0; a < sessionInfoNodes.getLength(); a++) {
                        Node sessionInfoNode = sessionInfoNodes.item(a);
                        if (sessionInfoNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element sessionInfoElement = (Element) sessionInfoNode;

                            //String _id = file.getName().replaceAll("-data\\.xml", " ");

                            //here we get all relevant information about the protocol
                            int protocolNumber = Integer.parseInt(sessionInfoElement.getAttribute("sitzung-nr"));
                            int electionPeriod = Integer.parseInt(sessionInfoElement.getAttribute("wahlperiode"));
                            String _id = (electionPeriod + "/" + protocolNumber);
                            System.out.println(_id);

                            //using the methods from the TimeHelper class we can convert the date and time
                            String sessionDate = (sessionInfoElement.getAttribute("sitzung-datum"));
                            LocalDate date = TimeHelper.convertToISOdate(sessionDate);
                            String beginTime = (sessionInfoElement.getAttribute("sitzung-start-uhrzeit"));
                            LocalTime begin = TimeHelper.convertToISOtime(beginTime);
                            String endTime = (sessionInfoElement.getAttribute("sitzung-ende-uhrzeit"));
                            LocalTime end = TimeHelper.convertToISOtime(endTime);
                            long sessionDuration = TimeHelper.durationBetweenTimesInMinutes(begin, end);

                            //Iterate through all Tagesordnungspunkte
                            List<Element> aiElementList = getElementList(sessionInfoElement, "tagesordnungspunkt");
                            ArrayList<String> agendaItemIDS = new ArrayList<>();
                            Set<String> protocolSessionLeaders = new HashSet<>();

                            for (Element aiElement : aiElementList) {
                                String topid = aiElement.getAttribute("top-id");
                                if (agendaItemIDS.contains(topid)) {
                                } else agendaItemIDS.add(topid);
                                //System.out.println(topid);

                                //Go through all Speeches
                                List<Element> speechElementList = getElementList(aiElement, "rede");


                                for (Element speech : speechElementList) {

                                    String speechID = speech.getAttribute("id");

                                    String speakerID = "";
                                    String speechText = "";
                                    Integer commentNumber = 0;
                                    //String sessionLeader= "";
                                    //List for comments (every speech get a list of comments)
                                    List<String> commentList = new ArrayList<>(0);
                                    boolean addStatus = false;
                                    int sameSpeechCounter = 0;
                                    ArrayList<String[]> commentsPos = new ArrayList<>(0);


                                    //Go through all ChildNodes of a speech
                                    List<Element> speechChildNodeList = getChildElementList(speech);
                                    String commentID = null;
                                    ArrayList<String> sessionLeaders = null;
                                    String comment = null;
                                    for (Element speechChild : speechChildNodeList) {
                                        switch (speechChild.getTagName()) {
                                            //If its a <name>-Tag -> So its not a speaker (set addStatus = false) --> The whole text up to this will be added if the tag before was a <p klasse="redner">-Tag (addStatus == true)
                                            case "name":
                                                sameSpeechCounter = addToSpeechMap(speechID, speakerID, speechText, addStatus, mongoDBHandler, sameSpeechCounter, TimeHelper.convertToISOdate(sessionDate));
                                                addStatus = false;
                                                speakerID = "";
                                                speechText = "";
                                                commentList.clear();

                                                // @Testing //Get sessionLeader
                                                String sessionLeader = speechChild.getTextContent();
                                                //removes ":" after each session leader
                                                sessionLeader = sessionLeader.replaceFirst("\\:", "");
                                                sessionLeaders = new ArrayList<>();

                                                // Adds missing session leaders to the list
                                                if (sessionLeaders.contains(sessionLeader)) {
                                                } else {sessionLeaders.add(sessionLeader);
                                                protocolSessionLeaders.add(sessionLeader);}


                                                break;

                                            //If its a <p klasse="redner">-Tag -> So its a speaker (set addStatus = true) --> The whole text up to this will be added if the tag before was a <p klasse="redner">-Tag (addStatus == true)
                                            case "p":
                                                if (speechChild.getAttribute("klasse").equals("redner")) {
                                                    sameSpeechCounter = addToSpeechMap(speechID, speakerID, speechText, addStatus, mongoDBHandler, sameSpeechCounter, TimeHelper.convertToISOdate(sessionDate));
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

                                            case "kommentar":
                                                comment = speechChild.getTextContent();
                                                // commentID soll sein die speechID + / + Kommentar#
                                                commentNumber++;
                                                commentID = speechID + "/" + commentNumber;
                                                Integer commentPosition = speechText.length();
                                                //Remove first bracket of comment
                                                comment = comment.replaceFirst("\\(", "");
                                                //Remove last bracket of comment
                                                String reComment = new StringBuilder(comment).reverse().toString();
                                                reComment = reComment.replaceFirst("\\)", "");
                                                comment = new StringBuilder(reComment).reverse().toString();

                                                String[] commentIdPos = new String[2];
                                                commentIdPos[0] = commentID;
                                                commentIdPos[1] = commentPosition + "";
                                                commentsPos.add(commentIdPos);

                                                String commentatorID = "";
                                                ArrayList<String> commentatorFractions = new ArrayList<>(0);
                                                /*
                                                hier wird eine Liste fullName benötigt/aus den Stammdaten generiert
                                                Jedes Mal, wenn eckige Klammern vorkommen, wird geprüft,
                                                ob vor den Klammern der Name vorkommt.
                                                   */

                                                for (int k = 0; k < persons.size(); k++) {
                                                    Boolean commentatorName = (comment.indexOf(persons.get(i).getFirstName() + " " + persons.get(i).getLastName()) < comment.indexOf("["));
                                                    if (commentatorName) {
                                                        commentatorID = persons.get(k).getID();
                                                        if (!(persons.get(k).getFraction19() == null)) {
                                                            commentatorFractions.add(persons.get(k).getFraction19());
                                                        }
                                                        if (!(persons.get(k).getFraction20() == null)) {
                                                            commentatorFractions.add(persons.get(k).getFraction20());
                                                        }
                                                    }
                                                }
                                                addToCommentMap(commentID, speechID, speakerID, commentatorID, commentPosition, comment, date, commentatorFractions);

                                                if (commentList.contains(comment)) {
                                                } else commentList.add(comment);
                                                break;

                                            default:
                                                break;
                                        }
                                    }

                                    //At the end of a xml speech: The whole text up to this will be added if the tag before was a <p klasse="redner">-Tag (addStatus == true)
                                    addToSpeechMap(speechID, speakerID, speechText, addStatus, mongoDBHandler, sameSpeechCounter, TimeHelper.convertToISOdate(sessionDate));
                                }

                                for (Map.Entry<String, Comment_Impl> stringCommentEntry : commentMap.entrySet()) {
                                    if (!mongoDBHandler.checkIfDocumentExists("comment", stringCommentEntry.getValue().getID())) {
                                        double sentiment = uima.getAverageSentiment(uima.getJCas(stringCommentEntry.getValue().getText()));
                                        mongoDBHandler.insertComment(
                                                stringCommentEntry.getValue(),
                                                sentiment
                                        );
                                    }
                                }
                                commentMap.clear();

                            }

                            List<Element> speakerElementList = getElementList(sessionInfoElement, "redner");

                            for (Element speakerElement : speakerElementList) {
                                String speakerID = speakerElement.getAttribute("id");
                                String[] speakerProperties = new String[10];
                                List<Element> nameElementList = getElementList(speakerElement, "name");
                                for (Element name : nameElementList) {
                                    List<Element> childElementList = getChildElementList(name);
                                    for (Element nameChild : childElementList) {
                                        switch (nameChild.getTagName()) {
                                            case "titel":
                                                speakerProperties[1] = nameChild.getTextContent();
                                                break;
                                            case "vorname":
                                                speakerProperties[2] = nameChild.getTextContent();
                                                break;
                                            case "namenszusatz":
                                                speakerProperties[3] = nameChild.getTextContent();
                                                break;
                                            case "nachname":
                                                speakerProperties[4] = nameChild.getTextContent();
                                                break;
                                            case "ortszusatz":
                                                speakerProperties[5] = nameChild.getTextContent();
                                                break;
                                            case "fraktion":
                                                speakerProperties[6] = nameChild.getTextContent();
                                                break;
                                            case "rolle":
                                                List<Element> roleElementList = getChildElementList(nameChild);
                                                for (Element role : roleElementList) {
                                                    if (role.getTagName().equals("rolle_kurz")) {
                                                        speakerProperties[7] = role.getTextContent();
                                                    }
                                                }
                                                break;
                                            case "bdland":
                                                speakerProperties[8] = nameChild.getTextContent();
                                                break;
                                            default:
                                                break;
                                        }
                                    }
                                    // aus den Protokollen: id, Titel, Vorname, namenszusatz, nachname, ortszusatz,
                                    //fraktion, rolle

                                    // aus den Stammdaten muss folgendes rein: Partei, Place (?)
                                    /*
                                    Person_Impl person = new Person_Impl(speakerProperties[0], speakerProperties[1], speakerProperties[2], speakerProperties[3], speakerProperties[4], speakerProperties[5], speakerProperties[6], speakerProperties[7], speakerProperties[8]);
                                    personMap.put(person.getID(), person)
                                     */
                                }


                                if (!mongoDBHandler.checkIfDocumentExists("person", speakerID)) {
                                    Person_Impl person = null;
                                    if (electionPeriod == 19) {
                                        person = new Person_Impl(speakerID, speakerProperties[2], speakerProperties[4], speakerProperties[7],
                                                speakerProperties[1], speakerProperties[5], speakerProperties[6], null, "Parteilos",
                                                PictureScraper.producePictureUrl(speakerProperties[2], speakerProperties[4]), null, null,
                                                null, speakerProperties[8]);
                                    } else if (electionPeriod == 20) {
                                        person = new Person_Impl(speakerID, speakerProperties[2], speakerProperties[4], speakerProperties[7],
                                                speakerProperties[1], speakerProperties[5], speakerProperties[6], null, "Parteilos",
                                                PictureScraper.producePictureUrl(speakerProperties[2], speakerProperties[4]), null, null,
                                                null, speakerProperties[8]);
                                    }
                                    mongoDBHandler.insertPerson(person);
                                    persons.add(person);
                                }

                                commentMap.clear();
                            }
                            ArrayList<String> agendaItemFullIDS = new ArrayList<>(0);
                            String protcolID = electionPeriod + "/" + protocolNumber;
                            for (int k = 0; k < agendaItemIDS.size(); k++) {
                                agendaItemFullIDS.add(protcolID + "/" + agendaItemIDS.get(k));
                            }
                            if (!mongoDBHandler.checkIfDocumentExists("protocol", protcolID)) {
                                Protocol_Impl protocol = new Protocol_Impl(protcolID, date, begin, end, sessionDuration,
                                        electionPeriod, protocolNumber, protocolSessionLeaders, agendaItemFullIDS);
                                mongoDBHandler.insertProtocol(protocol);
                            }
                        }
                    }

                    List<ProcessedSpeech> processedSpeeches = new ArrayList<>();
                    for (Map.Entry<String, Speech_Impl> stringSpeechEntry : speechMap.entrySet()) {
                        if (!mongoDBHandler.checkIfDocumentExists("speech", stringSpeechEntry.getValue().getID())) {
                            processedSpeeches.add(uima.processSpeech(stringSpeechEntry.getValue()));
                        }
                    }

                    mongoDBHandler.insertSpeeches(processedSpeeches);
                    speechMap.clear();

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
     * Get the Child Elementlist of a parentElement (Same logic as in the method getElementList)
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


    /**
     * This method is for Database Control
     *
     * @param speechID
     * @param speakerID
     * @param speechText
     * @param sameSpeechCounter
     * @param date
     * @author Andrej Artuschenko
     */
    @Testing
    public static int addToSpeechMap(String speechID, String speakerID, String speechText, Boolean addStatus, MongoDBHandler mongoDBHandler, int sameSpeechCounter, LocalDate date) {
        if (((!(speakerID.equals(""))) && (!(speechText.equals(""))) && (!(speechMap.containsKey(speechID))) && addStatus)) {
            sameSpeechCounter = sameSpeechCounter + 1;
            //If speech is not in MongoDB, add it in MongoDB
            if (!mongoDBHandler.checkIfDocumentExists("test_Speech_Andrej", speechID)) {
                speechMap.put(speechID, new Speech_Impl(speechID, speakerID, speechText, date));
            } else {
                System.out.println("Speech" + (speechID + "#" + String.valueOf(sameSpeechCounter)) + " ist schon in der Datenbank");
            }
        }
        return sameSpeechCounter;
    }

    /**
     * This Method generates an object of Comment_Impl and adds it into a Map with its Id as its key.
     *
     * @param commentID
     * @param speechID
     * @param speakerID
     * @param commentatorID
     * @param commentPosition
     * @param comment
     * @param date
     * @param commentatorFractions
     * @author Andrej Artuschenko
     * @author Julian Ocker
     */
    public static void addToCommentMap(String commentID, String speechID, String speakerID, String commentatorID,
                                       Integer commentPosition, String comment, LocalDate date, ArrayList commentatorFractions) {
        if (((!(commentID.equals(""))) && (!(commentMap.containsKey(commentID))))) {
            commentMap.put(commentID, new Comment_Impl(commentID, speechID, speakerID, commentatorID, commentPosition, comment, date, commentatorFractions));
        }
    }
}

