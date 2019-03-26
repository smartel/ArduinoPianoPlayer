package Translators;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Collections;
import java.util.LinkedList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import Utils.NoteUtils;

/**
 * Code to translate a musicxml file into my format (.alc)
 * 
 * @author smartel
 */
public class TransMusicXML {

	public TransMusicXML() {
	}
	
	// wrapper around fields relating to the Attribute node and some of its child nodes
	public class AttributeNode {
		private int beats;
		private int beatType;
		private int divisions;
		
		public AttributeNode() {}
		
		public int getBeats() {
			return beats;
		}
		public int getBeatType() {
			return beatType;
		}
		public int getDivisions() {
			return divisions;
		}
		
		public void setBeats(int val) {
			beats = val;
		}
		public void setBeatType(int val) {
			beatType = val;
		}
		public void setDivisions(int val) {
			divisions = val;
		}

		public String toString() {
			String data;
			data = "AttributeNode details - beats: " + beats + ", beat_type: " + beatType + ", divisions: " + divisions;
			return data;
		}
	}
	
	// wrapper around fields relating to the Note node
	public class NoteNode implements Comparable<NoteNode> {
		private double alter; // -1 = flat, 1 = sharp, and decimal values are microtones, so we can't store it in an integer
		private int duration; // the duration this note is played for
		private int measure; // informational only, to confirm we are reading the xml and "backing up" correctly, don't anticipate needing this for converting to time slices
		private int octave;
		private int startTime; // when the note should be played in the song (based on a rolling duration value)
		private String step;
		private boolean isRest; // if true, indicates this is a rest, and will not have an octave, step, or alter indicator
		private boolean isChord; // if true, indicates this note is to be played with the previous note(s)
		
		public NoteNode() {
			isRest = false;
		}
		
		// NOTE: If the note is a "rest", it will have a duration (defining how long the rest is), but it will otherwise have a 'null' step, octave 0, alter 0.
		// This is because a rest can't have a pitch.

		public double getAlter() {
			return alter;
		}
		public int getDuration() {
			return duration;
		}
		public int getMeasure() {
			return measure;
		}
		public int getOctave() {
			return octave;
		}
		public int getStartTime() {
			return startTime;
		}
		public String getStep() {
			return step;
		}
		public boolean isChord() {
			return isChord;
		}
		public boolean isRest() {
			return isRest;
		}
		
		public void setAlter(double val) {
			alter = val;
		}
		public void setDuration(int val) {
			duration = val;
		}
		public void setIsChord(boolean val) {
			isChord = val;
		}
		public void setIsRest(boolean val) {
			isRest = val;
		}
		public void setMeasure(int val) {
			measure = val;
		}
		public void setOctave(int val) {
			octave = val;
		}
		public void setStartTime(int val) {
			startTime = val;
		}
		public void setStep(String val) {
			step = val;
		}
		
		public String toString() {
			String data = "NoteNode details - ";

			// full raw data
			//data += "start time: " + startTime + ", step: " + step + ", octave: " + octave + ", duration: " + duration + ", alter: " + alter + ", isRest: " + isRest + ", isChord: " + isChord;
			
			// conditional to display less data based on whether it is a note or a rest
			if (isRest) { // if it is a rest, we can exclude step, octave, and alter
				data  += "rest note, start time: " + startTime + ", duration: " + duration + ", measure: " + measure;
			} else {
				data += "start time: " + startTime + ", step: " + step + ", octave: " + octave + ", duration: " + duration + ", measure: " + measure + ", alter: " + alter + ", isChord: " + isChord;
			}
			
			return data;
		}

		public int compareTo(NoteNode other) {
			int returnVal = 0;

			// we will compare on a few different tiers:
			// if the notes have the same start time, then we'll effectively try to go musically-alphabetically / left-to-right across the piano surface.
			// we'll compare octave, and if they're the same, then we compare note (step), and if those are the same, then we'll compare alter.
			if (startTime == other.getStartTime()) {
				if (octave == other.getOctave()) {
					if (step == other.getStep()) {
						returnVal = Double.compare(alter, other.getAlter());
					} else {
						returnVal = step.compareTo(other.getStep());
					}
				} else {
					returnVal = Integer.compare(octave, other.getOctave());
				}
			} else {
				returnVal = Integer.compare(startTime, other.getStartTime());
			}
			
			return returnVal;
		}
	}
	
	public class MusicXMLHandler extends DefaultHandler {
		
		AttributeNode attrNode = null;
		NoteNode currentNote = null;
		// This collection will hold all notes as they are read from the xml, with the intention of sorting them into their playing-order once it contains all notes.
		LinkedList<NoteNode> notes = new LinkedList<NoteNode>();
		StringBuilder data = null;
		
		// attribute fields:
		private boolean hasDivisions = false;
		private boolean hasBeats = false;
		private boolean hasBeatType = false;
		
		// pitch fields:
		private boolean hasStep = false;
		private boolean hasAlter = false;
		private boolean hasOctave = false;
		// note fields:
		private boolean hasDuration = false;
		private boolean hasRest = false;
		private boolean hasChord = false;
		
		// misc / rolling counter fields:
		private int currentMeasure = -1; // purely for debugging / confirming we are reading the xml properly, we likely won't need this value for converting to time slices
		private int rollingDuration = 0;
		private int previousStartTime = 0;
		private boolean hasPart = false; // if we are at a new part, we need to skip to the beginning of the song
		
		public InputSource resolveEntity(java.lang.String publicId,
                java.lang.String systemId)
         throws java.io.IOException,
                SAXException {
			return null;
		}
		
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			
			// initialize containers
			data = new StringBuilder();
			if (attrNode == null) {
				attrNode = new AttributeNode();
			}
			if (notes == null) {
				notes = new LinkedList<NoteNode>();
			}
			
			// if we're on a new Note node, then create a new object to store its values.
			// if there was a previous NoteNode, it will already have been inserted into the linkedlist by now.
			if (qName.equalsIgnoreCase("Note")) {
				currentNote = new NoteNode();
			}
			
			// Attribute elements we want
			if (qName.equalsIgnoreCase("divisions")) {
				hasDivisions = true;
			}
			if (qName.equalsIgnoreCase("beats")) { // actually in <attribute><time>
				hasBeats = true;
			}
			if (qName.equalsIgnoreCase("beat-type")) { // actually in <attribute><time>
				hasBeatType = true;
			}
			
			// Pitch elements we want
			if (qName.equalsIgnoreCase("step")) {
				hasStep = true;
			}
			if (qName.equalsIgnoreCase("alter")) {
				hasAlter = true;
			}
			if (qName.equalsIgnoreCase("octave")) {
				hasOctave = true;
			}
			// Note elements we want
			if (qName.equalsIgnoreCase("duration")) {
				hasDuration = true;
			}
			if (qName.equalsIgnoreCase("rest")) {
				hasRest = true;
			}
			if (qName.equalsIgnoreCase("chord")) {
				hasChord = true;
			}
			if (qName.equalsIgnoreCase("part")) {
				hasPart = true;
			}
			
			
			// misc / rolling counters
			if (qName.equalsIgnoreCase("measure")) {
				// the measure we are currently on is stored in the "number" attribute.
				// measures wrap around many notes - it is not a 1:1 ratio
				// we only need to store the last measure number we've seen, and use it for all notes it encapsulates.
				// when we get to a new measure, the current value will be overwritten with its number.
				for (int x = 0; x < attributes.getLength(); ++x) {
					String attrName = attributes.getQName(x);
					if (attrName.equalsIgnoreCase("number")) {
						currentMeasure = Integer.parseInt(attributes.getValue(x));
					}
				}
			}
			// see endElement's explanation in the hasDuration conditional for how "backup" is handled
			// (the issue is, 2 separate nodes both share the same name "Duration" - one in <Note> and one in <Backup>
		}
		
		public void endElement(String uri, String localName, String qName) {
			
			if (hasDivisions) {
				attrNode.setDivisions(Integer.parseInt(data.toString()));
				hasDivisions = false;
			} else if (hasBeats) {
				attrNode.setBeats(Integer.parseInt(data.toString()));
				hasBeats = false;
			} else if (hasBeatType) {
				attrNode.setBeatType(Integer.parseInt(data.toString()));
				hasBeatType = false;
			} else if (hasStep) {
				currentNote.setStep(data.toString());
				hasStep = false;
			} else if (hasAlter) {
				currentNote.setAlter(Double.parseDouble(data.toString()));
				hasAlter = false;
			} else if (hasOctave) {
				currentNote.setOctave(Integer.parseInt(data.toString()));
				hasOctave = false;
			} else if (hasDuration) {

				// this is very important - there are 2 different nodes that both have the same name, "duration".
				// one is within <Note> and denotes the length of time the note should be hit for.
				// the other is within <Backup> and denotes how much the xml should be "rewound" to place notes
				//  behind our current location within the xml.
				// since they both have the same name ("duration"), we will need to know whether it is the first instance
				//  of seeing "duration" (which is within a note), or the 2nd instance (which is within backup).
				// the easiest way to tell will be: does the note already have a non-0 duration?
				// if it does, then we know to instead rewind, rather than overwrite the currentnote's already-set duration.
				if (currentNote.getDuration() == 0) {
					currentNote.setDuration(Integer.parseInt(data.toString()));
				} else {
					rollingDuration -= Integer.parseInt(data.toString()); // this rewinds our location within the xml sheet music to an earlier point
				}
				hasDuration = false;
				
			} else if (hasRest) {
				currentNote.setIsRest(true);
				hasRest = false;
			} else if (hasChord) {
				currentNote.setIsChord(true);
				hasChord = false;
			}
			else if (hasPart) {
				rollingDuration = 0;
				hasPart = false;
			}
			
			// if we have finished processing a Note node, then insert it into the collection of NoteNodes.
			// there are a few complicated rules around the note's start duration, and the rolling duration.
			// 1. store the "current" start time as the "previous" start time
			// 2. update the rolling duration (aka our current time location within the song) by the current note's duration only if it isn't part of a chord.
			//    2a. if the note IS part of a chord, then its duration is already accounted for in the rolling duration. HOWEVER, since it is part of a chord with the previous note,
			//        we still need it to start at the same time as the previous note, so set its start time to the "previous" start time instead. 
			// TODO - is it possible for an individual note within a chord in musicxml to have a duration longer than any other note in the chord?
			//        If so, do we need to find the maximum duration?
			// TODO if a new note is added afterwards that is not part of this chord, but lies in a position between the previous note's start time but also before its duration ends,
			//       then will the xml account for it by "backing up" to correctly set the right start location?
			if (qName.equalsIgnoreCase("note")) {
				if (currentNote.isChord) {
					currentNote.setStartTime(previousStartTime);
				} else {
					currentNote.setStartTime(rollingDuration);
					previousStartTime = rollingDuration;
					rollingDuration += currentNote.getDuration();
				}

				// measures are expected to be wrapped around many notes, so just grab the latest measure value we stored
				currentNote.setMeasure(currentMeasure);
				
				notes.add(currentNote);
			}
		}
		
		public void characters(char ch[], int start, int length) throws SAXException {
			data.append(new String(ch, start, length));
		}
		
		public AttributeNode getAttributeNode() {
			return attrNode;
		}
		
		public LinkedList<NoteNode> getNotes() {
			return notes;
		}
	}
	
	/**
	 * Attempts to translate the provided xml / musicxml file (xmlFilePath) into an Alchemized Music Data File, stored at (alcFilePath)
	 * @param xmlFilePath filepath for the xml / musicxml file to translate
	 * @param alcFilePath filepath to write the output .alc file to
	 * @param bpmMultiplier value to multiply the start times and note durations, in milliseconds. May take some experimentation adjusting it for individual songs to get it to sound right.
	 * @return true if successfully translated and output an .alc file, false otherwise. if it translated but failed to output an .alc file (such as due to file permissions),
	 * 		        it will still return false, but the valid contents of the .alc file may be available in the standard output terminal.
	 */
	public boolean parseMusicXMLFile(String xmlFilePath, String alcFilePath, int bpmMultiplier) {
		boolean isSuccessful = false;
		AttributeNode attrNode = null;
		LinkedList<NoteNode> notes = null;
		int previousStartTime;
		int currentStartTime;
		int startTimeInMs;
		int durationInMs;
		double compValue;
		
		// the contents of the .alc file. The first line is purely informational and mentions which file was translated.
		// The rest of the contents are generated after translation has completed, during printing of the song details for manual review.
		String alcContent = "Alchemized Music Data File generated from translation of the following .musicxml file: [" + xmlFilePath + "] with bpmMultiplier of: [" + bpmMultiplier + "]\n";
	
		try {
			
			// Opting to use a SAX parser since it'll be faster and more efficient than DOM. We only need to read the xml, not write anything.
			SAXParserFactory saxFactory = SAXParserFactory.newInstance();
			SAXParser saxParser = saxFactory.newSAXParser();
			MusicXMLHandler musHandler = new MusicXMLHandler();
			
			
			// If we see a new part in the musicxml, we roll the rollingDuration (our location within the song) back to the very beginning and start merging those notes in.
			// If there are an excessive number of parts in the song, such as voices for drums, bass, ... you may want to forcibly remove those undesired voices from the xml.
			// This would be referring to the:  <score-part id=blah> and <part-name> elements, and then finding the matching <part id=blah> node
			
			// Will likely need to care about:
			// <attributes>
			//    <time>
			//       <beats> int </beats> - numerator of the time signature
			//       <beat-type> int </beat-type> - denominator of the time signature
			//    </time>
			
			//    <divisions> int </divisions> - this is the number of divisions per quarter note
			//    for example, if divisions is set to 1 (meaning 1 division per quarter note), then a whole note (4 quarters) would be represented with a duration of 4 in the pitch
			// 
			// we won't care about the key at all, because all musicxml pitches with a sharp / flat will have an alter value.
			// </attributes>
			
			// Notes will have either a pitch (with the step, octave, alter), OR it will be a rest, which excludes a pitch node.
			// PITCH EXAMPLE:
			// <note>
			//    <pitch>
			//       <step> String </step>      (A-G)
			//       <alter> int </alter>   OPTIONAL FIELD. if flat or sharp, will be noted here. -1 = flat, 1 = sharp, decimal values like 0.5 are used for microtones
			//       <octave int </octave>  which octave the pitch is in
			//    </pitch>
			//
			//    <duration> int </duration>
			//
			//    additionally, if there is a <chord/> element, then that means this note should be played simultaneously with the PREVIOUS note.
			//    their standard dictates that the first note of a chord does not have a <chord/> indicator, only all following notes in the chord,
			//    so I'll represent it as such in the NoteNode, but it will all be in the same "time slice" when it gets to my .alc file.
			//
			// REST EXAMPLE:
			// <note>
			//    <rest/>
			//    <duration> int </duration>
			//    <type> String </type>
			//
			// Do we care about <Type> in either case ? I think it might just be informational / for display purposes, ie "eighth", "16th", "whole", "quarter", etc.
			// We have the duration and divisions/time signature already to determine length
			
			// There is a BACKUP element. This causes the sheet music to roll back in time.
			// This is typically used after finishing the measure in one clef (such as placing all the treble clef notes) - it will then back up to the start of the measure,
			// so it can place the bass clef notes.
			// We keep a rolling "start duration" value while creating note nodes. This way, when we see to backup x duration, we can simply subtract that from the running total,
			//   and new notes (for the 2nd clef / staff / whichever) will have their correct "start duration."
			//      The collection is later sorted by "start duration.", then octave, step, etc.
			//         This also results in the addition of more chords (technically, since at the same start time) that don't rely on the <chord/> element, because they were on different staves.
			//             That is, we could have a chord on the treble clef of 4 notes, while also playing 2 notes on the bass clef, but in musicxml only 4 of those 6 notes are marked w/ <chord/>
			// Additionally, notes that are chords will have special rules not to increment the rolling duration with an already-account-for duration from the first note,
			//   nor will it use the rolling duration already incremented by the first note in the chord (it will instead use the "previous" rolling duration)
			
			// <part>
			// Every time we see a new part, it is a new instrument's notes. Reset the position back to the beginning.
			
			// TODO it looks like there is a Repeat bar element we need to handle.
			//      note - it would be possible to edit out by hand / just duplicate the notes in an absolute worst case scenario.
			//      but try to find a song with a repeat section if you can, so we can implement it.
			
			// We end up with one attribute object and a collection of note objects
			saxParser.parse(xmlFilePath, musHandler);
			attrNode = musHandler.getAttributeNode();
			notes = musHandler.getNotes();
			Collections.sort(notes);

			// Display for manual review while also creating a string representation of our .alc format
			System.out.println(attrNode + "\n");
			
			// get the count of how many notes are rests which don't need to be written to the .alc file, so we can adjust the note-count appropriately.
			int numRests = 0;
			for (int x = 0; x < notes.size(); ++x) {
				if (notes.get(x).isRest) {
					++numRests;
				}
			}
			
			// add in the header line containing the total note count (minus rests), which is used to check the integrity of the .alc file later on when the notes are read in
			alcContent += (notes.size() - numRests) + "\n";
			
			currentStartTime = 0;
			for (int x = 0; x < notes.size(); ++x) {
				NoteNode note = notes.get(x);
				
				previousStartTime = currentStartTime;
				currentStartTime = note.getStartTime();
				// extra newlines for readability between different start times
				if (previousStartTime != currentStartTime) {
					System.out.println("");
				}
				
				// perform conversion of the note's duration and start time values into milliseconds, based on the provided multiplier.
				startTimeInMs = note.getStartTime() * bpmMultiplier;
				durationInMs = note.getDuration() * bpmMultiplier;
				
				// No rest notes need to be written to the .alc file, since there are no actions that robotic fingers need to take to play rests.
				if (!note.isRest) {
					// We need to handle musicxml's microtones. Our .alc file only allows for sharps and flats (1 and -1 respectively).
					// So, if we have a microtone (some value between 0 and 1, or 0 and -1), I guess we'll have to decide whether it is closer to 0 or closer to +-1. Round it.
					double currentAlter = note.getAlter();
					if (currentAlter == 1 || currentAlter >= 0.5) { // treat as a sharp
						note.setAlter(1);
					} else if (currentAlter == -1 || currentAlter <= -0.5) { // treat as a flat
						note.setAlter(-1);
					} else { // 0 or close enough to round to 0, so a neutral note
						note.setAlter(0);
					}
					
					compValue = NoteUtils.generateCompareValue(note.getStep(), note.getOctave(), note.getAlter() == 1 ? true : false,
						                                                                        note.getAlter() == -1 ? true : false);

					alcContent += startTimeInMs + " " + compValue + " " + durationInMs + "\n";				
					System.out.println(note);
				} else {
					System.out.println("Skipping rest note, no action required by gui or arduino: " + note);
				}

			}

			isSuccessful = true;
			
			// display the full contents of the .alc file, as well as export it to the desired filepath
			System.out.println("Full contents of the .alc string:\n-------------\n" + alcContent);			
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(new File(alcFilePath)));
				bw.write(alcContent);
				bw.close();
				
			} catch (Exception e) {
				System.out.println("[TransMusicXML#parseMusicXMLFile] Exception caught while attempting to write the .alc file to disk at path [ " + alcFilePath + " ]: " + e.getMessage());
				e.printStackTrace();
				isSuccessful = false;
			}
			
		} catch (Exception e) {
			System.out.println("Exception caught attempting to translate MusicXML file: " + xmlFilePath + "\r\nStack trace:\r\n");
			e.printStackTrace();
			isSuccessful = false; // ensuring we return false
		}
		
		return isSuccessful;
	}
	
	
	
}
