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
					instructions.add(pushInstruct);
					
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
		
		// sorts all the instructions so the hits / releases are written out in a linear order from start to finish
		Collections.sort(instructions);
		
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
}
