/**
 *
 * Display music on a panel
 *
 * @author Wil Macaulay
 * @version 2.0b2 3 April 2006
 *
 */

/*
 *
 *  StaffPanel.java
 *  History
 *       0.1    31 May 1999 WM
 *       0.6    26 Jun 2000 WM implement Scrollable (later removed)
 *	     0.9   9 Jun 2001 WM cleanup for multi-tune display
 *             28 Dec 2001 WM bagpipe key (tails down)
 *       1.0a1 17 feb 2002 use factory interface to get renderer (prepare for Graphics 2D support)
 *       6 Mar 2002 use G2D print interface
 *	     1.0a4 30 May 2002 scrolling improvements
 *       1.0a6 2 Jul 2002 aligned words
 *       1.0b2 19 jul 2002 fix up comment line rendering
 *	     1.0d  8 jan 2003 move constants out
 *       1.0f1 7 aug 2003 wide staff support, scale output on print
 *       1.1a2 2 sep 2003 refactor dot count into MusicElement
 *		 1.1f2 17 may 2004 improve layout for chords
 *       1.2a1 aug 2004 improved layout, zoom in and out
 *		2.0a0 jul 7 2005 recover lost HP, bass clef handling
 *		2.0b2 3 apr 2006  multi-stave
 *		2.0e0 6 dec 2007 factor out staff drawing
 */

// note that y is the top left of the note position: e.g if note is c, y is the d line 
package ca.celticmusic.skink;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

import javax.swing.*;
import java.awt.print.*;

@SuppressWarnings("serial")
public class StaffPanel extends JPanel implements Printable, Scrollable{ //, StaffPainter {
	protected static  int scaleN = 63;
	protected static  int scaleD = 100;
	protected static  int scaleH = 10;
	public static  double spaceHeight = (scaleH * scaleN) / scaleD;
	protected static  double staffWidth = SkinkConstants.DEFAULT_STAFF_WIDTH;
	protected static  double xStart = (20 * scaleN) / scaleD;
	protected static  double yStart = (8 * spaceHeight * scaleN) / scaleD;
	protected static  double wholeSize = (15 * scaleN) / scaleD;
	protected static  double halfSize = (12 * scaleN) / scaleD;
	public static  double quarterSize = (12 * scaleN) / scaleD;
	protected static  double staffHeight = (10 * spaceHeight * scaleN) / scaleD;
	protected static  double tailHeight = (3 * spaceHeight);

	static void setStaffWidth(double width) {
		staffWidth = width;
	}

	static void setScaleN(int n) {
		scaleN = n;
		setScales();
	}

	static double getStaffWidth() {
		return staffWidth;
	}

	static int getScaleN() {
		return scaleN;
	}

	static void setScaleD(int d) {
		scaleD = d;
		setScales();
	}

	static int getScaleD() {
		return scaleD;
	}

	static void setScaleH(int h) {
		scaleH = h;
		setScales();
	}

	static int getScaleH() {
		return scaleH;
	}

	static void setScales() {
		spaceHeight = (scaleH * scaleN) / scaleD;
		xStart = (20 * scaleN) / scaleD;
		yStart = (4 * spaceHeight * scaleN) / scaleD;

		wholeSize = (15 * scaleN) / scaleD;
		halfSize = (12 * scaleN) / scaleD;
		quarterSize = (12 * scaleN) / scaleD;
		staffHeight = (10 * spaceHeight * scaleN) / scaleD;
		tailHeight = ((11 * spaceHeight) / 4);
	}

	//instance variables					 
	private int debugLevel = 1;
	boolean firstLine = true;

	AbcLineMetrics lineMetrics = new AbcLineMetrics();

	// drawing positions
	// lastX is where the last glyph was drawn
	double xLast = 0;

	int xLastWidth = 0; // width of the last glyph

	// xCurrent is where the next glyph will be drawn if there is one

	// yCurrent is at the top of the current tune
	double xCurrent = (float) xStart;
	double yCurrent = 0;

	java.util.List<Tune> theTunes = null;
	Renderer r;

	PrintJob job = null;
	// maximum staff width
	double maxStaffWidth = staffWidth;

	int pageEnd; // finished printing when we get to this page
	int tuneStart; // position of the tune iterator
	int lineStart; // position of the line iterator
	int tuneEnd;
	int lineEnd;
	int headerWordStart;
	int headerWordEnd;
	int seenPage;

	AffineTransform baseAt = null;

	public StaffPanel() {
		setBackground(Color.white);
		setForeground(Color.black);
		setLayout(new GridLayout(0, 1));
		r = RendererFactory.getRenderer();
	}

	float zoomScale;

	public void zoom(int percentValue, Graphics2D g2) {
		zoomScale = percentValue / 100.0f;
		g2.scale(zoomScale, zoomScale);
	}

	int zoomFactor = 100;

	public void zoomIn() {
		if (zoomFactor < 400)
			zoomFactor += 25;
	}

	public void zoomOut() {
		if (zoomFactor > 25)
			zoomFactor -= 25;
	}

	public void paintComponent(Graphics g) {
		Debug.output(debugLevel, "StaffPanel.paintComponent: clipping region: "
				+ g.getClipBounds()
				+ " panel bounds: "+getBounds()
				+ " parent bounds: "+getParent().getBounds());
		//Debug.traceback();
		Graphics2D g2 = (Graphics2D) g;
		baseAt = g2.getTransform();
		super.paintComponent(g);
		r.setGraphics(g);

		//g.setClip(getParent().getBounds()); //not quite right yet...
		setPreferredSize(new Dimension(g.getClipBounds().width, g
				.getClipBounds().height));
		zoom(zoomFactor, g2);
		initStaffPanel();
		if (theTunes != null)
			paintTunes(theTunes);
		setViewSize(g);

	}
	private void initStaffPanel(){
		yCurrent = yStart;
		
	}
	public void setViewSize(Graphics g)
	// set the preferred size according to the size of the music. Assumes it is set to clipregion
	// at the start of paint
	{
		double newX = getPreferredSize().width;
		double newY = getPreferredSize().height;

		if (newX < ((maxStaffWidth + 10.0f) * zoomScale))
			newX = (maxStaffWidth + 10.0f) * zoomScale;
		if (newY < ((yCurrent + 2.0f * staffHeight) * zoomScale))
			newY = (yCurrent + 2.0f * staffHeight) * zoomScale;
		setPreferredSize(new Dimension((int) newX, (int) newY));
		//setBounds(0, 0, (int)newX, (int)newY);
		//g.setClip(0, 0, (int)newX, (int)newY);
		Debug.output(4, "setViewSize called: maxStaffWidth " + maxStaffWidth
				+ " pref " + getPreferredSize());
		revalidate();
	}

	// draw multiple tunes
	public void setTunes(java.util.List<Tune> tunes) {
		Debug.output(1, "setTunes");
		theTunes = tunes;
		maxStaffWidth = staffWidth;
	}
/** 
 * paint tunes on the panel
 * @param tunes List of tunes
 */
	public void paintTunes(java.util.List<Tune> tunes) {
		tuneEnd = 0;
		for (Tune t: tunes) {
			lineEnd = -1; // always paint entire tune, not paginated
			paintTune(t,0d);
			
		}
	}
/** 
 * initialize counters for printing
 *
 */
	public void initPrint() {
		tuneStart = 0;
		lineStart = 0;
		seenPage = -1;
		tuneEnd = 0;
		lineEnd = -1;
		pageEnd = -1;
		headerWordStart = 0;
		headerWordEnd = 0;
	}

	public int print(Graphics g, PageFormat pf, int pageNo)
			throws PrinterException {
		Debug.output(debugLevel, "StaffPanel.print: Printing page " + pageNo + " Tune " + tuneStart
				+ " Line " + lineStart);
		//Debug.traceback();
		if (pageNo == pageEnd) {
			Debug.output(debugLevel, "Printing at end: Tune " + tuneStart + " Line "
					+ lineStart);
			return Printable.NO_SUCH_PAGE;
		}
		// scale and translate to the right place for printing  

		double height = pf.getImageableHeight();
		double width = pf.getImageableWidth();
		Debug.output(debugLevel, "StaffPanel.print: Page height" + height + " Width " + width);

		((Graphics2D) g).translate(pf.getImageableX(), pf.getImageableY());
		double scale = (width - 10) / getStaffWidth();
		double scaledPageHeight =  (height / scale);
		((Graphics2D) g).scale(scale, scale);
		r.setGraphics(g);

		initStaffPanel();

		// have we seen this page yet?  pages are rendered twice.
		if (seenPage != pageNo) {
			Debug.output(1, "StaffPanel.print: haven't seen page " + pageNo + " yet");
			seenPage = pageNo;
			tuneStart = tuneEnd;
			lineStart = lineEnd;
			headerWordStart = headerWordEnd;
		}

		// keep track of the tunes we've already printed  
		tuneEnd = tuneStart;
		lineEnd = lineStart;
		headerWordEnd = headerWordStart;

		if (theTunes != null) {
			printTunes(theTunes,scaledPageHeight);
		}
		return Printable.PAGE_EXISTS;
	}
	public void printTunes(java.util.List<Tune> tunes,double pageHeight) {
		// blast through tunes we've already seen

		boolean pageAdvance = false;
		ListIterator i;
		Debug.output(debugLevel,"StaffPanel.printTunes: Starting print at tune"+tuneStart + " line "+lineEnd);
		// this  hoary pagination code is necessary because Java printing 
		// renders the same page [at least] twice
		for (i = tunes.listIterator(tuneStart); i.hasNext() && !pageAdvance;) {
			Tune t = (Tune) i.next();
			pageAdvance= paintTune(t,pageHeight);
			
		}
		// if we fell off the end, we know to stop when we reach the next page   
		if ((!i.hasNext()) && (lineEnd == -1)) {
			Debug.output(debugLevel, "StaffPanel.printTunes:Finished printing after tune " + tuneEnd);
			pageEnd = seenPage + 1;
		} else {
			Debug.output(debugLevel, "StaffPanel.printTunes: at tune "+tuneEnd+" line "+lineEnd);
			initStaffPanel();
		}
	}
	// if I'm not starting at the first line, I can't assume thePainter is the right tunePainter
	private boolean paintTune(Tune tune,double pageHeight) {
		boolean mustAdvance = false;
		if (tune != null){
			TunePainter thePainter = tune.getPainter();
			if (thePainter == null){
				thePainter = new TunePainter(r,tune);
				tune.setPainter(thePainter);
			}
			if(lineEnd == -1){				
				if (!thePainter.setTuneStart(xCurrent, yCurrent, StaffPanel.staffWidth, pageHeight)){
					Debug.output(debugLevel, "StaffPanel.paintTune: no room for new tune");
					return true;
				}
			}
			Point tuneBounds = thePainter.paintTune(lineEnd,yCurrent, headerWordEnd);
			yCurrent = tuneBounds.getY();
			maxStaffWidth = Math.max(tuneBounds.getX(),maxStaffWidth);
			if (thePainter.doneTune()) {
				Debug.output(debugLevel, "StaffPanel.paintTune: done painting tune "+ tuneEnd + " lines: "+thePainter.lineEnd());
				tuneEnd++;
				lineEnd = -1;
				headerWordEnd = 0;
				mustAdvance = false;
			}
			else{
				headerWordEnd = thePainter.headerWordEnd();
				lineEnd = thePainter.lineEnd();
				Debug.output(debugLevel, "StaffPanel.paintTune: tune lineEnd "  + lineEnd);
				mustAdvance = true;
			}
		}
		return mustAdvance;
	}


	// implement Scrollable  - copied from JTextComponent

	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}

	public int getScrollableUnitIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		switch (orientation) {
		case SwingConstants.VERTICAL:
			return visibleRect.height / 10;
		case SwingConstants.HORIZONTAL:
			return visibleRect.width / 10;
		default:
			throw new IllegalArgumentException("Invalid orientation: "
					+ orientation);
		}
	}

	public int getScrollableBlockIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		switch (orientation) {
		case SwingConstants.VERTICAL:
			return visibleRect.height;
		case SwingConstants.HORIZONTAL:
			return visibleRect.width;
		default:
			throw new IllegalArgumentException("Invalid orientation: "
					+ orientation);
		}
	}

	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

}
