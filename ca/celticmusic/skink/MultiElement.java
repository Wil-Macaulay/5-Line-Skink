// MultiElement.java
/*******
 *
 * MultiElement - baseclass for multiple notes (chord or tuplet or grace)
 *            6 May 1999 WM
 *            30 Jan 2002 WM  - support for chords within broken
 *			  16 may 2004 WM  - better calculation of width, etc
 *           17 oct 2005 WM - updatePlayerContext
 *           3 jan 2008 WM - revamped positioning
 *
 *******/
package ca.celticmusic.skink;

import java.util.*;

public class MultiElement extends MusicElement {
	List<AbcElement> theElements = new ArrayList<AbcElement>();

	String startDelim = "[";

	String endDelim = "]";

	public int glyphWidth() { // works for chords but not for tuplets

		int width = 0;
		
		for (AbcElement theElement:theElements) {
		
			int w = theElement.glyphWidth();
			if (w > width)
				width = w;
		}
		return width;
	}

	// the reference point is where the notehead or other 'major' part of the glyph will be drawn
	public int getReferencePoint() {
		int refP = 0;
		for (AbcElement theElement:theElements) {
			int r = ((MusicElement) theElement).getReferencePoint();
			if (r > refP)
				refP = r;
		}
		return refP;

	}

	/**
	 * handle markup (slurs etc) - delegate to the first element
	 */

	public AbcElement firstElement() {
		return (AbcElement) theElements.get(0);
	}

	public AbcElement lastElement() {
		if (theElements.size() <= 0)
			return null;
		return (AbcElement) theElements.get(theElements.size() - 1);
	}

	public String elementType() {
		return "mult";
	}

	public void addElement(AbcElement theElement) {
		Debug.output(3, "adding element(multi)");
		theElements.add(theElement);
	}

	public int size() {
		return theElements.size();
	}

	public List getElements() {
		return theElements;
	}

	public void setAbsLength(int defaultLength, int graceNoteLength) {
		// grace notes belong to the chord
		setGraceNoteLength(graceNoteLength);
		absLength = 0;
		for (AbcElement theElement:theElements){
			theElement.setAbsLength(defaultLength, graceNoteLength);
			absLength = Math.max(absLength, theElement.getAbsLength());
		}
	}

	public String asString() {
		String theString = startDelim;
		for(AbcElement a: theElements){
			theString += a.asString();
		}
		theString += endDelim;
		return theString;
	}

	public void dotIt() {
		Debug.output(2, "Chord dot-it");
		for (AbcElement theElement:theElements){
			((MusicElement) theElement).dotIt();
		}
	}

	public void dashIt() {
		Debug.output(2, "Chord dash-it");
		for (AbcElement theElement:theElements) {
			((MusicElement) theElement).dashIt();
		}
	}

	/**
	 * play the notes of a chord
	 * @returns the duration of the first element
	 */

	public int play(Player player, int atTick, double stretch, KeySig curKey) {
		int duration = 0; // will end up being the duration of the last element
		int dur = 0;

		for (AbcElement theElement:theElements) {
			dur = theElement.play(player, atTick, stretch, curKey);
			if (duration == 0)
				duration = dur;
		}
		return duration;
	}

	// assume audible, for now

	public boolean isAudible() {
		return true;
	}

	// FIXME because of the way chords within a beam are set up this is a kludge  
	public double getReferenceX() {
		double x = super.getReferenceX();
		if (x == 0) {
			x = firstElement().getReferenceX();
		}
		return x;
	}

	public double getReferenceY() {
		double y = super.getReferenceY();
		if (y == 0) {
			y = firstElement().getReferenceY();
		}
		return y;
	}

	public boolean isTailUp() {
		return firstElement().isTailUp();
	}

	public double getYmax() {
		return firstElement().getYmax();
	}

	public double getYmin() {
		return firstElement().getYmin();
	}

	public void paint(StaffPainter p, boolean advance) {
		p.paintChord(this);
		p.advance(glyphWidth(), false, bpThis);
	}

	public void setReferencePosition(double xRef, double yRef, boolean tailUp,
			double yOffset) {
		super.setReferencePosition(xRef, yRef, tailUp, yOffset);
		firstElement().setReferencePosition(xRef, yRef, tailUp, yOffset);
	}

	/**
	 *  update accidentals on, etc
	 *  @param player the player context to update
	 */
	public void updatePlayerContext(Player player) {
		for (AbcElement theElement:theElements) {
			theElement.updatePlayerContext(player);
		}
	}

}