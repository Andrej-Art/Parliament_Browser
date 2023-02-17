package data.tex;

import org.bson.Document;
import utility.MongoDBHandler;
import utility.annotations.Testing;
import utility.annotations.Unfinished;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * The {@code AgendaItem_TeX} class.
 *
 * @author DavidJordan
 */
public class AgendaItem_TeX {

    private Document aiDoc;
    private MongoDBHandler mdbh;

    /**
     * Constructor
     *
     * @param agendaItem
     * @param mongoDBHandler
     */
    public AgendaItem_TeX(Document agendaItem, MongoDBHandler mongoDBHandler) {
        this.mdbh = mongoDBHandler;
        this.aiDoc = agendaItem;
    }

    /**
     * Method to generate Latex formatted String containing the relevant data of the AgendaItem
     * @param speeches the list of speeches contained in the AgendaItem
     * @param targetDirectory the target directory for retrieving the images
     * @return Latex formatted String
     * @author DavidJordan
     */
    public String insertSpeechToAgendaitem(List<Document> speeches, String targetDirectory) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (Document speech : speeches) {
            // Get relevant speker and speech data
            if (mdbh.getDocument("person", speech.getString("speakerID")) != null) {
                Document speaker = mdbh.getDocument("person", speech.getString("speakerID"));
                Speech_TeX speechTex = new Speech_TeX(mdbh);
                LaTeXHandler laTeXHandler = new LaTeXHandler(mdbh,"");

                // the image URL to download the jpg from
                String imageURL = null;
                try {
                    imageURL = speaker.getList("picture", String.class).get(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String speechID = speech.getString("_id");
                String speakerName = speaker.getString("fullName");


                // if a speaker name exists download his image and rename it so latex can insert it
                if (speakerName != null) {
                    String speakerImageName = speakerName.replaceAll("\\s+", "_") + ".jpg";

                    if (imageURL != null) {
    //                    // Source:  https://www.baeldung.com/java-download-file
    //                    // Downloading the image of the speaker and storing it in the current working directory
                        try (InputStream inp = new URL(imageURL).openStream()) {
                            if (!new File(targetDirectory + "/" + speakerImageName).exists()) {
                                Files.copy(inp, Paths.get(targetDirectory, speakerImageName));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        // Adding the Speaker data and the image insert code

                        sb.append("\\subsection{Rede: " + speechID + "  Redner: " + speakerName + "}\n\n"
                                + "\\begin{figure}[ht]\n\n"
                                + "\\centering\n\n"
                                + "\\includegraphics[width=0.3\\textwidth]{" + speakerImageName + "}\n\n"
                                + "\\caption{" + speakerName + "}\n\n"
                                + "\\end{figure}\n\n");
                    }
                    // Adding the speech text and the nlp table
                    sb.append(speechTex.speechToTex(speech.getString("_id")) + "\n\n");
                    sb.append(laTeXHandler.nlpTableTex(speech.getString("_id")) + "\n\n");
                }
            }
        }
        // Return the finished String built
        return sb.toString();
    }
}
