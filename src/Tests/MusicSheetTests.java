package Tests;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import DataObjs.MusicSheet;
import Processors.AlcReader;

public class MusicSheetTests {
	
	@Test
	public void testGCD() {
		try {
			MusicSheet sheet;
			int gcd;
			AlcReader ar = new AlcReader();

			sheet = ar.loadAlcFile(".\\sample musixcml\\for unit tests\\my confession.alc");
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
			AlcReader ar = new AlcReader();

			sheet = ar.loadAlcFile(".\\sample musixcml\\for unit tests\\my confession-diffgcd.alc");
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
			AlcReader ar = new AlcReader();

			sheet = ar.loadAlcFile(".\\sample musixcml\\for unit tests\\my confession.alc");
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
			AlcReader ar = new AlcReader();

			sheet = ar.loadAlcFile(".\\sample musixcml\\for unit tests\\my confession-diffenddur.alc");
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
}
