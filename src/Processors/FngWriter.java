package Processors;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;

import DataObjs.Finger;
import DataObjs.MusicNote;
import DataObjs.MusicSheet;
import DataObjs.MusicSlice;
import DataObjs.PianoProperties;
import Utils.Constants;

public class FngWriter {

	public FngWriter() {
	}
	
	/**
	 * Given a musicsheet (aka an imported .alc file), create a .fng file from it
	 * We will need the Hand so we know if notes are in range or not. We can't write out instructions for fingers that aren't present.
	 * @param hand hand object containing information regarding all robotic fingers' locations
	 * @param sheet music sheet to convert to .fng file format
	 * @param fngFilePath output path to write the .fng file to
	 * @return true if the music was successfully converted to a .fng file format, false otherwise
	 */
	public boolean writeFngFromSheet(Hand hand, MusicSheet sheet, String fngFilePath) {
		boolean wasSuccessful = true;
		
		// This is how we'll structure the pseudo-code for the arduino instructions.
		// {timestamp to start at} {finger number} {"hit"} for when we are hitting a piano key
		// {timestamp to start at} {finger number} {"release"} for when we are releasing a piano key
		// We'll generate the instructions by parsing over every slice, but since we also need to insert the release instructions, it would be out of order if we just iterated over all the instructions in the insertion order.
		// So, we'll store the raw instruction strings in a collection that can be sorted, since instructions start with the timestamp and can be sorted using them as the basis.
		
		LinkedList<String> instructions = new LinkedList<String>();
		int numDigitsForTimestamp = (sheet.getEndTime()+"").length();
		// since we're comparing instructions based on their start time, we'll prepend zeroes in the front of any that have a length less than the maximum timestamp length,
		// so 110 compares accurately with, say, 1150, 12, 1100, and so on.
		
		LinkedList<MusicSlice> slices = sheet.getSlices();
		for (int x = 0; x < slices.size(); ++x) {
			MusicSlice slice = slices.get(x);
			TreeSet<MusicNote> notes = slice.getNotes();
			Iterator<MusicNote> iter = notes.iterator();
			String strStartTime = slice.getStartTime() + "";
			while (strStartTime.length() < numDigitsForTimestamp) {
				strStartTime = "0" + strStartTime;
			}
			while (iter.hasNext()) {
				MusicNote note = iter.next();
				// Get the finger for this note at this start time.
				// It will remain the same finger for the entire duration it is pressing it (that is, it can't slide away mid-press if using a sliding implementation)
				Finger finger = hand.getFingerForNoteAtTime(note.getCompareValue(), slice.getStartTime());
				
				// If a finger was returned, then the note is in range of the piano and hittable. Hit it.
				if (finger != null) {
					
					// create and insert the push instruction
					String pushInstruct = strStartTime + " FINGER " + finger.getFingerSequence() + " CV " + note.getCompareValue() + " " + Constants.INSTRUCT_HIT;
					
					// However, before we insert it, we need to make sure that the previous instruction for this compareValue is a release message, or that there is no previous instruction.
					// If the previous instruction was a hit, this means the key is already depressed, so we need to release it immediately and THEN hit it again.
					// This also begs the question, which release do we use, the one attached to this hit, or to the previous hit?
					// Current ideas are to either use whichever release ends last, or to chomp the original note and just use the new note's duration.
					
					// Here's a diagram to help illustrate the issue:
					// TIME: 0         1
					//       ----------------------------------------------------
					// NOTE: A         A
					// NOTE:     B         B
					// each dash representing 100ms
					// note A starts at 0ms and ends at 1000ms for cv 20 (arbitrary cv, could be anything as long as A and B are the same cv)
					// note B starts at 500ms and ends at 1500ms
					// The instructions would read like this: (pseudocode)
					// 0000ms  Finger 1  Hit
					// 0500ms  Finger 1  Hit
					// 1000ms  Finger 1  Release
					// 1500ms  Finger 1  Release
					// Since they are for the same cv, the finger is already depressed hitting A once it gets an instruction to hit B
					// So to get it to hit the key again, we need to insert a release at 500ms, that will occur BEFORE the hit at 500ms
					// And then we need to determine which of the original 2 releases to hit (because if we don't delete one of them, we'll have 2 hit instructions but 3 release instructions)
					// Which is why we're aiming for keeping either the longest remaining duration or for the last instruction's.
					// !! GOING WITH KEEPING THE LAST INSTRUCTION'S RELEASE FOR NOW. We CHOMP the previous note's release, since we have to put it at the current time !!
					
					// Determine if there was a previous instruction
					String prevInstruct = searchForInstruction(instructions, note.getCompareValue(), slice.getStartTime(), Constants.DIR_BACKWARD);
					if (!prevInstruct.isEmpty()) {					
						// See if the previous instruction was a release. If it is, then we don't need to take additional action
						if (prevInstruct.contains(Constants.INSTRUCT_HIT)) {
							// yikes, ok, so we'll need to add a release here for the previous hit, as well as delete the release that was originally attached to it.
							// find the original release to delete:
							String nextRelease = searchForInstruction(instructions, note.getCompareValue(), slice.getStartTime(), Constants.DIR_FORWARD);
							instructions.remove(nextRelease);
							// then add the new release. All we need to do is modify the timestamp on the original release message to be now.
							nextRelease = strStartTime + nextRelease.substring(nextRelease.indexOf(" "));
							instructions.add(nextRelease);
						}
					}
					
					// No matter what, we add the hit. The only question is whether we add a release before it.
					instructions.add(pushInstruct);
					
					// We'll always add the release instruction attached to this note, even if it was a stacked hit.
					// create and insert the release instruction. we'll need to calculate the time we release it at.
					String strEndTime = (slice.getStartTime() + note.getDuration()) + "";
					while (strEndTime.length() < numDigitsForTimestamp) {
						strEndTime = "0" + strEndTime;
					}
					String releaseInstruct = strEndTime + " FINGER " + finger.getFingerSequence() + " CV " + note.getCompareValue() + " " + Constants.INSTRUCT_RELEASE;
					instructions.add(releaseInstruct);
					
					// HIGH PRIORITY:
					
					// TODO what happens when a hit happens again immediately after a release? For this reason, we start release commands with 1, so they're ordered first in the instructions.
					//      That is, at 000, we hit, and at 200 we release, but then we hit again at 200. Do we need to build a buffer into that? like let go 50ms early?
					//      I don't want a finger to do a super fast doubletake and break something :^
					// IT WILL SOUND WRONG TO PEOPLES EARS IF NOTES DONT HIT AT THE RIGHT TIME, SO DONT OFFSET BOTH THE RELEASE AND THE HIT (LIKE MAKE THE RELEASE 50ms earlier and the hit 50ms later)
					// ONLY MAKE THE RELEASE OCCUR EARLIER, NOT THE HIT! We can confirm by ear once the fingers are set up.
					
					
					// LESSER PRIORITY:
					// TODO any instructions for lighting up / turning off LEDs? Would that be an optional input arg?
					
					
					// FAR-FUTURE PRIORITY:
					// TODO in the future - sliding instructions would need to be added here to reposition sliding fingers to the note they need to hit
					// TODO oh no what would happen if bad data or translating led to a sliding finger trying to slide WHILE pressing down on a key?
					
					
				} else {
					// Warn that there is no finger in range for this given note - it can't be hit.
					System.out.println("FngWriter#writeFngFromSheet - warning - skipped note because no finger is capable of hitting it. Details: " + note.toString());
					
				}
			}
		}
		
		// one final sort to ensure all the instructions are written out in a linear order from start to finish
		Collections.sort(instructions);
		
		// TODO do we want a sanity check method, that ensures that there are no stacks pushes?
		// TODO do we need to parse for simultaneous hit / hold instructions for a cv occurring at the same time in a song, and space them out a bit? or will the finger hardware release and hit just fine on its own?
		
		// Write all the instructions to the .fng output file
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(fngFilePath)));
			
			// TODO do we need boilerplate before we get into writing instructions? opening loops over time in milliseconds until song endtime?
			
			for (int x = 0; x < instructions.size(); ++x) {
				
				// TODO what will instructions actually look like? If time in milliseconds is between x and y, power some solenoid?
				//      and we'll just have a massive chain of conditionals for the entire song? is that viable?
				
				bw.write(instructions.get(x));
				bw.newLine();
			}
			
			// TODO do we need any boilerplate after writing instructions? closing loops?
			
			bw.close();
		} catch (Exception e) {
			System.out.println("FngWriter#writeFngFromSheet - failed to write to .fng file at path: " + fngFilePath);
			e.printStackTrace();
			wasSuccessful = false;
		}
		
		return wasSuccessful;
	}
	
	/**
	 * Given a list of all the instructions so far, a compare value, a time in milliseconds to start from, and a direction,
	 * (whether to go backwards from the given time, trying to find the previous instruction for this compareValue, or forward from the given time, looking for the next instruction)
	 * this finds and returns the previous / next instruction for the given compare value, if any. If no instruction is found or an error occurs, and empty string is returned.
	 * @param instructions list of all instruction strings generated for the song so far
	 * @param compareVal the desired compare value to find an instruction for
	 * @param startTime the time to look backward or forwards from
	 * @param direction whether to go backward (looking for a previous instruction for this compval) or forward (looking for the next instruction for this compval)
	 * @return the next or previous instruction for this compare value if found. An empty string is returned otherwise.
	 */
	public String searchForInstruction(LinkedList<String> instructions, double compareVal, int startTime, int direction) {
		String foundInstruct = "";
		
		// Is there a chance for there to be instructions on the exact startTime?
		// The translation to an .alc would've prevented any "true duplicates" (that is, 2 commands for a cv starting at the same timestamp).
		//      It is true we are inserting new release instructions when we chomp notes,
		//      but when the list is sorted, releases appear before hits for a given note at a given time.
		
		// sort so we can ensure we're grabbing the correct previous instruction (if any)
		Collections.sort(instructions);
		
		if (direction == Constants.DIR_BACKWARD) {
			for (int x = 0; x < instructions.size(); ++x) {
				String currInstruct = instructions.get(x);
				int time = Integer.parseInt(currInstruct.substring(0, currInstruct.indexOf(" ")));

				// the instant we are past the start time, we break out. if we found any instructions, the latest one will be stored in the instruct string. otherwise, there were none.
				if (time > startTime) {
					break;
				} else {
					if (currInstruct.contains(" " + compareVal + " ")) { // this is a horrendous way to do it. may want to opt for a stringtokenizer. only reason not to tokenizer yet is because it's arduino pseudocode atm anyway, so it's likely the format will change.
						// TODO read above comment. SHould strongly consider making a wrapper around lines we write to the file, so we can easily access values. Can take in the string as a ctor. May be useful for having a writeCode() method that returns a string that ISNT pseudocode, while keeping existing logic.
						foundInstruct = currInstruct;
					}
				}
			}
		} else if (direction == Constants.DIR_FORWARD) {
			for (int x = 0; x < instructions.size(); ++x) {
				String currInstruct = instructions.get(x);
				int time = Integer.parseInt(currInstruct.substring(0, currInstruct.indexOf(" ")));

				// We need to skip over all elements until we are at the start time or greater.
				// Additionally, we break out immediately after finding the first instruction after the skip phase, because it is the immediate instruction following the desired start time.
				if (time < startTime) {
					continue;
				} else {
					if (currInstruct.contains(" " + compareVal + " ")) { // this is a horrendous way to do it. may want to opt for a stringtokenizer. only reason not to tokenizer yet is because it's arduino pseudocode atm anyway, so it's likely the format will change.
						foundInstruct = currInstruct;
						break;
					}
				}
			}
		} else {
			System.out.println("FngWriter#searchForInstruction - Unknown direction passed in. Given direction: " + direction);
		}
		
		// If no instruction was found, then empty string is returned.
		return foundInstruct;
	}
}
