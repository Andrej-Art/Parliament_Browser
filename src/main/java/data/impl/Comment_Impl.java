package data.impl;

import data.Comment;
import org.bson.Document;
import utility.TimeHelper;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;

/**
 * Class that implement the Comment interface. Provides different constructors to enable
 * generation of the data from both an XML or from a BSON document.
 * @author DavidJordan
 */
public class Comment_Impl implements Comment {

    // The BSON Document
    private Document commentDoc;

    private int commentPosition;
    private String _id, speechID, speakerID, commentatorID, text;
    private LocalDate date;

    private ArrayList<String> fractions;

    /**
     * Full {@code Comment} constructor to be used when data is parsed from a Protocol document and entered.
     * @param id the ID of the Comment
     * @param speechID the ID of the Speech
     * @param speakerID the ID of the Speaker receiving the comment
     * @param commentatorID the ID of the commentator
     * @param commentPosition the position of the comment in its speech
     * @param text the raw text of the comment
     * @param date  the LocalDate when the comment was made
     * @param fractions the list of fractions involved in making the comment
     * @author DavidJordan
     */
    public Comment_Impl(String id, String speechID, String speakerID, String commentatorID, int commentPosition, String text, LocalDate date, ArrayList<String> fractions){
        this._id = id;
        this.speechID = speechID;
        this.speakerID = speakerID;
        this.commentatorID = commentatorID;
        this.text = text;
        this.date = date;
        this.fractions = fractions;
        this.commentPosition = commentPosition;
    }

    public Comment_Impl(String id, String speechID, String speakerID, String text, LocalDate date){
        this._id = id;
        this.speechID = speechID;
        this.speakerID = speakerID;
        this.text = text;
        this.date = date;
    }

    /**
     * Full {@code Comment} constructor which uses a BSON document to load the data for the variables.
     * @param document The BSON Document retrieved from the Database
     * @author DavidJordan
     */
    public Comment_Impl(Document document){
        this.commentDoc = document;
        this._id = document.getString("_id");
        this.speechID = document.getString("speechID");
        this.speakerID = document.getString("speakerID");
        this.commentatorID = document.getString("commentator");
        this.text = document.getString("text");
        this.date = TimeHelper.dateToLocalDate(document.getDate("date"));
        this.fractions = (ArrayList<String>) document.get("fractions");
        this.commentPosition = document.getInteger("commentPosition");
    }


    @Override
    public String getID() {
        return this._id;
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
    public String getCommentatorID() {
        return this.commentatorID;
    }

    @Override
    public int getCommentPosition() {
        return commentPosition;
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
    public LocalDate getDate() {
        return this.date;
    }
}
