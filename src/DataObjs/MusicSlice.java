package DataObjs;

import java.util.Iterator;
import java.util.TreeSet;

import Utils.Constants;

/**
 * A MusicSlice is a representation of a single, very specific point in time,
 *  containing a collection of all the piano keys (MusicNotes) that need to be struck simultaneously.
 * 
 * It is ok for only one note to be in the collection, and for some songs, especially easy/beginner/simpler piano pieces, that may be common.
 * However, the slice should never be empty. If the slice is empty, it simply shouldn't exist.
 * Slices exist on an as-needed basic, rather than for every 0.01 seconds of a music piece (or some other arbitrary unit of time)
 * 
 * MusicNotes within a MusicSlice are stored in a set and ordered by the compareValue, from the low notes to the high notes (left side of the piano to the right side).
 * 
 * @author smartel
 */
public class MusicSlice {

	TreeSet<MusicNote> notes;
	int startTime;
	
	/**
	 * Constructs a MusicSlice, which holds a collection of MusicNotes that need to be hit simultaneously, at the supplied "start time" (in milliseconds) within the song.
	 * If an invalid start time is supplied (any integer value below 0), then we'll automatically set it to start at 0 instead, while displaying a warning.
	 * @param startTime the time, in milliseconds, after which the notes should play
	 */
	public MusicSlice(int startValue) {
		notes = new TreeSet<MusicNote>();
		startTime = startValue;
		if (startTime < 0) {
			startTime = 0;
			System.out.println("MusicSlice#ctor(startTime) - warning - invalid startValue supplied, using 0 instead. Supplied startValue: " + startValue);
		}
	}
	
	/**
	 * Simple copy constructor. Will also copy all MusicNotes
	 * @param other
	 */
	public MusicSlice(MusicSlice other) {
		notes = new TreeSet<MusicNote>();
		this.startTime = other.startTime;
		
		Iterator<MusicNote> iter = other.notes.iterator();
		while (iter.hasNext()) {
			MusicNote note = iter.next();
			MusicNote noteCopy = new MusicNote(note);
			notes.add(noteCopy);
		}
	}
	
	
	public boolean addMusicNote(MusicNote note) {
		boolean isSuccessful;
		
		isSuccessful = notes.add(note);
		
		if (!isSuccessful) {
			// Presumably, this is a "duplicate entry."
			// Should we chomp the existing note, by removing it and adding the new one?
			// Should we intentionally keep the one with the longest (remaining) duration?
			// Like, it could be the case there was already an "A" with x duration in there, and we tried to add another "A" same octave, perhaps same or different duration.
			// So far, I seem to be hitting this with rests (exclusively?), probably because they all have the same compare value of 0.
			// So you know what? Since rests CAN'T EVEN BE HEARD, and may be the only thing causing this error, I'm only going to issue an error if it is a non-rest, and we can go from there for potential solutions if / when we see the error again.
			if (note.getCompareValue() != Constants.REST_COMP_VALUE) {
				System.out.println("MusicSlice#addMusicNote: failed to add MusicNote to notes collection. This is likely a duplicate of an existing note.\r\nMusicSlice startTime: " + startTime + " | Note details: " + note.toString());
			}
		}

		return isSuccessful;
	}
	
	// We shouldn't ever need a method to remove music notes? Since we're just transcribing xml / sheet music to a new format, and not editing the piece.
	
	/**
	 * Given a new bpm multiplier, multiply the note's duration by the amount.
	 * It is expected that the note's start time will also be multiplied by the same amount within its MusicSlice. 
	 * @param bpmMult positive integer value to multiply the song's duration by
	 * @return true if successful, false otherwise
	 */
	public boolean applyBpmMultipler(int bpmMult) {
		boolean isSuccessful = true;
		if (bpmMult <= 0) {
			isSuccessful = false;
		} else {
			// adjust the slice's start time
			startTime *= bpmMult;
			
			// adjust all of the slice's notes' durations
			Iterator<MusicNote> iter = notes.iterator();
			while (iter.hasNext()) {
				MusicNote note = iter.next();
				isSuccessful = note.applyBpmMultiplier(bpmMult);
				if (isSuccessful == false) {
					break;
				}
			}
		}
		return isSuccessful;
	}
	

	/**
	 * If this MusicSlice contains a note with the given compareValue, it returns true. otherwise, false.
	 * @param compareValue A compareValue representing a note, to see if it is contained in this MusicSlice
	 * @return true if this MusicSlice contains a MusicNote with the given compare value
	 */
	public boolean containsNote(double compareValue) {
		boolean doesContain = false;
		
		Iterator<MusicNote> iter = notes.iterator();
		while (iter.hasNext()) {
			MusicNote note = iter.next();
			if (note.getCompareValue() == compareValue) {
				doesContain = true;
				break;
			}
		}
		
		return doesContain;
	}
	
	/**
	 * Getter for notes collection
	 * @return notes collection
	 */
	public TreeSet<MusicNote> getNotes() {
		return notes;
	}
	
	/**
	 * Getter for "start time" in milliseconds (how far into the song until the notes are hit)
	 * @return startTime
	 */
	public int getStartTime() {
		return startTime;
	}
	
	/**
	 * Setter for "start time" in milliseconds (how far into the song until the notes are hit)
	 * @return startTime
	 */
	public void setStartTime(int start) {
		startTime = start;
	}
}
