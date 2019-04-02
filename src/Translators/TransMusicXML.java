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
	
	/**
	 * Base for the most basic units of the musicxml - note elements and barline elements.
	 * 
	 * @author smartel
	 */
	public class Node implements Comparable<Node> {
		protected int startTime; // when the note should be played in the song (based on a rolling duration value)

		public int getStartTime() {
			return startTime;
		}
		public void setStartTime(int val) {
			startTime = val;
		}
		
		@Override
		public int compareTo(Node other) {
			return Integer.compare(this.startTime, other.startTime);
		}
	}
	
	// wrapper around fields relating to the Attribute node and some of its child nodes
	public class AttributeNode extends Node {
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
	public class NoteNode extends Node {
		private double alter; // -1 = flat, 1 = sharp, and decimal values are microtones, so we can't store it in an integer
		private int duration; // the duration this note is played for
		private int measure; // informational only, to confirm we are reading the xml and "backing up" correctly, don't anticipate needing this for converting to time slices
		private int octave;
		private String step;
		private boolean isRest; // if true, indicates this is a rest, and will not have an octave, step, or alter indicator
		private boolean isChord; // if true, indicates this note is to be played with the previous note(s)
		
		public NoteNode() {
			isRest = false;
		}
		
		// copy-constructor
		public NoteNode(NoteNode other) {
			alter = other.alter;
			duration = other.duration;
			measure = other.measure;
			octave = other.octave;
			if (other.getStep() != null) {
				step = new String(other.getStep());
			}
			isRest = other.isRest;
			isChord = other.isChord;
			startTime = other.startTime;
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

		public int compareTo(Node other) {
			if (other instanceof NoteNode) {
				int returnVal = 0;
				NoteNode otherNote = (NoteNode) other;
				
				// we will compare on a few different tiers:
				// if the notes have the same start time, then we'll effectively try to go musically-alphabetically / left-to-right across the piano surface.
				// we'll compare octave, and if they're the same, then we compare note (step), and if those are the same, then we'll compare alter.
				if (startTime == otherNote.getStartTime()) {
					if (octave == otherNote.getOctave()) {
						if (step == otherNote.getStep()) {
							returnVal = Double.compare(alter, otherNote.getAlter());
						} else {
							returnVal = step.compareTo(otherNote.getStep());
						}
					} else {
						returnVal = Integer.compare(octave, otherNote.getOctave());
					}
				} else {
					returnVal = Integer.compare(startTime, otherNote.getStartTime());
				}
				
				return returnVal;
			} else {
				return super.compareTo(other);
			}
		}
	}
	
	// wrapper around fields relating to the Barline node
	public class BarLineNode extends Node {
		private String location;
		private String repeatDir = ""; // optional, should be "forward" or "backward" if it directs the song cursor to move, that is, a "backward" tells it to jump to the previous "forward"
		private int endingNum; // optional, the number of a branching path at the end of a repeat, as in, play ending 1 first, then play ending 2 second (after repeated portions)
		private String endingType = ""; // optional, "start" and "stop" indicate the beginning and end of a branching end number portion
		
		public BarLineNode() {}
		
		public int getEndingNum() {
			return endingNum;
		}
		public String getEndingType() {
			return endingType;
		}
		public String getLocation() {
			return location;
		}
		public String getRepeatDir() {
			return repeatDir;
		}
		
		public void setEndingNum(int val) {
			endingNum = val;
		}
		public void setEndingType(String val) {
			endingType = val;
		}
		public void setLocation(String val) {
			location = val;
		}
		public void setRepeatDir(String val) {
			repeatDir = val;
		}

		public String toString() {
			String data;
			data = "BarLineNode details - start time: " + startTime + ", location: " + location + ", repeat direction: " + repeatDir + ", ending number: " + endingNum + ", ending type: " + endingType;
			return data;
		}
		
		// I don't think we need to override the compareTo. We can just compare on startTime.
		// There may be cases where bars are adjacent, such as when a right bar ends (the end of one repeating section), and then a new left bar occurs immediately after.
		// But since the start times will be equal, the insert order should remain untouched.
	}
	
	public class MusicXMLHandler extends DefaultHandler {
		
		AttributeNode attrNode = null;
		NoteNode currentNote = null;
		BarLineNode currentBar = null;
		// This collection will hold all notes as they are read from the xml, with the intention of sorting them into their playing-order once it contains all notes.
		LinkedList<Node> notes = new LinkedList<Node>();
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
		
		// flag if there were any barline notes (boolean flags aren't set because we grab all fields from attributes instead of inspecting elements
		private boolean containsRepeats = false;
		
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
				notes = new LinkedList<Node>();
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
			
			// Barline work:
			// if we're on a new BarLine node (optional notes that indicate repeat bars), create a new object to store its values.
			// Note that everything we need for barlines is stored in the xml's attributes, and not within any elements
			if (qName.equalsIgnoreCase("barline")) {
				currentBar = new BarLineNode();
				for (int x = 0; x < attributes.getLength(); ++x) {
					String attrName = attributes.getQName(x);
					if (attrName.equalsIgnoreCase("location")) {
						currentBar.setLocation(attributes.getValue(x));
					}
				}
				if (!containsRepeats) {
					containsRepeats = true;
				}
			}
			// BarLine elements we want
			if (qName.equalsIgnoreCase("repeat")) {
				for (int x = 0; x < attributes.getLength(); ++x) {
					String attrName = attributes.getQName(x);
					if (attrName.equalsIgnoreCase("direction")) {
						currentBar.setRepeatDir(attributes.getValue(x));
					}
				}
			}
			if (qName.equalsIgnoreCase("ending")) {
				for (int x = 0; x < attributes.getLength(); ++x) {
					String attrName = attributes.getQName(x);
					if (attrName.equalsIgnoreCase("number")) {
						currentBar.setEndingNum(Integer.parseInt(attributes.getValue(x)));
					} else if (attrName.equalsIgnoreCase("type")) {
						currentBar.setEndingType(attributes.getValue(x));
					}
				}
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
			
			if (qName.equalsIgnoreCase("barline")) {
				// we don't need to do a check in case the given sheet music starts with a bar - whichever we use, both previousStartTime and rollingDuration are initialized to 0.
				currentBar.setStartTime(rollingDuration);
				notes.add(currentBar);
			}
		}
		
		public void characters(char ch[], int start, int length) throws SAXException {
			data.append(new String(ch, start, length));
		}
		
		public boolean containsRepeats() {
			return containsRepeats;
		}
		
		public AttributeNode getAttributeNode() {
			return attrNode;
		}
		
		public LinkedList<Node> getNotes() {
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
		LinkedList<Node> notes = null;
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
			
			// Repeats
			// Initial thoughts:
			// Repeat elements indicate that all of the notes in a given segment should be played twice (aka, repeated)
			// It appears musicxml does not support nested repeats, nor do many applications (like Finale), although it could potentially exist in real life / other formats, like... music21?
			// Repeats can have different endings - that is, there could be a repeat that wraps around 4 measures, let's call them A,B,C and D.
				// There may be another "nested" repeat for D, with an ending number, like "ending number  1", and then it closes, and then another repeat with ending number 2.
					// This effectively unwinds to a repeat with a branching ending, that is: A B C D1 A B C D2.
			// It will be very important to pay attention for branching endings via ending number, as well as looking for "repeat direction", which will be "forward" or "backward" and directs the song cursor.
			// How we will handle repeats:
			// Since we are not expecting real "nested repeats" from musicxml, when we find a repeat with a forward direction, we can just duplicate all the created notes into a collection as we add them to the song like normal.
			// When we hit the end of the repeat and find a backward direction, we simply add all of the duplicated notes (aka the entire new collection) to the song and bam, we have it in the song 2x now.
			//    We then clear the "new collection" so we don't accidentally add them again if we find another repeat barline later.
			// If we instead hit a new barline with an ending number, then we add that ending like normal (so, per our earlier example, A B C are already in the song, and we now add D1)
			// 	!! We do NOT add any "endings" to the "new collection"
			// After adding the ending, we add everything from the "new collection" to the song, so now we have: A B C D1 A B C
			// And finally, we can read in the second ending, and we'll have A B C D1 A B C D2, and should be all set (although all notes after this will need to have their start time adjusted by the... length of the repeat?)
			// How many different endings can we have? I've only seen examples with 2 branches in musicxml. Technically, we should keep adding from the "new collection" until there are no more "endings" ?
			
			
			// We end up with one attribute object and a collection of note objects
			saxParser.parse(xmlFilePath, musHandler);
			attrNode = musHandler.getAttributeNode();
			notes = musHandler.getNotes();
			Collections.sort(notes);

			// Look for bar lines, so we can insert repeated notes into the collection before generating the .alc file and integrity count.
			// The collection returned will have all BarLine nodes excluded.
			if (musHandler.containsRepeats()) {
				notes = postProcessBarLines(notes);
			}
			
			notes = postProcessCutDuplicates(notes);
			
			// Display for manual review while also creating a string representation of our .alc format
			System.out.println(attrNode + "\n");
			
			// get the count of NoteNodes for the integrity check by just straight up counting all non-rests / non-barlines within the notes collection
			int integrityCount = 0;
			for (int x = 0; x < notes.size(); ++x) {
				if (notes.get(x) instanceof NoteNode) {
					NoteNode note = (NoteNode)notes.get(x);
					if (!note.isRest) {
						++integrityCount;
					}
				}
			}
			// add in the header line's total note count, which is used to check the integrity of the .alc file later on when the notes are read in
			alcContent += integrityCount + "\n";
			
			currentStartTime = 0;
			for (int x = 0; x < notes.size(); ++x) {
				if (notes.get(x) instanceof NoteNode) {
					NoteNode note = (NoteNode) notes.get(x);
					
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
				} else {
					// Note - it used to be possible to hit instances of BarLineNode, but it should no longer be possible.
					// If the song has any repeat blocks, then the BarLineNodes should be stripped from the collection of notes returned by postProcessBarLines()
					System.out.println("TransMusicXml#parseMusicXMLFile - Unknown node at: " + notes.get(x).startTime + "ms. Details: " + notes.get(x).toString());
				}

			}

			isSuccessful = true;
			
			// display the full contents of the .alc file, as well as export it to the desired filepath
			//System.out.println("Full contents of the .alc string:\n-------------\n" + alcContent);			
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
	
	/**
	 * Given a collection of Nodes (containing NoteNodes and BarLineNodes),
	 * determine where repeat bars start and stop, and duplicate the notes within that range and add it into a new collection of notes.
	 * The new collection is technically optional, but I think it helps with understanding how the original notes collection is expanded, and we'll also exclude barline nodes from it.
	 * Counters will need to keep track of the rolling start time adjustment, so the notes appearing after the repeated section can be accurately moved ahead within the song, timewise.
	 * 
	 * Additional work will need to see if there are different ending branches at the end of a repeat.
	 * 
	 * @param notes
	 */
	public LinkedList<Node> postProcessBarLines(LinkedList<Node> notes) {
		LinkedList<Node> expandedNotes = new LinkedList<Node>();
		LinkedList<Node> duplicateNotes = new LinkedList<Node>();
		LinkedList<Node> endingNotes = new LinkedList<Node>();
		int startTimeAdjustment = 0;
		int repeatLeftTime = 0; // the original time (pre-expansion, pre-bpm-multiplier) in the song when we hit the left side of the repeat. for calculating the adjustment applied to all future notes.
		int repeatRightTime = 0; // the original time (pre-expansion, pre-bpm-multiplier) in the song when we hit the right side of the repeat. for calculating the adjustment applied to all future notes.
		boolean inRepeat = false; // if we are currently inside of a repeat block, meaning we are currently duplicating notes
		boolean inEnding = false; // if we are in an 'ending' block
		int endingLeftTime = 0;
		int endingRightTime = 0;
		int endingGap = 0;
		
		// To make it as simple as possible:
		//    Only start duplicating if we see a repeat direction: forward in a left barline (and clear any existing contents of the collection first! this is crucial!)
		//    Only consume from the "new collection" (the duplicated notes) once we see a right barline with repeat direction: backward
		//	  Additionally, stop duplicating if you see an "ending number" attribute in a barline. You'll need to have all of the "duplicated notes" within the expanded collection once per ending.
		//    This would remove any code specializing around looking for specific / incrementing ending numbers, etc. We can have n number of endings, because we just keep inserting the "new collection" until we stop having backward directions and get a new forward direction (if there ever is one)
		
		for (int x = 0; x < notes.size(); ++x) {
			if (notes.get(x) instanceof BarLineNode) {
				BarLineNode currentBar = (BarLineNode)notes.get(x);
				System.out.println("Informational only: A repeat bar had been placed here. " + currentBar.toString() + " (NOTE: times are before bpm-multiplier adjustments)");				
				if (currentBar.getLocation().equalsIgnoreCase("LEFT") && currentBar.getRepeatDir().equalsIgnoreCase("FORWARD")) {
					// We are at the start of a repeat block! Check if it's the first repeat block with a forward direction, warn if it isn't.
					if (inRepeat) {
						// unexpected outcome..? we're already in a repeat and found another left. Don't think musicxml allows for nested repeats. Just keep the innermost one.
						System.out.println("TransMusicXML#postProcessBarLines - warning - attempt at nested repeats? Using inner repeat, scrapping outer.");						
					}
					inRepeat = true;
					duplicateNotes.clear();
					repeatLeftTime = currentBar.getStartTime();
				} else if (currentBar.getLocation().equalsIgnoreCase("RIGHT") && currentBar.getRepeatDir().equalsIgnoreCase("BACKWARD") && inRepeat && currentBar.getEndingNum() == 0) {
					// We have a closing barline to go with a successful opening barline!
					repeatRightTime = currentBar.getStartTime();
					startTimeAdjustment += (repeatRightTime - repeatLeftTime); // add the length of time from went the repeated section started and ended, to the total time adjustment for all following notes

					// add all dupes within this repeat section to the new collection along with their time adjustment
					for (int d = 0; d < duplicateNotes.size(); ++d) { // d for dupe
						NoteNode dupe = (NoteNode)duplicateNotes.get(d);
						dupe.setStartTime(dupe.getStartTime() + startTimeAdjustment);
						if (!expandedNotes.add(dupe)) {
							System.out.println("TransMusicXML#postProcessBarLines - error - failed to add duplicate note to expanded notes collection.");
							System.out.println("Failed duplicate's values: " + dupe.toString());
						}
					}
					inRepeat = false;
				}
				
				
				// Ending cases
				else if (currentBar.getLocation().equalsIgnoreCase("LEFT") && currentBar.getEndingNum() != 0) {
					inEnding = true;
					endingLeftTime = currentBar.getStartTime();
				} else if (currentBar.getLocation().equalsIgnoreCase("RIGHT") && currentBar.getEndingNum() != 0) {
					endingRightTime = currentBar.getStartTime();
					endingGap = endingRightTime - endingLeftTime; // the gap in time between the ending barlines
					
					// when the duplicate notes collection is built, it is from a repeat block.
					// unfortunately, this makes our work with ending blocks a bit convoluted,
					// because ending blocks are preceded by repeat blocks.
					// long story short:
					// if the ending number is 1, that means the duplicate collection is already inserted into the expanded
					// collection, and then we just plop in the current ending notes collection into expanded, and thus, we 
					// have the portion before ending 1, and then we have ending 1. so, we're all set.
					// however, for endings > 1, we need to reinsert from the duplicate collection (which is the shared portion before any endings),
					// and THEN insert the (newly repopulated) ending collection.
					
					if (currentBar.getEndingNum() > 1) {
						// since we're dumping a whole nother ton of measures in (the entire length of duplicatedNotes),
						// we need to massively add to the startTimeAdjustment.
						startTimeAdjustment += (endingLeftTime - repeatLeftTime);
						
						for (int d = 0; d < duplicateNotes.size(); ++d) {
							NoteNode dupe = new NoteNode((NoteNode)duplicateNotes.get(d));
							dupe.setStartTime(dupe.getStartTime() + startTimeAdjustment);
							if (!expandedNotes.add(dupe)) {
								System.out.println("TransMusicXML#postProcessBarLines - error - failed to add duplicate note preceding ending block to expanded notes collection.");
								System.out.println("Failed duplicate's values: " + dupe.toString());
							}
						}
						startTimeAdjustment -= endingGap; // we need to go back in time slightly, for the ending note's measure(s) to be lined up right.
					}
					
					// since this ending block is done, add everything from the endingNotes collection to the song
					for (int e = 0; e < endingNotes.size(); ++e) { // e for end
						NoteNode end = (NoteNode)endingNotes.get(e);
						end.setStartTime(end.getStartTime() + startTimeAdjustment);
						if (!expandedNotes.add(end)) {
							System.out.println("TransMusicXML#postProcessBarLines - error - failed to add ending note to expanded notes collection.");
							System.out.println("Failed duplicate's values: " + end.toString());
						}
					}
					
					endingNotes.clear();
					inEnding = false;
					endingLeftTime = 0;
					endingRightTime = 0;
					endingGap = 0;
				}
				
				
				else if (currentBar.getLocation().equalsIgnoreCase("RIGHT") && currentBar.getRepeatDir().equalsIgnoreCase("BACKWARD") && !inRepeat) {
					// a right bar line telling us to go backwards but we weren't in a repeat. take no action. may need to inspect the xml manually.
					System.out.println("TransMusicXML#postProcessBarLines - warning - found a closing right BarLine but we weren't in a repeat. Direction: " + currentBar.getRepeatDir());
					// sometimes we see musicxml ending in a right repeat with no backward direction and no left. is it technically valid?
					// decision made, not going to bother doing a repeat without a left (wouldn't it just be the whole song again?), hence the inRepeat check.
				} else {
					// May be bad / damaged xml data.
					if (currentBar.getRepeatDir().equalsIgnoreCase("BACKWARD")) {
						System.out.println("TransMusicXML#postProcessBarLines - Unexpected outcome. BarLine location: " + currentBar.getLocation() +
										   ", direction: " + currentBar.getRepeatDir() + ", inRepeat: " + inRepeat);
					}
				}
			} else { // it is a step note or a rest

				NoteNode currentNote = (NoteNode)notes.get(x);
				if (inEnding) {
					// since we are in an ending block, build a collection of all the notes to attach to the song,
					// appearing after the repeating "beginning" which is stored in duplicateNotes still.
					// Note that for ending num = 1, the beginning would've already been inserted into expanded as part of the inRepeat block's logic.
					
					endingNotes.add(new NoteNode(currentNote));
					if (!currentNote.isRest()) {
						System.out.println("Ending note created: " + NoteUtils.generateCompareValue(currentNote.getStep(), currentNote.getOctave(), currentNote.getAlter() == 1 ? true : false, currentNote.getAlter() == -1 ? true : false));
					}
				} else if (inRepeat) {
					// since we're in a repeat block, these notes are being duplicated (we're 'expanding' the repeat block).
					// one note goes into the 'expanded' collection, and the other note goes into the 'duplicate' collection,
					// which will be added to the expanded collection once we're at the end of the repeat block and know the start time adjustment to apply.
					duplicateNotes.add(new NoteNode(currentNote));
					currentNote.setStartTime(currentNote.getStartTime() + startTimeAdjustment);
					expandedNotes.add(currentNote);
				} else { // if we aren't currently in a repeat or ending block, then no 'expansion' is needed for this part. just transfer the notes to the new collection with time adjustment (if any)
					currentNote.setStartTime(currentNote.getStartTime() + startTimeAdjustment);
					expandedNotes.add(currentNote);
				}
				
			}
		}
		
		return expandedNotes;
	}
	
	/**
	 * Given a collection of Nodes, this method verifies there are no duplicate NoteNodes. If a duplicate is found, it is removed.
	 * While duplicates may not necessarily cause issues, and may be present in the source musicxml itself (as in the case causing me to write this method),
	 * I'd rather clean them up and remove them.
	 * If a NoteNode already exists that starts at the exact same timestamp, with the same properties, then it is a duplicate, don't include it.
	 * 
	 * @param notes A cleaned up collection of the passed in notes (any duplicate NoteNodes are excluded)
	 */
	public LinkedList<Node> postProcessCutDuplicates(LinkedList<Node> notes) {
		LinkedList<Node> cleanedNotes = new LinkedList<Node>();
		
		for (int x = 0; x < notes.size(); ++x) {
			NoteNode note = (NoteNode)notes.get(x);
			
			// The NoteNode compareTo wasn't working the way I'd expect when I tried checking if "cleanedNotes.contains(note)", dupes were still sneaking in.
			// Who knows, maybe we have different measure values or something, or repeats are making this harder than it might need to be.
			// Regardless, that is why there is a brute-forcey nested for-loop to ensure we don't have duplicates.
			boolean allowIn = true;
			for (int y = 0; y < cleanedNotes.size() && allowIn; ++y) {
				NoteNode cleanNote = (NoteNode)cleanedNotes.get(y);
				
				if (note.getAlter() == cleanNote.getAlter() &&
					note.getStartTime() == cleanNote.getStartTime() &&
					note.getDuration() == cleanNote.getDuration() &&
					note.getOctave() == cleanNote.getOctave()) {

					// is this a duplicate rest note?
					if (note.isRest() && cleanNote.isRest()) {
						allowIn = false;
					}
					// is this a duplicate step note?
					else if (note.getStep() != null && cleanNote.getStep() != null &&
						note.getStep().equalsIgnoreCase(cleanNote.getStep())) {					
						allowIn = false;
					}
				}
			}
			if (allowIn) {
				cleanedNotes.add(note);
			} else {
				System.out.println("TransMusicXML#postProcessCutDuplicates - info - duplicate note cleaned up: " + note);
			}
		}
		
		return cleanedNotes;
	}
	
	
}
