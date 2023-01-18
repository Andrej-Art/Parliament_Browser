package utility;

import data.Poll;
import data.impl.Poll_Impl;
import exceptions.NoPollException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

/**
 * The {@code PollScraper} class finds the german Bundestag's polls and converts them into Poll objects.
 * <p>Notice: <a href="https://www.bundestag.de/parlament/plenum/abstimmung/">https://www.bundestag.de/parlament/plenum/abstimmung/</a>
 * and <a href="https://www.bundestag.de/abstimmung">https://www.bundestag.de/abstimmung</a> are effectively the same page,
 * but the poll results are only saved on the first path.
 * @see Poll
 * @author Eric Lakhter
 */
public class PollScraper {
    /*
        Current last poll is https://www.bundestag.de/parlament/plenum/abstimmung/abstimmung?id=830
        The page's source has elements which directly correspond to the poll results.
        Polls (seemingly) start at ID 1: https://www.bundestag.de/parlament/plenum/abstimmung/abstimmung?id=1
        The query ID doesn't have a limit, https://www.bundestag.de/parlament/plenum/abstimmung/abstimmung?id=900
        and even https://www.bundestag.de/parlament/plenum/abstimmung/abstimmung?id=-30000 exist.
        Polls with ID = 0 and lower all seem to have a default result (and since we are iterating from i = 1 upward we won't
        ever need to worry about them anyway) while polls with IDs which are too high don't have any results at all.
        Some IDs seem to be missing, e.g. 470. The noPollCounter variable controls whether missing polls are
        consistent (which means they are truly over) or if it's just an outlier, after which it gets reset to 0.
     */

    // Counts how many polls in a row don't exist.
    private static int noPollCounter = 0;

    // Private to restrict other classes from instantiating a PollScraper.
    private PollScraper() {}

    /**
     * Iterates over all polls on the german Bundestag's webpage and returns them.<br>
     * Collecting all the poll data takes a few minutes.
     * @return A list of {@link Poll} objects.
     * @see #getOnePoll(int)
     * @author Eric Lakhter
     */
    public static List<Poll> getAllPolls() {
        List<Poll> polls = new ArrayList<>();

        boolean hasMorePolls = true;
        for (int i = 1; hasMorePolls; i++) {
            try {
                polls.add(getOnePoll(i));
            } catch (IOException e) {
                System.err.println("There was a problem with poll ID #" + i + ": " + e.getMessage());
                e.printStackTrace();
            } catch (NoPollException e) {
                System.err.println(e.getMessage() + " noPollCounter is at " + ++noPollCounter);
                // if 15 polls in a row don't exist it's a safe bet that there won't be any more
                // there is a 10 poll gap between ID 422 and 431
                if (noPollCounter > 14) hasMorePolls = false;
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
    public static Poll getOnePoll(int id) throws NoPollException, IOException {

        if (id < 1) throw new NoPollException("There are no polls with an ID < 1");

        Document pollHTML = Jsoup.connect("https://www.bundestag.de/parlament/plenum/abstimmung/abstimmung?id=" + id).get();
        Elements pollElements = pollHTML.getElementsByClass("bt-teaser-chart-solo");

        if (pollElements.isEmpty()) throw new NoPollException("The poll with ID " + id + " doesn't exist.");

        // If pollElements isn't empty poll results were found
        noPollCounter = 0;

        LocalDate date = TimeHelper.convertToISOdate(
                pollHTML.getElementsByClass("bt-dachzeile").first().text(), 2);

        /*
            Each index represents a party's votes:
            [0]: # of YES votes
            [1]: # of NO votes
            [2]: # of ABSTAINED votes
            [3]: # of DIDN'T VOTE vote
         */
        Map<String, int[]> pollMap = new HashMap<>();
        pollMap.put("SPD",              new int[]{0, 0, 0, 0});
        pollMap.put("CDU/CSU",          new int[]{0, 0, 0, 0});
        pollMap.put("B90/GRÜNE",        new int[]{0, 0, 0, 0});
        pollMap.put("FDP",              new int[]{0, 0, 0, 0});
        pollMap.put("AfD",              new int[]{0, 0, 0, 0});
        pollMap.put("DIE LINKE.",       new int[]{0, 0, 0, 0});
        pollMap.put("fraktionslose",    new int[]{0, 0, 0, 0});

        for (Element ele : pollElements) {
            String currentFraction = ele.attr("data-value");
            String[] results = ele.child(1).child(0).attr("data-chart-values").split(",");
            pollMap.get(currentFraction)[0] = Integer.parseInt(results[0]);
            pollMap.get(currentFraction)[1] = Integer.parseInt(results[1]);
            pollMap.get(currentFraction)[2] = Integer.parseInt(results[2]);
            pollMap.get(currentFraction)[3] = Integer.parseInt(results[3]);
        }

//        System.out.println();
//        for (Map.Entry<String, int[]> stringEntry : pollMap.entrySet()) {
//            System.out.println(stringEntry.getKey() + " = " +Arrays.toString(stringEntry.getValue()));
//        }

        return new Poll_Impl(id,
                date,
                pollMap.get("SPD"),
                pollMap.get("CDU/CSU"),
                pollMap.get("B90/GRÜNE"),
                pollMap.get("FDP"),
                pollMap.get("AfD"),
                pollMap.get("DIE LINKE."),
                pollMap.get("fraktionslose"));
    }
}
