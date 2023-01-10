package utility;


import jdk.internal.org.xml.sax.SAXException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Class to analyze the XML files and create objects
 * @author Andrej Artuschenko
 * @author Julian Ocker
 */
public class XMLParser {


    /**
     * !!Work in Progress!!
     *
     * This Funktion
     *
     * @param source
     * @param target
     * @param data_pack
     * @return
     * @author Julian Ocker
     */
    private static Data_Kraken deputyParse(String source, String target, Data_Kraken data_pack) {

        ArrayList<String> allFractions = new ArrayList<>(0);
        try {
            //gett the File and make it accessible
            File input_file = new File(source + "" + target);
            DocumentBuilderFactory dbfFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dbBuilder = dbfFactory.newDocumentBuilder();
            Document dProtocol = dbBuilder.parse(input_file);
            NodeList testList = dProtocol.getElementsByTagName("MDB");
            for (int h = 0; h < testList.getLength(); h++) {

                //declaring Variables that should remain unchanged throughout one Iteration
                NodeList attributes = testList.item(h).getChildNodes();
                String deputyID = null;
                String deputyFirstName = null;
                String deputyLastName = null;
                String deputyTitle = null;
                String party = null;
                String deputyAkadTitle = null;
                String deputyPoliticStart = null;
                String deputyPoliticEnd = null;
                String birthdate = null;
                String birthplace = null;
                String birthnation = null;
                String deathdate = null;
                String sex = null;
                String familyFactor = null;
                String religion = null;
                String vita = null;
                String job = null;
                String publicInterest = null;
                ArrayList<String> WPList = new ArrayList<>(0);
                ArrayList<String> fractionList = new ArrayList<>(0);
                ArrayList<ArrayList<String>> WPFractionList = new ArrayList<>(0);

                // pathing to get the personal data of a represantative
                for (int i = 0; i < attributes.getLength(); i++) {
                    if (attributes.item(i).getNodeName().equals("ID")) {
                        deputyID = attributes.item(i).getTextContent();
                    }
                    if (attributes.item(i).getNodeName().equals("NAMEN")) {
                        NodeList namesAttributes = attributes.item(i).getChildNodes();
                        for (int j = 0; j < namesAttributes.getLength(); j++) {

                            if (namesAttributes.item(j).getNodeName().equals("NAME")) {
                                NodeList nameAttributes = namesAttributes.item(j).getChildNodes();
                                for (int k = 0; k < nameAttributes.getLength(); k++) {

                                    if (nameAttributes.item(k).getNodeName().equals("VORNAME")) {
                                        deputyFirstName = nameAttributes.item(k).getTextContent();
                                    }
                                    if (nameAttributes.item(k).getNodeName().equals("NACHNAME")) {
                                        deputyLastName = nameAttributes.item(k).getTextContent();
                                    }
                                    if (nameAttributes.item(k).getNodeName().equals("ANREDE_TITEL")) {
                                        deputyTitle = nameAttributes.item(k).getTextContent();
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
                    if (attributes.item(i).getNodeName().equals("BIOGRAFISCHE_ANGABEN")) {
                        NodeList biographicAttributes = attributes.item(i).getChildNodes();
                        for (int j = 0; j < biographicAttributes.getLength(); j++) {
                            if (biographicAttributes.item(j).getNodeName().equals("GEBURTSDATUM")) {

                                birthdate = biographicAttributes.item(j).getTextContent();

                            }
                            if (biographicAttributes.item(j).getNodeName().equals("GEBURTSORT")) {

                                birthplace = biographicAttributes.item(j).getTextContent();

                            }
                            if (biographicAttributes.item(j).getNodeName().equals("GEBURTSLAND")) {

                                birthnation = biographicAttributes.item(j).getTextContent();

                            }
                            if (biographicAttributes.item(j).getNodeName().equals("STERBEDATUM")) {

                                deathdate = biographicAttributes.item(j).getTextContent();

                            }
                            if (biographicAttributes.item(j).getNodeName().equals("GESCHLECHT")) {

                                sex = biographicAttributes.item(j).getTextContent();

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

                    if (attributes.item(i).getNodeName().equals("WAHLPERIODEN")) {
                        NodeList periodsAttributes = attributes.item(i).getChildNodes();

                        for (int j = 0; j < periodsAttributes.getLength(); j++) {
                            if (periodsAttributes.item(j).getNodeName().equals("WAHLPERIODE")) {
                                NodeList periodAttributes = periodsAttributes.item(j).getChildNodes();

                                for (int k = 0; k < periodAttributes.getLength(); k++) {
                                    if (periodAttributes.item(k).getNodeName().equals("WP")) {

                                        WPList.add(periodAttributes.item(k).getTextContent());
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

                // combining the extracted information to initialize a parliamentary represantative and Fraction
                for (int i = 0; i < WPList.size(); i++) {

                    if (WPList.get(i).equals("19")) {
                        ArrayList<String> funfacts = new ArrayList<>(0);
                        funfacts.add(deputyAkadTitle);
                        funfacts.add(deputyPoliticStart);
                        funfacts.add(deputyPoliticEnd);
                        funfacts.add(birthdate);
                        funfacts.add(birthplace);
                        funfacts.add(birthnation);
                        funfacts.add(deathdate);
                        funfacts.add(sex);
                        funfacts.add(familyFactor);
                        funfacts.add(religion);
                        funfacts.add(vita);
                        funfacts.add(job);
                        funfacts.add(publicInterest);

                        if(!(deputyFirstName.equals(null)) || !(deputyLastName.equals(null)) || !(deputyID.equals(null)) )
                            data_pack.addDeputy(deputyFirstName, deputyLastName, deputyTitle, deputyID, WPFractionList.get(i).get(i), funfacts);
                        data_pack.addPartyToSpeaker(deputyID, party);
                        boolean check = true;
                        // creating the fraction if it doesn't exist
                        for (int j = 0; j < allFractions.size(); j++) {
                            if(allFractions.get(j).equals(WPFractionList.get(i).get(i))){
                                check = false;
                            }
                        }
                        if(check){
                            allFractions.add(WPFractionList.get(i).get(i));
                            data_pack.addFraction(WPFractionList.get(i).get(i));
                        }

                        data_pack.addPartyToFraction(WPFractionList.get(i).get(i), party);

                    }

                }

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
        return data_pack;
    }

}


