package Tests;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import DataObjs.MusicNote;
import DataObjs.MusicSheet;
import DataObjs.MusicSlice;
import Processors.AlcReaderWriter;

public class MusicSheetTests {
	
	@Test
	public void testGCD() {
		try {
			MusicSheet sheet;
			int gcd;
			AlcReaderWriter arw = new AlcReaderWriter();

			sheet = arw.loadAlcFile(".\\sample musixcml\\for unit tests\\my confession.alc");
			gcd = sheet.getGCD();
			
			assertTrue(gcd == 145);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	@Test
	public void testGCD2() {
		try {
			MusicSheet sheet;
			int gcd;
			AlcReaderWriter arw = new AlcReaderWriter();

			sheet = arw.loadAlcFile(".\\sample musixcml\\for unit tests\\my confession-diffgcd.alc");
			gcd = sheet.getGCD();
			
			assertTrue(gcd == 5);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	@Test
	public void testGCDNoNotes() {
		try {
			MusicSheet sheet;
			int gcd;
			
			sheet = new MusicSheet("Empty file", 0);
			gcd = sheet.getGCD();
			
			assertTrue(gcd == -1);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	@Test
	public void testEndDuration() {
		try {
			MusicSheet sheet;
			int endTime;
			AlcReaderWriter arw = new AlcReaderWriter();

			sheet = arw.loadAlcFile(".\\sample musixcml\\for unit tests\\my confession.alc");
			endTime = sheet.getEndTime();
			
			assertTrue(endTime == 67280);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	@Test
	public void testEndDuration2() {
		try {
			MusicSheet sheet;
			int endTime;
			AlcReaderWriter arw = new AlcReaderWriter();

			sheet = arw.loadAlcFile(".\\sample musixcml\\for unit tests\\my confession-diffenddur.alc");
			endTime = sheet.getEndTime();
			
			assertTrue(endTime == 67370);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	@Test
	public void testEndDurationNoNotes() {
		try {
			MusicSheet sheet;
			int endTime;
			
			sheet = new MusicSheet("Empty file", 0);
			endTime = sheet.getEndTime();
			
			assertTrue(endTime == -1);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	// Copy constructor test
	
	@Test
	public void testCopyConstructor() {
		// Create a sheet consisting of a slice with a note
		MusicSheet sheet = new MusicSheet("Dummy info", 1);
		MusicSlice slice = new MusicSlice(0);
		MusicNote note = new MusicNote("A", 1, 1, false, false);
		sheet.addSlice(slice);
		
		boolean didAdd = slice.addMusicNote(note);		
		boolean doesContain = slice.containsNote(note.getCompareValue());	
		assertTrue(doesContain);
		assertTrue(didAdd);
		
		// Now copy the sheet and ensure all the values match and that the slice + note were also copied
		MusicSheet other = new MusicSheet(sheet);
		assertTrue(sheet.getInfoLine().equals(other.getInfoLine()));
		assertTrue(sheet.getNoteCount() == other.getNoteCount());
		assertTrue(sheet.getEndTime() == other.getEndTime());
		assertTrue(sheet.getSlices().getFirst().getStartTime() == other.getSlices().getFirst().getStartTime());
		assertTrue(sheet.getSlices().getFirst().getNotes().first().getCompareValue() == other.getSlices().getFirst().getNotes().first().getCompareValue());
	}
}
