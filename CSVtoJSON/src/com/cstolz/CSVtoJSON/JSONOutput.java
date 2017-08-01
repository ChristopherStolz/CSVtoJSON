package com.cstolz.CSVtoJSON;

import java.io.BufferedWriter;
import java.io.Writer;
import java.io.IOException;

import java.lang.StringBuilder;

import java.util.ArrayList;

public class JSONOutput {
	BufferedWriter output;
	String[] headers;
	
	public JSONOutput () {
		output = null;
		headers = null;
	}
	
	public JSONOutput (Writer writer, String[] headers){
		/*
		*	Takes a writer of any type and initializes the buffered writer
		*	The headers are initialized here to be reused for each field
		*/ 
		output = new BufferedWriter(writer);
		this.headers = headers;
	}
	
	public boolean writefield () {
		boolean success = false;
		// Placeholder; logic to come.
		return success;
	}
	
	private String formatField (String incomingField) {
		StringBuilder field = new StringBuilder(incomingField);
			if (field.charAt(field.length() - 1) == ',') {
				field.setLength(field.length() - 1); //removes trailing commas left from input
			}
			if (field.charAt(0) == '"') {
				/*
				*	Remove outer quotes if they appear
				*	This allows us to parse multi-line objects and arrays
				*	If the quotes belong there they will be replaced
				*	Escape characters still need to be dealt with at this point (Doubled double quotes should be \")
				*/
				field = new StringBuilder(field.substring(1, field.length() - 1));
				if(field.indexOf("\"\"") != -1) {
					//call to escape subroutine
				}
			}
			if (field.charAt(0) == '[' && 
				field.charAt(field.length() - 1) == ']') {
				/*
				*	Checks if the contained information is an Array.
				*/
				field = this.arrayHelper(new StringBuilder(field.substring(1, field.length() - 1))); //outer brackets removed for processing
			} else if (field.charAt(0) == '{' &&
				field.charAt(field.length() -1) == '}') {
				/*
				*	Checks if the contained information is an object
				*/
				field = this.objectHelper(field);
			}
		return field.toString();
	}
	
	private StringBuilder arrayHelper(StringBuilder value){
		StringBuilder returnVal = new StringBuilder("");
		ArrayList<StringBuilder> helper = new ArrayList<StringBuilder>();
		if (value.equals(returnVal)){
			// Empty arrays return just square braces back
			returnVal.append("[]");
		} else {
			
			/*
			*	Build an ArrayList of the elements that belong to the Array
			*	Handles nested arrays, objects within arrays, and multi-line elements within arrays
			*/
			
			int quotes = 0;
			int curlyOpen = 0;
			int curlyClose = 0;
			int squareOpen = 0;
			int squareClose = 0;
			
			for (int i = 0; i < returnVal.length(); i++){
				if (value.charAt(i) == '"') {
					quotes++;
				} 
				if (quotes % 2 == 0) {
					if (value.charAt(i) == '{') {
						curlyOpen++;
					} else if (value.charAt(i) == '}') {
						curlyClose++;
					} else if (value.charAt(i) == '[') {
						squareOpen++;
					} else if (value.charAt(i) == ']') {
						squareClose++;
					}
					if (curlyOpen == curlyClose && squareOpen == squareClose) {
						if (returnVal.charAt(i) == ','){
							helper.add(new StringBuilder(returnVal.substring(i)));
							returnVal = new StringBuilder(returnVal.substring(i, returnVal.length()));
						}
					}
				}
			} //end loop
			
		}
		
		/*
		*	TODO: Handle nested elements
		*		  Handle element formatting
		*/
		
		return returnVal;
	}
	
	private StringBuilder objectHelper(StringBuilder value) {
		//placeholder, program logic to come
		return null;
	}
	
	private StringBuilder formatField(StringBuilder value) {
		//paceholder, program logic to come
		return null;
	}
	
	private StringBuilder doubleQuoteRemoval(StringBuilder value) {
		for (int i = 0; i < value.length(); i++){
			if(value.charAt(i) == '"' ){
				
			}
		}
		return null;
	}
}