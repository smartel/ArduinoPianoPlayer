package Utils;

import java.awt.Color;

public class Constants {

	// The positions of notes within an octave, with A being the first white key in the octave (1), and G being the last white key (7)
	// We treat white keys as whole integers, because the white keys are effectively the surface / track that the robotic "fingers" will move across.
	// Sharps and flats are treated as "half-steps", as in, the distance between a "C" and an "E" is 2 white notes, but because the distance
	//  between "C" and "D sharp" results in a slightly smaller gap, we call it 1.5. That .5 will be instrumental for the finger knowing not to hit a sharp/flat key.
	// Synonymous notes (A sharp, B flat) exist, but not to the extent of double sharps / double flats.
	// While "synonyms" exist, ideally it'd be best to stay as accurate as possible with the source material (sheetmusic, xml, ...),
	//  instead of converting our stored notes to all sharps or all flats.
	final public static double A_FLAT_POS = 5.5;
	final public static double A_POS = 6;
	final public static double A_SHARP_POS = 6.5;
	final public static double B_FLAT_POS = 6.5;
	final public static double B_POS = 7;
	// B has no sharp, thus C has no flat
	final public static double C_POS = 1;
	final public static double C_SHARP_POS = 1.5;
	final public static double D_FLAT_POS = 1.5;
	final public static double D_POS = 2;
	final public static double D_SHARP_POS = 2.5;
	final public static double E_FLAT_POS = 2.5;
	final public static double E_POS = 3;
	// E has no sharp, thus F has no flat
	final public static double F_POS = 4;
	final public static double F_SHARP_POS = 4.5;
	final public static double G_FLAT_POS = 4.5;
	final public static double G_POS = 5;
	final public static double G_SHARP_POS = 5.5;
	
	// how much a note increases or decreases in value based on whether it is sharp or flat
	final public static double SHARP_CHANGE = 0.5;
	final public static double FLAT_CHANGE = -0.5;
	// TODO musicxml allows for double-sharps and double-flats by using +2 and -2 alter values respectively. I don't handle this yet.
	
	// the length of an octave (in white keys)
	final public static int OCTAVE_LENGTH = 7;
	
	// string representations of each note
	final public static String NOTE_C = "C"; // start of an octave
	final public static String NOTE_D = "D";
	final public static String NOTE_E = "E";
	final public static String NOTE_F = "F";
	final public static String NOTE_G = "G";
	final public static String NOTE_A = "A";
	final public static String NOTE_B = "B"; // end of an octave
	final public static String NOTE_REST = "REST";
	
	// Standard values when dealing with Rest notes (which don't have an octave, alter, ...)
	final public static double REST_COMP_VALUE = 0; // A rest note has a compare value of 0.0, as it has no step, no octave, no alter, ...
	final public static int REST_OCTAVE_VALUE = -1; // octave 0 is a valid octave, so we need to go down 1 more
	final public static double REST_ALTER_VALUE = 0;
	
	// Piano Feigner (gui) key sizes
	final public static int KEY_WIDTH_WHITE = 27;
	final public static int KEY_HEIGHT_WHITE = 170;
	final public static int KEY_WIDTH_BLACK = 18;
	final public static int KEY_HEIGHT_BLACK = 125;
	// Various magic numbers for spacing within the piano gui
	final public static int LETTER_X_BUFFER = (KEY_WIDTH_WHITE / 2) - 2;
	final public static int LETTER_Y_BUFFER = KEY_HEIGHT_WHITE - 30;
	final public static int LETTER_HEIGHT = 30;
	final public static int LETTER_WIDTH = 30;
	// colors
	final public static Color KEY_COLOR_WHITE = Color.WHITE;
	final public static Color KEY_COLOR_BORDER = Color.BLACK;
	final public static Color KEY_COLOR_BLACK = Color.BLACK;
	final public static Color KEY_COLOR_HIT = Color.YELLOW;
	
	// implemented voices for the PianoFeigner (determines which .wav files are picked up for compareValues)
	final public static String VOICE_GRAND = "GRAND"; // 3-25-2019 - note: changed the name of this voice from the vague "PIANO" value to "GRAND", as in, grand piano
	final public static String VOICE_ORGEL = "ORGEL";
	

	// Assumed max number of keys there will be on a physical piano
	final public static int MAX_PIANO_KEYS = 88;
	// Assumed max octave value that will be on a physical piano (this is also the max octave value per midi format. musicxml allows up to octave 9.)
	final public static int MAX_PIANO_OCTAVE = 10;
	// Assumed min octave value that will be on a physical piano (this is also the lowest octave value per midi format)
	final public static int MIN_PIANO_OCTAVE = 0;
	// With a theoretical max octave value of 10 per MIDI, the highest possible Compare Value would be a 77 (B on octave 10), if we started at a Compare Value of 1 for a C on octave 0
	final public static double MAX_THEORETICAL_COMPARE_VALUE = 77.0;
	final public static double MIN_THEORETICAL_COMPARE_VALUE = 1.0; // ok, this isn't theoretical, but I wanted its name to be consistent. We aren't counting rests as a hittable comp val here.
	
	// Properties-file settings:
	 // the number of fields that are expected in a Properties file. If the count doesn't match at runtime, we'll throw an error.
	public static final int SETTINGS_EXPECTED_COUNT = 7;
	
	// Properties-file settings that are READ FROM THE FILE
	// Total number of keys (the assumption is made that the first key and last key will both be white keys)
	public static final String SETTINGS_TOTAL_NUM_KEYS = "TOTAL_NUM_KEYS";
	// first note (leftmost key on the physical piano - assumed to be a white key)
	public static final String SETTINGS_FIRST_NOTE = "FIRST_NOTE";
	// first octave (which octave the leftmost key on the piano is in)
	public static final String SETTINGS_FIRST_OCTAVE = "FIRST_OCTAVE";
	// voice (which voice to use for the PianoFeigner)
	public static final String SETTINGS_VOICE = "VOICE";
	// number of sliding fingers available for playing songs
	public static final String SETTINGS_NUM_SLIDING_FINGERS = "NUM_SLIDING_FINGERS";
	// number of static fingers available for playing songs (expected to be 0 if there are sliding fingers, or a 1:1 ratio with total number of keys if there are no sliding fingers)
	public static final String SETTINGS_NUM_STATIC_FINGERS = "NUM_STATIC_FINGERS";
	// flag for whether or not to display letters in the PianoFeigner gui. If 1, letters will be displayed on the gui's piano keys. Any other value will show no letters.
	public static final String SETTINGS_DISPLAY_LETTERS = "DISPLAY_PIANO_LETTERS";

	// Properties-file settings that are DETERMINED FROM READ VALUES
	// minimum compare value possible on the piano (Determined from first key and first octave)
	public static final String SETTINGS_MIN_COMP_VALUE = "MIN_COMPARE_VALUE";
	// max compare value (Determined from first key and first octave)
	public static final String SETTINGS_MAX_COMP_VALUE = "MAX_COMPARE_VALUE";
	// how many white keys (determined from total)
	public static final String SETTINGS_NUM_WHITE_KEYS = "NUM_WHITE_KEYS";
	// how many black keys (determined from total)
	public static final String SETTINGS_NUM_BLACK_KEYS = "NUM_BLACK_KEYS";
	// last note (determined from total)
	public static final String SETTINGS_LAST_NOTE = "LAST_NOTE";
	// last octave (determined from total)
	public static final String SETTINGS_LAST_OCTAVE = "LAST_OCTAVE";
	
	// AlcAlterer options
	public static final String BPM_OPTION = "BPM";
	public static final String OCTAVE_OPTION = "OCTAVE";
	public static final String LOOP_OPTION = "LOOP";
}
