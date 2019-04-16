package Programs;

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;

import DataObjs.MusicSheet;
import Processors.AlcReaderWriter;
import Utils.AlcStatsUtils;

/**
 * Given an .alc file, will print out the music stats generated for that file
 * 
 * @author smartel
 */
public class StatsGenerator {

	/**
	 * @param args file path to the .alc file to generate stats from
	 */
	public static void main(String[] args) {
		String alcFilePath;
		String statsFilePath;
		boolean displayNonHitNotes = false;
		
		if (args.length >= 2) { // if the 2 required input args are present, and possibly an optional arg
			alcFilePath = args[0];
			statsFilePath = args[1];
			if (args.length > 2) { // if the optional arg is present
				if (args[2].equalsIgnoreCase("1")) {
					displayNonHitNotes = true;
				}
			}
			try {
				AlcReaderWriter arw = new AlcReaderWriter();
				MusicSheet sheet = arw.loadAlcFile(alcFilePath);
				AlcStatsUtils asu = new AlcStatsUtils();

				asu.generateCompValStats(sheet);
				String stats = asu.getFullStats(sheet, displayNonHitNotes);
				
				System.out.println(stats);
				BufferedWriter bw = new BufferedWriter(new FileWriter(new File(statsFilePath)));
				bw.write(stats);
				bw.close();
				
			} catch (Exception e) {
				System.out.println("StatsGenerator#main: exception caught trying to generate stats for file: " + alcFilePath);
			}
		} else {
			System.out.println("StatsGenerator#main - usage: {filepath to alc file} {output file path to write the stats to} {optional: 1 if you want to display details for notes that were never hit}");
			System.out.println("StatsGenerator#main - Please provide a filepath to an .alc file to generate stats for and an output path to write a .stats file to. Gracefully exiting.");
		}
		
	}
	
}
