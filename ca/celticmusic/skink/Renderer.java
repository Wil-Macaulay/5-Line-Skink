

//
//  Renderer.java
//  5 Line Skink
//
//  Created by wil macaulay on Sun Feb 17 2002.
//  Copyright (c) 2001 Wil Macaulay. All rights reserved.
//
/**
 * Interface supporting music drawing operations on a Graphics object
 *
 * @author Wil Macaulay
 * @version 1.0 17 Feb 2002 
 * History
 *		9 Jun 2004 WM Convert ints to doubles, remove some private methods
 *      1.2a1 5 aug 2004 finish converting to doubles to support G2D
 */
package ca.celticmusic.skink;
import java.awt.*;

public interface Renderer {
   public void setGraphics(Graphics gr);
   public Graphics getGraphics();
   
   public void paintTupletMark(double xFirst, double yFirst, 
                          double xLast,  double yLast,
                          int tupSize);
        
   public void paintTail(double x,double y,double tailLen,boolean tailsUp,int numTails,double headSize,boolean isGrace);
	   
   public void paintBeam(double xStart,double yStart,double xEnd,double yEnd, boolean isGrace);
       	   
   public void paintGuitarChord(double x, double y, String chord);
   public void paintStringDeco(double x, double y, String chord);
   public void paintVoiceName(double x, double y, String voiceName);
   public void paintInfoString(double x, double y, String s);
   public void paintBarline(double x, double y, String type);
   public void paintNthEnding(double x, double y, String repeatString);
   public void paintDot(double x, double y);
   public void paintNumericMeter(double x, double y, String num, String denom);
   public void paintSpecialMeter(double x, double y, String s);
   public void paintThirtysecondRest(double x,double y);
   public void paintSixteenthRest(double x,double y);
   public void paintEighthRest(double x,double y);
   public void paintQuarterRest(double x, double y);
   public void paintHalfRest(double x, double y);
   public void paintWholeRest(double x, double y);
   public void paintBreveRest(double x, double y);
   public void paintLongaRest(double x, double y);
   public void paintTie(double tieStart,double y, double tieEnd, boolean tailsUp);
   public void paintSlur(double x1, double y, double x2, boolean tailsUp);
   public void paintSlur(double x1, double y1, double xcp1, double ycp1, double xcp2, double ycp2, double x2, double y2);
   public void paintTrill(double x, double y);
	public void paintFermata(double x, double y);
	public void paintInvertedFermata(double x, double y);
	public void paintAccent(double x, double y);
	public void paintLowerMordent(double x, double y);
	public void paintLongPhrase(double x, double y);
	public void paintShortPhrase(double x, double y);
	public void paintMediumPhrase(double x, double y);
	
	
	public void paintSegno(double x, double y);
	public void paintCoda(double x, double y);
	public void paintSlide(double x, double y);
	public void paintUpperMordent(double x, double y);
   public void paintRoll(double x, double y);
   public void paintUpBow(double x, double y);
   public void paintDownBow(double x, double y);
   public void paintGraceNote(double x, double y);
   public void paintGraceSlash(double x, double y, boolean tailUp);
   public void paintLeger(double x, double y);
   public void paintHeader(String title,double x, double y);
   public void paintComposer(String title,double x, double y);
   public void paintLines( double xStart, double xEnd, double y);    
   public void paintNoteHeadUnfilled(double x,double y);
   public void paintWhole(double x,double y);
   public void paintBreve(double x,double y);
   public void paintLonga(double x,double y);
   public void paintVertical(double x, double y);
   public void paintTrebleClef(double currentX, double currentY); 
   public void paintBassClef(double currentX, double currentY); 
   public void paintTenorClef(double currentX, double currentY); 
   public void paintAltoClef(double currentX, double currentY); 
   public void paintSharp(double currentX, double y);
   public void paintFlat(double currentX, double y);
   public void paintNatural(double currentX, double y);
   public void paintNoteHeadFilled(double currentX, double y);
   public void paintDash(double x, double y);
   public void paintLyric(String lyric, double x, double y);
   public void paintHorizLine(double x1, double x2, double y);
public void paintSystemBracket(double current, double save, double d);
}
