package data.impl;

import data.Person;
import org.bson.Document;

import java.util.ArrayList;

/**
 * !!Unfinished!!
 * Class that implements the Person interface. It maps all "Abgeordnete" and Persons with
 * an ID appearing in the Stammdaten and Protocols.
 * @author DavidJordan
 * @author Julian Ocker
 */
public class Person_Impl implements Person {

    //The BSON Document retrieved from the Database
    private Document personDoc;

    //Class Variables
    private String _id, firstName, lastName, role, title, fraction19, fraction20, party, place, gender,  birthDate, deathDate, birthPlace;
    private ArrayList picture;

    /**
     * Default Constructor.
     * @author DavidJordan
     * @author Julian Ocker
     */
    public Person_Impl(String id, String firstName, String lastName, String role, String title, String place,String fraction19,
                       String fraction20, String party, String[] pictureArray, String gender, String birthDate, String deathDate, String birthPlace) {
        this._id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.title = title;
        this.fraction19 = fraction19;
        this.fraction20 = fraction20;
        this.party = party;
        this.place = place;
        this.gender = gender;
        this.birthDate = birthDate;
        this.deathDate = deathDate;
        this.birthPlace = birthPlace;
        for (int i = 0; i < pictureArray.length; i++) {
            picture.add(pictureArray[i]);
        }
    }

    /**
     * This is also a working idea for a constructor to generate the object from the DB document.
     * Please everybody inspect these two interpretations of our results yesterday and make the necessary adjustments
     * where necessary. Whoever needs setters for specific variables, please add those and modify the constructor accordingly.
     * @param document The BSON document retrieved from the database.
     * @author DavidJordan
     * @author Julian Ocker
     */
    public Person_Impl(Document document){
        this.personDoc = document;
        this._id = document.getString("_id");
        this.firstName = document.getString("firstName");
        this.lastName = document.getString("lastName");
        this.role = document.getString("role");
        this.title = document.getString("title");
        this.fraction19 = document.getString("fraction19");
        this.fraction20 = document.getString("fraction20");
        this.party = document.getString("party");
        this.place = document.getString("place");
        this.gender = document.getString("gender");
        this.birthDate = document.getString("birthDate");
        this.deathDate = document.getString("deathDate");
        this.birthPlace = document.getString("birthPlace");
        this.picture = document.get("picture", ArrayList.class);

    }

    @Override
    public String getID() {
        return this._id;
    }

    @Override
    public ArrayList<String> getPicture() {
        return this.picture;
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
    public String getFraction19() {
        return this.fraction19;
    }

    public String getFraction20() {
        return this.fraction20;
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


    /**
     * This Method generates a org.bson.Document containing the Data of Person.
     * @return
     * @author Julian Ocker
     */
    public Document getPersonDoc() {
        personDoc = new Document()
                .append("_id", _id)
                .append("firstname",firstName)
                .append("lastname",lastName)
                .append("role",role)
                .append("title",title)
                .append("fraction19", fraction19)
                .append("fraction20", fraction20)
                .append("party",party)
                .append("place", place)
                .append("gender", gender)
                .append("birthDate", birthDate)
                .append("deathDate", deathDate)
                .append("birthPlace", birthPlace)
                .append("picture", picture);

        return personDoc;
    }

}
