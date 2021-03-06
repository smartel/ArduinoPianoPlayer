package Processors;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.LinkedList;
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
public class AlcReaderWriter {
	
	public AlcReaderWriter() {
	}
	
	/**
	 * Just a synonym for loadAlcFile :^)
	 */
	public MusicSheet readAlcFile(String alcFilePath) {
		return loadAlcFile(alcFilePath);
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
		int duplicatesCaught = 0;

		int prevStartTime = -1;
		StringTokenizer st;
		
		int currStartTime;
		double compareValue;
		int noteDuration;
		
		// TODO While I removed rest instructions from legacy .alc files a while ago, and regenerated those .alc files that had rests,
		//      I guess it still bugs me that someone could manually add rests back in, and that may throw off stuff like the StatsGenerator's values or how many fingers could be needed (even though rests aren't "hit").
		//      Should we throw an error or exception if we see a rest compare value when attempting to load an .alc file?
		//      Should we try to fix it here (by erasing it and decrementing the note counter in the file), or is that too invasive, since a method called >load shouldn't be >overwriting the existing file.
		//      The AlcAlterer cleans up rests already, so I'd rather not duplicate any cleanup code here anyway. Erroring out and saying when the rest is, allows the user to manually decrement the counter by 1 and delete the line.
		//        If there are tons of rests, the AlcAlterer would trivially clean it up programmatically.
		//      The AlcAlterer relies on first doing importAlcFileWithPlaceholders() anyway, as in, it expects there could be rests for it to ignore.
		//      I don't know, just some thoughts.
		
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
				} else {
					// all lines after the first 2 contain note information.
					// if there are not enough tokens in a line, or bad data (such as non-numeric characters), an exception will be thrown and the load aborted.
					st = new StringTokenizer(line);
					currStartTime = Integer.parseInt(st.nextToken());
					compareValue = Double.parseDouble(st.nextToken());
					noteDuration = Integer.parseInt(st.nextToken());

					MusicNote note = new MusicNote(compareValue, noteDuration);
					
					if (slice == null) {
						slice = new MusicSlice(currStartTime);
					}
					
					// initializing prevStartTime if it hasn't already.
					// we initialize here, so the first line in an .alc file doesn't HAVE to start on a 0.
					if (prevStartTime == -1) {
						prevStartTime = currStartTime;
					}

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
						slice = new MusicSlice(currStartTime);
						slice.addMusicNote(note);
						prevStartTime = currStartTime;
					} else {
						// if we fail to add a note (such as a duplicate), then we need to decrement the valid note count
						if (!slice.addMusicNote(note)) {
							--noteCount;
							++duplicatesCaught;
							sheet.setNoteCount(noteCount);
						}
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
		} else if (noteCount != (noteLinesReadIn-duplicatesCaught)) { // if we fail the integrity check, report an error and return a null object
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



	public boolean writeAlcFile(MusicSheet sheet, String outputPath) {
		boolean isSuccessful = true;
		LinkedList<MusicSlice> slices;
		MusicSlice slice;
		String line;
		
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(outputPath)));
			bw.write(sheet.getInfoLine());
			bw.newLine();
			bw.write(sheet.getNoteCount() + "");
			bw.newLine();
			
			slices = sheet.getSlices();
			for (int x = 0; x < slices.size(); ++x) {
				slice = slices.get(x);
				Iterator<MusicNote> iter = slice.getNotes().iterator();
				while (iter.hasNext()) {
					MusicNote note = iter.next();
					// Generate the line for this MusicNote as it would appear in an .alc file, ie:
					// {start time in ms} {compareValue} {duration}
					line = slice.getStartTime() + " " + note.getCompareValue() + " " + note.getDuration();
					bw.write(line);
					bw.newLine();
				}
			}
			bw.flush();
			bw.close();
		} catch (Exception e) {
			System.out.println("AlcReaderWriter#writeAlcFile - error - exception caught attempting to write .alc file: " + e.getMessage());
			e.printStackTrace();
			isSuccessful = false;
		}
		return isSuccessful;
	}
}