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
	
	public JSONOutput (Writer writer, String[] header){
		/*
		*	Takes a writer of any type and initializes the buffered writer
		*	The headers are initialized here to be reused for each field
		*/ 
		output = new BufferedWriter(writer);
		headers = new String[header.length];
 		for (int i = 0; i < headers.length; i++){
			headers[i] = formatField(header[i]);
		} 
	}
	
	public boolean write (String[] record) throws IOException{
		boolean success = false;
		for (int i = 0; i < record.length; i++){
			record[i] = this.formatField(record[i]);
		}
		output.write("\"" + record[0] + "\"" + ": {");
		output.newLine();
		for (int i = 0; i < headers.length; i++){
			output.write(headers[i]);
			output.write(" : ");
			output.write(record[i]);
			if (i < headers.length - 1) this.writeSeparator();
		}
		output.write("}");
		output.newLine();
		output.flush();
		success = true;
		return success;
	}
	
	public boolean writeSeparator() throws IOException{
		boolean success = false;
		output.write(",");
		output.newLine();
		output.flush();
		success = true;
		return success;
	}
	
	public boolean writeStart() throws IOException{
		boolean success = false;
		output.write("{");
		output.newLine();
		output.flush();
		success = true;
		return success;
	}
	
	public boolean writeEnd() throws IOException{
		boolean success = false;
		output.write("}");
		output.flush();
		success = true;
		return success;
	}
	
	private String formatField (String incomingField) {
		StringBuilder field = new StringBuilder(incomingField.trim());
			if (field.length() > 0 && field.charAt(field.length() - 1) == ',') {
				field.setLength(field.length() - 1); //removes trailing commas left from input
			}
			if (field.length() == 0){
				return "null";
			}
			if (field.charAt(0) == '"') {
				/*
				*	Remove outer quotes if they appear
				*	This allows us to parse multi-line objects and arrays
				*	If the quotes belong there they will be replaced
				*	Escape characters still need to be dealt with at this point (Doubled double quotes should be \")
				*/
				field = new StringBuilder(field.substring(1, field.length() - 1));
				field = new StringBuilder(this.stringHelper(field));
				if (field.length() == 0) return "\"\""; //For empty Strings we should return an empty String
			}
			if (field.charAt(0) == '[' && 
				field.charAt(field.length() - 1) == ']') {
				/*
				*	Checks if the contained information is an Array.
				*/
				field = new StringBuilder(this.arrayHelper(new StringBuilder(field.substring(
											1, field.length() - 1)))); //outer brackets removed for processing
			} else if (field.charAt(0) == '{' &&
				field.charAt(field.length() -1) == '}') {
				/*
				*	Checks if the contained information is an object
				*/
				field = new StringBuilder(this.objectHelper(new StringBuilder(field.substring(1, field.length() - 1))));
			}
			incomingField = field.toString();
			if (incomingField.matches("[0-9]+") || ((incomingField.charAt(0) == '[' || incomingField.charAt(0) == '{') &&
														incomingField.charAt(incomingField.length() - 1) == ']' ||
														incomingField.charAt(incomingField.length() - 1) == '}')) {
				return incomingField;
			} else {
				incomingField = "\"" + incomingField + "\"";
				return incomingField;
			}
	}
	
	private String arrayHelper(StringBuilder value){
		
		String returnVal = new String("");
		ArrayList<StringBuilder> helper = new ArrayList<StringBuilder>();
		
		if (value.toString().equals(returnVal)){
			// Empty arrays return just square braces back
			returnVal = returnVal + "[]";
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
			int prevIndex = 0;
			for (int i = 0; i < value.length(); i++){
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
						if (value.charAt(i) == ','){
							/*
							*	Breaks the field on commas which are not inside another element
							*/
							helper.add(new StringBuilder(value.substring(prevIndex, i)));
							prevIndex = i + 1;
						}
					}
				}
			} //end loop
			helper.add(new StringBuilder(value.substring(prevIndex, value.length())));
		}
		returnVal = "[";
		for (int i = 0; i < helper.size(); i++) {
			/*
			*	Deals with nested elements by calling the parent function on each field;
			*	Should handle internal string parsing, as well as object and array parsing
			*/
			helper.set(i, new StringBuilder(this.formatField(helper.get(i).toString())));
			if (i == 0) {
				returnVal = returnVal + helper.get(i).toString();
			} else {
				returnVal = returnVal + " , " + helper.get(i).toString();
			}
			
		}
		returnVal = returnVal + "]";
		
		return returnVal;
	}
	
	private String objectHelper(StringBuilder value) {
		
		ArrayList<String> tokens = new ArrayList<String>();
		String returnValue = "{";
		String helper = "";
		int prevIndex = 0;
		
		int quotes = 0;
		int curlyOpen = 0;
		int curlyClose = 0;
		int squareOpen = 0;
		int squareClose = 0;
		
		for (int i = 0; i < value.length(); i++) {
			/*
			*	Loops to find the comma seperating fields of the object
			*	Handles nested objects and arrays by passing back to the format method
			*/
			if(value.charAt(i) == '"') {
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
					if (value.charAt(i) == ',') {
						helper = value.substring(prevIndex, i);
						prevIndex = i + 1;
						helper = objectElementHelper(helper);
						returnValue = returnValue + helper + ", ";
					} //end of key:value pair separation
					/*
					* This is where we build the ArrayList
					*/
				} //end object/array check
			} //end outside of quotes check
		} //end loop
		helper = value.substring(prevIndex, value.length()); //Catch the last key:value pair (no comma after)
		helper = objectElementHelper(helper);
		returnValue = returnValue + helper;
		/*
		*	Loop through array list to rebuild the object w/ formatted strings
		*	to handle "key":<value>, even numbers must be quoted, odd numbers go through regular formatting.
		*/
		returnValue = returnValue + "}";
		return returnValue;
	}
	
	private String objectElementHelper(String helper){
		String[] keyAndValue = new String[2];
		int jQuotes = 0;
		String returnValue = "";
		for (int j = 0; j < helper.length(); j++) {
			/*
			*	* MOVED TO FUNCTION *
			*	Nested loop to look for the ':' separating key and value
			*	Strings.split() would not allow all valid values for either
			*	the key or the value, so we need to manually split.
			*/
			if (helper.charAt(j) == '\''){
				jQuotes++;
			}
			if (jQuotes % 2 == 0 && helper.charAt(j) == ':'){
				keyAndValue[0] = helper.substring(0, j).trim();
				keyAndValue[1] = helper.substring(j + 1, helper.length()).trim();
				keyAndValue[0] = "\"" + keyAndValue[0].substring(1, keyAndValue[0].length() - 1) + "\"";
				keyAndValue[1] = this.formatField(keyAndValue[1].substring(1, keyAndValue[1].length() - 1));
				returnValue = keyAndValue[0] + " : " + keyAndValue[1];
				break;
			} //end outside of quotes check
		} //end loop
		return returnValue;
	}
	
	private String stringHelper(StringBuilder value) {
		String helper = value.toString();
		helper = helper.replace("\n", "\\n");
		helper = helper.replace("\"\"", "\\\"");
		return helper;
	}
}