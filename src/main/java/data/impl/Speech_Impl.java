package data.impl;

import data.Speech;
import org.bson.Document;
//import org.w3c.dom.Document;

/**
 * Class that implements the Speech interface. Provides different constructors to enable
 * generation of the data from both an XML or from a BSON document.
 * @author DavidJordan
 */
public class Speech_Impl implements Speech {

    private Document speechDoc;
    private String id, speakerID, text, date;

    /**
     * Full {@code Speech} Constructor to be used when parsed data is entered.
     * @param id The
     * @param speakerID  The "RednerID" of the Speaker
     * @param text The raw Speech text
     * @param date The String value of the Date of the Speech.
     * @author DavidJordan
     */
    public Speech_Impl(String id, String speakerID, String text, String date){
        this.id = id;
        this.speakerID = speakerID;
        this.text = text;
        this.date = date;
    }

    /**
     * Full {@code Speech} Constructor to be used when the object is generated from a given BSON document.
     * @param document
     * @author DavidJordan
     */
    public Speech_Impl(Document document){
        this.speechDoc = document;
        this.id = document.getString("_id");
        this.speakerID = document.getString("speakerID");
        this.text = document.getString("text");
        this.date = document.getString("date");
    }

    @Override
    public String getID() {
        return this.id;
    }

    @Override
    public String getSpeakerID() {
        return this.speakerID;
    }

    @Override
    public String getText() {
        return this.text;
    }

    @Override
    public String getDate() {
        return this.date;
    }
}
