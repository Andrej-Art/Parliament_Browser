package data.tex;

import org.bson.Document;
import org.json.JSONObject;
import utility.MongoDBHandler;
import utility.annotations.Testing;
import utility.annotations.Unfinished;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@code Protocl_TeX} class.
 *
 * @author Eric Lakhter
 */
@Testing
@Unfinished("Generates a Latex String together with the other TEX classes' toTex() Methods")
public class Protocol_TeX {
    private Document protDoc;
    private MongoDBHandler mdbh;

    public Protocol_TeX(Document protocolDoc, MongoDBHandler mongoDBHandler) {
        this.protDoc = protocolDoc;
        this.mdbh = mongoDBHandler;
    }

    public String toTeX(List<Document> agendaItems) {

        StringBuilder sb = new StringBuilder();
        sb.append("\\begin{document} \n\n  \\chapter{Protokoll: " + protDoc.getString("_id") + "}" + "\n\n");


        for (Document agDoc : agendaItems) {
            List<String> speechIDs = null;
            if (agDoc.getList("speechIDs", String.class) != null){
                speechIDs = new ArrayList<>((agDoc.getList("speechIDs", String.class)));
            }
            List<Document> speechDocs = new ArrayList<>(0);
            if(speechIDs != null) {
                for (String speechID : speechIDs) {
                    speechDocs.add(mdbh.getDocument("speech", speechID));
                }
            }
            AgendaItem_TeX agTEX = new AgendaItem_TeX(agDoc, mdbh);
            sb.append("\\section{" + agDoc.getString("_id") + "} \n\n" +  agTEX.toTeX(speechDocs));
        }

        sb.append("\\end{Document}");

        return sb.toString();
    }
}
