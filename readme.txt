CSV to JSON converter

	This utility processes a rich CSV file which can contain objects, arrays, and multi-line text and produces a JSON objects for each record processed. Currently it executes from the command line as a java class with its own main method.

(1) Prerequesites
	- Java 1.8 or higher

(2) Installing
	CSVConverter.jar contains all necessary elements to write from a file and to a file. For alternate streams the current application will require modification in the CSVConverter.java file. The file is commented noting which lines need to be modified.

(3) Executiuon

	The utility takes two command line arguments. 
	
	The first is the input file which should be the CSV file you want to JSONify.
	
	The second is the output file which must be in a location addressable by the JVM. You can use either the fully qualified path, or a path from the pwd.
	
	From the directory your jar is in run:
	java -jar CSVConverter.jar <input file> <output file>
	
	ie:
	java -jar CSVConverter.jar myfile.csv output.json
	
(4) Errors

	Should you have an error, feel free to contact me with the nature of the error and any applicable error logs. error_log.txt should be generated if there is a mismatch of Array sizes. This is most commonly caused by unnaccounted for cases in a freeform text field which I am at the time of writing this still catching.
	
	You can contact me at cstolz@gmail.com
	The subject line should read "CSVtoJSON" + the type of error you are receiving.
	Include the error_log.txt if any or any console error you are receiving, and I will need the headers and offending record from your CSV. For large CSV files it will be most efficient to isolate those error cases.

(5) Authors

	Christopher Stolz - initial work
	email: cstolz@gmail.com

(6) Future planned updates
	
	- Allow for execution time type of stream selection
	- Additional error handling and logging allowing a recovery state
	- Web hosted version with error logging to aide development (this will record the original records which cause errors as well)

(7) Completed updates

	(8/3/2017) 
	"Continued work to improve handling of freeform text fields"
		- Rewrote how fields are read for robust parsing
	"Web hosted version with error logging to aide development (this will record the original records which cause errors as well)"
		- Modularized components; can now be driven externally from the library.