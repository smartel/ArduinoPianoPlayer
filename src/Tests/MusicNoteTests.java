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
	public void testOctaveBelowZeroOnNonRest() {
		MusicNote note = new MusicNote("A", -1, 1, false, false);
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
	public void testOctaveBelowZeroOnRestCtor1() {
		MusicNote note = new MusicNote(Constants.NOTE_REST, -1, 1, false, false);
		boolean validCompVal = note.getCompareValue() == 0;
		assertTrue(validCompVal);
	}

	@Test
	public void testOctaveBelowZeroOnRestCtor1Flat() {
		MusicNote note = new MusicNote(Constants.NOTE_REST, -1, 1, false, true);
		boolean validCompVal = note.getCompareValue() == 0;
		assertTrue(validCompVal);
	}

	@Test
	public void testOctaveBelowZeroOnRestCtor1Sharp() {
		MusicNote note = new MusicNote(Constants.NOTE_REST, -1, 1, true, false);
		boolean validCompVal = note.getCompareValue() == 0;
		assertTrue(validCompVal);
	}

	@Test
	public void testOctaveBelowZeroOnRestCtor1FlatSharp() {
		MusicNote note = new MusicNote(Constants.NOTE_REST, -1, 1, true, true);
		boolean validCompVal = note.getCompareValue() == 0;
		assertTrue(validCompVal);
	}

	@Test
	public void testOctaveNonZeroOnRestCtor1() {
		MusicNote note = new MusicNote(Constants.NOTE_REST, 1, 1, false, false);
		boolean validCompVal = note.getCompareValue() == 0;
		assertTrue(validCompVal);
	}

	@Test
	public void testOctaveNonZeroOnRestCtor1Flat() {
		MusicNote note = new MusicNote(Constants.NOTE_REST, 1, 1, false, true);
		boolean validCompVal = note.getCompareValue() == 0;
		assertTrue(validCompVal);
	}

	@Test
	public void testOctaveNonZeroOnRestCtor1Sharp() {
		MusicNote note = new MusicNote(Constants.NOTE_REST, 1, 1, true, false);
		boolean validCompVal = note.getCompareValue() == 0;
		assertTrue(validCompVal);
	}

	@Test
	public void testNegativeOctaveOnRestCtor1FlatSharp() {
		MusicNote note = new MusicNote(Constants.NOTE_REST, -1, 1, true, true);
		boolean validCompVal = note.getCompareValue() == 0;
		assertTrue(validCompVal);
	}

	@Test
	public void testNegativeOctaveOnRestCtor1() {
		MusicNote note = new MusicNote(Constants.NOTE_REST, -1, 1, false, false);
		boolean validCompVal = note.getCompareValue() == 0;
		assertTrue(validCompVal);
	}

	@Test
	public void testOctaveBelowZeroOnRestCtor2() {
		MusicNote note = new MusicNote(Constants.NOTE_REST, -1, 1);
		boolean validCompVal = note.getCompareValue() == 0;
		assertTrue(validCompVal);
	}

	@Test
	public void testOctaveNonZeroOnRestCtor2() {
		MusicNote note = new MusicNote(Constants.NOTE_REST, 1, 1);
		boolean validCompVal = note.getCompareValue() == 0;
		assertTrue(validCompVal);
	}

	@Test
	public void testNegativeOctaveOnRestCtor2() {
		MusicNote note = new MusicNote(Constants.NOTE_REST, -1, 1);
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
	
	
	// Compare Value constructor tests
	
	@Test
	public void testCVCtorFirstOctaveSharp() {
		MusicNote note = new MusicNote(6.5, 1000);
		boolean validCompVal = note.getCompareValue() == 6.5;
		assertTrue(validCompVal);
	}

	@Test
	public void testCVCtorSecondOctaveSharp() {
		MusicNote note = new MusicNote(8.5, 1000);
		boolean validCompVal = note.getCompareValue() == 8.5;
		assertTrue(validCompVal);
	}

	@Test
	public void testCVCtorBigInt() {
		MusicNote note = new MusicNote(43, 1000);
		boolean validCompVal = note.getCompareValue() == 43;
		assertTrue(validCompVal);
	}

	// Promotion / Demotion tests (we put in checks for B sharps and E sharps to become C naturals and F naturals, and for C / F flats to become B / E naturals, for safety's sake.
	// Also catch cases where we can't tell which way to promote / demote, so we return an error value. (A 9.5 is indecipherable from B sharp or C flat)
	
	
	@Test
	public void testCtorPromote1() {
		MusicNote note = new MusicNote(Constants.NOTE_B, 2, 1000, true, false);
		boolean validCompVal = note.getCompareValue() == 22; // should promote B sharp to C, a 21 to 22
		assertTrue(validCompVal);
	}
	
	@Test
	public void testCtorPromote2() {
		MusicNote note = new MusicNote(Constants.NOTE_E, 2, 1000, true, false);
		boolean validCompVal = note.getCompareValue() == 18; // should promote E sharp to F, a 17 to 18
		assertTrue(validCompVal);
	}
	
	@Test
	public void testCtorDemote1() {
		MusicNote note = new MusicNote(Constants.NOTE_C, 2, 1000, false, true);
		boolean validCompVal = note.getCompareValue() == 14; // should demote C flat to B, a 15 to 14
		assertTrue(validCompVal);
	}
	
	@Test
	public void testCtorDemote2() {
		MusicNote note = new MusicNote(Constants.NOTE_F, 2, 1000, false, true);
		boolean validCompVal = note.getCompareValue() == 17; // should demote F flat to E, a 18 to 17
		assertTrue(validCompVal);
	}
	
	@Test
	public void testCVCtorIndecipherable1() {
		MusicNote note = new MusicNote(7.5, 1000); // a 7.5 would be a "B sharp" or a "C flat" and shouldn't be possible. We can't tell if it should be B or C, so -1, error out.
		boolean validCompVal = note.getCompareValue() == -1;
		assertTrue(validCompVal);
	}
	
	@Test
	public void testCVCtorIndecipherable2() {
		MusicNote note = new MusicNote(3.5, 1000); // a 3.5 would be an "E sharp" or a "F flat" and shouldn't be possible. We can't tell if it should be E or F, so -1, error out.
		boolean validCompVal = note.getCompareValue() == -1;
		assertTrue(validCompVal);
	}
	
	
	// BPM Adjustment tests
	
	@Test
	public void testBpmPositive() {
		MusicNote note = new MusicNote("A", 2, 2, false, false);
		note.applyBpmMultiplier(10);
		assertTrue(note.getDuration() == 20);
	}

	@Test
	public void testBpmNegative() {
		MusicNote note = new MusicNote("A", 2, 2, false, false);
		note.applyBpmMultiplier(-10);
		assertTrue(note.getDuration() == 2);
	}

	@Test
	public void testBpmZero() {
		MusicNote note = new MusicNote("A", 2, 2, false, false);
		note.applyBpmMultiplier(0);
		assertTrue(note.getDuration() == 2);
	}

	@Test
	public void testBpmOne() {
		MusicNote note = new MusicNote("A", 2, 2, false, false);
		note.applyBpmMultiplier(1);
		assertTrue(note.getDuration() == 2);
	}
	
	// Octave Adjustment tests
	
	@Test
	public void testOctavePositiveInBounds() {
		MusicNote note = new MusicNote("A", 2, 2, false, false);
		note.applyOctaveAdjustment(2, true);
		assertTrue(note.getOctave() == 4);
	}
	@Test
	public void testOctavePositiveLandOnBounds() {
		MusicNote note = new MusicNote("A", 2, 2, false, false);
		note.applyOctaveAdjustment(8, true);
		assertTrue(note.getOctave() == Constants.MAX_PIANO_OCTAVE);
	}
	@Test
	public void testOctavePositiveOverBoundsBy1Cap() {
		MusicNote note = new MusicNote("A", 4, 2, false, false);
		note.applyOctaveAdjustment(7, false);
		assertTrue(note.getOctave() == Constants.MAX_PIANO_OCTAVE); // it capped at the boundary
	}
	public void testOctavePositiveOverBoundsByALotCap() {
		MusicNote note = new MusicNote("A", 4, 2, false, false);
		note.applyOctaveAdjustment(20, false);
		assertTrue(note.getOctave() == Constants.MAX_PIANO_OCTAVE); // it capped at the boundary
	}
	@Test
	public void testOctavePositiveOverBoundsBy1Delete() {
		MusicNote note = new MusicNote("A", 2, 2, false, false);
		note.applyOctaveAdjustment(9, true);
		assertTrue(note.getOctave() == Constants.REST_OCTAVE_VALUE);
		assertTrue(note.getCompareValue() == Constants.REST_COMP_VALUE); // it became a rest, effectively deleted
	}
	@Test
	public void testOctavePositiveOverBoundsByALotDelete() {
		MusicNote note = new MusicNote("A", 2, 2, false, false);
		note.applyOctaveAdjustment(20, true);
		assertTrue(note.getOctave() == Constants.REST_OCTAVE_VALUE);
		assertTrue(note.getCompareValue() == Constants.REST_COMP_VALUE); // it became a rest, effectively deleted
	}
	
	@Test
	public void testOctaveNegativeInBounds() {
		MusicNote note = new MusicNote("A", 4, 4, false, false);
		note.applyOctaveAdjustment(-1, true);
		assertTrue(note.getOctave() == 3);
	}
	@Test
	public void testOctaveNegativeLandOnBounds() {
		MusicNote note = new MusicNote("A", 4, 4, false, false);
		note.applyOctaveAdjustment(-4, true);
		assertTrue(note.getOctave() == Constants.MIN_PIANO_OCTAVE);
	}
	@Test
	public void testOctaveNegativeUnderBoundsBy1Cap() {
		MusicNote note = new MusicNote("A", 4, 4, false, false);
		note.applyOctaveAdjustment(-5, false);
		assertTrue(note.getOctave() == Constants.MIN_PIANO_OCTAVE); // it capped at the boundary
	}
	public void testOctavePositiveUnderBoundsByALotCap() {
		MusicNote note = new MusicNote("A", 4, 4, false, false);
		note.applyOctaveAdjustment(-20, false);
		assertTrue(note.getOctave() == Constants.MIN_PIANO_OCTAVE); // it capped at the boundary
	}
	@Test
	public void testOctaveNegativeUnderBoundsBy1Delete() {
		MusicNote note = new MusicNote("A", 4, 4, false, false);
		note.applyOctaveAdjustment(-5, true);
		assertTrue(note.getOctave() == Constants.REST_OCTAVE_VALUE);
		assertTrue(note.getCompareValue() == Constants.REST_COMP_VALUE); // it became a rest, effectively deleted
	}
	@Test
	public void testOctaveNegativeUnderBoundsByALotDelete() {
		MusicNote note = new MusicNote("A", 4, 4, false, false);
		note.applyOctaveAdjustment(-20, true);
		assertTrue(note.getOctave() == -1);
		assertTrue(note.getCompareValue() == Constants.REST_COMP_VALUE); // it became a rest, effectively deleted
	}
	
	// Copy constructor test
	
	@Test
	public void testCopyConstructor() {
		MusicNote note = new MusicNote("A", 3, 4, true, false);
		MusicNote other = new MusicNote(note);
		assertTrue(note.getNote().equalsIgnoreCase(other.getNote()));
		assertTrue(note.getDuration() == other.getDuration());
		assertTrue(note.getOctave() == other.getOctave());
		assertTrue(note.getCompareValue() == other.getCompareValue()); // this will cover isSharp / isFlat
	}
	
}
