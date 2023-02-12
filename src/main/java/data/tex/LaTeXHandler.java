package data.tex;

import org.bson.Document;
import utility.MongoDBHandler;
import utility.annotations.Testing;
import utility.annotations.Unfinished;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static spark.Spark.staticFiles;

/**
 * Class to manage al TEX related classes and generate the pdf from the tex file.
 */
@Testing
@Unfinished("Generates a Latex String together with the other TEX classes' toTex() Methods. Does not generate a pdf from the latex yet.")
public class LaTeXHandler {

    private final String targetDir = System.getProperty("user.dir");

    private final MongoDBHandler mdbh = new MongoDBHandler();


    public LaTeXHandler() throws IOException {
    }


    /**
     * Creates Tex file from the generated String of the Protocol with the given ID
     * @param protocolID
     * @return
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

            stringBuilder.append(protocolTeX.toTeX(agendaItemDocs));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return stringBuilder.toString();
    }

    /**
     * Attempts to generate the .pdf file from the created .tex file does not run yet in its current form.
     * @param latexString
     * @param protocolID
     * @throws IOException
     * @throws InterruptedException
     */
    @Testing
    @Unfinished("Fails to run the command pdflatex properly, although .tex is in correct syntax...")
    public void createPDF(String latexString, String protocolID) throws IOException, InterruptedException {

        // Writing the Generated String to a .tex file
        // Setting up the directory where the files will be saved and downloaded from
        String protocolIDNoSlash = protocolID.replace("/", "");
        try {

            String downloadDirectory = targetDir;
            File checkDir = new File(downloadDirectory);
            if (!checkDir.exists()) {
                new File(downloadDirectory).mkdir();
            }
//            staticFiles.externalLocation(downloadDirectory);
//            //Create .tex file from given String
            String pathToTexFile = downloadDirectory + "\\protocolWithImages.tex";
            File file = new File(pathToTexFile);
            // Writing the String in Latex format to a .tex file
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(latexString.getBytes());
            fileOutputStream.close();

            // Using the user's local installation of LateX, we run a command 'pdflatex' to generate
            // the .pdf file compiling the latex code contained in the .tex file

            String command = "pdflatex -synctex=1 -file-line-error -recorder -output-directory=" + downloadDirectory +"  " +  pathToTexFile;
            Process process = Runtime.getRuntime().exec(command);
            //process.waitFor();
//            String[] cmd = { "cmd.exe", "-c", command };
//            Process process = Runtime.getRuntime().exec(cmd);

            InputStreamReader isr = new InputStreamReader(process.getInputStream());
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }

            isr = new InputStreamReader(process.getErrorStream());
            br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                System.err.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
