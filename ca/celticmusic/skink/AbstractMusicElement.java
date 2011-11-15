/**
 *
 *  AbstractMusicElement.java
 *
 *     0.1 9 Nov 1998 wm
 *     0.2 10 May 1999 WM
 *             Ornamentation, broken rhythm, etc.
 *     0.6 23 Jan 2001 WM
 *             symbol width for layout
 *     1.0 2 Mar 2002 WM
 *           refactor - use AbcElement interface
 *     1.0d1 Sep 17 2002 WM
 *           add isAudible()
 **/
package ca.celticmusic.skink;

import java.util.List;

public abstract class AbstractMusicElement implements AbcElement
{
	String symbol="";
	String pitch=null;
	boolean isAudible=false;
	boolean isVisible=true;
	boolean fixedAdvance = false;
	Ornaments myOrnaments = null; // ornaments are more closely bound
	java.util.List myDecorations = null;    // than decorations
	// after the first paint pass, update the elements with their position 
	double xRef = 0;  // reference position 
	double yRef = 0;
	boolean tailUp = true;
	double yOffset = 0; // offset of end of tail FIXME - this probably should be a rectangle around the element

	int slurBegin = 0;  // note begins a slur (one or more)
	int slurEnd = 0;    // note ends a slur
	BarPosition bpThis = null;       
	int debugLevel = 3;


	/**
	 * element type String identifier (abstract)
	 */   

	public abstract String elementType();
	/**
	 * Does this kind of element need to be written on a staff   
	 */

	public boolean isAudible(){
		return isAudible;
	}

	/**
	 * begin a slur
	 */
	public void beginSlur(int numSlurs){
		slurBegin+=numSlurs; 
	}

	/** 
	 * end a slur
	 */

	public void endSlur(int numSlurs){
		slurEnd+=numSlurs;
	} 

	public abstract boolean needsStaff();
	/**
	 * Set the reference position of the element after the first pass
	 */
	public void setReferencePosition(double xRef, double yRef, boolean tailUp, double yOffset){
		this.xRef = xRef;
		this.yRef = yRef;
		this.tailUp = tailUp;
		this.yOffset = yOffset;
	}
	public double getReferenceX(){
		return xRef;
	}
	public double getReferenceY(){
		return yRef;
	}
	public boolean isTailUp(){
		return tailUp;
	}
	public double getYOffset(){
		return yOffset;
	}
	public double getYmax(){
		if (tailUp) {
			return yRef + StaffPanel.spaceHeight;
		}
		else{
			return yRef + yOffset + StaffPanel.spaceHeight;
		}
	}
	public double getYmin(){
		if (tailUp){
			return yRef - yOffset;
		}
		else{
			return yRef;
		}
	}

	public void setDecorations(java.util.List theDecos){
		myDecorations = theDecos;
	}

	public void setOrnaments(Ornaments theOrnaments){
		myOrnaments = theOrnaments;
	}

	public Ornaments getOrnaments(){
		return myOrnaments;
	}

	public void setSymbol(String theSymbol) {
		symbol = theSymbol;
	}

	public String getSymbol(){
		return symbol;
	}

	public void doUnroll(Unroller u)
	{
		u.advance(this);
	}
	public String toString(){
		return asString();
	}
	public String asString(){
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

	public void setAbsLength(int defaultLen,int graceNoteLen){};     


	/**
	 * update the player context with respect to keysig, accidentals, tempo, etc
	 */

	public void updatePlayerContext(Player player){};


	/**
	 * play myself on a particular player
	 *
	 * @param player  Player object to sequence the note
	 * @param atTick  current tick count
	 * @param stretch stretch factor (triplets, etc)
	 * @param curKey  current key signature in effect
	 * @returns duration of note in ticks
	 */

	public int play(Player player,int atTick, double stretch,KeySig curKey){
		return getAbsLength();
	};

	public int getAbsLength(){
		return 0;
	};  
	/**
	 * default paint myself on a staff panel ending beam and calling doPaint
	 */

	public void paint(StaffPainter p, boolean advance)
	{
		Debug.output(3,"Paint: element type "+elementType());
		doPaint(p,advance);
		p.advance(glyphWidth(),fixedAdvance, bpThis);
	};

	public void doPaint(StaffPainter p, boolean advance){
	};    
	/**
	 * Get painting metrics 
	 */
	public BarPosition getMetrics(AbcLineMetrics m,BarPosition bpCurrent){
		// set the current drawing position for the current glyph
		Debug.output(debugLevel, "AbstractMusicElement.getMetrics: "
				+this.toString() + " bpCurrent ="
				+bpCurrent);
		setBarPosition(bpCurrent);
		if (glyphWidth() > 0){
			m.addGlyph(glyphWidth(),fixedAdvance,bpCurrent, getSymbol());
		}
		// update for next glyph
		return updateBarPosition(bpCurrent);

	}
	public BarPosition getBarPosition(){
		return bpThis;
	}
	/**
	 * paint markup - any element could have markup, but only if it grabbed it from
	 * the line context
	 */
	public void paintMarkup(StaffPainter p){
		// paint decorations
		java.util.List ornaments = null;
		List<GuitarChordElement> gcUp = null;
		List<GuitarChordElement> gcDown = null;		
		List<GuitarChordElement> gcLeft = null;		
		List<GuitarChordElement> gcRight = null;		
		if (myOrnaments!=null){
			ornaments = myOrnaments.getOrnaments();
			gcUp = myOrnaments.getGuitarChordsUp();
			gcDown = myOrnaments.getGuitarChordsDown();
			gcRight = myOrnaments.getGuitarChordsRight();
			gcLeft = myOrnaments.getGuitarChordsLeft();
			
		}
		p.drawDecos(getReferenceX(),getReferenceY(),getYmax(),getYmin(),
				isTailUp(),ornaments,myDecorations,gcUp, gcLeft, gcDown, gcRight);
		
		
	}	 

	/**
	 * abstract method to get the glyph width
	 */

	abstract public int glyphWidth();  
	public void updateLineContext(Line theLine){};  
	abstract protected BarPosition updateBarPosition(BarPosition bpCurrent);
	abstract protected void setBarPosition(BarPosition bpCurrent); 
}

