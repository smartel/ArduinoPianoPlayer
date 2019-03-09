package DataObjs;

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
 * @author smartel
 *
 */
public class MusicSheet {
	
	
	
	
	
}
