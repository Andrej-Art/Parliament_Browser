package data.tex;

import com.mongodb.DBCursor;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONObject;
import utility.MongoDBHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
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

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.####");

    /**
     * Builds a string which can be formatted by a TeX compiler.<br>
     * TeX command looks like this:<br>
     * {@code \speech[showNamedEntities=true,showSentiment=true,showComments=true]}
     *
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


    public String testTex() {

        Bson limit = limit(10);
        MongoCursor<Document> protocolCursor = mdbh.getDB().getCollection("protocol").find().iterator();
        Document protocolDoc = protocolCursor.tryNext();
        if (protocolDoc == null) return "";

        String texDocFinal = "\\documentclass[a4paper,11pt,twocolumn]{scrartcl}\n" +
                "\\usepackage[ngerman,shorthands=off]{babel}\n" +
                "\\usepackage[utf8]{inputenc}\n" +
                "\\usepackage[T1]{fontenc}\n" +
                "\\usepackage{tikz}\n" +
                "\\usepackage{enumitem}\n" +
                "\\usepackage{graphicx}\n" +
                "\\usepackage{hyperref}\n" +
                "\\usepackage{subfig}\n" +
                "\\usepackage[capitalize]{cleveref}\n" +
                "\\usepackage{makecell}\n" +
                "\\usepackage{pgf-pie}\n" +
                "\\begin{document}\n" +
                "\\maketitle\n";

        StringBuilder texDocFinalBuilder = new StringBuilder();
        texDocFinalBuilder.append(texDocFinal);


        System.out.println(texDocFinalBuilder);
        return (texDocFinalBuilder.toString());
    }

    /**
     * converts LaTeX source file to .pdf
     *
     * @author Edvin Nise
     */
    public void pdfTest() throws IOException, InterruptedException {
////// Create load options for LaTeX file
//        TeXLoadOptions options = new TeXLoadOptions();
//
//// Create Document object to load the LaTeX file
//        com.aspose.pdf.Document document = new com.aspose.pdf.Document("testPDF3.tex", options);
//
//// Save output PDF document

    }

    /**
     * returns a table with one cell for each NamedEntity
     *
     * @param speechID
     * @return String
     * @author Edvin Nise
     */
    public String nlpTableTex(String speechID) {
        Document speechDoc = mdbh.getDB().getCollection("speech").find(new Document("_id", speechID)).iterator().tryNext();
        if (speechDoc == null) return "";
        StringBuilder sb = new StringBuilder();
        Double sentiment = speechDoc.getDouble("sentiment");
        sb.append("\\textbf{Rede Sentimentvalue:} ").append(DECIMAL_FORMAT.format(sentiment)).append("\n");
        sb.append("\\begin{table}[h!]\n" +
                "\\centering\n" +
                "\\begin{tabular}{||c | c | c||}\n" +
                "\\hline\n" +
                "PersonEntities & OrgEntities & LocEntities \\\\ [0.5ex]\n" +
                "\\hline\\hline\n");

        List<Document> perDoc = (List<Document>) speechDoc.get("namedEntitiesPer");
        List<Document> orgDoc = (List<Document>) speechDoc.get("namedEntitiesOrg");
        List<Document> locDoc = (List<Document>) speechDoc.get("namedEntitiesLoc");

        sb.append("\\makecell{");
        for (Document docPer : perDoc) {
            sb.append(docPer.getString("coveredText")).append(" \\\\\n");
        }
        sb.append("} &\n" +
                "\\makecell{");

        for (Document docOrg : orgDoc) {
            sb.append(docOrg.getString("coveredText")).append(" \\\\\n");
        }
        sb.append("} &\n" +
                "\\makecell{");
        for (Document docLoc : locDoc) {
            sb.append(docLoc.getString("coveredText")).append(" \\\\\n");
        }
        sb.append("}\\\\\n" +
                "\\hline");
        sb.append("\\end{tabular}\n" +
                "\\end{table}");
        System.out.println(sb);
        return sb.toString();
    }

    /**
     * returns a piechart for each party and for the total poll result in latex code
     *
     * @param date
     * @return String
     * @author Edvin Nise
     */
    public String pollResults(String date) {
        Bson match = new Document("$match", new Document("date", date));
        ArrayList<JSONObject> pollCursor = mdbh.getPollResults(date, date, "", "", "");
        StringBuilder sb = new StringBuilder();
        String[] pollFractionsList = {"SPD", "LINKE", "B90", "independent", "FDP", "CxU", "AfD"}; //AfD fehlt

        for (JSONObject json : pollCursor) {
            for (String s : pollFractionsList) {
                sb.append("\\begin{tikzpicture}\n" +
                        "\\pie{");
                sb.append(DECIMAL_FORMAT.format((json.getDouble(s + "Yes") / json.getDouble(s + "totalVotes")) * 100)).append("/" + s + "Yes,\n");
                sb.append(DECIMAL_FORMAT.format((json.getDouble(s + "No") / json.getDouble(s + "totalVotes")) * 100)).append("/" + s + "No,\n");
                sb.append(DECIMAL_FORMAT.format((json.getDouble(s + "Abstained") / json.getDouble(s + "totalVotes")) * 100)).append("/" + s + "Abstained,\n");
                sb.append(DECIMAL_FORMAT.format((json.getDouble(s + "NoVotes") / json.getDouble(s + "totalVotes")) * 100)).append("/" + s + "Votes}\n");
                sb.append("\\end{tikzpicture}\n");
            }
            sb.append("\\begin{tikzpicture}\n" +
                    "\\pie{");
            sb.append(DECIMAL_FORMAT.format((json.getDouble("totalYes") / json.getDouble("totalVotes")) * 100)).append("/totalYes,\n");
            sb.append(DECIMAL_FORMAT.format((json.getDouble("totalNo") / json.getDouble("totalVotes")) * 100)).append("/totalNo,\n");
            sb.append(DECIMAL_FORMAT.format((json.getDouble("totalAbstained") / json.getDouble("totalVotes")) * 100)).append("/totalAbstained,\n");
            sb.append(DECIMAL_FORMAT.format((json.getDouble("totalNoVotes") / json.getDouble("totalVotes")) * 100)).append("/totalNoVotes}\n");
            sb.append("\\end{tikzpicture}\n");
        }

        System.out.println(sb);
        return sb.toString();
    }

    public String agendaItems(String protocol) {
        Document protocolDoc =  mdbh.getDB().getCollection("protocol").find(new Document("_id", protocol)).iterator().tryNext();
        StringBuilder sb = new StringBuilder();
        sb.append("\\tableofcontents\n");
        ArrayList<String> agendaItemsList = (ArrayList<String>) protocolDoc.get("agendaItems");
        for (String s : agendaItemsList) {
            sb.append("\\section{" + s + "}\n");
        }
        System.out.println(sb);
        return sb.toString();
    }
}
