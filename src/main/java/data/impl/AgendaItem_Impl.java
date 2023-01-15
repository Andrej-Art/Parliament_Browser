package data.impl;

import data.AgendaItem;
import org.bson.Document;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

/**
 * Class that implements the AgendaItem interface. It stores the speechIDs of all speeches in the AgendaItem.
 * It has a unique id  which is made up of the concatenation of  ProtocolId + "/" + AgendaItemID.
 * @author DavidJordan
 */
public class AgendaItem_Impl implements AgendaItem {

    private Document AgendaItemDoc;
    private String _id, subject;

    private LocalDate date;
    private ArrayList<String> speechIDs;

    /**
     * Full {@code AgendaItem} constructor which fills the class variables from parameters provided.
     * @param id  The AgendaItem's unique  id made up of the concatenation of  ProtocolId + "/" + AgendaItemID
     * @param date The date of the Item
     * @param subject The subject-matter of the Item
     * @param speechIDs A List of all Ids of the speeches in the Item
     * @author DavidJordan
     */
    public AgendaItem_Impl(String id, LocalDate date, String subject, ArrayList<String> speechIDs){
        this._id = id;
        this.date = date;
        this.subject = subject; //Unclear if we get this from the xml protocol or the UIMA analysis, we assume here that we parse it from the xml file
        this.speechIDs = speechIDs;
    }

    /**
     * Full {@code AgendaItem} constructor which fills the class variables from the objects BSON Document.
     * @param document  The BSON Document from the Database.
     * @author DavidJordan
     */
    public AgendaItem_Impl(Document document){
        this.AgendaItemDoc = document;
        this._id = document.getString("_id");
        this.date = (LocalDate) document.get("date");
        this.subject = document.getString("subject");
        this.speechIDs = (ArrayList<String>) document.get("speechIDs");
    }



    @Override
    public String getID() {
        return this._id;
    }

    @Override
    public LocalDate getDate() {
        return this.date;
    }

    @Override
    public ArrayList<String> getSpeechIDs() {
        return this.speechIDs;
    }

    @Override
    public String getSubject() {
        return this.subject;
    }
}
