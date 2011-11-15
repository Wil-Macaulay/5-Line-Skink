/**
 * 
 */
package ca.celticmusic.skink;

import java.awt.Point;
import java.util.ArrayList;
import java.util.ListIterator;

/**
 * @author wil macaulay
 *
 */
public class TunePainter {
	Renderer r = null;
	int lineEnd = -1;
	int headerWordEnd = -1;
	boolean pageLeft;
	boolean doneTune;

	ArrayList<Staff> staves = new ArrayList<Staff>();  // can be multiple voices on a staff
	int numStaves = 0;
	double yStaffHeight = StaffPanel.staffHeight; // height of a staff
	double ySystemHeight = 0;   // height of a staff system
	private double yInterSystem = 0;    // distance between systems
	private double yStaffDelta = 0;	 // distance between staves on a system
	
	double yCurrent = 0;
	double xCurrent = 0;
	
	double xInitialOffset;
	double xMaxStaffWidth = StaffPanel.staffWidth;
	double yMaxStartNewPage;  // if exceeded, start a new page for new tune 
	double yMaxStartNewStaff; // if exceeded, start a new page for new staff
	Tune theTune;
	StaffMetrics theStaffMetrics;
	
	private int debugLevel = 1;
	
	public TunePainter(Renderer theRenderer,Tune tune){
		theTune = tune;
		r = theRenderer;
		theStaffMetrics = new StaffMetrics(theTune.getNumVoices());
		for(Voice v : theTune.getVoices()){
			Staff theStaff = new Staff(v,r);
			staves.add(theStaff);
		}

	}
	private boolean advanceHeader() {
		return advanceStaff();
	}
	private boolean advanceStaff() {
		boolean canAdvance = true;		

		canAdvance = checkStaffAdvance();
		return canAdvance;
	}
	
	private boolean checkStaffAdvance() {
		Debug.output(debugLevel, "TunePainter.checkStaffAdvance: Checking staff advance at: " + yCurrent + " max= " + yMaxStartNewStaff);
		if (yMaxStartNewStaff > 0) {
			if (yCurrent > yMaxStartNewStaff) {
				Debug.output(debugLevel, "TunePainter.checkStaffAdvance: no more room for advance");
				return false;
			}
		}
		return true;
	}

	
	public boolean setTuneStart(double xPos,double yPos, double xPageSize, double yPageSize){
		double yNeeded = 0;
		// calculate header size 
		yNeeded += 10*StaffPanel.spaceHeight;
		
	//	yNeeded += getHeaderWordNeeded();
		double yGiven = yPageSize - yPos;
		// calculate staff system size
		numStaves = theTune.getNumStaves();
		setYStaffDelta(yStaffHeight + (StaffPanel.spaceHeight+4)*theTune.getNumLyricsLines()+2);
		ySystemHeight = (numStaves*yStaffHeight)+(numStaves-1)*getYStaffDelta();
		setYInterSystem(yStaffHeight );
		xInitialOffset = xPos;
		yCurrent = yPos;
		xMaxStaffWidth = xPageSize;
		yMaxStartNewStaff = yPageSize - (ySystemHeight + getYInterSystem());
		yNeeded += ySystemHeight;
		Debug.output(debugLevel, "TunePainter.setTuneStart: "+theTune.getTitle() + " need "+yNeeded+" got "+yGiven);
		return ((yPageSize == 0) || (yNeeded <= yGiven));
	}
	public void paintHeaderWords(int headerWordStart) {
		java.util.List words = theTune.getHeaderWords();
		for (ListIterator i = words.listIterator(headerWordStart); i.hasNext();) {
			InfoField iF = (InfoField) i.next();
			r.paintInfoString(xText(), yText(), iF.getInfoString());
			headerWordEnd++;
			// break out if we've run out of space
			if (!advanceInfo()) return;

			
		}
		advanceInfo();
	}
	public Point paintTune(int lineStart, double yPos, int headerWordStart) {
		lineEnd = lineStart;
		headerWordEnd = headerWordStart;
		yCurrent = yPos;
		double xSystemWidth = 0.0;
		
		if (theTune != null) {
			boolean pageLeft = true;
			if (lineEnd == -1) {
				paintHeader(theTune);
				pageLeft = advanceHeader();
				doneTune = false;
				lineEnd = 0;
			}
			// now do header words
			int hwCount = theTune.getHeaderWordSize();
			if(pageLeft &&(headerWordEnd < hwCount)){
				paintHeaderWords(headerWordStart);
			}
			int lineCount = theTune.getNumLines();
			pageLeft = checkStaffAdvance();

			while (pageLeft && (lineEnd < lineCount)) {
				Debug.output(debugLevel,
						"TunePainter.paintTune: Staff system for line: "
								+ lineEnd + " of " + lineCount);
				initMetrics(lineEnd);
				finalizeMetrics(xMaxStaffWidth);
				xSystemWidth = Math.max(xSystemWidth, paintSystem());
				lineEnd++;
				pageLeft = advanceStaff();
			}
			Debug.output(debugLevel,
					"TunePainter.paintTune: Finished page at line: " + lineEnd
							+ " of " + lineCount);
			doneTune = (lineEnd >= lineCount);
		}
		return new Point((int) xSystemWidth, (int) yCurrent);
	}
	/**
	 * Go through all lines and ensure corresponding notes line up vertically
	 * @param lineNum
	 */
	private void initMetrics(int lineNum){

		setStaffIndent(lineNum);

		theStaffMetrics.init();
		for (Staff s: staves){
			s.layoutStaff(theStaffMetrics,lineEnd,xCurrent,xMaxStaffWidth);
		}

	}	
		
	/**
	 * all lines have been scanned - update line metrics
	 *
	 */
		
	private void finalizeMetrics(double xMax) {
		// TODO Auto-generated method stub
		theStaffMetrics.update(xMax);
		
	}
	/**
	 * 
	 */
	
	private double paintSystem() {
		double xSystemWidth = 0.0;
		double yStart = yCurrent;
		boolean bracketsNeeded = false;
		Point systemOffset;
		for(Staff s : staves){
		
			systemOffset = s.paintStaffMusic(lineEnd,xCurrent,yCurrent,xMaxStaffWidth);
			xSystemWidth = systemOffset.getX();
			yCurrent += systemOffset.getY();
			if(s.needsStaff()){
				bracketsNeeded = true;
				yCurrent += getYStaffDelta();
			}
								
		}
		if (bracketsNeeded){
			drawSystemBrackets(yStart);
		}
		return xSystemWidth;
	}
			


	private void setStaffIndent(int lineNum){
		xCurrent = xInitialOffset;
		if (numStaves > 1){
			if (lineNum == 0){
				xCurrent = xInitialOffset + 80;				
			}
			else{
				xCurrent = xInitialOffset + 40;				
			}
			xMaxStaffWidth = StaffPanel.staffWidth - xCurrent;
		}
		
	}
	/**
	 * @param ySave
	 */
	private void drawSystemBrackets(double ySave) {
		if (numStaves>1){
			r.paintSystemBracket(xCurrent,ySave,ySave + ySystemHeight - (2*StaffPanel.spaceHeight));
			yCurrent += getYInterSystem();		}
	}
	public int lineEnd(){
		return lineEnd;
	}
	public int headerWordEnd(){
		return headerWordEnd;
	}
	public boolean doneTune(){
		return doneTune;
	}
	public void paintHeader(Tune t) {
		xCurrent = xInitialOffset;
		r.paintHeader(t.getTitle(), xCurrent, yCurrent + StaffPanel.spaceHeight * 4);
		r.paintComposer(t.getComposer(), xCurrent, yCurrent + StaffPanel.spaceHeight * 8);
		yCurrent += StaffPanel.spaceHeight * 14;
		// paint other header fields
	}

	private double xText() {
		return xCurrent +20;
	}
	private double yText() {
		return yCurrent - 2 * StaffPanel.spaceHeight;
	}
	private double yInfoHeight() {
		return (2 * StaffPanel.spaceHeight);
	}
	private boolean advanceInfo() {
		yCurrent += yInfoHeight();
		return checkStaffAdvance();   // FIXME this will mean we will pagebreak a little too soon when printing header words
	}
	void setYInterSystem(double yInterSystem) {
		this.yInterSystem = yInterSystem;
	}
	double getYInterSystem() {
		return yInterSystem;
	}
	void setYStaffDelta(double yStaffDelta) {
		this.yStaffDelta = yStaffDelta;
	}
	double getYStaffDelta() {
		return yStaffDelta;
	}


}
