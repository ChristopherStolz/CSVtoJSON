package com.cstolz.CSVtoJSON;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.FileNotFoundException;
import java.io.IOException;

public class CSVConverter{
	/*
	*	CSV to JSON converter
	*	Written by Christopher Stolz
	*	7/10/2017
	*/
	public static void main (String[] args) {
		
		/*
		*	Presently hooked up to files
		*	The Parser and JSONOutput classes will accept any type of stream
		*	This should require minimal changes (change the FileInputStream & FileOutputStream
		*	to your desired type and adjust the arguments accordingly)
		*	Lines 25, 26, 33, and 36 would need to be changed.
		*/
		FileInputStream inStream = null;
		OutputStreamWriter outStream = null;
		Parser myParser = null;
		JSONOutput output = null;
		String[] x = null;
		
		try {
			System.out.println("Initializing streams.");
			inStream = new FileInputStream(new File(args[0]));
			myParser = new Parser (inStream);
			x = myParser.readRecord();
			outStream = new OutputStreamWriter(new FileOutputStream(new File(args[1])));
			output = new JSONOutput (outStream, x);
		} catch (IOException e) {
			System.err.println(e.toString());
			System.exit(-1);
		} finally {
		}
		
		try {
			System.out.println("Parsing records, this could take a few minutes.");
 			int count = 0;
			output.writeStart();
			 while (true) {
				 x = myParser.readRecord();
				 if (x == null) {
					break;
				 } else if (count > 0){
					count++;
					output.writeSeparator();
					output.write(x);
				 } else {
					output.write(x);
					count++;
				 }
			}
			output.writeEnd();
			System.out.println("Success! " + count + " objects read.");
		} catch (IOException e) {
			System.err.println(e.toString());
			System.exit(-1);
		} finally {
		}
		
	}
}