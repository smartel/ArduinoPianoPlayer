package DataObjs;

import java.util.Iterator;
import java.util.TreeSet;

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
	
	
	public void addMusicNote(MusicNote note) {
		boolean isSuccessful;
		
		isSuccessful = notes.add(note);
		
		if (!isSuccessful) {
			// Log the error. no action will need to be taken otherwise, because it is presumably not adding because it is a duplicate?
			// But the error message itself will alert me to look at the code and debug what happened.
			System.out.println("MusicSlice#addMusicNote: failed to add MusicNote to notes collection.\r\nNote details: " + note.toString());
		}

	}
	
	// We shouldn't ever need a method to remove music notes? Since we're just transcribing xml / sheet music to a new format, and not editing the piece.
	

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
	
}
