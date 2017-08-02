package com.cstolz.CSVtoJSON;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.FileNotFoundException;
import java.io.IOException;

public class TestMain {
	public static void main (String[] args) {
		
		File inputFile = new File(args[0]);
		FileInputStream inStream = null;
		OutputStreamWriter outStream = null;
		Parser myParser = null;
		JSONOutput output = null;
		String[] x = null;
		
		try {
			inStream = new FileInputStream(inputFile);
			myParser = new Parser (inStream);
			x = myParser.readRecord();
			outStream = new OutputStreamWriter(new FileOutputStream(new File("output.txt")));
			output = new JSONOutput (outStream, x);
		} catch (IOException e) {
			System.err.println(e.toString());
			System.exit(-1);
		} finally {
		}
		
		try {
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

			// for(int i = 0; i < x.length; i++){
				// System.out.println(x[i]);
			// }
		} catch (IOException e) {
			System.err.println(e.toString());
			System.exit(-1);
		} finally {
		}
		
	}
}