package utility.webservice;

import org.bson.Document;

/**
 * This class is meant to make the editing of Users Easier
 * @author Julian Ocker
 */
public class User {
    private String id;
    private String rank;

    /**
     * This method constructs an object of the class User.
     * @param document
     * @author JulianOcker
     */
    public User(Document document){
        this.id = document.getString("_id");
        this.rank = document.getString("rank");
    }

    /**
     * This method returns the ID of an object of this class.
     * @return
     */
    public String getID(){
        return this.id;
    }

    /**
     * This method returns the Rank of an object of this class.
     * @return
     */
    public String getRank(){
        return this.rank;
    }
}
