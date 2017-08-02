package com.cstolz.CSVtoJSON;

import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.IOException;
import java.io.Reader;

import java.lang.StringBuilder;

import java.util.ArrayList;

public class Parser{
	private InputStreamReader input;
	
	public Parser(){
		input = null;
	}

	public Parser (InputStream inStream){
		input = new InputStreamReader(inStream);
	}
	
	public String[] readRecord() throws IOException {
		if (!input.ready()) {
			return null;
		}
		
		ArrayList<String> fields = new ArrayList<String>();
		String[] returnVal = null;
		StringBuilder stringHelper = new StringBuilder("");
		int numQuotes = 0;
		
		while(input.ready()){
			/*
			* Reads the input until the end of a record is detected
			* The end of a record is a new line outside of quotes
			*/
			int helper = input.read();
			if((char)helper == '\r'){
				//Carriage returns are unneccessary and make erros.
			} else {
				if ((char)helper == '"'){
					numQuotes++;
					stringHelper.append((char)helper);
				} else if (numQuotes % 2 == 0){
					if((char)helper == ','){
						/*
						* A comma outside of quotes should be a field seperator
						*/
						stringHelper.append((char)helper);
						fields.add(stringHelper.toString());
						stringHelper = new StringBuilder("");
						numQuotes = 0;
					} else if ((char)helper == '\n') {
						/*
						* records are terminated with \n in this file
						* if it is inside of quotes it should be ignored
						*/
						helper = input.read();
						break;
					} else {
						stringHelper.append((char)helper);
					}
				} else {
					stringHelper.append((char)helper);
				}
			}
		}
		fields.add(stringHelper.toString());
		returnVal = fields.toArray(new String[0]);
		return returnVal;
	}
}



