package Programs;

import DataObjs.MusicSheet;
import DataObjs.PianoProperties;
import Processors.AlcReaderWriter;
import Processors.FngWriter;
import Processors.Hand;
import Translators.TransMusicXML;
import Utils.AlcStatsUtils;
import Utils.Constants;

/**
 * NoteTransposer
 * This program is intended to read a music file in some standard format (TBD - MusicXML?), and convert it into my data format (.alc - alchemized music data file)
 * (If the provided music file is already in my .alc format, this translation step will be skipped.)
 * Once the data is in my format, the program will then proceed to process the data and create instructions that assign the arduino's "fingers" their
 * specific movements, along with when and for how long, which will allow the arduino to then perform the music piece with its "fingers."
 * 
 * @author smartel
 */
public class NoteTransposer {

	public static void main(String[] args) {
		PianoProperties properties;
		AlcReaderWriter arw;
		AlcStatsUtils asu;
		MusicSheet sheet;
		FngWriter fw;
		String propertiesPath; // path to the 
		String targetFilePath; // path to the target input file to translate to .alc / transpose to arduino code
		String alcFilePath; // path to write the output alc file to (only used if translating from some other format like .musicxml)
		String fngFilePath; // path to write the output fng file to
		String statsFilePath; // path to write the output .stats file to
		int bpmMultiplier;
		boolean isSuccessful = true;
		int numMandatoryArgs = 3;
	
		// Usage:
		// 1. piano_properties file path to setup the internal piano data (how many keys, fingers, ...)
		// 2. file path to desired file to translate, including filename and extension
		// 3. bpm-multiplier
		// 4. OPTIONAL - file path to write the output .alc file to, including filename and extension
		//               (if not provided, will be the same path as the input file, but swap the previous extension for .alc if translating)
		//               (if an .alc conversion isn't necessary, this value will be ignored)
		// 5. OPTIONAL - file path to write the output .fng file to, including filename and extension
		//               (if not provided, will be the same path as the input file, but swap the previous extension for .fng)
		// 6. OPTIONAL - file path to write the output .stats file to, including filename and extension
		//               (if not provided, will be the same path as the input file, but swap the previous extension for .stats)
		
		if (args.length < 3) {
			System.out.println("NoteTransposer#main usage:  {String: input piano_properties file path, String: input .xml file path, Integer: bpm-multiplier, Optional: String: output .alc file path, Optional: String: output .fng file path, Optional: String: output .stats file path} ");
			System.out.println("NoteTransposer#main - Please provide a filepath to a Piano Properties file, a filepath to a music data file (.alc, musicxml) to convert for the arduino player, a bpm-multiplier (the lower the integer, the faster the song will play), and optionally, an output filepath for the generated .alc file. Gracefully exiting.");
		} else {
			propertiesPath = args[0];
			targetFilePath = args[1];
			
			try {
				properties = new PianoProperties(propertiesPath);				
				if (!properties.didLoad()) {
					throw new Exception("Invalid properties file provided. Aborting execution.");
				}
				
				bpmMultiplier = Integer.parseInt(args[2]);

				// if the file needs to be translated and an optional output alc filepath wasn't provided, display the path that will be used
				if (args.length == numMandatoryArgs) {
					alcFilePath = targetFilePath.substring(0, targetFilePath.lastIndexOf(".")); // shave off the extension
					alcFilePath += ".alc";
					System.out.println("NoteTransposer#main - No optional filepath was provided for the output .alc file. The following path will be used: " + alcFilePath);
				} else {
					// use the provided .alc filepath when writing the output file
					alcFilePath = args[3];
				}
			
				if (targetFilePath.endsWith(".xml") || targetFilePath.endsWith(".musicxml")) {
					// Call MusicXML translator
					TransMusicXML transXml = new TransMusicXML();
					isSuccessful = transXml.parseMusicXMLFile(targetFilePath, alcFilePath, bpmMultiplier);
				}
				
				// if supplied with an alc file, or if there was a successful translation of a different file format, we can create arduino code
				if (targetFilePath.endsWith("alc") || 
		           ((targetFilePath.endsWith(".xml") || targetFilePath.endsWith(".musicxml")) && isSuccessful) ) {

					// import the alc format into a MusicSheet for further processing
					arw = new AlcReaderWriter();
					sheet = arw.loadAlcFile(alcFilePath);
		 			
					if (sheet != null) { // if we successfully loaded the .alc file
						
						// heck, write out a .stats file for kicks, why not, might be useful if using a limited finger implementation, saves time by not having to run StatsGenerator separately
						asu = new AlcStatsUtils();
						if (args.length < numMandatoryArgs+2) {
							statsFilePath = targetFilePath.substring(0, targetFilePath.lastIndexOf(".")); // shave off the extension
							statsFilePath += ".stats";
							System.out.println("NoteTransposer#main - No optional filepath was provided for the output .stats file. The following path will be used: " + statsFilePath);
						} else {
							// use the provided .stats filepath when writing the output file
							statsFilePath = args[5];
						}
						if (asu.writeFullStatsToFile(sheet, false, statsFilePath)) {
							System.out.println("NoteTransposer#main - Successfully wrote output .stats file to path: " + statsFilePath);
						} else {
							System.out.println("NoteTransposer#main - Failed to write output .stats file to path: " + statsFilePath);
						}
						
						int fingerImpl = Integer.parseInt(properties.getSetting(Constants.SETTINGS_FINGER_IMPL));
						if (fingerImpl == Constants.FINGER_IMPL_FULL || fingerImpl == Constants.FINGER_IMPL_LIMITED || fingerImpl == Constants.FINGER_IMPL_SLIDING) {
	
							// create the finger assignments using the alc file
							if (sheet != null) {
								Hand hand = new Hand(properties, sheet);
								if (hand.didInit()) {
									fw = new FngWriter();
									
									// if an optional output fng filepath wasn't provided, display the path that will be used
									if (args.length < numMandatoryArgs+1) {
										fngFilePath = targetFilePath.substring(0, targetFilePath.lastIndexOf(".")); // shave off the extension
										fngFilePath += ".fng";
										System.out.println("NoteTransposer#main - No optional filepath was provided for the output .fng file. The following path will be used: " + fngFilePath);
									} else {
										// use the provided .fng filepath when writing the output file
										fngFilePath = args[4];
									}
									
									if (fw.writeFngFromSheet(hand, sheet, fngFilePath)) {
										System.out.println("\nNoteTransposer#main - Successfully converted the .alc file into an .fng file!");
									} else {
										System.out.println("\nNoteTransposer#main - Failed to convert the .alc into an .fng. Exiting.");
									}
								}
							}
						} else {
							System.out.println("NoteTransposer#main - Finger implementation is not FULL, LIMITED, or SLIDING, so no arduino / finger code will be written. Exiting.");
						}
					}
				}
			} catch (NumberFormatException e) {
				System.out.println("NoteTransposer#main - Please provide a valid integer to use for the bpm-multiplier. Value passed in: [" + args[2] + "]. Gracefully exiting.");
			} catch (Exception e) {
				System.out.println("NoteTransposer#main - Exception caught: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}
	

}
