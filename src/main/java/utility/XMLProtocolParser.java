package utility;

import data.impl.Comment_Impl;
import data.impl.Protocol_Impl;
import data.impl.Speech_Impl;
import data.impl.Person_Impl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import utility.annotations.Testing;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is currently only for testing purposes. At the end there will be one class for parsing.
 * I don't want to get in Julians way with the Stammdaten-Parser.
 *
 * @author Andrej
 * @Testing
 * @Unfinished
 */

public class XMLProtocolParser {

    //private Map <Object, Speech_Impl>speechMap;
    HashMap<Object, Speech_Impl> speechMap = new HashMap<>();



    private Map<String, Person_Impl> speakerMap;
    private Map<String, Comment_Impl> commentMap;
    private Map<String, Protocol_Impl> protocolMap;


    //Create New Instance of DocumentBuilderFactory
    static DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

    public static void speechParse2() {

        //Reset Maps
        //speechMap = new HashMap<>();


         //Create connection to mongoDB
         MongoDBHandler mongoDBHandler = null;
         try {
             mongoDBHandler = new MongoDBHandler();
         } catch (IOException e) {
         e.printStackTrace();
         }

        try {

            //Parsing all XMLs-protocols from last to first one
            DocumentBuilder db = dbf.newDocumentBuilder();
            //access to our downloaded protocol-files
            File[] files = new File("ProtokollXMLs/").listFiles();



            // iterating over all xml-protocols
            for (int i = files.length - 1; i >= 0; i--) {
                File file = files[i];
                //check if the file is a xml-file
                if (file.isFile() && file.getName().matches(".*\\.xml")) {
                    //Show Progress in console (we won't need later)
                    //System.out.println(" The file " + file.getName() + " is being edited...");
                    //System.out.println("\tSpeeches are collected right now...");

                    //Go through the protocol-sessions
                    Document xmlDoc = db.parse("ProtokollXMLs/" + file.getName());

                    // orientation for the Dom-Parser: https://mkyong.com/java/how-to-read-xml-file-in-java-dom-parser/
                    //get the root element of the protocol --> (dbtplenarprotokoll)
                    NodeList sessionInfoNodes = xmlDoc.getElementsByTagName("dbtplenarprotokoll");
                    //iterate threw all dbtplenarptotokoll nodes

                    /*


                    ArrayList <String> sessionLeaders = new ArrayList<>();
                    ArrayList<String> agendaItemIDS = new ArrayList<>();

                     */



                    for (int a = 0; a < sessionInfoNodes.getLength(); a++) {
                        Node sessionInfoNode = sessionInfoNodes.item(a);
                        if (sessionInfoNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element sessionInfoElement = (Element) sessionInfoNode;

                            //String _id = file.getName().replaceAll("-data\\.xml", " ");

                            //here we get all relevant information about the protocol
                            int protocolNumber = Integer.parseInt(sessionInfoElement.getAttribute("sitzung-nr"));
                            int electionPeriod = Integer.parseInt(sessionInfoElement.getAttribute("wahlperiode"));
                            String _id = (electionPeriod + "/" + protocolNumber);
                            //System.out.println(_id);

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

                            // ################################################### !!!!! Aufpassen wo sollen die Listen hin?
                            ArrayList <String> sessionLeaders = new ArrayList<>();
                            ArrayList<String> agendaItemIDS = new ArrayList<>();
                            // ####################################################


                            for (Element aiElement : aiElementList) {
                                String topid = aiElement.getAttribute("top-id");
                                //ArrayList<String> agendaItemIDS = new ArrayList<>();
                                if(agendaItemIDS.contains(topid))
                                {}
                                else agendaItemIDS.add(topid);
                                //System.out.println(topid);

                                //Go through all Speeches
                                List<Element> speechElementList = getElementList(aiElement, "rede");


                                for (Element speech : speechElementList) {

                                    String speechID = speech.getAttribute("id");

                                    String speakerID = "";
                                    String speechText = "";
                                    //String sessionLeader= "";
                                    //List for comments (every speech get a list of comments)
                                    List<String> commentList = new ArrayList<>();
                                    boolean addStatus = false;
                                    int sameSpeechCounter = 0;


                                    //Go through all ChildNodes of a speech
                                    List<Element> speechChildNodeList = getChildElementList(speech);
                                    String commentID = null;
                                    for (Element speechChild : speechChildNodeList) {
                                        switch (speechChild.getTagName()) {
                                            //If its a <name>-Tag -> So its not a speaker (set addStatus = false) --> The whole text up to this will be added if the tag before was a <p klasse="redner">-Tag (addStatus == true)
                                            case "name":
                                                speakerID = "";
                                                speechText = "";
                                                commentList.clear();
                                                addStatus = false;
                                                // @Testing //Get sessionLeader
                                                String sessionLeader = speechChild.getTextContent();

                                                //removes ":" after each session leader
                                                sessionLeader = sessionLeader.replaceFirst("\\:", "");


                                                // Adds missing session leaders to the list
                                                if (sessionLeaders.contains(sessionLeader)) {
                                                } else sessionLeaders.add(sessionLeader);
                                                //System.out.println(sessionLeaders);

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

                                            case "kommentar":
                                                String comment = speechChild.getTextContent();

                                                //Remove first bracket of comment
                                                comment = comment.replaceFirst("\\(", "");

                                                //Remove last bracket of comment
                                                String reComment = new StringBuilder(comment).reverse().toString();
                                                reComment = reComment.replaceFirst("\\)", "");
                                                comment = new StringBuilder(reComment).reverse().toString();

                                                /*
                                                hier wird eine Liste fullName benötigt/aus den Stammdaten generiert
                                                Jedes Mal, wenn eckige Klammern vorkommen, wird geprüft,
                                                ob vor den Klammern der Name vorkommt.

                                                String fullName = "Renate Künast" ;

                                                Boolean commentatorName =(comment.indexOf(fullName) < comment.indexOf("["));
                                                if (commentatorName = true){
                                                    System.out.println(fullName);
                                                }
                                                 */

                                                //String rauslesen aus den eckigen Klammen, um Partei zu erhalten
                                                /*
                                                String partyComment = commentList.get().substring();
                                                substring(comment.indexOf("[")+1, comment.indexOf("]"));
                                                System.out.println(partyComment);

                                                 */


                                                //System.out.println(comment + comment.contains("["));

                                                if (commentList.contains(comment)) {
                                                } else commentList.add(comment);

                                                break;

                                            default:
                                                break;
                                        }

                                        /*
                                        for (int p = 0; p <= speechElementList.size(); p++){
                                            for (int c = 0; c <= commentList.size(); c++) {
                                                commentID = speechID + "/"+ p+ "/" + c;
                                                System.out.println(commentID);

                                                //System.out.println(commentID);
                                            }
                                        }
                                         */

                                    }

                                    //At the end of a xml speech: The whole text up to this will be added if the tag before was a <p klasse="redner">-Tag (addStatus == true)
                                    addToSpeechMap(speechID, speakerID, speechText, TimeHelper.convertToISOdate(sessionDate));
                                    addToProtocolMap(_id, date, begin, end, sessionDuration, electionPeriod, protocolNumber, sessionLeaders, agendaItemIDS);
                                    //Speech_Impl speech2 = new Speech_Impl(speechID, speakerID, speechText, TimeHelper.convertToISOdate(sessionDate));
                                    //Protocol_Impl protocol = new Protocol_Impl(_id, sessionDate, beginTime, endTime, electionPeriod, protocolNumber, sessionLeader, topid);
                                    //Protocol_Impl protocol = new Protocol_Impl(_id, date, begin, end, sessionDuration, electionPeriod, protocolNumber, sessionLeaders, agendaItemIDS);






                                   /*
                                   #####################################################################################

                                   speeches haben unterschiedliche Zeichenlänge und werden nicht wiederholt
                                   System.out.println(speech2.getText().length());


                                    */


                                    // Comment_Impl comment = new Comment_Impl(CommentID, speechID, speakerID, commentatorID, commentText, date,


                                    //System.out.println(protocol + " "+protocol.getSessionLeaders());
                                    //Comment_Impl comment = new Comment_Impl(commentID, speakerID);
                                    /*
                                        private String _id, speechID, speakerID, commentatorID, text;
                                            private LocalDate date;

                                                private ArrayList<String> fractions;
                                     */


                                    //addToCommentMap(commentID, speechID, speakerID, commentID);
                                    //System.out.println(commentID);

                                }
                            }
                            //System.out.println("\tPersons werden gesammelt...");

                            List<Element> speakerElementList = getElementList(sessionInfoElement, "redner");
                            for(Element speakerElement: speakerElementList) {
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
                                                for(Element role: roleElementList){
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

                                    // aus den Stammdaten muss folgendes rein: Partei, Place
                                  // Person_Impl person = new Person_Impl(speakerProperties[0], speakerProperties[1]);
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
     *
     * @author Andrej Artuschenko
     * @param speechID
     * @param speakerID
     * @param speechText
     * @param date
     */

    public static void addToSpeechMap(String speechID, String speakerID, String speechText, LocalDate date){

        /**

        if ((!(speakerID.equals(""))) && (!(speechText.equals("")))) {
            speechMap.put(speechID, speakerID, speechText, date);

        }
         */
        //new Speech_Impl(speechID, speakerID, speechText, date);
       // speechMap.put(new Speech_Impl(speechID, speakerID, speechText, date));

        /*
        System.out.println(speechID);
        System.out.println(speakerID);
        System.out.println(speechText);
        System.out.println(date);
        System.out.println("------------------------------------------------------------------");

         */

        /*
        Note: die kommentare/Ansagen der Sitzungsleiter sind eine eigene "Speech" und haben eine eigene.
        speechID erhalten. Sie haben keinen Eintrag für speakerID (das müsste noch befüllt werden).
         */



    }

    public static void addToProtocolMap(String _id,LocalDate date,LocalTime begin,LocalTime end,long sessionDuration,int electionPeriod, int protocolNumber, ArrayList<String> sessionLeaders,ArrayList<String> agendaItemIDS){
        new Protocol_Impl(_id, date, begin, end, sessionDuration, electionPeriod, protocolNumber, sessionLeaders, agendaItemIDS);


        System.out.println(_id);
        System.out.println(date);
        System.out.println(begin);
        System.out.println(end);
        System.out.println(sessionDuration);
        System.out.println(electionPeriod);
        System.out.println(protocolNumber);
        System.out.println(sessionLeaders);
        System.out.println(agendaItemIDS);
        System.out.println("--------------------------------------------------------------------------------------------");


    }

    /**
     * Get the Speech Map
     * @author Andrej Artuschenko
     * @return Map<String, Speech_Impl>
     */
    /*
    public static Map<String, Speech_Impl> getSpeechMap() {
        return speechMap;

    }

     */
    /*
    public static Map<Object, Speech_Impl> getSpeechMap(){
        return speechMap;
    }

     */





    public Map<String, Protocol_Impl> getProtocolMap(){
        return protocolMap;
    }



    /*
    public static void addToCommentMap(String commentID, String speechID, String speakerID, String commentatorID, String commentText, LocalDate date, String fraction) {
        new Comment_Impl(commentID, speechID, speakerID, commentatorID, commentText, date, fraction);
    }

     */

    /**
     * Get the Comment Map
     * @author Andrej Artuschenko
     * @return Map<String, Speech_Impl>
     */
    public  Map<String, Comment_Impl> getCommentMap() {return commentMap;}

}

