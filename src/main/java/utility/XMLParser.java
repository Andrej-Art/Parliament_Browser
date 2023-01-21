package utility;


import data.Person;
import data.impl.AgendaItem_Impl;
import data.impl.Person_Impl;
import data.impl.Protocol_Impl;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class to analyze the XML files and create objects
 *
 * @author Andrej Artuschenko
 * @author Julian Ocker
 * @author DavidJordan
 */
public class XMLParser {

    // Here the persons are saved for the duration of the parsing
    static List<Person> persons = new ArrayList<Person>(0);

    // Here the protocols are saved for the duration of the parsing
    static ArrayList<Protocol_Impl> protocols = new ArrayList<>(0);

    // Here the agenda items are saved for the duration of the parsing
    static ArrayList<AgendaItem_Impl> agendaItems = new ArrayList<>(0);

    /**
     * This method reads the Stammdaten-file and creates instances of the person-Class.
     *
     * @return
     * @author Julian Ocker
     */
    public static void personParse() throws IOException {
        System.out.println(XMLParser.class.getClassLoader().getResource("ProtokollXMLs/MdB-Stammdaten-data/MDB_STAMMDATEN.XML").getPath());
        String path = XMLParser.class.getClassLoader().getResource("ProtokollXMLs/MdB-Stammdaten-data/MDB_STAMMDATEN.XML").getPath();
        try {
            //gett the File and make it accessible
            File input_file = new File(path);
            DocumentBuilderFactory dbfFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dbBuilder = dbfFactory.newDocumentBuilder();
            Document dProtocol = dbBuilder.parse(input_file);
            NodeList testList = dProtocol.getElementsByTagName("MDB");
            for (int h = 0; h < testList.getLength(); h++) {

                //declaring Variables that should remain unchanged throughout one Iteration
                NodeList attributes = testList.item(h).getChildNodes();
                String id = null;
                String firstName = null;
                String lastName = null;
                String title = null;
                String party = null;
                String fraction19 = null;
                String fraction20 = null;
                String deputyAkadTitle = null;
                String deputyPoliticStart = null;
                String deputyPoliticEnd = null;
                String birthDate = null;
                String birthPlace = null;
                String birthnation = null;
                String deathDate = null;
                String gender = null;
                String familyFactor = null;
                String religion = null;
                String vita = null;
                String job = null;
                String publicInterest = null;
                String place = null;
                ArrayList<String> WPList = new ArrayList<>(0);
                ArrayList<String> fractionList = new ArrayList<>(0);
                ArrayList<ArrayList<String>> WPFractionList = new ArrayList<>(0);
                boolean noDoubleRepresantativeInsurance = true;

                // pathing to get the personal data of a represantative
                for (int i = 0; i < attributes.getLength(); i++) {
                    if (attributes.item(i).getNodeName().equals("ID")) {
                        id = attributes.item(i).getTextContent();
                    }
                    if (attributes.item(i).getNodeName().equals("NAMEN")) {
                        NodeList namesAttributes = attributes.item(i).getChildNodes();
                        for (int j = 0; j < namesAttributes.getLength(); j++) {

                            if (namesAttributes.item(j).getNodeName().equals("NAME")) {
                                NodeList nameAttributes = namesAttributes.item(j).getChildNodes();
                                for (int k = 0; k < nameAttributes.getLength(); k++) {

                                    if (nameAttributes.item(k).getNodeName().equals("VORNAME")) {
                                        firstName = nameAttributes.item(k).getTextContent();
                                    }
                                    if (nameAttributes.item(k).getNodeName().equals("NACHNAME")) {
                                        lastName = nameAttributes.item(k).getTextContent();
                                    }
                                    if (nameAttributes.item(k).getNodeName().equals("ORTSZUSATZ")) {
                                        place = nameAttributes.item(k).getTextContent();
                                    }
                                    if (nameAttributes.item(k).getNodeName().equals("ANREDE_TITEL")) {
                                        title = nameAttributes.item(k).getTextContent();
                                    }
                                    if (nameAttributes.item(k).getNodeName().equals("AKAD_TITEL")) {
                                        deputyAkadTitle = nameAttributes.item(k).getTextContent();
                                    }
                                    if (nameAttributes.item(k).getNodeName().equals("HISTORIE_VON")) {
                                        deputyPoliticStart = nameAttributes.item(k).getTextContent();
                                    }
                                    if (nameAttributes.item(k).getNodeName().equals("HISTORIE_BIS")) {
                                        deputyPoliticEnd = nameAttributes.item(k).getTextContent();
                                    }
                                }
                            }
                        }
                    }
                    // Here personal information get fetched.
                    if (attributes.item(i).getNodeName().equals("BIOGRAFISCHE_ANGABEN")) {
                        NodeList biographicAttributes = attributes.item(i).getChildNodes();
                        for (int j = 0; j < biographicAttributes.getLength(); j++) {
                            if (biographicAttributes.item(j).getNodeName().equals("GEBURTSDATUM")) {
                                birthDate = biographicAttributes.item(j).getTextContent();
                            }
                            if (biographicAttributes.item(j).getNodeName().equals("GEBURTSORT")) {
                                birthPlace = biographicAttributes.item(j).getTextContent();
                            }
                            if (biographicAttributes.item(j).getNodeName().equals("GEBURTSLAND")) {
                                birthnation = biographicAttributes.item(j).getTextContent();
                            }
                            if (biographicAttributes.item(j).getNodeName().equals("STERBEDATUM")) {
                                deathDate = biographicAttributes.item(j).getTextContent();
                            }
                            if (biographicAttributes.item(j).getNodeName().equals("GESCHLECHT")) {
                                gender = biographicAttributes.item(j).getTextContent();
                            }
                            if (biographicAttributes.item(j).getNodeName().equals("FAMILIENSTAND")) {
                                familyFactor = biographicAttributes.item(j).getTextContent();
                            }
                            if (biographicAttributes.item(j).getNodeName().equals("RELIGION")) {
                                religion = biographicAttributes.item(j).getTextContent();
                            }
                            if (biographicAttributes.item(j).getNodeName().equals("BERUF")) {
                                job = biographicAttributes.item(j).getTextContent();
                            }
                            if (biographicAttributes.item(j).getNodeName().equals("VITA_KURZ")) {
                                vita = biographicAttributes.item(j).getTextContent();
                            }
                            if (biographicAttributes.item(j).getNodeName().equals("VEROEFFENTLICHUNGSPFLICHTIGES")) {
                                publicInterest = biographicAttributes.item(j).getTextContent();
                            }
                            if (biographicAttributes.item(j).getNodeName().equals("PARTEI_KURZ")) {
                                party = biographicAttributes.item(j).getTextContent();
                            }
                        }
                    }
                    // Here the fractions of a person get fetched.
                    if (attributes.item(i).getNodeName().equals("WAHLPERIODEN")) {
                        NodeList periodsAttributes = attributes.item(i).getChildNodes();

                        for (int j = 0; j < periodsAttributes.getLength(); j++) {
                            if (periodsAttributes.item(j).getNodeName().equals("WAHLPERIODE")) {
                                NodeList periodAttributes = periodsAttributes.item(j).getChildNodes();

                                String electionPeriod = null;

                                for (int k = 0; k < periodAttributes.getLength(); k++) {
                                    if (periodAttributes.item(k).getNodeName().equals("WP")) {
                                        WPList.add(periodAttributes.item(k).getTextContent());
                                        electionPeriod = periodAttributes.item(k).getTextContent();
                                    }

                                    if (periodAttributes.item(k).getNodeName().equals("INSTITUTIONEN")) {
                                        NodeList institutionsAttributes = periodAttributes.item(k).getChildNodes();
                                        for (int l = 0; l < institutionsAttributes.getLength(); l++) {

                                            if (institutionsAttributes.item(l).getNodeName().equals("INSTITUTION")) {
                                                NodeList InstitiutionAttributes = institutionsAttributes.item(l).getChildNodes();
                                                boolean check = false;

                                                // getting the fraction of the representative

                                                for (int m = 0; m < InstitiutionAttributes.getLength(); m++) {
                                                    if (InstitiutionAttributes.item(m).getNodeName().equals("INSART_LANG")) {
                                                        if (InstitiutionAttributes.item(m).getTextContent().equals("Fraktion/Gruppe")) {

                                                            check = true;

                                                        }
                                                    }

                                                    if (InstitiutionAttributes.item(m).getNodeName().equals("INS_LANG") && check) {
                                                        if (electionPeriod.equals("19")) {
                                                            fraction19 = InstitiutionAttributes.item(m).getTextContent();
                                                        } else if (electionPeriod.equals("20")) {
                                                            fraction20 = InstitiutionAttributes.item(m).getTextContent();
                                                        }
                                                        fractionList.add(InstitiutionAttributes.item(m).getTextContent());
                                                        check = false;

                                                    }
                                                }

                                                WPFractionList.add(fractionList);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                //Here the picture is fetched and the Instance of Person is created
                if ( ! (fraction19 == null && fraction20 == null)){
                if (!(firstName.equals(null)) || !(lastName.equals(null)) || !(id.equals(null))) {
                    String[] pictureArray = PictureScraper.producePictureUrl(firstName, lastName);
                    Person_Impl person = new Person_Impl(id, firstName, lastName, null, title, place, fraction19,
                            fraction20, party, pictureArray, gender, birthDate, deathDate, birthPlace);
                    persons.add(person);
                }}
            }

        } catch (ParserConfigurationException e) {
            e.printStackTrace();

        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();

        } catch (SAXException e) {
            e.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        }
        MongoDBHandler mongoDBHandler = new MongoDBHandler();
        mongoDBHandler.insertPersons(persons);
    }


    /**
     * Method to get the Title and the ID of the Agenda Items.
     *
     * @param headList
     * @param ivzAgendaItems
     * @param ivzAgendaTitle
     * @return
     * @author Julian Ocker
     */
    private static List<ArrayList<String>> getAgendaItem(NodeList headList, ArrayList<String> ivzAgendaItems, ArrayList<String> ivzAgendaTitle) {

        for (int k = 0; k < headList.getLength(); k++) {
            if (headList.item(k).getNodeName().equals("ivz-block")) {
                NodeList contentRegister = headList.item(k).getChildNodes();
                //System.out.println(headList.item(k).getTextContent());
                boolean check = false;
                for (int l = 0; l < contentRegister.getLength(); l++) {
                    if (contentRegister.item(l).getNodeName().equals("ivz-block-titel")) {

                        ivzAgendaItems.add(contentRegister.item(l).getTextContent());
                        check = true;
                    }
                    if (check && contentRegister.item(l).getNodeName().equals("ivz-eintrag")) {

                        ivzAgendaTitle.add(contentRegister.item(l).getTextContent());
                        check = false;
                    }

                }

            }

        }

        return Arrays.asList(ivzAgendaItems, ivzAgendaTitle);
    }


    /**
     * This Method parses a protocol for the Agenda-Item- and Protocol-Data, creates Instances of the AgendaItem_Impl and Protocol_Impl classes
     * and calls the speechParse()-Method
     *
     * @author Julian Ocker
     */
    public void protocolParse(Integer firstProtocol, Integer lastProtocol) throws IOException {
        personParse();

        // declaring the Variables needed
        String beginTime = "";
        String endTime = "";
        String protocolNumber = "";
        String electionPeriod = "";
        String protocolID = "";
        String protocolTitle = "";
        LocalDate date = null;
        ArrayList<String> ivzAgendaItems = new ArrayList<>(0);
        ArrayList<String> ivzAgendaTitle = new ArrayList<>(0);

        System.out.println(XMLParser.class.getClassLoader().getResource("ProtokollXMLs/Protokolle/").getPath());
        String path = XMLParser.class.getClassLoader().getResource("ProtokollXMLs/Protokolle/").getPath();
        for (int out = firstProtocol; out < lastProtocol+1; out++) {

            try {
                File input_file = new File(path + out + ".xml" );
                DocumentBuilderFactory dbfFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dbBuilder = dbfFactory.newDocumentBuilder();
                Document dProtocol = dbBuilder.parse(input_file);
                NodeList testList = dProtocol.getElementsByTagName("dbtplenarprotokoll").item(0).getChildNodes();
                ArrayList agendaItemsStr = new ArrayList<>(0);
                ArrayList<String> sessionLeaders = new ArrayList<>(0);

                for (int i = 0; i < testList.getLength(); i++) {
                    if (testList.item(i).getNodeName().equals("vorspann")) {
                        NodeList preList = testList.item(i).getChildNodes();

                        for (int j = 0; j < preList.getLength(); j++) {
                            if (preList.item(j).getNodeName().equals("kopfdaten")) {
                                NodeList headList = preList.item(j).getChildNodes();

                                for (int k = 0; k < headList.getLength(); k++) {
                                    if (headList.item(k).getNodeName().equals("plenarprotokoll-nummer")) {
                                        NodeList plProtoNoList = headList.item(k).getChildNodes();

                                        for (int l = 0; l < plProtoNoList.getLength(); l++) {
                                            if (plProtoNoList.item(l).getNodeName().equals("wahlperiode")) {
                                                electionPeriod = plProtoNoList.item(l).getTextContent();
                                            }
                                            if (plProtoNoList.item(l).getNodeName().equals("sitzungsnr")) {
                                                protocolNumber = plProtoNoList.item(l).getTextContent();
                                            }
                                        }
                                    }

                                    protocolID = electionPeriod + "/" + protocolNumber;

                                    if (headList.item(k).getNodeName().equals("veranstaltungsdaten")) {
                                        NodeList dateList = headList.item(k).getChildNodes();
                                        for (int l = 0; l < dateList.getLength(); l++) {
                                            if (dateList.item(l).getNodeName().equals("datum")) {

                                                date = TimeHelper.convertToISOdate(
                                                        dateList.item(l).getAttributes().getNamedItem("date").getTextContent());

                                            }
                                        }
                                    }

                                    if (headList.item(k).getNodeName().equals("sitzungstitel")) {
                                        protocolTitle = headList.item(k).getTextContent();

                                    }
                                }
                            }

                            if (preList.item(j).getNodeName().equals("inhaltsverzeichnis")) {
                                NodeList headList = preList.item(j).getChildNodes();

                                List<ArrayList<String>> e = getAgendaItem(headList, ivzAgendaItems, ivzAgendaTitle);
                                ivzAgendaItems = e.get(0);
                                ivzAgendaTitle = e.get(1);

                            }
                        }
                    }

                    if (testList.item(i).getNodeName().equals("sitzungsverlauf")) {
                        NodeList preList = testList.item(i).getChildNodes();

                        for (int j = 0; j < preList.getLength(); j++) {
                            if (preList.item(j).getNodeName().equals("sitzungsbeginn")) {
                                beginTime = preList.item(j).getAttributes().getNamedItem("sitzung-start-uhrzeit").getTextContent();
                            }
                            if (preList.item(j).getNodeName().equals("tagesordnungspunkt")) {

                                //Für Tagesordnunspunkte nötige Variablen werden definiert/ zurückgesetzt
                                ArrayList<String> heldSpeechList = new ArrayList<String>(0);
                                String agendaItemSubID = new String();
                                String agendaItemTitle = "Ich bin ein hässliches Lelek das keiner mag weil ich nicht ordentlich funktioniere";

                                for (int k = 0; k < preList.item(j).getAttributes().getLength(); k++) {

                                    agendaItemSubID = preList.item(j).getAttributes().item(k).getTextContent();

                                    for (int l = 0; l < ivzAgendaItems.size(); l++) {
                                        if (ivzAgendaItems.equals(agendaItemSubID)) {

                                            agendaItemTitle = ivzAgendaTitle.get(l);

                                        }
                                    }
                                    if (agendaItemTitle.equals("Ich bin ein hässliches Lelek das keiner mag weil ich nicht ordentlich funktioniere")) {

                                        agendaItemTitle = agendaItemSubID;

                                    }
                                }
                                NodeList speechesList = preList.item(j).getChildNodes();
                                for (int k = 0; k < speechesList.getLength(); k++) {
                                    if (speechesList.item(k).getNodeName().equals("rede")) {
                                        String speechID = new String();
                                        for (int l = 0; l < speechesList.item(k).getAttributes().getLength(); l++) {
                                            speechID = speechesList.item(k).getAttributes().item(l).getTextContent();
                                            heldSpeechList.add(speechID);
                                        }
                                    }

                                    NodeList speechDetailsList = speechesList.item(k).getChildNodes();
                                    for (int l = 0; l < speechDetailsList.getLength(); l++) {
                                        if (speechDetailsList.item(l).getNodeName().equals("p")) {
                                            if (speechDetailsList.item(l).getNodeName().equals("name")) {

                                                boolean check = true;

                                                for (int m = 0; m < sessionLeaders.size(); m++) {


                                                    if (sessionLeaders.get(m).equals(speechDetailsList.item(l).getTextContent())) {
                                                        check = false;

                                                    }
                                                }

                                                if (check) {
                                                    for (int m = 0; m < persons.size(); m++) {
                                                        if (speechDetailsList.item(l).getTextContent().contains(persons.get(m).getFirstName()) &&
                                                                speechDetailsList.item(l).getTextContent().contains(persons.get(m).getLastName())) {
                                                            sessionLeaders.add(persons.get(m).getID());
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                String agendaItemID = protocolID + agendaItemSubID;

                                // hier wird ein Tagesordnungspunkt abgespeichert
                                AgendaItem_Impl angendaItem = new AgendaItem_Impl(agendaItemID, date, electionPeriod, heldSpeechList);
                                agendaItems.add(angendaItem);
                            }

                            if (preList.item(j).getNodeName().equals("sitzungsende")) {
                                endTime = preList.item(j).getAttributes().getNamedItem("sitzung-ende-uhrzeit").getTextContent();

                            }
                        }
                    }
                }

                // hier wird ein Protokol abgespeichert.
                Protocol_Impl protocol = new Protocol_Impl(protocolID, date, TimeHelper.convertToISOtime(beginTime),
                        TimeHelper.convertToISOtime(endTime), Integer.valueOf(electionPeriod),
                        Integer.valueOf(protocolNumber), sessionLeaders, agendaItemsStr);
                protocols.add(protocol);

            } catch (ParserConfigurationException e) {
                e.printStackTrace();

            } catch (FileNotFoundException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();

            } catch (SAXException e) {
                e.printStackTrace();

            } catch (Exception e) {
                e.printStackTrace();

            }
        }
    }


    /**
     * A helper method to extract all Nodes of a given name from the XML Document which is parsed.
     * It recursively scans the document for all occurrences. It is a modified version of a function by
     * G.Abrami.
     *
     * @param node The Node from which the function starts to recursively scan the xml tree
     * @param name The name of the Nodes that will be extracted from the Sub-Tree
     * @return nodeList  A List of the Nodes with the given name
     * @author DavidJordan, (Giuseppe Abrami)
     */
    public static List<Node> getNodesByName(Node node, String name) {
        // list to store the selected nodes
        List<Node> nodeList = new ArrayList<>(0);
        // add node to list if it has the desired name
        if (node != null && node.getNodeName().equals(name)) {
            nodeList.add(node);
        }
        //check if the node has children. This leads to the subtree of the provided node being searched in the following
        else {
            if (node != null && node.hasChildNodes()) {
                // for each of the children perform the following recursive action
                for (int i = 0; i < node.getChildNodes().getLength(); i++) {
                    // recursively call this function on each child, adding all the accumulating nodes
                    nodeList.addAll(getNodesByName(node.getChildNodes().item(i), name));
                }
            } else {
                if (node != null && node.getNodeName().equals(name)) nodeList.add(node);
            }
        }
        return nodeList;
    }

    /**
     * The complementary helper Method to the getNodesByName Method which calls it in the method body
     * and extracts the first ocurrence in the resulting List of Nodes. If the List of Nodes is empty, it returns
     * null.
     *
     * @param node The root node of the Document from which the subtree is scanned
     * @param name The Node name to scan for
     * @return Node
     * @author DavidJordan, (Giuseppe Abrami)
     */
    public static Node getSingleNode(Node node, String name) {
        //Scan the subtree recursively
        List<Node> list = getNodesByName(node, name);
        if (!list.isEmpty()) {
            // Return the first node if present
            return list.stream().findFirst().get();
        }
        return null;
    }


}


