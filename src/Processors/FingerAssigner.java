package Processors;

public class FingerAssigner {

	// TODO should this be in a different package? but which one? tbd.
	
	
	//Don't drop holds / duration early on a finger when a new instruction is read - we need to "block" somehow / signal that the finger is busy and can't move to a new note yet.
	//Different hold durations and trying to swap hitting notes with different fingers? If you only have 2 fingers, and one note is holding for 4 seconds, and the other is trying to hit notes to the left and right of the holding note every 1 second, then you're stuck :^)
	// DONT TRAP / BLOCK NOTES!
	
	// problem solver framework? based on the minimum number of keys needed based on >how many notes are ever hit simultaneously?
		// if we account for lifting keys 0.15 seconds early or whatever, we should TOTAL how much "play time" is "lost" that way
			// and then see if we can decrease the "lost time" by adding more fingers, hopefully bringing the "lost" time to 0.
				// so an optimal solution would be somewhere between >least number of fingers while also having >0 lost time?
	
}
