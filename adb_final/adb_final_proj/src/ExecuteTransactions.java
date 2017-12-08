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
    TransactionManager tm = new TransactionManager();
    boolean isNotStandardInput = true;
    
    if(args.length == 0) {
      System.out.println("How are you entering the input? ");
      System.out.println("Please Enter 1 for entering via input file ");
      System.out.println("Please Enter 2 for entering via standard input \n");
      Scanner src = new Scanner(System.in);
      int userInput = src.nextInt();
      if(userInput == 1) {
        System.out.println("Please Enter the file name\n");
        file_name = src.next();
      } else if(userInput == 2) {
        System.out.println("Please input your file\n");
        isNotStandardInput = false;
        while(src.hasNext()) {
          tm.assignTransaction(src.next().trim());
        }
        System.exit(0);
      }
    } else if(args.length == 1) {
      file_name = args[0];
    }
    
    if(isNotStandardInput) {
      try {
        Scanner fileScanner = new Scanner(new File(file_name));

        while (fileScanner.hasNextLine()) {
          String line = fileScanner.nextLine();

          if (isDumpOutput(line) || (isComment(line) || isEmpty(line))) {
            if (isDumpOutput(line)) {
              while (fileScanner.hasNextLine() && !isComment(fileScanner.nextLine())) {
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
