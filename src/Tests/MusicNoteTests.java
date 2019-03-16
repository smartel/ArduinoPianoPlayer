package Tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.jupiter.api.Test;

import DataObjs.MusicNote;
import Utils.Constants;

public class MusicNoteTests {

	// MusicNote constructors inevitably hit NoteUtils.generateCompareValue.
	// The way we can tell if tests pass or not, is by seeing if an invalid compare value is returned (-1), because then we'll know it failed to construct with the given values.
	// We anticipate rest notes returning a 0 compare value.
	
	// Tests on valid note letters (A-G, uppercase and lowercase, alternating overloaded constructors)
	
	@Test
	public void testAUpper() {
		MusicNote note = new MusicNote("A", 1, 1, false, false);
		boolean validCompVal = note.getCompareValue() > 0;
		assertTrue(validCompVal);
	}

	@Test
	public void testALower() {
		MusicNote note = new MusicNote("a", 1, 1);
		boolean validCompVal = note.getCompareValue() > 0;
		assertTrue(validCompVal);
	}

	@Test
	public void testBUpper() {
		MusicNote note = new MusicNote("B", 1, 1, false, false);
		boolean validCompVal = note.getCompareValue() > 0;
		assertTrue(validCompVal);
	}

	@Test
	public void testBLower() {
		MusicNote note = new MusicNote("b", 1, 1);
		boolean validCompVal = note.getCompareValue() > 0;
		assertTrue(validCompVal);
	}

	@Test
	public void testCUpper() {
		MusicNote note = new MusicNote("C", 1, 1);
		boolean validCompVal = note.getCompareValue() > 0;
		assertTrue(validCompVal);
	}

	@Test
	public void testCLower() {
		MusicNote note = new MusicNote("c", 1, 1, false, false);
		boolean validCompVal = note.getCompareValue() > 0;
		assertTrue(validCompVal);
	}

	@Test
	public void testDUpper() {
		MusicNote note = new MusicNote("D", 1, 1);
		boolean validCompVal = note.getCompareValue() > 0;
		assertTrue(validCompVal);
	}

	@Test
	public void testDLower() {
		MusicNote note = new MusicNote("d", 1, 1, false, false);
		boolean validCompVal = note.getCompareValue() > 0;
		assertTrue(validCompVal);
	}

	@Test
	public void testEUpper() {
		MusicNote note = new MusicNote("E", 1, 1, false, false);
		boolean validCompVal = note.getCompareValue() > 0;
		assertTrue(validCompVal);
	}

	@Test
	public void testELower() {
		MusicNote note = new MusicNote("e", 1, 1, false, false);
		boolean validCompVal = note.getCompareValue() > 0;
		assertTrue(validCompVal);
	}

	@Test
	public void testFUpper() {
		MusicNote note = new MusicNote("F", 1, 1);
		boolean validCompVal = note.getCompareValue() > 0;
		assertTrue(validCompVal);
	}

	@Test
	public void testFLower() {
		MusicNote note = new MusicNote("f", 1, 1);
		boolean validCompVal = note.getCompareValue() > 0;
		assertTrue(validCompVal);
	}

	@Test
	public void testGUpper() {
		MusicNote note = new MusicNote("G", 1, 1, false, false);
		boolean validCompVal = note.getCompareValue() > 0;
		assertTrue(validCompVal);
	}

	@Test
	public void testGLower() {
		MusicNote note = new MusicNote("g", 1, 1);
		boolean validCompVal = note.getCompareValue() > 0;
		assertTrue(validCompVal);
	}

	// Tests on random invalid letters / symbols / numbers / etc as the note letter, some alternating constructors again
	
	@Test
	public void testRUpper() {
		MusicNote note = new MusicNote("R", 1, 1, false, false);
		boolean validCompVal = note.getCompareValue() > 0;
		assertFalse(validCompVal);
	}

	@Test
	public void testRLower() {
		MusicNote note = new MusicNote("r", 1, 1);
		boolean validCompVal = note.getCompareValue() > 0;
		assertFalse(validCompVal);
	}

	@Test
	public void testSUpper() {
		MusicNote note = new MusicNote("S", 1, 1);
		boolean validCompVal = note.getCompareValue() > 0;
		assertFalse(validCompVal);
	}

	@Test
	public void testSLower() {
		MusicNote note = new MusicNote("s", 1, 1, false, false);
		boolean validCompVal = note.getCompareValue() > 0;
		assertFalse(validCompVal);
	}

	@Test
	public void test1AsNote() {
		MusicNote note = new MusicNote("1", 1, 1, false, false);
		boolean validCompVal = note.getCompareValue() > 0;
		assertFalse(validCompVal);
	}

	@Test
	public void test0AsNote() {
		MusicNote note = new MusicNote("0", 1, 1);
		boolean validCompVal = note.getCompareValue() > 0;
		assertFalse(validCompVal);
	}

	@Test
	public void testEmptyStringNote() {
		MusicNote note = new MusicNote("", 1, 1, false, false);
		boolean validCompVal = note.getCompareValue() > 0;
		assertFalse(validCompVal);
	}
	
	@Test
	public void testWhitespaceNote() {
		MusicNote note = new MusicNote(" ", 1, 1);
		boolean validCompVal = note.getCompareValue() > 0;
		assertFalse(validCompVal);
	}

	@Test
	public void testMultiLetterNote() {
		MusicNote note = new MusicNote("AA", 1, 1, false, false);
		boolean validCompVal = note.getCompareValue() > 0;
		assertFalse(validCompVal);
	}

	@Test
	public void testPeriodNote() {
		MusicNote note = new MusicNote(".", 1, 1);
		boolean validCompVal = note.getCompareValue() > 0;
		assertFalse(validCompVal);
	}

	@Test
	public void testPoundNote() {
		MusicNote note = new MusicNote("#", 1, 1, false, false);
		boolean validCompVal = note.getCompareValue() > 0;
		assertFalse(validCompVal);
	}

	@Test
	public void testNewlineNote() {
		MusicNote note = new MusicNote("\n ", 1, 1);
		boolean validCompVal = note.getCompareValue() > 0;
		assertFalse(validCompVal);
	}
	
	
	
	// Octave Tests
	
	@Test
	public void testOctaveTwo() {
		MusicNote note = new MusicNote("A", 2, 1);
		boolean validCompVal = note.getCompareValue() > 0;
		assertTrue(validCompVal);
	}

	@Test
	public void testOctaveEight() {
		MusicNote note = new MusicNote("A", 8, 1, false, false);
		boolean validCompVal = note.getCompareValue() > 0;
		assertTrue(validCompVal);
	}

	@Test
	public void testOctaveZeroOnNonRest() {
		MusicNote note = new MusicNote("A", 0, 1, false, false);
		boolean validCompVal = note.getCompareValue() > 0;
		assertFalse(validCompVal);
	}

	@Test
	public void testOctaveExcessive() {
		MusicNote note = new MusicNote("A", 999, 1, false, false);
		boolean validCompVal = note.getCompareValue() > 0;
		assertTrue(validCompVal);
	}
	

	@Test
	public void testOctaveNegative() {
		MusicNote note = new MusicNote("A", -1, 1, false, false);
		boolean validCompVal = note.getCompareValue() > 0;
		assertFalse(validCompVal);
	}
	
	
	// Duration Tests
	
	@Test
	public void testAPosDuration() {
		MusicNote note = new MusicNote("A", 1, 8, false, false);
		boolean validCompVal = note.getCompareValue() > 0;
		assertTrue(validCompVal);
	}

	@Test
	public void testAExcessiveDuration() {
		MusicNote note = new MusicNote("A", 1, 800000, false, false);
		boolean validCompVal = note.getCompareValue() > 0;
		assertTrue(validCompVal);
	}
	
	@Test
	public void testANegDuration() {
		MusicNote note = new MusicNote("A", 1, -8, false, false);
		boolean validCompVal = note.getCompareValue() > 0;
		assertFalse(validCompVal);
	}
	
	@Test
	public void testAZeroDuration() {
		MusicNote note = new MusicNote("A", 1, 0, false, false);
		boolean validCompVal = note.getCompareValue() > 0;
		assertFalse(validCompVal);
	}
	
	
	
	// Rest note tests including Octaves and Flags (note - we want a compare value of 0 for a rest)
	
	@Test
	public void testOctaveZeroOnRestCtor1() {
		MusicNote note = new MusicNote(Constants.REST_NOTE, 0, 1, false, false);
		boolean validCompVal = note.getCompareValue() == 0;
		assertTrue(validCompVal);
	}

	@Test
	public void testOctaveZeroOnRestCtor1Flat() {
		MusicNote note = new MusicNote(Constants.REST_NOTE, 0, 1, false, true);
		boolean validCompVal = note.getCompareValue() == 0;
		assertTrue(validCompVal);
	}

	@Test
	public void testOctaveZeroOnRestCtor1Sharp() {
		MusicNote note = new MusicNote(Constants.REST_NOTE, 0, 1, true, false);
		boolean validCompVal = note.getCompareValue() == 0;
		assertTrue(validCompVal);
	}

	@Test
	public void testOctaveZeroOnRestCtor1FlatSharp() {
		MusicNote note = new MusicNote(Constants.REST_NOTE, 0, 1, true, true);
		boolean validCompVal = note.getCompareValue() == 0;
		assertTrue(validCompVal);
	}

	@Test
	public void testOctaveNonZeroOnRestCtor1() {
		MusicNote note = new MusicNote(Constants.REST_NOTE, 1, 1, false, false);
		boolean validCompVal = note.getCompareValue() == 0;
		assertTrue(validCompVal);
	}

	@Test
	public void testOctaveNonZeroOnRestCtor1Flat() {
		MusicNote note = new MusicNote(Constants.REST_NOTE, 1, 1, false, true);
		boolean validCompVal = note.getCompareValue() == 0;
		assertTrue(validCompVal);
	}

	@Test
	public void testOctaveNonZeroOnRestCtor1Sharp() {
		MusicNote note = new MusicNote(Constants.REST_NOTE, 1, 1, true, false);
		boolean validCompVal = note.getCompareValue() == 0;
		assertTrue(validCompVal);
	}

	@Test
	public void testNegativeOctaveOnRestCtor1FlatSharp() {
		MusicNote note = new MusicNote(Constants.REST_NOTE, -1, 1, true, true);
		boolean validCompVal = note.getCompareValue() == 0;
		assertTrue(validCompVal);
	}

	@Test
	public void testNegativeOctaveOnRestCtor1() {
		MusicNote note = new MusicNote(Constants.REST_NOTE, -1, 1, false, false);
		boolean validCompVal = note.getCompareValue() == 0;
		assertTrue(validCompVal);
	}

	@Test
	public void testOctaveZeroOnRestCtor2() {
		MusicNote note = new MusicNote(Constants.REST_NOTE, 0, 1);
		boolean validCompVal = note.getCompareValue() == 0;
		assertTrue(validCompVal);
	}

	@Test
	public void testOctaveNonZeroOnRestCtor2() {
		MusicNote note = new MusicNote(Constants.REST_NOTE, 1, 1);
		boolean validCompVal = note.getCompareValue() == 0;
		assertTrue(validCompVal);
	}

	@Test
	public void testNegativeOctaveOnRestCtor2() {
		MusicNote note = new MusicNote(Constants.REST_NOTE, -1, 1);
		boolean validCompVal = note.getCompareValue() == 0;
		assertTrue(validCompVal);
	}
	
	// Rest notes - duration tests
	
	@Test
	public void testRestCtor3RegularDur() {
		MusicNote note = new MusicNote(1);
		boolean validCompVal = note.getCompareValue() == 0;
		assertTrue(validCompVal);
	}

	@Test
	public void testRestCtor3NegativeDur() {
		MusicNote note = new MusicNote(-1);
		boolean validCompVal = note.getCompareValue() == 0;
		assertFalse(validCompVal);
	}

	@Test
	public void testRestCtor3ZeroDur() {
		MusicNote note = new MusicNote(0);
		boolean validCompVal = note.getCompareValue() == 0;
		assertFalse(validCompVal);
	}
	
	
}
