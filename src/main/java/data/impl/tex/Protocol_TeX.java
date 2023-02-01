package data.impl.tex;

import data.impl.Protocol_Impl;
import org.bson.Document;

/**
 * The {@code Protocl_TeX} class.
 *
 * @author Eric Lakhter
 */
public class Protocol_TeX extends Protocol_Impl {
    public Protocol_TeX(Document document) {
        super(document);
    }

    public String toTeX() {
        return "";
    }
}
