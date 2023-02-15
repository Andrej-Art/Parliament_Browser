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
        sb.append("\\textbf{Sentimentvalue : " + speechDoc.getDouble("sentiment") + "}\n" +
                "\\vspace*{1cm}\n");
        sb.append("\n\\begin{table}[ht]\n" +
                "\\centering\n" +
                "\\begin{tabular}{||c | c | c||}\n" +
                "\\hline\n" +
                "PersonEntities & OrgEntities & LocEntities \\\\ \n\n" +
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
                "\\hline\n");
        sb.append("\\end{tabular}\n" +
                "\\end{table}\n");
        sb.append("\\clearpage\n");
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
        String[] pollFractionsList = {"SPD", "LINKE", "B90", "FDP", "CxU", "AfD", "independent"};


        for (JSONObject json : pollCursor) {
            Integer i = 0;
            sb.append("\n\\begin{figure}[ht]\n" +
                    "\\centering\n" +
                    "\\caption{Abstimmung zu: " + json.getString("topic") + "}\n\n");

            for (String s : pollFractionsList) {
                switch (i % 3) {
                    case 0:
                        if (i == 6) {
                            sb.append("\\begin{tikzpicture}\n" +
                                    "\\centering\n" +
                                    "\\pie[scale=0.45]%\n{");
                            sb.append(DECIMAL_FORMAT.format((json.getDouble(s + "Yes") / json.getDouble(s + "totalVotes")) * 100)).append("/" + s + "Yes,\n");
                            sb.append(DECIMAL_FORMAT.format((json.getDouble(s + "No") / json.getDouble(s + "totalVotes")) * 100)).append("/" + s + "No,\n");
                            sb.append(DECIMAL_FORMAT.format((json.getDouble(s + "Abstained") / json.getDouble(s + "totalVotes")) * 100)).append("/" + s + "Abstained,\n");
                            sb.append(DECIMAL_FORMAT.format((json.getDouble(s + "NoVotes") / json.getDouble(s + "totalVotes")) * 100)).append("/" + s + "Votes}\n");
                            break;
                        }
                        sb.append("\\begin{tikzpicture}\n" +
                                "\\pie[scale=0.45]%\n{");
                        sb.append(DECIMAL_FORMAT.format((json.getDouble(s + "Yes") / json.getDouble(s + "totalVotes")) * 100)).append("/" + s + "Yes,\n");
                        sb.append(DECIMAL_FORMAT.format((json.getDouble(s + "No") / json.getDouble(s + "totalVotes")) * 100)).append("/" + s + "No,\n");
                        sb.append(DECIMAL_FORMAT.format((json.getDouble(s + "Abstained") / json.getDouble(s + "totalVotes")) * 100)).append("/" + s + "Abstained,\n");
                        sb.append(DECIMAL_FORMAT.format((json.getDouble(s + "NoVotes") / json.getDouble(s + "totalVotes")) * 100)).append("/" + s + "Votes}\n");
                        break;


                    case 1:
                        sb.append("\\pie[xshift=5cm, scale=0.45]%\n{");
                        sb.append(DECIMAL_FORMAT.format((json.getDouble(s + "Yes") / json.getDouble(s + "totalVotes")) * 100)).append("/" + s + "Yes,\n");
                        sb.append(DECIMAL_FORMAT.format((json.getDouble(s + "No") / json.getDouble(s + "totalVotes")) * 100)).append("/" + s + "No,\n");
                        sb.append(DECIMAL_FORMAT.format((json.getDouble(s + "Abstained") / json.getDouble(s + "totalVotes")) * 100)).append("/" + s + "Abstained,\n");
                        sb.append(DECIMAL_FORMAT.format((json.getDouble(s + "NoVotes") / json.getDouble(s + "totalVotes")) * 100)).append("/" + s + "Votes}\n");
                        break;


                    case 2:
                        sb.append("\\pie[xshift=10cm, scale=0.45]%\n{");
                        sb.append(DECIMAL_FORMAT.format((json.getDouble(s + "Yes") / json.getDouble(s + "totalVotes")) * 100)).append("/" + s + "Yes,\n");
                        sb.append(DECIMAL_FORMAT.format((json.getDouble(s + "No") / json.getDouble(s + "totalVotes")) * 100)).append("/" + s + "No,\n");
                        sb.append(DECIMAL_FORMAT.format((json.getDouble(s + "Abstained") / json.getDouble(s + "totalVotes")) * 100)).append("/" + s + "Abstained,\n");
                        sb.append(DECIMAL_FORMAT.format((json.getDouble(s + "NoVotes") / json.getDouble(s + "totalVotes")) * 100)).append("/" + s + "Votes}\n");
                        sb.append("\\end{tikzpicture}\n\n").append("\\vspace*{1cm}\n");
                        break;

                }

                i++;
            }

                   sb.append("\\pie[xshift=5cm, scale=0.45]%\n{");
            sb.append(DECIMAL_FORMAT.format((json.getDouble("totalYes") / json.getDouble("totalVotes")) * 100)).append("/totalYes,\n");
            sb.append(DECIMAL_FORMAT.format((json.getDouble("totalNo") / json.getDouble("totalVotes")) * 100)).append("/totalNo,\n");
            sb.append(DECIMAL_FORMAT.format((json.getDouble("totalAbstained") / json.getDouble("totalVotes")) * 100)).append("/totalAbstained,\n");
            sb.append(DECIMAL_FORMAT.format((json.getDouble("totalNoVotes") / json.getDouble("totalVotes")) * 100)).append("/totalNoVotes}\n");
            sb.append("\\end{tikzpicture}\n");
            sb.append("\\end{figure}\n");


        }

        System.out.println(sb);
        return sb.toString();
    }

    public String agendaItems(String protocol) {
        Document protocolDoc = mdbh.getDB().getCollection("protocol").find(new Document("_id", protocol)).iterator().tryNext();
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
