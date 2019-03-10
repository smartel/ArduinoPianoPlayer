package Programs;

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
		
		// TODO implement
		// Step 1. Translate the data file *if needed* (ie musicxml passed in, convert to alc)
			// a. determine the file format
			// b. call the appropriate Translator if needed
		// Step 2. Read your format into a MusicSheet
		// Step 3. Determine finger assignments to output a finger file
		
		
		
	}
	
}
