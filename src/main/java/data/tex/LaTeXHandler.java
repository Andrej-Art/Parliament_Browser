package data.tex;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONObject;
import utility.MongoDBHandler;
import utility.annotations.Testing;
import utility.annotations.Unfinished;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to manage al TEX related classes and generate the pdf from the tex file.
 * @author DavidJordan
 */

public class LaTeXHandler {

    private String targetDir;

    private MongoDBHandler mdbh;
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.####");

    /**
     * Constructor
     * @param mongoDBHandler
     * @param targetDirectory
     * @throws IOException
     */
    public LaTeXHandler(MongoDBHandler mongoDBHandler, String targetDirectory) throws IOException {
        this.mdbh = mongoDBHandler;
        this.targetDir = targetDirectory;
    }


    /**
     * Creates a LaTeX formatted String of the Protocol with the given ID
     * @param protocolID
     * @return The formatted String.
     * @author DavidJordan
     */
    public String createTEX(String protocolID) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            // Get the Protocol, aitems, and speeches from the db
            Document protocolDoc = mdbh.getDocument("protocol", protocolID);
            //System.out.println(protocolDoc.get("agendaItems"));
            List<Document> agendaItemDocs = new ArrayList<>(0);


            for (String aitemID : protocolDoc.getList("agendaItems", String.class)) {
                //Get all agendaItems by id
                agendaItemDocs.add(mdbh.getDocument("agendaItem", aitemID));
                //System.out.println(mdbh.getDocument("agendaItem", aitemID));
            }

            Protocol_TeX protocolTeX = new Protocol_TeX(protocolDoc, mdbh, targetDir);

            stringBuilder.append(protocolTeX.insertAgendaitemsToProtocol(agendaItemDocs));
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(stringBuilder);
        return stringBuilder.toString();
    }

    /**
     * Attempts to generate the .tex and the .pdf file from the generated String  does not run yet in its current form.
     * @param latexString
     * @throws IOException
     * @throws InterruptedException
     * @author DavidJordan
     */
    @Testing
    @Unfinished("Produces pdf now, has to be bound into the frontend now.")
    public void createPDF(String latexString) throws IOException, InterruptedException {

        // Writing the Generated String to a .tex file
        // Setting up the directory where the files will be saved and downloaded from
        try {
            String downloadDirectory = targetDir;
            File checkDir = new File(downloadDirectory);
            if (!checkDir.exists()) {
                new File(downloadDirectory).mkdir();
            }


            String pathToTexFile = downloadDirectory + "\\protocol.tex";
            File file = new File(pathToTexFile);

            // Replacing various problematic Unicode chars that stopped the .pdf compilation
            latexString = latexString.replaceAll("\u202F", " ");
            latexString = latexString.replaceAll("\u02BC", "");
            latexString = latexString.replaceAll("&", "");
            latexString = latexString.replaceAll("\u2002", "");
            latexString = latexString.replaceAll("\u2003", "");

            // Writing the .tex file into the target directory where the command line pdflatex command will compile it to pdf
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
            String modifiedLatexString = latexString.replace("#", "\\#");
            outputStreamWriter.write(modifiedLatexString);
            outputStreamWriter.close();
            fileOutputStream.close();

            // Using the user's local installation of LateX, we run a command 'pdflatex' to generate
            // the .pdf file compiling the latex code contained in the .tex file

        } catch (IOException e) {
            e.printStackTrace();
        }

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
        sb.append("\\textbf{Sentimentvalue : " + speechDoc.getDouble("sentiment") + "}\n\n" +
                "\\textbf{Thema der Rede : " + speechDoc.getString("mainTopic") + "}\n" +
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


}
