package Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;

import DataObjs.MusicNote;
import DataObjs.MusicSheet;
import DataObjs.MusicSlice;

public class AlcStatsUtils {
	DecimalFormat decFmt;
	MusicSlice liveSlice; // ripped from PianoFeigner and used solely for determining the max hits and holds that occur simultaneously
	
	public AlcStatsUtils() {
		decFmt = new DecimalFormat("###.##");
	}
	
	public class NoteStats implements Comparable<NoteStats> {
		private double compareValue;
		private int numTimesHit;
		private int totalDuration;
		
		public NoteStats(double compareValue) {
			numTimesHit = 0;
			totalDuration = 0;
		}
		
		public void hitNote(int duration) {
			++numTimesHit;
			totalDuration += duration;
		}
		
		public int getNumTimesHit() {
			return numTimesHit;
		}
		
		public int getTotalDuration() {
			return totalDuration;
		}

		@Override
		public int compareTo(NoteStats other) {
			return Double.compare(this.compareValue, other.compareValue);
		}
	}
	
	// Given a MusicSheet (loaded from an Alc file, my personal format), determine various statistics, like the range of notes that are hit and how many simultaneous fingers would be required to play the song.
	
	/**
	 * Wrapper around getFullStats that writes its results to the given output path
	 * @param sheet
	 * @param displayNonHitNotes
	 * @param outputPath
	 * @return boolean true if successfully wrote the stats file to the given output path, false otherwise
	 */
	public boolean writeFullStatsToFile(MusicSheet sheet, boolean displayNonHitNotes, String outputPath) {
		boolean wasSuccessful = true;
		
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(outputPath)));
			bw.write(getFullStats(sheet, displayNonHitNotes));
			bw.close();
		} catch (Exception e) {
			System.out.println("AlcStatsUtils#writeFullStatsToFile - Exception caught trying to write stats output file to: " + outputPath + ".");
			e.printStackTrace();
			wasSuccessful = false;
		}
		
		return wasSuccessful;
	}
	
	/**
	 * Given a MusicSheet, will returning a String containing the full detailed statistics for it
	 * @param sheet MusicSheet to generate stats for
	 * @param displayNonHitNotes if true, then stats will be printed out for compareValues that were never hit
	 * @return A string containing all generated stats for the MusicSheet
	 */
	public String getFullStats(MusicSheet sheet, boolean displayNonHitNotes) {
		String results = "";
		String noteResults = ""; // we build the notes' stats out in a separate string, that is appended to the headed (results) after we've processed all of the notes.
								 // this is because we need time to count up total unique compare value hits and so on
		int totalUniqueHits = 0;
		int totalUniqueNaturalsHit = 0;
		int totalUniqueSharpsHit = 0;
		double lowestCompValHit = -1;
		double highestCompValHit = -1;
		
		results += sheet.getInfoLine() + "\n";
		results += "Note count: " + sheet.getNoteCount() + "\n"; // we don't need to total the NoteStats' getNumTimesHit() because it should equal this value already
		results += "Runtime: " + sheet.getEndTime() + "ms\n";
		results += "Time interval length (GCD): " + sheet.getGCD() + "ms\n";
		results += "Max simultaneous note hits: " + getMaxSimulHits(sheet) + "\n";
		results += "Max simultaneous note hits and holds: " + getMaxSimulHitsAndHolds(sheet) + "\n";
		
		// Note: this for-loop explicitly skips compareValue 0, as there should not be any rest notes in an .alc file as robotic fingers can't take any action to hit them.
		HashMap<Double, NoteStats> compValStats = generateCompValStats(sheet);
		for (double x = Constants.MIN_THEORETICAL_COMPARE_VALUE; x <= Constants.MAX_THEORETICAL_COMPARE_VALUE; x = NoteUtils.getNextNoteCV(x)) {
			NoteStats stats = compValStats.get(x);
			
			if (stats.getNumTimesHit() != 0) {
				++totalUniqueHits;
				if (lowestCompValHit == -1) {
					lowestCompValHit = x;
				}
				if (x > highestCompValHit) {
					highestCompValHit = x;
				}
			}
			
			// if the note was hit, OR if the note was never hit but the flag is set to display non-hit notes anyway, then display stats for the note
			if (stats.getNumTimesHit() != 0 || (stats.getNumTimesHit() == 0 && displayNonHitNotes)) {
				MusicNote tempNote = new MusicNote(x, 1); // placeholder duration, we just want to grab the letter and octave
				double percentHit = ((double)stats.getNumTimesHit() / (double)sheet.getNoteCount()) * 100;
				
				noteResults += "CompareValue " + x + " " + tempNote.getNote() + tempNote.getOctave();
				if (x % 1.0 == 0.5) {
					noteResults += " sharp";
					++totalUniqueSharpsHit;
				} else {
					noteResults += "      ";
					++totalUniqueNaturalsHit;
				}
				noteResults += " hits: " + stats.getNumTimesHit() + " (" + decFmt.format(percentHit) + "%), total duration: " + stats.getTotalDuration() + "\n";
			}
		}
		
		results += "Total unique compare values hit: " + totalUniqueHits + "\n";
		results += "Total unique natural compare values hit: " + totalUniqueNaturalsHit + "\n";
		results += "Total unique sharp compare values hit: " + totalUniqueSharpsHit + "\n";
		results += "Lowest compare value hit: " + lowestCompValHit + "\n";
		results += "Highest compare value hit: " + highestCompValHit + "\n";
		
		results += "-----\n" + noteResults;
		
		return results;
	}
	
	/**
	 * Returns a HashMap with keys representing compareValues, pointing to NoteStats objects as values.
	 * A NoteStats object contains the statistics for a given compareValue (how many times they were hit, ...)
	 * 
	 * @return HashMap<Double,NoteStats> mapping stats objects to compareValues
	 */
	public HashMap<Double, NoteStats> generateCompValStats(MusicSheet sheet) {
		HashMap<Double, NoteStats> compValStats = new HashMap<Double, NoteStats>();
		
		// Populate the list with notes for every minimum / maximum compare value. We can exclude ranges later *if desired* (as in, out of range of piano, never hit, ...)
		for (double x = Constants.MIN_THEORETICAL_COMPARE_VALUE; x <= Constants.MAX_THEORETICAL_COMPARE_VALUE; x = NoteUtils.getNextNoteCV(x)) {
			NoteStats ns = new NoteStats(x);
			compValStats.put(x, ns);
		}
		
		// Iterate through every slice's notes so we can see how many times notes were hit and for how long
		LinkedList<MusicSlice> slices = sheet.getSlices();
		for (int x = 0; x < slices.size(); ++x) {
			MusicSlice slice = slices.get(x);
			TreeSet<MusicNote> notes = slice.getNotes();
			Iterator<MusicNote> iter = notes.iterator();
			while (iter.hasNext()) {
				MusicNote note = iter.next();
				NoteStats targetStats = compValStats.get(note.getCompareValue());
				targetStats.hitNote(note.getDuration()); // this will both increment the hit counter, and add the duration to the total duration
			}
		}
		
		return compValStats;
	}
	
	/**
	 * Gets the maximum number of notes that are ever hit simultaneously throughout the course of the song.
	 * This helps determine the minimum number of sliding (or human) fingers that will be needed to play a song.
	 * 
	 * @param sheet
	 * @return
	 */
	public int getMaxSimulHits(MusicSheet sheet) {
		int maxNotes = 0;
		
		// Just walk through each slice and keep track of the highest count of notes in an individual slice
		LinkedList<MusicSlice> slices = sheet.getSlices();
		for (int x = 0; x < slices.size(); ++x) {
			
			// Since this is coming from an .alc file, there shouldn't be any rests, so we can safely just get the size() of the collection.
			// While legacy .alc files used to have rests, any old .alc files I've had have since been regenerated without them.
			int notesHit = slices.get(x).getNotes().size();
			if (notesHit > maxNotes) {
				maxNotes = notesHit;
			}
		}
		
		return maxNotes;
	}
	
	/**
	 * Gets the maximum number of notes that are ever hit OR HELD simultaneously throughout the course of the song.
	 * The distinction with getMaxSimultaneousHits is this:
	 * say the max number of notes hit at the same time is 6 in a given song.
	 * But say there were also 2 notes being held down 1 second earlier than the 6 are hit, and they are still being held as the 6 are hit.
	 * This means you would actually need 8 fingers when you go to hit those 6, because you still need to hold the 2 held notes until their durations are up.
	 * 
	 * Why this distinction exists is because it isn't completely known yet how the physical hardware fingers will work.
	 * If it only costs power to toggle a finger to move onto the on or off position, then we'd only need to power 6 finger toggles in the above example.
	 * However, if we need to continue supplying power to fingers to keep them holding down a note, and not just to toggle their current position, then we'd in fact need to power 8 fingers.
	 * 
	 * Thus, both getMaxSimulHits and getMaxSimulHitsAndHolds both exist.
	 * 
	 * @param sheet
	 * @return
	 */
	public int getMaxSimulHitsAndHolds(MusicSheet sheet) {
		int maxNotes = 0;
		
		liveSlice = new MusicSlice(0);
		int sliceIndex = 0;
		int rollingTime = 0;
		
		int delay = sheet.getGCD();
		
		// This is going to get ugly.
		// Basically, we're going to walk through the song using the GCD, and keep track of notes' durations, to see how many holds and hits occur simultaneously.
		// Almost all of this code is ripped straight from the PianoFeigner's code to play the song, minus the waits and sound files.

		LinkedList<MusicSlice> slices = sheet.getSlices();
		while (sliceIndex < slices.size()) {
			MusicSlice currentSlice = slices.get(sliceIndex);
			
			if (currentSlice.getStartTime() != rollingTime) {
				// while this is the next MusicSlice we need to play, it is NOT time to play it yet. so do nothing and wait for the next loop.
				// we'll send an empty MusicSlice, so the LiveSlice will update as needed and remove expired Notes from the collection.
				setHitNotes(new MusicSlice(rollingTime), delay, rollingTime);
			} else {
				++sliceIndex;
				setHitNotes(currentSlice, delay, rollingTime);
			}
			
			int currentNotes = liveSlice.getNotes().size();
			if (currentNotes > maxNotes) {
				maxNotes = currentNotes;
			}
			
			rollingTime += delay;
		}
		
		return maxNotes;
	}
	
	/**
	 * Note: this is largely ripped STRAIGHT from the PianoFeigner
	 * See PianoFeigner#setHitNotes for more details.
	 */
	private void setHitNotes(MusicSlice currentSlice, int duration, int startTime) {
		MusicSlice newSlice = new MusicSlice(startTime); // this will store the new notes to display in the gui
		
		// check for expired previous notes
		if (liveSlice.getNotes() != null) {
			Iterator<MusicNote> iter = liveSlice.getNotes().iterator();
			while (iter.hasNext()) {
				MusicNote note = iter.next();
				note.feignerDecreaseRemainingDuration(duration);
				if (note.feignerGetRemainingDuration() > 0) {
					// only notes that have remaining duration can be kept
					//newSlice.addMusicNote(note); // we don't check the returned success boolean flag, because we take no action if it were to fail as a duplicate
					
					newSlice.getNotes().add(note);
					// We are doing getNotes().add() instead of calling MusicSlice#addMusicNote(), because the liveSlice will log duplicates if they exist
					// and I don't want those interfering with the rest of the statistics reporting.
				}
			}
		}
		
		// add the new notes to display as hit
		Iterator<MusicNote> iter = currentSlice.getNotes().iterator();
		while (iter.hasNext()) {
			MusicNote note = iter.next();
			note.feignerInitRemainingDuration();
			newSlice.addMusicNote(note);
		}
		
		liveSlice = newSlice;
	}
	
	
	
	
	
	
	// Is there an argument to be made about moving MusicSheet's getEndTime and getGCD to this class instead?
	// Although, those values are *required* for MusicSheets to operate / play, whereas the Stats returned here may only be for manual inspection or for limited use cases
	
}
