package org.insilico.sbmlsheets.core;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Class for writing to a .csv file
 * @author Robert Deibel
 *
 */
public class SheetWriter {
	
	
	public static void writeSheetToFile(String uri, String out) {
		try {
			FileWriter writer = new FileWriter(uri, false);
			writer.write(out);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void newCSV(String filePath) {
			try {
				FileWriter writer = new FileWriter(filePath,false);
				writer.write("");
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}


}
