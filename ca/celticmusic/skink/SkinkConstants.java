//
//  SkinkConstants.java
//  5 Line Skink
//
//  Created by wil macaulay on Wed Jan 08 2003.
//  Copyright (c) 2003 Wil Macaulay. All rights reserved.
//
/**
 * Defines constants 
 *
 */
package ca.celticmusic.skink; 
public class SkinkConstants {
 
   static final String skinkVersion = "2.0k0 (public beta) 18 Sep 2009";
   static final String skinkCopyright = "Copyright 2000-2009 Wil Macaulay wil.macaulay@gmail.com";
   static final String skinkUrl = "http://celticmusic.ca/skink.html";
   static final String defaultTool = "abcm2ps";
   static final String defaultToolDirectory = "";
   static final String defaultInitialText = "X:1\nT:new tune\nK:C\nabc";
   
   final static int SIXTYFOURTH_LEN  = 12;    // so that ticks are divisible for grace notes and tuplets 
   final static int THIRTYSECOND_LEN = SIXTYFOURTH_LEN * 2;
   final static int SIXTEENTH_LEN    = SIXTYFOURTH_LEN * 4;
   final static int EIGHTH_LEN       = SIXTYFOURTH_LEN * 8;
   final static int QUARTER_LEN      = SIXTYFOURTH_LEN * 16;
   final static int HALF_LEN         = SIXTYFOURTH_LEN * 32;
   final static int WHOLE_LEN        = SIXTYFOURTH_LEN * 64;
   final static int BREVE_LEN        = SIXTYFOURTH_LEN * 128;
   final static int LONGA_LEN        = SIXTYFOURTH_LEN * 256;
   
   static final int SMALL_TREE = 45;
   static final int BIG_TREE = 300;

   
   static final int DEFAULT_NOTE_LENGTH = EIGHTH_LEN;   
   static final int DEFAULT_NOTE = 8;   // eigth note 
   static final int TICKS_PER_QUARTER = QUARTER_LEN;
   static final int DEFAULT_BPM = 120;
   final static int NARROW_STAFF_WIDTH = 700;
   final static int WIDE_STAFF_WIDTH = 900;
   final static int DEFAULT_STAFF_WIDTH = NARROW_STAFF_WIDTH;
   final static int DEFAULT_INITIAL_WINDOW_WIDTH = DEFAULT_STAFF_WIDTH + BIG_TREE;
    

	/* empty */
   private SkinkConstants(){} // prevents instantiation
}
