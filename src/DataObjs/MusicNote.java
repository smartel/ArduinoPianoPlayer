package DataObjs;

import Utils.Constants;

/**
 * A MusicNote refers to a specific individual musical note within a specific octave with a specific duration.
 * @author smartel
 *
 */
public class MusicNote {

	// TODO may as well implement 'comparable' and use the compareValue for it?
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
	private int compareValue;
	
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
	 * @param isSharp if true, the note is treated as a sharp
	 * @param isFlat if false, the note is treated as a flat
	 */
	public MusicNote(String note, int octave, boolean isSharp, boolean isFlat) {
		initializeNote(note, octave, isSharp, isFlat);
	}
	
	/**
	 * Overloaded constructor asssumes the note is neither flat nor sharp
	 * @param note
	 * @param octave
	 */
	public MusicNote(String note, int octave) {
		initializeNote(note, octave, false, false);
	}
	
	/**
	 * Attempts to initialize the note with the given parameters. If it fails, an error is logged and an exception thrown
	 * @return
	 */
	public void initializeNote(String note, int octave, boolean isSharp, boolean isFlat) {
		this.note = note;
		this.octave = octave;
		this.isSharp = isSharp;
		this.isFlat = isFlat;
		
		if (isSharp && isFlat) {
			// TODO log error message
			// TODO abort (throw exception?) - unless we want to just keep one value instead, like, force it to be sharp? then log that change and dont abort? and update comments.
		}
		
		if (note != Constants.A_NOTE &&
			note != Constants.B_NOTE &&
			note != Constants.C_NOTE &&
			note != Constants.D_NOTE &&
			note != Constants.E_NOTE &&
			note != Constants.F_NOTE &&
			note != Constants.G_NOTE) {		
			// TODO the note is not recognized (not A through G), so log an error and throw an exception
		}
		
		if (octave <= 0) { // || octave >= ?? We can put a "max octave" final in Constants.java if necessary
			// TODO if the octave is not valid (0, negative, perhaps some maximum value like 9?), log error and throw exception.
		}
		
		// TODO should we check the properties of the piano here to see if the note is valid / a note within the bounds of the specific piano?
		
		// TODO duration error checking (if the duration is smaller than 0, perhaps smaller than 1/8th note (still TBD on how we're doing duration))
		
		// TODO call NoteUtils#generateCompareValue
		//compareValue = ;
	}
}
