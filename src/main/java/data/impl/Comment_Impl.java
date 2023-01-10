package data.impl;

import data.Comment;
import org.bson.Document;

import java.util.ArrayList;

/**
 * Class that implement the Comment interface. Provides different constructors to enable
 * generation of the data from both an XML or from a BSON document.
 * @author DavidJordan
 */
public class Comment_Impl implements Comment {

    // The BSON Document
    private Document commentDoc;

    private String id, speechID, speakerID, commentator, text, date;

    private ArrayList<String> fractions;

    /**
     * Full {@code Comment} constructor to be used when data is parsed from a Protocol document and entered.
     * @param id the ID of the Comment
     * @param speechID the ID of the Speech
     * @param speakerID the ID of the Speaker receiving the comment
     * @param commentator the name of the commentator
     * @param text the raw text of the comment
     * @param date  the date when the comment was made
     * @param fractions the fractions involved in making the comment
     * @author DavidJordan
     */
    public Comment_Impl(String id, String speechID, String speakerID, String commentator, String text, String date, ArrayList<String> fractions){
        this.id = id;
        this.speechID = speechID;
        this.speakerID = speakerID;
        this.commentator = commentator;
        this.text = text;
        this.date = date;
        this.fractions = fractions;
    }

    /**
     * Full {@code Comment} constructor which uses a BSON document to load the data for the variables.
     * @param document The BSON Document retrieved from the Database
     * @author DavidJordan
     */
    public Comment_Impl(Document document){
        this.commentDoc = document;
        this.id = document.getString("_id");
        this.speechID = document.getString("speechID");
        this.speakerID = document.getString("speakerID");
        this.commentator = document.getString("commentator");
        this.text = document.getString("text");
        this.date = document.getString("date");
        this.fractions = (ArrayList<String>) document.get("fractions");
    }


    @Override
    public String getID() {
        return this.id;
    }

    @Override
    public String getSpeechID() {
        return this.speechID;
    }

    @Override
    public String getSpeakerID() {
        return this.speakerID;
    }

    @Override
    public String getCommentator() {
        return this.commentator;
    }

    @Override
    public ArrayList<String> getFractions() {
        return this.fractions;
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
