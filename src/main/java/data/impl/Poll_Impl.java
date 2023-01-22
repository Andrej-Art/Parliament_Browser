package data.impl;

import data.Poll;
import exceptions.WrongInputException;

import java.time.LocalDate;

/**
 * The {@code Poll} class contains the id, the date and each fraction's poll results.
 *
 * @author Eric Lakhter
 */
public class Poll_Impl implements Poll {
    private final int _id;
    private final LocalDate date;
    private final int[] spd;
    private final int[] cxu;
    private final int[] b90;
    private final int[] fdp;
    private final int[] afd;
    private final int[] linke;
    private final int[] independent;

    /**
     * Constructor for a new Poll. The results can be read with the {@link #toJson} method.<br>
     * Each array should contain the result in this order:
     * <ul>
     *     <li>[0]: # of <b>YES</b> votes</li>
     *     <li>[1]: # of <b>NO</b> votes</li>
     *     <li>[2]: # of <b>ABSTAINED</b> votes</li>
     *     <li>[3]: # of <b>DIDN'T VOTE</b> votes</li>
     * </ul>
     * @param _id The poll ID.
     * @param spd Array with SPD results.
     * @param cxu Array with CDU/CSU results.
     * @param b90 Array with BÜNDNIS 90/DIE GRÜNEN results.
     * @param fdp Array with FDP results.
     * @param afd Array with AfD results.
     * @param linke Array with DIE LINKE results.
     * @param independent Array with independent results.
     * @throws WrongInputException if any of the arrays has a length != 4.
     */
    public Poll_Impl(int _id, LocalDate date, int[] spd, int[] cxu, int[] b90, int[] fdp, int[] afd, int[] linke, int[] independent) throws WrongInputException {
        if (spd.length != 4 || cxu.length != 4 || b90.length != 4 || fdp.length != 4 || afd.length != 4 || linke.length != 4 || independent.length != 4)
            throw new WrongInputException("At least one of the fraction results doesn't have the right amount of fields.");
        this._id = _id;
        this.date = date;
        this.spd = spd;
        this.cxu = cxu;
        this.b90 = b90;
        this.fdp = fdp;
        this.afd = afd;
        this.linke = linke;
        this.independent = independent;
    }

    @Override
    public String toJson(){
        return "{\n  _id:" + _id + ",date:\"" + date + "\"," +
                "\n  SPDYes:" + spd[0] + ",SPDNo:" + spd[1] + ",SPDAbstained:" + spd[2] + ",SPDNoVotes:"  + spd[3] + "," +
                "\n  CxUYes:" + cxu[0] + ",CxUNo:" + cxu[1] + ",CxUAbstained:" + cxu[2] + ",CxUNoVotes:"  + cxu[3] + "," +
                "\n  B90Yes:" + b90[0] + ",B90No:" + b90[1] + ",B90Abstained:" + b90[2] + ",B90NoVotes:"  + b90[3] + "," +
                "\n  FDPYes:" + fdp[0] + ",FDPNo:" + fdp[1] + ",FDPAbstained:" + fdp[2] + ",FDPNoVotes:"  + fdp[3] + "," +
                "\n  AfDYes:" + afd[0] + ",AfDNo:" + afd[1] + ",AfDAbstained:" + afd[2] + ",AfDNoVotes:"  + afd[3] + "," +
                "\n  LINKEYes:" + linke[0] + ",LINKENo:" + linke[1] + ",LINKEAbstained:" + linke[2] + ",LINKENoVotes:"  + linke[3] + "," +
                "\n  independentYes:" + independent[0] + ",independentNo:" + independent[1] + ",independentAbstained:" + independent[2] + ",independentNoVotes:"  + independent[3] +
                "\n}";
    }
}
