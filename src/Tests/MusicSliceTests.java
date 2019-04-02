package Tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import DataObjs.MusicNote;
import DataObjs.MusicSlice;

public class MusicSliceTests {

	@Test
	public void testContainsNoteSuccess() {
		MusicSlice slice = new MusicSlice(0);
		MusicNote note = new MusicNote("A", 1, 1, false, false);
		MusicNote note2 = new MusicNote("B", 1, 2, false, false);
		boolean didAdd1 = slice.addMusicNote(note);
		boolean didAdd2 = slice.addMusicNote(note2);
		
		boolean doesContain = slice.containsNote(note.getCompareValue());
		assertTrue(doesContain);
		doesContain = slice.containsNote(note2.getCompareValue());
		assertTrue(doesContain);
		assertTrue(didAdd1);
		assertTrue(didAdd2);
	}
	
	@Test
	public void testContainsNoteFail() {
		MusicSlice slice = new MusicSlice(0);
		MusicNote note = new MusicNote("A", 1, 1, false, false);
		MusicNote note2 = new MusicNote("B", 1, 2, false, false);
		boolean didAdd1 = slice.addMusicNote(note);
		boolean didAdd2 = slice.addMusicNote(note2);
		
		boolean doesContain = slice.containsNote(0);
		assertFalse(doesContain);
		doesContain = slice.containsNote(-1);
		assertFalse(doesContain);
		assertTrue(didAdd1);
		assertTrue(didAdd2);
	}
	
	@Test
	public void testContainsNegativeStartTime() {
		MusicSlice slice = new MusicSlice(-10);
		MusicNote note = new MusicNote("A", 1, 1, false, false);
		boolean didAdd = slice.addMusicNote(note);
		
		assertTrue(slice.getStartTime() == 0);
		assertTrue(didAdd);
	}
	
	@Test
	public void testAddDuplicateNote() {
		MusicSlice slice = new MusicSlice(10);
		MusicNote note1 = new MusicNote("A", 1, 1, false, false);
		MusicNote note2 = new MusicNote("A", 1, 1, false, false);
		boolean didAdd1 = slice.addMusicNote(note1);
		boolean didAdd2 = slice.addMusicNote(note2);
		
		assertTrue(slice.getStartTime() == 10);
		assertTrue(didAdd1);
		assertFalse(didAdd2);
	}
	
	
	// BPM Adjustment tests
	
	@Test
	public void testAdjustBpmPositive() {
		MusicSlice slice = new MusicSlice(10);
		slice.applyBpmMultipler(10);
		assertTrue(slice.getStartTime() == 100);
	}
	
	@Test
	public void testAdjustBpmNegative() {
		MusicSlice slice = new MusicSlice(10);
		slice.applyBpmMultipler(-10);
		assertTrue(slice.getStartTime() == 10);
	}
	
	@Test
	public void testAdjustBpmZero() {
		MusicSlice slice = new MusicSlice(10);
		slice.applyBpmMultipler(0);
		assertTrue(slice.getStartTime() == 10);
	}
	
	@Test
	public void testAdjustBpmOne() {
		MusicSlice slice = new MusicSlice(10);
		slice.applyBpmMultipler(1);
		assertTrue(slice.getStartTime() == 10);
	}
	
	// Copy constructor test
	
	@Test
	public void testCopyConstructor() {
		// Create a slice with 1 note
		MusicSlice slice = new MusicSlice(0);
		MusicNote note = new MusicNote("A", 1, 1, false, false);
		boolean didAdd = slice.addMusicNote(note);		
		boolean doesContain = slice.containsNote(note.getCompareValue());
		assertTrue(doesContain);
		assertTrue(didAdd);
		
		// Now copy the slice and ensure all the values match and that the note was also copied
		MusicSlice other = new MusicSlice(slice);
		assertTrue(slice.getStartTime() == other.getStartTime());
		assertTrue(other.containsNote(note.getCompareValue()));
	}
}
