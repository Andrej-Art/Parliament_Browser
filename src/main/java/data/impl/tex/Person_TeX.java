package data.impl.tex;

import data.impl.Person_Impl;
import org.bson.Document;

/**
 * The {@code Person_TeX} class.
 *
 * @author Eric Lakhter
 */
public class Person_TeX extends Person_Impl {
    public Person_TeX(Document document) {
        super(document);
    }

    public String toTeX() {
        return "";
    }
}
