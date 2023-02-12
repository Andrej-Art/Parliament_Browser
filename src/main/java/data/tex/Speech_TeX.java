package data.tex;

import com.mongodb.DBCursor;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.bson.conversions.Bson;
import utility.MongoDBHandler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.mongodb.client.model.Aggregates.limit;
import static java.util.Arrays.asList;

/**
 * The {@code Speech_TeX} class.
 *
 * @author Eric Lakhter
 */
public class Speech_TeX {
    private static MongoDBHandler mdbh;
    public Speech_TeX(MongoDBHandler mongoDBHandler) {
        mdbh = mongoDBHandler;
    }

    /**
     * Builds a string which can be formatted by a TeX compiler.<br>
     * TeX command looks like this:<br>
     * {@code \speech[showNamedEntities=true,showSentiment=true,showComments=true]}
     * @param speechID the speechID to texify.
     * @return String in TeX format.
     * @author Eric Lakhter
     */
    public String toTeX(String speechID) {


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

//        System.out.println(speechEditorBuilder);
////------------------------------------------------
//        MongoCursor<Document> speechCursor = mdbh.getDB().getCollection("speech").find(new Document("_id", speechID)).iterator();
//        Document speechDoc = speechCursor.tryNext();
//        if (speechDoc == null) return "";
//
//

//        StringBuilder speechTeX = new StringBuilder();


//        String speechText = speechDocument.getString("text");
//        String speechWithBreaks = speechText.replace(". ", ". \n");
//        speechTeX.append(speechWithBreaks);

//        Document commentDoc = commentCursor.tryNext();
//
//        List<String> textArray = new ArrayList<>(asList(speechDoc.getString("text").split("")));
//        textArray.add("");
//        Iterator<String> textIter = textArray.iterator();
//
//
////        List<MongoSentence> sentences = new ArrayList<>(0);
////        for (Document doc : (ArrayList<Document>) speechDoc.get("sentences")) {
////            sentences.add(new MongoSentence(doc.getInteger("startPos"), doc.getInteger("endPos"), doc.getDouble("sentiment")));
////        }
//
////        int perIndex = 0;
////        int orgIndex = 0;
////        int locIndex = 0;
////        int sentenceIndex = 0;
//        for (int i = 0; textIter.hasNext(); i++) {
////            if (sentenceIndex < sentences.size() && sentences.get(sentenceIndex).getEndPos() == i) {
////                speechTeX.append(sentences.get(sentenceIndex).getSentiment());
////                sentenceIndex++;
////            }
//            if (commentDoc != null && commentDoc.getInteger("commentPos") == i) {
//                speechTeX.append("\n\n\\textcolor{green}{");
//
//
//                if (!commentDoc.getString("commentatorID").equals("")) {
////                    mdbh.pictureURL(commentDoc.getString("commentatorID"));
//                }
//                speechTeX.append(commentDoc.getString("text"));
//                speechTeX.append("}\n\n");
//                commentCursor.tryNext();
//            }
//
//            speechTeX.append(textIter.next());
//        }
//
//        return speechTeX.toString();
//    }
//}
