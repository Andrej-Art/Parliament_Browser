package utility;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.hucompute.textimager.fasttext.labelannotator.LabelAnnotatorDocker;
import org.hucompute.textimager.uima.gervader.GerVaderSentiment;
import org.hucompute.textimager.uima.spacy.SpaCyMultiTagger3;
import utility.annotations.Testing;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URL;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

/**
 * Class containing all methods required to perform NLP on speeches and comments.
 *
 * @author Eric Lakhter
 */
public class UIMAResources {

    /**
     * for testing purposes only.
     * @param args unused.
     * @author Eric Lakhter
     */
    @Testing
    public static void main(String[] args) {
    }

    /**
     * Builds an {@link org.apache.uima.fit.factory.AggregateBuilder} and returns it.
     * @return {@code AggregateBuilder} with necessary features.
     * @throws UIMAException If an exception is thrown whilst adding features to the builder.
     * @author Eric Lakhter
     */
    public static AggregateBuilder getAggregateBuilder() throws UIMAException {
        AggregateBuilder builder = new AggregateBuilder();
        URL posmap = UIMAResources.class.getClassLoader().getResource("am_posmap.txt");

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

        return builder;
    }

    /**
     * Returns a {@code String[]} containing all 1000 DDC categories.
     * <p>The CategoryCoveredTagged DDC number matches the respective String index, e.g.
     * {@code __label_ddc__320} matches index 320, which would be "Politikwissenschaft".
     * @return {@code String} array containing all DDC categories in order as specified by {@code ddc3-names-de.csv}.
     * @throws FileNotFoundException If {@code ddc3-names-de.csv} is not found in {@code /resources/backend/}.
     * @author Eric Lakhter
     */
    public static String[] getDDCCategories() throws FileNotFoundException {
        BufferedReader br = new BufferedReader(new FileReader(UIMAResources.class.getClassLoader().getResource("ddc3-names-de.csv").getPath()));
        return br.lines()                           // stream of the lines of the csv file
                .filter(s -> !s.isEmpty())          // remove all empty lines
                .map(s -> s.split("\t")[1])         // tabulator is the delimiter in the file, we need the second column
                .toArray(String[]::new);            // convert stream to String array
    }
}
