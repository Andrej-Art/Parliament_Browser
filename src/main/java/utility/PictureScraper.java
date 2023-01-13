package utility;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import utility.annotations.Testing;
import utility.annotations.Unfinished;

import java.io.IOException;

/**
 * @author Andrej Artuschenko
 */
@Testing
@Unfinished
public class PictureScraper {

    /**
     * Method to produce the Picture Array of a speaker with all relevant Data (URL, Meta-Data) from the Picture database
     * @return
     * @throws IOException
     */

    /*
        Assumption: we are looking for portraits.

         This is the URL for portraits
         https://bilddatenbank.bundestag.de/search/picture-result?query=&filterQuery%5Bereignis%5D%5B0%5D=Porträt%2FPortrait&sortVal=3

         when we search for a specific person, we get the following URL:
         https://bilddatenbank.bundestag.de/search/picture-result?query=Angela+Merkel&filterQuery%5Bereignis%5D%5B%5D=Porträt%2FPortrait&sortVal=3
         in html: <img alt = Merkel, Angela ... >
     */

    public static String[] producePictureUrl(String firstName, String lastName) {

        String[] pictureArray = new String[8];


        // with replaceAll you can replace all spaces with a plus
        firstName = firstName.replaceAll(" ", "+");
        lastName = lastName.replaceAll(" ", "+");
        //Build Bilddatenbank-URL using the fullname like in the URL
        // "%2C" is the comma in URL-Encoding
        String fullName = lastName + "%2C+" + firstName;

        // URL of the website
        String urlWebsite = "https://bilddatenbank.bundestag.de/search/picture-result?filterQuery%5Bname%5D%5B%5D=" + fullName + "&filterQuery%5Bereignis%5D%5B%5D=Portr%C3%A4t%2FPortrait&sortVal=3";
        /*
        https://bilddatenbank.bundestag.de/search/picture-result?filterQuery%5Bname%5D%5B%5D=Merkel,+Angela&filterQuery%5Bereignis%5D%5B%5D=Portr%C3%A4t%2FPortrait&sortVal=3
        */
        // Connection to website
        try {
            Document doc = Jsoup.connect(urlWebsite).get();
            System.out.println("wir sind online");
        } catch (IOException e) {
            e.printStackTrace();
        }


        //Check if the Bilddatenbank-Site has results
        Document picDatabase = null;
        try {
            picDatabase = Jsoup.connect(urlWebsite).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /**
        // search after pTag where we can find the message
        Elements errors = picDatabase.select("p");
        boolean error = false;
        for (org.jsoup.nodes.Element element : errors) {
            if (element.text().equals("Es wurden keine Bilder gefunden.")) {
                error = true;
            }
        }
         */


        // if they are no errors in the Bilddatenbank
        String[] metadata = new String[0];
       try {
            /*
            We always fetch the first element.
            To get access to the image and the relevant meta-data we neet the attribute data-fancybox
             */
            org.jsoup.nodes.Element firstPictureInDatabase = picDatabase.getElementsByAttributeValue("data-fancybox", "group").first();
            System.out.println("wir haben die fancybox erfasst");
            //href to get the HTML of the jpg
            Document firstPictureHTML = null;
            try {
                firstPictureHTML = Jsoup.connect(firstPictureInDatabase.attr("abs:href")).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            org.jsoup.nodes.Element picture = firstPictureHTML.getElementsByTag("figure").first();

            //Get Picture URL
            pictureArray[0] = picture.child(0).attr("abs:src");
            //Get MetaData of the Picture
            metadata = picture.child(1).child(0).child(0).html().replaceAll("\\s*<h6>.*</h6>\\s*", "").replaceAll("\\s*<b>.*</b>\\s*", "").split("\\s*\n*\\s*<br>\\s*");

            //Fill pictureArray with the MetaData
            for (int a = 0; a < metadata.length; a++) {
                pictureArray[a + 1] = metadata[a];
            }
        } catch (Exception e) {
           e.printStackTrace();
       }
        System.out.println(pictureArray[1]);


        return pictureArray;

    }


}
