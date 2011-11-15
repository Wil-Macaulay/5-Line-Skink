/**
 * Draws one or more voices on a staff
 * @author wil macaulay
 * @since  6 dec 2007
 * @version 2.0e0 - refactored from Staff Panel
 * 
 */

package ca.celticmusic.skink;

import java.util.List;
import java.util.ListIterator;
import java.util.Stack;
import java.awt.Point;

public class Staff implements StaffPainter{

	AbcLineMetrics lineMetrics = new AbcLineMetrics();
	Voice thisVoice = null;
	Line theLine = null;
	
	// drawing positions
	// lastX is where the last glyph was drawn
	double xLast = 0;
	double xMaxStaffWidth = 0;

	double xLastWidth = 0; // width of the last glyph

	// currentX is where the next glyph will be drawn if there is one
	double xCurrent =0;// = (float) StaffPanel.xStart;

	double currentXwidth = 0; // width of current glyph
	int debugLevel = 1;
	// offsetX is the offset from currentX of the next part of the glyph
	double xOffset = 0;

	// currentY is at the top of the current treble staff
	double yCurrent = StaffPanel.yStart;

	double yBaseC = StaffPanel.yStart + 6 * StaffPanel.spaceHeight;

	double yMidLine;

	double yTextInc;
	
	double xStart;


	Beamer theBeamer = null;

	GraceBeamer theGraceBeamer = null;

	Renderer thisRenderer;
	double tieStart;

	boolean tieInProgress = false;

	Stack<SlurMarkup> slurStack = null;

	double xSaved = 0;

	double ySaved = 0;

	double xText;
	
	int slurNumUp = 0; // number of up slurs to start next line
	int slurNumDown = 0; // number of downslurs
	
	public Staff(Voice v,Renderer r){
		thisVoice = v;
		thisRenderer = r;
		slurStack = new Stack<SlurMarkup>();
		lineMetrics = new AbcLineMetrics();
	}
	private double yText() {
		return yCurrent - 2 * StaffPanel.spaceHeight;
	}

	// FIXME    
	private int xFixed() {
		return 25;
	}
	private void endStaff(){
		slurNumUp = slurNumDown = 0;
		while (!slurStack.empty()) {
			SlurMarkup slur = (SlurMarkup) slurStack.pop();
			if (slur.isTailUp()) {
				slurNumUp++;
				thisRenderer.paintSlur(slur.xStart(), slur.yStart(), slur.xcp1(), slur
						.ycp1(), slur.xcp2(), slur.ycp2(), xLast + 20, yBaseC);
			} else {
				slurNumDown++;
				thisRenderer.paintSlur(slur.xStart(), slur.yStart(), slur.xcp1(), slur
						.ycp1(), slur.xcp2(), slur.ycp2(), xLast + 20, yCurrent
						- 2 * StaffPanel.spaceHeight);
			}
		}
	}
	private void startStaff(double xStartPos, double yStartPos){
	
		slurStack.clear();
		initPositions(xStartPos,yStartPos);
		// push leftover slurs
		for (int i = 0; i < slurNumUp; i++) {
			slurStack.push(new SlurMarkup(xStartPos, yBaseC, true));
		}
		for (int i = 0; i < slurNumDown; i++) {
			slurStack.push(new SlurMarkup(xStartPos, yCurrent, false));
		}
		slurNumUp = slurNumDown = 0;
		
	}
	public void layoutStaff(StaffMetrics theStaffMetrics, int lineNumCurrent, double current, double xStaffWidth) {
		double xLineWidth;

		lineMetrics.init(xStaffWidth);
		theStaffMetrics.addLineMetrics(lineMetrics);
		theLine = thisVoice.getLineByNumber(lineNumCurrent);
		theLine.getMetrics(lineMetrics);
		xLineWidth = lineMetrics.getTotGlyphWidth(); //FIXME if (lineWid>staffWidth)..
		Debug.output(1, "line width=" + xLineWidth); 
		if (theLine.numAlignedWords() > 0) {
			for (AlignedWords aw: theLine.getAlignedWords()){
				aw.reset(xCurrent);
			}
		}
		
		
	}

	public Point paintStaffMusic(int lineNumCurrent,double xPos,double yPos,double xStaffWidth){
		double yHeight = yText();
		// figure out how long the line needs
		startStaff(xPos,yPos);		
		paintMusic(theLine);
		yHeight = 2 * StaffPanel.spaceHeight;
		if (theLine.needsStaff()){
			paintLines(xLast + xLastWidth);
			yHeight = StaffPanel.staffHeight;
		}
		endStaff();
		return new Point((int)xMaxStaffWidth,(int)yHeight);
	}
	public boolean needsStaff(){
		return (theLine==null)?false:(theLine.needsStaff());
	}

	private void paintMusic(Line theLine) {
		//endBeam();
		paintMusicElements(theLine.getElements(), true);
		endBeam();
		paintMarkup(theLine.getElements());
	}
	
	// initialize drawing positions with respect to currentY   
	private void initPositions(double xInit,double yInit) {
		yCurrent = yInit;
		xCurrent = xInit;
		xStart = xInit;
		xLast = xCurrent;
		xOffset = 0;
		yBaseC = yCurrent + 6 * StaffPanel.spaceHeight;
		yMidLine = yCurrent + 3 * StaffPanel.spaceHeight / 2; // midline of staff for tail direction
		yTextInc = 0;
		xText = xStart;

		theBeamer = new Beamer();
		theGraceBeamer = new GraceBeamer();
	}
	
	/* (non-Javadoc)
	 * @see ca.celticmusic.skink.StaffPainter#advance(int, boolean)
	 */
	public void advance(int glyphSize, boolean fixedAdvance, BarPosition bpCurrent) {
		Debug.output(1, "Staff.advance: currentX = " + xCurrent
				+ " glyphSize " + glyphSize + " fixed? " + fixedAdvance);
		xLast = xCurrent;
		xLastWidth = glyphSize;
//		xCurrent = lineMetrics.getXoffset(bpCurrent) + xStart;
//		xCurrent = xLast + glyphSize + lineMetrics.getFixedPad();
//		if (!fixedAdvance)
//			xCurrent += lineMetrics.getFloatPad();
	}

	private void setXdrawingPosition(BarPosition bpCurrent){
		xCurrent = lineMetrics.getXoffset(bpCurrent) + xStart;
	}
	
	private void advanceSmallFixed() {
		xCurrent += xFixed() / 3;
	}

	private boolean advanceInfo() {
		yCurrent = yCurrent + 2 * StaffPanel.spaceHeight;
		//initPositions();
		return false; //StaffPanel.checkStaffAdvance();
	}


	private void advanceDeco() {
		advanceSmallFixed();
	}

	private void backupAcc() {
		// back up to put accidentals in the right spot
		xCurrent -= xFixed() / 3;
	}

	private void advanceAcc()
	// advance after accidental 
	{
		xCurrent += xFixed() / 3;
	}

	public void paintMarkup(java.util.List<AbcElement> abcList) {
		for (AbcElement e: abcList){
			e.paintMarkup(this);
		}
	}

	/**
	 * Allows painting of aligned words to proceed if waiting for barline
	 */

	public void barAlignedWords() {
		if (theLine.numAlignedWords() > 0) {
			for (AlignedWords a: theLine.getAlignedWords()){
				a.newBar();
			}
		}

	}

	/**
	 * draws aligned words under the current note 
	 */

	public void drawAlignedWords(double xPos) {
		double yPos = 10 * StaffPanel.spaceHeight + yCurrent; // get past slurs
		// now aligned words, if any                  
		if (theLine.numAlignedWords() > 0) {
			for (AlignedWords a: theLine.getAlignedWords()){
				a.paint(this, xPos, yPos);
				yPos += StaffPanel.spaceHeight + 4; //FIXME should be a function of the height of the word font
			}
		}

	}

	public void paintDash(double xPos, double yPos) {
		thisRenderer.paintDash(xPos, yPos - StaffPanel.spaceHeight / 2);
	}

	public void paintLine(double xFirst, double xLast, double yPos) {
		thisRenderer.paintHorizLine(xFirst + StaffPanel.wholeSize * 2, xLast, yPos);
	}

	public void paintLyric(String theLyric, double xPos, double yPos) {
		thisRenderer.paintLyric(theLyric, xPos, yPos);
	}

	public void paintMusicElements(java.util.List<AbcElement> abcList, boolean advance) {
		for (AbcElement e :abcList) {
			BarPosition bp = e.getBarPosition();
			Debug.output(debugLevel, "Staff.paintMusicElements: currentX=" + xCurrent
					+ " lastX= " + xLast);
			Debug.output(debugLevel, "Staff.paintMusicElements: element "+ e 
					+ " current bp" + bp);

			setXdrawingPosition(bp);
			Debug.output(debugLevel, "Staff.paintMusicElements: (updated) currentX=" + xCurrent
					+ " lastX= " + xLast);

			//lastX = currentX;  // just past last glyph
			e.paint(this, advance);
		}
	}

	public void endBeam() {
		Debug.output(1, "endBeam: currentX=" + xCurrent + " lastX= " + xLast);
		theBeamer.endBeam(thisRenderer);
		theBeamer.init(yMidLine, theLine.autoTail(), theLine.tailsUp());
		if (!theLine.autoTail())
			theBeamer.setMaxSlope(0.0); // FIXME assume pipe music for now
	}

	public void slurBegin(MusicElement m, int numSlurs) {
		Debug.output(1, "slurBegin at " + m.getReferenceX() + ","
				+ m.getReferenceY() + m.asString());
		for (int i = 0; i < numSlurs; i++) {
			SlurMarkup slur = new SlurMarkup(m);
			slurStack.push(slur);
		}
	}

	public void slurEnd(MusicElement m, int numSlurs) {
		Debug.output(1, "slurEnd at " + m.getReferenceX() + ","
				+ m.getReferenceY() + m.asString());
		for (int i = 0; i < numSlurs; i++) {
			if (!slurStack.empty()) {
				SlurMarkup slur = (SlurMarkup) slurStack.pop();
				thisRenderer.paintSlur(slur.xStart(), slur.yStart(), slur.xcp1(), slur
						.ycp1(), slur.xcp2(), slur.ycp2(), slur.xEnd(), slur
						.yEnd());
				if (!slurStack.empty()) {
					SlurMarkup outerSlur = (SlurMarkup) slurStack.peek();
					outerSlur.update(slur); // update enclosing slur
					outerSlur.update(m);
				}
			}
		}
	}

	public void updateSlurContour(MusicElement m) {
		Debug.output(1, "update slur at " + m.getReferenceX() + ","
				+ m.getReferenceY() + m.asString());
		if (!slurStack.empty()) {
			SlurMarkup slur = (SlurMarkup) slurStack.peek();
			slur.update(m);
		}
	}

	/** paint tuplet marks
	 * during markup pass, so the notes have their x and y references
	 */
	public void paintTuplet(double x1, double y1, double x2, double y2,
			int beats) {
		thisRenderer.paintTupletMark(x1, y1, x2, y2, beats);

	}

	public void paintChord(MultiElement m) {
		Ornaments o = m.getOrnaments();
		// draw gracenotes first
		if (o != null) {
			Grace gr = o.getGraceNotes();
			if (gr != null) {
				drawGraceNotes(gr);
			}
		}

		// draw without advancing - XXX note that dotted will not advance enough 
		Debug.output(3, "painting chord");
		MusicElement n = (MusicElement) m.firstElement();
		double maxY = findOffset(n, theLine.getMidLine());
		double minY = maxY;
		int len = n.getAbsLength();
		double refX = xCurrent + m.getReferencePoint();
		for (ListIterator i = m.getElements().listIterator(); i.hasNext();) {
			n = (MusicElement) i.next();
			// set currentX with respect to reference point (FIXME kludge city!!)
			xCurrent = refX - n.getReferencePoint();
			double y = findOffset(n, theLine.getMidLine());
			if (y > maxY)
				maxY = y;
			if (y < minY)
				minY = y;
			n.paint(this, false);
		}
		xCurrent = refX;

		// now paint tail (quarter and half notes only)
		double yRef = tailUp((maxY + minY) / 2) ? maxY : minY;
		if ((len < SkinkConstants.WHOLE_LEN)
				&& (len >= SkinkConstants.QUARTER_LEN)) {
			thisRenderer.paintTail(xCurrent, yRef, (StaffPanel.tailHeight + maxY - minY),
					tailUp((maxY + minY) / 2), 0, StaffPanel.quarterSize, false);
			m.setReferencePosition(xCurrent, yRef, tailUp((maxY + minY) / 2),
					(StaffPanel.tailHeight + maxY - minY));
		}
		//  other decorations, if any  
		drawAlignedWords((int) xCurrent);
	}

	// FIXME just looking at the deco contents - need to generalize    
	public void paintDecoration(String deco) {
		paintDecoration(deco, xCurrent, yCurrent, yBaseC, yCurrent, false);
	}

	public void paintDecoration(String deco, double x, double y, double yMax,
			double yMin, boolean tailUp) {
		if (deco == null)
			return;
		String testdeco = DecorationElement.getCanonicalDeco(deco);
		double yTopPos = yCurrent - StaffPanel.spaceHeight / 2; // position on top of note
		double yBottomPos = yBaseC + StaffPanel.spaceHeight / 2; // below
		if (yMin < yTopPos)
			yTopPos = yMin - StaffPanel.spaceHeight / 2;
		if (yMax > yBottomPos)
			yBottomPos = yMax + StaffPanel.spaceHeight / 2;
		if ("coda".equals(testdeco)) {
			thisRenderer.paintCoda(x, yTopPos);
			return;
		}
		if ("fermata".equals(testdeco)) {
			thisRenderer.paintFermata(x, yTopPos);
			return;
		}
		if ("invertedfermata".equals(testdeco)) {
			thisRenderer.paintInvertedFermata(x, yBottomPos);
			return;
		}
		if ("segno".equals(testdeco)) {
			thisRenderer.paintSegno(x, yTopPos);
			return;
		}
		if ("slide".equals(testdeco)) {
			thisRenderer.paintSlide(x, y);
			return;
		}
		if ("mordent".equals(testdeco)) {
			thisRenderer.paintUpperMordent(x, yTopPos);
			return;
		}
		if ("pralltriller".equals(testdeco)) {
			thisRenderer.paintLowerMordent(x, yTopPos);
			return;
		}
		if ("accent".equals(testdeco)) {
			double yPos = y - StaffPanel.spaceHeight * 0.8;
			if (tailUp)
				yPos = y + StaffPanel.spaceHeight * 1.8;
			thisRenderer.paintAccent(x, yPos);
			return;
		}
		if ("longphrase".equals(testdeco)) {
			thisRenderer.paintLongPhrase(x, yCurrent - StaffPanel.spaceHeight);
			return;
		}
		if ("shortphrase".equals(testdeco)) {
			thisRenderer.paintShortPhrase(x, yCurrent - StaffPanel.spaceHeight);
			return;
		}
		if ("mediumphrase".equals(testdeco)) {
			thisRenderer.paintMediumPhrase(x, yCurrent - StaffPanel.spaceHeight);
			return;
		}
		if ("turn".equals(testdeco)) {
			thisRenderer.paintRoll(x, yTopPos);
			return;
		}
		if ("trill".equals(testdeco)) {
			thisRenderer.paintTrill(x, yTopPos);
			return;
		}
		if ("staccato".equals(testdeco)) {
			double yPos = y - StaffPanel.spaceHeight * 0.8;
			if (tailUp)
				yPos = y + StaffPanel.spaceHeight * 1.8;
			double xPos = x + StaffPanel.quarterSize / 2;
			thisRenderer.paintDot(xPos, yPos);
			return;
		}
		if ("upbow".equals(testdeco)) {
			thisRenderer.paintUpBow(x, yTopPos);
			return;
		}
		if ("downbow".equals(testdeco)) {
			thisRenderer.paintDownBow(x, yTopPos);
			return;
		}
		// no deco, just print the original string	
		thisRenderer.paintStringDeco(xCurrent, yCurrent-StaffPanel.spaceHeight, deco);
	}

	
	public void paintVoiceName(String name) {
		thisRenderer.paintVoiceName(xCurrent -5, yMidLine+StaffPanel.spaceHeight/2, name);
	}


	public void paintBarline(BarLineElement b) {
		double xsave = xCurrent;
		xCurrent += b.getReferencePoint();
		thisRenderer.paintBarline(xCurrent, yCurrent, b.type);
		b.setReferencePosition(xCurrent, yCurrent, false, StaffPanel.spaceHeight * 4);
		barAlignedWords(); // if they are waiting for the barline, let'em proceed
		xCurrent = xsave;
	}

	// the bar line position is determined first (currentX), then the dots 
	// note that this is ugly if the begin repeat is at the end of a line
	public void paintRepeat(RepeatElement rE) {
		double xsave = xCurrent;
		if (rE.endRepeat) {
			drawDoubleDots(xCurrent);
			advanceDeco();
		}
		paintBarline(rE);
		if (rE.beginRepeat) {
			advanceDeco();
			drawDoubleDots(xCurrent);
		}
		xCurrent = xsave;

	}

	public void paintNthEnding(NthRepeatElement n) {
		double xSave = xCurrent;
		if (n.endRepeat) {
			drawDoubleDots(xCurrent);
			advanceDeco();
		}
		// put the ending mark
		thisRenderer.paintNthEnding(xCurrent, yCurrent, n.repeatNum);
		paintBarline(n);
		advanceDeco();
		xCurrent = xSave;
	}

	public void drawDoubleDots(double x) {
		thisRenderer.paintDot(x, yMidLine - (StaffPanel.spaceHeight / 2) + 2);
		thisRenderer.paintDot(x, yMidLine + (StaffPanel.spaceHeight / 2) + 2);
	}

	public void paintField(FieldElement f) {
		// in-tune field.  
		InfoField iF = f.getField();
		String fieldType = iF.getCode();
		Debug.output(3, "field type = " + fieldType);
		String contents = iF.getInfoString();
		if (contents == null)
			return;
		if (fieldType.equals("P") || fieldType.equals("T")) {
			thisRenderer.paintInfoString(xText, yText(), iF.getInfoString());
			advanceInfo();
		}
		if (fieldType.equals("W")) {
			thisRenderer.paintInfoString(xText + 20, yText(), iF.getInfoString());
		}
	}

	public void paintMeter(Meter m) {
		double xSave = xCurrent;
		if (m != null) {
			if (m.getNumerator() == 0) { // unknown or free meter
				advanceDeco();
			} else {
				if (m.isSpecial()) {// for now, only special ones are C and C|
					thisRenderer.paintSpecialMeter(xCurrent, yCurrent, m.getSymbol());
					advanceDeco();
				}

				else {
					thisRenderer.paintNumericMeter(xCurrent, yCurrent, ("" + m
							.getNumerator()), ("" + m.getDenominator()));
				}
			}
		}
		xCurrent = xSave;

	}

	public void paintKeySig(KeySig k) {

		// find note, offset
		double xSave = xCurrent;
		String sharps = "fcgdAeB";
		String flats = "BeAdGcF";
		String theNote;

		// start with sharps - f,c,g,d,A,e,B 
		int i;

		for (i = 0; i < 7; i++) {
			theNote = sharps.substring(i, i + 1);
			//          Debug.output(1,"checking for sharp"+theNote);
			if (k.sharpOrFlatShown(theNote) == 1) {
				double y = findOffset(new MusicElement(theNote), theLine.getMidLine());
				//highest permissible is space above top line
				while (y < (yCurrent - StaffPanel.spaceHeight))
					y += (StaffPanel.spaceHeight) * 7.0 / 2.0;
				drawSharp(y);
				advanceSmallFixed();
			}
		}

		// now flats B,e,A,d,g,c,f
		for (i = 0; i < 7; i++) {
			theNote = flats.substring(i, i + 1);
			//          Debug.output(1,"checking for sharp"+theNote);
			if (k.sharpOrFlatShown(theNote) == -1) {
				double y = findOffset(new MusicElement(theNote), theLine.getMidLine());
				while (y < (yCurrent - StaffPanel.spaceHeight))
					y += (StaffPanel.spaceHeight) * 7.0 / 2.0;
				drawFlat(y);
				advanceSmallFixed();
			}
		}
		// naturals in the same order as sharps, for now    
		for (i = 0; i < 7; i++) {
			theNote = sharps.substring(i, i + 1);
			//          Debug.output(1,"checking for sharp"+theNote);
			if (k.sharpOrFlatShown(theNote) == 9) {
				double y = findOffset(new MusicElement(theNote), theLine.getMidLine());
				while (y < (yCurrent - StaffPanel.spaceHeight))
					y += (StaffPanel.spaceHeight) * 7.0 / 2.0;
				drawNatural(y);
				advanceSmallFixed();
			}
		}
		xCurrent = xSave;
	}

	public void paintRest(RestElement r) {
		double xSave = xCurrent;
		// Debug.output(1,"length of note: "+length);
		int headLen = r.getHeadLength();
		int nDots = r.getDotCount();
		switch (headLen) {
		case SkinkConstants.THIRTYSECOND_LEN: // sixteenth
			drawThirtysecondRest();
			break;

		case SkinkConstants.SIXTEENTH_LEN: // sixteenth
			drawSixteenthRest();
			break;
		case SkinkConstants.EIGHTH_LEN: // eighth
			drawEighthRest();
			break;
		// quarter
		case (SkinkConstants.QUARTER_LEN):
			drawQuarterRest();
			break;

		case (SkinkConstants.HALF_LEN): // half
			drawHalfRest();
			break;

		case (SkinkConstants.WHOLE_LEN): // whole
			drawWholeRest();
			break;

		case (SkinkConstants.BREVE_LEN): // breve
			drawBreveRest();
			break;

		case (SkinkConstants.LONGA_LEN): // longa
			drawLongaRest();
			break;

		}
		r.setReferencePosition(xCurrent, yMidLine + StaffPanel.spaceHeight + 2, true,
				StaffPanel.spaceHeight * 3);
		for (int i = 0; i < nDots; i++) {
			advanceDeco();
			drawDot(yMidLine - 2);
		}

		xCurrent = xSave;
	}

	private void drawThirtysecondRest() {
		thisRenderer.paintThirtysecondRest((int) xCurrent, yCurrent);
	}

	private void drawSixteenthRest() {
		thisRenderer.paintSixteenthRest((int) xCurrent, yCurrent);
	}

	private void drawEighthRest() {
		thisRenderer.paintEighthRest((int) xCurrent, yCurrent);
	}

	private void drawQuarterRest() {
		// XXX should be consistent with currentY
		thisRenderer.paintQuarterRest((int) xCurrent, yMidLine);

	}

	private void drawHalfRest() {
		thisRenderer.paintHalfRest((int) xCurrent, yMidLine);
	}

	private void drawWholeRest() {
		thisRenderer.paintWholeRest((int) xCurrent, yMidLine);
	}

	private void drawBreveRest() {
		thisRenderer.paintBreveRest((int) xCurrent, yMidLine);
	}

	private void drawLongaRest() {
		thisRenderer.paintLongaRest((int) xCurrent, yMidLine);
	}

	// set a reference for decos    
	public void setReference(MusicElement m) {
		m.setReferencePosition(xCurrent, yCurrent, false, StaffPanel.spaceHeight * 4);
	}

	public void paintNote(MusicElement m, boolean advance) {
		double xsave = xCurrent;
		Ornaments o = m.getOrnaments();
		// draw gracenotes first
		if (o != null) {
			Grace gr = o.getGraceNotes();
			if (gr != null) {
				drawGraceNotes(gr);
			}
		}

		// now draw accidentals
		double y = findOffset(m, theLine.getMidLine());
		// accidental
		drawAccidental(m.getAccidental(), y, true);
		// draw leger lines 
		drawLeger(y);
		// draw note
		double xSave = xCurrent;
		// Debug.output(1,"length of note: "+length);
		int headLen = m.getHeadLength();
		int nDots = m.getDotCount();
		// end of a tie?
		if (tieInProgress) {
			thisRenderer.paintTie(tieStart, y, xCurrent + StaffPanel.quarterSize / 2, tailUp(y));
		}

		// if less than a quarter note, continue beam
		if (headLen < SkinkConstants.QUARTER_LEN) {
			theBeamer.addNote(m, xCurrent, y);
			drawNoteHeadFilled(y);
		} else {

			// end beam, because no hooks on these notes
			endBeam();
			switch (headLen) {
			case SkinkConstants.QUARTER_LEN:
				drawNoteHeadFilled(y);
				if (advance)
					drawNoteTail(m, y, 0);
				break;
			case SkinkConstants.HALF_LEN:
				drawNoteHeadUnfilled(y);
				if (advance)
					drawNoteTail(m, y, 0);
				break;
			case SkinkConstants.WHOLE_LEN: // whole
				drawWhole(m, y);
				m.setReferencePosition(xCurrent, y, tailUp(y), StaffPanel.spaceHeight);
				break;
			case SkinkConstants.BREVE_LEN: //breve
				drawBreve(m, y);
				m.setReferencePosition(xCurrent, y, tailUp(y), StaffPanel.spaceHeight);
				break;
			case SkinkConstants.LONGA_LEN: // breve
				drawLonga(m, y);
				m.setReferencePosition(xCurrent, y, tailUp(y), StaffPanel.spaceHeight);
				break;
			default:
				break;
			}
		}
		for (int i = 0; i < nDots; i++) {
			advanceDeco();
			drawDot(y);
		}

		if (advance) {
			drawAlignedWords((int) xSave); // center at note head
		} else {
			xCurrent = xSave;
		}
		if (m.isTied()) {
			tieStart = xCurrent + StaffPanel.quarterSize / 2;
			tieInProgress = true;
		} else
			tieInProgress = false;

		xCurrent = xsave;
	}

	// tails up for this Y?
	private boolean tailUp(double y) {
		// eventually, the line will know which voice it is in
		Debug.output(1, "finding tail direction");
		if (theLine.autoTail())
			return (y >= yMidLine);
		Debug.output(1, "tail direction set in line - up?" + theLine.tailsUp());
		return theLine.tailsUp();
	}

	// find offset in spaces from clef midline  

	private double findOffset(MusicElement m, int midNoteNumber) {

		int i = m.findVerticalOffset(midNoteNumber);
		double offset = (yCurrent + 3 * StaffPanel.spaceHeight / 2)
				- (i * StaffPanel.spaceHeight / 2);
		return offset;
	}

	private void drawAccidental(int pM, double y, boolean backup) {
		switch (pM) {
		// FIXME double sharps and double flats??
		case 1: // sharp
			if (backup)
				backupAcc();
			drawSharp(y);
			advanceAcc();
			break;
		case -1:
			if (backup)
				backupAcc();
			drawFlat(y);
			advanceAcc();
			break;
		case 9:
			if (backup)
				backupAcc();
			drawNatural(y);
			advanceAcc();
			break;
		default:
			break;
		}

	}
/**
 * draw decorations and guitar chords
 */
	public void drawDecos(double x, double y, double yMax, double yMin,
			boolean tailUp, List<String> decorations,
			List<DecorationElement> extendedDecorations,
			List<GuitarChordElement> guitarChordsUp, 
			List<GuitarChordElement> guitarChordsLeft, 
			List<GuitarChordElement> guitarChordsDown, 
			List<GuitarChordElement> guitarChordsRight) {
		xCurrent = x;
		if (decorations != null) {
			for (String deco : decorations) {
				Debug.output(1, "decoration = " + deco);
				String drawnDeco = DecorationElement.getDecoFromGracing(deco);
				if (drawnDeco != null) {
					paintDecoration(drawnDeco, x, y, yMax, yMin, tailUp);
				}
			}
			double gcHeight = 2*StaffPanel.spaceHeight;
			
			drawGuitarChords(guitarChordsUp, yCurrent - gcHeight ,1.0);
			drawGuitarChords(guitarChordsRight,y + gcHeight/2,0.5);
			drawGuitarChords(guitarChordsLeft,y+gcHeight/2,0.5);
			drawGuitarChords(guitarChordsDown,yCurrent + 4*gcHeight,0.0);

		}
		if (extendedDecorations != null) {
			for (DecorationElement decElem: extendedDecorations) {
				String deco = decElem.getContents();
				Debug.output(1, "decoration = " + deco);
				if (deco != null) {
					paintDecoration(deco, x, y, yMax, yMin, tailUp);
				}
			}
		}
	}
/**
 * @param guitarChords
 * @param yPos TODO
 */
	
private void drawGuitarChords(List<GuitarChordElement> guitarChords, double yPos,double offset) {
	yPos -= offset*(2 * StaffPanel.spaceHeight)*(guitarChords.size() -1);
	for (GuitarChordElement gc: guitarChords){
		thisRenderer.paintGuitarChord(xCurrent,yPos,gc.getContents());
		yPos +=  2 * StaffPanel.spaceHeight;
		
	}
}

	private void drawGraceNotes(Grace grace) {
		theGraceBeamer.init(yMidLine); // tails always up
		if (!theLine.autoTail()) {
			theGraceBeamer.setMaxSlope(0.0); // FIXME assume pipe music for now
			theGraceBeamer.setGraceStyle(true, false, false, 3);
		}
		int midLine = theLine.getMidLine();
		for (ListIterator i = grace.getElements().listIterator(); i.hasNext();) {
			MusicElement m = (MusicElement) i.next();
			double y = findOffset(m, midLine);
			drawAccidental(m.getAccidental(), y, false);
			thisRenderer.paintGraceNote(xCurrent, y);
			drawLeger(y);
			theGraceBeamer.addNote(m, xCurrent, y);
			advanceSmallFixed();
		}
		theGraceBeamer.endBeam(thisRenderer);
		advanceSmallFixed();
	}

	private void drawLeger(double y) { // works for any clef
		//  leger lines above staff?
		double legerY = yCurrent - StaffPanel.spaceHeight;
		if (y < legerY) {
			while (legerY > y) {
				thisRenderer.paintLeger(xCurrent, legerY);
				legerY -= StaffPanel.spaceHeight;
			}
		} else { // below staff? 
			legerY += 5 * StaffPanel.spaceHeight;
			if (y >= legerY) {
				while (legerY <= y + StaffPanel.spaceHeight / 2) { // y is the top of the note
					thisRenderer.paintLeger(xCurrent, legerY);
					legerY += StaffPanel.spaceHeight;
				}
			}
		}

	}

	private void drawDot(double y) {
		int xDot = (int) xCurrent + (xFixed() / 3);
		int yDot = (int) (y + (StaffPanel.spaceHeight / 3));
		thisRenderer.paintDot(xDot, yDot);
	}



	public void paintClef() {
		if (theLine.getClef().equals("bass")) {
			drawBassClef();
		} else if (theLine.getClef().equals("alto")){
			drawAltoClef();
		}
		else if (theLine.getClef().equals("tenor")){
			drawTenorClef();
		}
		else{
			
			drawTrebleClef();
		}
	}

	private void paintLines(double xEnd) {
		Debug.output(4, "paint lines: currentX =" + xCurrent + " lastX = "
				+ xLast);
		if (xStart != xEnd) {
			thisRenderer.paintLines(xStart, xEnd, yCurrent);
		}
		
		if (xEnd > xMaxStaffWidth) {
			xMaxStaffWidth = (xEnd + 10);
			Debug.output(4, "resetting xMaxStaffWidth to " + xMaxStaffWidth);
		}

	}

	private void drawWhole(MusicElement m, double y) {
		thisRenderer.paintWhole(xCurrent, y);
		m.setReferencePosition(xCurrent, y, tailUp(y), StaffPanel.spaceHeight);
	}

	private void drawBreve(MusicElement m, double y) {
		thisRenderer.paintBreve(xCurrent, y);
		m.setReferencePosition(xCurrent, y, tailUp(y), StaffPanel.spaceHeight);
	}

	private void drawLonga(MusicElement m, double y) {
		thisRenderer.paintLonga(xCurrent, y);
		m.setReferencePosition(xCurrent, y, tailUp(y), StaffPanel.spaceHeight);
	}

	private void drawNoteHeadUnfilled(double y) {
		thisRenderer.paintNoteHeadUnfilled(xCurrent, y);
	}

	private void drawTrebleClef() {
		thisRenderer.paintTrebleClef(xCurrent, yCurrent);
	}
	private void drawAltoClef(){
		thisRenderer.paintAltoClef(xCurrent, yCurrent);
	}
	
	private void drawTenorClef(){
		thisRenderer.paintTenorClef(xCurrent,yCurrent);
	}
	private void drawBassClef() {
		thisRenderer.paintBassClef(xCurrent, yCurrent);
	}

	private void drawSharp(double y) {
		thisRenderer.paintSharp(xCurrent, y);
	}

	private void drawFlat(double y) {
		thisRenderer.paintFlat(xCurrent, y);
	}

	private void drawNatural(double y) {
		thisRenderer.paintNatural(xCurrent, y);
	}

	private void drawNoteHeadFilled(double y) {
		thisRenderer.paintNoteHeadFilled(xCurrent, y);
	}

	private void drawNoteTail(MusicElement m, double y, int numTails) {
		thisRenderer.paintTail(xCurrent, y, StaffPanel.tailHeight, tailUp(y), numTails, StaffPanel.quarterSize,
				false);
		m.setReferencePosition(xCurrent, y, tailUp(y), StaffPanel.tailHeight);

	}


}
