package data.tex;

import org.bson.Document;
import utility.MongoDBHandler;
import utility.TimeHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@code Protocl_TeX} class.
 * @author DavidJordan
 */

public class Protocol_TeX {

    private Document protDoc;
    private MongoDBHandler mdbh;

    private final String targetDirectory;

    public Protocol_TeX(Document protocolDoc, MongoDBHandler mongoDBHandler, String targetDirectory) {
        this.protDoc = protocolDoc;
        this.mdbh = mongoDBHandler;
        this.targetDirectory = targetDirectory;
    }

    /**
     * Method to generate Latex formatted String containing the relevant data.
     *
     * @param agendaItems
     * @return
     * @author DavidJordan
     */
    public String insertAgendaitemsToProtocol(List<Document> agendaItems) throws IOException {
        LaTeXHandler laTeXHandler = new LaTeXHandler(mdbh, "");

        StringBuilder sb = new StringBuilder();
        sb.append("\\documentclass[a4paper,11pt]{article}\n" +
                "\\usepackage[ngerman,shorthands=off]{babel}\n" +
                "\\usepackage{graphicx}\n\n" +
                "\\usepackage{hyperref}\n\n" +
                "\\usepackage{color}\n\n" +
                "\\usepackage[utf8]{inputenc}\n\n" +
                "\\usepackage[T1]{fontenc}\n\n" +
                "\\usepackage{pgf-pie}\n" +
                "\\usepackage{tikz}\n" +
                "\\usepackage[capitalize]{cleveref}\n" +
                "\\usepackage{enumitem}\n" +
                "\\usepackage{makecell}\n" +
                "\\title{Protokoll: " +
                protDoc.getString("_id") + "}\n\n" +
                "\\begin{document}\n\n" +
                "\\maketitle\n\n" +
                "\\tableofcontents\n\n");


        for (Document agDoc : agendaItems) {
            List<String> speechIDs = null;
            if (agDoc.getList("speechIDs", String.class) != null) {
                speechIDs = new ArrayList<>((agDoc.getList("speechIDs", String.class)));
            }
            List<Document> speechDocs = new ArrayList<>(0);
            if (speechIDs != null) {
                for (String speechID : speechIDs) {
                    speechDocs.add(mdbh.getDocument("speech", speechID));
                }
            }
            AgendaItem_TeX agTEX = new AgendaItem_TeX(agDoc, mdbh);
            sb.append("\\section{" + agDoc.getString("_id") + "} \n\n" + agTEX.insertSpeechToAgendaitem(speechDocs, targetDirectory));
        }

        sb.append(laTeXHandler.pollResults(String.valueOf(TimeHelper.dateToLocalDate(protDoc.getDate("date")))));
        sb.append("\\end{document}");

        return sb.toString();
    }
}
