package data;

import java.util.ArrayList;
import java.util.List;

/**
 * Interface representing all Persons appearing in the Parliament Protocols and the
 * Abgeordneten Stammdaten.
 * @author DavidJordan
 * @author Julian Ocker
 */
public interface Person {

    /**
     * Gets the ID of the Person.
     * @return id
     * @author DavidJordan
     */
    String getID();

    /**
     * Gets a List of the election picture data in which this Person was present.
     *
     * @return electionPeriods
     * @author DavidJordan
     */
    List<String> getPicture();


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
     * Gets the full name of the Person.
     * @return fullName
     * @author DavidJordan
     */
    String getFullName();

    /**
     * Gets the Bundestags Fraction of the Person in the Election Period 20.
     * @return fraction
     * @author Julian Ocker
     */
    String getFraction20();


    /**
     * Gets the Bundestags Fraction of the Person in the Election Period 19.
     * @return fraction
     * @author Julian Ocker
     */
    String getFraction19();

    /**
     * Gets the Party the Person belongs to.
     * @return party
     * @author DavidJordan
     */
    String getParty();

    /**
     * Gets the Person's place of residence. The "Ortszusatz"
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
