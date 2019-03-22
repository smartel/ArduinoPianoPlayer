package Tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import Utils.Constants;
import Utils.NoteUtils;

public class NoteUtilsTests {

	// Basically a ton of generateCompareValue tests, similar to the MusicNoteTests

	// Tests on valid note letters (A-G, uppercase and lowercase, alternating overloaded constructors)
	
	@Test
	public void testAUpper() {
		double compVal = NoteUtils.generateCompareValue("A", 1, false, false);
		boolean validCompVal = compVal > 0;
		assertTrue(validCompVal);
	}

	@Test
	public void testALower() {
		double compVal = NoteUtils.generateCompareValue("a", 1, false, false);
		boolean validCompVal = compVal > 0;
		assertTrue(validCompVal);
	}

	@Test
	public void testBUpper() {
		double compVal = NoteUtils.generateCompareValue("B", 1, false, false);
		boolean validCompVal = compVal > 0;
		assertTrue(validCompVal);
	}

	@Test
	public void testBLower() {
		double compVal = NoteUtils.generateCompareValue("b", 1, false, false);
		boolean validCompVal = compVal > 0;
		assertTrue(validCompVal);
	}

	@Test
	public void testCUpper() {
		double compVal = NoteUtils.generateCompareValue("C", 1, false, false);
		boolean validCompVal = compVal > 0;
		assertTrue(validCompVal);
	}

	@Test
	public void testCLower() {
		double compVal = NoteUtils.generateCompareValue("c", 1, false, false);
		boolean validCompVal = compVal > 0;
		assertTrue(validCompVal);
	}

	@Test
	public void testDUpper() {
		double compVal = NoteUtils.generateCompareValue("D", 1, false, false);
		boolean validCompVal = compVal > 0;
		assertTrue(validCompVal);
	}

	@Test
	public void testDLower() {
		double compVal = NoteUtils.generateCompareValue("d", 1, false, false);
		boolean validCompVal = compVal > 0;
		assertTrue(validCompVal);
	}

	@Test
	public void testEUpper() {
		double compVal = NoteUtils.generateCompareValue("E", 1, false, false);
		boolean validCompVal = compVal > 0;
		assertTrue(validCompVal);
	}

	@Test
	public void testELower() {
		double compVal = NoteUtils.generateCompareValue("e", 1, false, false);
		boolean validCompVal = compVal > 0;
		assertTrue(validCompVal);
	}

	@Test
	public void testFUpper() {
		double compVal = NoteUtils.generateCompareValue("F", 1, false, false);
		boolean validCompVal = compVal > 0;
		assertTrue(validCompVal);
	}

	@Test
	public void testFLower() {
		double compVal = NoteUtils.generateCompareValue("f", 1, false, false);
		boolean validCompVal = compVal > 0;
		assertTrue(validCompVal);
	}

	@Test
	public void testGUpper() {
		double compVal = NoteUtils.generateCompareValue("G", 1, false, false);
		boolean validCompVal = compVal > 0;
		assertTrue(validCompVal);
	}

	@Test
	public void testGLower() {
		double compVal = NoteUtils.generateCompareValue("g", 1, false, false);
		boolean validCompVal = compVal > 0;
		assertTrue(validCompVal);
	}

	// Tests on random invalid letters / symbols / numbers / etc as the note letter, some alternating constructors again
	
	@Test
	public void testRUpper() {
		double compVal = NoteUtils.generateCompareValue("R", 1, false, false);
		boolean validCompVal = compVal > 0;
		assertFalse(validCompVal);
	}

	@Test
	public void testRLower() {
		double compVal = NoteUtils.generateCompareValue("r", 1, false, false);
		boolean validCompVal = compVal > 0;
		assertFalse(validCompVal);
	}

	@Test
	public void testSUpper() {
		double compVal = NoteUtils.generateCompareValue("S", 1, false, false);
		boolean validCompVal = compVal > 0;
		assertFalse(validCompVal);
	}

	@Test
	public void testSLower() {
		double compVal = NoteUtils.generateCompareValue("s", 1, false, false);
		boolean validCompVal = compVal > 0;
		assertFalse(validCompVal);
	}

	@Test
	public void test1AsNote() {
		double compVal = NoteUtils.generateCompareValue("1", 1, false, false);
		boolean validCompVal = compVal > 0;
		assertFalse(validCompVal);
	}

	@Test
	public void test0AsNote() {
		double compVal = NoteUtils.generateCompareValue("0", 1, false, false);
		boolean validCompVal = compVal > 0;
		assertFalse(validCompVal);
	}

	@Test
	public void testEmptyStringNote() {
		double compVal = NoteUtils.generateCompareValue("", 1, false, false);
		boolean validCompVal = compVal > 0;
		assertFalse(validCompVal);
	}
	
	@Test
	public void testWhitespaceNote() {
		double compVal = NoteUtils.generateCompareValue(" ", 1, false, false);
		boolean validCompVal = compVal > 0;
		assertFalse(validCompVal);
	}

	@Test
	public void testMultiLetterNote() {
		double compVal = NoteUtils.generateCompareValue("AA", 1, false, false);
		boolean validCompVal = compVal > 0;
		assertFalse(validCompVal);
	}

	@Test
	public void testPeriodNote() {
		double compVal = NoteUtils.generateCompareValue(".", 1, false, false);
		boolean validCompVal = compVal > 0;
		assertFalse(validCompVal);
	}

	@Test
	public void testPoundNote() {
		double compVal = NoteUtils.generateCompareValue("#", 1, false, false);
		boolean validCompVal = compVal > 0;
		assertFalse(validCompVal);
	}

	@Test
	public void testNewlineNote() {
		double compVal = NoteUtils.generateCompareValue("\n", 1, false, false);
		boolean validCompVal = compVal > 0;
		assertFalse(validCompVal);
	}
	
	
	
	// Octave Tests
	
	@Test
	public void testOctaveTwo() {
		double compVal = NoteUtils.generateCompareValue("A", 2, false, false);
		boolean validCompVal = compVal > 0;
		assertTrue(validCompVal);
	}

	@Test
	public void testOctaveEight() {
		double compVal = NoteUtils.generateCompareValue("A", 8, false, false);
		boolean validCompVal = compVal > 0;
		assertTrue(validCompVal);
	}

	@Test
	public void testOctaveZeroOnNonRest() {
		double compVal = NoteUtils.generateCompareValue("A", 0, false, false);
		boolean validCompVal = compVal > 0;
		assertFalse(validCompVal);
	}

	@Test
	public void testOctaveExcessive() {
		double compVal = NoteUtils.generateCompareValue("A", 999, false, false);
		boolean validCompVal = compVal > 0;
		assertTrue(validCompVal);
	}
	

	@Test
	public void testOctaveNegative() {
		double compVal = NoteUtils.generateCompareValue("A", -1, false, false);
		boolean validCompVal = compVal > 0;
		assertFalse(validCompVal);
	}
	
	
	
	// Rest note tests including Octaves and Flags (note - we want a compare value of 0 for a rest)
	
	@Test
	public void testOctaveZeroOnRest() {
		double compVal = NoteUtils.generateCompareValue(Constants.NOTE_REST, 0, false, false);
		boolean validCompVal = compVal == 0;
		assertTrue(validCompVal);
	}

	@Test
	public void testOctaveZeroOnRestAndFlat() {
		double compVal = NoteUtils.generateCompareValue(Constants.NOTE_REST, 0, false, true);
		boolean validCompVal = compVal == 0;
		assertTrue(validCompVal);
	}

	@Test
	public void testOctaveZeroOnRestAndSharp() {
		double compVal = NoteUtils.generateCompareValue(Constants.NOTE_REST, 0, true, false);
		boolean validCompVal = compVal == 0;
		assertTrue(validCompVal);
	}

	@Test
	public void testOctaveZeroOnRestAndFlatSharp() {
		double compVal = NoteUtils.generateCompareValue(Constants.NOTE_REST, 0, true, true);
		boolean validCompVal = compVal == 0;
		assertTrue(validCompVal);
	}

	@Test
	public void testOctaveNonZeroOnRest() {
		double compVal = NoteUtils.generateCompareValue(Constants.NOTE_REST, 1, false, false);
		boolean validCompVal = compVal == 0;
		assertTrue(validCompVal);
	}

	@Test
	public void testOctaveNonZeroOnRestFlat() {
		double compVal = NoteUtils.generateCompareValue(Constants.NOTE_REST, 1, false, true);
		boolean validCompVal = compVal == 0;
		assertTrue(validCompVal);
	}

	@Test
	public void testOctaveNonZeroOnRestSharp() {
		double compVal = NoteUtils.generateCompareValue(Constants.NOTE_REST, 1, true, false);
		boolean validCompVal = compVal == 0;
		assertTrue(validCompVal);
	}

	@Test
	public void testNegativeOctaveOnRestFlatSharp() {
		double compVal = NoteUtils.generateCompareValue(Constants.NOTE_REST, -1, true, true);
		boolean validCompVal = compVal == 0;
		assertTrue(validCompVal);
	}

	@Test
	public void testNegativeOctaveOnRest() {
		double compVal = NoteUtils.generateCompareValue(Constants.NOTE_REST, -1, false, false);
		boolean validCompVal = compVal == 0;
		assertTrue(validCompVal);
	}
	
	// GetNoteForPosition tests

	@Test
	public void testGetNoteForPosition1() {
		String val = NoteUtils.getNoteForPosition(1);
		assertTrue(val.equalsIgnoreCase(Constants.NOTE_A));
	}

	@Test
	public void testGetNoteForPosition2() {
		String val = NoteUtils.getNoteForPosition(2);
		assertTrue(val.equalsIgnoreCase(Constants.NOTE_B));
	}
	
	@Test
	public void testGetNoteForPosition3() {
		String val = NoteUtils.getNoteForPosition(3);
		assertTrue(val.equalsIgnoreCase(Constants.NOTE_C));
	}
	
	@Test
	public void testGetNoteForPosition4() {
		String val = NoteUtils.getNoteForPosition(4);
		assertTrue(val.equalsIgnoreCase(Constants.NOTE_D));
	}
	
	@Test
	public void testGetNoteForPosition5() {
		String val = NoteUtils.getNoteForPosition(5);
		assertTrue(val.equalsIgnoreCase(Constants.NOTE_E));
	}
	
	@Test
	public void testGetNoteForPosition6() {
		String val = NoteUtils.getNoteForPosition(6);
		assertTrue(val.equalsIgnoreCase(Constants.NOTE_F));
	}
	
	@Test
	public void testGetNoteForPosition7() {
		String val = NoteUtils.getNoteForPosition(7);
		assertTrue(val.equalsIgnoreCase(Constants.NOTE_G));
	}

	@Test
	public void testGetNoteForPosition0() {
		String val = NoteUtils.getNoteForPosition(0);
		assertTrue(val.equalsIgnoreCase(""));
	}
	
	@Test
	public void testGetNoteForPosition8() {
		String val = NoteUtils.getNoteForPosition(8);
		assertTrue(val.equalsIgnoreCase(""));
	}
	
	@Test
	public void testGetNoteForPosition10() {
		String val = NoteUtils.getNoteForPosition(10);
		assertTrue(val.equalsIgnoreCase(""));
	}

	@Test
	public void testGetNoteForPositionNegative() {
		String val = NoteUtils.getNoteForPosition(-1);
		assertTrue(val.equalsIgnoreCase(""));
	}
	
}
