package utility;

import data.Speech;
import utility.uima.ProcessedSpeech;
import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
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
import utility.uima.*;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

/**
 * This class contains all methods required to perform NLP on speeches and comments.
 * @author Eric Lakhter
 */
public class UIMAPerformer {

    private final AnalysisEngine analysisEngine;
    private final String[] ddcCategories;

    /**
     * Sets up the necessary resources to perform UIMA analysis on texts.
     * @throws UIMAException If an error occurs while building the {@code AnalysisEngine}.
     * @throws FileNotFoundException If {@code ddc3-names-de.csv} is not found in {@code /resources/}.
     */
    public UIMAPerformer(MongoDBHandler mongoDBHandler) throws UIMAException, FileNotFoundException {
        analysisEngine = generateAnalysisEngine();
        ddcCategories = generateDDCCategories();
    }

    /*
        Methods for serialization of all data at once
     */

    /**
     * Processes a {@code Speech} and returns an object ready for insertion into the MongoDB.
     * @param speech The speech to be processed.
     * @return A processed speech without getters but 3 different {@code toJson()} methods.
     */
    public ProcessedSpeech processSpeech(Speech speech) {
        JCas jcas = getJCas(speech.getText());
        String fullCas = getFullCas(jcas);
        double sentiment = getAverageSentiment(jcas);
        String mainTopic = getMainTopic(jcas);
        List<MongoToken> tokens = getTokens(jcas);
        List<MongoSentence> sentences = getSentences(jcas);
        List<MongoNamedEntity> namedEntities = getNamedEntities(jcas);
        return new ProcessedSpeech(speech, fullCas, sentiment, mainTopic, tokens, sentences, namedEntities);
    }

    /*
        Methods for single serialization steps
     */

    /**
     * Returns the JCas containing the analyzed text.
     * @param text The text to analyze.
     * @return JCas holding the analysis.
     * @see #getFullCas(JCas)
     * @see #getMainTopic(JCas)
     * @see #getAverageSentiment(JCas)
     * @see #getTokens(JCas) 
     * @see #getSentences(JCas) 
     * @see #getNamedEntities(JCas)
     * @author Eric Lakhter
     */
    public JCas getJCas(String text) {
        try {
            JCas jcas = JCasFactory.createText(text, "de");
            SimplePipeline.runPipeline(jcas, analysisEngine);
            return jcas;
        } catch (UIMAException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Extracts the full XMI String from the CAS.
     * @param jcas JCas to have the String extracted from.
     * @return String in XMI file format.
     * @see #getJCas(String)
     * @author Eric Lakhter
     */
    public String getFullCas(JCas jcas) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            XmlCasSerializer.serialize(jcas.getCas(), baos);
            return baos.toString();
        } catch (SAXException | IOException e) {
            throw new RuntimeException(e);
        }
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
                    token.getBegin(), token.getEnd(), token.getLemmaValue(), token.getPos().getCoarseValue()
            ));
        }
        return mongoTokens;
    }

    /**
     * Returns the DDC category with the highest score in the text.
     * @param jcas JCas containing the text.
     * @return DDC category name.
     * @see #getJCas(String)
     * @author Eric Lakhter
     */
    public String getMainTopic(JCas jcas) {
        Iterator<CategoryCoveredTagged> cct = JCasUtil.select(jcas, CategoryCoveredTagged.class).iterator();
        return cct.hasNext() ?
                ddcCategories[Integer.parseInt(cct.next().getValue().substring(13))]
                : null;
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

        // if the text is empty, the sentiment list has 1 entry
        // if there is 1 sentence, the sentiment list has 2 entries
        // if there are more sentences, the sentiment list has at least 4 entries
        switch (sentences.size()) {
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
        for (NamedEntity ne : JCasUtil.select(jcas, NamedEntity.class)) {
            mongoNamedEntities.add(new MongoNamedEntity(ne.getBegin(), ne.getEnd(), ne.getValue(), ne.getCoveredText()));
        }
        return mongoNamedEntities;
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
