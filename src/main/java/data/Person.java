package data;

import java.util.ArrayList;

/**
 * Interface representing all Persons appearing in the Parliament Protocols and the
 * Abgeordneten Stammdaten.
 * @author DavidJordan
 */
public interface Person {

    /**
     * Gets the ID of the Person.
     * @return id
     * @author DavidJordan
     */
    String getID();

    /**
     * Gets a List of the election Periods in which this Person was present.
     * @return electionPeriods
     * @author DavidJordan
     */
    ArrayList<String> getElectionPeriods();

    /**
     * Gets the URL of the Persons Picture:
     * @return pfpURL
     * @author DavidJordan
     */
    String getPfpURL();

    /**
     * Gets the Metadata of the Persons Picture.
     * @return pfpMetadata
     * @author DavidJordan
     */
    String getPfpMetadata();

    /**
     * Gets the Person's role if he/she fills a role.
     * @return role
     * @author DavidJordan
     */
    String getRole();

    /**
     * Gets the title of the Person if he/she has a title.
     * @return title
     * @author DavidJordan
     */
    String getTitle();

    /**
     * Gets the first name of the Person.
     * @return firstName
     * @author DavidJordan
     */
    String getFirstName();

    /**
     * Gets the last name of the Person.
     * @return lastName
     * @author DavidJordan
     */
    String getLastName();

    /**
     * Gets the Bundestags Fraction of the Person.
     * @return fraction
     */
    String getFraction();

    /**
     * Gets the Party the Person belongs to.
     * @return party
     */
    String getParty();

    /**
     * Gets the Person's place of residence.
     * @return place
     * @author DavidJordan
     */
    String getPlace();

    /**
     * Gets the Person's gender.
     * @return gender
     * @author DavidJordan
     */
    String getGender();

    /**
     * Gets the person's birthdate in String format.
     * @return birthDate
     * @author DavidJordan
     */
    String getBirthDate();

    /**
     * Gets the person's death Date in string format.
     * @return deathDate
     * @author DavidJordan
     */
    String getDeathDate();

    /**
     * Gets the person's birthplace.
     * @return birthPlace
     * @author DavidJordan
     */
    String getBirthPlace();

}
