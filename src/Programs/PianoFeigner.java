package Programs;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import DataObjs.MusicNote;
import DataObjs.MusicSheet;
import DataObjs.MusicSlice;
import DataObjs.PianoProperties;
import Processors.AlcReader;
import Utils.Constants;
import Utils.NoteUtils;


	// feigner brainstorming:

	// this is not a "smart" interface. there are no objects representing each individual piano key. the keys are drawn once, and if it is determined it is being hit,
	// then it will be drawn with a different color. trying to click on a piano key will not produce an effect.
	// when it paints, it is just showing the state of the piano at one given point in time.
	// the feigner is meant to be continuously redrawing itself, to show live playing.
	// TODO HUGE UNANSWERED QUESTION:
	// When the .alc file is transposed into arduino code, it could be as simple as, say, "finger 8, hold note xyz for n duration"
	// and other fingers will be given their own notes and durations.
	// However, consider the following example:
	// Finger 8, hold your note for 10 seconds
	// And then 2 seconds later:
	// Finger 2, hold your note for 3 seconds
	// While those are simple >push >wait for x >retract operations on each finger in the arduino, the Feigner will need to know to keep showing finger 8's hold
	// even though it wouldn't technically be in the same MusicSlice
	// WHERE DO WE WANT TO FIX THIS?
	// While I haven't reached the arduino step yet, I anticipate this is a problem solely for the Feigner.
	// So we'd want to handle it here only, and not potentially mess up the arduino's operations?
	// Perhaps MusicSlices need a timestamp on them. When we add a new MusicSlice here, it compares it to the current one via comparing both timestamps.
	// If there are notes in the first music slice that would STILL BE PLAYING in the new music slice, perhaps we add them to the new slice object here with their remaining time.
    // If we do need to bring that solution to arduino (once we get there), we'll already have the solution here too.
	// Although admittedly it feels junky to handle MusicSlices different ways in 2 different places. But we don't want to break a note held for 10 seconds into a ton of separate
	// key presses for the finger either, because that would sound vastly different. We're just trying to solve a potential display issue.

	// look of the gui:
	// how would the feigner gui look? topdown view of a piano, or sheet music view?
	// one could open the musicxml in any sheet music program, so that'd be redundant
	// let's do a topdown view, we could highlight which keys are being hit during playback, which will easily show all notes in a chord
	
	// TODO - do we want audio?
	// audio of the gui:
	// we could capture audio of all of your pianos keys (held for a long duration), so the gui can play them back (and chomp them early if needed)
	// could store each note audio file under its compareValue ? so we don't worry if a given sound is a G sharp or A flat etc
	// not sure how chords will sound / if they'll cut each other off / not sound right
	// we could have a toggle for voice - something like a radio button, options are piano and orgel (my favorite voice when I would record my own playing)

	// TODO - need it to play live / scroll through time

/**
 * Basic gui that shows the top-down view of a piano keyboard, showing how a song will play (given an .alc file) in real time.
 * 
 * @author smartel
 */
public class PianoFeigner extends JFrame {
	
	PianoProperties properties;
	
	public static void main(String[] args) {
		String propertiesPath = "";
		String alcPath = "";
		MusicSheet sheet;

		if (args.length < 2) {
			System.out.println("PianoFeigner#main - Please provide a filepath to a Piano Properties file, and a file path to an .alc file. Gracefully exiting.");
		} else {
			propertiesPath = args[0];
			alcPath = args[1];
	
			PianoFeigner pf = new PianoFeigner();
			pf.properties = new PianoProperties(propertiesPath);
			
			AlcReader ar = new AlcReader();
			sheet = ar.loadAlcFile(alcPath);
			
			if (pf.properties.didLoad()) {
				System.out.println(pf.properties); // for manual inspection of properties
				pf.execute(sheet);
			} else {
				System.out.println("PianoFeigner#main - Please fix the reported errors with the properties file and execute the program again. Gracefully exiting.");
			}
		}
	}
	
	public void execute(MusicSheet sheet) {
		boolean showLetters;
		int numWhiteKeys = Integer.parseInt(properties.getSetting(Constants.SETTINGS_NUM_WHITE_KEYS));
		int numBlackKeys = Integer.parseInt(properties.getSetting(Constants.SETTINGS_NUM_BLACK_KEYS));
		String firstNote = properties.getSetting(Constants.SETTINGS_FIRST_NOTE);
		int firstOctave = Integer.parseInt(properties.getSetting(Constants.SETTINGS_FIRST_OCTAVE));
		String pianoVoice = properties.getSetting(Constants.SETTINGS_VOICE);
		LinkedList<MusicSlice> slices = sheet.getSlices();
		MusicSlice currentSlice;

		if (properties.getSetting(Constants.SETTINGS_DISPLAY_LETTERS).equalsIgnoreCase("1")) {
			showLetters = true;
			// seems like the gui hangs when set to true? can't exit out of the program by clicking the [X] exit button anymore. TODO look into..?
		} else {
			showLetters = false;
		}
		
		// TODO perhaps we should make a radio button for voice, instead of having it in the Settings file?
		// TODO and perhaps a "Start" button instead of it immediately jumping into playback when you run the application?
		//      After you hit start, we could then replace the button with a "Pause" button, so hitting it again would stop playback. When it is paused,
		//      we could then turn the button into a "Restart" button, and hitting it again could restart the playback from the beginning. just some ideas.

		
		PianoPanel pianoPanel = new PianoPanel(numWhiteKeys, numBlackKeys, firstNote, firstOctave, showLetters);
		add(pianoPanel);
		
		// TODO this is not fully implemented yet. we do not have a timer that iterates through all the slices during playback.
		//      for now, we can only display and play sound for the first slice in the collection.
		currentSlice = slices.get(0);
		
		pianoPanel.setHitNotes(currentSlice);

		// every time we repaint the piano gui with new notes, play the sounds too
		playSoundsForSlice(currentSlice, pianoVoice);
				
		// we'll have slight buffer space in the ui
		setSize(Constants.KEY_WIDTH_WHITE * (numWhiteKeys + 1), Constants.KEY_HEIGHT_WHITE + 45);
		setTitle("Piano Feigner");
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	public void playSoundsForSlice(MusicSlice slice, String pianoVoice) {

		double compareValue = -1;
		TreeSet<MusicNote> notes = slice.getNotes();
		Iterator<MusicNote> iter = notes.iterator();
		
		while (iter.hasNext()) {
			MusicNote note = iter.next();
			compareValue = note.getCompareValue();
			System.out.println("PianoFeigner#execute - This note's compareValue is: " + compareValue); // TODO delete in the future, just testing the right .wav's are playing.
			if (compareValue > 0) {
				try {
					String uri = NoteUtils.getSoundWavForNote(compareValue, pianoVoice, properties);
					URL url = getClass().getClassLoader().getResource(uri);
					AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
					Clip clip = AudioSystem.getClip();
					clip.open(audioIn);
					clip.start();
				} catch (Exception e) {
					System.out.println("PianoFeigner#execute - exception caught attempting to test playing .wav files: " + e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}
}

class PianoPanel extends JPanel {
	
	private int numWhiteKeys;
	private int numBlackKeys;
	private String firstKey;
	private int firstOctave;
	private boolean showLetters;
	private MusicSlice currentSlice;
	
	/**
	 * @param numWhiteKeys The number of white keys to display in the piano gui
	 * @param numBlackKeys The number of black keys to display in the piano gui
	 * @param firstKey The letter of the first white key that appears on the piano gui
	 * @param firstOctave The number of the octave attached to the first white key on the piano gui
	 * @param showLetters If true, will show the note letters (A-G) on the white keys
	 */
	public PianoPanel(int numWhiteKeys, int numBlackKeys, String firstKey, int firstOctave, boolean showLetters) {
		this.numWhiteKeys = numWhiteKeys;
		this.numBlackKeys = numBlackKeys;
		this.firstKey = firstKey;
		this.firstOctave = firstOctave;
		this.showLetters = showLetters;
	}
	
	public void setHitNotes(MusicSlice currentSlice) {
		this.currentSlice = currentSlice;
	}
	
	public void doDrawing(Graphics g) {
		Graphics2D gra = (Graphics2D) g;
		int patternPosition;
		int currentOctave;
		int walker; // a tracked position that walks across the white piano keys from left to right, used when placing black keys
		int startX;
		int startY;
		
		if (showLetters) {
			// required for setting labels in coordinate positions, if "showLetters" is turned on
			setLayout(null);
		}
		
		// Check all inputs for valid values
		
		// Determine where you are in the pattern.
		// We'll map the letters to numbers ie A=1, B=2, ... G=7, and use conditionals to know when to skip.
		patternPosition = NoteUtils.getPositionForNote(firstKey);
		if (patternPosition == -1) {
			System.out.println("PianoFeigner.PianoPanel#doDrawing - invalid first note of piano given. firstKey: " + firstKey);
		}
		
		// ensure the supplied octave is valid
		if (firstOctave <= 0) {
			System.out.println("PianoFeigner.PianoPanel#doDrawing - invalid octave value given. firstOctave: " + firstOctave);
		}
		currentOctave = firstOctave;
		
		// at a minimum, ensure the number of white and black keys are at least positive
		// TODO - get some real checking on the ratio of white to black?
		// TODO actually, am I just doing this wrong from the start? Instead of passing in white and black as separate values,
		//      we already have the start key + octave, so just pass in a "total count of keys" value instead? and use that to build the white / black keys, building from the start.
		if (numWhiteKeys < 0 || numBlackKeys < 0) {
			System.out.println("PianoFeigner.PianoPanel#doDrawing - invalid white/black key counts given. Both values must be greater than 0. " +
							   "numWhiteKeys: " + numWhiteKeys + ", numBlackKeys: " + numBlackKeys);
		}
		
		startY = 0; // the top of the gui - this will never change, whether white or black keys
		
		
		
		
		// draw the white keys
		
		for (int i = 0; i < numWhiteKeys; ++i) {
			startX = Constants.KEY_WIDTH_WHITE * i;

			// Determine the compare value of this key, so we can check if it is being struck currently.
			// Get the note letter we are on by checking where we are in the ABCDEFG pattern
			String displayLetter = NoteUtils.getNoteForPosition(patternPosition);
			double currentCompVal = NoteUtils.generateCompareValue(displayLetter, currentOctave, false, false);

			// we'll fill in the color for the key, and then draw a rectangle over it to give it a border
			// if this is a note that is being struck, color it with the struck-color
			if (currentSlice.containsNote(currentCompVal)) {
				gra.setColor(Constants.KEY_COLOR_HIT);
				gra.fillRect(startX, startY, Constants.KEY_WIDTH_WHITE, Constants.KEY_HEIGHT_WHITE);
			} else {
				gra.setColor(Constants.KEY_COLOR_WHITE);
				gra.fillRect(startX, startY, Constants.KEY_WIDTH_WHITE, Constants.KEY_HEIGHT_WHITE);
			}
			gra.setColor(Constants.KEY_COLOR_BORDER);
			gra.drawRect(startX, startY, Constants.KEY_WIDTH_WHITE, Constants.KEY_HEIGHT_WHITE);
			
			if (showLetters) {
				JLabel letterLabel = new JLabel(displayLetter);
				add(letterLabel);
				letterLabel.setLocation(startX + Constants.LETTER_X_BUFFER, Constants.LETTER_Y_BUFFER);
				letterLabel.setSize(Constants.LETTER_WIDTH, Constants.LETTER_HEIGHT);
			}

			++patternPosition;
			if (patternPosition > Constants.OCTAVE_LENGTH) {
				patternPosition = 1;
				++currentOctave;
			}
		}
		
		
		
		
		// draw the black keys
		
		// guess we'll just drop em on top of the white ones.
		// NOTE: while natural keys are all immediate neighbors with each other, sharp/flat keys do not physically touch any other sharp/flat keys.
		// this means there are spaces between black keys. we can't just draw them all sequentially like white keys, we need to determine their placement pattern / position.
		// if we pretend that black notes fall in the exact gap between 2 white keys (as in, there is a black key that is positioned with one half in white C and in white D),
		// and we give black keys a width that is only approximately 2/3rd the width of a white key (so a white key can be surrounded by black keys on both sides,
		//																							 1/3rd the space on each side of the white key being black, when needed),
		// then we can work with a pattern of:
		// C - Black - D - Black - E - NO BLACK - F - Black - G - Black - A - Black - B - NO BLACK - (repeat)
		// which would translate into a repeating pattern of:
		// (FIRST TIME ONLY: SKIP THE POSITION 1/3 INTO THE WHITE NOTE TO INITIALIZE)
		// skip 1/3rd (for C's middle), place black key, skip 1/3rd (for D's middle), place key, skip 1/3rd for E's middle, SKIP 1/3RD  AGAIN(E has no sharp), ...
		// (init with one skip) skip place skip place skip skip skip place skip place skip place skip skip (repeat)
		
		// Init: skip 1/3rd in
		walker = 0 + (Constants.KEY_WIDTH_WHITE / 3);
		patternPosition = NoteUtils.getPositionForNote(firstKey);
		currentOctave = firstOctave;
		for (int i = 0; i < numBlackKeys; ++i) {
			// no matter what, skip over the "middle" of the white key
			walker += (Constants.KEY_WIDTH_WHITE / 3);
			
			// A,C,D,F,G have sharps, so we need a black key here, which takes up 2/3 the space of a white key, half inside this white key, and half inside the next
			if (patternPosition == (int)Constants.A_POS || patternPosition == (int)Constants.C_POS || patternPosition == (int)Constants.D_POS ||
                patternPosition == (int)Constants.F_POS || patternPosition == (int)Constants.G_POS) {
				
				// we'll fill in the color for the key
				// Determine the compare value of this key, so we can check if it is being struck currently.
				String displayLetter = NoteUtils.getNoteForPosition(patternPosition);
				double currentCompVal = NoteUtils.generateCompareValue(displayLetter, currentOctave, true, false);
				if (currentSlice.containsNote(currentCompVal)) {
					gra.setColor(Constants.KEY_COLOR_HIT);
				} else {
					gra.setColor(Constants.KEY_COLOR_BLACK);
				}
				startX = walker;
				gra.fillRect(startX, startY, Constants.KEY_WIDTH_BLACK, Constants.KEY_HEIGHT_BLACK);
				gra.setColor(Constants.KEY_COLOR_BORDER);
				gra.drawRect(startX, startY, Constants.KEY_WIDTH_BLACK, Constants.KEY_HEIGHT_BLACK);
				
				// move to the next position
				walker += Constants.KEY_WIDTH_BLACK;
			} else { //if (patternPosition == (int)Constants.B_POS || patternPosition == (int)Constants.E_POS) { // B, E do not have sharps, so no black key here, skip over the next 2/3rds
				walker += Constants.KEY_WIDTH_BLACK;
				--i;
			}
			
			// increment our pattern to the next key
			++patternPosition;
			if (patternPosition > Constants.OCTAVE_LENGTH) {
				patternPosition = 1;
				++currentOctave;
			}
			
		}
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		doDrawing(g);
	}
	
}
	

