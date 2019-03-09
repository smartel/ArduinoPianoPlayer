package DataObjs;

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
	
	public MusicSlice() {
		notes = new TreeSet<MusicNote>();
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
	
	
}
