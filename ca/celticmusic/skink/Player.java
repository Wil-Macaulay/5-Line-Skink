//
//  Player.java
//  5 Line Skink
//
//  Created by wil macaulay on Fri Mar 01 2002.
//  Copyright (c) 2002 Wil Macaulay. All rights reserved.
//
/**
 * abc tune player interface.
 *
 * @author Wil Macaulay
 * @version 1.0 1 Mar 2002
 * @version 1.0e1 9 Jan 2003  - add write interface 
 *
 */
package ca.celticmusic.skink;
import java.util.*;
import java.io.*;
public interface Player {

/**
 * Check if this interface flavour is supported.
 */
 
  public boolean canPlay();
  
/**
 * Play a set of tunes n times
 */
 
  public void play(List<Tune> tunes, int nTimes) throws Exception;
  
/**
 * stop playing
 */  
  public void stopPlay();
 
/**
 * play a note at a given time on the current active channel
 */
 public void makeNote(int midiNote,int atTick,int duration);     
/**
 * pause
 */
  public void pausePlay();
/**
 * starts a tied note
 */
 public void makeTiedNote( int midiNote,int atTick,int duration);   
/**
 * set the key signature context
 */
 public void setKeySig(KeySig theKey);
 
/**
 * set an accidental
 */
 public void setAccidental(int midiNoteNum,int pitchMod);
  
/**
 * accidental in force?
 */
 public int accidentalOn(int midiNoteNum);
/**
 * clear all accidentals on this channel
 */
 public void clearAccidentals();                                        
/**
 * resume
 */
  public void resumePlay();
/**
 * write the appropriate output file
 */
 public void write(File midiFile,List<Tune> tunes, int nTimes) throws Exception;
   
  

}
