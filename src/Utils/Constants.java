package Utils;

public class Constants {

	// The positions of notes within an octave, with A being the first white key in the octave (1), and G being the last white key (7)
	// We treat white keys as whole integers, because the white keys are effectively the surface / track that the robotic "fingers" will move across.
	// Sharps and flats are treated as "half-steps", as in, the distance between a "C" and an "E" is 2 white notes, but because the distance
	//  between "C" and "D sharp" results in a slightly smaller gap, we call it 1.5. That .5 will be instrumental for the finger knowing not to hit a sharp/flat key.
	// Synonymous notes (A sharp, B flat) exist, but not to the extent of double sharps / double flats.
	// While "synonyms" exist, ideally it'd be best to stay as accurate as possible with the source material (sheetmusic, xml, ...),
	//  instead of converting our stored notes to all sharps or all flats.
	// TODO look into how MusicXML treats double-sharps / double-flats?
	final public static double A_FLAT_POS = 0.5;
	final public static double A_POS = 1;
	final public static double A_SHARP_POS = 1.5;
	final public static double B_FLAT_POS = 1.5;
	final public static double B_POS = 2;
	// B has no sharp, thus C has no flat
	final public static double C_POS = 3;
	final public static double C_SHARP_POS = 3.5;
	final public static double D_FLAT_POS = 3.5;
	final public static double D_POS = 4;
	final public static double D_SHARP_POS = 4.5;
	final public static double E_FLAT_POS = 4.5;
	final public static double E_POS = 5;
	// E has no sharp, thus F has no flat
	final public static double F_POS = 6;
	final public static double F_SHARP_POS = 6.5;
	final public static double G_FLAT_POS = 6.5;
	final public static double G_POS = 7;
	final public static double G_SHARP_POS = 7.5;
	
	// how much a note increases or decreases in value based on whether it is sharp or flat
	final public static double SHARP_CHANGE = 0.5;
	final public static double FLAT_CHANGE = -0.5;
	
	// the length of an octave (in white keys)
	final public static int OCTAVE_LENGTH = 7;
	
	// string representations of each note
	final public static String A_NOTE = "A";
	final public static String B_NOTE = "B";
	final public static String C_NOTE = "C";
	final public static String D_NOTE = "D";
	final public static String E_NOTE = "E";
	final public static String F_NOTE = "F";
	final public static String G_NOTE = "G";
}
