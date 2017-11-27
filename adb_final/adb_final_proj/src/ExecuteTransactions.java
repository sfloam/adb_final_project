import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.File;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Arrays;
import java.util.ArrayList;


public class ExecuteTransactions {
    public static void main(String[] args) {
        read_file(args);

    }

	/*
     * Author: Scott Floam
	 * Purpose: Determines if a data file was provided
	 * Inputs: file_name --debug (optional)
	 * Outputs: void
	 */

    public static void read_file(String[] args) {
        String file_name = "";

        if ((args.length > 0 && args.length < 3)) {
            boolean debugFlag = (args.length == 2 && args[1].equals("--debug"));
            file_name = args[0];

            try {
                File file = new File(file_name);
                Scanner fileScanner = new Scanner(file);
                
                DataManager dm = new DataManager();
                TransactionManager tm = new TransactionManager(dm);

                while (fileScanner.hasNextLine()) {
                    String line = fileScanner.nextLine();
                    if (debugFlag) {
                        System.out.println(line);
                    }

                    if (isDumpOutput(line) || (isComment(line) || isEmpty(line))) {
                        if (isDumpOutput(line)) {
                            while (fileScanner.hasNextLine() && !isComment(fileScanner.nextLine())) {
                                if (debugFlag) {
                                    System.out.println(line);
                                }
                            }
                        }
                    } else {

                        //Reads transactions and parses them so that they are assignable
                        line = line.replaceAll("[(),]", " ");
                        ArrayList<String> lineArray = new ArrayList<String>(Arrays.asList(line.split(" ")));

                        //Gets transactions and sends them to the transaction manager for assignments
                        tm.assignTransaction(lineArray);
                    }
                }
                fileScanner.close();
            } catch (FileNotFoundException e) {
                System.out.println("File not found!");
            }
        } else {
            System.out.println("You entered an invalid argument or did not provide a data file!");
        }
    }

    public static boolean isComment(String line) {
        Pattern commentPattern = Pattern.compile("//");
        Matcher commentMatcher = commentPattern.matcher(line);
        return commentMatcher.find();
    }

    public static boolean isDumpOutput(String line) {
        Pattern dumpOutputPattern = Pattern.compile("=== output of dump");
        Matcher dumpOutputMatcher = dumpOutputPattern.matcher(line);
        return dumpOutputMatcher.find();
    }

    public static boolean isEmpty(String line) {
        return line.equals("");
    }


}