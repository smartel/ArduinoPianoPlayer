package Tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import DataObjs.MusicNote;
import DataObjs.MusicSlice;

public class MusicSliceTests {

	// TODO tbd. basically confirm the compareTo works
	
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
}
