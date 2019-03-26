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
		slice.addMusicNote(note);
		slice.addMusicNote(note2);
		
		boolean doesContain = slice.containsNote(note.getCompareValue());
		assertTrue(doesContain);
		doesContain = slice.containsNote(note2.getCompareValue());
		assertTrue(doesContain);
	}
	
	@Test
	public void testContainsNoteFail() {
		MusicSlice slice = new MusicSlice(0);
		MusicNote note = new MusicNote("A", 1, 1, false, false);
		MusicNote note2 = new MusicNote("B", 1, 2, false, false);
		slice.addMusicNote(note);
		slice.addMusicNote(note2);
		
		boolean doesContain = slice.containsNote(0);
		assertFalse(doesContain);
		doesContain = slice.containsNote(-1);
		assertFalse(doesContain);
	}
	
	@Test
	public void testContainsNegativeStartTime() {
		MusicSlice slice = new MusicSlice(-10);
		MusicNote note = new MusicNote("A", 1, 1, false, false);
		slice.addMusicNote(note);
		
		assertTrue(slice.getStartTime() == 0);
	}
}
