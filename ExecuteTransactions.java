import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;

public class ExecuteTransactions{
	public static void main (String [] args){
		read_file(args);

	}

	/*
	 * Author: Scott Floam
	 * Purpose: Determines if a data file was provided
	 * Inputs: file_name --debug (optional)
	 * Outputs: void
	 */

	public static void read_file(String [] args) {
		String file_name = "";
            
            if ((args.length > 0 && args.length < 3)) {
            	boolean debugFlag = (args.length == 2 && args[1].equals("--debug"));
            	file_name = args[0];
            	
            	try {
            		File file = new File(file_name);
            		Scanner fileScanner = new Scanner (file);
	        		while(fileScanner.hasNextLine()){
	        			String line = fileScanner.nextLine();
	        			if (debugFlag){
	        				System.out.println(line);
	        			}
	        		}
            	}
            	
            	catch(FileNotFoundException e){
            		System.out.println("File not found!");
            	}
            }
            else {
				System.out.println("You entered an invalid argument or did not provide a data file!");
			}
	}
}