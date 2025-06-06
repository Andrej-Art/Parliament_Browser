package utility;

import data.impl.*;
import org.apache.uima.UIMAException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
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
import java.util.function.Consumer;

/**
 * This class is currently only for testing purposes. At the end there will be one class for parsing.
 * I don't want to get in Julians way with the Stammdaten-Parser.
 *
 * @author Andrej
 * @author Julian Ocker
 */

public class XMLProtocolParser {

    private static Map<String, Speech_Impl> speechMap;
    private static Map<String, Comment_Impl> commentMap;

    //Create New Instance of DocumentBuilderFactory
    private static DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

    public static Boolean speechParse(File[] files) throws FileNotFoundException, UIMAException {

        speechMap = new HashMap<>();
        commentMap = new HashMap<>();

        //Reset Maps
        //Create connection to mongoDB
        MongoDBHandler mongoDBHandler = MongoDBHandler.getHandler();

        UIMAPerformer uima = new UIMAPerformer();

        try {
            ArrayList<Person_Impl> persons = mongoDBHandler.getPersons();
            //Parsing all XMLs-protocols from last to first one
            DocumentBuilder db = dbf.newDocumentBuilder();
            //access to our downloaded protocol-files
            String path = XMLProtocolParser.class.getClassLoader().getResource("").getPath();

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

                            NodeList contentTableElementList = getElementList(sessionInfoElement, "inhaltsverzeichnis").get(0).getChildNodes();

                            Map<String, String> incompleteAiMap = new HashMap<>(0);
                            incompleteAiMap = getAiElements(contentTableElementList);

                            //here we get all relevant information about the protocol
                            int protocolNumber = Integer.parseInt(sessionInfoElement.getAttribute("sitzung-nr"));
                            int electionPeriod = Integer.parseInt(sessionInfoElement.getAttribute("wahlperiode"));
                            String _id = (electionPeriod + "/" + protocolNumber);

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
                            List<AgendaItem_Impl> completeAis = new ArrayList<>(0);

                            for (Element aiElement : aiElementList) {
                                String topid = aiElement.getAttribute("top-id");
                                if (agendaItemIDS.contains(topid)) {
                                } else agendaItemIDS.add(topid);

                                //Go through all Speeches
                                List<Element> speechElementList = getElementList(aiElement, "rede");
                                ArrayList<String> speechIDs = new ArrayList<>(0);

                                for (Element speech : speechElementList) {

                                    String speechID = speech.getAttribute("id");
                                    if (!speechIDs.contains(speechID)) {
                                        speechIDs.add(speechID);
                                    }
                                    String speakerID = "";
                                    String speechText = "";
                                    Integer commentNumber = 0;
                                    //String sessionLeader= "";
                                    //List for comments (every speech get a list of comments)
                                    List<String> commentList = new ArrayList<>(0);
                                    boolean addStatus = false;
                                    int sameSpeechCounter = 0;

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
                                                commentList.clear();

                                                String sessionLeader = speechChild.getTextContent();
                                                //removes ":" after each session leader
                                                sessionLeader = sessionLeader.replaceFirst("\\:", "");
                                                sessionLeaders = new ArrayList<>();

                                                // Adds missing session leaders to the list
                                                if (sessionLeaders.contains(sessionLeader)) {
                                                } else {
                                                    sessionLeaders.add(sessionLeader);
                                                    protocolSessionLeaders.add(sessionLeader);
                                                }


                                                break;

                                            //If its a <p klasse="redner">-Tag -> So it is a speaker (set addStatus = true) --> The whole text up to this will be added if the tag before was a <p klasse="redner">-Tag (addStatus == true)
                                            case "p":
                                                // "Drucksache X" is not part of the speech
                                                if (speechChild.getAttribute("klasse").equals("T_Drs") || speechChild.getAttribute("klasse").equals("T_Beratung"))
                                                    break;

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
                                                commentNumber++;
                                                commentID = speechID + "/" + commentNumber;
                                                Integer commentPosition = speechText.length();
                                                //Remove first bracket of comment
                                                comment = comment.replaceFirst("\\(", "");
                                                //Remove last bracket of comment
                                                String reComment = new StringBuilder(comment).reverse().toString();
                                                reComment = reComment.replaceFirst("\\)", "");
                                                comment = new StringBuilder(reComment).reverse().toString();

                                                String commentatorID = "N/A";
                                                ArrayList<String> commentatorFractions = new ArrayList<>(0);

                                                for (Person_Impl person : persons) {
                                                    String fullName = person.getFirstName() + " " + person.getLastName();
                                                    if (comment.indexOf(fullName) < comment.indexOf("[") && comment.contains(fullName)) {
                                                        commentatorID = person.getID();
                                                        if (person.getFraction19() != null) {
                                                            commentatorFractions.add(person.getFraction19());
                                                        }
                                                        if (person.getFraction20() != null) {
                                                            commentatorFractions.add(person.getFraction20());
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

                                String trueAiID = electionPeriod + "/" + protocolNumber + "/" + topid;

                                if (incompleteAiMap.containsKey(topid)) {
                                    completeAis.add(new AgendaItem_Impl(trueAiID, date, incompleteAiMap.get(topid), speechIDs));
                                } else {
                                    completeAis.add(new AgendaItem_Impl(trueAiID, date, topid, speechIDs));
                                }
                            }

                            mongoDBHandler.insertAgendaItems(completeAis);

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
                                }


                                if (!mongoDBHandler.checkIfDocumentExists("person", speakerID) && !speakerID.isEmpty()) {
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
                            String protocolID = electionPeriod + "/" + protocolNumber;
                            for (int k = 0; k < agendaItemIDS.size(); k++) {
                                agendaItemFullIDS.add(protocolID + "/" + agendaItemIDS.get(k));
                            }
                            if (!mongoDBHandler.checkIfDocumentExists("protocol", protocolID)) {
                                Protocol_Impl protocol = new Protocol_Impl(protocolID, date, begin, end, sessionDuration,
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
            return false;
        }
        return true;
    }

    /**
     * Method to get the Subject of the agenda items out of the table of contents.
     *
     * @param contentTableElementList
     * @return
     * @author Julian Ocker
     */
    private static Map<String, String> getAiElements(NodeList contentTableElementList) {
        Map<String, String> incompleteAiMap = new HashMap<>(0);
        for (int i = 0; i < contentTableElementList.getLength(); i++) {

            if (contentTableElementList.item(i).getNodeName().equals("ivz-block")) {
                String aiID = "";
                String aiSubject = "";
                NodeList dataOfContentElements = contentTableElementList.item(i).getChildNodes();
                Boolean first = true;
                for (int k = 0; k < dataOfContentElements.getLength(); k++) {
                    if (dataOfContentElements.item(k).getNodeName().equals("ivz-block-titel")) {
                        aiID = dataOfContentElements.item(k).getTextContent().replace(":", "");
                    }
                    if (dataOfContentElements.item(k).getNodeName().equals("ivz-eintrag")) {
                        NodeList contentOfContentElements = dataOfContentElements.item(k).getChildNodes();
                        Boolean check = true;
                        String contentOfContentElement = "";

                        for (int l = 0; l < contentOfContentElements.getLength(); l++) {
                            if (contentOfContentElements.item(l).getNodeName().equals("ivz-eintrag-inhalt")) {
                                contentOfContentElement = contentOfContentElements.item(l).getTextContent();
                            }
                            if (contentOfContentElements.item(l).getNodeName().equals("xref")) {
                                check = false;
                            }
                        }
                        if (check) {
                            if (first) {
                                aiSubject = aiSubject + contentOfContentElement;
                                first = false;
                            } else {
                                aiSubject = aiSubject + "\n" + contentOfContentElement;
                            }
                        }
                    }
                }
                incompleteAiMap.put(aiID, aiSubject);
            }
            contentTableElementList.item(i);
        }

        return incompleteAiMap;
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
     * This Method generates an object of Comment_Impl and adds it into a Map with its ID as its key.
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

    /**
     * This Method returns a list of all parsed protocols.
     *
     * @return
     * @author Julian Ocker
     */
    public static List<String> getListOfParsedProtocols() {
        MongoDBHandler mongoDBHandler = MongoDBHandler.getHandler();
        List<Integer> protocolNumbers = new ArrayList(0);
        List<String> protocols = new ArrayList(0);
        mongoDBHandler.getDB().getCollection("protocol").find().forEach(
                (Consumer<? super org.bson.Document>) procBlock
                        -> protocolNumbers.add((procBlock.getInteger("electionPeriod") * 1000
                        + procBlock.getInteger("protocolNumber"))));
        for (int i = 0; i < protocolNumbers.size(); i++) {
            protocols.add(protocolNumbers.get(i) + "-data.xml");
        }
        return protocols;
    }

    /**
     * This method gets and returns a List containing all Protocol-Files.
     *
     * @return
     * @author Julian Ocker
     */
    public static File[] getAllFiles() {
        File[] files = new File[0];
        try {
            //Parsing all XMLs-protocols from last to first one
            DocumentBuilder db = dbf.newDocumentBuilder();
            //access to our downloaded protocol-files
            String path = XMLProtocolParser.class.getClassLoader().getResource("").getPath();
            files = new File(path + "ProtokollXMLs/Protokolle/").listFiles();
            if (files == null) {
                files = new File[0];
            }
            List<File> fileList = Arrays.asList(files);
            List<File> newFileList = new ArrayList<>(0);
            int i = 0;
            while (i < fileList.size()) {
                String fileName = fileList.get(i).getName();
                if (fileName.contains("-data.xml")) {
                    newFileList.add(fileList.get(i));
                }
                i++;
            }
            files = new File[newFileList.size()];
            for (int k = 0; k < newFileList.size(); k++) {
                files[k] = newFileList.get(k);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return files;
    }

    /**
     * This function returns a FileArray of all new Files.
     *
     * @return
     * @author Julian Ocker
     */
    public static File[] getArrayOfNewProtocols() {
        List<String> protocols = getListOfParsedProtocols();
        File[] newFiles = new File[0];
        List<Integer> fileIndices = new ArrayList(0);
        try {
            File[] files = getAllFiles();
            for (int i = 0; i < files.length; i++) {
                if (!protocols.contains(files[i].getName().toString())) {
                    fileIndices.add(i);
                }
            }
            newFiles = new File[fileIndices.size()];
            for (int i = 0; i < fileIndices.size(); i++) {
                newFiles[i] = files[fileIndices.get(i)];
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return newFiles;
    }

    /**
     * This method starts the parser with all protocols
     *
     * @return
     * @author Julian Ocker
     */
    public static Boolean parserStarterGenerell() {
        File[] files = getAllFiles();
        try {
            return speechParse(files);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * This method starts the parser with all files that aren't part of the database
     *
     * @return
     * @author Julian Ocker
     */
    public static Boolean parserStarterNewProtocols() {
        File[] files = getArrayOfNewProtocols();
        try {
            return speechParse(files);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * This method starts the parser with a single specific file.
     *
     * @param filename
     * @return
     * @author Julian Ocker
     */
    public static Boolean parserStarterSingle(String filename) {
        File[] files = getAllFiles();
        File[] fileToParse = new File[1];
        for (int i = 0; i < files.length; i++) {
            if (files[i].getPath().contains(filename)) {
                fileToParse[0] = files[i];
            }
        }
        try {
            return speechParse(fileToParse);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}