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

		// TODO input args
		// 1. file path to desired file to translate, including filename and extension
		// 2. OPTIONAL - file path to write the output .alc file to, including filename and extension
		//               (if not provided, will be the same path as the input file, but swap the previous extension for .alc if translating)
		// ?  anything else? a properties file so we know how many keys are on the piano, etc? Anything else would likely need to push optionals to the end of the list, so properties would be the new arg[1] and so on. this would need to be reflected in several conditionals or statements that check "if size > 1", "size == 1 and doesnt contain .alc", "alcFilePath = args[1]" and so on.
		//    ? why do we need to know the size of the piano when translating to .alc? the .alc output itself is valid regardless of whether a given piano's range can play it. some other process should check it, like at transpose time (when writing to arduino code for a specific piano). Although that'd still be NoteTransposer.java. Ok.
		
		if (args.length < 1) {
			System.out.println("Please provide a filepath to a music data file (.alc, musicxml) to convert for the arduino player. Gracefully exiting.");
		} else {
			targetFilePath = args[0];
			
			// if the file needs to be translated and an optional output alc filepath wasn't provided, display the path that will be used
			if (args.length == 1 && (!targetFilePath.contains(".alc"))) {
				alcFilePath = targetFilePath.replace(".musicxml", ".alc");
				System.out.println("No optional filepath was provided for the output .alc file. The following path will be used: " + alcFilePath);
			} else {
				// use the provided .alc filepath when writing the output file
				alcFilePath = args[1];
			}
		
			if (targetFilePath.endsWith(".xml") || targetFilePath.endsWith(".musicxml")) {
				// Call MusicXML translator
				TransMusicXML transXml = new TransMusicXML();
				transXml.parseMusicXMLFile(targetFilePath, alcFilePath);
			}
			
			// TODO implement
			// Step 1. Translate the data file *if needed* (ie musicxml passed in, convert to alc)
				// a. determine the file format
				// b. call the appropriate Translator if needed
				//    ba. see if translating it returned true
			// Step 2. Read your format into a MusicSheet
			// Step 3. Determine finger assignments to output a finger file
			
		
		}
	}
}
