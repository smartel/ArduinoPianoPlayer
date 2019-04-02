package Programs;

import Translators.TransMusicXML;

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
		String targetFilePath; // path to the target input file to translate to .alc / transpose to arduino code
		String alcFilePath; // path to write the output alc file to (only used if translating from some other format like .musicxml)
		int bpmMultiplier;
		boolean isSuccessful = true;
	
		// Usage:
		// 1. file path to desired file to translate, including filename and extension
		// 2. bpm-multiplier
		// 3. OPTIONAL - file path to write the output .alc file to, including filename and extension
		//               (if not provided, will be the same path as the input file, but swap the previous extension for .alc if translating)

		if (args.length < 2) {
			System.out.println("NoteTransposer#main usage:  {String: input .xml file path, Integer: bpm-multiplier, String: output .alc file path} ");
			System.out.println("NoteTransposer#main - Please provide a filepath to a music data file (.alc, musicxml) to convert for the arduino player, a bpm-multiplier (the lower the integer, the faster the song will play), and optionally, an output filepath for the generated .alc file. Gracefully exiting.");
		} else {
			targetFilePath = args[0];
			try {
				bpmMultiplier = Integer.parseInt(args[1]);

				// if the file needs to be translated and an optional output alc filepath wasn't provided, display the path that will be used
				if (args.length == 1 && (!targetFilePath.contains(".alc"))) {
					alcFilePath = targetFilePath.replace(".musicxml", ".alc");
					System.out.println("NoteTransposer#main - No optional filepath was provided for the output .alc file. The following path will be used: " + alcFilePath);
				} else {
					// use the provided .alc filepath when writing the output file
					alcFilePath = args[2];
				}
			
				if (targetFilePath.endsWith(".xml") || targetFilePath.endsWith(".musicxml")) {
					// Call MusicXML translator
					TransMusicXML transXml = new TransMusicXML();
					isSuccessful = transXml.parseMusicXMLFile(targetFilePath, alcFilePath, bpmMultiplier);
				}
				
				// if supplied with an alc file, or if there was a successful translation of a different file format, we can create arduino code
				if (targetFilePath.endsWith("alc") || 
		           ((targetFilePath.endsWith(".xml") || targetFilePath.endsWith(".musicxml")) && isSuccessful) ) {

					// TODO
					// create the arduino code / finger assignments using the alc file
					
				}
				
			
			} catch (NumberFormatException e) {
				System.out.println("NoteTransposer#main - Please provide a valid integer to use for the bpm-multiplier. Value passed in: [" + args[1] + "]. Gracefully exiting.");
			}
		}
	}
}
