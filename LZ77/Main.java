import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.ArrayList;

public class Main {

    public static void stats(String originalFile, String encodedFile, String decodedFile) throws IOException {
        String original = LZ77.read_txt(originalFile);
        String decoded = LZ77.read_txt(decodedFile);
        ArrayList<Tuple> encoded = LZ77.read_tags(encodedFile);

        long originalSize = Files.size(Paths.get(originalFile));
        long encodedSize = Files.size(Paths.get(encodedFile));

        System.out.println("\nDo they match?: " + original.equals(decoded));
        System.out.println("\nCompression Ratio: " + ((double) encodedSize / originalSize));
    }

    public static void main(String[] args) throws IOException {
        try (Scanner scanner = new Scanner(System.in)) {
            int SEARCH_SIZE = 8;
            int LOOK_AHEAD_SIZE = 7;

            while (true) {
                System.out.println("What would you like to do\n Compress File: 1\n Decompress File: 2\n Compare Files: 3");
                int userChoice = scanner.nextInt();
                scanner.nextLine();

                while (userChoice != 1 && userChoice != 2 && userChoice != 3) {
                    System.out.println("Please choose a valid option\n Compress File: 1\n Decompress File: 2\n Compare Files: 3");
                    userChoice = scanner.nextInt();
                    scanner.nextLine();
                }

                if (userChoice == 1) {
                    System.out.println("What file would you like to compress?:");
                    String userInput1 = scanner.nextLine();
                    System.out.println("What file would you like to save to?:");
                    String userInput2 = scanner.nextLine();
                    LZ77.encoder(userInput1, userInput2, SEARCH_SIZE, LOOK_AHEAD_SIZE);
                } else if (userChoice == 2) {
                    System.out.println("What file would you like to decompress?:");
                    String userInput1 = scanner.nextLine();
                    System.out.println("What file would you like to save to?:");
                    String userInput2 = scanner.nextLine();
                    LZ77.decoder(userInput1, userInput2);
                } else {
                    System.out.println("What's the original file?:");
                    String userInput1 = scanner.nextLine();
                    System.out.println("What's the encoded file?:");
                    String userInput2 = scanner.nextLine();
                    System.out.println("What's the decoded file?:");
                    String userInput3 = scanner.nextLine();
                    stats(userInput1, userInput2, userInput3);
                    System.out.println("Do you want to perform another operation? (yes/no)");
                    String continueChoice = scanner.nextLine();
                    if (!continueChoice.equalsIgnoreCase("yes")) {
                        break;
                    }
                }
            }
        }
    }
}