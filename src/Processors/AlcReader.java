package Processors;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.StringTokenizer;

import DataObjs.MusicNote;
import DataObjs.MusicSheet;
import DataObjs.MusicSlice;

/**
 * Given the filepath to an .alc file, this will read its contents into memory (MusicSheet/Slice/Note)
 * while also performing minor error + integrity checking.
 * 
 * @author smartel
 */
public class AlcReader {

	public AlcReader() {
	}
	
	/**
	 * Given the file path to an .alc file, attempts to import it and create a MusicSheet object comprised of MusicSlices and MusicNotes.
	 * If the data load fails for any reason, an error will be written out and a null MusicSheet object will be returned instead.
	 * @param alcFilePath filepath to the desired .alc file to load
	 * @return created MusicSheet object for the .alc file, or null if there were any errors
	 */
	public MusicSheet loadAlcFile(String alcFilePath) {
		MusicSheet sheet = null;
		MusicSlice slice = null;
		
		String headerLine = null;
		String countLine = null;
		int noteCount = -1;
		int noteLinesReadIn = 0;

		String noteLine = null;
		int prevStartTime = 0;
		StringTokenizer st;
		
		int currStartTime;
		double compareValue;
		int noteDuration;
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(alcFilePath)));
			while (br.ready()) {
				String line = br.readLine();
				
				if (headerLine == null) {
					// first line is informational only
					headerLine = line;
				}
				else if (countLine == null) {
					// second line is an integrity check line - it holds the count of notes (the number of following lines) in the song
					countLine = line;
					noteCount = Integer.parseInt(countLine); // if this fails, we throw an exception and return a null MusicSheet. Checking for nullness is how we determine if a file loaded successfully.
					sheet = new MusicSheet(headerLine, noteCount);
					slice = new MusicSlice();
				} else {
					// all lines after the first 2 contain note information.
					// if there are not enough tokens in a line, or bad data (such as non-numeric characters), an exception will be thrown and the load aborted.
					st = new StringTokenizer(line);
					currStartTime = Integer.parseInt(st.nextToken());
					compareValue = Double.parseDouble(st.nextToken());
					noteDuration = Integer.parseInt(st.nextToken());

					MusicNote note = new MusicNote(compareValue, noteDuration);

					// if the currStartTime is greater than prevStartTime, this means we're in a new time slice.
					// we need to add the old slice to the music sheet object and construct a new slice to put this music note into.
					// otherwise, the start times are the same, so just add it to the existing slice.
					if (prevStartTime != currStartTime) {
						
						// data integrity check: if the current line's start time occurs EARLIER than the previous line's, then we have bad data, most likely due to hand-editing of the file.
						if (prevStartTime > currStartTime) {
							System.out.println("AlcReader#loadAlcFile - error - note data is out of order. Aborting load. Confirm - prevStartTime: " + prevStartTime + ", currStartTime: " + currStartTime);
							sheet = null;
							break;
						}
						
						sheet.addSlice(slice);
						slice = new MusicSlice();
						slice.addMusicNote(note);
						prevStartTime = currStartTime;
					} else {
						slice.addMusicNote(note);
					}
					
					// counter for our file integrity check
					++noteLinesReadIn;
				}
			}
			if (sheet != null) {
				sheet.addSlice(slice); // this is necessary because the newest slice hasn't been added to the musicsheet yet, due to how the while-loop handles old/new slices.
			}
			br.close();
		} catch (Exception e) {
			System.out.println("AlcReader#loadAlcFile - error - exception occurred while reading .alc file at path: [" + alcFilePath + "]. Exception: " + e.getMessage());
			e.printStackTrace();
			sheet = null; // just ensuring we return null on an exception, although depending on when the failure occurred, it may have still been null.
		}

		if (headerLine == null || countLine == null) { // if the file was incomplete (only 1 or 2 lines in length), then there isn't enough data to play a song
			System.out.println("AlcReader#loadAlcFile - error - .alc file did not have at least 3 lines (1 informational, 1 counter, at least 1 note). File is too small to process.");
			sheet = null; // the object may not be null if the file was 2 lines long, so this ensures we return null
		} else if (noteCount != noteLinesReadIn) { // if we fail the integrity check, report an error and return a null object
			System.out.println("AlcReader#loadAlcFile - error - .alc file failed the file integrity check. The number of notes read in does not match the expected note count." +
						       " Read in: " + noteLinesReadIn + ", Expected: " + noteCount);
			sheet = null;
		} else if (noteCount == 0) { // if there was a note count of 0, then it is an (intentionally?) empty alc file
			System.out.println("AlcReader#loadAlcFile - error - .alc file had a value of 0 in the note count line. File integrity can't be validated. File will not be processed.");
			sheet = null;
		} else if (noteLinesReadIn == 0) { // if there were no notes read in, we can't play a song. Although I don't think this conditional is capable of being hit.
			System.out.println("AlcReader#loadAlcFile - error - .alc file contains 0 note lines. File will not be processed.");
			sheet = null;
		}

		return sheet;
	}
	
	/* TODO potential bug discovered 3-24:
	// If an alc file is written with start times out of order, for example:
	7000 12 1000
	7000 19 1000
	8000 13 1000
	8000 20 1000
	9000 13.5 1000
	9000 20.5 1000
	10000 14 1000
	10000 21 1000
	11000 14.5 1000
	11000 21.5 1000
	7000 15 1000
	7000 22 1000
	8000 16 1000
	8000 23 1000
	9000 17 1000
	9000 24 1000
	// The 2nd set of notes starting at 7000, 8000, 9000, are not added to the original music slices created for the first 7000, 8000, 9000. They are instead appended to the linkedlist
	// as new slices appearing after 11000, which can result in playback errors when read by the PianoFeigner.
	// A simple solution comes at a performance cost - see if an existing slice already has the current note's start time. If it does, add it to that slice instead.
	// However, this bug presumably would only occur when hand-editing an .alc file and making a mistake. Will gauge later how much of a priority this fix is.
	// Perhaps it would be better to have a data integrity check, where if the next line's start time is LESS than the previous lines, we throw a warning or error.
	*/
}
