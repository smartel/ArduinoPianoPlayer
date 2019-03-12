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

		// TODO input args? file path to desired file to translate? anything else? a properties file so we know how many keys are on the piano, etc?
		
		if (args.length != 1) {
			System.out.println("Please provide a filepath to a music data file (.alc, musicxml) to convert for the arduino player. Gracefully exiting.");
		} else {
			String filePath = args[0];
		
			if (filePath.endsWith(".xml") || filePath.endsWith(".musicxml")) {
				// Call MusicXML translator
				TransMusicXML transXml = new TransMusicXML();
				transXml.parseMusicXMLFile(filePath);
			}
			
			// TODO implement
			// Step 1. Translate the data file *if needed* (ie musicxml passed in, convert to alc)
				// a. determine the file format
				// b. call the appropriate Translator if needed
				//    ba. see if translating it returned true. do we want to pass in a file name for it to export the .alc file, or just let it swap the extension and call it good?
			// Step 2. Read your format into a MusicSheet
			// Step 3. Determine finger assignments to output a finger file
			
		
		}
	}
}
