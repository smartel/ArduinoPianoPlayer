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
	// d. Shifter - will shift all notes in the song x steps (either positive of negative), with positive numbers moving keys that many notes higher, and negative moving that many notes lower, deleting any notes that go out of range.

	
	// 4. Perform cleanup again
	// a. Performing the cleanup steps a second time ensures we have an accurate integrity note counter, ... after making modification(s) to the note content.
	
	// 5. Write out the new .alc file with modification(s)
	
	
	

	// Potential unimplemented change ideas are:
	// x. Research needed - what goes into changing a song's key - differences major vs minor key? Would the shifter as implemented be able to handle it, or need something new?
	// x. I'm not sure how easy it would be to mark a section of the alc file for deletion. Like, say you wanted to delete all notes that start from 1400ms to 1800ms in a song.
	//    You'd need to be able to specify a start point, an end point, and then the program would mass update the start times of every note after 1800ms to what, start 400ms earlier?
	//    Not sure how valuable this would be yet.
	//    Alternatively, what if we want to delete a whole octave instead of a time range? Like, the song has a ton of notes in octaev 0 and we don't have fingers for it.
	//    Alternatively, the reverse of either of these: we want to keep only the parts of the song between starting time x and ending time y, or in range of octave x to octave y.
	// Of course, there may be more cases that come up where we go "oh hey, being able to edit an alc file to add / modify / remove blah would be great", so we could just shove it in here...
		
	
	
	public static void main(String[] args) {
		MusicSheet sheet;
		if (args.length < 3) {
			System.out.println("AlcAlterer#main - usage: {input alc file path} {translated alc file path} {output alc file path} {optionals ...}\n" +
							   "Optionals set 1: \"" + Constants.BPM_OPTION + "\" {integer} - if the first optional argument is \"" + Constants.BPM_OPTION + "\", and the second is a positive integer, the integer will be used as a bpm-multiplier on the target alc file.\n" +
							   "Optionals set 2: \"" + Constants.OCTAVE_OPTION + "\" {integer} {\"true\" or \"false\"} - if the first optional argument is \"" + Constants.OCTAVE_OPTION + "\", and the second is a non-zero integer, the integer will be used to adjust notes up (if positive) or down (if negative) that many octaves within the target alc file. If the 3rd optional argument is \"true\", than any notes pushed below octave 1 or above octave 8 will be DELETED, otherwise they will cap at octave 1 or 8. By default, notes cap at the boundaries.\n" +
							   "Optionals set 3: \"" + Constants.LOOP_OPTION + "\" {integer} - will loop the song an additional {integer} number of times in the alc file.\n" +
							   "Optionals set 4: \"" + Constants.SHIFT_OPTION + "\" {integer} - will shift all keys the desired number of compare values up (if positive) or down (if negative) the piano.\n" +
							   "Optionals set 5: \"" + Constants.REPLACE_OPTION + "\" {double} {double} - will replace all instances of the first compareValue with the second compareValue.\n" +
							   "Optionals set 6: \"" + Constants.MOVETIME_OPTION + "\" {integer} - will adjust the starting time of every note by the given (positive or negative) integer (in milliseconds).\n" +
							   "Optionals set 7: \"" + Constants.MINIMIZE_OPTION + "\" - used to set all start times and durations to their smallest possible value by using the song's greatest common divisor, effectively resetting any applied bpm changes.\n" +
							   "Optionals set 8: \"" + Constants.WIGGLE_OPTION + "\" {double} {double} - given a playable range (a starting compare value and an ending compare value, at least 1 octave in length), any notes that are outside of the range in the sheet will \"wiggle\" to the same note letter / sharpness on the nearest playable octave.\n" +
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
							System.out.println("AlcAlterer#main - multiplied bpm by " + bpmMult + ".");
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
							System.out.println("AlcAlterer#main - AlcAlterer adjusted octave value by " + octaveAdjustment + ".");
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
							System.out.println("AlcAlterer#main - AlcAlterer looped the song " + loopCount + " additional times.");
						} catch (NumberFormatException e) {
							System.out.println("AlcAlterer#main - error - Please provide a positive integer value to use as the desired number of loops. Exiting.");
						}
					} else {
						System.out.println("AlcAlterer#main - error - Please provide a positive integer value to use as the desired number of loops. Exiting.");
						sheet = null;
					}
				} else if (args[3].equalsIgnoreCase(Constants.SHIFT_OPTION)) {
					if (args.length >= 5) {
						try {
							int shiftAmount = Integer.parseInt(args[4]);
							if (shiftAmount == 0) {
								System.out.println("AlcAlterer#main - error - Please provide a non-zero integer value to use as the desired number of times to shift (positive for upward, negative for downward). Exiting.");
							} else {
								// we have a valid value - loop through the musicsheet and shift every note by the supplied amount, "deleting" it if necessary (if it goes over the bounds, it turns into a rest, which cleanup will later delete).
								
								LinkedList<MusicSlice> slices = sheet.getSlices();
								for (int x = 0; x < slices.size(); ++x) {
									MusicSlice slice = slices.get(x);
									Iterator<MusicNote> iter = slice.getNotes().iterator();
									while (iter.hasNext()) {
										MusicNote note = iter.next();
										note.applyShift(shiftAmount);
									}
								}
							}
							
							sheet.setInfoLine(sheet.getInfoLine() + " - AlcAlterer shifted notes by " + shiftAmount + ".");
							System.out.println("AlcAlterer#main - AlcAlterer shifted notes by " + shiftAmount + ". Any notes shifted out of range have been turned into rests.");
						} catch (NumberFormatException e) {
							System.out.println("AlcAlterer#main - error - Please provide a non-zero integer value to use as the desired shift amount. Exiting.");
						}
					} else {
						System.out.println("AlcAlterer#main - error - Please provide a non-zero integer value to use as the desired shift amount. Exiting.");
						sheet = null;
					}
				} else if (args[3].equalsIgnoreCase(Constants.REPLACE_OPTION)) {
					if (args.length >= 6) {
						try {
							int notesAffected = 0;
							double findCompVal = Double.parseDouble(args[4]); // the "find" compare value in our find-and-replace operation
							double replaceCompVal = Double.parseDouble(args[5]); // the "replace" compare value in our find-and-replace operation
							
							if (!NoteUtils.verifyValidNonRestCompareValue(findCompVal) || !NoteUtils.verifyValidNonRestCompareValue(replaceCompVal)) {
								System.out.println("AlcAlterer#main - error - Please provide valid compare values to find and replace (An existant note on an octave between 0 and 10, so no B sharp for example). Supplied values: findCompVal: " + findCompVal + ", replaceCompVal: " + replaceCompVal + ".\nExiting.");
							} else if (findCompVal == replaceCompVal) {
								System.out.println("AlcAlterer#main - error - Please provide different compare values to find and replace - the \"find\" value and the \"replace\" value are the same. Supplied values: findCompVal: " + findCompVal + ", replaceCompVal: " + replaceCompVal + ".\nExiting.");
							} else {
								// we have a valid value - loop through the musicsheet and replace any notes that match the "find" compareValue with the "replace" compareValue.
								LinkedList<MusicSlice> slices = sheet.getSlices();
								for (int x = 0; x < slices.size(); ++x) {
									MusicSlice slice = slices.get(x);
									Iterator<MusicNote> iter = slice.getNotes().iterator();
									while (iter.hasNext()) {
										MusicNote note = iter.next();
										if (note.getCompareValue() == findCompVal) {
											slice.getNotes().remove(note);
											note = new MusicNote(replaceCompVal, note.getDuration());
											if (!slice.getNotes().add(note)) {
												System.out.println("AlcAlterer#main - notice - replace operation failed to add duplicate note to timestamp: " + slice.getStartTime() + ". Note details for manual inspection of which note to keep (if different durations): " + note);
											}
											++notesAffected;
											break; // a compareValue can only be present once in a given timeslice - you can't hit a piano key multiple times at the exact point in time
										}
									}
								}
							}
							sheet.setInfoLine(sheet.getInfoLine() + " - AlcAlterer replaced " + notesAffected + " notes that were originally compareValue " + findCompVal + " with the new value " + replaceCompVal + ".");
							System.out.println("AlcAlterer#main - AlcAlterer replaced " + notesAffected + " notes that were originally compareValue " + findCompVal + " with the new value " + replaceCompVal + ".");
						} catch (NumberFormatException e) {
							System.out.println("AlcAlterer#main - error - Please provide valid numbers (integer or decimal) to use as the desired compare values to find and to replace. Exiting.");
						}
					} else {
						System.out.println("AlcAlterer#main - error - Please provide a compare value to find, and a compare value to replace with. Exiting.");
						sheet = null;
					}
				} else if (args[3].equalsIgnoreCase(Constants.MOVETIME_OPTION)) {
					if (args.length >= 5) {
						try {
							int adjustAmount = Integer.parseInt(args[4]);
							if (adjustAmount == 0) {
								System.out.println("AlcAlterer#main - error - Please provide a non-zero integer value to use as the desired millisecond adjustment to apply to all notes. Exiting.");
							} else {
								// we have a valid value - loop through the musicsheet and adjust the start time of every slice (which will thus impact every MusicNote).
								
								LinkedList<MusicSlice> slices = sheet.getSlices();
								for (int x = 0; x < slices.size(); ++x) {
									MusicSlice slice = slices.get(x);
									slice.setStartTime(slice.getStartTime() + adjustAmount);
									
									if (slice.getStartTime() < 0) {
										System.out.println("AlcAlterer#main - WARNING - Start Time set to below 0 for slice number: " + x + ", startTime was: " + slice.getStartTime() + ". Will be reset to 0.");
										slice.setStartTime(0);
									}
								}
							}
							
							sheet.setInfoLine(sheet.getInfoLine() + " - AlcAlterer adjusted start times by " + adjustAmount + ".");
							System.out.println("AlcAlterer#main - AlcAlterer adjusted start times by " + adjustAmount + ".");
						} catch (NumberFormatException e) {
							System.out.println("AlcAlterer#main - error - Please provide a non-zero integer value to use as the desired millisecond adjustment. Exiting.");
						}
					} else {
						System.out.println("AlcAlterer#main - error - Please provide a non-zero integer value to use as the desired millisecond adjustment. Exiting.");
						sheet = null;
					}
				} else if (args[3].equalsIgnoreCase(Constants.MINIMIZE_OPTION)) {
					if (args.length >= 4) {
						try {
							// We need to reduce all start times in the MusicSlice's to their smallest possible value.
							// We also need to reduce the durations of all MusicNotes to their smallest possible value.
							// Determine the current greatest-common-divisor, which we can divide everything by.
							int origGcd = sheet.getGCD();
							LinkedList<MusicSlice> slices = sheet.getSlices();
							for (int x = 0; x < slices.size(); ++x) {
								MusicSlice slice = slices.get(x);
								int currStartTime = slice.getStartTime();
								slice.setStartTime(currStartTime / origGcd); // since we're dividing by the gcd, it will divide cleanly into an integer
								
								// next up, do the notes
								Iterator<MusicNote> iter = slice.getNotes().iterator();
								while (iter.hasNext()) {
									MusicNote note = iter.next();
									int currDuration = note.getDuration();
									note.setDuration(currDuration / origGcd);
								}
							}
							
							int newGcd = sheet.getGCD();
							if (origGcd == newGcd) {
								System.out.println("AlcAlterer#main - AlcAlterer could not minimize the sheet - it is already as minimized as it can get.");
							} else {
								sheet.setInfoLine(sheet.getInfoLine() + " - AlcAlterer minimized the sheet. Old GCD: " + origGcd + ", New GCD: " + newGcd);
								System.out.println("AlcAlterer#main - AlcAlterer minimized the sheet. Old GCD: " + origGcd + ", New GCD: " + newGcd);
							}
						} catch (Exception e) {
							System.out.println("AlcAlterer#main - Unexpected exception caught. Exiting.");
							e.printStackTrace();
						}
					} // If adding new options don't copy-paste this particular block (MINIMIZE_OPTION), it doesn't take in a single argument after MINIMIZE.
				} else if (args[3].equalsIgnoreCase(Constants.WIGGLE_OPTION)) {
					if (args.length >= 6) {
						try {
							int numWiggled = 0;
							double startCv = Double.parseDouble(args[4]);
							double endCv = Double.parseDouble(args[5]);
							
							if ((endCv - startCv) < Constants.OCTAVE_LENGTH) {
								System.out.println("AlcAlterer#main - error - Provided range (from the start compare value to the end compare value) is less than 1 octave in length and is too small for wiggling. Exiting.");
							} else if (startCv < Constants.MIN_THEORETICAL_COMPARE_VALUE) {
								System.out.println("AlcAlterer#main - error - Provided start range is below the theorical smallest Compare Value (" + Constants.MIN_THEORETICAL_COMPARE_VALUE + "). Exiting.");
							} else if (endCv > Constants.MAX_THEORETICAL_COMPARE_VALUE) {
								System.out.println("AlcAlterer#main - error - Provided end range is above the theorical max Compare Value (" + Constants.MAX_THEORETICAL_COMPARE_VALUE + "). Exiting.");
							} else {
								// we have a valid range - loop through the musicsheet and wiggle the compare values of any notes outside the range, to be inside the range
								LinkedList<MusicSlice> slices = sheet.getSlices();
								for (int x = 0; x < slices.size(); ++x) {
									MusicSlice slice = slices.get(x);
									Iterator<MusicNote> iter = slice.getNotes().iterator();
									while (iter.hasNext()) {
										MusicNote note = iter.next();
										if (note.getCompareValue() < startCv || note.getCompareValue() > endCv) {
											++numWiggled;
											System.out.println("AlcAlterer#main - Wiggling out-of-range note at time: " + slice.getStartTime() + " - " + note);
										}
										while (note.getCompareValue() < startCv) {
											note.applyOctaveAdjustment(1, true);
										}
										while (note.getCompareValue() > endCv) {
											note.applyOctaveAdjustment(-1, true);
										}
									}
								}
							}
							
							sheet.setInfoLine(sheet.getInfoLine() + " - AlcAlterer wiggled " + numWiggled + " notes to be within the range of: " + startCv + " - " + endCv + ".");
							System.out.println("AlcAlterer#main - AlcAlterer wiggled " + numWiggled + " notes to be within the range of: " + startCv + " - " + endCv + ".");
						} catch (NumberFormatException e) {
							System.out.println("AlcAlterer#main - error - Please provide a valid compare value range (the first playable compare value, and the last playable compare value) to use for wiggling out-of-range notes. Exiting.");
						}
					} else {
						System.out.println("AlcAlterer#main - error - Please provide a compare value range (the first playable compare value, and the last playable compare value) to use for wiggling out-of-range notes. Exiting.");
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
