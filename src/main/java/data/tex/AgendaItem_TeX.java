package data.tex;

import org.bson.Document;
import org.json.JSONObject;
import utility.MongoDBHandler;
import utility.annotations.Testing;
import utility.annotations.Unfinished;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@code AgendaItem_TeX} class.
 *
 * @author Eric Lakhter
 */
@Testing
@Unfinished("Generates a Latex String together with the other TEX classes' toTex() Methods")
public class AgendaItem_TeX {

    private Document aiDoc;
    private MongoDBHandler mdbh;
    public AgendaItem_TeX(Document agendaItem, MongoDBHandler mongoDBHandler) {
        this.mdbh = mongoDBHandler;
        this.aiDoc = agendaItem;
    }

    public String toTeX(List<Document> speeches) {
        StringBuilder sb = new StringBuilder();
        List<Speech_TeX> speechTexs = new ArrayList<>(0);
        for(Document speech: speeches){
           Speech_TeX speechTex = new Speech_TeX(mdbh, speech);
           sb.append("\\subsection{Rede: " + speech.getString("_id") + "}\n\n" + speechTex.toTeX(speech.getString("_id")) + "\n\n");
        }
        return sb.toString();
    }
}
