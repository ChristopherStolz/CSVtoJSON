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
	
(4) Authors
	Christopher Stolz - initial work
