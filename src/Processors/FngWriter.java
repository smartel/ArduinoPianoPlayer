package Processors;

public class FngWriter {

	// TODO what input are we expecting here?
	// Alc file, no? We can load that directly into a musicsheet with AlcReaderWriter... so we arent reinventing the wheel.
	
	
	// TODO
	// 	ARDUINO: just 2 states, up, down. Dont need durations. Just a script of waits and a key up / key down (even just toggle?) Instruction. Next goal can just be to print that pseudo
	// 	So we need a collection of fingers added to the pseudo arduino, as in, Finger 6 - CV 14.5 (G2 sharp) - Toggle (from depressing to releasing)
	// TODO handle outputting instructions here too? should we always do it, and its usually 1:1 ratio (full) or limited? like, always output 2 files? .fng and .fngconf?
	
	// TODO if a note is not in range, display an error but DONT WRITE IT TO THE FNG FILE I DONT NEED IT TO BREAK!!!
	// Alc files are meant to be translations of the musicxml / midi / what have you, WITHOUT exclude notes that arent in range.
	// It is in the fng file that must exclude notes out of range.
	// Is that worth putting in documentation somewhere? ALC files have all notes, FNG files will be piano specific. 
	
	// TODO flag for including LED commands with solenoid push/retracts?
	
	// TODO note to self: this is crucial, absolutely crucial, so we don't stack pushes on top of each other and idk, break or fry a finger / arduino part.
	//      you know how sometimes a duplicate is thrown out while a song is playing?
	//      that duplicate would probably appear in finger pseudo code as something like the below:
	//      at time 2000   finger 1   cv 40    push 500
	//      at time 2200   finger 1   cv 40    push 500
	//      at time 2500   finger 1   cv 40    release
	//      at time 2700   finger 1   cv 40    release
	// do you see the issue? we stacked a push on top of a push. we need to have a sanity check programmatically that catches double pushes, so we can work with it from there.
	// the sane thing to do is what, change the stacked push into a >release and then >push immediately afterwards?
	// whatever the fix you do here is, I would really like it to be consistent with the pianofeigner.
	
	// TODO if we have linear tracks for fingers, (as in: finger A: down at 1100, up at 1300, down at 1600, up at 1900), then it'd be easy to see two stacked commands by eye, ala:
	//      down 1000, down 1300, up 1500, up 1600
	//      but I think we need a linear list of all fingers in order, like finger A down 1100, finger b down 1200, ... for the machine to play it
}
