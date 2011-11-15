/**
 *
 * Line.java
 *
 *     0.1 9 Nov 1998 wm
 *     0.2   28 Apr 1999 WM  - dump()
 *     0.3   7 May 2000 wm  - default Length, meter, key in Line instead of Tune
 *     0.9  28 Dec 2001 WM  - auto tail
 *     1.0a 2 Mar 2002 WM - refactor
 *     1.1.a2 28 Sep 2003 WM - default grace note length
 *     1.1f2 10 may 2004 WM - line context 
 *
 **/
package ca.celticmusic.skink;
import java.util.*;

public class Line //implements AbcSequence
{
	List<AbcElement> theElements = new ArrayList<AbcElement>();
	int defaultLength = 0;
	KeySig theKey;
	Meter theMeter;

	// context variables
	boolean advance = true;
	boolean newKey = false;
	boolean newMeter = false;
	boolean newClef = false;
	boolean needsStaff = false;
	boolean autoTail = true;
	boolean tailsUp = false;

	int debugLevel = 1;

	int slursStarting = 0; // just processed  start slurs
	BrokenRhythmElement theBrokenElement = null;
	int graceNoteLength = SkinkConstants.EIGHTH_LEN;
	AbcElement lastAudibleElement = null;

	String theClef = "treble";
	int midLine = -1; // mid line for the clef, defaults to B

	// aligned words
	List<AlignedWords> theWordList = null;

	// decorations
	List<DecorationElement> theDecos = null;

	// decorations in progress
	Ornaments theOrnaments = null;

	// gracenotes in progress
	Grace theGrace = null;

	public Line()
	{
		defaultLength = SkinkConstants.EIGHTH_LEN;
		theKey = null;
		theMeter = null;
	}

	public Line(KeySig k, Meter m, int d)
	{
		defaultLength = d;
		theKey = k;
		theMeter = m;
	}
	public Line(Line prev){
		defaultLength = prev.defaultLength;
		theKey = prev.theKey;
		theMeter = prev.theMeter;
		theClef = prev.theClef;
		midLine = prev.midLine;
		autoTail = prev.autoTail;

	}
	public void setNoAdvance() {
		advance = false;
	}

	public boolean advance(){
		return advance;
	}   

	public void addElement(AbcElement theElement) {
		if (theElement == null) return; // don't push on an empty element
		theElements.add(theElement);
		if (theElement.needsStaff())
			needsStaff = true;
		theElement.updateLineContext(this);
	}
//	add a decoration - 'non simple' !trill!
	public void addDecoration(DecorationElement theDeco){
		if (theDeco == null) return;
		if (theDecos == null) theDecos = new ArrayList<DecorationElement>();
		theDecos.add(theDeco);
	}
	public String getClef(){
		return theClef;
	}
	public void setMidLine(String lineNote){
		MusicElement theMidLineElement = new MusicElement(lineNote);
		midLine = theMidLineElement.getAbsNoteNumber();
	}
	public int getMidLine() {
		return midLine;
	}

	public void setClef(String clef){
		theClef = clef;
		if (theClef.equals("treble")){
			setMidLine("B");
		}
		else if (theClef.equals("bass")){
			setMidLine("D,");
		}
		else if (theClef.equals("tenor")){
			setMidLine("A,");
		}
		else if (theClef.equals("alto")){
			setMidLine("C");
		}
		else {
			setMidLine("B");
		}

	}

//these are 'non-simple' decorations like !trill!
	public List<DecorationElement> getDecorations(){
		java.util.List<DecorationElement> decos = theDecos;
		theDecos = null;
		return decos;
	}

	public BrokenRhythmElement getBrokenRhythmElement(){
		return theBrokenElement;
	}

	public void setBrokenRhythmElement(BrokenRhythmElement theElement){
		theBrokenElement = theElement;
	}
	public AbcElement getLastAudibleElement(){
		return lastAudibleElement;
	}

	public void setLastAudibleElement(AbcElement theElement){
		lastAudibleElement = theElement;
	}	  

	public void appendElements(Line otherLine){
		theElements.addAll(otherLine.getElements());
	}

	public boolean needsStaff(){
		return needsStaff;
	}  

	public AbcElement lastElement() {
		if (theElements.size() <= 0) return null;
		return theElements.get(theElements.size()-1);
	}

	public boolean isInteresting(){
		return (!theElements.isEmpty());
	}

	public List<AbcElement> getElements() {
		return theElements;
	}

	public String asString(){
		String theString="";
		AbcElement theElement=null;
		for(ListIterator<AbcElement> i = theElements.listIterator() ; i.hasNext();){  
			theElement = i.next();
			theString += theElement.asString();
		}
		return theString;
	}

	// get the current default note length    
	public int getDefaultNoteDuration() {
		return defaultLength;
	}

	public KeySig getKeySig()
	{
		//System.out.println("getting key from Line");
		return theKey;
	}
	public int getGraceNoteLength(){
		return graceNoteLength;
	}

	public void setKeySig(KeySig k) {
		Debug.output(1,"setting key"+k);    
		theKey = k;
		// assume tail direction for voice is set first, then override with key if necessary
		graceNoteLength = k.getGraceNoteLength();
		if (autoTail) {
			Debug.output(1,"tail direction is not yet set");
			autoTail = !k.setTailDirection();
			tailsUp = k.tailsUp();

			Debug.output(1,"setting tail direction from key - tail up "+tailsUp);
		}   
	}
	public boolean autoTail()  {
		Debug.output(1,"tail direction is auto? "+autoTail);
		return autoTail;
	}
	public boolean tailsUp()  {
		return tailsUp;
	}

	public void setChanged(boolean key, boolean meter,boolean clef)  {
		newKey = key;
		newMeter = meter;
		newClef = clef;
	}    
	public boolean newClef(){
		return newClef;
	}
	public boolean newKey() {
		return newKey;
	}
	public void setSlurStarting(){
		slursStarting++;
	}
	public void clearSlursStarting(){
		slursStarting = 0;
	}	  
	public int getSlursStarting(){
		return slursStarting;
	}		
	public void setDefaultNoteDuration(int len){      
		defaultLength = len;
	}
	public void setMeter(Meter m){
		theMeter = m;
	}

	public boolean newMeter() {
		return newMeter;
	}

	public Meter getMeter() {
		return theMeter;
	}

	public void dump() {
		System.out.println(asString());
	}

	public void getMetrics(AbcLineMetrics m){
		if (needsStaff){
			BarPosition bpCurrent = new BarPosition();

			for (AbcElement e : theElements){
				bpCurrent = e.getMetrics(m,bpCurrent);
				Debug.output(debugLevel, "Line.getMetrics: elem "+e+" Next Bar position="+bpCurrent);
			}
			m.setMaxAlignedWords(numAlignedWords());   
		}
	}
	/**
	 * current markup
	 */
	public void addSimpleOrnament(String theOrnament){
		if (theOrnaments == null){
			theOrnaments = new Ornaments();
		}
		theOrnaments.addOrnament(theOrnament);
	}
	public void addGuitarChord(GuitarChordElement theChord){
		if (theOrnaments == null){
			theOrnaments = new Ornaments();
		}
		theOrnaments.addGuitarChord(theChord);

	}

	public void setOrnaments(Ornaments o){
		theOrnaments = o;
	}	   

	public Ornaments getOrnaments(){
		return theOrnaments;
	}

	/**
	 * add to the list of words
	 *
	 */

	public void addAlignedWords(AlignedWords theWords){
		if (theWordList == null){
			theWordList = new ArrayList<AlignedWords>();
		}
		theWordList.add(theWords);
		//theWords.dump();
	}   

	public void setGrace(Grace gr){
		theGrace=gr;
	}

	public Grace getGrace(){
		return theGrace;
	}

	public int numAlignedWords(){

		if (theWordList == null){
			Debug.output(1,"no aligned words");
			return 0;
		}
		else{   
			Debug.output(1,"aligned words: size = "+theWordList.size());
			return theWordList.size();
		}
	}

	public List<AlignedWords> getAlignedWords(){
		Debug.output(1,"getAlignedWords"+theWordList);
		return theWordList;
	}

	public void setAlignedWords(List<AlignedWords> theWords){
		Debug.output(1,"setAlignedWords"+theWords);
		theWordList = theWords;
	}  
	/**
	 * appends the contents of each aligned word in the list to the corresponding
	 * existing one
	 */

	public void appendAlignedWords(List<AlignedWords> theNewWords){
		if (theNewWords == null) return;
		if (theWordList == null){
			theWordList = theNewWords;
		}
		else {

			ListIterator<AlignedWords> newIter = theNewWords.listIterator();
			for (ListIterator<AlignedWords> i=theWordList.listIterator(); 
			(i.hasNext()&&newIter.hasNext()) ;){
				i.next().addAll(newIter.next());
			}
		}  
	}         
}