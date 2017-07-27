import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.StringBuilder;
import java.lang.ArrayIndexOutOfBoundsException;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class CSVtoJSON{
	/*
	*	CSV to JSON converter
	*	Written by Christopher Stolz
	*	7/10/2017
	*/
	public static void main (String[] args) {
		
		File inputFile = new File(args[0]); //initialize input file
		File outputFile = new File(args[1]); //initialize output file
		File errorFile = new File("error_log.txt"); //initialize an error logging file
		
		BufferedReader input = null; //BufferedReader to read input file
		BufferedWriter output = null; //output writer
		BufferedWriter error = null;
		
		String[] headers = null;
		String[] item = null;
		try{
			System.out.println("Initializing.");
			input = new BufferedReader(new FileReader(inputFile)); //Initializes a buffered reader for input file
			output = new BufferedWriter(new FileWriter(outputFile)); //Initializes buffered writer for output file/stream
			error = new BufferedWriter(new FileWriter(errorFile));
		}catch (IOException e){
			// errors out on file not found
			System.err.println(e.toString());
			System.err.println("File error, input file not available or output file is unreachable address.");
			System.exit(1); //exits application due to file not found
		}finally{
		}
		
		try{
		System.out.println("Reading headers.");
		headers = getHeaders(input.readLine(), 0); //initialize field headers
		} catch(IOException e) {
			System.err.println(e.toString());
			System.err.println("Header data error");
			System.exit(3);
		} finally {
		}

		try{
		System.out.println("Reading data. This could take some time.");
		while (input.ready()){
			try{
				item = tokenize(input.readLine(), 0, input); // Propogates each item
				//System.out.println(item[0] + " : {"); // Console output used during testing
			} catch(IOException e) {
				System.err.println(e.toString());
				System.err.println("data error");
				System.exit(3);
			} finally {
			}
			try{
				output.write(item[0] + " : {");
				output.newLine();
				for(int i = 0; i < item.length; i++){
					//System.out.println(headers[i] + " : " + item[i]); // Console output used during testing
					output.write(headers[i] + " : " + item[i] + ",");
					output.newLine();
				}
				//System.out.println("}" + "\n"); //console output used during testing
				if(input.ready()){
					output.write("},");
				} else {
					output.write("}");
				}
				output.newLine();
				output.flush();
			} catch (IOException|ArrayIndexOutOfBoundsException ex) {
				System.err.println(ex);
				System.err.println("Output failure");
				if (ex.getCause() instanceof ArrayIndexOutOfBoundsException){
					/*
					* For ArrayIndexOutOfBoundsExceptions we print to System.err
					* and store to error_log.txt. This allows for local debugging and an error file to be sent in.
					*/
					System.err.println(item[0] + ": {\n");
					error.write(item[0] + ": {");
					error.newLine();
					for (int i = 0; i < headers.length; i++){
						System.err.println(headers[i] + " : " + item[i] + ",\n");
						error.write(headers[i] + " : " + item[i]);
						error.newLine();
					}
					error.flush();
				}
				System.exit(2);
			} finally {
			}
		}
		} catch(IOException e) {
			System.err.println(e.toString());
			System.exit(4);
		} finally {
			try{
			input.close();
			output.close();
			} catch(IOException e){
				System.err.println(e.toString());
				System.exit(5);
			}
		}
		System.out.println("Success!");
	}
	public static String[] getHeaders(String arg, int count) throws IOException{
		/*
		* CSV files have all their headers in their first line.
		* This reads those headers without the special cases needed for the
		* additional fields in the records.
		*/
		count += 1; // used to find the size of and then address the array as it is built
		String helper = ""; // stores the value of the current record before the recursive case is executed
		String[] returnVal = null; // initialize the return array as null (prevents "may not be initialized")
		
		if(arg.contains(",")){
			returnVal = getHeaders (arg.substring(arg.indexOf(",") + 1), count); //recursive case
			helper = arg.substring(0, arg.indexOf(",")); //takes the token for this case
		} else {
		helper = arg; // base case; no remaining items after current item
		returnVal = new String[count]; //initializes the array with a size of "count"
		}
		returnVal[count - 1] = helper; //count is going to be 1 ahead of the actual index we want to fill
		return returnVal; //collapse the recursion
	}
	
	public static String[] tokenize (String arg, int count, BufferedReader input) throws IOException{
		String helper = "";
		count += 1;
		String[] returnVal = null;
		if (arg.startsWith("[")){
			/*
			* Array handler
			*/
			while (!arg.contains("]")){
				// if there is no closing brace, seek a closing brace
				arg = arg + input.readLine();
			}
			if(arg.contains("],")){
				/*
				* Handles naked arrays (including empty arrays)
				*/
				helper = arg.substring(arg.indexOf("["), arg.indexOf("]") + 1);
				helper = helper.replace("'", ""); //removes single quotes
				helper = arrayHelper(helper); //passes the string to the array helper and stores back in itself
				String argHelper = arg.substring(arg.indexOf("]"));  //stores a partial cut off the current value to make finding the end easier
				arg = argHelper.substring(argHelper.indexOf(",") + 1); //updates the value for the argument to the next field
			} else {	
				helper = arg.substring(0, arg.indexOf(",")); //handles random brackets which aren't an array
				arg = arg.substring(arg.indexOf(",") + 1); 
			}
			returnVal = tokenize(arg, count, input); // recurs when the end of the array is reached
		}
		
		else if (arg.startsWith("{")){
			/*
			* Object Handler
			*/
			while (!arg.contains("}")){
				arg = arg + input.readLine();
			}
			if((arg.contains("},")) && arg.indexOf("},") == arg.indexOf("}")){
				helper = arg.substring(arg.indexOf("{"), arg.indexOf("}") + 1);
				helper = objectHelper(helper.split(","));
				arg = arg.substring(arg.indexOf("}") + 2);
			} else {
				helper = arg.substring(0, arg.indexOf(","));
				arg = arg.substring(arg.indexOf(",") + 1);
			}
			returnVal = tokenize(arg, count, input);
		}
		else if (arg.startsWith("\"")){
			arg = arg.substring(1);
			if(arg.startsWith("[") && arg.contains("]\",") && arg.indexOf("]\",") < arg.indexOf("\",")){
				/*
				* Handles arrays surrounded by quotes
				* functionally a duplicate of the array handler
				*/
				helper = arg.substring(arg.indexOf("["), arg.indexOf("]") + 1);
				helper = helper.replace("'", "");
				helper = arrayHelper(helper);
				String argHelper = arg.substring(arg.indexOf("]"));
				arg = argHelper.substring(argHelper.indexOf(",") + 1);
				returnVal = tokenize(arg, count, input);
			} else if (arg.startsWith("{") && arg.contains("}\",") && arg.indexOf("}\",") < arg.indexOf("\",")){
				/*
				* Handles objects surrounded by quotes
				* Functionally a duplicate of object handler
				*/
				helper = arg.substring(arg.indexOf("{"), arg.indexOf("}") + 1);
				helper = objectHelper(helper.split(","));
				arg = arg.substring(arg.indexOf("}") + 2);
				returnVal = tokenize(arg, count, input);
			} else {
				if(arg.startsWith("\",")){
					/*
					* Handles the case that the whole field was "" (a null string)
					*/
					arg = arg.substring(2);
					returnVal = tokenize(arg, count, input);
				} else {
					if(arg.contains("\"\"")){
						/*
						* handles the fact that quotes are used the escape quotes
						*/
						arg = doubleQuoteHelper(arg);
					}
					while (!arg.contains("\",")){
						// seeks for a delimiter if none is present
						arg = arg + input.readLine();
					}
					int position = arg.indexOf("\","); //used to track the delimiter pisition
					while (arg.charAt(position - 1) == '\\'){
						//seeks through all possible delimiters to account for false delimiters
						while(!arg.substring(position + 2).contains("\",")) {
							//if it cannot reach a proper delimiter, seek for one
							arg = arg + input.readLine();
							if(arg.contains("\"\"")){
								//if it slurps more double quotes, remove them
								arg = doubleQuoteHelper(arg);
							}
						}
						position = arg.indexOf("\",", position + 1);
					}
					helper = arg.substring(0, position);
					arg = arg.substring(position + 2);
					returnVal = tokenize(arg, count, input);
				}
			}
		}
		else if (arg.contains(",")){
			helper = arg.substring(0, arg.indexOf(","));
			arg = arg.substring(arg.indexOf(",") + 1);
			returnVal = tokenize(arg, count, input);
		} else {
			helper = arg;
			returnVal = new String[count];
		}
		if (helper.equals("")){
			returnVal[count - 1] = "null";
		} else if (helper.matches("[0-9]+") || helper.contains("[")) {
			returnVal[count - 1] = helper;
		} else {
			returnVal[count - 1] = "\"" + helper + "\"";
		}
		return returnVal;
	}
	
	public static String arrayHelper(String arg) throws IOException{
		/*
		 * Arrays needed to be split into individual items and have single quotes removed
		 * Integer values are written without quotes, Strings receive double quotes
		 * to comply with JSON typing.
		*/
		String returnVal = ""; //initialize return value
		arg = arg.substring(arg.indexOf("[") + 1, arg.indexOf("]"));
		if (!arg.equals(returnVal)){
			String[] helper = arg.split(","); // Splits the object into its individual pairs
			if(arg.contains("{")){
				returnVal = objectHelper(helper);
				returnVal = returnVal.substring(0, returnVal.length() - 2); //cuts the last ", "
			} else {
				for (int i = 0; i < helper.length; i++){
					if(!helper[i].matches("[0-9]+") || !helper[i].equals("")){
						helper[i] = "\"" + helper[i] + "\", ";
					}
					returnVal = returnVal + helper[i];
				}
				returnVal = returnVal.substring(0, returnVal.length() - 2); // cuts the last ", "
			}
		}
	return "[" + returnVal + "]"	;
	}
	
	public static String objectHelper(String[] args) throws IOException{
		/*
		* takes an already split on record array and seperates it into key-value pairs
		* before making it JSON ready.
		*/
		String returnVal = "";
		if(args[0].length() > 2){
			for (int i = 0; i < args.length; i ++){
				String[] helper2 = new String[2]; //second helper string in order to split the key from the value
				if(args[i].contains(":")){
					/*
					* Opted for a manual split instead of Strings.split(":") in order to allow URL parsing
					* Splits the Key and Value and removes whitespace
					*/
					helper2[0] = args[i].substring(0, args[i].indexOf(":"));
					helper2[1] = args[i].substring(args[i].indexOf(":") + 1);
					helper2[1] = helper2[1].trim();
				}
				if(!helper2[1].matches("[0-9]+")){
					// Number only fields are left naked so they store as ints
					if (helper2[1].contains("}")){
						//The end of an object must be preserved within the array
						helper2[1] = helper2[1].substring(0, helper2[1].indexOf("}") - 1);
						helper2[1] = "\"" + helper2[1] + "\"}"; //Quotes added for String values
					} else {
						helper2[1] = "\"" + helper2[1] + "\""; //Quotes added for String values
					}
				}
				args[i] = helper2[0] + " : " + helper2[1] + ", "; // recombines the key and value after typing
				returnVal = returnVal + args[i]; //Puts the value from this pass into its place in the return String
		}
		} else {
			returnVal = "{}";
		}
		return returnVal;
	}
	
	public static String doubleQuoteHelper(String arg) {
		/*
		* quotes were escaped with another quote
		* we need quotes escaped with /
		*/
		for (int i = 0; i < arg.length(); i ++) {
			StringBuilder quoteHelper = new StringBuilder(arg);
			if(quoteHelper.charAt(i) == ',' && quoteHelper.charAt(i - 1) == '\"' && quoteHelper.charAt(i - 2) != '\\'){
				// stops the helper from removing triple quotes when double quotes were inside of a quoted area
				break;
			}
			if(quoteHelper.charAt(i) == '\"' && i != arg.length() - 1) { //Prevents removal of trailing final quote (and index out of bounds errors)
					if (quoteHelper.charAt(i + 1) == '\"'){ // checks if the next character is a quote
						if(i > 0){ // prevents index out of bounds
							if(quoteHelper.charAt(i - 1) != '\\') { //checks if there is an escape character already before it
								quoteHelper.setCharAt(i, '\\'); // changes an escape quote to a JSON escape character
							}
						} else {
					quoteHelper.setCharAt(i, '\\'); // if it is in position 1 it has to be an escape
					}
				}
			}
			arg = quoteHelper.toString(); //sets the argument to the rebuilt StringBuilder
		}
		return arg; //return the argument
	}
	
}