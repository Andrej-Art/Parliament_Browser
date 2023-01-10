import java.util.Scanner;

/**
 * This is an exemplary menu for our test procedures and console outputs.
 * @author Andrej
 */

public class Main {

    /**
     * User menu as switch-case, which allows the user to access our implemented functions.
     * (Later we don't need this, because everything is handled by the frontend).
     * @param args
     */
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        String userInput = "";

        //Print User Menu
        while (!userInput.equals("5")){
            System.out.println("----------Parliament Sentiment Radar----------\n" +
                    "(1) Downloading protocols\n" +
                    "(2) Create database\n" +
                    "(3) Start NLP analysis\n" +
                    "(4) Clear Database\n" +
                    "(5) Exit\n" +
                    "----------------------------------------------");
            userInput = scanner.nextLine();

            //Receiving user input
            switch (userInput){
                case "1":
                    System.out.println("All protocols and necessary files will be downloaded.");
                    Scraper.downloadAllXMLs();
                    break;

                case "2":
                    System.out.println("Database will be created.");
                    break;

                case "3":
                    System.out.println("NLP analysis is performed...");
                    break;

                case "4":
                    System.out.println("Do you really want to empty the database?\n" +
                            "----------------------------------------------\n" +
                            "(1) Continue\n (2)Abort");
                    userInput = scanner.nextLine();
                    switch (userInput){
                        case"1":
                            System.out.println("hier kommen die zugriffe auf die collections, die gelöscht werden.");

                            System.out.println("The database was successfully cleared.");
                            break;

                        case"2":
                            System.out.println("Empty the database was aborted.");
                            break;

                        default:
                            System.out.println("Invalid input");
                            break;
                    }
                    break;

                case "5":
                    System.out.println("Programm will be terminated.");
                    break;

                default:
                    System.out.println("Invalid input");
                    break;
            }
        }
    }
}
