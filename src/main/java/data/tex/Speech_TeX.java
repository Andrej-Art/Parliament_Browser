package data.tex;

import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONObject;
import utility.MongoDBHandler;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Aggregates.limit;

/**
 * The {@code Speech_TeX} class.
 * @author DavidJordan
 * @author Edvin Nise
 * @author Eric Lakhter
 */
public class Speech_TeX {


    private static MongoDBHandler mdbh;

    /**
     * Constructor
     * @param mongoDBHandler
     */
    public Speech_TeX(MongoDBHandler mongoDBHandler) {
        mdbh = mongoDBHandler;
    }



    /**
     * Builds a string which can be formatted by a TeX compiler.<br>
     * TeX command looks like this:<br>
     *
     * @param speechID the speechID to texify.
     * @return String in TeX format.
     * @author Eric Lakhter
     * @author DavidJordan
     * @author Edvin Nise
     */
    public String speechToTex(String speechID) {


        Document speechDoc = mdbh.getDocument("speech", speechID);
        StringBuilder speechEditorBuilder = new StringBuilder(speechDoc.getString("text"));

        MongoCursor<Document> commentCursor = mdbh.getDB().getCollection("comment").find(new Document("speechID", speechID)).iterator();
        Document commentDoc = commentCursor.tryNext();
        int offSet = 0;
        int previousPos = 0;
        int currentPos = 0;

        String speechEditorText = speechDoc.getString("text");

        while (commentDoc != null) {
            currentPos = commentDoc.getInteger("commentPos");

            if (previousPos > currentPos || speechEditorText.length() < currentPos + offSet) break;

            String commentText = commentDoc.getString("text");
            speechEditorBuilder.insert(commentDoc.getInteger("commentPos") + offSet, "\n\n[KOMMENTAR]" + commentText + "\n");
            previousPos = currentPos;
            offSet += commentText.length() + 13;
            commentDoc = commentCursor.tryNext();
        }
        return speechEditorBuilder.toString();
    }
}
