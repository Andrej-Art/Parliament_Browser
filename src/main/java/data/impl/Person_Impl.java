package data.impl;

import data.Person;
import org.bson.Document;

import java.util.ArrayList;

/**
 * !!Unfinished!!
 * Class that implements the Person interface. It maps all "Abgeordnete" and Persons with
 * an ID appearing in the Stammdaten and Protocols.
 * @author DavidJordan
 */
public class Person_Impl implements Person {

    //The BSON Document retrieved from the Database
    private Document personDoc;

    //Class Variables
    private String id, firstName, lastName, role, title, fraction, party, place, pfpURL, pfpMetadata, gender,  birthDate, deathDate, birthPlace;
    private ArrayList<String> electionPeriods;

    /**
     * Default Constructor.
     * @author DavidJordan
     */
    public Person_Impl() {
    }

    /**
     * !!Unfinished!!!
     * !!Important!!  This is only a working idea for a constructor that takes all of the required variables.
     *  We will have to improve this depending on how the Parser and the Scraper (pfpData!) gets the data from the Stammdaten .  If the parser only gets the
     *  data from the Stammdaten XML for the Person class then this could work.  We should all find a solution together that works.
     *  We will probably have to add optional setters.
     * @param id
     * @param firstName
     * @param lastName
     * @param role
     * @param title
     * @param fraction
     * @param party
     * @param place
     * @param pfpURL
     * @param pfpMetadata
     * @param gender
     * @param birthDate
     * @param deathDate
     * @param birthPlace
     * @param electionPeriods
     * @author DavidJordan
     */
    public Person_Impl(String id, String firstName, String lastName, String role, String title, String fraction, String party, String place,
                       String pfpURL, String pfpMetadata, String gender, String birthDate, String deathDate, String birthPlace, ArrayList<String> electionPeriods) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.title = title;
        this.fraction = fraction;
        this.party = party;
        this.place = place;
        this.pfpURL = pfpURL;
        this.pfpMetadata = pfpMetadata;
        this.gender = gender;
        this.birthDate = birthDate;
        this.deathDate = deathDate;
        this.birthPlace = birthPlace;
        this.electionPeriods = electionPeriods;

    }

    /**
     * !!Unfinished!!!
     * !!Important!!  This is also a working idea for a constructor to generate the object from the DB document.
     * Please everybody inspect these two interpretations of our results yesterday and make the necessary adjustments
     * where necessary. I think since we all work on these fundamentally important designs we should all be authors?
     * @param document
     * @author DavidJordan
     */
    public Person_Impl(Document document){
        this.personDoc = document;
        this.id = document.getString("_id");
        this.firstName = document.getString("firstname");
        this.lastName = document.getString("lastName");
        this.role = document.getString("role");
        this.title = document.getString("title");
        this.fraction = document.getString("fraction");
        this.party = document.getString("party");
        this.place = document.getString("place");
        this.pfpURL = document.getString("pfpURL");
        this.pfpMetadata = document.getString("pfpMetadata");
        this.gender = document.getString("gender");
        this.birthDate = document.getString("birthDate");
        this.deathDate = document.getString("deathDate");
        this.birthPlace = document.getString("birthPlace");
        this.electionPeriods = (ArrayList<String>) document.get("electionPeriods");

    }





    @Override
    public String getID() {
        return this.id;
    }

    @Override
    public ArrayList<String> getElectionPeriods() {
        return this.electionPeriods;
    }

    @Override
    public String getPfpURL() {
        return this.pfpURL;
    }

    @Override
    public String getPfpMetadata() {
        return this.pfpMetadata;
    }

    @Override
    public String getRole() {
        return this.role;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public String getFirstName() {
        return this.firstName;
    }

    @Override
    public String getLastName() {
        return this.lastName;
    }

    @Override
    public String getFraction() {
        return this.fraction;
    }

    @Override
    public String getParty() {
        return this.party;
    }

    @Override
    public String getPlace() {
        return this.place;
    }

    @Override
    public String getGender() {
        return this.gender;
    }

    @Override
    public String getBirthDate() {
        return this.birthDate;
    }

    @Override
    public String getDeathDate() {
        return this.deathDate;
    }

    @Override
    public String getBirthPlace() {
        return this.birthPlace;
    }
}
