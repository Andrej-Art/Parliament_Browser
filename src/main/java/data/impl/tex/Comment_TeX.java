package data.impl.tex;

import data.impl.Comment_Impl;
import org.bson.Document;

/**
 * The {@code Comment_TeX} class.
 *
 * @author Eric Lakhter
 */
public class Comment_TeX extends Comment_Impl {
    public Comment_TeX(Document document) {
        super(document);
    }

    public String toTeX() {
        return "";
    }
}
