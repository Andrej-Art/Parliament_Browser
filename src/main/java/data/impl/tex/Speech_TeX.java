package data.impl.tex;

import org.json.JSONObject;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Arrays.copyOf;

/**
 * The {@code Speech_TeX} class.
 *
 * @author Eric Lakhter
 */
public class Speech_TeX {
    private final String id, text, date;
    private final JSONObject speaker;
    private final JSONArray perEntities, orgEntities, locEntities, sentences, comments;

    public Speech_TeX(JSONObject speechTeXObj) {
        id = speechTeXObj.getString("id");
        text = speechTeXObj.getString("text");
        date = speechTeXObj.getString("date");
        speaker = speechTeXObj.getJSONObject("speaker");
        perEntities = speechTeXObj.getJSONArray("perEntities");
        orgEntities = speechTeXObj.getJSONArray("orgEntities");
        locEntities = speechTeXObj.getJSONArray("locEntities");
        sentences = speechTeXObj.getJSONArray("sentences");
        comments = speechTeXObj.getJSONArray("comments");
    }

    /**
     * Builds a string which can be formatted by a TeX compiler.
     * @param showNamedEntities whether named entity markers should show up in the text.
     * @param showSentiment whether sentiment information should show up at the end of each sentence.
     * @param showComments whether comments should show up.
     * @return String in TeX format.
     * @author Eric Lakhter
     */
    public String toTeX(boolean showNamedEntities, boolean showSentiment, boolean showComments) {
        List<String> textArray = new ArrayList<>(asList(text.split("")));
        textArray.add("");
        Iterator<String> textIter = textArray.iterator();
        StringBuilder speechTeX = new StringBuilder();

        int perIndex = 0;
        int orgIndex = 0;
        int locIndex = 0;
        int sentenceIndex = 0;
        int commentIndex = 0;
        for (int i = 0; textIter.hasNext(); i++) {
            if (sentenceIndex < sentences.length() && sentences.getJSONObject(sentenceIndex).getInt("endPos") == i) {
                speechTeX.append(sentences.getJSONObject(sentenceIndex).getString("sentiment"));
                sentenceIndex++;
            }
            speechTeX.append(textIter.next());
        }

        return speechTeX.toString();
    }
}
