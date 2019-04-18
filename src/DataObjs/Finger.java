package DataObjs;

import java.util.HashMap;

public class Finger {

	// "Fingers" represent the physical finger units that the arduino manipulates in real time to play a song.
	// We only need to keep track of which finger this is sequentially (which will help with preventing finger collisions in the case of slide-able fingers),
	// As well as a reference in time to know which note it is hovering over and when.
	// If the finger implementation is Full or Limited (aka, non-sliding),
	//   then there is only 1 time/note relation: the note it is hovering over at 0ms, is the same note it will be hovering over the entire song, including at {end time}ms
	// We will only need to store the times when changes occur: for example is, if we have a sliding finger, and we move from CompareValue 10 to 11 at 40000ms,
	// Then the collection would contain compVal 10 at 0, and compVal 11 at 40000, so if you try to look up any value between 0 and 39999 ms (although it is expected to be a
	//   multiple of the MusicSheet's greatestCommonDivisor), then it would return compVal 10, and anything greater would be 11.
	
	// TODO how much time will sliding fingers require to move from one key to the next? And does this differ for coming to / from sharps?
	//      will we need to account for this incrementally in the time/cv collection? Like estimates of its location per greatestCommonDivisor?
	
	int fingerSequence;
	HashMap<Integer, Double> timesToCompVals;
	
	public Finger(int sequence, double startCompareValue) {
		fingerSequence = sequence;
		timesToCompVals = new HashMap<Integer, Double>();
		timesToCompVals.put(0, startCompareValue);
	}
	
	
	public void slide(int time, double destCompareValue) {
		// TODO NYI
		// Hand should check if it is possible to slide first. If it is, "slide" by adding a new entry to the collection
		// with the new compare value at the given timestamp.
		// The hashmap will need to stay ordered by the Timestamp (integer) key
	}
	
	
	/**
	 * Given a timestamp within the song (in milliseconds), return which compareValue this Finger is hovering over at that time.
	 * @param timeInMs
	 * @return
	 */
	public double getCompValAtTime(int timeInMs) {
		double compVal;
		
		if (timesToCompVals.size() == 1) {
			compVal = timesToCompVals.get(0);
		} else {
			// Interate through the ordered collection until you come to a timestamp that is GREATER than the desired timestamp.
			// Once you find that one, you know you use the note it was hovering over prior to that change.
			// If there is no timestamp greater than the desired timeInMs, then that means the last note hovered over is the one we want.
			// TODO NYI
			compVal = -1;
		}
		
		return compVal;
	}
	
	@Override
	public String toString() {
		return "Finger: " + fingerSequence + " - initial position: " + getCompValAtTime(0);
	}
	
	/**
	 * Returns the identifying finger sequence id
	 * @return
	 */
	public int getFingerSequence() {
		return fingerSequence;
	}
	
}
