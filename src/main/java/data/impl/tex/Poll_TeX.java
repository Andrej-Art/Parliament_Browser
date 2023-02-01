package data.impl.tex;

import data.impl.Poll_Impl;
import exceptions.WrongInputException;
import org.bson.Document;
import utility.TimeHelper;

/**
 * The {@code Poll_TeX} class.
 *
 * @author Eric Lakhter
 */
public class Poll_TeX extends Poll_Impl {
    private Document pollDoc;
    public Poll_TeX(Document doc) throws WrongInputException {
        super(
                doc.getInteger("_id"),
                TimeHelper.dateToLocalDate(doc.getDate("date")),
                new int[] {doc.getInteger("SPDYes"), doc.getInteger("SPDNo"), doc.getInteger("SPDAbstained"), doc.getInteger("SPDNoVotes")},
                new int[] {doc.getInteger("CxUYes"), doc.getInteger("CxUNo"), doc.getInteger("CxUAbstained"), doc.getInteger("CxUNoVotes")},
                new int[] {doc.getInteger("B90Yes"), doc.getInteger("B90No"), doc.getInteger("B90Abstained"), doc.getInteger("B90NoVotes")},
                new int[] {doc.getInteger("FDPYes"), doc.getInteger("FDPNo"), doc.getInteger("FDPAbstained"), doc.getInteger("FDPNoVotes")},
                new int[] {doc.getInteger("AfDYes"), doc.getInteger("AfDNo"), doc.getInteger("AfDAbstained"), doc.getInteger("AfDNoVotes")},
                new int[] {doc.getInteger("LINKEYes"), doc.getInteger("LINKENo"), doc.getInteger("LINKEAbstained"), doc.getInteger("LINKENoVotes")},
                new int[] {doc.getInteger("independentYes"), doc.getInteger("independentNo"), doc.getInteger("independentAbstained"), doc.getInteger("independentNoVotes")}
        );
        this.pollDoc = doc;
    }

    public String toTeX() {
        return "";
    }
}
