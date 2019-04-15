package Processors;

import java.util.LinkedList;

import DataObjs.Finger;
import DataObjs.PianoProperties;
import Utils.Constants;
import Utils.NoteUtils;

/**
 * The Hand's sole job is: given an alc file, determine which fingers will hit which notes and when.
 * On a sliding implementation, individual finger(s) may move with relation to time, up to as often as the greatestCommonDivisor in milliseconds for the song per the MusicSheet.
 * On full and limited implementations, fingers stay in one static location, and so we only need to look up the finger hovering over a given compareValue regardless of time.
 * 
 * @author smartel
 */
public class Hand {

	LinkedList<Finger> fingers;
	
	public Hand(PianoProperties properties) {
		
		// Initialize fingers
		// Fingers are simple objects that basically know their sequential position, and what know they are hovering over (and or pressing) at a given time.
		// We only track changes when it MOVES to a new note to hover over / press.
		// For example, if a finger starts on compareValue 6 and stays there for 40 seconds, we don't need to record it is on 6 at every greatestCommonDivisor, only when the change happens at 40.
		// And the goal is that the Hand will be able to tell you what Finger is hovering over a specific compareValue at any given point in time.
		// This may seem like a lot of work for what should boil down to just a quick 1:1 lookup for Full (and still a quick static lookup for Limited finger implementations),
		//   but accounting for time leaves the door open for implementing Sliding finger implementations sometime in the future.
		fingers = new LinkedList<Finger>();
		
		// If a Full finger implementation, create a finger object for every available compareValue
		// TODO just keep calling getNextNoteCV with the piano's start cv until you reach the end cv
		// TODO do
		double currCompVal = Double.parseDouble(properties.getSetting(Constants.SETTINGS_MIN_COMP_VALUE)); // The start / initial compare value on the leftmost of the piano
		double endCompVal = Double.parseDouble(properties.getSetting(Constants.SETTINGS_MAX_COMP_VALUE));
		int fingerSeq = 1;
		while (currCompVal <= endCompVal) {
			Finger finger = new Finger(fingerSeq, currCompVal);
			fingers.add(finger);
			++fingerSeq;
			currCompVal = NoteUtils.getNextNoteCV(currCompVal);
		}
		
		// If a Limited finger implementation, determine how many fingers are needed (StatsUtils), and create a finger for each note.
		// TODO and we'll need to print out instructions so the user can set up the physical fingers.
		// TODO NYI
		
		// Sliding finger implementation TBD. We'd need to know how many fingers are available, how fast they can slide, and track their positions within Finger objects,
		// while also detecting / preventing potential collisions.
		// TODO NYI
		// do a java implementation of our old c++ problem solver framework to find the best solution for handling slides?
			// based on the minimum number of keys needed based on >how many notes are ever hit simultaneously?
				// if we account for lifting keys 0.15 seconds early or whatever, we should TOTAL how much "play time" is "lost" that way
					// and then see if we can decrease the "lost time" by adding more fingers, hopefully bringing the "lost" time to 0.
						// so an optimal solution would be somewhere between >least number of fingers while also having closest to 0 lost time?
		
	}
	
	public Finger getFingerForNoteAtTime(double desiredCompVal, int currentTime) {
		Finger finger = null;
		
		for (int x = 0; x < fingers.size(); ++x) {
			if (fingers.get(x).getCompValAtTime(currentTime) == desiredCompVal) {
				finger = fingers.get(x);
				break;
			}
		}
		
		return finger;
	}
	
	
	public boolean slideFinger(Finger slidee, double destCompVal) {
		boolean wasSuccessful = false;
		// TODO NYI
		// when the finger slides, we need to see if it collides with any other physical fingers while attempting to reach the destination compareValue (note).
		// If it does collide, then this fails and returns false.
		// If it doesn't collide: will it make it in a "reasonable amount of time" ? This is TBD. We don't know how fast fingers would be able to slide in the final implementation,
		// but we may need to know Constants of how fast it can slide over a white key distance, as in, if the note needs to go from compareValue 6 to 40 in 1 second, it probably won't make it.
		// Long story short, if the gap in compareValues falls within some TBD time threshold WITHOUT colliding, only then can we return true.
		
		// TODO do we want to do any error checking, such as ensuring there aren't 2 notes that somehow have the same compareValue? Which would be impossible with physical fingers.
		//      it shouldn't be possible to have duplicates with Full and Limited, since we just iterate over every key or every necessary key, but maybe sliding could introduce it.
		
		return wasSuccessful;
	}
	

	
	
	

	
}
