package DataObjs;

import Utils.Constants;
import Utils.NoteUtils;

/**
 * A MusicNote refers to a specific individual musical note within a specific octave with a specific duration.
 * 
 * @author smartel
 */
public class MusicNote {

	// TODO it's not really worth it to have an enum for WHOLE-STEP (or something?), SHARP, FLAT, is it? Or a ternary :^)

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
	 * @param note The specific note (A,B,C,D,E,F,G) to hit (ignoring half-steps)
	 * @param octave The octave the note is in (1-x)
	 * @param isSharp a flag indicating whether the note is a sharp. if true, the note is treated as a sharp
	 * @param isFlat a flag indicating whether the note is a flat. if false, the note is treated as a flat
	 */
	public MusicNote(String note, int octave, boolean isSharp, boolean isFlat) {
		initializeNote(note, octave, isSharp, isFlat);
	}
	
	/**
	 * Overloaded constructor that assumes the note is neither sharp nor flat
	 * @param note The specific note (A,B,C,D,E,F,G) to hit (ignoring half-steps)
	 * @param octave The octave the note is in (1-x)
	 */
	public MusicNote(String note, int octave) {
		initializeNote(note, octave, false, false);
	}
	
	/**
	 * Attempts to initialize the note with the given parameters. If it fails, an error is logged and an exception thrown
	 * @param note The specific note (A,B,C,D,E,F,G) to hit (ignoring half-steps)
	 * @param octave The octave the note is in (1-x)
	 * @param isSharp a flag indicating whether the note is a sharp. if true, the note is treated as a sharp
	 * @param isFlat a flag indicating whether the note is a flat. if false, the note is treated as a flat
	 */
	public void initializeNote(String note, int octave, boolean isSharp, boolean isFlat) {
		this.note = note;
		this.octave = octave;
		this.isSharp = isSharp;
		this.isFlat = isFlat;
		
		// Ensure the note is not marked as both sharp AND flat
		if (isSharp && isFlat) {
			System.out.println("MusicNote#initializeNote - failed to initialize - isSharp and isFlat both set to true.\r\nConfirmation - isSharp: " + isSharp + ", isFlat: " + isFlat);
			// TODO abort / throw exception?
		}
		
		// Ensure the note is a valid piano key
		if (note != Constants.A_NOTE &&
			note != Constants.B_NOTE &&
			note != Constants.C_NOTE &&
			note != Constants.D_NOTE &&
			note != Constants.E_NOTE &&
			note != Constants.F_NOTE &&
			note != Constants.G_NOTE) {
			System.out.println("MusicNote#initializeNote - failed to initialize - unrecognized note value passed in.\r\nnote: " + note);
			// TODO abort / throw exception?
			
		}
		
		// Ensure the note is a non-zero / non-negative octave
		if (octave <= 0) {
			System.out.println("MusicNote#initializeNote - failed to initialize - invalid octave value (0 or negative).\r\noctave: " + octave);
			// TODO abort / throw exception?
		}
		// TODO Ensure the note exists on the piano (is not out-of-bounds) by checking the piano's properties (this will catch if the octave value is too high, for example).
		//      Additionally, it will also check if the note is so far to the left that it doesn't exist on the piano,
		//      because a piano may not reach all the way down to A on its first octave. Mine starts on note E on its first octave, so a 1st octave A,B,C, or D would be out of range.
		
		
		// Ensure the note is a non-zero / non-negative duration
		if (duration <= 0) {
			System.out.println("MusicNote#initializeNote - failed to initialize - invalid duration value (0 or negative).\r\nduration: " + duration);
			// TODO abort / throw exception?
		}		
		// TODO additionally, do we want to check if the duration value is too small for our piano player to handle? Such as 1/8th notes, 1/16th, 1/32nd, so on?
		//      Not sure what will be "humanly-possible" for our technology yet. Presumably the smallest duration we can handle is something we can put in Properties and compare against.


		compareValue = NoteUtils.generateCompareValue(note, octave, isSharp, isFlat);
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
		details = "CompareValue: " + compareValue + " | Duration: " + duration + " | Note: " + note + ", Octave: " + octave + " | isSharp: " + isSharp + ", isFlat: " + isFlat;
		return details;
	}
}
