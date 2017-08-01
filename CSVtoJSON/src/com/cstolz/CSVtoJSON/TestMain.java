package com.cstolz.CSVtoJSON;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class TestMain {
	public static void main (String[] args) {
		
		File inputFile = new File(args[0]);
		FileInputStream inStream = null;
		Parser myParser = null;
		
		try {
			inStream = new FileInputStream(inputFile);
			myParser = new Parser (inStream);
		} catch (FileNotFoundException e) {
			System.err.println(e.toString());
			System.exit(-1);
		} finally {
		}
		
		try {
			String[] x = myParser.readRecord();
			for(int i = 0; i < x.length; i++){
				System.out.println(x[i]);
			}
		} catch (IOException e) {
			System.err.println(e.toString());
			System.exit(-1);
		} finally {
		}
		
	}
}