package utility;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.XmlCasSerializer;
import org.hucompute.textimager.fasttext.labelannotator.LabelAnnotatorDocker;
import org.hucompute.textimager.uima.gervader.GerVaderSentiment;
import org.hucompute.textimager.uima.spacy.SpaCyMultiTagger3;
import org.xml.sax.SAXException;
import utility.annotations.*;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

/**
 * This class contains all methods required to perform NLP on speeches and comments.
 *
 * @author Eric Lakhter
 */
public class UIMAPerformer {

    private final MongoDBHandler mongoDBHandler;
    private final AnalysisEngine analysisEngine;
    private final String[] ddcCategories;

    /**
     * Sets up the necessary resources to perform UIMA analysis on texts.
     * @param mongoDBHandler Required MongoDB connection.
     * @throws UIMAException If an error occurs while building the {@code AnalysisEngine}.
     * @throws FileNotFoundException If {@code ddc3-names-de.csv} is not found in {@code /resources/}.
     */
    public UIMAPerformer(MongoDBHandler mongoDBHandler) throws UIMAException, FileNotFoundException {
        this.mongoDBHandler = mongoDBHandler;
        analysisEngine = getNewAnalysisEngine();
        ddcCategories = getDDCCategories();
    }

    /**
     * Fetches a speech or a comment specified by collection and ID.
     * @param col Target collection. Must be either "speech" or "comment".
     * @param id ID of the text to fetch.
     * @throws UIMAException If an Error occurs during JCas creation.
     * @throws IOException If {@code col} does not equal "speech" or "comment" or ID doesn't exist.
     * @throws SAXException If an error occurs during CAS serialization.
     * @author Eric Lakhter
     */
    public void serializeFromDB(String col, String id) throws UIMAException, IOException, SAXException {
        validateQuery(col, id);
        serializeData(col, id, mongoDBHandler.getText(col, id));
    }

    /**
     * Updates a speech or comment ID with their respective UIMA data.
     * @param col Target collection. Must be either "speech" or "comment".
     * @param id Target ID which indicates where to put the data.
     * @param text The text to analyze.
     * @throws UIMAException If an Error occurs during JCas creation.
     * @throws IOException If {@code col} does not equal "speech" or "comment" or ID doesn't exist.
     * @throws SAXException If an error occurs during CAS serialization.
     * @author Eric Lakhter
     */
    public void serializeToDB(String col, String id, String text) throws UIMAException, IOException, SAXException {
        validateQuery(col, id);
        serializeData(col, id, text);
    }

    /**
     * Checks if collection names and ID values are valid.
     * @param col Target collection. Must be either "speech" or "comment".
     * @param id Target ID which indicates where to put the data.
     * @throws IOException If either of the conditions aren't met.
     * @author Eric Lakhter
     */
    @Testing
    @Unfinished // actually only validates test_speech and test_comment right now
    private void validateQuery(String col, String id) throws IOException {
        if (!(col.equals("test_speech") || col.equals("test_comment")))
            throw new IOException("Target collection must be either \"speech\" or \"comment\"");
        if (!mongoDBHandler.checkIfDocumentExists(col, id))
            throw new IOException("Document with the given ID doesn't exist");
    }

    /**
     * After passing validation, the text gets analyzed and inserted into the DB.
     * @param col Target collection. Must be either "speech" or "comment".
     * @param id Target ID which indicates where to put the data.
     * @param text The text to analyze.
     * @throws UIMAException If an Error occurs during JCas creation.
     * @throws SAXException If an Error occurs during serialization.
     */
    @Unfinished // Only creates the full CAS so far
    private void serializeData(String col, String id, String text) throws UIMAException, IOException, SAXException {
        JCas jcas = JCasFactory.createText(text, "de");
        SimplePipeline.runPipeline(jcas, analysisEngine);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XmlCasSerializer.serialize(jcas.getCas(), baos);
        mongoDBHandler.addCAS(col + "_cas", id, baos.toString());

        // Tokens
        // does nothing
        Collection<Token> token_list = JCasUtil.select(jcas, Token.class);
        ArrayList<String> token_lemma_list = new ArrayList<>(0);
        ArrayList<String> token_type_list = new ArrayList<>(0);
        for (Token token : token_list) {
            token_lemma_list.add(token.getLemmaValue());
            token_type_list.add(token.getPos().getCoarseValue());
        }
    }

    /*
        Resources: Analysis Engine, DDC Categories
     */

    /**
     * Builds an {@link org.apache.uima.fit.factory.AggregateBuilder} and returns it.
     * @return {@code AggregateBuilder} with necessary features.
     * @throws UIMAException If an exception is thrown whilst adding features to the builder.
     * @author Eric Lakhter
     */
    private AnalysisEngine getNewAnalysisEngine() throws UIMAException {
        AggregateBuilder builder = new AggregateBuilder();
        URL posmap = UIMAPerformer.class.getClassLoader().getResource("am_posmap.txt");

        builder.add(createEngineDescription(SpaCyMultiTagger3.class,
                SpaCyMultiTagger3.PARAM_REST_ENDPOINT, "http://spacy.lehre.texttechnologylab.org"
        ));
        builder.add(createEngineDescription(GerVaderSentiment.class,
                GerVaderSentiment.PARAM_REST_ENDPOINT, "http://gervader.lehre.texttechnologylab.org",
                GerVaderSentiment.PARAM_SELECTION, "text,de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence"
        ));
        builder.add(createEngineDescription(LabelAnnotatorDocker.class,
                LabelAnnotatorDocker.PARAM_FASTTEXT_K, 100,
                LabelAnnotatorDocker.PARAM_CUTOFF, false,
                LabelAnnotatorDocker.PARAM_SELECTION, "text",
                LabelAnnotatorDocker.PARAM_TAGS, "ddc3",
                LabelAnnotatorDocker.PARAM_USE_LEMMA, true,
                LabelAnnotatorDocker.PARAM_ADD_POS, true,
                LabelAnnotatorDocker.PARAM_POSMAP_LOCATION, posmap.getPath(),
                LabelAnnotatorDocker.PARAM_REMOVE_FUNCTIONWORDS, true,
                LabelAnnotatorDocker.PARAM_REMOVE_PUNCT, true,
                LabelAnnotatorDocker.PARAM_REST_ENDPOINT, "http://ddc.lehre.texttechnologylab.org"
        ));

        return builder.createAggregate();
    }

    /**
     * Returns a {@code String[]} containing all 1000 DDC categories.
     * <p>The CategoryCoveredTagged DDC number matches the respective String index, e.g.
     * {@code __label_ddc__320} matches index 320, which would be "Politikwissenschaft".
     * @return {@code String} array containing all DDC categories in order as specified by {@code ddc3-names-de.csv}.
     * @throws FileNotFoundException If {@code ddc3-names-de.csv} is not found in {@code /resources/}.
     * @author Eric Lakhter
     */
    private String[] getDDCCategories() throws FileNotFoundException {
        BufferedReader br = new BufferedReader(new FileReader(UIMAPerformer.class.getClassLoader().getResource("ddc3-names-de.csv").getPath()));
        return br.lines()                           // Stream<String> of the lines in the csv file
                .filter(s -> !s.isEmpty())          // remove all empty lines
                .map(s -> s.split("\t")[1])         // tabulator is the delimiter in the file, we need the second column
                .toArray(String[]::new);            // convert stream to String array
    }
}
