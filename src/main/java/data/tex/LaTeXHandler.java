package data.tex;

import org.bson.Document;
import utility.MongoDBHandler;
import utility.annotations.Testing;
import utility.annotations.Unfinished;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static spark.Spark.staticFiles;

/**
 * Class to manage al TEX related classes and generate the pdf from the tex file.
 * @author DavidJordan
 */
@Testing
@Unfinished("Generates a Latex String together with the other TEX classes' toTex() Methods. Does not generate a pdf from the latex yet.")
public class LaTeXHandler {

    private String targetDir;

    private MongoDBHandler mdbh;


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

            stringBuilder.append(protocolTeX.toTeX(agendaItemDocs));
        } catch (Exception e) {
            e.printStackTrace();
        }

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
//            staticFiles.externalLocation(downloadDirectory);
//            //Create .tex file from given String
            String pathToTexFile = downloadDirectory + "\\protocol.tex";
            File file = new File(pathToTexFile);
            // Writing the String in Latex format to a .tex file
//            FileOutputStream fileOutputStream = new FileOutputStream(file);
//            fileOutputStream.write(latexString.getBytes());
//            fileOutputStream.close();
            latexString = latexString.replaceAll("\u202F", " ");
            latexString = latexString.replaceAll("\u02BC", "");
            latexString = latexString.replaceAll("&", "");

//            FileOutputStream fileOutputStream = new FileOutputStream(file);
//            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
//            outputStreamWriter.write(latexString);
//            outputStreamWriter.close();
//            fileOutputStream.close();

            // Replace all occurrences of the problematic character with an ASCII equivalent character


//            FileOutputStream fileOutputStream = new FileOutputStream(file);
//            fileOutputStream.write(latexString.getBytes());
//            fileOutputStream.close();

            FileOutputStream fileOutputStream = new FileOutputStream(file);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
            String modifiedLatexString = latexString.replace("#", "\\#");
            outputStreamWriter.write(modifiedLatexString);
            outputStreamWriter.close();
            fileOutputStream.close();

            // Using the user's local installation of LateX, we run a command 'pdflatex' to generate
            // the .pdf file compiling the latex code contained in the .tex file

//            String command = "pdflatex -synctex=1 -interaction=nonstopmode -output-directory=" + downloadDirectory  + pathToTexFile;
//            Process process = Runtime.getRuntime().exec(command);
//
//
//            InputStreamReader isr = new InputStreamReader(process.getInputStream());
//            BufferedReader br = new BufferedReader(isr);
//            String line;
//            while ((line = br.readLine()) != null) {
//                System.out.println(line);
//            }
//
//            isr = new InputStreamReader(process.getErrorStream());
//            br = new BufferedReader(isr);
//            while ((line = br.readLine()) != null) {
//                System.err.println(line);
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void createTexFile(String texString){

    }
}
