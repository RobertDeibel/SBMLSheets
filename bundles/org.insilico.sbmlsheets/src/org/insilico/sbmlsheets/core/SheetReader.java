package org.insilico.sbmlsheets.core;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class SheetReader {
	private static final char DEFAULTSEPERATOR = ',';
	private static final char DEFAULTQUOTE ='"';
	
	public static Spreadsheet readSheetFromFile(String uri) {
		
		try (Scanner s = new Scanner(new FileReader(uri))){
			if(!s.hasNextLine()) {
				return new Spreadsheet(uri);
			}
			List<List<String>> data = new ArrayList<>();
			while (s.hasNextLine()) {
				List<String> line = parseLine(s.nextLine());
				data.add(line);
			}
			s.close();
			return new Spreadsheet(data, "", "", uri);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Error while parsing csv or tsv file: Inputerror");
			e.printStackTrace();
		} catch (NoSuchElementException e) {
			System.err.println("Error while parsing csv or tsv file: File empty");
//			e.printStackTrace();
		}
		
		return new Spreadsheet(uri);
	}
	
	private static List<String> parseLine(String nextLine) {
		return parseLine(nextLine, DEFAULTSEPERATOR, DEFAULTQUOTE);
	}
	

	private static List<String> parseLine(String nextLine, char separator, char quote) {
		List<String> result = new ArrayList<String>();
		
		if (nextLine==null && nextLine.isEmpty()) {
			return result;
		}
		
		if (quote == ' ') {
			quote = DEFAULTQUOTE;
		}
		
		if (separator == ' ') {
			separator = DEFAULTSEPERATOR;
		}
		
		StringBuffer curVal = new StringBuffer();
		boolean inQuotes = false;
		boolean startCollectChar = false;
		boolean doubleQuotesInCol = false;
		
		 char[] lineAsChars = nextLine.toCharArray();
		
		for (char ch : lineAsChars) {

            if (inQuotes) {
                startCollectChar = true;
                if (ch == quote) {
                    inQuotes = false;
                    doubleQuotesInCol = false;
                } else {

                    if (ch == '\"') {
                        if (!doubleQuotesInCol) {
                            curVal.append(ch);
                            doubleQuotesInCol = true;
                        }
                    } else {
                        curVal.append(ch);
                    }

                }
            } else {
                if (ch == quote) {
                    inQuotes = true;
                    if (lineAsChars[0] != '"' && quote == '\"') {
                        curVal.append('"');
                    }
                    if (startCollectChar) {
                        curVal.append('"');
                    }

                } else if (ch == separator) {
                    result.add(curVal.toString());
                    curVal = new StringBuffer();
                    startCollectChar = false;
                } else if (ch == '\r') {
                    continue;
                } else if (ch == '\n') {
                    break;
                } else {
                    curVal.append(ch);
                }
            }

        }

        result.add(curVal.toString());

        return result;
	}

	public static SheetProject readProjectFromFile(String uri) {
		//TODO
		return new SheetProject();
	}


}
