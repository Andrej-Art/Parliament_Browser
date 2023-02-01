package data.impl.tex;

import data.impl.Speech_Impl;
import org.bson.Document;

/**
 * The {@code Speech_TeX} class.
 *
 * @author Eric Lakhter
 */
public class Speech_TeX extends Speech_Impl {
    public Speech_TeX(Document document) {
        super(document);
    }

    public String toTeX() {
        return "";
    }
}
