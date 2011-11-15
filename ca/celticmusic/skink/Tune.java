//Tune.java
/**
 * An abc tune.
 *
 * @author Wil Macaulay
 * @version 1.0 3 Mar 2002
 * 
 *     0.1    9 Nov 1998 WM
 *     0.2   28 Apr 1999 WM  - dump()
 *     0.3    3 May 1999 WM  - info field support
 *     0.4   21 Jul 1999 WM  - starting row, col
 *     0.5    7 May 2000 WM  - key, meter handling -> Line
 *     1.0    3 Mar 2002 WM  - Unroll
 *            4 Apr 2002 WM  - replace Vectors with ArrayList
 *     1.0b2  18 July 2002 WM - tune tree includes errors
 *     1.2a0  2 July 2004 WM - getNewLine, newLine to update line context
 *	    2.0a0  7 july 2005 wm - reapply lost changes (HP support, clef support)
 *		2.0a2 22 mar 2006 wm - multi voice
 *		2.0d0 26 nov 2007 wm - fix multivoice
 *
 *
 **/
package ca.celticmusic.skink;
import java.util.*;

import ca.celticmusic.skink.abcparser.ParseException;


public class Tune implements TuneTreeItem
{
	boolean errorDetected = false;
	List<String> titles   = new ArrayList<String>();
	List lines    = new ArrayList();
	List<InfoField> info     = new ArrayList<InfoField>();
	List<Voice>voices	= new ArrayList<Voice>();
	List<Line> assembledLines = null;
	List headerWords = null;
	List<List<?>> unrolled = null;
	boolean explicitVoice = false; 
	Voice currentVoice = null;
	List<TuneErrorItem> errors = null;
	String theClef = null;
	KeySig theKey   = null;
	Meter theMeter  = null;
	TempoElement theTempo = null;
	String theIndex = null;
	int startLine = 0;
	int startColumn = 0;
	int endLine = -1;
	int endColumn = -1;
	private int defaultNoteLength = SkinkConstants.DEFAULT_NOTE_LENGTH;
	private TunePainter myPainter = null;


	public Tune() {
		currentVoice = addDefaultVoice();
	}
	/* (non-Javadoc)
	 * @see Skink.Voice#addAlignedWords(Skink.AlignedWords)
	 */
	public void setPainter(TunePainter p){
		myPainter = p;

	}
	
	public TunePainter getPainter(){
		return myPainter;
	}
	public void addAlignedWords(AlignedWords theWords) {
		currentVoice.addAlignedWords(theWords);
	}
	/* (non-Javadoc)
	 * @param 
	 */
	public void addInfo(InfoField theField) {
		info.add(theField);
	}
	/* (non-Javadoc)
	 * @see Skink.Voice#assembleLines()
	 */
	/**
	 * Add a voice
	 * 
	 */
	private Voice addVoice(String voiceId){
		Voice newVoice;
		if(!explicitVoice) {
			newVoice = currentVoice; // just reuse the current voice
		}
		else{
			newVoice = new Voice(currentVoice);
			voices.add(newVoice);
		}
		newVoice.setIndex(voiceId);  
		newVoice.setMeter(theMeter);
		newVoice.setDefaultDuration(defaultNoteLength);
		explicitVoice = true;
		return newVoice;	
	}
	public int getNumVoices(){
		return voices.size();
	}

	/**
	 * Make sure lines are assembled and return the number of staves required
	 * @return number of staves required
	 */
	//FIXME still needs work for merged staves
	public int getNumStaves(){
		int nStaves = 0;
		for (Voice v: voices){
			v.assembleLines();
			if (v.isStaffNeeded()){
				nStaves++;
			}
		}
		return nStaves;
	}
	/**
	 * add the default (no ID) voice
	 * @return
	 */
	private Voice addDefaultVoice(){
		Voice newVoice = new Voice();
		//FIXME this should be under debug control
		//newVoice.setName("default");
		voices.add(newVoice);
		return newVoice;
		
	}
	/**
	 * @return the max number of lines of aligned lyrics for staff system layout
	 *
	 */
	public int getNumLyricsLines(){
		List<Line> theLines = assembleLines();
		int lyricCount = 0;
		for(Line line:theLines){
			lyricCount = Math.max(lyricCount,line.numAlignedWords());
		}
		Debug.output(3, "Tune.getNumLyricsLines: "+lyricCount + " lines of lyrics");
		return lyricCount;
	}
	/**
	 * assemble the lines to print them
	 * @return the assembled list of lines
	 */
	public List<Line> assembleLines() { //FIXME this just assembles them sequentially
		
		if (assembledLines == null){
			if (!explicitVoice){
				List<Line> assembleLines = currentVoice.assembleLines();
				assembledLines = assembleLines;
			}
			else{
				assembledLines = new ArrayList<Line>();
					/*
				 * each voice knows how many staves
				 * calculate max staves from voice
				 * tell each voice to pad to max staves
				 * get staves one by one
				 */
				
				int maxNumLines = getNumLines();
				int numVoices = getNumVoices();
				
				// now get lines one at a time from all voices until done
				for (int i = 0; i<maxNumLines;i++){
					for (int j=0; j<numVoices; j++){
						Voice thisVoice = voices.get(j);
						Line theLine = thisVoice.getLineByNumber(i);
						assembledLines.add(theLine);
					}
					
				}
				
			}
				
		}
		return assembledLines;
	}
	/**
	 *
	 * @return number of lines in tune 
	 */
	public int getNumLines() {
		int maxNumLines = 0;			
		for (Voice theVoice : voices){
			if (theVoice.getNumStaves()>maxNumLines){
				maxNumLines = theVoice.getNumStaves();
			}
		}
		return maxNumLines;
	}

	/* (non-Javadoc)
	 * @see Skink.Voice#dump()
	 */
	public void dump(){
		dumpHeader();
		dumpVoices();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Skink.Voice#dumpHeader()
	 */
	private void dumpHeader() {
		System.out.println(titleString());
		InfoField theInfo;
		for (ListIterator i = info.listIterator(); i.hasNext();) {
			theInfo = (InfoField) i.next();
			theInfo.dump();
		}
	 }
	/*
	 * (non-Javadoc)
	 * 
	 * @see Skink.Voice#dumpLines()
	 */
	private void dumpVoices() {
		for (Voice theVoice : voices){
			theVoice.dumpLines();
		}
	}
	/* (non-Javadoc)
	 * @see Skink.Voice#getBPM()
	 */
	
	private Voice findVoice(String voiceID){
		for (Voice theVoice : voices ){
			if (voiceID.equals(theVoice.getIndex())){
				return theVoice;
			}
		}
		return null;
	}
	
	public List<Voice> getVoices(){
		return voices;
	}
	public int getBPM() {
		return currentVoice.getBPM();
	}
	/* (non-Javadoc)
	 * @see Skink.Voice#getClef()
	 */
	public String getClef() {
		return currentVoice.getClef();
	}
	/* (non-Javadoc)
	 * @see 
	 */
	
	public String getComposer() {
		String comp = " ";
		InfoField theInfo;
		for (ListIterator i = info.listIterator();i.hasNext();){
			theInfo = (InfoField) i.next();
			if (theInfo.getCode().equals("C")){
				return theInfo.getInfoString();
			}
		}
		return comp;    
	}
	/* (non-Javadoc)
	 * @see Skink.Voice#getDefaultLength()
	 */
	public int getDefaultDuration() {
		return currentVoice.getDefaultDuration();
	}
	/* (non-Javadoc)
	 * @see Skink.Voice#getErrors()
	 */
	public List getErrors() {
		return errors;
	}
		        
	/* (non-Javadoc)
	 * @see Skink.Voice#getFields(java.lang.String)
	 */

	    
	
	public List getFields(String type) {
		List<InfoField> vF = new ArrayList<InfoField>();
		InfoField theInfo;
		
		for (ListIterator i = info.listIterator();i.hasNext();){
			theInfo = (InfoField) i.next();
			if (theInfo.getCode().equals(type)){
				vF.add(theInfo);
			}
		}
		return vF;        
	}
	  /* (non-Javadoc)
	 * @see Skink.Voice#getHeaderWords()
	 */
	public List getHeaderWords() {
		if (headerWords == null) {
			headerWords = getFields("W");     
		}
		return headerWords;
	}
	public int getHeaderWordSize(){
		List hw = getHeaderWords();
		return hw.size();
	}
	public String getIndex(){
		return theIndex;
	}
	/* (non-Javadoc)
	 * @see Skink.Voice#getKey()
	 */
	public KeySig getKey() {
		return currentVoice.getKey();
	}
	/* (non-Javadoc)
	 * @see Skink.Voice#getLines()
	 */
	public List getLines() {
		return currentVoice.getLines();
	}
	/* (non-Javadoc)
	 * @see Skink.Voice#getMeter()
	 */
	public Meter getMeter() {
		return currentVoice.getMeter();
	}
	/* (non-Javadoc)
	 * @see Skink.Voice#getNewLine()
	 */
	public Line getNewLine() {
		return currentVoice.getNewLine();
	}
	/* (non-Javadoc)
	 * @see Skink.Voice#getStartColumn()
	 */
	public int getStartColumn() {
		return startColumn;
	}
	/* (non-Javadoc)
	 * @see Skink.Voice#getStartLine()
	 */
	public int getStartLine() {
		return startLine;
	}
	/* (non-Javadoc)
	 * @see Skink.Voice#isErrorDetected()
	 */
	public boolean isErrorDetected() {
		return errorDetected;
	}
	/* (non-Javadoc)
	 * @see Skink.Voice#newLine(Skink.Line)
	 */
	public void newLine(Line theLine) {
		currentVoice.newLine(theLine);
	}
	/* (non-Javadoc)
	 * @see Skink.Voice#setClef(java.lang.String)
	 */
	public void setClef(String clef) {
		currentVoice.setClef(clef);

	}
	/* (non-Javadoc)
	 * @see Skink.Voice#setDefaultLength(int)
	 */
	public void setDefaultDuration(int duration) {
		defaultNoteLength = duration;
		currentVoice.setDefaultDuration(duration);
	}
	
	public void setDefaultNote(int noteDenom) {
		int denom = SkinkConstants.WHOLE_LEN / noteDenom;
		if (denom > 0) {
			setDefaultDuration(denom);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Skink.Voice#setErrorDetected(Skink.ParseException)
	 */

	public void setErrorDetected(ParseException e){
		errorDetected = true;
		if (errors == null){
			errors = new ArrayList<TuneErrorItem>();
		}
		errors.add(new TuneErrorItem(e));
	}
	     
	/* (non-Javadoc)
	 * @see Skink.Voice#setIndex(java.lang.String)
	 */
	public void setIndex(String index){
		theIndex = index;
		Debug.output(1,"Tune index = "+index);
	}	
	/* (non-Javadoc)
	 * @see Skink.Voice#setKey(java.lang.String)
	 */
	public void setKey(String key) {
		if (theKey==null){
			// first time key is set, so initialize all voices
			for (ListIterator i = voices.listIterator(); i.hasNext();) {
				((Voice) i.next()).setKey(key);
			}
			theKey = KeySig.findKey(key);;
			
		}
		else{
			currentVoice.setKey(key);
		}
	}
	/* (non-Javadoc)
	 * @see Skink.Voice#setMeter(java.lang.String)
	 */
	public void setMeter(String meter) {
		if (theMeter == null) {
			// first time meter is set, so initialize all voices (only the default voice)
			for (ListIterator i = voices.listIterator(); i.hasNext();) {
				((Voice) i.next()).setMeter(meter);
			}
			theMeter = Meter.findMeter(meter);
		}
		else{
			currentVoice.setMeter(meter);
		}
	}
	
	public void setMiddleNoteName(String middle){
		currentVoice.setMiddleNoteName(middle);
	}
	public boolean isLineIn(int currentLine){
		// assumes we start at the end and work forward
		return currentLine >= startLine;
		//(endLine == -1) || ((currentLine >= startLine)&&(currentLine < endLine));
	}
	public void setStart(int row, int col){
		startLine = row;
		startColumn = col;  
	}
	public void setEnd(int row, int col){
		endLine = row;
		endColumn = col;
	}
	public void setNoAdvance(){
		if (currentVoice !=null){
			currentVoice.setNoAdvance();
		}
	}
/* (non-Javadoc)
	 * @see Skink.Voice#setStart(int, int)
	 */
	public void setStartVoice(int row, int col) {
		currentVoice.setStart(row, col);
	}
	/* (non-Javadoc)
	 * @see Skink.Voice#setTempoBeat(java.lang.String, java.lang.String)
	 */
	public void setTempoBeat(String numerator, String denom) {
		if (theTempo == null) {
			// first time tempo is set, so initialize all voices
			for (ListIterator i = voices.listIterator(); i.hasNext();) {
				((Voice) i.next()).setTempoBeat(numerator,denom);
			}
			theTempo = new TempoElement();
			theTempo.setBeat(Integer.parseInt(numerator),Integer.parseInt(denom));
		}
		else{
			currentVoice.setTempoBeat( numerator,  denom);
		}
	}
	
	/* (non-Javadoc)
	 * @see Skink.Voice#setTempoBpm(java.lang.String)
	 */
	public void setTempoBpm(String bpm) {
		
		if (theTempo == null) {
			// first time tempo is set, so initialize all voices
			for (ListIterator i = voices.listIterator(); i.hasNext();) {
				((Voice) i.next()).setTempoBpm(bpm);
			}
			theTempo = new TempoElement();
			theTempo.setBeatsPerMinute(Integer.parseInt(bpm));
		}
		else{
			currentVoice.setTempoBpm(bpm);
		}
	}
	/* (non-Javadoc)
	 * @see Skink.Voice#setTitle(java.lang.String)
	 */
	public void setTitle(String title){
		titles.add(title);
	}
	/**
	 * sets the current voice and creates a new one if necessary
	 * @param voiceId voice identifier
	 */
	public void setVoice(String voiceId){
		Voice theVoice = findVoice(voiceId);
		
		if (theVoice == null){
			theVoice = addVoice(voiceId);			
		}
        // need to ensure old voice line is cleaned up
		currentVoice = theVoice;
		// need to ensure new current line is appropriately initialized
		
	}
	/**
	 * set the name of the current voice
	 * @param voiceName
	 */
	public void setVoiceName(String voiceName){
		currentVoice.setName(voiceName);
	}
	/**
	 * set the subname (short name) of the voice
	 * @param snm
	 */
	public void setVoiceSubName(String snm){
		currentVoice.setSubName(snm);
	}
	/* (non-Javadoc)
	 * @see Skink.Voice#title()
	 */
	public String getTitle(){
		if (titles.size() > 0){
			return titles.get(0);
		}
		else {
			return " ";
		}   
	}
	/* (non-Javadoc)
	 * @see Skink.Voice#titleString()
	 */
	public String titleString() {
	     return (theIndex + "\t" + getTitle() + "\t" + getKey());
	}
	/* (non-Javadoc)
	 * @see Skink.Voice#toString()
	 */
	@Override
	public String toString(){
		if (!errorDetected){
			return getTitle();
		}
		else{
			return("**ERROR** "+getTitle());
		}
	}	
	 /* (non-Javadoc)
	 * @see Skink.Voice#trimLines(java.util.List)
	 */
	public void trimLines(List lines) {
		currentVoice.trimLines(lines);
	}
	/* (non-Javadoc)
	 * @see Skink.Voice#unrollAbc()
	 */
	public List unrollAbc() { 
		if (unrolled == null){
			unrolled = new ArrayList();
			for (ListIterator i = voices.listIterator(); i.hasNext();) {
				unrolled.add(((Voice)i.next()).unrollAbc());
			}
		}
		
		return unrolled;
	}
	
	  /* (non-Javadoc)
	 * @see Skink.TuneTreeItem#getStartLine()
	 */
		 
  
 }  