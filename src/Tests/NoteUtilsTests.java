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
		// new rules made octave 0 valid per MIDI (see CompVal Chart), so this will return a valid compVal
		assertTrue(validCompVal);
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
		assertTrue(val.equalsIgnoreCase(Constants.NOTE_C));
	}

	@Test
	public void testGetNoteForPosition2() {
		String val = NoteUtils.getNoteForPosition(2);
		assertTrue(val.equalsIgnoreCase(Constants.NOTE_D));
	}
	
	@Test
	public void testGetNoteForPosition3() {
		String val = NoteUtils.getNoteForPosition(3);
		assertTrue(val.equalsIgnoreCase(Constants.NOTE_E));
	}
	
	@Test
	public void testGetNoteForPosition4() {
		String val = NoteUtils.getNoteForPosition(4);
		assertTrue(val.equalsIgnoreCase(Constants.NOTE_F));
	}
	
	@Test
	public void testGetNoteForPosition5() {
		String val = NoteUtils.getNoteForPosition(5);
		assertTrue(val.equalsIgnoreCase(Constants.NOTE_G));
	}
	
	@Test
	public void testGetNoteForPosition6() {
		String val = NoteUtils.getNoteForPosition(6);
		assertTrue(val.equalsIgnoreCase(Constants.NOTE_A));
	}
	
	@Test
	public void testGetNoteForPosition7() {
		String val = NoteUtils.getNoteForPosition(7);
		assertTrue(val.equalsIgnoreCase(Constants.NOTE_B));
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
	
	
	
	// getNextNoteCV tests

	@Test
	public void testGetNextNoteCV1() { // C
		double val = NoteUtils.getNextNoteCV(1);
		assertTrue(val == 1.5);
	}

	@Test
	public void testGetNextNoteCV1Point5() { // C sharp aka D flat
		double val = NoteUtils.getNextNoteCV(1.5);
		assertTrue(val == 2);
	}

	@Test
	public void testGetNextNoteCV2() { // D
		double val = NoteUtils.getNextNoteCV(2);
		assertTrue(val == 2.5);
	}

	@Test
	public void testGetNextNoteCV3() { // E which will go to F since there's no E sharp
		double val = NoteUtils.getNextNoteCV(3);
		assertTrue(val == 4);
	}

	@Test
	public void testGetNextNoteCV3Point5() { // E sharp which should get marked invalid
		double val = NoteUtils.getNextNoteCV(3.5);
		assertTrue(val == -1);
	}

	@Test
	public void testGetNextNoteCV4() { // F
		double val = NoteUtils.getNextNoteCV(4);
		assertTrue(val == 4.5);
	}

	@Test
	public void testGetNextNoteCV4Point5() { // F sharp aka G flat
		double val = NoteUtils.getNextNoteCV(4.5);
		assertTrue(val == 5);
	}

	@Test
	public void testGetNextNoteCV5() { // G
		double val = NoteUtils.getNextNoteCV(5);
		assertTrue(val == 5.5);
	}

	@Test
	public void testGetNextNoteCV6() { // A
		double val = NoteUtils.getNextNoteCV(6);
		assertTrue(val == 6.5);
	}

	@Test
	public void testGetNextNoteCV6Point5() { // A sharp aka B flat
		double val = NoteUtils.getNextNoteCV(6.5);
		assertTrue(val == 7);
	}

	@Test
	public void testGetNextNoteCV7() { // B which will skip straight to C since there's no B sharp
		double val = NoteUtils.getNextNoteCV(7);
		assertTrue(val == 8);
	}

	@Test
	public void testGetNextNoteCV7Point5() { // B sharp which should get marked invalid
		double val = NoteUtils.getNextNoteCV(7.5);
		assertTrue(val == -1);
	}

	@Test
	public void testGetNextNoteCV8() { // C on 2nd octave
		double val = NoteUtils.getNextNoteCV(8);
		assertTrue(val == 8.5);
	}

	@Test
	public void testGetNextNoteCV8Point5() { // C sharp aka D flat on 2nd octave
		double val = NoteUtils.getNextNoteCV(8.5);
		assertTrue(val == 9);
	}

	@Test
	public void testGetNextNoteCV16() { // D on higher octave
		double val = NoteUtils.getNextNoteCV(16);
		assertTrue(val == 16.5);
	}

	@Test
	public void testGetNextNoteCV16Point5() { // D sharp aka E flat on higher octave
		double val = NoteUtils.getNextNoteCV(16.5);
		assertTrue(val == 17);
	}
	
	@Test
	public void testGetNextNoteCV27() { // A on even higher octave
		double val = NoteUtils.getNextNoteCV(27);
		assertTrue(val == 27.5);
	}
	
	@Test
	public void testGetNextNoteCV27Point5() { // A sharp aka G flat on even higher octave
		double val = NoteUtils.getNextNoteCV(27.5);
		assertTrue(val == 28);
	}
	
	@Test
	public void testGetNextNoteCV28() { // B meaning even higher octave + 1 note
		double val = NoteUtils.getNextNoteCV(28);
		assertTrue(val == 29);
	}
	
	@Test
	public void testGetNextNoteCV31() { // E meaning even higher octave + a few notes, just making sure E's not-having-a-sharp is fine
		double val = NoteUtils.getNextNoteCV(31);
		assertTrue(val == 32);
	}
	
	@Test
	public void testGetNextNoteCV32() { // F meaning even higher octave + a few more notes, just making sure F having-a-sharp is fine
		double val = NoteUtils.getNextNoteCV(32);
		assertTrue(val == 32.5);
	}
	
	@Test
	public void testGetNextNoteCV40Point5() { // G sharp aka A flat on a super high octave
		double val = NoteUtils.getNextNoteCV(40.5);
		assertTrue(val == 41);
	}
	
	@Test
	public void testGetNextNoteCVZero() { // 0, so it should be invalid
		double val = NoteUtils.getNextNoteCV(0);
		assertTrue(val == -1);
	}
	
	@Test
	public void testGetNextNoteCVNegative() { // negative, so it should be invalid
		double val = NoteUtils.getNextNoteCV(-10);
		assertTrue(val == -1);
	}
	
	
	
	// getPrevNoteCV tests

	@Test
	public void testGetPrevNoteCV1Point5() { // C sharp aka D flat
		double val = NoteUtils.getPrevNoteCV(1.5);
		assertTrue(val == 1);
	}

	@Test
	public void testGetPrevNoteCV2() { // D
		double val = NoteUtils.getPrevNoteCV(2);
		assertTrue(val == 1.5);
	}

	@Test
	public void testGetPrevNoteCV3() { // E
		double val = NoteUtils.getPrevNoteCV(3);
		assertTrue(val == 2.5);
	}

	@Test
	public void testGetPrevNoteCV3Point5() { // E sharp which should get marked invalid
		double val = NoteUtils.getPrevNoteCV(3.5);
		assertTrue(val == -1);
	}

	@Test
	public void testGetPrevNoteCV4() { // F which will go to E since there's no E sharp
		double val = NoteUtils.getPrevNoteCV(4);
		assertTrue(val == 3);
	}

	@Test
	public void testGetPrevNoteCV4Point5() { // F sharp aka G flat
		double val = NoteUtils.getPrevNoteCV(4.5);
		assertTrue(val == 4);
	}

	@Test
	public void testGetPrevNoteCV5() { // G
		double val = NoteUtils.getPrevNoteCV(5);
		assertTrue(val == 4.5);
	}

	@Test
	public void testGetPrevNoteCV6() { // A
		double val = NoteUtils.getPrevNoteCV(6);
		assertTrue(val == 5.5);
	}

	@Test
	public void testGetPrevNoteCV6Point5() { // A sharp aka B flat
		double val = NoteUtils.getPrevNoteCV(6.5);
		assertTrue(val == 6);
	}

	@Test
	public void testGetPrevNoteCV7() { // B
		double val = NoteUtils.getPrevNoteCV(7);
		assertTrue(val == 6.5);
	}

	@Test
	public void testGetPrevNoteCV7Point5() { // B sharp which should get marked invalid
		double val = NoteUtils.getPrevNoteCV(7.5);
		assertTrue(val == -1);
	}

	@Test
	public void testGetPrevNoteCV8() { // C on 2nd octave which will skip straight to B since there's no B sharp
		double val = NoteUtils.getPrevNoteCV(8);
		assertTrue(val == 7);
	}

	@Test
	public void testGetPrevNoteCV8Point5() { // C sharp aka D flat on 2nd octave
		double val = NoteUtils.getPrevNoteCV(8.5);
		assertTrue(val == 8);
	}

	@Test
	public void testGetPrevNoteCV16() { // D on higher octave
		double val = NoteUtils.getPrevNoteCV(16);
		assertTrue(val == 15.5);
	}

	@Test
	public void testGetPrevNoteCV16Point5() { // D sharp aka E flat on higher octave
		double val = NoteUtils.getPrevNoteCV(16.5);
		assertTrue(val == 16);
	}
	
	@Test
	public void testGetPrevNoteCV27() { // A on even higher octave
		double val = NoteUtils.getPrevNoteCV(27);
		assertTrue(val == 26.5);
	}
	
	@Test
	public void testGetPrevNoteCV27Point5() { // A sharp aka G flat on even higher octave
		double val = NoteUtils.getPrevNoteCV(27.5);
		assertTrue(val == 27);
	}
	
	@Test
	public void testGetPrevNoteCV28() { // B meaning even higher octave + 1 note
		double val = NoteUtils.getPrevNoteCV(28);
		assertTrue(val == 27.5);
	}
	
	@Test
	public void testGetPrevNoteCV31() { // E meaning even higher octave + a few notes
		double val = NoteUtils.getPrevNoteCV(31);
		assertTrue(val == 30.5);
	}
	
	@Test
	public void testGetPrevNoteCV32() { // F meaning even higher octave + a few more notes, should skip straight to E
		double val = NoteUtils.getPrevNoteCV(32);
		assertTrue(val == 31);
	}
	
	@Test
	public void testGetPrevNoteCV40Point5() { // G sharp aka A flat on a super high octave
		double val = NoteUtils.getPrevNoteCV(40.5);
		assertTrue(val == 40);
	}
	
	@Test
	public void testGetPrevNoteCVZero() { // 0, so it should be invalid
		double val = NoteUtils.getPrevNoteCV(0);
		assertTrue(val == -1);
	}
	
	@Test
	public void testGetPrevNoteCV1() { // 1, which can't have any notes prior to it as it is the first key, so it should be invalid
		double val = NoteUtils.getPrevNoteCV(1);
		assertTrue(val == -1);
	}
	
	@Test
	public void testGetPrevNoteCVNegative() { // negative, so it should be invalid
		double val = NoteUtils.getPrevNoteCV(-10);
		assertTrue(val == -1);
	}
	
	// GetShiftedCV Tests
	
	@Test
	public void testGetShiftedCVZeroShifts() { // invalid number of shifts (0)
		double val = NoteUtils.getShiftedCV(8, 0);
		assertTrue(val == -1);
	}

	@Test
	public void testGetShiftedCVZeroCV() { // invalid starting CV (0)
		double val = NoteUtils.getShiftedCV(0, 1);
		assertTrue(val == -1);
	}

	@Test
	public void testGetShiftedCVTooHighCV() { // invalid starting CV (too high)
		double val = NoteUtils.getShiftedCV(80, 1);
		assertTrue(val == -1);
	}

	@Test
	public void testGetShiftedCVTooLowCV() { // invalid starting CV (negative)
		double val = NoteUtils.getShiftedCV(-2, 1);
		assertTrue(val == -1);
	}

	@Test
	public void testGetShiftedCVMinCVUp() { // bound checking on min cv
		double val = NoteUtils.getShiftedCV(1, 1);
		assertTrue(val == 1.5);
	}

	@Test
	public void testGetShiftedCVMinCVDown() { // bound checking on min cv
		double val = NoteUtils.getShiftedCV(1, -1);
		assertTrue(val == -1);
	}

	@Test
	public void testGetShiftedCVMaxCVUp() { // bound checking on max cv
		double val = NoteUtils.getShiftedCV(Constants.MAX_THEORETICAL_COMPARE_VALUE, 1);
		assertTrue(val == -1);
	}

	@Test
	public void testGetShiftedCVMaxCVDown() { // bound checking on max cv
		double val = NoteUtils.getShiftedCV(Constants.MAX_THEORETICAL_COMPARE_VALUE, -1);
		assertTrue(val == 76.5);
	}

	@Test
	public void testGetShiftedCVUpOneOctave() { // full octave up
		double val = NoteUtils.getShiftedCV(8, 12);
		assertTrue(val == 15);
	}

	@Test
	public void testGetShiftedCVUpTwoOctaves() { // two full octaves up, starting on a sharp
		double val = NoteUtils.getShiftedCV(13.5, 24);
		assertTrue(val == 27.5);
	}

	@Test
	public void testGetShiftedCVDownOneOctave() { // full octave down, starting on a sharp
		double val = NoteUtils.getShiftedCV(32.5, -12);
		assertTrue(val == 25.5);
	}

	@Test
	public void testGetShiftedCVDownTwoOctaves() { // two full octaves down
		double val = NoteUtils.getShiftedCV(52, -24);
		assertTrue(val == 38);
	}

	@Test
	public void testGetShiftedCVAllTheWayUp() { // from min to max
		double val = NoteUtils.getShiftedCV(1, 131);
		assertTrue(val == 77);
	}

	@Test
	public void testGetShiftedCVAllTheWayDown() { // from max to min
		double val = NoteUtils.getShiftedCV(77, -131);
		assertTrue(val == 1);
	}
	
	// the following are all sequential steps up and down (a to a#, a# to b, b to c, c to c#, c# to d, ...)
	@Test
	public void testGetShiftedCVPosC1() {
		double val = NoteUtils.getShiftedCV(8, 1);
		assertTrue(val == 8.5);
	}
	@Test
	public void testGetShiftedCVPosC2() {
		double val = NoteUtils.getShiftedCV(8, 2);
		assertTrue(val == 9);
	}
	@Test
	public void testGetShiftedCVPosC3() {
		double val = NoteUtils.getShiftedCV(8, 3);
		assertTrue(val == 9.5);
	}
	@Test
	public void testGetShiftedCVPosC4() {
		double val = NoteUtils.getShiftedCV(8, 4);
		assertTrue(val == 10);
	}
	@Test
	public void testGetShiftedCVPosC5() {
		double val = NoteUtils.getShiftedCV(8, 5);
		assertTrue(val == 11);
	}
	@Test
	public void testGetShiftedCVPosC6() {
		double val = NoteUtils.getShiftedCV(8, 6);
		assertTrue(val == 11.5);
	}
	@Test
	public void testGetShiftedCVPosC7() {
		double val = NoteUtils.getShiftedCV(8, 7);
		assertTrue(val == 12);
	}
	@Test
	public void testGetShiftedCVPosC8() {
		double val = NoteUtils.getShiftedCV(8, 8);
		assertTrue(val == 12.5);
	}
	@Test
	public void testGetShiftedCVPosC9() {
		double val = NoteUtils.getShiftedCV(8, 9);
		assertTrue(val == 13);
	}
	@Test
	public void testGetShiftedCVPosC10() {
		double val = NoteUtils.getShiftedCV(8, 10);
		assertTrue(val == 13.5);
	}
	@Test
	public void testGetShiftedCVPosC11() {
		double val = NoteUtils.getShiftedCV(8, 11);
		assertTrue(val == 14);
	}
	@Test
	public void testGetShiftedCVPosC12() {
		double val = NoteUtils.getShiftedCV(8, 12);
		assertTrue(val == 15);
	}
	@Test
	public void testGetShiftedCVPosC13() {
		double val = NoteUtils.getShiftedCV(8, 13);
		assertTrue(val == 15.5);
	}
	@Test
	public void testGetShiftedCVNegC1() {
		double val = NoteUtils.getShiftedCV(36, -1);
		assertTrue(val == 35);
	}
	@Test
	public void testGetShiftedCVNegC2() {
		double val = NoteUtils.getShiftedCV(36, -2);
		assertTrue(val == 34.5);
	}
	@Test
	public void testGetShiftedCVNegC3() {
		double val = NoteUtils.getShiftedCV(36, -3);
		assertTrue(val == 34);
	}
	@Test
	public void testGetShiftedCVNegC4() {
		double val = NoteUtils.getShiftedCV(36, -4);
		assertTrue(val == 33.5);
	}
	@Test
	public void testGetShiftedCVNegC5() {
		double val = NoteUtils.getShiftedCV(36, -5);
		assertTrue(val == 33);
	}
	@Test
	public void testGetShiftedCVNegC6() {
		double val = NoteUtils.getShiftedCV(36, -6);
		assertTrue(val == 32.5);
	}
	@Test
	public void testGetShiftedCVNegC7() {
		double val = NoteUtils.getShiftedCV(36, -7);
		assertTrue(val == 32);
	}
	@Test
	public void testGetShiftedCVNegC8() {
		double val = NoteUtils.getShiftedCV(36, -8);
		assertTrue(val == 31);
	}
	@Test
	public void testGetShiftedCVNegC9() {
		double val = NoteUtils.getShiftedCV(36, -9);
		assertTrue(val == 30.5);
	}
	@Test
	public void testGetShiftedCVNegC10() {
		double val = NoteUtils.getShiftedCV(36, -10);
		assertTrue(val == 30);
	}
	@Test
	public void testGetShiftedCVNegC11() {
		double val = NoteUtils.getShiftedCV(36, -11);
		assertTrue(val == 29.5);
	}
	@Test
	public void testGetShiftedCVNegC12() {
		double val = NoteUtils.getShiftedCV(36, -12);
		assertTrue(val == 29);
	}
	@Test
	public void testGetShiftedCVNegC13() {
		double val = NoteUtils.getShiftedCV(36, -13);
		assertTrue(val == 28);
	}
	

	
	// VerifyValidNonRestCompareValue tests
	
	@Test
	public void testVerifyValidCompVal0() {
		boolean isValid = NoteUtils.verifyValidNonRestCompareValue(Constants.REST_COMP_VALUE);
		assertFalse(isValid);
	}
	
	@Test
	public void testVerifyValidCompValMinBounds() {
		boolean isValid = NoteUtils.verifyValidNonRestCompareValue(Constants.MIN_THEORETICAL_COMPARE_VALUE);
		assertTrue(isValid);
	}
	
	@Test
	public void testVerifyValidCompValBelowMinBounds1() {
		boolean isValid = NoteUtils.verifyValidNonRestCompareValue(Constants.MIN_THEORETICAL_COMPARE_VALUE-1);
		assertFalse(isValid);
	}

	@Test
	public void testVerifyValidCompValBelowMinBounds2() {
		boolean isValid = NoteUtils.verifyValidNonRestCompareValue(Constants.MIN_THEORETICAL_COMPARE_VALUE-0.5);
		assertFalse(isValid);
	}
	
	@Test
	public void testVerifyValidCompValMaxBounds() {
		boolean isValid = NoteUtils.verifyValidNonRestCompareValue(Constants.MAX_THEORETICAL_COMPARE_VALUE);
		assertTrue(isValid);
	}
	
	@Test
	public void testVerifyValidCompValAboveMaxBounds1() {
		boolean isValid = NoteUtils.verifyValidNonRestCompareValue(Constants.MAX_THEORETICAL_COMPARE_VALUE+1);
		assertFalse(isValid);
	}

	@Test
	public void testVerifyValidCompValAboveMaxBounds2() {
		boolean isValid = NoteUtils.verifyValidNonRestCompareValue(Constants.MAX_THEORETICAL_COMPARE_VALUE+0.5);
		assertFalse(isValid);
	}
	
	// the span of one octave, including B# and E# which will be marked invalid
	@Test
	public void testVerifyValidCompValC() {
		boolean isValid = NoteUtils.verifyValidNonRestCompareValue(8);
		assertTrue(isValid);
	}
	@Test
	public void testVerifyValidCompValCSharp() {
		boolean isValid = NoteUtils.verifyValidNonRestCompareValue(8.5);
		assertTrue(isValid);
	}
	@Test
	public void testVerifyValidCompValD() {
		boolean isValid = NoteUtils.verifyValidNonRestCompareValue(9);
		assertTrue(isValid);
	}
	@Test
	public void testVerifyValidCompValDSharp() {
		boolean isValid = NoteUtils.verifyValidNonRestCompareValue(9.5);
		assertTrue(isValid);
	}
	@Test
	public void testVerifyValidCompValE() {
		boolean isValid = NoteUtils.verifyValidNonRestCompareValue(10);
		assertTrue(isValid);
	}
	@Test
	public void testVerifyValidCompValESharp() {
		boolean isValid = NoteUtils.verifyValidNonRestCompareValue(10.5);
		assertFalse(isValid);
	}
	@Test
	public void testVerifyValidCompValF() {
		boolean isValid = NoteUtils.verifyValidNonRestCompareValue(11);
		assertTrue(isValid);
	}
	@Test
	public void testVerifyValidCompValFSharp() {
		boolean isValid = NoteUtils.verifyValidNonRestCompareValue(11.5);
		assertTrue(isValid);
	}
	@Test
	public void testVerifyValidCompValG() {
		boolean isValid = NoteUtils.verifyValidNonRestCompareValue(12);
		assertTrue(isValid);
	}
	@Test
	public void testVerifyValidCompValGSharp() {
		boolean isValid = NoteUtils.verifyValidNonRestCompareValue(12.5);
		assertTrue(isValid);
	}
	@Test
	public void testVerifyValidCompValA() {
		boolean isValid = NoteUtils.verifyValidNonRestCompareValue(13);
		assertTrue(isValid);
	}
	@Test
	public void testVerifyValidCompValASharp() {
		boolean isValid = NoteUtils.verifyValidNonRestCompareValue(13.5);
		assertTrue(isValid);
	}
	@Test
	public void testVerifyValidCompValB() {
		boolean isValid = NoteUtils.verifyValidNonRestCompareValue(14);
		assertTrue(isValid);
	}
	@Test
	public void testVerifyValidCompValBSharp() {
		boolean isValid = NoteUtils.verifyValidNonRestCompareValue(14.5);
		assertFalse(isValid);
	}
	@Test
	public void testVerifyValidCompValCTwo() {
		boolean isValid = NoteUtils.verifyValidNonRestCompareValue(15);
		assertTrue(isValid);
	}
	
}
