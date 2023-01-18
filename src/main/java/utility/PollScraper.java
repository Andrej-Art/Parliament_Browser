package utility;

import data.Poll;
import data.impl.Poll_Impl;
import exceptions.NoPollException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import utility.annotations.Testing;
import utility.annotations.Unfinished;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@code PollScraper} class finds the german Bundestag's polls and converts them into {@link Poll} objects.
 * <p>Notice: <a href="https://www.bundestag.de/parlament/plenum/abstimmung/">https://www.bundestag.de/parlament/plenum/abstimmung/</a>
 * and <a href="https://www.bundestag.de/abstimmung">https://www.bundestag.de/abstimmung</a> are effectively the same page,
 * but the poll results are only saved on the first path.
 * @author Eric Lakhter
 */
@Unfinished("Doesn't actually scrape anything yet")
public class PollScraper {
    /*
        Current last poll is https://www.bundestag.de/parlament/plenum/abstimmung/abstimmung?id=830
        The page's source has elements which directly correspond to the poll results.
        Polls (seemingly) start at ID 0: https://www.bundestag.de/parlament/plenum/abstimmung/abstimmung?id=0
        The query ID doesn't have a limit, https://www.bundestag.de/parlament/plenum/abstimmung/abstimmung?id=900
        and even https://www.bundestag.de/parlament/plenum/abstimmung/abstimmung?id=-30000 exist.
        Negative number polls all seem to have one default result (and since we are iterating from i = 0 upward we won't
        ever need to worry about them anyway) while polls with IDs which are too high don't have any results at all.
     */

    /**
     * Iterates over all polls on the german Bundestag's webpage and returns them.
     * @return A list of {@link Poll} objects.
     * @see #getOnePoll(int)
     * @author Eric Lakhter
     */
    public static List<Poll> getAllPolls() {
        List<Poll> polls = new ArrayList<>();

        boolean hasMorePolls = true;
        for (int i = 0; hasMorePolls; i++) {
            try {
                polls.add(getOnePoll(i));
            } catch (IOException e) {
                System.err.println("There was a problem with poll ID #" + i + ":" + e.getMessage());
                e.printStackTrace();
            } catch (NoPollException e) {
                hasMorePolls = false;
            }
        }

        return polls;
    }

    /**
     * Generates a new {@code Poll} object based on the given ID.
     * @param id This is the ID the poll has on the Bundestag's website.
     * @return A new {@code Poll}.
     * @throws NoPollException If the poll with the given ID cannot be found.
     * @author Eric Lakhter
     */
    @Unfinished("It's almost 4 am")
    public static Poll getOnePoll(int id) throws NoPollException, IOException {
        if (id < 0) throw new NoPollException("There are no polls with an ID < 0");

        // Ganz ehrlich, ich weiß noch nicht, ob die ID ein einziger String ist oder lieber eine liste oder sonst was,
        // auf jeder Abstimmungsseite scheinen mehrere Drucksachen verlinkt zu sein (Für gewöhnlich 2, manchmal auch 3).
        // Allerdings scheint es auch so, dass die zweite die erste in ihrem Inneren erwähnt, die dritte erwähnt die
        // zweite Drucksache usw.
        // Bsp. https://www.bundestag.de/parlament/plenum/abstimmung/abstimmung?id=828
        // Erwähnte Drucksachen (in derselben Reihenfolge wie auf der Seite): 20/3879, 20/4229 und 20/4729
        // - 20/3879 erwähnt keine
        // - 20/4229 erwähnt 20/3879
        // - 20/4729 erwähnt 20/3879, 20/4229
        // Abhängig davon wie die Drucksachen in den Protokollen erwähnt sind brauchen wir entweder alle,
        // oder es ist immer eindeutig welche wir nehmen müssen.
        String _id = "";
        /*
            Each index represents a party's votes:
            [0]: # of YES votes
            [1]: # of NO votes
            [2]: # of ABSTAINED votes
            [3]: # of DIDN'T VOTE vote
         */
        int[] spd = new int[]{0, 0, 0, 0};
        int[] cxu = new int[]{0, 0, 0, 0};
        int[] b90 = new int[]{0, 0, 0, 0};
        int[] fdp = new int[]{0, 0, 0, 0};
        int[] afd = new int[]{0, 0, 0, 0};
        int[] linke = new int[]{0, 0, 0, 0};
        int[] independent = new int[]{0, 0, 0, 0};

        Document pollHTML = Jsoup.connect("https://www.bundestag.de/parlament/plenum/abstimmung/abstimmung?id=" + id).get();

//        System.out.println(pollHTML.html());

        // magic happens here


        // need to throw NoPollException if the poll # is too high

        return new Poll_Impl(_id, spd, cxu, b90, fdp, afd, linke, independent);
    }
}
