package Translators;

import java.util.LinkedList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

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
	public class NoteNode {
		private double alter; // -1 = flat, 1 = sharp, and decimal values are microtones, so we can't store it in an integer
		private int duration;
		private int octave;
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
		public void setOctave(int val) {
			octave = val;
		}
		public void setStep(String val) {
			step = val;
		}
		
		public String toString() {
			String data = "NoteNode details - ";

			// full raw data
			//data += "step: " + step + ", octave: " + octave + ", duration: " + duration + ", alter: " + alter + ", isRest: " + isRest + ", isChord: " + isChord;
			
			// conditional to display less data based on whether it is a note or a rest
			if (isRest) { // if it is a rest, we can exclude step, octave, and alter
				data  += "rest note, duration: " + duration;
			} else {
				data += "step: " + step + ", octave: " + octave + ", duration: " + duration + ", alter: " + alter + ", isChord: " + isChord;
			}
			
			// additional spacing to more easily separate chords from not-chords in the logging
			if (!isChord) {
				data = "\r\n" + data;
			}
			
			return data;
		}
	}
	
	public class MusicXMLHandler extends DefaultHandler {
		
		AttributeNode attrNode = null;
		NoteNode currentNote = null;
		LinkedList<NoteNode> noteNodes = null;
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
		private boolean hasBackup = false;
		private boolean hasMeasure = false;
		
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
			if (noteNodes == null) {
				noteNodes = new LinkedList<NoteNode>();
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
			
			// TODO misc - rolling counters? TBD
			if (qName.equalsIgnoreCase("measure")) {
				hasMeasure = true;
			}
			if (qName.equalsIgnoreCase("backup")) {
				hasBackup = true;
			}
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
				currentNote.setDuration(Integer.parseInt(data.toString()));
				hasDuration = false;
			} else if (hasRest) {
				currentNote.setIsRest(true);
				hasRest = false;
			} else if (hasChord) {
				currentNote.setIsChord(true);
				hasChord = false;
			} else if (hasMeasure) {
				// TODO tbd
			} else if (hasBackup) {
				// TODO tbd
			}
			
			
			// if we have finished processing a Note node, then insert it into the collection of NoteNodes
			if (qName.equalsIgnoreCase("note")) {
				noteNodes.add(currentNote);
			}
		}
		
		public void characters(char ch[], int start, int length) throws SAXException {
			data.append(new String(ch, start, length));
		}
		
		public AttributeNode getAttributeNode() {
			return attrNode;
		}
		
		public LinkedList<NoteNode> getNoteNodes() {
			return noteNodes;
		}
	}
	
	public boolean parseMusicXMLFile(String xmlFilePath) {
		boolean isSuccessful = false;
		AttributeNode attrNode = null;
		LinkedList<NoteNode> noteNodes = null;
	
		try {
			
			// Opting to use a SAX parser since it'll be faster and more efficient than DOM. We only need to read the xml, not write anything.
			SAXParserFactory saxFactory = SAXParserFactory.newInstance();
			SAXParser saxParser = saxFactory.newSAXParser();
			MusicXMLHandler musHandler = new MusicXMLHandler();
			
			
			// TODO how do we want to handle different voices? As in, if the musicxml contains multiple instruments / parts.
			// Short term I've been removing other voices that aren't needed / aren't piano. I guess the alternative options are:
			//  1. merging them all together into one piano file,
			//  2. indicating which voice(s) you want as an optional input parameter,
			//  3. breaking them into individual exports, one per voice
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
			
			// TODO:
			// CRITICAL:
			// There is a BACKUP element. This causes the sheet music to roll back in time.
			// This is typically used after finishing the measure in one clef (such as placing all the treble clef notes) - it will then back up to the start of the measure,
			// so it can place the bass clef notes.
			// My original implementation did not account for this when creating a linear linkedlist in the order of reading notes on simple (1 clef) songs.
			// Proposed ways to handle it:
			// Keep a rolling "start duration" value while creating note nodes. This way, when we see to backup x duration, we can simply subtract that from the running total,
			//   and new notes (for the 2nd clef / staff / whichever) will have their correct "start duration."
			//      The collection can later be sorted by "start duration."
			//         This may also result in the addition of more chords that don't rely on the <chord/> element, because they were on different staves.
			//             That is, we could have a chord on the treble clef of 4 notes, while also playing 2 notes on the bass clef, but in musicxml only 4 of those 6 notes are marked w/ <chord/>
			// Alternatively, we could consider a new collection for storing notes.
			//   Perhaps the key is the "start duration", and the value is a collection of all notes to be played at that start duration.
			// Additionally, we could have a rolling counter of which measure we're all too, increment it every time you get to a new measure block.
			//    Just for more user-friendly readability in the log, since it got a little wild for a bit between jumping across staves in addition to changing clef signs within the current staff.
			
			
			
			// We end up with one attribute object and a collection of note objects
			saxParser.parse(xmlFilePath, musHandler);
			attrNode = musHandler.getAttributeNode();
			noteNodes = musHandler.getNoteNodes();
			
			System.out.println(attrNode);
			for (int x = 0; x < noteNodes.size(); ++x) {
				System.out.println(noteNodes.get(x));
			}
			
			isSuccessful = true;
		} catch (Exception e) {
			System.out.println("Exception caught attempting to translate MusicXML file: " + xmlFilePath + "\r\nStack trace:\r\n");
			e.printStackTrace();
			isSuccessful = false; // ensuring we return false
		}
		
		return isSuccessful; // TODO allow this to actually return true at some point
	}
	
	
	
}
