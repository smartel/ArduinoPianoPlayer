package Programs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.StringTokenizer;

import DataObjs.MusicNote;
import DataObjs.MusicSheet;
import DataObjs.MusicSlice;
import Processors.AlcReaderWriter;
import Utils.Constants;
import Utils.NoteUtils;

public class AlcAlterer {

	// The goal of this program is:
	// Given an alc file and some modification(s) to be made, perform the modification, and output a new alc file with the modifications (don't alter the source file).
	// There will be at minimum 3 input arguments:
	// 1. source .alc file to import, that wants desired changes
	// 2. output .alc file path without the desired modification(s), but it will be cleaned up / have notes translated to compareValues if they had placeholders
	//   (we are expecting to use this with a lot of hand-translated source files)
	// 3. output .alc file path that is cleaned up AND contains desired modification(s)
	
	// STEPS:	
	// 1. Read everything in / translate / cleanup
	// 	a. Translate from placeholders to compareValues if necessary
	// 	b. Ignore REST lines entirely.
	// 	c. Duplicate notes will be discarded when they attempt to add to the MusicSlice
	// 	d. Generate a new integrity note counter (since it may have changed from dropping duplicates, rest notes, ...)
	// 	e. Ensure the notes are Sorted (they should be - MusicSlice is a TreeSet and MusicNote has a Comparator)
	//  	This is because: when editing .alc files with mass Find and Replace All commands, and there are multiple notes occurring on the same timestamp,
	//  	it can result in "out of order" notes, where larger compareValues are shown before smaller ones, which creates extra hits when trying to diff two .alc files.
	// 		Ensuring they are sorted simply helps us have consistent .alc files.
	
	// 2. Write the intermediate .alc file out (the filepath that is cleaned up but no big modification(s) yet. may be useful for diffing against changes in the modified .alc file)
	
	// 3. Perform desired change:
	// a. Mass adjusting the bpm after-the-fact (by multiplying all durations and start times)
	// b. "Increase every octave by x" option. This could be negative, which means decreasing every octave by x instead. A flag can be included to cap notes at boundaries or delete them.
	// c. Looper - will loop the song an additional x times within the .alc file

	
	// 4. Perform cleanup again
	// a. Performing the cleanup steps a second time ensures we have an accurate integrity note counter, ... after making modification(s) to the note content.
	
	// 5. Write out the new .alc file with modification(s)
	
	
	

	// Potential unimplemented change ideas are:
	// x. shift left or right by x steps / tones? including half steps, i mean, go from an f to an f sharp? or be able to change from a major key to minor key and so on?
	// x. I'm not sure how easy it would be to mark a section of the alc file for deletion. Like, say you wanted to delete all notes that start from 1400ms to 1800ms in a song.
	//    You'd need to be able to specify a start point, an end point, and then the program would mass update the start times of every note after 1800ms to what, start 400ms earlier?
	//    Not sure how valuable this would be yet.
	//    Alternatively, what if we want to delete a whole octave instead of a time range? Like, the song has a ton of notes in octaev 0 and we don't have fingers for it.
	//    Alternatively, the reverse of either of these: we want to keep only the parts of the song between starting time x and ending time y, or in range of octave x to octave y.
	// x. Perhaps one day we might want to be able to target specific compare values and replacements for them? Such as, if we generate an alc file with one key out of range of the piano,
	//    or if a finger was damaged in some way. Or if the .stats generated for a song has a lot of hits outside of the playable range per PianoProperties.
	//    We could target compareValue xyz and tell it to replace it with compareValue abc? Or heck, replace it with 0, which will then be cut from the file and we get a new integrity count.
	// x. "Wiggle" an octave left or right if the note is out of range? That is, if the octave is too low by one, then add an octave to the note. If it is too high, subtract one.
	//    Then we get into the issue of >what if it is now a duplicate (a note was already at that position), and then we need to subtract 1 from the note counter, unless we wiggle again, but that'd sound way off.
	
	// Of course, there may be more cases that come up where we go "oh hey, being able to edit an alc file to add / modify / remove blah would be great", so we could just shove it in here...
		
	
	
	public static void main(String[] args) {
		MusicSheet sheet;
		if (args.length < 3) {
			System.out.println("AlcAlterer#main - usage: {input alc file path} {translated alc file path} {output alc file path} {optionals ...}\n" +
							   "Optionals set 1: \"" + Constants.BPM_OPTION + "\" {integer} - if the first optional argument is \"" + Constants.BPM_OPTION + "\", and the second is a positive integer, the integer will be used as a bpm-multiplier on the target alc file.\n" +
							   "Optionals set 2: \"" + Constants.OCTAVE_OPTION + "\" {integer} {\"true\" or \"false\"} - if the first optional argument is \"" + Constants.OCTAVE_OPTION + "\", and the second is a non-zero integer, the integer will be used to adjust notes up (if positive) or down (if negative) that many octaves within the target alc file. If the 3rd optional argument is \"true\", than any notes pushed below octave 1 or above octave 8 will be DELETED, otherwise they will cap at octave 1 or 8. By default, notes cap at the boundaries.\n" +
							   "Optionals set 3: \"" + Constants.LOOP_OPTION + "\" {integer} - will loop the song an additional {integer} number of times in the alc file.\n" +
		                       "Exiting."); // more options TBD, will need to be added to usage as we implement them
		} else {
			String inputAlcPath = args[0];
			String outputTranslatedPath = args[1];
			String outputAlcPath = args[2];
			
			AlcAlterer aa = new AlcAlterer();
			AlcReaderWriter arw = new AlcReaderWriter();
			
			// Step 1 - read in and translate placeholders (if needed)
			sheet = aa.importAlcFileWithPlaceholders(inputAlcPath);
			
			// Step 2 - write out intermediate / cleaned up file
			if (sheet != null) {
				arw.writeAlcFile(sheet, outputTranslatedPath);
				// verify the AlcReader can read it back in
				sheet = arw.loadAlcFile(outputTranslatedPath);
				if (sheet == null) {
					System.out.println("AlcAlterer#main - error - verification load failed for the translated .alc file written to: " + outputTranslatedPath + ". Translation failed.");
				}
			}
			
			// Step 3 - perform changes			
			if (sheet != null && args.length > 3) {
				
				if (args[3].equalsIgnoreCase(Constants.BPM_OPTION)) {
					if (args.length >= 5) {
						try {
							int bpmMult = Integer.parseInt(args[4]);
							if (bpmMult <= 0) {
								System.out.println("AlcAlterer#main - error - Please provide a positive integer value to use as the desired bpm-multiplier. Exiting.");
							}
							
							// we have a valid value - loop through the musicsheet and update everything's startTime and duration by the bpmMultiplier.
							// Slices will be responsible for updating their individual notes.
							LinkedList<MusicSlice> slices = sheet.getSlices();
							for (int x = 0; x < slices.size(); ++x) {
								MusicSlice slice = slices.get(x);
								slice.applyBpmMultipler(bpmMult);
							}
							
							sheet.setInfoLine(sheet.getInfoLine() + " - AlcAlterer multiplied bpm by " + bpmMult + ".");
						} catch (NumberFormatException e) {
							System.out.println("AlcAlterer#main - error - Please provide a positive integer value to use as the desired bpm-multiplier. Exiting.");
						}
					} else {
						System.out.println("AlcAlterer#main - error - Please provide a positive integer value to use as the desired bpm-multiplier. Exiting.");
						sheet = null;
					}
				} else if (args[3].equalsIgnoreCase(Constants.OCTAVE_OPTION)) {
					if (args.length >= 5) {
						try {
							boolean doDeleteNotes = false;
							if (args.length >= 6) {
								String capFlag = args[5];
								if (capFlag.equalsIgnoreCase("true")) {
									doDeleteNotes = true;
								}
							}
							int octaveAdjustment = Integer.parseInt(args[4]);
							if (octaveAdjustment == 0) {
								System.out.println("AlcAlterer#main - error - Please provide a non-zero integer value to use as the desired octave adjustment. Exiting.");
							} else {
								
								if (Math.abs(octaveAdjustment) >= Constants.MAX_PIANO_OCTAVE && doDeleteNotes) {
									System.out.println("AlcAlterer#main - warning - The program will continue, but it is strongly recommended not to use such a large octave adjustment value with the delete flag turned on, as all notes will just be pushed to the highest / lowest octave and deleted.");
								}
								
								// we have a valid value - loop through the musicsheet and update every note's octave by the supplied amount, "deleting" it if necessary (over the bounds. we'll just turn it into a rest, cleanup will later delete it).
								
								LinkedList<MusicSlice> slices = sheet.getSlices();
								for (int x = 0; x < slices.size(); ++x) {
									MusicSlice slice = slices.get(x);
									Iterator<MusicNote> iter = slice.getNotes().iterator();
									while (iter.hasNext()) {
										MusicNote note = iter.next();
										note.applyOctaveAdjustment(octaveAdjustment, doDeleteNotes);
									}
								}
							}
							
							sheet.setInfoLine(sheet.getInfoLine() + " - AlcAlterer adjusted octave value by " + octaveAdjustment + ".");
						} catch (NumberFormatException e) {
							System.out.println("AlcAlterer#main - error - Please provide a non-zero integer value to use as the desired octave adjustment. Exiting.");
						}
					} else {
						System.out.println("AlcAlterer#main - error - Please provide a non-zero integer value to use as the desired octave adjustment. Exiting.");
						sheet = null;
					}
				} else if (args[3].equalsIgnoreCase(Constants.LOOP_OPTION)) {
					if (args.length >= 5) {
						try {
							int loopCount = Integer.parseInt(args[4]);
							if (loopCount <= 0) {
								System.out.println("AlcAlterer#main - error - Please provide a positive integer value to use as the desired number of loops. Exiting.");
							}

							// we have a valid loop value - first, create a COPY of the existing music sheet, to use as a baseline reference
							MusicSheet sheetCopy = new MusicSheet(sheet);
							// get the length of time of an entire loop
							int loopTime = sheet.getEndTime();
							int loopNoteCount = sheet.getNoteCount();
							
							// for the number of desired loops, create a copy of our baseline reference, adjust the start times by using the loopTime and number of loops we're at,
							// and append the new slices into the original MusicSheet, and increase the noteCount by the original total again
							for (int x = 0; x < loopCount; ++x) {
								MusicSheet loopSheet = new MusicSheet(sheetCopy);
								LinkedList<MusicSlice> loopSlices = loopSheet.getSlices();
								for (int y = 0; y < loopSlices.size(); ++y) {
									loopSlices.get(y).setStartTime(loopSlices.get(y).getStartTime() + (loopTime * (x+1)));
									sheet.addSlice(loopSlices.get(y));
								}
								sheet.setNoteCount(sheet.getNoteCount() + loopNoteCount);
							}
							
							sheet.setInfoLine(sheet.getInfoLine() + " - AlcAlterer looped the song " + loopCount + " additional times.");
						} catch (NumberFormatException e) {
							System.out.println("AlcAlterer#main - error - Please provide a positive integer value to use as the desired number of loops. Exiting.");
						}
					} else {
						System.out.println("AlcAlterer#main - error - Please provide a positive integer value to use as the desired number of loops. Exiting.");
						sheet = null;
					}
				}
			}
			
			
			// Step 4 - cleanup again
			// Honestly, the easiest way to do another cleanup is to reuse all the existing code from earlier.
			// So write it out to the outputAlcPath, and import...Placeholders it back in, which will delete any rests, re-sort, recount the number of lines, ...
			if (sheet != null) {
				File file = new File(outputAlcPath);
				if (file.exists()) {
					//System.out.println("Local file temp.temp already exists. For the sake of not deleting or overwriting random files, a cleanup dump + load will not be performed.");
					file.delete();
				}
				arw.writeAlcFile(sheet, outputAlcPath);
				sheet = aa.importAlcFileWithPlaceholders(outputAlcPath);
				file.delete();
			}
			
			// Step 5 - write out new modified alc file			
			if (sheet != null) {
				if (arw.writeAlcFile(sheet, outputAlcPath)) {
					// see if the AlcReader can import the file at the outputAlcPath to confirm it worked
					sheet = arw.loadAlcFile(outputAlcPath);
					if (sheet == null) {
						System.out.println("AlcAlterer#main - error - verification load failed for the modified .alc file written to: " + outputAlcPath + ". Modification failed.");
					} else {
						System.out.println("AlcAlterer#main - success - .alc file modification complete. Program exiting.");
					}
				} else {
					System.out.println("AlcAlterer#main - error - failed to write modified .alc file to: " + outputAlcPath + ".");
				}
			}
			
		}
	}
	
	/**
	 * Given the path to an alc file for modification, this will attempt to import it, while additionally cleaning up various issues
	 * that may result from hand-editing (such as removing rest notes and replacing hand-edited placeholder values with compareValues)
	 * 
	 * My format for placeholder values follows is as such:
	 * {step-letter}{optional: -sharp- or -flat-}{octave value}
	 * So, C-sharp-4 is a 4th octave C sharp. E-5 is a 5th octave E. D-flat-2 is a 2nd octave D flat.
	 * Additionally, rest notes are simply "REST", since they don't have an octave, nor can they be sharp / flat.
	 * 
	 * If an unhandlable error occurs (such as a step letter of "H"), an error is thrown and the process will abort.
	 * 
	 * This code is mildly based on AlcReader#loadAlcFile
	 * 
	 * It will return the MusicSheet if an import was successful, otherwise it will return null
	 * @return MusicSheet the imported MusicSheet from the filepath
	 */
	public MusicSheet importAlcFileWithPlaceholders(String alcFilePath) {
		MusicSheet sheet = null;
		MusicSlice slice = null;
		String headerLine = null;
		String countLine = null;
		int noteLinesReadIn = 0;
		int prevStartTime = -1;
		
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
					sheet = new MusicSheet(headerLine, 0); // temp note count, don't care what we read in, we'll call updateNoteCount with the newly generated value when we're doing translating
				} else {
					// all lines after the first 2 contain note information.
					// if there are not enough tokens in a line, or bad data (such as non-numeric characters), an exception will be thrown and the load aborted.
					
					// If we see placeholder text (like C-sharp-3), we'll translate it to a valid compareValue.
					// If we see REST notes, we'll ignore them entirely. This may change the note count, which is why we'll generate a new note count value.
					// If there are duplicate notes from hand editing, they should get filtered out automatically when added to a MusicSlice, which likewise can affect the note count value.
					StringTokenizer st;
					int currStartTime;
					String compValToken;
					double compareValue;
					int noteDuration;
					boolean isSharp = false;
					boolean isFlat = false;
					String stepLetter;
					int octaveNum;
					
					st = new StringTokenizer(line);
					currStartTime = Integer.parseInt(st.nextToken());
					compValToken = st.nextToken();
					if (compValToken.equalsIgnoreCase("REST")) {
						// We skip this line, and do not increase the note counter by 1.
						continue;
					} else if (compValToken.contains("-")) {
						// translate from your format, if applicable.
						// it is in either of these forms:
						// a. {step letter}-{sharp/flat}-{octave number}
						// b. {step letter}-{octave number}
						compValToken = compValToken.toUpperCase();
						if (compValToken.contains("-FLAT")) {
							isFlat = true;
							compValToken = compValToken.replace("-FLAT","");
						} else if (compValToken.contains("-SHARP")) {
							isSharp = true;
							compValToken = compValToken.replaceAll("-SHARP", "");
						}
						// now it can only be in the format of {step letter}-{octave number}
						stepLetter = compValToken.substring(0, compValToken.indexOf("-"));
						octaveNum = Integer.parseInt(compValToken.substring(compValToken.indexOf("-")+1));
						compareValue = NoteUtils.generateCompareValue(stepLetter, octaveNum, isSharp, isFlat);
					} else {
						compareValue = Double.parseDouble(compValToken);
						if (compareValue == 0.0) {
							continue; // skip rest notes, don't increase counter by 1
						}
					}
					noteDuration = Integer.parseInt(st.nextToken());

					MusicNote note = new MusicNote(compareValue, noteDuration);

					// counter for our file integrity check. will decrement by 1 if it fails to add to the slice for any reason.
					++noteLinesReadIn;
					
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
							System.out.println("AlcAlterer#importAlcFileWithPlaceholders - error - note data is out of order. Aborting load. Confirm - prevStartTime: " + prevStartTime + ", currStartTime: " + currStartTime);
							sheet = null;
							break;
						}
						
						sheet.addSlice(slice);
						slice = new MusicSlice(currStartTime);
						if (!slice.addMusicNote(note)) {
							--noteLinesReadIn;
						}
						prevStartTime = currStartTime;
					} else {
						if (!slice.addMusicNote(note)) {
							--noteLinesReadIn;
						}
					}
				}
			}
			if (sheet != null) {
				sheet.addSlice(slice); // this is necessary because the newest slice hasn't been added to the musicsheet yet, due to how the while-loop handles old/new slices.
			}
			br.close();
		} catch (Exception e) {
			System.out.println("AlcAlterer#importAlcFileWithPlaceholders - error - exception occurred while reading .alc file at path: [" + alcFilePath + "]. Exception: " + e.getMessage());
			e.printStackTrace();
			sheet = null; // just ensuring we return null on an exception, although depending on when the failure occurred, it may have still been null.
		}
		
		sheet.setNoteCount(noteLinesReadIn);
		
		if (headerLine == null || countLine == null) { // if the file was incomplete (only 1 or 2 lines in length), then there isn't enough data to play a song
			System.out.println("AlcAlterer#importAlcFileWithPlaceholders - error - .alc file did not have at least 3 lines (1 informational, 1 counter, at least 1 note). File is too small to process.");
			sheet = null; // the object may not be null if the file was 2 lines long, so this ensures we return null
		} else if (noteLinesReadIn == 0) { // if there were no notes read in, we can't play a song. Although I don't think this conditional is capable of being hit.
			System.out.println("AlcAlterer#importAlcFileWithPlaceholders - error - .alc file contains 0 note lines. File will not be processed.");
			sheet = null;
		}
		
		return sheet;
	}
	
	
	
}
