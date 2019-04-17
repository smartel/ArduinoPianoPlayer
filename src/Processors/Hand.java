package Processors;

import java.util.HashMap;
import java.util.LinkedList;

import DataObjs.Finger;
import DataObjs.MusicSheet;
import DataObjs.PianoProperties;
import Utils.AlcStatsUtils;
import Utils.AlcStatsUtils.NoteStats;
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
	boolean didInit;
	
	public Hand(PianoProperties properties, MusicSheet sheet) {
		
		didInit = true; // we'll assume a successful initialization until we see otherwise
		int fingerImpl = Integer.parseInt(properties.getSetting(Constants.SETTINGS_FINGER_IMPL));
		
		// Initialize fingers
		// Fingers are simple objects that basically know their sequential position, and what know they are hovering over (and or pressing) at a given time.
		// We only track changes when it MOVES to a new note to hover over / press.
		// For example, if a finger starts on compareValue 6 and stays there for 40 seconds, we don't need to record it is on 6 at every greatestCommonDivisor, only when the change happens at 40.
		// And the goal is that the Hand will be able to tell you what Finger is hovering over a specific compareValue at any given point in time.
		// This may seem like a lot of work for what should boil down to just a quick 1:1 lookup for Full (and still a quick static lookup for Limited finger implementations),
		//   but accounting for time leaves the door open for implementing Sliding finger implementations sometime in the future.
		fingers = new LinkedList<Finger>();

		AlcStatsUtils asu = new AlcStatsUtils();
		HashMap<Double, NoteStats> stats = asu.generateCompValStats(sheet);
		
		
		// If a Full finger implementation, create a finger object for every available compareValue
		if (fingerImpl == Constants.FINGER_IMPL_FULL) {
			double currCompVal = Double.parseDouble(properties.getSetting(Constants.SETTINGS_MIN_COMP_VALUE)); // The start / initial compare value on the leftmost of the piano
			double endCompVal = Double.parseDouble(properties.getSetting(Constants.SETTINGS_MAX_COMP_VALUE));
			int fingerSeq = 1;
			while (currCompVal <= endCompVal) {
				Finger finger = new Finger(fingerSeq, currCompVal);
				fingers.add(finger);
				++fingerSeq;
				currCompVal = NoteUtils.getNextNoteCV(currCompVal);
			}
		}
		
		// If a Limited finger implementation, determine how many fingers are needed (StatsUtils), and create a finger for each note.
		// We don't actually check how many static fingers they have. Either they have enough and it is fine, or they don't have enough, and when they get the .stats / .fng file,
		// they'll realize they can't play it. As long as there is > 0 static fingers in their properties file, we'll consider it in LIMITED implementation move and continue to process.
		else if (fingerImpl == Constants.FINGER_IMPL_LIMITED) {
			
			double currCompVal = Double.parseDouble(properties.getSetting(Constants.SETTINGS_MIN_COMP_VALUE)); // The start / initial compare value on the leftmost of the piano
			double endCompVal = Double.parseDouble(properties.getSetting(Constants.SETTINGS_MAX_COMP_VALUE));
			int fingerSeq = 1;
			while (currCompVal <= endCompVal) {
				// Since this is limited (not full), only create a finger IF it was hit
				if (stats.get(currCompVal).getNumTimesHit() > 0) {
					Finger finger = new Finger(fingerSeq, currCompVal);
					fingers.add(finger);
					++fingerSeq;
				}
				currCompVal = NoteUtils.getNextNoteCV(currCompVal);
			}
			
			System.out.println("Stats required for setting up limited fingers:\n");
			System.out.println(asu.getFullStats(sheet, false));
			System.out.println("\nFinger setup confirmation:");
			for (int x = 0; x < fingers.size(); ++x) {
				System.out.println(fingers.get(x).toString());
			}
		}
		
		else if (fingerImpl == Constants.FINGER_IMPL_SLIDING) {
			// Sliding finger implementation TBD. We'd need to know how many fingers are available, how fast they can slide, and track their positions within Finger objects,
			// while also detecting / preventing potential collisions.
			// At a minimum, we would need either AlcStatsUtils#getMaxSimulHitsAndHolds or AlcStatsUtils#getMaxSimulHits number of fingers.
			// TODO NYI
			// do a java implementation of our old c++ problem solver framework to find the best solution for handling slides?
				// based on the minimum number of keys needed based on >how many notes are ever hit simultaneously?
					// if we account for lifting keys 0.15 seconds early or whatever, we should TOTAL how much "play time" is "lost" that way
						// and then see if we can decrease the "lost time" by adding more fingers, hopefully bringing the "lost" time to 0.
							// so an optimal solution would be somewhere between >least number of fingers while also having closest to 0 lost time?
		} else if (fingerImpl == Constants.FINGER_IMPL_GUI_ONLY) {
			System.out.println("Hand#ctor: Finger Implementation is set to GUI-only mode (no static or sliding fingers). Fingers can't be initialized. Confirm - fingerImpl: " + fingerImpl);
			didInit = false;
		} else {
			System.out.println("Hand#ctor: unknown finger implementation supplied. Fingers can't be initialized. Supplied fingerImpl: " + fingerImpl);
			didInit = false;
		}
		
		
		// print out a GREAT BIG WARNING about notes that are out of range? (that is, on octaves that the piano can't reach, even if a Full implementation)
		for (double x = Constants.MIN_THEORETICAL_COMPARE_VALUE;
			 x < Double.parseDouble(properties.getSetting(Constants.SETTINGS_MIN_COMP_VALUE));
			 x = NoteUtils.getNextNoteCV(x)) {
			
			// Warn that the following hit notes are too low
			if (stats.get(x).getNumTimesHit() > 0) {
				System.out.println("Hand#ctor: warning - Note compareValue hit in song is too low to be hit according to the piano properties. Out-of-range compareValue: " + x);
			}
		}
		for (double x = NoteUtils.getNextNoteCV(Double.parseDouble(properties.getSetting(Constants.SETTINGS_MAX_COMP_VALUE)));  
			 x <= Constants.MAX_THEORETICAL_COMPARE_VALUE;
			 x = NoteUtils.getNextNoteCV(x)) {
			
			// Warn that the following hit notes are too high
			if (stats.get(x).getNumTimesHit() > 0) {
				System.out.println("Hand#ctor: warning - Note compareValue hit in song is too high to be hit according to the piano properties. Out-of-range compareValue: " + x);
			}
		}
		
		
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
	

	
	
	

	/**
	 * @return true if this Hand successfully initialized (didn't have a bad finger implementation, ...). false otherwise
	 */
	public boolean didInit() {
		return didInit;
	}
}
