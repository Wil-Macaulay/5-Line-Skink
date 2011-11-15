//
//  AbcElement.java
//  5 Line Skink
//
//  Created by wil macaulay on Sat Mar 02 2002.
//  Copyright (c) 2001 Wil Macaulay. All rights reserved.
//
/**
 * ABC element interface - used by Player
 * @author Wil Macaulay
 * @version 1.0 2 Mar 2002
 * History
 *     1.0d1  17 Sep 2002 - add isAudible()
 *     2.0e2  1 Feb 2008 - modify getMetrics to update bar position
 */
package ca.celticmusic.skink;
public interface AbcElement {
/**
 * play myself on a particular player
 *
 * @param player  Player object to sequence the note
 * @param atTick  current tick count
 * @param chan    channel
 * @param stretch stretch factor (triplets, etc)
 * @param curKey  current key signature in effect
 * @returns duration of note in ticks
 */
 
 public int play(Player player,int atTick, double stretch,KeySig curKey);
 
/**
 * update the player context with respect to keysig, accidentals, etc
 */
  
 public void updatePlayerContext(Player player);
 
 
/**
 * the element needs a staff for display
 */
 
 public boolean needsStaff();
/**
 * unroll 
 */
 public void doUnroll(Unroller u);
/**
 * set the audible length
 */
 public void setAbsLength(int defaultLength,int graceNoteLength); 
/**
 * get the audible length
 */
 public int getAbsLength();
/**
 * String representation of the element
 */
 public String asString();   
 
/**
 * Paint yourself on a component
 */
 public void paint(StaffPainter p, boolean advance); 

/**
 * Get painting metrics 
 */
 public BarPosition getMetrics(AbcLineMetrics m, BarPosition bpCurrent);
 
/**
 * Glyph width
 */
 public int glyphWidth();
   
/**
 * set ornaments
 */
 public void setOrnaments(Ornaments theOrnaments);   

/**
 * element is audible, has a duration
 */ 
 public boolean isAudible();
 
/**
 * update the line context
 */
public void updateLineContext(Line theLine); 
/**
 * begin a slur  on this element
 */
public void beginSlur(int numSlurs);
/** 
 * end a slur on this element
 */

public void endSlur(int numSlurs);

/**
 * Set the reference position of the element after the first pass
 */
public void setReferencePosition(double xRef, double yRef, boolean tailUp, double yOffset);
public double getReferenceX();
public double getReferenceY();
public boolean isTailUp();
public double getYOffset();
public double getYmax();
public double getYmin();
public void paintMarkup(StaffPainter p);

public BarPosition getBarPosition();
 
}
