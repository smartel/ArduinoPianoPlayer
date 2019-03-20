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
	 * Which Note (A,B,C,D,E,F,G) to hit
	 */
	private String note;
	
	/**
	 * Which octave the note is in
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
	 * TBD: will depend on bpm / time signature / etc. May need to exclude notes smaller than a certain size, such as 1/8th notes.
	 */
	private double duration;
	
	/**
	 * Constructs the note using the octave and a String note value (for example, octave 2 "C"), with flags for whether the note is sharp or flat.
	 * We maintain the exact note that is sharp or flat, rather than try to guess it, so as to be accurate with the source file (such as sheet music or a MusicXML file).
	 * If for any reason the note can't be created (such as both the sharp and flat flags being set to true), then an error will be logged and an exception thrown.
	 * @param note The specific note (A,B,C,D,E,F,G) to hit (ignoring half-steps). Additionally, "REST" is accepted here as a Rest note.
	 * @param octave The octave the note is in (1-x)
	 * @param duration The duration the note should be played for, in milliseconds
	 * @param isSharp a flag indicating whether the note is a sharp. if true, the note is treated as a sharp
	 * @param isFlat a flag indicating whether the note is a flat. if false, the note is treated as a flat
	 */
	public MusicNote(String note, int octave, int duration, boolean isSharp, boolean isFlat) {
		initializeNote(note, octave, duration, isSharp, isFlat);
		// TODO throw if initializeNote returns false?
	}
	
	/**
	 * Overloaded constructor that assumes the note is neither sharp nor flat
	 * @param note The specific note (A,B,C,D,E,F,G) to hit (ignoring half-steps). Additionally, "REST" is accepted as a Rest note.
	 * @param octave The octave the note is in (1-x)
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
		initializeNote(Constants.REST_NOTE, Constants.REST_OCTAVE_VALUE, duration, false, false);
		// TODO throw if initializeNote returns false?
	}
	
	/**
	 * Attempts to initialize the note with the given parameters. If it fails, an error is logged and an exception thrown
	 * @param note The specific note (A,B,C,D,E,F,G) to hit (ignoring half-steps)
	 * @param octave The octave the note is in (1-x)
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
		
		// Ensure the note is not marked as both sharp AND flat
		if (isSharp && isFlat) {
			// although, if it is a rest note, we only need to warn
			if (note.equalsIgnoreCase(Constants.REST_NOTE)) {
				System.out.println("MusicNote#initializeNote - warning - isSharp and isFlat both set to true, but on a rest note, so initialization will continue.\r\nConfirmation - isSharp: " + isSharp + ", isFlat: " + isFlat + ", note: " + note);
			} else {
				System.out.println("MusicNote#initializeNote - failed to initialize - isSharp and isFlat both set to true.\r\nConfirmation - isSharp: " + isSharp + ", isFlat: " + isFlat);
				isSuccessful = false;
			}
		}
		// Ensure the sharp or flat flags aren't set on a rest, but if they are, they can just be ignored and initialization can continue.
		if (isSharp && note.equalsIgnoreCase(Constants.REST_NOTE)) {
			System.out.println("MusicNote#initializeNote - warning - isSharp is set to true on a Rest note. isSharp flag ignored.\r\nConfirmation - isSharp: " + isSharp + ", note: " + note);
		}

		if (isFlat && note.equalsIgnoreCase(Constants.REST_NOTE)) {
			System.out.println("MusicNote#initializeNote - warning - isFlat is set to true on a Rest note. isFlat flag ignored.\r\nConfirmation - isFlat: " + isFlat + ", note: " + note);
		}
		
		// Ensure the note is a valid piano key
		if ( (!note.equalsIgnoreCase(Constants.A_NOTE)) &&
			 (!note.equalsIgnoreCase(Constants.B_NOTE)) &&
			 (!note.equalsIgnoreCase(Constants.C_NOTE)) &&
			 (!note.equalsIgnoreCase(Constants.D_NOTE)) &&
			 (!note.equalsIgnoreCase(Constants.E_NOTE)) &&
			 (!note.equalsIgnoreCase(Constants.F_NOTE)) &&
			 (!note.equalsIgnoreCase(Constants.G_NOTE)) &&
			 (!note.equalsIgnoreCase(Constants.REST_NOTE)) ) {
			System.out.println("MusicNote#initializeNote - failed to initialize - unrecognized note value passed in.\r\nnote: " + note);
			isSuccessful = false;
		}
		
		// Ensure the note has a non-zero / non-negative octave, if it is not a rest note
		if (octave <= 0 && (!note.equalsIgnoreCase(Constants.REST_NOTE))) {
			System.out.println("MusicNote#initializeNote - failed to initialize - invalid octave value (0 or negative) for a non-rest note.\r\noctave: " + octave);
			isSuccessful = false;
		}
		// Ensure the octave is zero if it is a rest note, but if it isn't, we only need to provide a warning, and can keep initializing
		if (octave != 0 && (note.equalsIgnoreCase(Constants.REST_NOTE))) {
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
	
	public double getCompareValue() {
		return compareValue;
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
}
