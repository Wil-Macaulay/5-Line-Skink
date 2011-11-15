/**
 * @author macaulay
 * @version 2.0a2
 * @since 22 mar 2006
 * 
 * history
 * 
 */
package ca.celticmusic.skink;
import ca.celticmusic.skink.abcparser.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;


public class Voice implements TuneTreeItem {
	
	List<Line> assembledLines = null;
	
	boolean clefChange = false;
	boolean needsStaff = false;
	
	String currentClef = "treble";
	
	String middleNote = null;  // mid line of clef
	KeySig currentKey = null;
	
	Line   currentLine = null;
	
	Meter currentMeter = null;
	int defaultNoteLength = SkinkConstants.DEFAULT_NOTE_LENGTH;
	
	boolean errorDetected = false;
	List<TuneErrorItem> errors = null;
	boolean keyChange = false;
	List<Line> lines    = new ArrayList<Line>();
	boolean meterChange = false;
	String name = " ";
	String subName = " ";
	int quartersPerMinute = 0;  // 'beats' per minute (quarternotes, in MIDI)
	public int startColumn = 0;
	
	public int startRow = 0;
	String theClef = "treble";
	String theIndex = null;
	KeySig theKey   = null;
	Meter theMeter  = null;
	TempoElement theTempo = null;
	List unrolledAbc = null;
	
	public Voice() {
        setClef("treble");
	}
	public Voice(Voice previousVoice){
		setClef("treble");
		theKey  = previousVoice.theKey;
		theMeter = previousVoice.getMeter();
		theTempo = previousVoice.theTempo;
		currentKey = previousVoice.currentKey;
		currentMeter = previousVoice.currentMeter;
		
	}
	
	public void addAlignedWords(AlignedWords theWords){
		Debug.output(1,"Tune.addAlignedWords");
		if (currentLine != null){
			Debug.output(1,"adding aligned words");
			currentLine.addAlignedWords(theWords);
		}
	}
	/**
	 * assemble lines for printing according to layout rules.
	 * note that this is only done once each time the tune
	 * is parsed
	 *
	 * @since April 9 2002 also reset the aligned words
	 *
	 * @returns List of lines with embedded key, clef events
	 */
	
	public List <Line>assembleLines(){
		if (assembledLines == null){
			assembledLines = new ArrayList<Line>();
			Line l2 = new Line();
			//add the voice identifier 
			insertVoiceName(l2);
			boolean paintStaff = true;
			for(Line theLine : lines){
				
				KeySig k = theLine.getKeySig();
				l2.setKeySig(k);  // FIXME this is to set tail direction for HP
				l2.setClef(theLine.getClef());
				l2.midLine = theLine.midLine;
				if (theLine.needsStaff()){
					needsStaff = true;
					if(paintStaff){
						l2.addElement(new ClefElement()); 
						paintStaff  = false; 
						l2.addElement(new KeysigElement(k)); 
					}
					else{
						if (theLine.newKey()){
							l2.addElement(new KeysigElement(theLine.getKeySig())); 
						}
						if (theLine.newClef()){
							l2.addElement(new ClefElement());
						}

					}
					
					// paint the keysig and meter if necessary 
					if (theLine.newMeter()){
						l2.addElement(new MeterElement(theLine.getMeter()));  
					}   
				}  
				l2.appendAlignedWords(theLine.getAlignedWords());   // assume the line was broken, and
				// the words were broken accordingly
				l2.appendElements(theLine); 
				if (theLine.advance()){
					assembledLines.add(l2); 
					l2 = new Line(); 
					l2.setKeySig(k);  // FIXME this is to set tail direction for HP
					insertVoiceSubName(l2);
					l2.midLine = theLine.midLine;
					paintStaff = true;
				}
			}
		}
		// now trim assembled lines to fit staff
		return assembledLines;
	}
	/**
	 * @param theLine
	 */
	private void insertVoiceName(Line theLine) {
		if (name != null){
			VoiceNameElement nameElement = new VoiceNameElement(name);
			theLine.addElement(nameElement );
		}
	}
	/**
	 * @param theLine
	 */
	private void insertVoiceSubName(Line theLine) {
		if (subName != null){
			VoiceNameElement nameElement = new VoiceNameElement(subName);
			theLine.addElement(nameElement );
		}
	}
	
	
	public void dump()
	{

		dumpLines();
	}
	
	
	
	
	
	public void dumpLines() {
		
		Line theLine;
		for (ListIterator i = lines.listIterator();i.hasNext();){
			theLine = (Line) i.next();
			Debug.output(1,"next line");
			theLine.dump();
		}
	}
	
	public int getBPM(){
		// return the appropriate beats per minute
		if (quartersPerMinute == 0){
			int bpm = 0;
			int beatNum = 0;
			int beatDenom = 0;
			// check explicit tempo
			if (theTempo != null){
				Debug.output(1,"processing tempo");
				bpm = theTempo.beatsPerMinute();
				beatNum = theTempo.beatNumerator();
				beatDenom = theTempo.beatDenominator();
			}
			if (bpm == 0) bpm = SkinkConstants.DEFAULT_BPM;
			if (beatDenom*beatNum == 0){
				beatDenom = 4;
				beatNum = 1;
				if (theMeter != null){
					if (theMeter.isCompound()){
						beatDenom = 8;
						beatNum = 3;
					}
				}
			}
			// now we should have a valid (or defaulted) tempo
			// normalize to sixteenth notes
			int den16 = 16/beatDenom; // number of sixteenths in denom
			int num16 = den16 * beatNum; // number of sixteenths in num
			// multiply by the number of beat units in a quarter
			quartersPerMinute =( bpm * num16) / 4;
		}
		return quartersPerMinute;
	}
	
	public String getClef() {
		return theClef;
	}
	
	public int getDefaultDuration()  {
		return defaultNoteLength;
	}
	
	public List getErrors(){
		return errors;
	}
	
	
	// nominal key of the voice   
	public KeySig getKey()
	{
		return theKey;
	} 
	public String getIndex(){
		return theIndex;
	}
	
	public List getLines()
	{
		return lines;
	}
	
	// nominal meter
	public Meter getMeter() {
		return theMeter;
	}        
	public String getName() {
		return name;
	}
	public String getSubName() {
		return subName;
	}
	
	public void setName(String theName) {
		name = theName;
		//setSubName(theName);
	}
	public void setSubName(String theSubName) {
		subName = theSubName;
	}
	public void setNoAdvance(){
		if (currentLine !=null){
			currentLine.setNoAdvance();
		}
	}
	/**
	 * create a new line updated with current line context
	 */
	
	public Line getNewLine(){
		Line theLine = new Line();
		theLine.setDefaultNoteDuration(defaultNoteLength);
		theLine.setKeySig(currentKey);
		theLine.setMeter(currentMeter);
		Debug.output(1, "Tune.getNewLine: current clef" + currentClef);
		theLine.setClef(currentClef);
		if (middleNote!=null){
			theLine.setMidLine(middleNote);
		}
		if (currentLine != null){
			carryOverLineContext(theLine);
		}
		return theLine;
	}
	private void carryOverLineContext(Line theLine){
		theLine.setBrokenRhythmElement(currentLine.getBrokenRhythmElement());
		theLine.setOrnaments(currentLine.getOrnaments());  //especially guitar chords
	}
	/**
	 * Get the number of staff lines
	 * @return
	 */	
	public int getNumStaves(){
		return assembleLines().size();
	}
	/**
	 * Return the requested stave, or a blank line
	 * @param staffNum
	 * @return
	 */	
	public Line getLineByNumber(int staffNum){
		if (staffNum<assembleLines().size()){
			return (Line)assembleLines().get(staffNum);
		}
		else{
			return getNewLine();			
		}
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see Skink.TuneTreeItem#getStartColumn()
	 */
	
	public int getStartColumn() {
		return 0;// startColumn;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see Skink.TuneTreeItem#getStartLine()
	 */
	public int getStartLine() {
		return startRow;
	}
	
	/* (non-Javadoc)
	 * @see Skink.TuneTreeItem#isErrorDetected()
	 */
	public boolean isErrorDetected() {
		return errorDetected;
	}
	
	public boolean isStaffNeeded(){
		return needsStaff;
	}
	
	public void newLine(Line theLine)
	{
		Debug.output(1,"Tune.newLine");
		currentLine = theLine;
		// set changed key or meter only for lines that actually have music
		if (currentLine.needsStaff())
		{
			currentLine.setChanged(keyChange,meterChange,clefChange);
			keyChange=meterChange = clefChange =false;
		}
		if (currentLine.isInteresting() ) lines.add(currentLine);
	}
	
	public void setClef(String clef) {
		clefChange = true;
		currentClef = clef;
		if (theClef == null)
			theClef = currentClef;
	}
	
	
	public void setDefaultDuration(int noteLen)  {
		defaultNoteLength = noteLen;
	}
	
	public void setMiddleNoteName(String middle){
		Debug.output(3,"setMiddleNoteName:"+middleNote);
		middleNote=middle;
	}
	public void setErrorDetected(ParseException e){
		errorDetected = true;
		if (errors == null){
			errors = new ArrayList<TuneErrorItem>();
		}
		errors.add(new TuneErrorItem(e));
	}
	public void setIndex(String index){
		theIndex = index;
		//setName(index);
		Debug.output(1,"Voice index = "+index);
	}
	
	
	public void setKey(String key)  {
		keyChange = true;
		currentKey =   KeySig.findKey(key);
		if (theKey == null) theKey = currentKey;  // initial key setting
	}
	
	public void setMeter(String meter) {
		Meter newMeter = Meter.findMeter(meter);
		setMeter(newMeter);
	}
	
	public  void setMeter(Meter newMeter){
		meterChange = true;
		currentMeter = newMeter;
		if (theMeter == null) theMeter = currentMeter;
	}
	public void setStart(int row, int col){
		startRow = row;
		startColumn = col;  
	}
	
	public void setTempoBeat(String numerator,String denom){
		if (theTempo == null){
			theTempo = new TempoElement();
		}
		theTempo.setBeat(Integer.parseInt(numerator),Integer.parseInt(denom));
	}
	public void setTempoBpm(String bpm){
		
		if(theTempo == null){
			theTempo = new TempoElement();
			
		}
		theTempo.setBeatsPerMinute(Integer.parseInt(bpm));
	}
	

	public String toString(){
		if (!errorDetected){
			return getName();
		}
		else{
			return("**ERROR** "+getName());
		}
	}  
	/**
	 * trim lines to the appropriate length
	 */
	public void trimLines(List lines){
	}     
	/**
	 * Unroll repeats, parts etc.
	 */
	public List unrollAbc(){
		if (unrolledAbc == null){
			Unroller u = new Unroller();
			u.unroll(assembleLines());
			unrolledAbc = u.getUnrolled();    
		}
		return unrolledAbc;    
	}
	
	
}
