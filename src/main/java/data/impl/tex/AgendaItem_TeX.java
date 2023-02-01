package data.impl.tex;

import data.impl.AgendaItem_Impl;
import org.bson.Document;

/**
 * The {@code AgendaItem_TeX} class.
 *
 * @author Eric Lakhter
 */
public class AgendaItem_TeX extends AgendaItem_Impl {
    public AgendaItem_TeX(Document doc) {
        super(doc);
    }

    public String toTeX() {
        return "";
    }
}
