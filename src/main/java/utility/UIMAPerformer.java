package utility;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
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
import org.hucompute.textimager.uima.type.Sentiment;
import org.hucompute.textimager.uima.type.category.CategoryCoveredTagged;
import org.xml.sax.SAXException;
import utility.annotations.*;
import utility.uima.MongoNamedEntity;
import utility.uima.MongoSentence;
import utility.uima.MongoToken;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

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
        analysisEngine = generateAnalysisEngine();
        ddcCategories = generateDDCCategories();
    }

    /*
        Methods for single serialization steps
     */

    /**
     * Returns the JCas containing the analyzed text.
     * @param text The text to analyze.
     * @return JCas holding the analysis.
     * @throws UIMAException If an Error occurs during JCas creation.
     * @see #getFullCas(JCas)
     * @see #getTokens(JCas) 
     * @see #getSentences(JCas) 
     * @see #getNamedEntities(JCas) 
     * @see #getMainTopic(JCas)
     * @see #getAverageSentiment(JCas)
     * @author Eric Lakhter
     */
    public JCas getJCas(String text) throws UIMAException {
        JCas jcas = JCasFactory.createText(text, "de");
        SimplePipeline.runPipeline(jcas, analysisEngine);
        return jcas;
    }

    /**
     * Extracts the full XMI String from the CAS.
     * @param jcas JCas to have the String extracted from.
     * @return String in XMI file format.
     * @see #getJCas(String)
     * @author Eric Lakhter
     */
    public String getFullCas(JCas jcas) throws IOException, SAXException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XmlCasSerializer.serialize(jcas.getCas(), baos);
        return baos.toString();
    }

    /**
     * Extracts all Tokens from a JCas.
     * @param jcas JCas with Tokens.
     * @return {@code List<}{@link MongoToken}{@code >} consisting of all text tokens.
     * @see #getJCas(String)
     * @author Eric Lakhter
     */
    public List<MongoToken> getTokens(JCas jcas){
        List<MongoToken> mongoTokens = new ArrayList<>(0);
        for (Token token : JCasUtil.select(jcas, Token.class)) {
            mongoTokens.add(new MongoToken(
                    token.getBegin(), token.getEnd(), token.getLemmaValue(), token.getPosValue()
            ));
        }
        return mongoTokens;
    }

    /**
     * Extracts all Sentences from a JCas.
     * @param jcas JCas with Sentences.
     * @return {@code List<}{@link MongoSentence}{@code >} consisting of all text sentences.
     * @see #getJCas(String)
     * @author Eric Lakhter
     */
    public List<MongoSentence> getSentences(JCas jcas) {
        List<Sentence> sentences = new ArrayList<>(JCasUtil.select(jcas, Sentence.class));
        List<Sentiment> sentiments = new ArrayList<>(JCasUtil.select(jcas, Sentiment.class));
        List<MongoSentence> mongoSentences = new ArrayList<>(0);
        switch (sentences.size()) {
            // if the text is empty, the sentiment list has 1 entry
            // if there is 1 sentence, the sentiment list has 2 entries
            // if there are more entries, the sentiment list has at least 4 entries
            case 0:
                break;
            case 1:
                mongoSentences.add(new MongoSentence(
                        sentences.get(0).getBegin(), sentences.get(0).getEnd(), sentiments.get(1).getSentiment()
                ));
                break;
            default:
                for (int i = 0; i < sentences.size(); i++) {
                    mongoSentences.add(new MongoSentence(
                            sentences.get(i).getBegin(), sentences.get(i).getEnd(), sentiments.get(i + 2).getSentiment()
                    ));
                }
        }
        return mongoSentences;
    }

    /**
     * Extracts all NamedEntities from a JCas.
     * @param jcas JCas with named entities.
     * @return {@code List<}{@link MongoNamedEntity}{@code >} consisting of all named entities.
     * @see #getJCas(String)
     * @author Eric Lakhter
     */
    public List<MongoNamedEntity> getNamedEntities(JCas jcas) {
        List<MongoNamedEntity> mongoNamedEntities = new ArrayList<>(0);
        for (CategoryCoveredTagged cct : JCasUtil.select(jcas, CategoryCoveredTagged.class)) {
            mongoNamedEntities.add(new MongoNamedEntity(cct.getBegin(), cct.getEnd(), cct.getValue()));
        }
        return mongoNamedEntities;
    }

    /**
     * Returns the DDC category with the highest score in the text.
     * @param jcas JCas containing the text.
     * @return DDC category name.
     * @see #getJCas(String)
     * @author Eric Lakhter
     */
    public String getMainTopic(JCas jcas) {
        try { // If the text is empty, there is no saved category ranking so the next() method throws an exception
            return ddcCategories[Integer.parseInt(
                    JCasUtil.select(jcas, CategoryCoveredTagged.class)
                            .iterator()
                            .next()
                            .getValue()
                            .substring(13)
            )];
        } catch (NoSuchElementException e) {
            return null;
        }

    }

    /**
     * Returns the average text sentiment.
     * @param jcas JCas containing the text
     * @return Average sentiment.
     * @see #getJCas(String)
     * @author Eric Lakhter
     */
    public double getAverageSentiment(JCas jcas) {
        try {
            return new ArrayList<>(JCasUtil.select(jcas, Sentiment.class))
                    .get(1)
                    .getSentiment();
        } catch (IndexOutOfBoundsException e) {
            return 0.0;
        }
    }

    /*
        Methods for serialization of all data at once
     */

    /**
     * Fetches a speech or a comment specified by collection and ID.
     * @param col Target collection. Must be either "speech" or "comment".
     * @param id ID of the text to fetch.
     * @throws IOException If {@code col} does not equal "speech" or "comment" or ID doesn't exist.
     * @see #validateQuery(String, String)
     * @see #serializeData(String, String, String)
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
     * @throws IOException If {@code col} does not equal "speech" or "comment" or ID doesn't exist.
     * @see #validateQuery(String, String)
     * @see #serializeData(String, String, String)
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
     * @see #serializeFromDB(String, String)
     * @see #serializeToDB(String, String, String)
     * @author Eric Lakhter
     */
    @Testing // actually only validates test_speech and test_comment right now
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
     * @see #serializeFromDB(String, String)
     * @see #serializeToDB(String, String, String)
     * @author Eric Lakhter
     */
    @Unfinished // Only inserts the full CAS so far
    private void serializeData(String col, String id, String text) throws UIMAException, IOException, SAXException {
        JCas jcas = getJCas(text);
        String fullCas = getFullCas(jcas);
        mongoDBHandler.addCAS(col + "_cas", id, fullCas);

        List<MongoToken> tokens = getTokens(jcas);
        List<MongoSentence> sentences = getSentences(jcas);
        List<MongoNamedEntity> namedEntities = getNamedEntities(jcas);
        String mainTopic = getMainTopic(jcas);
        double avgSentiment = getAverageSentiment(jcas);
    }

    /*
        Resources: Analysis Engine, DDC Categories
     */

    /**
     * Builds an {@code AnalysisEngine} and returns it.
     * @return {@code AnalysisEngine} with necessary features.
     * @throws UIMAException If an exception is thrown whilst adding features to the builder.
     * @author Eric Lakhter
     */
    private AnalysisEngine generateAnalysisEngine() throws UIMAException {
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
    private String[] generateDDCCategories() throws FileNotFoundException {
        BufferedReader br = new BufferedReader(new FileReader(UIMAPerformer.class.getClassLoader().getResource("ddc3-names-de.csv").getPath()));
        return br.lines()                           // Stream<String> of the lines in the csv file
                .filter(s -> !s.isEmpty())          // remove all empty lines
                .map(s -> s.split("\t")[1])         // tabulator is the delimiter in the file, we need the second column
                .toArray(String[]::new);            // convert stream to String array
    }
}
