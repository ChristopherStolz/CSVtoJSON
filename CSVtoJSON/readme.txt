CSV to JSON converter

	This utility processes a rich CSV file which can contain objects, arrays, and multi-line text and produces a JSON objects for each record processed. Currently it executes from the command line as a java class with its own main method.

(1) Prerequesites
	- Java 1.8 or higher

(2) Installing
	The CSVtoJSON.class file is the compiled binary. No further installation is necessary to run from the command line with the java command.

(3) Executiuon
	The utility takes two command line arguments. 
	
	The first is the input file which should be the CSV file you want to JSONify.
	
	The second is the output file which must be in a location addressable by the JVM. You can use either the fully qualified path, or
	a path from the pwd.
	
	From the directory your class is in run:
	java CSVtoJSON <input file> <output file>
	
	ie:
	java CSVtoJSON myfile.csv output.json
	
(4) Errors
	Should you have an error, feel free to contact me with the nature of the error and any applicable error logs. error_log.txt should be generated if there is a mismatch of Array sizes. This is most commonly caused by unnaccounted for cases in a freeform text field which I am at the time of writing this still catching.
	
	You can contact me at cstolz@gmail.com
	The subject line should read "CSVtoJSON" + the type of error you are receiving.
	Include the error_log.txt if any or any console error you are receiving, and I will need the headers and offending record from your CSV. For large CSV files it will be most efficient to isolate those error cases.

(5) Authors
	Christopher Stolz - initial work
	email: cstolz@gmail.com

(6) Future planned updates
	- Continued work to improve handling of freeform text fields
	- Additional error handling and logging allowing a recovery state
	- Web hosted version with error logging to aide development (this will record the original records which cause errors as well)