package DataObjs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;

import Utils.Constants;
import Utils.NoteUtils;

public class PianoProperties {

	// This file will contain piano properties (such as how many keys are on the piano, what its first key is, and so on)
	// A number of settings will be determined from the properties file, but not actually set-able by the user.
	// For example, the total number of keys is used to determine how many white and black keys are on the piano.
	
	// location of the properties file
	public String propertiesPath;
	private boolean didLoad;
	
	private HashMap<String, String> settings;

	public PianoProperties(String propertiesPath) {
		
		settings = new HashMap<String, String>();
		didLoad = loadPropertiesFile(propertiesPath);
		if (didLoad) {
			determineRemainingPropertyValues();
		}
	}
	
	
	/**
	 * Attempts to load the properties file at the given path.
	 * If there are errors reading any properties, or if there are missing properties, then it will be considered an unsuccessful load.
	 * @param propertiesPath The filepath for the properties file
	 * @return true if all expected properties were valid values and read in, false otherwise
	 */
	public boolean loadPropertiesFile(String propertiesPath) {
		this.propertiesPath = propertiesPath;
		String errorMsgs = "";
		String setting = "";
		String value = "";
		int fieldCount = 0;
		boolean wasSuccessful = true;
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(propertiesPath)));
			while (br.ready()) {
				String lineContents = br.readLine();
				setting = lineContents.substring(0, lineContents.indexOf(" "));
				value = lineContents.substring(lineContents.indexOf("[")+1, lineContents.indexOf("]"));
				
				if (setting.equalsIgnoreCase(Constants.SETTINGS_TOTAL_NUM_KEYS)) {
					fieldCount++;
					int intVal = Integer.parseInt(value);
					if (intVal <= 0 || intVal > Constants.MAX_PIANO_KEYS) {
						errorMsgs += "Error with " + Constants.SETTINGS_TOTAL_NUM_KEYS + ": value is <= 0 or greater than potential max: " + Constants.MAX_PIANO_KEYS + ". Value was: " + intVal + "\n";
					} else {
						settings.put(Constants.SETTINGS_TOTAL_NUM_KEYS, value);
					}
				} else if (setting.equalsIgnoreCase(Constants.SETTINGS_FIRST_NOTE)) {
					fieldCount++;
					if (!value.equalsIgnoreCase(Constants.NOTE_A) && !value.equalsIgnoreCase(Constants.NOTE_B) && !value.equalsIgnoreCase(Constants.NOTE_C) &&
						!value.equalsIgnoreCase(Constants.NOTE_D) && !value.equalsIgnoreCase(Constants.NOTE_E) && !value.equalsIgnoreCase(Constants.NOTE_F) &&
						!value.equalsIgnoreCase(Constants.NOTE_G)) {
						errorMsgs += "Error with " + Constants.SETTINGS_FIRST_NOTE + ": value is in the range of expected letter notes A-G. Value was: " + value + "\n";
					} else {
						settings.put(Constants.SETTINGS_FIRST_NOTE, value);
					}
				} else if (setting.equalsIgnoreCase(Constants.SETTINGS_FIRST_OCTAVE)) {
					fieldCount++;
					int intVal = Integer.parseInt(value);
					if (intVal <= 0 || intVal > Constants.MAX_PIANO_KEYS) {
						errorMsgs += "Error with " + Constants.SETTINGS_FIRST_OCTAVE + ": value is <= 0 or greater than potential max: " + Constants.MAX_PIANO_OCTAVE + ". Value was: " + intVal + "\n";
					} else {
						settings.put(Constants.SETTINGS_FIRST_OCTAVE, value);
					}
				} else if (setting.equalsIgnoreCase(Constants.SETTINGS_VOICE)) {
					fieldCount++;
					if (!value.equalsIgnoreCase(Constants.VOICE_GRAND) && !value.equalsIgnoreCase(Constants.VOICE_ORGEL)) {
						errorMsgs += "Error with " + Constants.SETTINGS_VOICE + ": value is not a valid Piano voice. Value was: " + value + "\n";
					} else {
						settings.put(Constants.SETTINGS_VOICE, value);
					}
				} else if (setting.equalsIgnoreCase(Constants.SETTINGS_NUM_SLIDING_FINGERS)) {
					fieldCount++;
					int intVal = Integer.parseInt(value);
					// sliding finger implementation tbd. just ensure it isn't negative, or more than the max number of piano keys.
					// 0 is a valid value, as perhaps static fingers will be used instead of sliding.
					if (intVal < 0 || intVal > Constants.MAX_PIANO_KEYS) {
						errorMsgs += "Error with " + Constants.SETTINGS_NUM_SLIDING_FINGERS + ": value is less than 0 or greater than potential max: " + Constants.MAX_PIANO_OCTAVE + ". Value was: " + intVal + "\n";
					} else {
						settings.put(Constants.SETTINGS_NUM_SLIDING_FINGERS, value);
					}
				} else if (setting.equalsIgnoreCase(Constants.SETTINGS_NUM_STATIC_FINGERS)) {
					fieldCount++;
					int intVal = Integer.parseInt(value);
					// static finger implementation tbd. just ensure it isn't negative, or more than the max number of piano keys.
					// 0 is a valid value, as perhaps sliding fingers will be used instead of static.
					if (intVal < 0 || intVal > Constants.MAX_PIANO_KEYS) {
						errorMsgs += "Error with " + Constants.SETTINGS_NUM_STATIC_FINGERS + ": value is less than 0 or greater than potential max: " + Constants.MAX_PIANO_OCTAVE + ". Value was: " + intVal + "\n";
					} else {
						settings.put(Constants.SETTINGS_NUM_STATIC_FINGERS, value);
					}
				} else if (setting.equalsIgnoreCase(Constants.SETTINGS_DISPLAY_LETTERS)) {
					fieldCount++;
					settings.put(Constants.SETTINGS_DISPLAY_LETTERS, value);
					// We only check if it is "1" before we display letters. Any other value, whether String or integer, will result in no letters being shown.
					// So no error checking needs to be done.
				}
			}
			
			// Should we warn if 0 static AND 0 slider fingers? well, if only the piano feigner is being used, then it would be fine. No warning or error.
			
			// ensure the properties file actually contained all settings
			if (fieldCount != Constants.SETTINGS_EXPECTED_COUNT) {
				errorMsgs += "Properties#loadPropertiesFile - error - the properties file does not have all required settings. Fields counted: " + fieldCount +
						     ". Expected # of fields: " + Constants.SETTINGS_EXPECTED_COUNT + "\n";
				wasSuccessful = false;
			}
			
			if (!errorMsgs.isEmpty()) {
				System.out.println("Properties#loadPropertiesFile - error - the following errors were encountered while processing the Settings file:\n" + errorMsgs);
				wasSuccessful = false;
			}
			
			br.close();
		} catch (NumberFormatException e) {
			System.out.println("Properties#loadPropertiesFile - error - a number value is needed for the following setting:\n" + setting + "\nValue was: " + value);
			wasSuccessful = false;
		} catch (FileNotFoundException e) {
			System.out.println("Properties#loadPropertiesFile - error - the properties file could not be found at: " + propertiesPath);
			wasSuccessful = false;
		} catch (Exception e) {
			System.out.println("Properties#loadPropertiesFile - error - unknown error reading in settings file. Exception was: " + e.getMessage());
			wasSuccessful = false;
		}
		
		return wasSuccessful;
	}
	
	/**
	 * Determines the remaining property values, determined by analyzing the provided values
	 */
	public void determineRemainingPropertyValues() {
		double minCompVal;
		int numWhiteKeys;
		int numBlackKeys;
		int currOctave;
		double prevCompVal;
		double currCompVal;
		int notePositionInOctave;
		
		// it should not be possible for this method to fail, as the input variables were already checked for validity in loadPropertiesFile,
		// thus no "success" boolean needs to be returned.
		
		// determine min compare (should always be a whole integer, since it won't be a sharp or flat)
		minCompVal = NoteUtils.generateCompareValue(settings.get(Constants.SETTINGS_FIRST_NOTE), Integer.parseInt(settings.get(Constants.SETTINGS_FIRST_OCTAVE)), false, false);
		settings.put(Constants.SETTINGS_MIN_COMP_VALUE, ((int)(minCompVal)) + "");
		
		// for determining the last note on the piano
		notePositionInOctave = NoteUtils.getPositionForNote(settings.get(Constants.SETTINGS_FIRST_NOTE));
		
		// we have a total key count
		// use it to determine how many white keys there are.
		// a full octave has 12 keys: 7 are white, 5 are black. odds are the piano does not start at the beginning of an octave.
		// honestly, it's lazy but intuitive, we could just "walk" from this key for (total number of keys) steps, and record the key totals and octaves that way.
		// We'll initialize it taking into account the first key - we know the first key is always a white key, and we have the letter note stored already.
		numWhiteKeys = 1;
		numBlackKeys = 0;
		currOctave = Integer.parseInt(settings.get(Constants.SETTINGS_FIRST_OCTAVE));
		currCompVal = minCompVal;
		for (int x = 1; x < Integer.parseInt(settings.get(Constants.SETTINGS_TOTAL_NUM_KEYS)); ++x) { // starting at 1 since we already did the "first note"
			prevCompVal = currCompVal;
			
			if (prevCompVal % 7.0 == 0) { // if the last note was at the end of the octave, we increment our octave count by 1 since our current note is in the next one
				++currOctave;
				notePositionInOctave = 1;
			} else if (prevCompVal % 1.0 == 0) {
				++notePositionInOctave;
			}
			
			// simple way to tell if we're on a white or black key is to see if we're a whole integer or not. sharps / flats are represented with a 0.5 compare value modification.
			currCompVal = NoteUtils.getNextNoteCV(currCompVal);
			if (currCompVal % 1.0 == 0.5) {
				++numBlackKeys;
			} else {
				++numWhiteKeys;
			}
		}
		
		// determine max compare (the last note from the "walking" for-loop, should be a whole integer since it won't be sharp or flat)
		settings.put(Constants.SETTINGS_MAX_COMP_VALUE, ((int)(currCompVal))+"");
		
		// determine num white keys
		settings.put(Constants.SETTINGS_NUM_WHITE_KEYS, numWhiteKeys+"");
		
		// determine num black keys
		settings.put(Constants.SETTINGS_NUM_BLACK_KEYS, numBlackKeys+"");
		
		// determine last octave
		settings.put(Constants.SETTINGS_LAST_OCTAVE, currOctave+"");
		
		// determine last note
		settings.put(Constants.SETTINGS_LAST_NOTE, NoteUtils.getNoteForPosition(notePositionInOctave));
	}
	
	// getters
	
	public String getSetting(String settingName) {
		return settings.get(settingName);
	}
	
	public String getSettingsPath() {
		return propertiesPath;
	}
	
	// Create a string containing all of the properties and their values, for manual inspection
	public String toString() {
		String data = "";
		
		data += "Passed-in property values:\n";
		data += Constants.SETTINGS_TOTAL_NUM_KEYS + ": " + settings.get(Constants.SETTINGS_TOTAL_NUM_KEYS) + "\n";
		data += Constants.SETTINGS_FIRST_NOTE + ": " + settings.get(Constants.SETTINGS_FIRST_NOTE) + "\n";
		data += Constants.SETTINGS_FIRST_OCTAVE + ": " + settings.get(Constants.SETTINGS_FIRST_OCTAVE) + "\n";
		data += Constants.SETTINGS_VOICE + ": " + settings.get(Constants.SETTINGS_VOICE) + "\n";
		data += Constants.SETTINGS_NUM_SLIDING_FINGERS + ": " + settings.get(Constants.SETTINGS_NUM_SLIDING_FINGERS) + "\n";
		data += Constants.SETTINGS_NUM_STATIC_FINGERS + ": " + settings.get(Constants.SETTINGS_NUM_STATIC_FINGERS) + "\n";
		data += Constants.SETTINGS_DISPLAY_LETTERS + ": " + settings.get(Constants.SETTINGS_DISPLAY_LETTERS) + "\n";
		data += "Generated property values:\n";
		data += Constants.SETTINGS_MIN_COMP_VALUE + ": " + settings.get(Constants.SETTINGS_MIN_COMP_VALUE) + "\n";
		data += Constants.SETTINGS_MAX_COMP_VALUE + ": " + settings.get(Constants.SETTINGS_MAX_COMP_VALUE) + "\n";
		data += Constants.SETTINGS_NUM_WHITE_KEYS + ": " + settings.get(Constants.SETTINGS_NUM_WHITE_KEYS) + "\n";
		data += Constants.SETTINGS_NUM_BLACK_KEYS + ": " + settings.get(Constants.SETTINGS_NUM_BLACK_KEYS) + "\n";
		data += Constants.SETTINGS_LAST_NOTE + ": " + settings.get(Constants.SETTINGS_LAST_NOTE) + "\n";
		data += Constants.SETTINGS_LAST_OCTAVE + ": " + settings.get(Constants.SETTINGS_LAST_OCTAVE) + "\n";

		return data;
	}
	
	/**
	 * @return true if the properties file was successfully loaded without issue
	 */
	public boolean didLoad() {
		return didLoad;
	}
}
