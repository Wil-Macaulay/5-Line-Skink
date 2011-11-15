/**
 *
 *  MusicElement.java
 *
 *     0.1 9 Nov 1998 wm
 *     0.2 10 May 1999 WM
 *             Ornamentation, broken rhythm, etc.
 *     0.6 23 Jan 2001 WM
 *             symbol width for layout
 *     1.0 2 Mar 2002 WM
 *           refactor - use AbcElement interface
 *     1.1a2 2 Sep 2002 WM - handle dot count for glyph width
 *     2.0a0 7 jul 2005 recover lost HP work
 **/
package ca.celticmusic.skink;

public class MusicElement extends AbstractMusicElement {
	String pitch = null;

	boolean isTied = false;

	int octaveModifier = 0;

	int dotCount = 0;

	int headLength = 0;

	MusicLength length = null;

	int absLength = 0;

	int pitchMod = 0; // 0 no mod, -1,-2 flat, +1,+2 sharp, +9 natural (should be enum)

	public MusicElement() {
	}

	// constructors
	public MusicElement(String thePitch) {
		setPitch(thePitch);
	}

	public String elementType() {
		return "note";
	}

	// this kind of element needs to be written on a staff   

	public boolean needsStaff() {
		return true;
	}

	public void dotIt() // dotted first of broken rhythm (note: need to set up for second note)
	{
		dotCount += 1;
	}

	public void dashIt() // second component is dotted
	{
		dotCount -= 1;
	}

	public void setTied() {
		isTied = true;
		symbol = symbol + "-";
	}

	public boolean isTied() {
		return isTied;
	}

	public void sharpen() {
		pitchMod = 1;
		symbol = "^" + symbol;
	}

	public void doubleSharpen() {
		symbol = "^^" + symbol;
		pitchMod = 2;
	}

	public void flatten() {
		symbol = "_" + symbol;
		pitchMod = -1;
	}

	public void doubleFlatten() {
		symbol = "__" + symbol;
		pitchMod = -2;
	}

	public void natural() {
		symbol = "=" + symbol;
		pitchMod = 9;
	}

	public String asString() {
		String theString = "";
		if (myOrnaments != null)
			theString += myOrnaments.asString();
		theString += symbol;
		return theString;
	}

	// usually the contents are the string (except for guitar chords)   
	public String getContents() {
		return asString();
	}

	public void upOctave() {
		octaveModifier += 1;
		symbol = symbol + "'";
	}

	public void downOctave() {
		octaveModifier -= 1;
		symbol = symbol + ",";
	}

	public int getOctave() {
		return octaveModifier;
	}

	// set the pitch 
	public void setPitch(String thePitch) {
		String scaleNote=thePitch.substring(0,1);
		Debug.output(2, "Setting pitch" + thePitch);
		isAudible = true;
		symbol = symbol + thePitch.substring(0,1); // add pitch to symbol before we lowercase
		pitch = scaleNote.toLowerCase();
		// now check to see if it was already in the lower octave (uppercase) and decrement octaveModifier
		if (!scaleNote.equals(pitch))
			octaveModifier -= 1;
		// now look for octave modifiers in the string - usually only if a result of 'middle='
		for (int i = 1;i<thePitch.length();i++ ){
			if(thePitch.charAt(i)=='\'')octaveModifier +=1;
			if(thePitch.charAt(i)==',')octaveModifier -=1;
			
		}
	}

	// get the scale note 
	public String getScaleNote() {
		return pitch;
	}

	// get the pitch, including octave modifiers, upper and lowercase   
	public String getPitch() {
		String thePitch = pitch;
		//System.out.println("getting pitch: pitch="+pitch+" octaveMod= "+octaveModifier);
		if (octaveModifier > 0) {
			for (int i = 0; i < octaveModifier; i++) {
				thePitch += "'";
			}
		}
		if (octaveModifier < 0) {
			//System.out.println("pitch is in lower register");
			thePitch = pitch.toUpperCase();
			for (int i = -1; i > octaveModifier; i--) {
				thePitch += ",";
			}
		}
		return thePitch;
	}

	// find the vertical offset from the clef midline 
	// note that abs note number is baselined at 0 for c above middle C, so we add one to get offset
	// from B for treble
	public int findVerticalOffset(int midNote) {

		return getAbsNoteNumber() - midNote; 
	}

	public int getAbsNoteNumber() {
		String pitches = "cdefgab";
		int i = pitches.indexOf(getScaleNote()) + 7 * getOctave();
		Debug.output(1, "AbsNoteNumber: " + i);
		return i;
	}

	/**
	 * update the player context with respect to keysig, accidentals, etc
	 */

	public void updatePlayerContext(Player player) {
		if (pitchMod != 0) {
			player.setAccidental(getRawMidi(), pitchMod);
		}
	};

	/**
	 * play myself on a particular player
	 *
	 * @param player  Player object to sequence the note
	 * @param atTick  current tick count
	 * @param stretch stretch factor (triplets, etc)
	 * @param curKey  current key signature in effect
	 * @return duration of note in ticks
	 */

	public int play(Player player, int atTick, double stretch, KeySig curKey) {
		if (absLength == 0)
			return 0;
		int duration = (int) (absLength * stretch);
		int midiNote = getMidi(curKey, player);
		// just set the start tick of the tied series of notes
		if (isTied) {
			player.makeTiedNote(midiNote, atTick, duration);
		} else {
			player.makeNote(midiNote, atTick, duration);
		}
		Debug.output(3, "playing note " + getPitch() + "MIDI val"
				+ getMidi(curKey, player) + " at " + atTick);

		return duration;
	}

	/** 
	 * get the raw midi note number, disregarding accidentals
	 */
	public int getRawMidi() {
		String midiNotes = "c.d.ef.g.a.b";
		return (midiNotes.indexOf(getScaleNote()) + 12 * getOctave() + 72);
	}

	/**
	 * get the midi note number: middle C = C = 60
	 */

	public int getMidi(KeySig k, Player player) {
		// now check for accidentals in force. Assume already set player context
		int i = getRawMidi();
		int acc = player.accidentalOn(i);
		switch (acc) {
		case 0:
			// no explicit accidental, check keysig
			int sf = k.sharpOrFlatPlayed(getScaleNote());
			if (sf == 9)
				sf = 0; // FIXME kludge for explicit accidental in keysig
			i += sf;
			break;
		case 9:
			break; // natural
		case -1:
		case -2:
		case +1:
		case +2:
			i += acc;
			break;
		default: // shouldn't happen
			break;
		}
		return i;

	}

	/** 
	 * set grace note length
	 */
	protected void setGraceNoteLength(int graceNoteLength) {
		Ornaments o = getOrnaments();
		// update length of gracenotes first
		if (o != null) {
			Grace gr = o.getGraceNotes();
			if (gr != null) {
				gr.setAbsLength(graceNoteLength, 0); // since gracenotes are notes
			}
		}
	}

	/**
	 * set the absolute length based on the default length of the line   
	 * also reset dotCount 
	 *
	 */
	// FIXME refactor music elements w.r.t audible/inaudible              
	public void setAbsLength(int defaultLength, int graceNoteLength) {
		setGraceNoteLength(graceNoteLength);
		if (!isAudible) {
			absLength = 0;
			return;
		}

		int myLen = 0;
		if (length != null)
			myLen = length.getAbs(defaultLength);
		else {
			myLen = defaultLength;
		}
		int increm = myLen / 2;
		// handle broken rhythms
		Debug.output(1, "dotCount" + dotCount);
		if (dotCount > 0)
			for (int i = 0; i < dotCount; i++) {
				myLen += increm;
				increm /= 2;
			}
		if (dotCount < 0)
			for (int i = 0; i > dotCount; i--) {
				myLen -= increm;
				increm /= 2;
			}
		Debug.output(1, "length" + myLen);
		absLength = myLen;
		headLength = headDuration();
		dotCount = numDots();
	}

	// find head length         
	private int headDuration() {
		// notes longer than a longa get displayed as longa
		int dur = SkinkConstants.LONGA_LEN;
		while (dur > absLength) {
			dur /= 2;
		}
		Debug.output(3, "note length " + absLength + " head " + dur);
		return dur;
	}

	private int numDots() { // note, only works properly for real dotted lengths (ignores remainders)
		int nDots = 0;
		int dur = headLength;
		int remain = absLength - dur;
		if (remain >= SkinkConstants.LONGA_LEN)
			return 0; // avoid loop
		while (remain > 0) {
			nDots += 1;
			dur = dur / 2;
			remain -= dur;
		}
		Debug.output(3, "note length " + absLength + " number of dots..."
				+ nDots);
		return nDots;
	}

	public int getHeadLength() {
		return headLength;
	}

	public int getDotCount() {
		return dotCount;
	}

	public int getAbsLength() {
		return absLength;
	}

	public int getNumTails() {
		int numTail = 1;
		for (int i = SkinkConstants.EIGHTH_LEN; i > headDuration(); i /= 2) {
			numTail++;
		}
		return numTail;
	}

	/**
	 * get the offset from the left of the glyph where the notehead will be drawn
	 */
	public int getReferencePoint() {
		int refP = 0;
		if (myOrnaments != null) {
			Grace g = myOrnaments.getGraceNotes();
			if (g != null) {
				refP += g.size() * 8 + 8;
			}
			if (pitchMod != 0) {
				refP += 8;
			} // accidental
		}
		return refP;
	}

	// FIXME need an abstract way to do this.      
	public int glyphWidth() {
		//  set the relative size of the glyph for layout
		int len = absLength;
		// slight differences between sizes
		int symSize = 14; // breve
		if (len < (64 / 4))
			symSize = 8; // less than quarter note
		if (len < (64 / 2))
			symSize = 9; // less than half note
		if (len < 64)
			symSize = 10; // less than whole
		if (len < (64 * 2))
			symSize = 12; // less than breve
		//reference point is past gracenotes, accidentals
		symSize += getReferencePoint();
		// dotted?
		if (dotCount > 0)
			symSize += (dotCount * 11); //4);
		return symSize;
	}

	public int getAccidental() {
		return pitchMod;
	}

	public void setLength(MusicLength theLength) {
		length = theLength;
		symbol += theLength.asString();
	}
/**
 * paint this note on a panel
 * @param p panel on which to paint the note
 * @param advance do we advance the painting position after painting?
 */
	public void paint(StaffPainter p, boolean advance) {
		p.paintNote(this, advance);
		if (advance) {
			p.advance(glyphWidth(), false, bpThis);
		}
	}

	/**
	 * paint slurs, etc
	 */
	public void paintMarkup(StaffPainter p) {
		// paint decorations
		super.paintMarkup(p);
		p.updateSlurContour(this);
		if (slurEnd > 0) {
			p.slurEnd(this, slurEnd);
		}
		if (slurBegin > 0) {
			p.slurBegin(this, slurBegin);
		}
	}

	/**
	 * update line context - broken rhythm in progress, etc
	 */
	public void updateLineContext(Line theLine) {
		//if (isAudible) {
			theLine.setLastAudibleElement(this);
			Debug.output(1, "set last audible");
			// broken rhythm affects audible (notes, rests, and invisible audibles)
			BrokenRhythmElement be = theLine.getBrokenRhythmElement();
			if (be != null) {
				Debug.output(1, "doBroken");
				be.doBroken(theLine);
			}
		//}
		// slur in progress?
		beginSlur(theLine.getSlursStarting());
		theLine.clearSlursStarting();
		// ornaments and guitar chords?
		myOrnaments = theLine.getOrnaments();
		theLine.setOrnaments(null);
		// complex ornaments
		myDecorations = theLine.getDecorations(); // sets null as a side effect	
		// gracenotes in progress?
		Grace gr = theLine.getGrace();
		if (gr != null) {
			if (myOrnaments == null) {
				myOrnaments = new Ornaments();
			}
			myOrnaments.setGraceNotes(gr);
			theLine.setGrace(null);
		}

		setAbsLength(theLine.getDefaultNoteDuration(), theLine.getGraceNoteLength()); // set the absolute length
	}
	
	protected BarPosition updateBarPosition(BarPosition bpCurrent){
		bpCurrent.incrementPulse(this.getAbsLength());
		Debug.output(debugLevel, "MusicElement.updateBarPosition: after update bpCurrent ="
				+bpCurrent);

		return bpCurrent;
	}
	 protected void setBarPosition(BarPosition bpCurrent) {
	
		bpCurrent.normalizePulse();
		BarPosition bpNew = new BarPosition(bpCurrent);
		bpThis = bpNew;

		
	}

}
