package utility;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import utility.annotations.Testing;

import java.io.IOException;

/**
 * @author Andrej Artuschenko
 */
@Testing
public class PictureScraper {

    /**
     * Method to produce the Picture Array of a speaker with all relevant Data (URL, Meta-Data) from the Picture database
     * @return
     * @throws IOException
     */

    /*
        Assumption: we are looking for pictures with the event "Reichstagsgebäude/Plenarsaal".
        After several test procedures, we have obtained the most consistent results for this.

         This is the URL for Reichstagsgebäude/Plenarsaal:
         https://bilddatenbank.bundestag.de/search/picture-result?query=&filterQuery%5Bort%5D%5B0%5D=Reichstagsgebäude%2C+Plenarsaal&sortVal=3

         when we search for a specific person, we get the following URL:
         https://bilddatenbank.bundestag.de/search/picture-result?query=Angela+Merkel&filterQuery%5Bort%5D%5B%5D=Reichstagsgebäude%2C+Plenarsaal&sortVal=3
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
        String urlWebsite = "https://bilddatenbank.bundestag.de/search/picture-result?query=" + fullName + "&filterQuery%5Bort%5D%5B%5D=Reichstagsgebäude%2C+Plenarsaal&sortVal=3";
        /*
        For Example:
        https://bilddatenbank.bundestag.de/search/picture-result?query=wirth+christian&filterQuery%5Bort%5D%5B%5D=Reichstagsgebäude%2C+Plenarsaal&sortVal=3
        */
        // Connection to website
        //Thread Sleep for 250ms so that we dont get kicked off the bundestag server

        try {
            Thread.sleep(250);
            Document doc = Jsoup.connect(urlWebsite).get();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        //Check if the Bilddatenbank-Site has results
        Document picDatabase = null;
        try {
            picDatabase = Jsoup.connect(urlWebsite).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // if they are no errors in the Bilddatenbank
        String[] metadata = new String[0];
       try {
            /*
            We always fetch the first element.
            To get access to the image and the relevant meta-data we need the attribute data-fancybox.
             */
            org.jsoup.nodes.Element firstPictureInDatabase = picDatabase.getElementsByAttributeValue("data-fancybox", "group").first();
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

       //Only for Test-Output in the console
        for(int i=0; i<pictureArray.length; i++ ){
            System.out.println(pictureArray[i]);
        }

        return pictureArray;

    }


}
