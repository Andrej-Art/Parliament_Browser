package data.tex;

import data.Speech;
import data.tex.Protocol_TeX;
import org.bson.Document;
import utility.MongoDBHandler;
import utility.annotations.Testing;
import utility.annotations.Unfinished;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Testing
@Unfinished("Generates a Latex String together with the other TEX classes' toTex() Methods")
public class LaTeXHandler {

    private final MongoDBHandler mdbh = new MongoDBHandler();


    public LaTeXHandler() throws IOException {
    }


    public String createTEX(String protocolID){
        // Get the Protocol, aitems, and speeches from the db
        Document protocolDoc = mdbh.getDocument("protocol", protocolID);
        //System.out.println(protocolDoc.get("agendaItems"));
        List<Document> agendaItemDocs = new ArrayList<>(0);


        for(String aitemID: protocolDoc.getList( "agendaItems", String.class)){
            //Get all agendaItems by id
            agendaItemDocs.add(mdbh.getDocument("agendaItem", aitemID));
            //System.out.println(mdbh.getDocument("agendaItem", aitemID));
        }


        StringBuilder stringBuilder = new StringBuilder();

        Protocol_TeX protocolTeX = new Protocol_TeX(protocolDoc, mdbh);

        stringBuilder.append(protocolTeX.toTeX(agendaItemDocs));

        return stringBuilder.toString();
    }

}
