package DataObjs;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * MusicSheet.java
 * 
 * On a high level, the purpose of a MusicSheet is to contain the individual music notes that need to be played, in the order they should be hit. It is allowed for notes to be hit simultaneously.
 * On a low level, a MusicSheet is effectively a 2-dimension collection (a linkedlist with each index referring to a specific point in time to play notes at)
 *  of collections (sets containing all the notes that need to be played simultaneously at the given point in time).
 *  This is slightly obscured, as the sets are wrapped by a "MusicSlice" object, containing all notes that are intended to be hit simultaneously at the given point in time.
 * 
 * The outer collection is a linear linkedlist of Instructions, and each index represents a different period in time, with [0] being the start of the song,
 *  and the last index [list size-1] being the end of the song. Thus, when going from the front of the collection to the back,
 *  you move through time from the first notes of the piece to the last notes of the piece, in order. Due to different durations notes may be held, it is NOT expected
 *  that the difference in time between all indices will be the same (for example, the time difference between indices [0] and [1] could be 1 second,
 *  but the time difference between indices [1] and [2] could be 2 seconds. As long as instructions are read and performed in the correct order and notes are held for the
 *  intended durations, then the size of the gaps between different instructions is ultimately irrelevant)
 * 
 * Thus, as one moves down the linkedlist collection (from index 0, to 1, to 2, ...),
 *  one will move across the song in order from start to finish, containing all the notes that need to be hit and when.
 * 
 * Processing of an .alc file is performed by the AlcReader.
 * 
 * @author smartel
 *
 */
public class MusicSheet {
	LinkedList<MusicSlice> slices;
	String infoLine; // the informational line from the .alc file
	int noteCount; // the note count from the .alc file
	
	public MusicSheet(String infoLine, int noteCount) {
		slices = new LinkedList<MusicSlice>();
		this.infoLine = infoLine;
		this.noteCount = noteCount;
	}
	
	/**
	 * Generates and returns the greatest-common-divisor for all of the MusicSlice's start durations.
	 * The PianoFeigner will rely on this duration for looping over the MusicSlices and repainting and playing sound files at appropriate times.
	 * @return greatest-common-divisor of all the MusicSlice's start durations, in milliseconds, or -1 if it fails for any reason (empty collection, ...)
	 */
	public int getGCD() {
		int gcd = -1;
		int highestGap = -1;
		
		if (slices.size() > 1) {
			// Slices are stored in increasing order of startTimes.
			// Scroll through all the Slices, comparing the previous slice's startDuration and the next slice's startDuration, to get the gap between slices.
			// We'll store the maximum gap we see.
			// If that maximum gap works as the gcd, great. If it doesn't, we'll keep subtracting 1 from it until we determine the gcd, then break out of the loop and return it.
			for (int n = 0; n < slices.size()-1; ++n) {
				int currentGap = slices.get(n+1).getStartTime() - slices.get(n).getStartTime();
				if (currentGap > highestGap) {
					highestGap = currentGap;
				}
			}
			
			// find the highest value, starting from highestGap, that can fit into all startDurations
			for (int x = highestGap; x > 0; --x) {
				for (int n = 0; n < slices.size()-1; ++n) {
					int currentGap = slices.get(n+1).getStartTime() - slices.get(n).getStartTime();
					if (currentGap % x == 0) {
						gcd = x;
						if (n == slices.size()-2) { // if we are at the end of the collection, then it means it was a valid gcd for all of them, so we can stop looking now
							x = 0;
						}
					} else {
						break;
					}
				}
			}
			
		} else {
			System.out.println("MusicSheet#getGCD - error - there are note enough MusicSlices in this MusicSheet (minimum required: 2). A GCD for gui playback can't be determined. MusicSlices available: " + slices.size());
		}
		
		return gcd;
	}
	
	/**
	 * Looks through every note, taking count of each note's start value and start time, and determines when this song "ends" (aka, when the note with the last playing duration expires).
	 * Need to be careful, because the last note to expire may not be from the last MusicSlice. It could be from a prior MusicSlice by having a note with a very long duration.
	 * This is why we must check every single MusicSlice, and not just the last one.
	 * @return the time (in milliseconds) that the last note in this song stops playing. -1 if there are any errors.
	 */
	public int getEndTime() {
		int highestEndTime = -1;
		
		for (int x = 0; x < slices.size(); ++x) {
			MusicSlice currSlice = slices.get(x);

			Iterator<MusicNote> iter = currSlice.getNotes().iterator();
			while (iter.hasNext()) {
				MusicNote currNote = iter.next();
				// The note's duration, in addition to the timestamp it starts, will show the ultimate timestamp the note ends at.
				int currEndTime = currNote.getDuration() + currSlice.getStartTime();
				// If this is the latest note we have so far, set the song's endDuration to it.
				if (currEndTime > highestEndTime) {
					highestEndTime = currEndTime;
				}
			}
		}
		
		return highestEndTime;
	}
	
	/**
	 * Adds the given MusicSlice to the collection
	 * @param slice
	 */
	public void addSlice(MusicSlice slice) {
		slices.add(slice);
	}
	
	/**
	 * Simple getter for MusicSlice collection
	 * @return
	 */
	public LinkedList<MusicSlice> getSlices() {
		return slices;
	}
}
