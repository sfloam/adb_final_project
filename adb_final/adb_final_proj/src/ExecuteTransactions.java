import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * @author scottfloam and pratikkarnik
 * <h1>Execute Transaction</h1>
 * <span>This class is used to parse the operation information to send to the {@link TransactionManager}</span>
 * @param args Command Line Arguments
 */
public class ExecuteTransactions {
  public static void main(String[] args) {
    read_file(args);

  }
 
  /**
   * <strong>read_file</strong>
   * <span>This function reads from a given input file based on the number of command line arguments. In case a file is passed over the command line,</span>
   * <span>then the application considers it as the input. In case a command line argument is not passed, the program asks the user how the user</span>
   * <span>wants to enter the input file. If the user enters 1, the user is asked to enter the file name. In case the user enters 2, the user can enter the whole file through</span>
   * <span>standard input.</span>
   * @param args Command Line Arguments
   */
  public static void read_file(String[] args) {
    String file_name = "";

    if ((args.length > 0 && args.length < 3)) {
      boolean debugFlag = (args.length == 2 && args[1].equals("--debug"));
      file_name = args[0];

      try {
        Scanner fileScanner = new Scanner(new File(file_name));
        TransactionManager tm = new TransactionManager();

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
            tm.assignTransaction(line.trim());
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
