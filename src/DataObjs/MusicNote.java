package DataObjs;

import Utils.Constants;
import Utils.NoteUtils;

/**
 * A MusicNote refers to a specific individual musical note within a specific octave with a specific duration.
 * 
 * @author smartel
 */
public class MusicNote implements Comparable<MusicNote> {

	/**
	 * Which Note (C,D,E,F,G,A,B) to hit
	 */
	private String note;
	
	/**
	 * Which octave the note is in. Note, octaves start on C and end on B.
	 */
	private int octave;
	
	/**
	 * Whether or not the note is a sharp
	 */
	private boolean isSharp;
	
	/**
	 * Whether or not the note is a flat
	 */
	private boolean isFlat;
	
	/**
	 * A simple double for comparing piano keys relative to each other. See NoteUtils#generateCompareValue for a full explanation.
	 * Quick synopsis - white notes count for full steps (1), a sharp adds 0.5, and a flat subtracts 0.5.
	 */
	private double compareValue;
	
	/**
	 * The duration of time the note should be held for when hit, in milliseconds.
	 */
	private int duration;
	
	/**
	 * Used purely by the PianoFeigner to know how much longer it needs to display the note for, in milliseconds.
	 */
	private int remainingDuration;
	
	/**
	 * Constructs the note using the octave and a String note value (for example, octave 2 "C"), with flags for whether the note is sharp or flat.
	 * We maintain the exact note that is sharp or flat, rather than try to guess it, so as to be accurate with the source file (such as sheet music or a MusicXML file).
	 * If for any reason the note can't be created (such as both the sharp and flat flags being set to true), then an error will be logged and an exception thrown.
	 * @param note The specific note (C,D,E,F,G,A,B) to hit (ignoring half-steps). Additionally, "REST" is accepted here as a Rest note.
	 * @param octave The octave the note is in (0-10)
	 * @param duration The duration the note should be played for, in milliseconds
	 * @param isSharp a flag indicating whether the note is a sharp. if true, the note is treated as a sharp
	 * @param isFlat a flag indicating whether the note is a flat. if false, the note is treated as a flat
	 */
	public MusicNote(String note, int octave, int duration, boolean isSharp, boolean isFlat) {
		
		// If we get an "impossible" note from some source material (such as a B sharp or E flat), promote or demote the note as necessary to its neighbor.
		if ( (note.equalsIgnoreCase(Constants.NOTE_B) && isSharp) ||
			 (note.equalsIgnoreCase(Constants.NOTE_E) && isSharp) ||
			 (note.equalsIgnoreCase(Constants.NOTE_C) && isFlat) ||
			 (note.equalsIgnoreCase(Constants.NOTE_F) && isFlat) ) {
			System.out.println("MusicNote#ctor(note,octave,duration,isSharp,isFlat) - warning - had a sharp B or sharp E, or a flat C or flat F. Confirm - note: " + note +
					           ", isSharp: " + isSharp + ", isFlat: " + isFlat);
			// promote the note to the appropriate natural note (B sharp becomes C natural, E sharp becomes F natural, C flat becomes B natural, F flat becomes E natural)
			// note that when a B is promoted to a C, we increment the octave by 1, since octaves start on C. Likewise for demoting from C to B and decrementing octave by 1.
			isSharp = false;
			isFlat = false;
			if (note.equalsIgnoreCase(Constants.NOTE_B)) {
				System.out.println("MusicNote#ctor(note,octave,duration,isSharp,isFlat) - warning - note has been promoted to a C natural");
				note = Constants.NOTE_C;
				++octave;
			} else if (note.equalsIgnoreCase(Constants.NOTE_E)) {
				System.out.println("MusicNote#ctor(note,octave,duration,isSharp,isFlat) - warning - note has been promoted to a F natural");
				note = Constants.NOTE_F;
			} else if (note.equalsIgnoreCase(Constants.NOTE_C)) {
				System.out.println("MusicNote#ctor(note,octave,duration,isSharp,isFlat) - warning - note has been demoted to a B natural");
				note = Constants.NOTE_B;
				--octave;
			} else if (note.equalsIgnoreCase(Constants.NOTE_F)) {
				System.out.println("MusicNote#ctor(note,octave,duration,isSharp,isFlat) - warning - note has been demoted to an E natural");
				note = Constants.NOTE_E;
			}
			
		}
		
		initializeNote(note, octave, duration, isSharp, isFlat);
		// TODO throw if initializeNote returns false?
	}
	
	/**
	 * Overloaded constructor that assumes the note is neither sharp nor flat
	 * @param note The specific note (C,D,E,F,G,A,B) to hit (ignoring half-steps). Additionally, "REST" is accepted as a Rest note.
	 * @param octave The octave the note is in (0-10)
	 * @param duration The duration the note should be played for, in milliseconds
	 */
	public MusicNote(String note, int octave, int duration) {
		initializeNote(note, octave, duration, false, false);
		// TODO throw if initializeNote returns false?
	}

	/**
	 * Overloaded constructor that assumes the note is a rest.
	 * Rests do not have octaves, sharps, flats, etc. Technically it wouldn't even have a "note" value except we're shoving "REST" in there.
	 * @param duration The duration the rest should last for, in milliseconds
	 */
	public MusicNote(int duration) {
		initializeNote(Constants.NOTE_REST, Constants.REST_OCTAVE_VALUE, duration, false, false);
		// TODO throw if initializeNote returns false?
	}
	
	/**
	 * Overloaded constructor that takes in a compare value and duration, and determines an appropriate note letter, octave, and whether it should be sharp or not.
	 * This will never result in a flat.
	 * @param compareValue A compare value to determine note letter and octave from, and whether it should be sharp
	 * @param duration The duration the rest should last for, in milliseconds
	 */
	public MusicNote(double compVal, int duration) {
		
		// If it is a rest note, use the rest constants
		if (compVal == Constants.REST_COMP_VALUE) {
			isSharp = false;
			isFlat = false;
			octave = Constants.REST_OCTAVE_VALUE;
			note = Constants.NOTE_REST;
		} else {
			
			// this is a temp variable to help determine what the letter / octave are
			double tempCompValue = compVal;
			
			// Determine if this is a sharp or not, by seeing if there is a 0.5 modifier.
			isFlat = false;
			if (tempCompValue % 1 == 0.5) {
				isSharp = true;
				tempCompValue -= 0.5;
			}
			// End result: a whole-number "temp" compare value.
			// Next, we'll shave off octaves 1 at a time, increasing the octave count each time, until we are left with a note position, which can be used to directly get the note letter.
			octave = 0;
			while (tempCompValue > 7) {
				tempCompValue -= 7;
				++octave;
			}
			note = NoteUtils.getNoteForPosition((int)tempCompValue);
		}
		
		// Check if we have an impossible note, but since we were trying to build from a compareValue, we can't be sure whether to promote or demote
		// (we don't know if they were trying to make a C become a flat (a B), or a B become a sharp (a C)
		if ( (note.equalsIgnoreCase(Constants.NOTE_B) && isSharp) ||
			 (note.equalsIgnoreCase(Constants.NOTE_E) && isSharp) ||
			 (note.equalsIgnoreCase(Constants.NOTE_C) && isFlat) ||
			 (note.equalsIgnoreCase(Constants.NOTE_F) && isFlat) ) {
			
			// Since we are basing this off of a "compare value", we can't tell if it was meant to be a B sharp (and thus, a C), or a C flat (and thus, a B),
			// since both of those would have a compare value of "6.5"
			
			// We're just going to set a compare value of -1 and error out, we don't have a way to 100% guarantee what this note should really be.
			compareValue = -1;
			// TODO throw exception?
		} else {
			initializeNote(note, octave, duration, isSharp, false);
			// TODO throw if initializeNote returns false?
		}
	}
	
	/**
	 * Simple copy constructor
	 * @param other
	 */
	public MusicNote(MusicNote other) {
		this.note = new String(other.note);
		this.octave = other.octave;
		this.isSharp = other.isSharp;
		this.isFlat = other.isFlat;
		this.duration = other.duration;
		this.compareValue = other.compareValue;
		this.remainingDuration = other.remainingDuration; // as things stand this shouldn't ever be needed, but erring on the side of caution
	}
	
	/**
	 * Attempts to initialize the note with the given parameters. If it fails, an error is logged and an exception thrown
	 * @param note The specific note (C,D,E,F,G,A,B) to hit (ignoring half-steps)
	 * @param octave The octave the note is in (0-10)
	 * @param duration The duration the note should be played for, in milliseconds
	 * @param isSharp a flag indicating whether the note is a sharp. if true, the note is treated as a sharp
	 * @param isFlat a flag indicating whether the note is a flat. if false, the note is treated as a flat
	 */
	public boolean initializeNote(String note, int octave, int duration, boolean isSharp, boolean isFlat) {
		boolean isSuccessful = true;
		this.note = note;
		this.octave = octave;
		this.isSharp = isSharp;
		this.isFlat = isFlat;
		this.duration = duration;
		
		// Ensure the note is not marked as both sharp AND flat
		if (isSharp && isFlat) {
			// although, if it is a rest note, we only need to warn
			if (note.equalsIgnoreCase(Constants.NOTE_REST)) {
				System.out.println("MusicNote#initializeNote - warning - isSharp and isFlat both set to true, but on a rest note, so initialization will continue.\r\nConfirmation - isSharp: " + isSharp + ", isFlat: " + isFlat + ", note: " + note);
			} else {
				System.out.println("MusicNote#initializeNote - failed to initialize - isSharp and isFlat both set to true.\r\nConfirmation - isSharp: " + isSharp + ", isFlat: " + isFlat);
				isSuccessful = false;
			}
		}
		// Ensure the sharp or flat flags aren't set on a rest, but if they are, they can just be ignored and initialization can continue.
		if (isSharp && note.equalsIgnoreCase(Constants.NOTE_REST)) {
			System.out.println("MusicNote#initializeNote - warning - isSharp is set to true on a Rest note. isSharp flag ignored.\r\nConfirmation - isSharp: " + isSharp + ", note: " + note);
		}

		if (isFlat && note.equalsIgnoreCase(Constants.NOTE_REST)) {
			System.out.println("MusicNote#initializeNote - warning - isFlat is set to true on a Rest note. isFlat flag ignored.\r\nConfirmation - isFlat: " + isFlat + ", note: " + note);
		}
		
		// Ensure the note is a valid piano key
		if ( (!note.equalsIgnoreCase(Constants.NOTE_C)) &&
			 (!note.equalsIgnoreCase(Constants.NOTE_D)) &&
			 (!note.equalsIgnoreCase(Constants.NOTE_E)) &&
			 (!note.equalsIgnoreCase(Constants.NOTE_F)) &&
			 (!note.equalsIgnoreCase(Constants.NOTE_G)) &&
			 (!note.equalsIgnoreCase(Constants.NOTE_A)) &&
			 (!note.equalsIgnoreCase(Constants.NOTE_B)) &&
			 (!note.equalsIgnoreCase(Constants.NOTE_REST)) ) {
			System.out.println("MusicNote#initializeNote - failed to initialize - unrecognized note value passed in.\r\nnote: " + note);
			isSuccessful = false;
		}
		
		// Ensure the note has a non-zero / non-negative octave, if it is not a rest note
		if (octave < Constants.MIN_PIANO_OCTAVE && (!note.equalsIgnoreCase(Constants.NOTE_REST))) {
			System.out.println("MusicNote#initializeNote - failed to initialize - invalid octave value (negative) for a non-rest note.\r\noctave: " + octave);
			isSuccessful = false;
		}
		
		// Ensure the octave is zero if it is a rest note, but if it isn't, we only need to provide a warning, and can keep initializing
		if (octave != Constants.REST_OCTAVE_VALUE && (note.equalsIgnoreCase(Constants.NOTE_REST))) {
			System.out.println("MusicNote#initializeNote - warning - non-zero octave value provided for a rest note. Octave value ignored.\r\noctave: " + octave);
		}
		
		// TODO Ensure the note exists on the piano (is not out-of-bounds) by checking the piano's properties (this will catch if the octave value is too high, for example).
		//      Additionally, it will also check if the note is so far to the left that it doesn't exist on the piano,
		//      because a piano may not reach all the way down to A on its first octave. Mine starts on note E on its first octave, so a 1st octave A,B,C, or D would be out of range.
		
		// Ensure the note is a non-zero / non-negative duration
		if (duration <= 0) {
			System.out.println("MusicNote#initializeNote - failed to initialize - invalid duration value (0 or negative).\r\nduration: " + duration);
			isSuccessful = false;
		}		
		// TODO additionally, do we want to check if the duration value is too small for our piano player to handle? Such as 1/8th notes, 1/16th, 1/32nd, so on converting to super tiny milliseconds?
		//      Not sure what will be "humanly-possible" for our technology yet. Presumably the smallest duration we can handle is something we can put in Properties and compare against.

		if (isSuccessful) {
			compareValue = NoteUtils.generateCompareValue(note, octave, isSharp, isFlat);
		} else {
			compareValue = -1; // intentionally set to an invalid value
		}
		
		// lastly, ensure we set isSuccessful to false if generateCompareValue had returned a failure value
		if (compareValue == -1) {
			isSuccessful = false;
		}
		
		return isSuccessful;
	}
	
	/**
	 * Simple getter for duration
	 * @return duration
	 */
	public int getDuration() {
		return duration;
	}
	
	/**
	 * Simple getter for compareValue
	 * @return
	 */
	public double getCompareValue() {
		return compareValue;
	}
	
	/**
	 * Simple getter for octave
	 * @return
	 */
	public int getOctave() {
		return octave;
	}
	
	/**
	 * Simple getter for note
	 * @return
	 */
	public String getNote() {
		return note;
	}
	
	/**
	 * Overridden compareTo that performs a Double compareTo using the MusicNotes' compareValue field.
	 * @return Double.compare result using the two objects' compareValue fields
	 */
	public int compareTo(MusicNote other) {
		return Double.compare(compareValue, other.compareValue);
	}
	
	/**
	 * Overridden toString that returns the details for this MusicNote object
	 * @return a string containing all the fields and values for this MusicNote
	 */
	public String toString() {
		String details;
		details = "CompareValue: " + compareValue + " | Duration: " + duration + "ms | Note: " + note + ", Octave: " + octave + " | isSharp: " + isSharp + ", isFlat: " + isFlat;
		return details;
	}
	
	/**
	 * Given a new bpm multiplier, multiply the note's duration by the amount.
	 * It is expected that the note's start time will also be multiplied by the same amount within its MusicSlice. 
	 * @param bpmMult positive integer value to multiply the note's duration by
	 * @return true if successful, false otherwise
	 */
	public boolean applyBpmMultipler(int bpmMult) {
		boolean isSuccessful = true;
		if (bpmMult <= 0) {
			isSuccessful = false;
		} else {
			duration *= bpmMult;
		}
		return isSuccessful;
	}
	
	/**
	 * Given an octave adjustment (a positive or negative integer), add it to the current octave value.
	 * If the octave would be pushed below 1 or above 8, then at this moment we will keep it at 1 or 8 rather than deleting the note, unless the delete flag is set.
	 * If the delete flag is set, then we will turn the note into a rest if it goes past a boundary, effectively deleting it.
	 * @param octaveAmt non-zero integer value to add to the note's current octave value.
	 * @param doDeleteNotesPastBoundary if true, if an octave goes below the minimum octave range (1) or above the maximum octave range (8), we'll turn it into a rest to "delete" it.
	 * @return true if successful, false if it attempted to go below octave 1 or above octave 8, while still keeping it at octave 1 or 8.
	 */
	public boolean applyOctaveAdjustment(int octaveAmt, boolean doDeleteNotesPastBoundary) {
		boolean hitBound = false;
		
		octave += octaveAmt;
		
		if (octave < Constants.MIN_PIANO_OCTAVE) {
			hitBound = true;
			octave = Constants.MIN_PIANO_OCTAVE;
		} else if (octave > Constants.MAX_PIANO_OCTAVE) {
			hitBound = true;
			octave = Constants.MAX_PIANO_OCTAVE;
		}
		
		if (hitBound && doDeleteNotesPastBoundary) {
			note = Constants.NOTE_REST;
			octave = Constants.REST_OCTAVE_VALUE;
			compareValue = Constants.REST_COMP_VALUE;
			isSharp = false; // rest alter is 0
			isFlat = false; // rest alter is 0
		} else {
			compareValue = NoteUtils.generateCompareValue(note, octave, isSharp, isFlat);
		}
		
		return hitBound;
	}
	
	/**
	 * Given a shift adjustment (a positive or negative integer), shift the note that many compare values to the left (down / negative) or right (up / positive).
	 * If the note goes out of range (too low or too high), turn it into a rest, effectively deleting it. Sheet music cleanup will later remove it from the slice.
	 * @param shiftAmt non-zero integer value to shift the note's current location by.
	 */
	public void applyShift(int shiftAmt) {

		double newCompVal = NoteUtils.getShiftedCV(compareValue, shiftAmt);
		if (newCompVal == -1) {
			// turn this note into a rest
			note = Constants.NOTE_REST;
			octave = Constants.REST_OCTAVE_VALUE;
			isSharp = false;
			isFlat = false;
			compareValue = Constants.REST_COMP_VALUE;
		} else {
			MusicNote tempNote = new MusicNote(newCompVal, duration);
			compareValue = tempNote.compareValue;
			note = tempNote.note;
			octave = tempNote.octave;
			isSharp = tempNote.isSharp;
			isFlat = tempNote.isFlat;
			// duration won't have changed, and remainingDuration shouldn't apply here.
		}
	}
	
	
	// Methods used purely by the PianoFeigner to know how long to display the note as held in the GUI.
	// This presumably will have no bearing on the Arduino side, and I'm trying to break away from my naming conventions as much as possible to show these are separate.
	
	/**
	 * Init the remaining duration for this note. On initialization, it should just equal the note's regular duration (since it hasn't been displayed yet)
	 */
	public void feignerInitRemainingDuration() {
		remainingDuration = duration;
	}
	/**
	 * Return the amount of time left (in milliseconds) that this note needs to be displayed for.
	 * @return remaining display duration in milliseconds
	 */
	public int feignerGetRemainingDuration() {
		return remainingDuration;
	}
	/**
	 * The amount of time this note has been displayed since it was initialized, or since the last time its duration was decreased.
	 * This needs to be subtracted from its current remaining duration, so we can keep track of how much longer it should be displayed for.
	 * @param amount amount of time in milliseconds to decrease from its remaining duration.
	 */
	public void feignerDecreaseRemainingDuration(int amount) {
		remainingDuration -= amount;
	}
	
}
